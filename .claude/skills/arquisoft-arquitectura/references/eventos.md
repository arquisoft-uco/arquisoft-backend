# Eventos de dominio, consumidores y replicación

Referencia de `arquisoft-arquitectura`. Se carga cuando la HU publica o consume un evento, notifica
una transición de estado, o mantiene una tabla réplica de otro contexto.

## Eventos de dominio — una sola forma

El `UseCase` inyecta el puerto `EventPublisher` (`com.arquisoft.shared.publisher`, en
`shared:application`) y publica directamente tras persistir —
`eventPublisher.publish(new AsesorFichaCambiadoEvent(...))`. Ver `CambiarAsesorFichaUseCaseImpl.java`
(fichas) y `AgregarEstudianteUseCaseImpl.java` (usuarios), que siguen la misma forma.

**El domain es una clase plana y no participa en la publicación**: no extiende ninguna clase base
para esto, no acumula eventos en memoria y no expone ningún método para emitirlos o drenarlos. Un
domain que declare algo así no compila — el tipo base que lo permitía no existe en el repo, y el
`{contexto}/domain` ni siquiera tiene `EventPublisher` en su classpath (`shared:domain` no lo trae).
Si un plan, un ejemplo o tu memoria proponen que la entidad acumule y el use case drene, es material
viejo: la forma es una sola y es la de arriba.

**Coherencia dura:** si el plan dice "Eventos: ninguno" → no hay clases en `event/` y el `UseCase`
no inyecta `EventPublisher`. `DomainEvent` sí sigue vigente: es la clase base de cada evento
(`AsesorFichaCambiadoEvent`, `UsuarioCreadoEvent`), aporta `idEvento`/`ocurridoEn`/`tipoEvento` y
valida que `EVENT_TOPIC` tenga el formato `{contexto}.{entidad}.{accion}` — que es además la
routing key de RabbitMQ.

`reconstruir(...)` nunca publica eventos y el `CommandOutputAdapter` siempre lee con
`reconstruir(...)`, nunca con `crear(...)`. Un evento carga todo lo que su consumidor necesita
(`AsesorFichaCambiadoEvent` lleva nombre y email del asesor) para que el consumidor no tenga que
volver a consultar al productor.

La publicación está centralizada en `shared:amqp` y **nunca se crea un `{Entidad}EventPublisher`
local**. Hay dos implementaciones del puerto y no son intercambiables: `SpringModulithEventPublisher`
(`@Component @Primary`) pasa por el outbox — inserta en `event_publication` dentro de la misma
transacción del `Interactor` — y `RabbitMQEventPublisher` publica directo al broker, declarado
respaldo con `@ConditionalOnMissingBean`. El `@Primary` es lo que hace determinista cuál gana: esa
condición, sobre un `@Component` escaneado, no la garantiza Spring fuera de una autoconfiguración, y
si ganara el directo los eventos irían al broker **saltándose el outbox**, sin fila en
`event_publication` y sin atomicidad. Consecuencia práctica: una implementación nueva de
`EventPublisher` no es una extensión inocente — rompe esa garantía. El use case inyecta siempre la
**interfaz**, nunca una de las dos clases.



### El evento se emite donde ocurre el hecho, y es uno por hecho — no por destinatario

Dos preguntas distintas, y confundirlas produce dos errores diferentes.

**Cuántos eventos: uno por hecho de negocio.** El evento nombra algo que ocurrió, no a quién hay que
avisar. Partirlo por destinatario —uno "para el asesor" y otro "para los estudiantes" del mismo
suceso— pone el reparto de correos en el productor, que es exactamente el acoplamiento que los
eventos quitan: el día que haya que avisar también al comité cambiaría el contexto productor. El
evento lleva **todos** los destinatarios que ese hecho afecta y `notificaciones` abanica.

**Dónde se emite: en el caso de uso dueño del hecho, aunque otro lo orqueste.**
`FichaPerfilRegistradaEvent` sale de `RegistrarFichaPerfil` y
`EstudiantesFichaPerfilAsignadosEvent` de `AsignarEstudiantesFichaPerfil`, aunque el segundo se
ejecute encadenado por el primero. Ponerlos juntos en el registro parecía más simple y dejaba un
hueco real: `AsignarEstudiantesFichaPerfil` tiene controller e interactor propios, así que añadir
estudiantes a una ficha ya existente no habría avisado a nadie — el mismo hecho notificando por un
camino y no por el otro.

