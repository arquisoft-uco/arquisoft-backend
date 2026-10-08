---
name: arquisoft-estandares
description: Estándares de código de Arquisoft Backend — Notification Pattern, orden de validación, catálogo de mensajes en Redis, excepciones, Checkstyle, testing y git. Cargar junto con arquisoft-arquitectura antes de implementar, testear o validar cualquier HU/HT.
---

# Skill: arquisoft-estandares

Complementa a `arquisoft-arquitectura` (capas y paquetes); esta cubre las reglas de código
transversales. Las dos juntas son la fuente de verdad: si `CLAUDE.md` discrepa, gana la skill. Cada
regla cita un archivo real; ábrelo con `Read` si necesitas el código exacto.

## Notification Pattern y orden de validación

Los invariantes de un domain se acumulan con `ValidationResult` (`shared:validation`): `crear(...)`
instancia el result, llama a sus setters privados pasándoselo y cierra con
`result.lanzarSiTieneErrores()` → una sola `DomainValidationException` (422 + `fieldErrors[]`).
Nunca `if/throw` disperso ni una clase de excepción por invariante. Ver `FichaPerfilDomain.java`.

Cada setter privado valida con la familia `Validator*` y **corta con `return` si falla**, para no
asignar un valor inválido. Helpers por tipo: `ValidatorObjeto` (`noNulo`), `ValidatorTexto`
(`noEnBlanco`, `correoValido`), `ValidatorLongitud` (`longitudMaxima/Minima/Entre`),
`ValidatorNumero` (`valorMinimo/Maximo/Entre`), `ValidatorUUID` (`uuidValido`), `ValidatorColeccion`
(`noVacia`, `tamanioMaximo`, `sinDuplicados`). Firma uniforme
`(valor, …, campo, codigoError, ValidationResult) → boolean`. No existe `DomainValidator`.

**Recorta antes de validar, y valida el valor recortado** (`var recortado =
UtilTexto.aplicarTrim(x);`, y `recortado` va a todos los `Validator*` y al campo; referencia
`AutenticacionDomain.setCorreo`). `correoValido` no aplica trim y `longitudMaxima` mide los espacios,
así que validar el crudo rechaza `" a@b.com "` por formato o longitud. No metas el trim dentro del
predicado: es su rigidez la que obliga a normalizar donde toca.

Orden obligatorio: **1) integridad del dato** (formato/longitud/duplicados dentro del payload) →
**2) existencia/unicidad contra BD** → **3) reglas de negocio**. Nunca se consulta la BD sobre un
dato cuya integridad no se validó.

## Validator, Rule, Finder — quién hace qué

| Componente | Pureza | Puede lanzar | Ejemplo real |
|---|---|---|---|
| `Validator` | Construye sus `Rule`s con `new` en un **constructor sin argumentos**; nunca inyecta `OutputPort`/`Finder`; **cero `if`**; expone **un único `validar(...)`** con todo lo que sus `Rule`s necesitan — partirlo deja que un llamador se salte reglas sin que nada falle | No decide, orquesta en orden | `RegistrarFichaPerfilValidatorImpl.java` |
| `Rule` | Pura: sin Spring, sin Lombok, sin dependencias de constructor; no es un bean | Sí, sobre un `record` ya cargado | `FichaPerfilTituloUnicoRuleImpl.java` |
| `Finder` | Delega en un `OutputPort` | Nunca por "no encontrado" — devuelve `Boolean`/`Long`, `Domain` o `UUID` | `AsesorFichaExisteFinderImpl.java` |

El I/O de un comando vive entero en el `UseCase`: los `Finder`s consultan, el `Validator` orquesta
las `Rule`s con lo consultado, el `OutputPort` persiste. Las `Rule`s corren en secuencia y cada una
lanza en su violación, así que una regla dependiente **confía en que la anterior ya lanzó** —
guardarla con un `if` es código muerto. Si la ausencia cambia la conclusión, esa decisión va dentro
de la `Rule`.

- **Un `Finder` = una sola llamada a un `OutputPort`.** No encadena `Finder`s, no compara ni deriva
  (`a.equals(b)`, `count > 0`). Combinar fuentes es del `UseCase`; decidir es de una `Rule`.
