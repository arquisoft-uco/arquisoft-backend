# Eventos de dominio, consumidores y replicación

Referencia de `arquisoft-arquitectura`. Se carga cuando la HU publica o consume un evento, notifica
una transición de estado, o mantiene una tabla réplica de otro contexto.

## Eventos de dominio — una sola forma

El `UseCase` inyecta la **interfaz** `EventPublisher` (`com.arquisoft.shared.publisher`) y publica tras
persistir: `eventPublisher.publish(new AsesorFichaCambiadoEvent(...))` (`CambiarAsesorFichaUseCaseImpl`,
`AgregarEstudianteUseCaseImpl`). **El domain es una clase plana**: no acumula ni drena eventos, y
`{contexto}/domain` ni siquiera tiene `EventPublisher` en su classpath. Cualquier material que proponga
lo contrario es viejo.

- **Coherencia dura:** "Eventos: ninguno" en el plan → no hay clases en `event/` y el `UseCase` no
  inyecta `EventPublisher`. Cada evento extiende `DomainEvent` (aporta `idEvento`/`ocurridoEn`/
  `tipoEvento` y valida que `EVENT_TOPIC` sea `{contexto}.{entidad}.{accion}`, que es la routing key).
- **Un evento se publica solo si alguien lo consume:** su `EVENT_TOPIC` aparece en al menos un
  `ColaEvento.declarar(...)` de algún `*QueueConfig`, existente o creado en la misma HU
  (`FichasUsuariosQueueConfig`). Sin cola enlazada, RabbitMQ lo descarta y la routing key queda como
  contrato público que nadie decidió. Un evento "por si acaso" se agrega en la HU de su consumidor; si
  ya se publicó, se retira completo (evento, test, `EventTopics`, `EventPublisher` del use case —
  commit `97d73dab`, HU-086).
- `reconstruir(...)` nunca publica, y el `CommandOutputAdapter` lee siempre con `reconstruir(...)`.
- **El evento carga todo lo que su consumidor necesita** (`AsesorFichaCambiadoEvent` lleva nombre y
  email del asesor), para que el consumidor no consulte de vuelta al productor.
- **Lo que el evento carga desde un `Finder` se consulta y valida antes de escribir**, en la fase de
  existencia, con una `Rule` que rechaza su `VACIO`. Consultado después de `registrar(...)`, el `Finder`
  devuelve el centinela sin lanzar: el evento sale con nombre y correo vacíos, `notificaciones` lo
  rechaza y la escritura ya quedó confirmada — compila y pasa los tests. Referencia:
  `EnviarSolicitudUseCaseImpl`. Quedan fuera lo que genera la propia escritura (id, fecha) y una
  ausencia que la HU declare aceptable, que el plan explica.
- **Nunca un `{Entidad}EventPublisher` local.** `shared:amqp` tiene dos implementaciones:
  `SpringModulithEventPublisher` (`@Component @Primary`, pasa por el outbox `event_publication` en la
  transacción del `Interactor`) y `RabbitMQEventPublisher` (directo al broker, respaldo con
  `@ConditionalOnMissingBean`). El `@Primary` es lo que garantiza que gane el outbox; una
  implementación nueva rompe esa garantía.

### El evento se emite donde ocurre el hecho, y es uno por hecho — no por destinatario

**Uno por hecho de negocio.** Partirlo por destinatario pone el reparto de correos en el productor: el
evento lleva **todos** los destinatarios afectados y `notificaciones` abanica. Por eso su idempotencia
es por `(idEvento, destinatario)` (ver `arquisoft-estandares`).

**Se emite en el caso de uso dueño del hecho, aunque otro lo orqueste.** `FichaPerfilRegistradaEvent`
sale de `RegistrarFichaPerfil` y `EstudiantesFichaPerfilAsignadosEvent` de
`AsignarEstudiantesFichaPerfil`, aunque el segundo vaya encadenado: tiene controller propio, y emitir
desde el registro dejaría sin aviso el camino directo. **Prueba:** ¿el caso de uso se puede invocar
solo? Entonces el hecho es suyo. Antes de partir un evento en dos, comprueba que sean dos hechos y no
uno con dos audiencias.