**La prueba para decidir:** ¿este caso de uso se puede invocar por sí solo? Si sí, el hecho es suyo y
el evento se emite ahí. Si el hecho solo existe como parte de otro, va con el otro. Y antes de
partir un evento en dos, comprueba que sean de verdad **dos hechos** —"se registró la ficha" y "se
vincularon estudiantes" lo son— y no un hecho con dos audiencias.

Consecuencia obligatoria de que un evento abanique: la idempotencia de `notificaciones` es por
`(idEvento, destinatario)` (ver `arquisoft-estandares`).

### Transición de estado ⇒ notificación

Un caso de uso que **crea o cambia un estado** — un campo de catálogo (`EstadoFicha`,
`EstadoEvaluacion`, …), una asignación de responsable, una aprobación o un rechazo — tiene consumidor
conocido: `notificaciones`. Emite el evento salvo que la HU diga explícitamente lo contrario. Ese es
el trabajo que el contexto `notificaciones` existe para hacer, y `AsesorFichaCambiadoEvent` →
`AsesorFichaCambiadoConsumer` es el camino completo que se copia.


**La excepción: un estado que es paso interno no notifica.** La regla anterior es el caso por
defecto, no una ley. Antes de emitir, pregúntate **quién queda afectado y qué le estás contando**. Si
el estado creado es un paso interno de un proceso —nadie fuera del equipo que lo ejecuta espera
enterarse, y no hay desenlace que comunicar— el evento sobra, y omitirlo es una decisión, no un
olvido.

`RegistrarEvaluacionFichaPerfil` es el caso a reconocer. Crea el estado `EN_EVALUACION` de la
evaluación de **un** representante del comité: alguien abrió su evaluación y todavía no ha decidido
nada. Tres cosas lo descalifican como notificación:

- **No es un desenlace.** `EN_EVALUACION` es literalmente "aún no hay veredicto".
- **Se repetiría.** Varios representantes evalúan la misma ficha, cada uno con su propia
  `EvaluacionFichaPerfil`, así que serían N correos casi idénticos sobre el mismo no-suceso.
- **Filtra proceso interno.** Le contaría al estudiante cuántos representantes van y cuándo empezó
  cada uno, que es mecánica del comité y no información suya.

El hecho que sí incumbe al estudiante es el cambio de **`EstadoFicha`** —la máquina de estados de la
ficha, no la de cada evaluación—, que ocurre una sola vez y vive en otro domain. Ese es el caso de
uso que emitirá, cuando exista.