- **El `UseCase` pasa al `Validator` el dato crudo del `Finder`** (agregado, `UUID`, conteo,
  `boolean`), nunca un veredicto ya calculado (`var esPropietario = ficha.getAsesorFicha().equals(...)`).
  Toda comparación de identidad/pertenencia vive en la `Rule`.
- **Mínimo de consultas** — ver *El `Finder` dependiente*.

`DomainRule<T>.validar(T)` (void, lanza) vive en `shared:domain`; `Finder<T, R>.obtener(T)` en
`shared:application` (ver `arquisoft-arquitectura` → *`shared:domain` vs `shared:application`*).

**Un comando sin restricciones de conjunto no lleva `Validator`**: sería una capa vacía
(`EnviarNotificacionUseCaseImpl`).

**No todo lo que consulta existencia es una `Rule`.** El criterio es si debe *lanzar*:

| Situación | Forma correcta |
|---|---|
| La existencia (o ausencia) es un error de negocio | `Finder` → `Validator` → `Rule` → `DomainException` 422 |
| Solo decide si vale la pena seguir | `Finder` consultado desde el `UseCase`, que devuelve la variante de su sellada |

El segundo caso es el corte de idempotencia de `notificaciones` (`EnviarNotificacionUseCaseImpl`):
como `Rule` lanzaría, mandaría a la DLQ y haría rollback de algo que RabbitMQ solo estaba
reentregando. La forma:

```java
var yaProcesada = notificacionProcesadaFinder.obtener(entrada);
logger.debug(NotificacionKey.LOG_VERIFICACION_PREVIA, entrada.getIdEvento(), yaProcesada);

if (yaProcesada) {
    return EnvioNotificacionResultMapper.toResultDuplicada(entrada);
}
```

El `Finder` recibe el domain, el `debug` va antes del `if` y el corte devuelve una variante de la
sellada (nunca un `return;` mudo: el consumidor hace `switch` sobre ella). **La clave de idempotencia
es `(idEvento, destinatario)`** (`uq_notificacion_event_id_destinatario`,
`existePorIdEventoYDestinatario`): un evento que abanica a varios destinatarios comparte `idEvento`, y
con la clave sobre el evento solo saldría el primer correo, en silencio.

Señal de un `Finder` mal nombrado: termina en `Validator`, inyecta un `OutputPort` y devuelve un
`boolean` que el use case consume con un `if`. Va a `command/finder/` con el nombre de lo que
responde (`NotificacionProcesadaFinder`).

**Sin `Optional` fuera del `OutputPort`.** El `Optional<Entity>` del puerto muere dentro del `Finder`:
ni `Finder<T, R>`, ni el `UseCase`, ni un record de dominio, ni un `Validator` lo ven.

| El puerto devuelve | El `Finder` devuelve | Ausente |
|---|---|---|
| `Optional<XEntity>` | `XDomain`, mapeado él mismo con `XMapper::toDomain` (nunca el `Entity`) | `XDomain.VACIO` |
| `Optional<UUID>` | `UUID` | `UtilUUID.obtenerUUIDPorDefecto()` |

```java
return estudianteOutputPort.obtenerPorId(id)
        .map(EstudianteMapper::toDomain)
        .orElse(EstudianteDomain.VACIO);
```

Quien llama pregunta con `esVacio()` (identidad) o `UtilUUID.esPorDefecto(uuid)`; nunca
`isPresent()`/`get()`/`orElse` en el `UseCase`. Si el agregado no tiene `VACIO`, se le añade con cada
campo en el valor por defecto de su `Util` (`UtilUUID.obtenerUUIDPorDefecto()`, `UtilTexto.VACIO`,
`UtilFecha.VACIO`, `UtilNumero.CERO`/`CERO_DECIMAL`, el `VACIO` del enum), nunca un literal (`""`,
`0`, `Instant.EPOCH`). Para no consultar más en la rama "no existe", corta con el booleano
(`var itemExiste = !UtilUUID.esPorDefecto(ficha); var esPropietario = itemExiste &&
vinculoFinder.obtener(...)`). Un valor suelto ausente viaja con un `boolean` explícito dentro de su
record `Existencia{Concepto}`.