### Transición de estado ⇒ notificación

Un caso de uso que **crea o cambia un estado** —campo de catálogo (`EstadoFicha`, `EstadoEvaluacion`),
asignación de responsable, aprobación o rechazo— emite un evento hacia `notificaciones` salvo que la HU
diga lo contrario. Camino a copiar: `AsesorFichaCambiadoEvent` → `AsesorFichaCambiadoConsumer`.

**Excepción: un estado que es paso interno no notifica.** Pregunta quién queda afectado y qué le
cuentas. `RegistrarEvaluacionFichaPerfil` crea `EN_EVALUACION` para **un** representante del comité: no
es un desenlace, se repetiría N veces sobre la misma ficha y filtraría mecánica interna del comité. Lo
que incumbe al estudiante es el cambio de `EstadoFicha`, que ocurre una vez en otro domain. **Señal:**
si el mismo hecho puede ocurrir N veces en paralelo para el mismo sujeto, es un paso hacia otro hecho.
No inventes un umbral ("cuando haya tres evaluaciones…") para convertir N pasos en uno: fosiliza una
decisión de negocio que nadie tomó.

Son **ocho** piezas en dos contextos, y faltando una el correo no sale sin que nada falle:

| # | Módulo | Archivo |
|---|---|---|
| 1 | `shared:message/constant/` | constante nueva en `EventTopics.{Contexto}` con la routing key |
| 2 | `{contexto}/domain` | `{feature}/event/{Entidad}{Accion}Event.java`, `EVENT_TOPIC = EventTopics.{Contexto}.{X}` |
| 3 | `notificaciones/infrastructure/config/` | un `@Bean Declarables` con `ColaEvento.declarar(...)` en `Notificaciones{Contexto}QueueConfig` |
| 4 | `notificaciones/.../primaryadapter/amqp/{contextoProductor}/{entidad}/` | `{Evento}Payload.java`, `record` propio del adaptador — **nunca** la clase de evento del productor |
| 5 | `notificaciones/.../primaryadapter/amqp/{contextoProductor}/{entidad}/` | `{Evento}Consumer.java` extiende `AbstractNotificacionConsumer` — aquí se elige el texto, **siempre con el helper heredado `plantilla(clave, args)`** |
| 6 | `notificaciones/domain/notificacion/model/` | constante nueva en `TipoNotificacion` (columna `VARCHAR`: **sin migración**) |
| 7 | `notificaciones/.../primaryadapter/amqp/` | la misma constante en `TipoNotificacionEvento` — `TipoNotificacionEventoTest` falla si falta |
| 8 | `shared:message` + `catalogo/notificaciones.properties` | `PlantillaKey.ASUNTO_*` / `CUERPO_*` con su aridad, más el texto |

El evento carga **nombre y correo del destinatario** y el dato legible del asunto. La dirección es
única: el productor nunca depende de `notificaciones`, y `notificaciones` solo consume.

### La routing key se declara una vez, en `EventTopics`

La leen el evento del productor y el `Binding` del consumidor, que no se ven entre sí; si cada uno la
escribe y una cambia, el binding deja de recibir sin error. Vive en `shared:message/constant/EventTopics`,
agrupada por productor. **El nombre de cola se deriva** como `{contextoConsumidor}.{routingKey}` con
constantes, porque `@RabbitListener` exige expresión constante:

```java
public static final String ASESOR_CAMBIADO_QUEUE =
        NotificacionesQueues.PREFIJO + EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;
```

Ningún `*QueueConfig` escribe literales: el prefijo vive en `{Contexto}Queues` (`infrastructure/config/`)
y los argumentos de protocolo en `RabbitMQConfig` (`ARG_DEAD_LETTER_EXCHANGE`,
`ARG_DEAD_LETTER_ROUTING_KEY`, `SUFIJO_DEAD_LETTER`, `SEPARADOR_COLA`). Nada de esto va al catálogo de
Redis: `Mensajes.obtener(...)` no compila en una anotación.

### Consumidores: primero el productor, después la entidad

`primaryadapter/amqp/{productor}/{entidad}/`, entidad en minúsculas sin separadores:

```
primaryadapter/amqp/
├── AbstractNotificacionConsumer.java     # base común
├── TipoNotificacionEvento.java           # espejo del enum de dominio
├── fichas/asesorficha/AsesorFichaCambiado{Consumer,Payload}.java
└── fichas/fichaperfil/{FichaPerfilRegistrada,EstudiantesFichaPerfilAsignados}{Consumer,Payload}.java
```

Directo en `amqp/` solo lo compartido entre productores. Los `*QueueConfig` se quedan en `config/`, uno
por contexto productor.

**Lo común sube a la base.** `AbstractNotificacionConsumer extends AbstractEventConsumer` posee el
`AppLogger` (`protected final`), `registrar(EnvioNotificacionResult)` con el `switch` exhaustivo y el
helper `plantilla(...)`; la subclase pone su `@RabbitListener`, su log de entrada y sus textos.

**El texto del correo se resuelve con `plantilla(clave, args)`, nunca con `Mensajes.formatear`
directo.** El helper lanza `PlantillaNotificacionNoDisponibleException` si la clave no está en el
catálogo; sin él, `formatear` degrada al respaldo y sale un correo con la clave cruda. Con él, el
mensaje va a la DLQ, recuperable.

**Son tres textos por correo:** `EnviarNotificacionCommand.crear(...)` recibe `asunto`, `cuerpo` y
`pie` (espejo de `Contenido`). Cada evento aporta su `ASUNTO_*` y `CUERPO_*`; el pie es
`PlantillaKey.PIE_GENERICO` (aridad 0), compartido. Ver `AsesorFichaCambiadoConsumer`.

### Una cola de evento se declara con `ColaEvento`, no bean a bean

Consumir un evento necesita cuatro declaraciones —cola, cola de descarte, `Binding` contra el DLX y
`Binding` contra el exchange de eventos—; sin la cola de descarte enlazada, un mensaje fallido se
evapora. `ColaEvento.declarar` (`shared:amqp`) las devuelve como un solo `Declarables`:

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

La constante del nombre de cola **se queda** (la lee `@RabbitListener`); una `*_ROUTING_KEY` aparte
sobra. Centralizarlo garantiza que `x-dead-letter-routing-key` y el binding contra el DLX sean la misma
cadena (`ColaEventoTest` lo fija). Las colas de descarte llevan `x-message-ttl`
(`RabbitMQConfig.TTL_COLA_DEAD_LETTER`, 14 días).

### El `nack` distingue fallo transitorio de mensaje envenenado

`AbstractEventConsumer.rechazar(...)` clasifica; el consumidor concreto no decide nada:

| Fallo | Qué hace |
|---|---|
| Transitorio, primera entrega | `basicNack(requeue=true)` — un reintento |
| Transitorio, ya reentregado | → DLQ |
| Envenenado | → DLQ, sin reintentar |

Transitorio es `InfrastructureException` o `DataAccessException` en cualquier punto de la cadena de
causas; todo lo demás (payload que no deserializa, `Command.crear` que rechaza, `DomainException`) es
envenenado. El límite sale de `isRedelivered()`. El reintento es inmediato, sin backoff; un backoff real
(cola de reintento con TTL) es topología nueva y no se añade sin evidencia.

### Un efecto externo que puede fallar se reintenta desde la base, no desde la cola

Si el caso de uso llama a un tercero que puede rechazar (SMTP), el reintento no vive en el consumidor:
con `prefetch: 1` bloquearía el listener, y reencolar no reenvía nada porque la idempotencia lo daría
por `Duplicada`. El fallo se guarda como estado y lo reintenta un `@Scheduled` que abre su
`AlcanceTraza` con `SolicitudTraza.paraProgramado()`.

**Para reintentar hay que persistir lo que se envió:** `notificacion` guarda `destinatario_nombre` y el
`cuerpo` ya renderizado, más `intentos` y `fecha_ultimo_intento`; re-renderizar no es posible porque los
parámetros no quedan guardados. Referencia: `ReintentarNotificacionesFallidasUseCaseImpl` +
`ReintentoNotificacionesConfig`.