**La señal para distinguirlos:** si el mismo hecho puede ocurrir N veces en paralelo para el mismo
sujeto, no es el hecho que se notifica — es un paso hacia otro que sí lo es, y ese otro suele vivir
en un domain distinto. Y si te encuentras inventando un umbral ("cuando haya tres evaluaciones,
entonces…") para convertir N pasos en un hecho, para y comprueba que el disparo real esté modelado:
un umbral inventado desde el código fosiliza en migraciones y contratos de evento una decisión de
negocio que nadie tomó.

El evento publicado no envía ningún correo por sí solo: sin nadie enganchado a esa routing key se
queda en el exchange. Son **ocho** piezas en dos contextos, y faltando una el correo no sale sin que
nada falle:

| # | Módulo | Archivo |
|---|---|---|
| 1 | `shared:message/constant/` | constante nueva en `EventTopics.{Contexto}` con la routing key |
| 2 | `{contexto}/domain` | `{feature}/event/{Entidad}{Accion}Event.java`, `EVENT_TOPIC = EventTopics.{Contexto}.{X}` |
| 3 | `notificaciones/infrastructure/config/` | un `@Bean Declarables` con `ColaEvento.declarar(...)` en `Notificaciones{Contexto}QueueConfig` |
| 4 | `notificaciones/.../primaryadapter/amqp/{contextoProductor}/{entidad}/` | `{Evento}Payload.java`, `record` propio del adaptador — **nunca** la clase de evento del productor |
| 5 | `notificaciones/.../primaryadapter/amqp/{contextoProductor}/{entidad}/` | `{Evento}Consumer.java` extiende `AbstractNotificacionConsumer` — aquí se elige el texto, no en el use case, y **siempre con el helper heredado `plantilla(clave, args)`** (ver abajo) |
| 6 | `notificaciones/domain/notificacion/model/` | constante nueva en `TipoNotificacion` (columna `VARCHAR`: **sin migración**) |
| 7 | `notificaciones/.../primaryadapter/amqp/` | la misma constante en `TipoNotificacionEvento` — `TipoNotificacionEventoTest` falla si falta |
| 8 | `shared:message` + `catalogo/notificaciones.properties` | `PlantillaKey.ASUNTO_*` / `CUERPO_*` con su aridad, más el texto |

El evento carga **nombre y correo del destinatario** más el dato legible del asunto, aunque duplique
lo que el productor ya tiene: un evento delgado obliga a `notificaciones` a llamar de vuelta, que es
el acoplamiento que los eventos eliminan. La dirección es de un solo sentido — el contexto productor
nunca depende de `notificaciones`, y `notificaciones` solo consume, nunca emite.


### La routing key se declara una vez, en `EventTopics`

La leen dos módulos que no se ven entre sí: el evento del productor y el `Binding` del consumidor.
Si cada lado la escribe por su cuenta y una cambia, **el binding deja de recibir sin que nada falle**
— no hay excepción, simplemente no llegan mensajes. Por eso vive en `shared:message/constant/EventTopics`,
agrupada por contexto productor, que es el único módulo que ambos lados ya alcanzan:

```java
public static final String EVENT_TOPIC = EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;   // productor
public static final String ASESOR_CAMBIADO_ROUTING_KEY = EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;
```

**El nombre de la cola se deriva, no se escribe.** La convención es `{contextoConsumidor}.{routingKey}`
y se compone con constantes, así que el compilador la resuelve y sigue sirviendo en `@RabbitListener`,
que exige una expresión constante (JLS §9.7.1):

```java
public static final String ASESOR_CAMBIADO_QUEUE =
        NotificacionesQueues.PREFIJO + EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;
```

Ningún `*QueueConfig` escribe literales propios. El prefijo del contexto vive en `{Contexto}Queues`
(`infrastructure/config/`), compartido por todos los `*QueueConfig` de ese contexto; los nombres de
argumento y el sufijo de dead letter viven en `RabbitMQConfig` (`shared:amqp`), porque son del
protocolo: `ARG_DEAD_LETTER_EXCHANGE`, `ARG_DEAD_LETTER_ROUTING_KEY`, `SUFIJO_DEAD_LETTER`,
`SEPARADOR_COLA`.

**Nada de esto va al catálogo de Redis.** RabbitMQ compara la routing key carácter a carácter, y
`Mensajes.obtener(...)` es una llamada a método: en `@RabbitListener` ni siquiera compila.


### Consumidores: primero el productor, después la entidad

`primaryadapter/amqp/` se subdivide por **quién produce** y, dentro, por **qué entidad de ese
productor describe el evento**:

```
primaryadapter/amqp/
├── AbstractNotificacionConsumer.java     # base común
├── TipoNotificacionEvento.java           # espejo del enum de dominio
├── fichas/asesorficha/AsesorFichaCambiado{Consumer,Payload}.java
└── fichas/fichaperfil/{FichaPerfilRegistrada,EstudiantesFichaPerfilAsignados}{Consumer,Payload}.java
```

Solo por productor dejó de escalar en cuanto un contexto emitió eventos sobre varios de sus
domains: el paquete plano `fichas/` los mezclaba y el emparejamiento de un consumidor con su
payload solo se veía leyendo los nombres de archivo. El segmento de entidad va todo en minúsculas y
sin separadores (`asesorficha`, `fichaperfil`), como cualquier otro paquete de feature.

Lo que se queda directo en `amqp/` es lo que se comparte entre productores: la base
`AbstractNotificacionConsumer` y el espejo `TipoNotificacionEvento`.

Los `*QueueConfig` **se quedan en `config/`**: ya hay uno por contexto productor y cada uno agrupa
todas sus colas, así que el paquete no se satura.

**Lo que todos los consumidores hacen igual sube a la base.** `AbstractNotificacionConsumer extends
AbstractEventConsumer` posee tres cosas: el `AppLogger` (`protected final`), el
`registrar(EnvioNotificacionResult)` con el `switch` exhaustivo del desenlace, y el helper
`plantilla(...)`. La subclase solo pone su `@RabbitListener`, su log de entrada y sus textos. Ventaja
real: cuando aparezca un desenlace nuevo, el compilador falla en un sitio y no en seis.

**El texto del correo se resuelve con `plantilla(clave, args)`, nunca con `Mensajes.formatear`
directo.** El helper comprueba primero que la clave exista en el catálogo y, si no está, lanza
`PlantillaNotificacionNoDisponibleException`:

```java
protected String plantilla(ClaveMensaje clave, Object... args) {
    if (!Mensajes.catalogo().contiene(clave)) {
        throw new PlantillaNotificacionNoDisponibleException(clave);
    }
    return Mensajes.formatear(clave, args);
}
```

Esa comprobación es la razón de que el helper exista. `Mensajes.formatear` degrada al respaldo
cuando la clave falta, así que sin él una plantilla ausente en Redis no falla: **sale un correo con
la clave cruda de asunto**. Con el helper, el fallo sube al `AbstractEventConsumer`, que hace
`basicNack(requeue=false)` y aparta el mensaje en la DLQ — recuperable en vez de enviado mal.

**Son tres textos por correo, no dos.** `EnviarNotificacionCommand.crear(...)` recibe `asunto`,
`cuerpo` **y `pie`**, espejo del value object `Contenido(asunto, cuerpo, pie)`. Cada evento aporta
su `PlantillaKey.ASUNTO_*` y su `CUERPO_*`; el pie es compartido y sale de
`PlantillaKey.PIE_GENERICO` (aridad 0), así que un evento nuevo no declara clave de pie propia. Ver
`AsesorFichaCambiadoConsumer`.

### Una cola de evento se declara con `ColaEvento`, no bean a bean

Consumir un evento necesita siempre las mismas cuatro declaraciones: la cola, su cola de descarte,
el `Binding` de esa cola contra el DLX y el `Binding` de la cola contra el exchange de eventos. No
son opcionales — marcar `x-dead-letter-exchange` en la cola de origen **no basta**: un exchange sin
cola enlazada descarta lo que le llega, así que un mensaje que falla se evapora sin dejar rastro
recuperable.

Escritas a mano son unas cuarenta líneas por evento, idénticas salvo el topic. `ColaEvento.declarar`
(`shared:amqp`) las devuelve como un solo `Declarables` — el contenedor de Spring AMQP que
`RabbitAdmin` recorre declarando todo lo que hay dentro — así que **un evento nuevo cuesta un método,
no cuatro beans**:

```java
public static final String FICHA_REGISTRADA_QUEUE =
        NotificacionesQueues.PREFIJO + EventTopics.Fichas.FICHA_PERFIL_REGISTRADA;

@Bean
public Declarables notificacionesFichaRegistradaDeclarables(
        @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
        @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
    return ColaEvento.declarar(
            FICHA_REGISTRADA_QUEUE,
            EventTopics.Fichas.FICHA_PERFIL_REGISTRADA,
            arquisoftEventsExchange,
            arquisoftDeadLetterExchange);
}
```

Eso es todo lo que hay que añadir al `*QueueConfig` para una cola nueva. **La constante del nombre se
queda** aunque `ColaEvento` sepa componerlo: `@RabbitListener(queues = ...)` la lee como valor de
anotación y necesita una expresión constante (JLS §9.7.1), que una llamada a método no es. No
declares una constante `*_ROUTING_KEY` aparte — el topic ya vive en `EventTopics` y el bean lo pasa
como argumento.

El motivo de centralizarlo no es el número de líneas. El argumento `x-dead-letter-routing-key` de la
cola de origen y la routing key del `Binding` contra el DLX **tienen que ser la misma cadena**, y si
divergen el descarte vuelve a ser silencioso; es el mismo riesgo que `EventTopics` resuelve para la
routing key del evento. Copiar el bloque por evento era copiar también esa oportunidad de
equivocarse. `ColaEventoTest` fija que los cuatro elementos salgan enlazados entre sí. Las colas de
descarte llevan `x-message-ttl` (`RabbitMQConfig.TTL_COLA_DEAD_LETTER`, 14 días) para no crecer sin
techo.

### El `nack` distingue fallo transitorio de mensaje envenenado

`AbstractEventConsumer.rechazar(...)` clasifica la excepción antes de rechazar, y no hay decisión que
tomar en el consumidor concreto:

| Fallo | Qué hace |
|---|---|
| Transitorio, primera entrega | `basicNack(requeue=true)` — un reintento |
| Transitorio, ya reentregado | → DLQ |
| Envenenado | → DLQ, sin reintentar |

Transitorio es `InfrastructureException` o `DataAccessException` **en cualquier punto de la cadena de
causas** (llegan casi siempre envueltas). Todo lo demás —payload que no deserializa, `Command.crear`
que rechaza, `DomainException`— es envenenado: fallará igual en el siguiente intento y reencolarlo
solo bloquea la cola. El límite de un reintento sale de `isRedelivered()`, que ya trae el mensaje: no
hace falta contador propio ni republicar.

**El reintento es inmediato, sin backoff.** Sirve para un pico de contención; una base caída dos
minutos agota el intento y acaba en el DLQ —pero ahí *queda*, que es la diferencia. Un backoff real
es el patrón de cola de reintento con TTL, y eso es topología nueva: no se añade sin evidencia.

### Un efecto externo que puede fallar se reintenta desde la base, no desde la cola

Cuando el caso de uso llama a un tercero que puede rechazar (SMTP, un proveedor externo), el reintento
**no** puede vivir en el consumidor, por dos razones que se refuerzan: con `prefetch: 1` bloquearía el
listener mientras el proveedor está caído, y reencolar el evento no reenvía nada porque la
idempotencia por `idEvento` lo daría por `Duplicada`. El fallo se guarda como estado y lo reintenta un
`@Scheduled`, que abre su propio `AlcanceTraza` con `SolicitudTraza.paraProgramado()`.

**Consecuencia de diseño que se olvida siempre: para reintentar hay que persistir lo que se envió.**
Guardar solo el resultado deja la fila `FALLIDA` sin nada con qué reconstruir el mensaje. `notificacion`
guarda `destinatario_nombre` y `cuerpo` —el texto ya renderizado— además de `intentos` y
`fecha_ultimo_intento` para acotar los ciclos. Re-renderizar la plantilla no es alternativa: sus
parámetros no quedan en ninguna columna.

Referencia completa: `ReintentarNotificacionesFallidasUseCaseImpl` + `ReintentoNotificacionesConfig`.

### Replicación entre contextos: dueño único, espejo y lápida

Una entidad que "existe en varios contextos" tiene **un dueño** y los demás guardan una **tabla
espejo** con lo poco que necesitan (`fichas/.../estudiante`, alimentada por
`EstudianteAgregadoConsumer` desde `usuarios.estudiante.agregado`; `proyectos` replica el mismo
evento con sus propias clases homónimas). No es un registro replicado en N sitios que pueda fallar a medias: es un
hecho del dueño que los demás replican.

El test que decide el diseño es **¿puede el contexto destino rechazar por regla de negocio?**

| Puede rechazar | Herramienta |
|---|---|
| No — solo necesita enterarse | Replicación eventual: outbox + reintento + idempotencia |
| Sí — tiene reglas propias que vetan | Saga con compensación |

Todos los flujos entre contextos que existen hoy caen en la primera fila.

**Lo que exige el espejo cuando llegue su HU** (decisión ya tomada, no volver a discutirla):

0. **El nombre de la tabla es el natural del concepto en el contexto destino**
   (`estudiante`, `asesor_ficha`) — sin prefijo ni sufijo que delate la replicación. Dentro de
   `fichas` un estudiante es un estudiante; que su dueño viva en otra base es despliegue, no
   lenguaje de negocio, y un sufijo mentiría si mañana cambia la estrategia. El calificador que sí
   se admite es el de **rol** (`asesor_ficha` = el asesor *de una ficha*), igual que en los client
   roles. Que la tabla no se escribe localmente se declara en el **comentario de cabecera de la
   migración**, no en el nombre:
   `-- Tabla réplica local de {entidad} (dueño: contexto {contexto})`
   Lo mismo vale para **clases y métodos**: el espejo no lleva `Espejo`, `Replica` ni `Mirror` en
   ningún nombre. Se replica un estudiante, no un "estudiante espejo" — pegar el mecanismo al
   sustantivo inventa un concepto que el negocio no tiene. `EstudianteDomain`,
   `AgregarEstudianteUseCase`, `EstudiantePorIdFinder`, `EstudianteOutputPort`.

   **Los beans también llevan el nombre natural.** Spring nombraría cada bean por el nombre simple
   de su clase, y dos homónimos en contextos distintos abortarían el arranque con
   `ConflictingBeanDefinitionException`. Eso se resuelve en la configuración, no en el nombre:
   `ArquisoftApplication` declara
   `@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)` y cada
   `{Contexto}DataSourceConfig` repite el mismo `nameGenerator` en su `@EnableJpaRepositories`. Los
   repositorios Spring Data no pasan por el escaneo de componentes y **no** heredan el generador de la
   aplicación: un `@EnableJpaRepositories` sin él vuelve a hacer chocar dos `{X}CommandRepository`.
   Con el nombre de bean igual al FQN, la réplica usa el nombre natural en **todas** sus clases
   (`AgregarCoordinadorInteractor`, `CoordinadorAgregadoConsumer`, `CoordinadorCommandRepository`),
   sin `Espejo`/`Replica` y sin calificador de contexto.

   Consecuencias:
   - Un bean escaneado **nunca** se referencia por nombre en cadena (`@Qualifier("…")`,
     `@DependsOn`, SpEL `@nombre`): su nombre es el FQN. Se inyecta por tipo (interfaz).
   - Los métodos `@Bean` **no** pasan por el generador: su nombre es el del método, y dos métodos
     homónimos en contextos distintos siguen abortando el arranque. Por eso llevan el contexto que
     los aloja como prefijo (`fichasTransactionManager`). En una réplica esto afecta sobre todo a la
     cola: el `@Bean Declarables` va en `{Contexto}{Productor}QueueConfig` y se llama
     `{contexto}{Evento}Declarables` (`fichasEstudianteAgregadoDeclarables` en `FichasUsuariosQueueConfig`,
     `proyectosEstudianteAgregadoDeclarables` en `ProyectosUsuariosQueueConfig`) — nunca
     `estudianteAgregadoDeclarables`, que choca con la siguiente réplica del mismo evento.
   - Las clases de `config/` siguen prefijadas por contexto (`FichasDataSourceConfig`) para leer a
     quién pertenecen desde el import.
   - Ningún bean de réplica lleva calificador de contexto: un nombre con `Fichas`/`Proyectos` como
     calificador (`AgregarEstudianteFichasInteractor`) es la convención retirada, no un precedente.

1. **`ocurridoEn` en el payload y en la tabla espejo.** Ya viaja en el JSON (`DomainEvent` lo asigna) y
   los payloads lo declaran. El espejo guarda el `ocurrido_en` del último evento aplicado y **descarta
   todo evento más viejo**: última escritura gana por tiempo del hecho, no por orden de llegada.
2. **La baja es lógica, nunca `DELETE`.** Un estado (`ANULADO`) con su fecha. Sin fila borrada no hay
   nada que resucitar, y en un sistema académico saber que alguien fue dado de baja importa más que
   ahorrar la fila.
3. **Lápida.** Un borrado que llega para una entidad que el espejo nunca recibió **inserta el registro
   ya marcado como anulado**. Si no, el `UsuarioCreado` atrasado entra después y revive a un usuario
   eliminado — la resurrección. Corolario: borrar algo que no está es un **no-op exitoso**, nunca una
   excepción (una excepción manda al DLQ un borrado que en realidad ya se cumplió).
4. **Nada de borrado en cascada entre contextos.** Al borrar, `fichas` sí puede vetar (una ficha con
   evaluaciones deja un histórico huérfano): eso es una regla del dueño, no un `DELETE` propagado.

**El orden no está garantizado, y no por el DLQ.** `application.yml` declara `concurrency: 5` sobre
cada cola: RabbitMQ mantiene FIFO hacia *un* consumidor, pero con cinco compitiendo, dos eventos de la
misma entidad pueden procesarse a la vez. Por eso la solución es la versión por `ocurridoEn` y no
confiar en el orden de la cola.

### Saga: solo cuando el destino puede rechazar

Una saga parte la operación en transacciones locales, cada una con su **compensación** — no se rebobina,
se emite un hecho nuevo que contrarresta al anterior (no se "des-cobra": se reembolsa). Solo hace falta
cuando un paso posterior puede **rechazar por negocio**; si el fallo es técnico, se reintenta, y
compensar sería borrar al usuario del dueño porque a Postgres le dio hipo en otro contexto.

No hay ninguna saga en el repo hoy y la palabra no aparece en ningún ADR. Antes de proponer una,
aplicar el test de arriba. Lo que **nunca** es opción es 2PC/`XA` entre las nueve bases: bloquea
recursos entre contextos y reintroduce el acoplamiento que los eventos eliminan.