El resultado de un `{X}ExisteFinder` también va con `var`. El contrato es `Finder<T, Boolean>` (un
genérico no admite primitivos) y el unboxing es seguro porque el `Finder` nunca devuelve `null`: el
`existePor...` del puerto es `boolean` primitivo.

### Validación en una consulta: política de acceso por instancia

`@PreAuthorize` autoriza el endpoint, no la instancia pedida. Cuando la HU pone una condición sobre
la instancia —que exista, que el solicitante pertenezca, que su estado permita verla—, la consulta
lleva su propio `Finder` → `Validator` → `Rule` y valida **antes** de leer; sin eso responde 200 vacío
para un id inexistente o entrega datos ajenos. Referencia:
`ConsultarEvaluacionesCualitativasJuradoUseCaseImpl` (`evaluaciones`).

| Pieza | Dónde | Por qué ahí |
|---|---|---|
| `Rule` + record + `DomainException` | `domain/{feature}/rules/`, `model/`, `exception/` | Es del dominio: la misma `Rule` sirve a comando y consulta; se reutiliza si existe |
| `Validator` | `application/{feature}/query/validator/` (+`impl/`), `Consultar{…}Validator` | Uno por lado, nunca el del comando: cada uno cambia con su caso de uso. Mismas reglas de pureza |
| `Finder` | `application/{featureConsultada}/query/finder/` (+`impl/`), sufijo `QueryFinder` | La consulta no toca `command/` |
| Puerto | `application/{featureConsultada}/query/secondaryport/{X}AccesoQueryOutputPort` | Aparte del `{X}QueryOutputPort`: aquel devuelve `ReadModel`s, este el dato crudo |
| Adaptador | `infrastructure/{featureConsultada}/query/secondaryadapter/repository/`, con su `JpaQueryEntity` + `QueryRepository` | Aislamiento CQRS |

Orden del `UseCase`: `debug` de entrada → `Finder`(s) → `validator.validar(...)` → `QueryOutputPort`
→ `debug` de cierre. La violación es `DomainException` → 422 (no hay 403 por instancia). Para la
pertenencia, el sujeto del JWT entra por un `{Consulta}{Entidad}Query` propio y el `Criteria` lo lleva
al `UseCase`; la `Rule` compara, nunca el `UseCase`.

- **Sin condición sobre la instancia en la HU, no hay `Validator`.** La política sale de la HU, no se
  añade "por seguridad" (HU-016 retiró un chequeo que no pedía, commit `2b0faeca`).
- **"Ver solo lo mío" en un listado es un filtro forzado en el `Criteria`, no una `Rule`**
  (`ConsultarFichasPerfilAsesoradasQuery`). La `Rule` es para una instancia concreta (path variable).

De la referencia **no** se copia: `boolean existe` (va `var`), `evaluacionJuradoId()` (nombre
objetual) ni el nombre `…EstudianteQuery`, residuo de la pertenencia retirada.

### El `Finder` dependiente

Cada `Finder` es un viaje a la BD. La señal a buscar es el **`Finder` dependiente**: uno cuya entrada
es la salida de otro. Casi siempre falta un método en el `OutputPort` que navegue la relación de una
vez (nombres ilustrativos):

```java
// ❌ dos viajes
var idFichaPerfil = idFichaPerfilPorItemFinder.obtener(entrada.getItem());
var fichaPerfil = fichaPerfilPorIdFinder.obtener(idFichaPerfil);

// ✅ un viaje: obtenerPorItem(UUID item) en el OutputPort, con JOIN en el adaptador
var fichaPerfil = fichaPerfilPorItemFinder.obtener(entrada.getItem());
```

La variante N+1 (una lista de `UUID` y un `Finder` por elemento) se colapsa igual, en una proyección
con `JOIN` dentro del adaptador. **El límite es a las cascadas, no a la cantidad**: varios `Finder`s
independientes están bien. Cascadas legítimas:

- El segundo lookup es **condicional** y ahorra viajes en el camino corto.
- Los dos `OutputPort` son de **features o contextos distintos** (entre contextos no hay `JOIN`).
- El identificador intermedio **es un dato que la `Rule` necesita**, no un peldaño.

## Identificadores y DTOs

Los IDs del body HTTP llegan como `String` y se validan en `Command.crear(...)` con
`ValidatorUUID.uuidValido(...)`, convirtiendo con `UtilUUID.generarUUIDDesdeTexto` — **nunca con una
anotación Jakarta**. El `Command` y los `@PathVariable` sí van tipados `UUID`. Ver
`RegistrarFichaPerfilCommand.java`.

El `RequestDTO` es un `record` **sin ninguna anotación**, y un `{Accion}{Entidad}RequestMapper`
(`final`, constructor privado, `static toCommand`) llama a `Command.crear(...)`
(`RegistrarFichaPerfilRequestMapper`, `IniciarSesionRequestMapper`). Única lógica admitida en un DTO:
sobrescribir `toString()` para enmascarar un secreto (`IniciarSesionRequestDTO`).

**Todo `Command` tiene su fábrica `crear(...)`**, también cuando la entrada llega por AMQP: el
payload lo arma Jackson sin comprobar nada y pudo reencolarse o inyectarse a mano. Ahí el fallo sube
al `AbstractEventConsumer`, que hace `basicNack(requeue=false)` y lo aparta en la DLQ. Ver
`EnviarNotificacionCommand.crear(...)`.

Nombres objetuales en contratos: `asesorFicha`, no `asesorFichaId`; `estudiantes`, no
`estudiantesIds`. Los nombres de campo son constantes en `{Contexto}Fields.{Entidad}.*`.

## Catálogo de mensajes — dos mundos, no los confundas

**1. Constantes Java** (lo que el compilador exige constante o una herramienta matchea exacto):

| Qué | Dónde |
|---|---|
| Códigos de error | `shared:message/constant/{Contexto}Codes.java` |
| Nombres de campo (`fieldErrors[]`) | `constant/{Contexto}Fields.java` |
| Límites de negocio | `constant/{Contexto}Limits.java` |
| Textos de Swagger | `annotation/{Contexto}ApiMessages.java` |
| Códigos HTTP de `@ApiResponse`, esquema de seguridad | `annotation/ApiCodes.java`, `ApiSecurity.BEARER_AUTH` |
| Client roles y su SpEL | `{contexto}/infrastructure/security/{Contexto}Authorities.java` |

Swagger se queda embebido porque un valor de anotación debe ser constante. Un identificador usado
solo dentro de una clase se queda `private static final` ahí; se promueve con un segundo lector.

**No son catálogo:** códigos, nombres de cola/exchange/bean/header, marcadores de log greppables,
etiquetas de display de un enum (`EstadoFicha.getNombre()`, fuente MER), literales de test y Swagger.

**2. Catálogo en Redis** (la prosa que lee un humano — errores y logs): texto en
`catalogo/{contexto}.properties`, cargado por `catalogo/cargar.sh`, referenciado por un enum
`{Feature}Key` en `shared:message/key/{contexto}/` que implementa `ClaveMensaje` con **clave +
aridad** (`FichaPerfilKey.java`). Clave `contexto.capa.objeto.tipo.descripcion`
(`fichas.dominio.fichaperfil.error.titulo-duplicado`). Todo enum nuevo se registra en `ClavesCatalogo`.

| Familia | Marcador | Cómo se resuelve |
|---|---|---|
| Mensaje al cliente | `%s` | `Mensajes.formatear(FichaPerfilKey.ERROR_TITULO_DUPLICADO, titulo)` |
| Log | `{}` | `logger.info(FichaPerfilKey.LOG_REGISTRADA, ficha.getId())` — **la clave**, no el texto |