### Replicación entre contextos: dueño único, espejo y lápida

Una entidad presente en varios contextos tiene **un dueño**; los demás guardan una tabla espejo con lo
que necesitan (`fichas/.../estudiante`, alimentada por `EstudianteAgregadoConsumer` desde
`usuarios.estudiante.agregado`; `proyectos` replica el mismo evento con clases homónimas). El test de
diseño es **¿puede el destino rechazar por regla de negocio?** No → replicación eventual (outbox +
reintento + idempotencia); sí → saga. Todos los flujos actuales son del primer tipo.

Lo que exige el espejo (decisión tomada):

0. **Nombre natural del concepto en el destino** (`estudiante`, `asesor_ficha`), sin prefijo ni sufijo
   que delate la replicación; se admite el calificador de **rol** (`asesor_ficha`). Que la tabla no se
   escribe localmente se declara en la cabecera de la migración:
   `-- Tabla réplica local de {entidad} (dueño: contexto {contexto})`.
   Clases y métodos igual: sin `Espejo`, `Replica` ni `Mirror` (`EstudianteDomain`,
   `AgregarEstudianteUseCase`, `EstudianteOutputPort`).

   **Los beans también llevan el nombre natural.** Dos homónimos en contextos distintos chocarían
   (`ConflictingBeanDefinitionException`); se resuelve en configuración: `ArquisoftApplication` declara
   `@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)` y cada
   `{Contexto}DataSourceConfig` repite ese `nameGenerator` en su `@EnableJpaRepositories` (los repos
   Spring Data no heredan el de la aplicación). Consecuencias:
   - Un bean escaneado nunca se referencia por nombre en cadena (`@Qualifier("…")`, `@DependsOn`,
     SpEL): su nombre es el FQN. Se inyecta por tipo.
   - Los métodos `@Bean` no pasan por el generador y llevan el contexto como prefijo
     (`fichasTransactionManager`). En una réplica: `@Bean Declarables` en
     `{Contexto}{Productor}QueueConfig`, llamado `{contexto}{Evento}Declarables`
     (`fichasEstudianteAgregadoDeclarables`, `proyectosEstudianteAgregadoDeclarables`).
   - Las clases de `config/` siguen prefijadas por contexto (`FichasDataSourceConfig`).
   - Ningún bean de réplica lleva calificador de contexto (`AgregarEstudianteFichasInteractor` es la
     convención retirada).

1. **`ocurridoEn` en el payload y en la tabla.** El espejo guarda el `ocurrido_en` del último evento
   aplicado y **descarta todo evento más viejo**: gana el tiempo del hecho, no el orden de llegada.
2. **Baja lógica, nunca `DELETE`.** `eliminado_en TIMESTAMPTZ NULL` con índice parcial
   `WHERE eliminado_en IS NULL` (`fichas/.../V20260916183407__agregar_eliminado_en_estudiante.sql`).
   `{Entidad}Removido` la marca (`eliminarLogica`) y un `{Entidad}Agregado` posterior la limpia
   (`reactivar`), ambos en `EstudianteCommandRepository`. Cómo se lee: `SKILL.md` → *Aislamiento de
   persistencia*.
3. **Lápida.** Un borrado para una entidad que el espejo nunca recibió **inserta el registro ya
   anulado**; si no, el alta atrasada la revive. Borrar algo ausente es un **no-op exitoso**, nunca una
   excepción (iría a la DLQ un borrado ya cumplido).
4. **Sin borrado en cascada entre contextos.** Un veto al borrar es regla del dueño, no un `DELETE`
   propagado.

**El orden no está garantizado:** `application.yml` declara `concurrency: 5` por cola, así que dos
eventos de la misma entidad pueden procesarse a la vez. Por eso se versiona por `ocurridoEn`.

### Saga: solo cuando el destino puede rechazar

Transacciones locales con **compensación** (un hecho nuevo que contrarresta, no un rebobinado). Solo si
un paso posterior puede rechazar por negocio; un fallo técnico se reintenta. No hay ninguna saga hoy:
aplica el test de arriba antes de proponer una. Nunca 2PC/`XA` entre bases.