Siempre por la fachada estática `Mensajes` (no hay bean). **Un log nunca resuelve su propio texto**:
`Mensajes.obtener` es un `GET` a Redis y Java evalúa el argumento aunque el nivel esté apagado; las
sobrecargas `(ClaveMensaje, Object...)` de `AppLogger` resuelven solo si el nivel está activo.
`parametros()` declara la aridad de ambas familias (un log con `{}` no es aridad 0). **Nunca
`Mensajes.obtener(clave).formatted(args)`**: salta el formateo del catálogo. `CatalogoCargaTest`
rompe el build ante una clave sin texto, un texto sin clave, un enum fuera de `ClavesCatalogo` o una
aridad que contradice los marcadores.

**Una clave que no es un log no lleva `LOG_` ni `.log.`**: el texto de una respuesta usa
`MENSAJE_`/`.mensaje.` (`TokenKey.MENSAJE_VALIDO`). El cuarto segmento debe estar en
`SEGMENTOS_ACEPTADOS` de `CatalogoCargaTest`.

**Texto multilínea: el único escape admitido es `\n`.** En producción el `.properties` lo lee
`cargar.sh` (shell, con `printf '%b'`), no `Properties.load`; los dos solo coinciden en `\n`, y
`CatalogoCargaTest` rompe ante cualquier otra barra invertida. Lo demás va literal. El salto viaja en
el texto y `correo-base.html` lo pinta con `white-space: pre-line`; no lo conviertas a `<br>`: rompería
la alternativa en texto plano del correo.

**La plantilla de correo también vive en Redis**, no en el classpath: `plantillas/cargar.sh` la sube
como `plantilla.correo-base`, y `notificacion.plantilla` (`NOTIFICACION_PLANTILLA`) es esa clave, no
una ruta. El prefijo `plantilla.` es obligatorio: `catalogo/podar.sh` barre `<contexto>.*`.

| Qué | Dónde | Cuándo se relee |
|---|---|---|
| Texto del correo (asunto, cuerpo, pie) | `PlantillaKey` + `catalogo/notificaciones.properties` | en cada envío |
| Cascarón HTML | `plantilla.correo-base` | cada `notificacion.plantilla-refresco.intervalo` (`PT5M`) |

Un cascarón nuevo solo se publica si pasa `HuecosPlantillaCorreo.verificar` (`String.replace` de un
hueco ausente enviaría correos sin cuerpo en silencio); si falla sigue el anterior, y al arrancar
aborta.

## Enums de catálogo

`valueOf` nunca se llama fuera del propio enum; cada enum expone `desde(String)` y `getId()`, más
`esValido(String)` si el valor llega por el `crear(...)` de un domain. El `String` del cliente viaja
crudo hasta el setter del domain, el único que lo convierte. Las constantes se copian de
`mer/data/`, nunca se deducen.

**Si la HU crea, modifica o convierte un enum de catálogo, lee `references/enums-catalogo.md`.**

## El objeto de acción desaparece cuando sus campos pasan a ser estado

`{Accion}{Entidad}Domain` transporta lo que la acción arrastra y el domain no posee. Si uno de esos
campos hay que persistirlo, pasa a ser estado del domain y el objeto de acción sobra: al persistir
`cuerpo` para reintentar, `EnvioNotificacionDomain` se borró y `EnviarNotificacionMapper.toDomain`
devuelve `NotificacionDomain` directo. El `{Accion}{Entidad}Mapper` sigue siendo obligatorio. Al
planificar "reintentar/auditar/reconstruir X", revisa qué campos quedan persistidos antes de
justificar el objeto de acción.

## Migraciones que añaden columnas a una tabla con filas

Una columna `NOT NULL` nueva necesita `DEFAULT` o Flyway falla contra la tabla poblada; el `DEFAULT`
declara qué significa para las filas anteriores, y eso va en el comentario de la migración.
Versión timestamp en `db/migration/{contexto}/`, sin retroceder nunca un timestamp (ver
`arquisoft-arquitectura` → *Aislamiento de persistencia*).

## Excepciones (4 bases, en `com.arquisoft.shared.exception`)

| Base | HTTP | Cuándo | Dónde vive |
|---|---|---|---|
| `DomainException` | 422 | Invariante, "no encontrado", duplicado, **no-propietario** | `domain/{feature}/exception/` |
| `DomainValidationException` | 422 + `fieldErrors[]` | Notification Pattern | la lanza `ValidationResult` |
| `ApplicationException` | 400 | Orquestación; también `FiltroException`/`FiltroInvalidoException` de `shared:query` | `application/{feature}/exception/` |
| `InfrastructureException` | 503 | Fallo real de infraestructura | `infrastructure/{feature}/exception/` |

Nunca `RuntimeException` directa. Constructor `super(message, errorCode)` — ambos `String`, así que
invertirlos compila y es un bug silencioso. "No eres el dueño" es otro 422
(`FichaNoPropietarioException`), no un 403. `GlobalAppExceptionHandler` (`shared:web`) resuelve el
status por jerarquía; ningún contexto define handler propio salvo `seguridad`. Ubicación de la
jerarquía y del handler: `arquisoft-arquitectura` → *Dónde vive cada excepción* y *Convención de
sufijos*.

## Checkstyle (obligatorio en CI — `config/checkstyle/checkstyle.xml`)

Línea máx. 150 · archivo máx. 500 líneas · método máx. 60 líneas · máx. 7 parámetros · sin tabs ·
sin wildcard imports · PascalCase tipos / camelCase métodos-campos / UPPER_SNAKE constantes · `_`
solo en nombres de test.

Si `reconstruir(...)` pasa de 7 parámetros, se agrupan campos en un `record` **anidado en el propio
domain** y quedan sueltos los que distinguen una reconstrucción de otra:
`NotificacionDomain.reconstruir(DatosNotificacion datos, EstadoNotificacion estado, String detalleError)`.

## Testing

JUnit 6 + Mockito + AssertJ, AAA con marcadores `// Arrange / // Act / // Assert`,
`debeHacerAlgo_cuandoCondicion()`, sin Javadoc.

- **Unitario** (`domain`, `application`): `@ExtendWith(MockitoExtension.class)`, sin Spring. `Rule` y
  `Validator` sin Mockito.
- **Repositorio:** `@DataJpaTest` (`org.springframework.boot.data.jpa.test.autoconfigure`) + H2,
  sembrando con `TestEntityManager` (`org.springframework.boot.jpa.test.autoconfigure`).
  `@SpringBootTest` no se usa. Un test del lado query puede sembrar con `JpaEntity` de comando (el
  aislamiento CQRS rige `src/main`). Una entidad `@Subselect` se prueba **siempre** así, nunca con
  mock del `QueryRepository`: solo ejecutándola se comprueba que los alias casan con sus `@Column`.
- **Controller:** `@WebMvcTest` (`org.springframework.boot.webmvc.test.autoconfigure`) con
  `@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
  {Test}.TestSecurityConfig.class})` — sin el handler todo sale 500, sin `AppLoggerConfig` falta
  `AppLogger`. `@MockitoBean` (nunca `@MockBean`); auth con
  `jwt().authorities(new SimpleGrantedAuthority({Contexto}Authorities.X))`, nunca `@WithMockUser`
  (prefija `ROLE_`). Ver `RegistrarFichaPerfilControllerTest.java`.
- **Mensajes:** el catálogo de prueba se instala solo (`InstaladorCatalogoPrueba` vía
  `ServiceLoader`, con `testImplementation testFixtures(project(':shared:message'))`) y lanza ante
  una aridad mal declarada. Compara contra la constante de `{Contexto}Codes`/`Fields`, no el literal.
- **Cobertura mínima 75%** en `check`. Excluidos: `*DTO`, `*Command`, `*ReadModel`, `*Application`,
  `*Entity` y `config/**`. **`*Domain` sí cuenta.** `shared:*` no aplica JaCoCo.

El gate real es `check` (tests + checkstyle + cobertura), no `test`.

## Inyección y logging

`@RequiredArgsConstructor`, nunca `@Autowired`; use cases y adaptadores son `@Component`, nunca
`@Service`; se inyectan interfaces. Logging por el puerto `AppLogger` (`shared:logger`), nunca
`@Slf4j`. `warn` para 4xx, `error` para 5xx.

**Nunca loguear desde un `@Bean` ni un `@PostConstruct`:** `Mensajes.instalar(...)` ocurre dentro de
un `@Bean`, y antes de eso se resuelve la clave cruda y SLF4J descarta los argumentos. Un log de
arranque va en un `@EventListener(ApplicationReadyEvent.class)`.

### Estructura de logs de un flujo de escritura

Un `UseCaseImpl` de escritura emite **exactamente tres líneas**. Los 4xx/5xx no se loguean en el
flujo: los cubren `GlobalAppExceptionHandler` y la línea `AUDIT` de `TrazabilidadFilter`. Referencias:
`AgregarItemFichaPerfil`, `ModificarFichaPerfil`, `CambiarAsesorFicha`, `RegistrarFichaPerfil`.

| Punto | Nivel | Dónde | Clave |
|---|---|---|---|
| Entrada | `info` | primera línea de `ejecutar` | `LOG_{GERUNDIO}` (`LOG_REGISTRANDO`) |
| Resultado de los finders | `debug` | justo antes de `validator.validar(...)` | `LOG_VERIFICACION_{ACCION}` |
| Cierre | `info` | **última sentencia** de `ejecutar` | `LOG_{PARTICIPIO}` (`LOG_REGISTRADA`) |
| Cada método de escritura del adapter | `debug` | tras el `save`/`delete` | `LOG_GUARDADO`/`LOG_ELIMINADO` (namespace `infraestructura`) |

El `INFO` de entrada deja rastro de un intento rechazado por validación. El `debug` previo lleva
exactamente lo que devolvieron los finders (booleanos, conteos, `.size()`, `!x.esVacio()`), porque el
mensaje de la `Rule` dice qué falló pero no qué se consultó. No existe un `debug` de "validación
superada" ni se duplica en el use case el de persistencia.

**El `InteractorImpl` nunca loguea** ni inyecta `AppLogger`: repetiría el cierre y no probaría el
commit, que ocurre en el proxy al retornar.

**Flujo anidado** (un `UseCase` que invoca otros): el raíz mantiene sus tres líneas, con el cierre
tras las llamadas encadenadas y el `publish`. El anidado loguea según si se invoca solo:

| Use case anidado | Log |
|---|---|
| Con `Interactor` + `Controller` propios (`AsignarEstudiantesFichaPerfil`) | Sus tres líneas |
| Paso interno sin `Interactor` (`AsignarEstadoInicialFichaPerfil`) | Un único `debug` |

**Dónde no va un log:** `Validator`/`Rule` (son puros; un `AppLogger` reabre la DI);
`Command.crear`, `Validator*`/`Util*`, mappers y DTOs (el campo inválido ya viaja en `fieldErrors[]`);
métodos de lectura de un adapter; un `try/catch` puesto solo para loguear.

### Estructura de logs de un flujo de lectura

Una consulta **no emite ningún `INFO`**: la línea `AUDIT` ya registra que ocurrió, y las lecturas
son el tráfico de mayor volumen. Interactor y `QueryOutputAdapter` no loguean.

| Punto | Nivel | Dónde | Contenido |
|---|---|---|---|
| Entrada | `debug` | primera línea de `ejecutar` | `pagina`, `tamanio`, `tieneFiltros()`, `tieneOrden()` — nunca la `Criteria` completa |
| Cierre | `debug` | tras el `QueryOutputPort` | `getTotalElements()` o `.size()` |

Sin criterio (un catálogo completo, `ConsultarEstadosFicha`), se omite la entrada y queda el cierre.

### Estructura de logs de un flujo de evento

Ver `references/eventos.md`.

### Datos sensibles en logs

Ningún secreto llega a un log (contraseñas, tokens, refresh tokens, `Authorization`, claves de API);
de un token se registra el **JTI**. Los correos se enmascaran con `UtilTexto.enmascararCorreo(...)`
(`j***@uco.edu.co`), vengan de donde vengan. Tampoco: documentos, teléfonos, direcciones, la
`Criteria` completa. Identificadores opacos (`UUID`, `idEvento`, `JTI`, `deliveryTag`) sí. Ante la
duda: si identifica a una persona fuera del sistema, se enmascara o no se registra.

## Estilo Java

**`var` en toda variable local, sin excepción por tipo** — `long`, `boolean`, `Boolean`, `UUID`,
`String` o agregado por igual. Es una regla de forma, no de legibilidad, para no juzgarla línea a
línea:

```java
var cantidadRevisiones = revisionesDelItemFinder.obtener(entrada.getItem());   // ✅
long cantidadRevisiones = revisionesDelItemFinder.obtener(entrada.getItem());  // ❌
```

Solo se sale de `var` donde no compila o cambia la semántica: diamante sin tipar (o se tipa:
`var mapa = new LinkedHashMap<String, Integer>()`), array por llaves, lambda o referencia a método,
inicializador `null`. Y solo locales: campos, parámetros, retornos y componentes de `record` van
explícitos.

`record` para `Command`/`ReadModel`/`RequestDTO`/`ResponseDTO`/payloads/entradas de `Rule`; nunca
para el domain. Imports explícitos. Sin Lombok en `domain/`.

**Sin Javadoc y sin comentarios que repitan el código**, tampoco en lo que escribas tú. `domain/` y
`application/` no llevan ninguno; en infraestructura, solo lo que el código no puede mostrar (una
restricción externa, por qué se descartó la alternativa obvia), y si cabe, va al commit.

### Los `Util` de `shared:util`

`UtilTexto` (`aplicarTrim`, `esVacioONulo`, `correoValido`, `enmascararCorreo`), `UtilUUID`
(`generarUUIDDesdeTexto`, `uuidValido`, `generarNuevoUUID`, `obtenerUUIDPorDefecto`), `UtilColeccion`
(`esVaciaONula`, `aplicarPorDefecto`, `primerDuplicado`), `UtilObjeto` (`esNulo`, `noEsNulo`,
`aplicarPorDefecto`), `UtilFecha`, `UtilNumero` (`esCero`, `tieneParteDecimal`, `obtenerPorDefecto`),
`UtilEnum`.

- **`UtilUUID.generarUUIDDesdeTexto`, no `UUID.fromString`**, con texto externo (subject del JWT,
  payload AMQP): devuelve `null` en vez de lanzar un 500.
- **`UtilColeccion.aplicarPorDefecto(x)`** en el constructor compacto de un `record` con colección
  (`null → List.of()` + copia inmutable).
- **Nulidad siempre con `UtilObjeto.esNulo`/`noEsNulo`**, nunca `== null` crudo; el código nuevo usa
  `noEsNulo` en vez de `!esNulo` (los sitios viejos no se migran en masa). No declares un `tieneX()`
  en un `record` para envolverlo. Excepción: los `shared:` que no declaran `shared:util` (`jpa`,
  `redis`, `amqp`, `web`) y clases que ya lo resuelven dentro (`QueryCriteria`).
- `ValidatorObjeto.noNulo` acumula un error (el valor era obligatorio); `UtilObjeto.esNulo` es una
  guarda de flujo sobre un estado que puede legítimamente no estar.

## Git y commits

Conventional Commits en español: `feat(contexto): descripción corta`. Rama desde `develop`
(`<prefijo>/<id>-<descripcion_snake_case>`, prefijos `feature/ fix/ refactor/ hotfix/ docs/ test/
chore/ spike/`), PR hacia `develop` con `.github/PULL_REQUEST_TEMPLATE.md` y 1 aprobación. Ver
`CONTRIBUTING.md`.

## Referencias bajo demanda — `references/`

Tan vinculantes como este archivo; ábrelos con `Read` antes de tocar la parte correspondiente. Ante
la duda, ábrelo.

| Abre | Cuando la HU… | Secciones |
|---|---|---|
| `references/eventos.md` | publica o consume un evento (el plan tiene la sección 10) | *Payload de evento: campos fijos y prueba de contrato* · *Estructura de logs de un flujo de evento* |
| `references/enums-catalogo.md` | crea, modifica o convierte un enum de catálogo | *Enums de catálogo* · *Cuando infrastructure necesita nombrar un enum de dominio* |
