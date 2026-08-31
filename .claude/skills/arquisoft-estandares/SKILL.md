---
name: arquisoft-estandares
description: Estándares de código de Arquisoft Backend — Notification Pattern, orden de validación, catálogo de mensajes en Redis, excepciones, Checkstyle, testing y git. Cargar junto con arquisoft-arquitectura antes de implementar, testear o validar cualquier HU/HT.
---

# Skill: arquisoft-estandares

Complementa a `arquisoft-arquitectura` (esa cubre capas y paquetes; esta cubre reglas de código
transversales). Detalle profundo en `CLAUDE.md` (raíz del repo). Cada regla referencia un archivo
real de `fichas` en vez de un snippet — ábrelo con `Read` si necesitas el código exacto.

## Notification Pattern y orden de validación

Los invariantes de un aggregate se acumulan con `ValidationResult` (`shared:validation`):
`crear(...)` instancia el result, llama a sus setters privados pasándoselo, y cierra con
`result.lanzarSiTieneErrores()` → una sola `DomainValidationException` (422 + `fieldErrors[]`).
Nunca `if/throw` disperso ni una clase de excepción por invariante. Ver
`fichas/domain/.../fichaperfil/FichaPerfilDomain.java`.

Cada setter privado valida con la familia `Validator*` y **corta con `return` si falla**, para no
asignar un valor inválido. Los helpers están partidos por tipo: `ValidatorObjeto` (`noNulo`),
`ValidatorTexto` (`noEnBlanco`, `correoValido`), `ValidatorLongitud` (`longitudMaxima/Minima/Entre`),
`ValidatorNumero` (`valorMinimo/Maximo/Entre`), `ValidatorUUID` (`uuidValido`), `ValidatorColeccion`
(`noVacia`, `tamanioMaximo`, `sinDuplicados`). Firma uniforme
`(valor, …, campo, codigoError, ValidationResult) → boolean`. **No existe `DomainValidator`.**

Orden obligatorio: **1) integridad del dato** (formato/longitud/duplicados dentro del payload) →
**2) existencia/unicidad contra BD** → **3) reglas de negocio**. Nunca se consulta la BD sobre un
dato cuya integridad no se validó primero.

## Validator, Rule, Finder — quién hace qué

| Componente | Pureza | Puede lanzar | Ejemplo real |
|---|---|---|---|
| `Validator` | Construye sus `Rule`s con `new` en un **constructor sin argumentos** (no `@RequiredArgsConstructor`); nunca inyecta `OutputPort`/`Finder`; **cero `if`** | No decide, solo orquesta en orden | `fichas/application/.../fichaperfil/command/validator/impl/RegistrarFichaPerfilValidatorImpl.java` |
| `Rule` | Pura: sin Spring, sin Lombok, **sin dependencias de constructor**; no es un bean | Sí, sobre un `record` ya cargado con el dato | `fichas/domain/.../fichaperfil/rules/impl/FichaPerfilTituloUnicoRuleImpl.java` |
| `Finder` | Delega en un `OutputPort` | Nunca por "no encontrado" — devuelve `Boolean`/`Long`/`Optional` | `fichas/application/.../asesorficha/command/finder/impl/AsesorFichaExisteFinderImpl.java` |

El I/O de un comando vive entero en el `UseCase`: los `Finder`s consultan todo el estado, el
`Validator` orquesta las `Rule`s con lo ya consultado, el `OutputPort` persiste. Las `Rule`s corren
en secuencia y cada una lanza en su violación, así que una regla dependiente **confía en que la
anterior ya lanzó** — guardarla con un `if` es código muerto. Si la ausencia debe cambiar la
conclusión, esa decisión va **dentro de la Rule**.

Los métodos de ambas interfaces son fijos: `DomainRule<T>.validar(T)` (void, lanza) y
`Finder<T, R>.obtener(T)` (devuelve, nunca lanza por "no encontrado"). **No viven en el mismo
módulo, y esa es justo la distinción de arriba hecha grafo:** `DomainRule` está en `shared:domain`
(`com.arquisoft.shared.rules`), porque la decide el dominio; `Finder` está en `shared:application`
(`com.arquisoft.shared.finder`), porque consulta y eso es orquestación. Compartían el paquete
`rules` y se separaron por eso mismo.

**Un comando sin restricciones de conjunto no lleva `Validator`.** No es opcional por pereza: un
`Validator` que no orquesta ninguna `Rule` es una capa vacía. `notificaciones` es el caso real —
`EnviarNotificacionUseCaseImpl` no tiene `Validator` porque no hay nada que validar.

**Y no todo lo que consulta existencia es una `Rule`.** El criterio es si debe *lanzar*:

| Situación | Forma correcta |
|---|---|
| La existencia (o su ausencia) es un error de negocio | `Finder` → `Validator` → `Rule` → `DomainException` 422 |
| La existencia solo decide si vale la pena seguir, y no es un error | `Finder` consultado directo desde el `UseCase`, que devuelve la variante correspondiente de su sellada |

El segundo caso es el corte de idempotencia de `notificaciones`: si
`notificacionProcesadaFinder.obtener(idEvento)` da `true`, el use case devuelve
`EnvioNotificacionResult.Duplicada`. Modelarlo como `Rule` sería un bug — lanzaría, el mensaje se
iría a la DLQ y se haría rollback de la fila, cuando RabbitMQ solo estaba reentregando algo ya
procesado. Un duplicado ahí no es un error, es el comportamiento normal de un broker con ACK manual.
La guarda es de flujo ("ya está hecho, no hay trabajo"), de la misma familia que un
`Optional.isPresent()`, no el `if/throw` de invariante que la convención prohíbe.

Señal de que algo mal nombrado es en realidad un `Finder`: la clase termina en `Validator`, inyecta
un `OutputPort` y devuelve un `boolean` que el use case consume con un `if`. Eso no valida nada —
consulta. Va a `command/finder/` con nombre de lo que responde (`NotificacionProcesadaFinder`).

**Sin `Optional` en records de dominio ni en firmas de validator.** `Optional` es tipo de retorno de
un `Finder` y nada más: el `UseCase` lo desenvuelve. Un agregado ausente viaja como su centinela
`VACIO` (`.orElse(FichaPerfilDomain.VACIO)`, con `esVacio()` comparando identidad); un valor suelto
viaja como el valor más un `boolean` explícito (`boolean asesorExiste`) dentro de su record
`Existencia{Concepto}`.

**El resultado de un `{X}ExisteFinder` se declara `boolean`, nunca `var`.** El contrato del finder
tiene que ser `Finder<T, Boolean>` — un genérico de Java no admite primitivos, así que el envuelto
ahí no es un error y no hay que "arreglarlo". Lo que no puede pasar es que ese `Boolean` siga vivo
dentro del use case: con `var` se propaga hasta el `validar(..., boolean existe)`, donde el unboxing
ocurre en silencio y un `null` sería un NPE sin línea propia. Declararlo `boolean` mueve ese
desempaquetado a un punto visible y único. Es la excepción explícita a la preferencia por `var` de
"Estilo Java".

## Identificadores y DTOs

Los IDs en el body HTTP llegan como `String`, nunca `UUID` tipado. Su formato **nunca** se valida
con una anotación Jakarta — ni custom ni de librería — sino en `Command.crear(...)` vía
`ValidatorUUID.uuidValido(...)`, convirtiendo con `UtilUUID.generarUUIDDesdeTexto`. El `Command`
sí está tipado `UUID`. Los `@PathVariable` sí son `UUID`. Ver
`fichas/application/.../fichaperfil/command/primaryport/model/RegistrarFichaPerfilCommand.java`.

**Hay una sola convención de DTO, no dos.** El `RequestDTO` es un `record` **sin ninguna
anotación**, y un `{Accion}{Entidad}RequestMapper` externo (`final`, constructor privado, `static
toCommand`) llama a `Command.crear(...)`. La siguen `fichas` y `seguridad`. Ver
`fichas/infrastructure/.../fichaperfil/command/primaryadapter/web/mapper/RegistrarFichaPerfilRequestMapper.java`
y `seguridad/infrastructure/.../auth/command/primaryadapter/web/mapper/IniciarSesionRequestMapper.java`.

Existió una variante "contexto pequeño" con `@NotBlank`/`@NotNull` y `toCommand()` propio en el DTO;
se retiró porque dejaba dos puertas de validación para la misma regla y dos formas de error distintas
(`MethodArgumentNotValidException` de Jakarta vs. los `fieldErrors[]` acumulados de
`DomainValidationException`). `usuarios/.../CrearUsuarioRequestDTO` es el último rezagado: es
**desviación conocida**, no una alternativa a elegir. Si trabajas en `usuarios`, escribe DTO desnudo +
`RequestMapper`; no copies el que está ahí.

**Todo `Command` tiene su fábrica `crear(...)`, sin excepción.** Un `record` que se construya con
`new` desde el adaptador no valida nada y es bloqueante en revisión.

**También cuando la entrada llega por AMQP y no por HTTP.** "El productor ya lo validó" es una
suposición sobre otro desplegable, no un hecho sobre este arreglo de bytes: el `{Evento}Payload` lo
arma Jackson sin una sola comprobación, un campo ausente es `null`, y el mensaje pudo reposar en la
cola, reencolarse desde la DLQ o inyectarse a mano por la consola. Lo que cambia respecto a HTTP no
es si se valida, sino **qué hace el fallo**: no hay cliente a quien devolverle un 422, así que la
`DomainValidationException` sube al `AbstractEventConsumer`, que hace `basicNack(requeue=false)` y
aparta el mensaje malformado en la DLQ — que es exactamente lo que se quiere. Ver
`EnviarNotificacionCommand.crear(...)`.

El DTO de request no lleva lógica, con una excepción que sí vale copiar: sobrescribir `toString()`
para enmascarar un secreto. `IniciarSesionRequestDTO` lo hace porque el `toString()` que el
compilador genera para un `record` imprime todos sus componentes y volcaría la contraseña en claro.

Nombres objetuales en contratos: `asesorFicha`, no `asesorFichaId`; `estudiantes`, no
`estudiantesIds`. Los nombres de campo son constantes en `FichasFields.{Entidad}.*`.

## Catálogo de mensajes — dos mundos, no los confundas

**1. Constantes Java** (lo que el compilador obliga a que sea constante, o lo que una herramienta
matcha exacto — códigos, no prosa):

| Qué | Dónde |
|---|---|
| Códigos de error (`TITULO_REQUERIDO`, …) | `shared:message/constant/FichasCodes.java` |
| Nombres de campo (`fieldErrors[]`) | `shared:message/constant/FichasFields.java` |
| Límites de negocio (`TITULO_MAX`) | `shared:message/constant/FichasLimits.java` |
| Textos de Swagger (`@Tag`/`@Operation`/`@ApiResponse`) | `shared:message/annotation/FichasApiMessages.java` |
| Códigos HTTP de `@ApiResponse`, nombre del esquema de seguridad | `annotation/ApiCodes.java`, `annotation/ApiSecurity.BEARER_AUTH` |
| Client roles y su expresión SpEL | `fichas/infrastructure/security/FichasAuthorities.java` |

Swagger se queda embebido a propósito: un valor de anotación debe ser expresión constante
(JLS §9.7.1) y la spec se congela al arrancar. Un identificador usado **solo dentro de una clase**
se queda como `private static final` de esa clase — solo se promueve a `shared:message` cuando
aparece un segundo lector.

**2. Catálogo en Redis** (la prosa que lee un humano — errores y logs):
El texto vive en `catalogo/{contexto}.properties` (raíz del repo, cargado por `catalogo/cargar.sh`)
y se referencia con un enum `{Feature}Key` en `shared:message/key/{contexto}/` que implementa
`ClaveMensaje` declarando **clave + aridad**. Ver `key/fichas/FichaPerfilKey.java`. La clave sigue
`contexto.capa.objeto.tipo.descripcion` (`fichas.dominio.fichaperfil.error.titulo-duplicado`).
Todo enum nuevo se registra en `ClavesCatalogo`.

Se resuelve **siempre** por la fachada estática `Mensajes` — no hay bean inyectable:

| Familia | Marcador | Cómo se resuelve |
|---|---|---|
| Mensaje al cliente (excepciones) | `%s` | `Mensajes.formatear(FichaPerfilKey.ERROR_TITULO_DUPLICADO, titulo)` |
| Patrón de log | `{}` (lo sustituye SLF4J) | `logger.info(Mensajes.obtener(FichaPerfilKey.LOG_REGISTRADA), ficha.getId())` |

`parametros()` declara el número de marcadores para **ambas** familias — un log con `{}` no es
aridad 0. **Nunca `Mensajes.obtener(clave).formatted(args)`**: parece lo mismo, pero salta el
formateo del catálogo y pierde respaldo y diagnóstico. `AridadClave` diagnostica el desajuste;
`CatalogoCargaTest` rompe el build ante una clave sin texto, un texto sin clave, un enum ausente de
`ClavesCatalogo` o una aridad que contradice los marcadores.

**No son catálogo:** códigos de error, nombres de cola/exchange/bean/header, marcadores de log
greppables, etiquetas de display de un enum de catálogo (`EstadoFicha.getNombre()`, cuya fuente es
el MER), literales de test y textos de Swagger.

## Enums de catálogo

`valueOf` **nunca** se llama fuera del propio enum. Cada enum expone `desde(String)` (devuelve la
constante o lanza su `{Enum}NoEncontradoException` → 422) y `getId()` devolviendo `name()`; si el
valor llega por el `crear(...)` de un agregado, expone además `esValido(String)` para acumular en el
`ValidationResult` en vez de abortar al primer error. Ambos delegan en `UtilEnum.desde(...)`.
Los mappers persisten `getId()`, nunca un `.name()` desnudo.

**Sus constantes no se inventan ni se deducen del Event Storming: se copian de
`mer/data/{NN}_data_{contexto}.sql` en `arquisoft-docs`** (ver la skill `gh-docs-reader`). Ese
archivo es la fuente de verdad y define fila por fila las tres cosas que necesitas: `id` es la
constante Java (`Enum.name()`, UPPER_SNAKE_CASE, ADR-012), `nombre` es la etiqueta que devuelve
`getNombre()` — por eso se queda en Java y no va al catálogo Redis, su fuente de verdad es esa
fila— y `descripcion` es solo documentación del MER. El conjunto de filas **es** el conjunto de
constantes; la única que existe sin fila es el centinela `VACIO`, artefacto del código que nunca se
persiste. Agregar un estado que el modelo enriquecido menciona pero el `data/` no tiene ya pasó una
vez y hubo que revertirlo.

**Dónde vive un enum de catálogo es una decisión abierta del proyecto** — hoy coexisten
`domain/{catalogo}/` (cuando tiene tabla propia: `EstadoFicha`, `TipoItem`, `EstadoEvaluacion`) y
`domain/{feature}/model/` (cuando no la tiene). Un enum nuevo sigue lo que ya use su contexto; no
declares "settled" una convención que no lo está.

### Cuando infrastructure necesita nombrar un enum de dominio

No puede importarlo — la barrera de capas lo prohíbe. Se espeja: un enum propio en infraestructura
que carga el código como texto, y el `Command.crear(...)` lo resuelve contra el catálogo del dominio.
Es lo que hacen `RolUsuarioDTO` (usuarios, porque además es el contrato JSON) y `TipoNotificacionEvento`
(notificaciones, `primaryadapter/amqp/`).

**Una tabla espejo, no una constante por clase.** Con un solo consumidor una `private static final
String` basta; con seis son seis literales sueltos y seis pruebas de deriva. El enum da un sitio
único donde ver qué valores existen y **una** prueba que cubre las dos direcciones: que cada código
resuelva con `desde(...)`, y que ambos enums declaren el mismo conjunto de constantes — así, si el
dominio gana un valor y nadie lo espeja, el build falla.

Hay dos espejos hoy y el nombre lleva **para qué lado** se espeja, no solo qué:

| Espejo | Dónde vive | Para qué |
|---|---|---|
| `TipoNotificacionEvento` | `primaryadapter/amqp/` | el código que el consumidor pone en el `Command` |
| `EstadoNotificacionPersistencia` | `secondaryadapter/repository/` | el valor de la columna en una consulta del adapter |

Cada uno vive **en el paquete del adaptador que lo usa**, no en un `config/` común: es un detalle de
ese adaptador, no del contexto. Y declara **todas** las constantes del enum de dominio, no solo la
que se usa hoy — con una sola, la prueba no detectaría que el dominio ganó un valor que la
infraestructura ignora, que es precisamente la deriva que se quiere cazar. Ver
`TipoNotificacionEventoTest` y `EstadoNotificacionPersistenciaTest`.


## El objeto de acción desaparece cuando sus campos pasan a ser estado

`{Accion}{Entidad}Domain` existe para el paquete de cosas que la acción arrastra y **el agregado no
posee**. En cuanto uno de esos campos hay que persistirlo, deja de ser transporte y pasa a ser estado
del agregado — y el objeto de acción se queda envolviéndolo sin aportar nada, que es la indirección
que la convención prohíbe.

Pasó en este repo: al persistir `cuerpo` y `destinatario_nombre` para poder reintentar el envío,
`EnvioNotificacionDomain` se quedó sin contenido propio y se borró. `EnviarNotificacionMapper.toDomain(command)`
devuelve ahora `NotificacionDomain` directo. **El `{Accion}{Entidad}Mapper` no es lo que se elimina —
ese es obligatorio siempre**; lo que desaparece es el objeto de acción.

Al planificar: si la HU dice "hay que poder reintentar/auditar/reconstruir X", pregúntate qué campos
deja eso del lado persistido antes de decidir si el objeto de acción se justifica.

## Migraciones que añaden columnas a una tabla con filas

Una columna `NOT NULL` nueva necesita `DEFAULT` o Flyway falla contra la tabla poblada. El `DEFAULT`
no es un descuido: es lo que declara qué significa esa columna para las filas anteriores, y eso va en
el comentario de la migración.

```sql
ALTER TABLE notificacion
    ADD COLUMN cuerpo   TEXT    NOT NULL DEFAULT '',
    ADD COLUMN intentos INTEGER NOT NULL DEFAULT 0;
```

Recordatorio de `CLAUDE.md`: versión **timestamp** (`V{yyyyMMddHHmmss}__…`), en
`db/migration/{contexto}/`, y nunca se retrocede un timestamp para colar una migración antes de otra
ya aplicada.

## Payload de evento: campos fijos y prueba de contrato

Todo `{Evento}Payload` declara al menos `idEvento` (clave de idempotencia) y **`ocurridoEn`**
(`Instant`, instante del hecho en el origen), más lo suyo. Los dos viajan siempre en el JSON porque
`DomainEvent` los asigna; omitirlos del record los tira en silencio. `ocurridoEn` es lo que permite
descartar eventos viejos cuando llegan desordenados — ver la sección de espejos en
`arquisoft-arquitectura`.

Su prueba **instancia la configuración de producción**, no un mapper armado a mano:

```java
private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();
```

El productor serializa con ese mismo bean (`RabbitTemplate` usa `JacksonJsonMessageConverter(rabbitObjectMapper)`),
así que es lo único que prueba el contrato de verdad: un doble configurado a mano puede pasar el test
y fallar en el broker. Dos casos, y el segundo importa tanto como el primero:

1. Serializar una subclase real de `DomainEvent` y deserializarla en el payload — comprueba que los
   tipos sobreviven el viaje (un `Instant` incluye la precisión de nanosegundos).
2. Un JSON **sin** el campo nuevo deserializa con `null`, no revienta. Es lo que permite desplegar
   productor y consumidor en cualquier orden, y deja fijado que `FAIL_ON_UNKNOWN_PROPERTIES` en
   `false` no es casualidad.

Ver `UsuarioCreadoPayloadTest` y `AsesorFichaCambiadoPayloadTest`.

## Excepciones (4 bases, en `com.arquisoft.shared.exception`)

| Base | HTTP | Cuándo | Dónde vive |
|---|---|---|---|
| `DomainException` | 422 | Invariante, "no encontrado", duplicado, **propiedad/no-propietario** | `domain/{feature}/exception/` |
| `DomainValidationException` | 422 + `fieldErrors[]` | Notification Pattern con varios errores | la lanza `ValidationResult` |
| `ApplicationException` | 400 | Orquestación de application; también `FiltroException`/`FiltroInvalidoException` de `shared:query` | `application/{feature}/exception/` |
| `InfrastructureException` | 503 | Fallo real de infraestructura (BD caída, timeout) | `infrastructure/{feature}/exception/`, la levantan los `OutputAdapter` |

Nunca `RuntimeException` directa. Constructor `super(message, errorCode)` — ambos `String`, así que
invertirlos compila y produce un bug silencioso. **No hay un caso 403 propio para "no eres el
dueño"**: se modela como otro 422 (`FichaNoPropietarioException` extiende `DomainException`). El
`GlobalAppExceptionHandler` de `shared:web` resuelve el status recorriendo la jerarquía; un contexto
no define handler propio (solo `seguridad`, por colisión de nombres con Spring Security).

**Las tres viven dentro del slice vertical del feature — no hay `exception/` a nivel de contexto.**
La capa que la aloja es la de su clase base, y toda la jerarquía de un concepto va junta en una sola
capa: `AutenticacionException`, `CredencialesInvalidasException` y `TokenInvalidoException` están las
tres en `seguridad/application/auth/exception/` porque las dos subclases extienden a la primera, que
es `ApplicationException`; en cambio `ProveedorIdentidadNoDisponibleException` (503, Keycloak caído,
la lanza `KeycloakAuthOutputAdapter`) está en `seguridad/infrastructure/auth/exception/`. Una
subclase que cae en otra capa que su padre parte una jerarquía en dos módulos — los imports
redundantes que reporta Checkstyle son el síntoma.

**Un `@RestControllerAdvice` va en `handler/`, nunca en `exception/`:**
`shared/web/handler/GlobalAppExceptionHandler` y `seguridad/infrastructure/handler/SeguridadGlobalExceptionHandler`.
Cada paquete del repo se llama como el sufijo de las clases que contiene (`filter/` → `*Filter`,
`mapper/` → `*Mapper`), y en los ~20 sitios donde aparece `exception/` significa "aquí viven los
tipos de excepción". Un handler no es una excepción. `advice/` también se descartó: es jerga de
Spring y la clase no se llama `*Advice`.

## Checkstyle (obligatorio en CI — `config/checkstyle/checkstyle.xml`)

Línea máx. 150 caracteres · archivo máx. 500 líneas · método máx. 60 líneas · máx. 7 parámetros ·
sin tabs · sin wildcard imports · PascalCase tipos / camelCase métodos-campos / UPPER_SNAKE
constantes · `_` permitido solo en nombres de test.

**Cuando `reconstruir(...)` pasa de 7 parámetros**, la salida no es partir el método ni silenciar la
regla: se agrupan los campos en un `record` **anidado dentro del propio agregado**, y los que el
lector necesita ver sueltos se quedan sueltos. `NotificacionDomain` (9 campos) declara
`public record DatosNotificacion(...)` con los siete de identidad y firma
`reconstruir(DatosNotificacion datos, EstadoNotificacion estado, String detalleError)` — el estado y
el motivo del fallo siguen visibles porque son lo que distingue una reconstrucción de otra. Es el
único agregado del proyecto que lo necesita hoy; los de `fichas` caben sin agrupar.

## Testing

JUnit 6 + Mockito + AssertJ, patrón AAA con marcadores `// Arrange / // Act / // Assert`, nombre
`debeHacerAlgo_cuandoCondicion()`, sin Javadoc.

- **Unitario** (`domain`, `application`): `@ExtendWith(MockitoExtension.class)`, sin contexto Spring.
  Los tests de `Rule` y `Validator` no necesitan Mockito — las reglas son puras.
- **Repositorio:** `@DataJpaTest` (`org.springframework.boot.data.jpa.test.autoconfigure`) + H2, con
  `TestEntityManager` (`org.springframework.boot.jpa.test.autoconfigure` en Boot 4) para sembrar.
  **`@SpringBootTest` no se usa en ningún test de este repo.** Un `@DataJpaTest` del lado query sí
  siembra con los `JpaEntity` de comando — el aislamiento CQRS rige `src/main`, no los tests. Si el
  adapter de lectura es delegación plana sobre un catálogo, un test con Mockito basta.
- **Controller:** `@WebMvcTest` (`org.springframework.boot.webmvc.test.autoconfigure`). En `fichas`
  el slice necesita `@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class,
  TrazabilidadConfig.class, {Test}.TestSecurityConfig.class})` — sin `GlobalAppExceptionHandler`
  toda excepción sale como 500; sin `AppLoggerConfig` no hay bean `AppLogger`. Mocks con
  `@MockitoBean` (nunca `@MockBean`), auth con
  `SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority(FichasAuthorities.X))`
  — nunca `@WithMockUser` (prefija `ROLE_`). El ancla `FichasInfrastructureTestApplication` ya
  existe en `fichas/infrastructure/src/test/`. Ver `RegistrarFichaPerfilControllerTest.java`.
- **Mensajes en tests:** el catálogo de prueba se instala solo (`InstaladorCatalogoPrueba` vía
  `ServiceLoader`, disponible por `testImplementation testFixtures(project(':shared:message'))`), y
  `CatalogoMensajesPrueba` **lanza** ante una aridad mal declarada — un `formatear` con argumentos
  de más rompe el test. Al comparar contra un código o campo, importa la constante de
  `FichasCodes`/`FichasFields`; no dupliques el literal.
- **Cobertura mínima 75%**, verificada por `check` (`jacocoTestCoverageVerification`). Excluidos:
  `*DTO`, `*Command`, `*ReadModel`, `*Application`, `*Entity` (cubre `JpaEntity`/
  `JpaQueryEntity`) y `config/**`. Ojo: **`*Domain` NO está excluido** — el agregado cuenta para el
  umbral. Los módulos `shared:*` no aplican jacoco.

El gate real es `check` (tests + `checkstyleMain`/`checkstyleTest` + cobertura), no `test`.

## Inyección y logging

Constructor injection con `@RequiredArgsConstructor` — nunca `@Autowired`, nunca `@Service` (todo
use case y adaptador es `@Component`). Se inyectan interfaces, nunca implementaciones. Logging vía
el puerto `AppLogger` (`shared:logger`) inyectado por constructor — no `@Slf4j`, del que ya no queda
ni uno en los cuatro contextos con código. `warn` para 4xx, `error` para 5xx.

**Nunca loguear desde un método `@Bean` ni desde un `@PostConstruct`:** `Mensajes.instalar(...)`
ocurre dentro de un `@Bean`, así que cualquier bean construido antes resuelve la **clave cruda** y,
como esa clave no lleva `{}`, SLF4J descarta también los argumentos. Un log de arranque que reporte
configuración efectiva va en un `@EventListener(ApplicationReadyEvent.class)`.


### Estructura de logs de un flujo de escritura

Un flujo de comando emite **exactamente dos `INFO` por petición** — entrada y cierre. Todo lo demás
es `DEBUG`. Los 4xx/5xx **no se loguean dentro del flujo**: `GlobalAppExceptionHandler` ya emite
`warn`/`error` con la URI y `TrazabilidadFilter` ya audita cada petición con usuario, duración y
status. Referencias en el repo: `AgregarItemFichaPerfil` (simple) y `RegistrarFichaPerfil` (anidado).

| Punto | Nivel | Dónde | Clave |
|---|---|---|---|
| Entrada de la operación | `info` | primera línea de `UseCaseImpl.ejecutar` | `LOG_{GERUNDIO}` — `LOG_REGISTRANDO`, `LOG_AGREGANDO`, `LOG_ASIGNANDO`, `LOG_CAMBIANDO_ASESOR` |
| Resultado de los finders | `debug` | inmediatamente **antes** de `validator.validar(...)` | `LOG_VERIFICACION_{ACCION}` |
| Cierre de la operación | `info` | después de la escritura | `LOG_{PARTICIPIO}` — `LOG_REGISTRADA`, `LOG_AGREGADO` |
| Cada método de **escritura** del adapter | `debug` | tras el `save`/`delete`/`actualizar` | `LOG_GUARDADO`/`LOG_GUARDADA`/`LOG_ELIMINADO` (namespace `infraestructura`) |

El `INFO` de entrada existe porque sin él un flujo rechazado por validación no deja rastro de que se
intentó: solo queda el `warn` del handler, que nombra la excepción y no la operación. El `DEBUG`
previo al validator lleva **exactamente lo que devolvieron los finders** — los booleanos, conteos y
tamaños sobre los que las `Rule`s van a decidir (colecciones como `.size()`, agregados como
`!x.esVacio()`) — porque cuando una `Rule` lanza, su mensaje dice qué falló pero no qué se consultó.

**Flujo anidado** (un `UseCase` que inyecta e invoca otros `UseCase`; hoy solo `RegistrarFichaPerfil`):
el `InteractorImpl` inyecta `AppLogger` y emite el `INFO` de cierre (`LOG_{ACCION}_COMPLETADO`) tras
llamar al use case; el use case raíz conserva solo su `INFO` de entrada y baja a `debug` su log de
"escrito"; los use cases anidados bajan su cierre a `debug` por ser pasos internos; y se agrega un
`debug` de validación superada tras el validator. **En un flujo simple el interactor no loguea nada**
— sería una tercera línea diciendo lo mismo que el cierre del use case. Ese `INFO` del interactor no
prueba el commit: `@Transactional` commitea al retornar, en el proxy, después de la última línea del
método; lo que prueba es que el flujo completo terminó sin excepción.

**Dónde no va un log, y por qué:**

| Sitio | Razón |
|---|---|
| `{Accion}{Entidad}ValidatorImpl` y las `Rule` | Son puros: constructor sin argumentos, cero dependencias inyectadas. Un `AppLogger` reabre la DI que la convención eliminó, y el validator no decide nada que el `debug` previo no diga ya |
| `Command.crear(...)`, helpers `Validator*`/`Util*`, mappers, DTOs | Un campo inválido ya viaja en `fieldErrors[]` del 422. Loguearlo produce una línea por campo y no añade nada |
| Métodos de **lectura** de un adapter | Solo los de escritura logean |
| `try/catch` puesto únicamente para loguear | La excepción de negocio la maneja el handler; la de infraestructura debe subir como 500 |
| Secretos, tokens, contraseñas | Y en general PII que el log de cierre no lleve ya |

**Una clave que no es un log no lleva prefijo `LOG_` ni segmento `.log.`.** El texto del cuerpo de
una respuesta HTTP usa `MENSAJE_`/`.mensaje.` (`TokenKey.MENSAJE_VALIDO`, que estuvo mal nombrado
como `LOG_VALIDO` y acopló el texto de una respuesta a lo que parecía un log). El cuarto segmento de
toda clave debe estar en `SEGMENTOS_ACEPTADOS` de `CatalogoCargaTest`.


### Estructura de logs de un flujo de lectura

Un flujo de consulta **no emite ningún `INFO`** y no toca ni el interactor ni el adapter. Esa es la
diferencia con escritura, y no es una omisión: `TrazabilidadFilter` ya emite una línea `AUDIT` a
nivel `info` por cada petición 2xx (`warn` en 4xx, `error` en 5xx) con método, URI, usuario, duración
y status, así que el registro operativo de "esta consulta ocurrió" ya existe. Un `INFO` propio lo
duplicaría, y las lecturas son el tráfico de mayor volumen del sistema.

| Punto | Nivel | Dónde | Contenido |
|---|---|---|---|
| Entrada de la consulta | `debug` | primera línea de `UseCaseImpl.ejecutar` | lo que el `AUDIT` **no** puede mostrar: `pagina`, `tamanio`, `tieneFiltros()`, `tieneOrden()` |
| Cierre de la consulta | `debug` | tras el `QueryOutputPort` | el volumen devuelto — `getTotalElements()` o `.size()` |
| `QueryOutputAdapter` | — | — | nada: es delegación pura a `PageableMapper`/`PaginationMapper` y duplicaría el cierre |
| `InteractorImpl` de query | — | — | nada: solo abre la transacción `readOnly` |

Lo valioso de una consulta no es que ocurrió, es **qué se pidió y cuánto volvió**: un resultado vacío
inesperado se explica con el árbol de filtros y el ordenamiento, que es exactamente lo que la línea
de auditoría no lleva. Por eso el log de entrada registra `tieneFiltros()`/`tieneOrden()` y no la
`Criteria` completa — el árbol serializado sería ilegible y podría arrastrar valores del cliente.

Cuando la consulta **no tiene criterio** (un catálogo completo, como `ConsultarEstadosFicha`), el log
de entrada no llevaría ningún dato: se omite y queda solo el `debug` de cierre con el total.


### Estructura de logs de un flujo de evento

Un flujo disparado por un mensaje **no pasa por `TrazabilidadFilter`**: no hay petición HTTP y por
tanto **no hay línea `AUDIT`**. El consumidor es lo único que puede dejar constancia de que el evento
llegó, y por eso aquí el `INFO` de entrada sí va en el adaptador y no en el use case.

| Punto | Nivel | Dónde |
|---|---|---|
| Encolado en el outbox | `debug` | `SpringModulithEventPublisher` (transversal, ya hecho) |
| Envelope recibido / confirmado | `debug` | `AbstractEventConsumer` (transversal, ya hecho) — cola y `deliveryTag` |
| **Evento recibido** | `info` | el `{Evento}Consumer`, tras `deserialize` — `idEvento` + identificadores de negocio |
| Cierre de la operación | `info` | el `UseCase` que el consumidor dispara |
| Nack a la DLQ | `error` | `AbstractEventConsumer` (transversal, ya hecho) |

**Dos `INFO` por mensaje**, igual que por petición. El `INFO` de entrada pertenece a quien es el punto
de entrada del flujo: en un comando HTTP es el use case, en un evento es el consumidor. Por eso **un
use case disparado por un consumidor no añade su propio `INFO` de entrada** — el del consumidor ya lo
es. Un `{Evento}Consumer` nuevo hereda los tres logs transversales de `AbstractEventConsumer` sin
escribir nada: solo aporta su `INFO` de recepción.

Todo log del consumidor va **dentro** del `withCorrelation(...)`, es decir dentro del `AlcanceTraza`.
Fuera de él el MDC ya se restauró y la línea sale sin `correlacionId` ni `transaccionId` — que es
justo lo que permite seguir el evento hasta el productor.

### Datos sensibles en logs

**Ningún secreto llega nunca a un log:** contraseñas, tokens de acceso, refresh tokens, el header
`Authorization`, claves de API. De un token se registra el **JTI** — identificador opaco — nunca el
valor. Por eso los logs de autenticación de `seguridad` son deliberadamente de aridad 0
(`LOG_AUTENTICAR_DEBUG`, `LOG_REFRESH_DEBUG`): no hay nada que puedan decir sin exponer algo.

**Los correos se enmascaran con `UtilTexto.enmascararCorreo(...)`** (`shared:util`), que deja
`j***@uco.edu.co`: conserva el dominio y la inicial para correlacionar, sin exponer la dirección. Un
correo es dato personal y los logs se envían a Loki. Aplica a todo argumento de log que sea un
correo, venga de un agregado, de un `Command`, de un payload de evento o de una `Entity`.

También quedan fuera de un log: documentos de identidad, teléfonos, direcciones, y la `Criteria` o el
árbol de filtros completos de una consulta (arrastran valores enviados por el cliente). Identificador
opaco sí — `UUID`, `idEvento`, `JTI`, `deliveryTag` — porque no dice nada por sí mismo. Ante la duda:
si el valor identifica a una persona fuera del sistema, se enmascara o no se registra.

## Estilo Java

`var` cuando el lado derecho ya nombra el tipo o evita repetir un genérico largo; **no** con
diamante (`new LinkedHashMap<>()`), ni con clases anónimas, ni cuando el tipo declarado es
deliberadamente una interfaz. Solo para locales — campos, parámetros y retornos van explícitos.
`record` para `Command`/`ReadModel`/`RequestDTO`/`ResponseDTO`/payloads de evento/entradas de `Rule`;
nunca para el agregado. Imports explícitos, nunca wildcard. **Sin Lombok en `domain/`.**

**Sin Javadoc y sin comentarios que repitan el código.** `domain/` y `application/` no llevan
ninguno — el nombre del agregado, la regla y el caso de uso son la documentación. En
infraestructura, un comentario se justifica solo si registra algo que el código no puede mostrar
(una restricción externa, por qué se descartó la alternativa obvia); si cabe, va al mensaje de
commit o a `CLAUDE.md`. **Esto incluye el código que escribes tú**: no adornes con Javadoc una clase
nueva "para que se entienda" — si hace falta explicarla, el razonamiento va a esta skill o al commit.

### Los `Util` de `shared:util`

`UtilTexto` (`aplicarTrim`, `esVacioONulo`, `correoValido`, `enmascararCorreo`), `UtilUUID`
(`generarUUIDDesdeTexto`, `uuidValido`, `generarNuevoUUID`, `obtenerUUIDPorDefecto`), `UtilColeccion`
(`esVaciaONula`, `aplicarPorDefecto`, `primerDuplicado`), `UtilObjeto` (`esNulo`, `aplicarPorDefecto`),
`UtilFecha`, `UtilNumero`, `UtilEnum`.

Dos que se olvidan y sí importan:

- **`UtilUUID.generarUUIDDesdeTexto` en vez de `UUID.fromString`**, siempre que el texto venga de
  fuera (subject de un JWT, campo de un payload AMQP): valida el patrón y devuelve `null`, mientras
  que `UUID.fromString` lanza `IllegalArgumentException` y sale como 500.
- **`UtilColeccion.aplicarPorDefecto(x)`** en el constructor compacto de un `record` que recibe una
  colección; hace el `null → List.of()` más la copia inmutable.
- **Recorta ANTES de validar, y valida el valor recortado.** El setter/factoría normaliza primero
  (`var recortado = UtilTexto.aplicarTrim(x);`) y pasa `recortado` a todos los `Validator*` y al
  campo. `AutenticacionDomain.setCorreo` y `UsuarioDomain.setEmail` son la referencia. Validar el
  texto crudo y recortar solo al asignar rompe dos cosas: `ValidatorTexto.correoValido` **no aplica
  trim** (delega en `coincidePatron`, que compara literal), así que `" a@b.com "` se rechaza por
  formato; y `ValidatorLongitud.longitudMaxima` mide los espacios, así que un valor que cabe una vez
  recortado se rechaza por longitud. Este fallo estuvo vivo en `Destinatario`/`Contenido` de
  `notificaciones`.

  Y no "arregles" esto metiendo el trim dentro de `UtilTexto.correoValido`: si el agregado olvida
  recortar, la validación pasaría y persistiría el valor con espacios. Que el predicado sea estricto
  es lo que obliga a normalizar donde toca.

Distingue `ValidatorObjeto.noNulo` de `UtilObjeto.esNulo`: el primero **acumula un error** en el
`ValidationResult` porque el valor era obligatorio; el segundo es una guarda de flujo sobre un estado
interno que legítimamente puede no estar asignado.

**No fuerces un `Util` donde no ahorra nada.** `x != null ? x.metodo() : otro` no mejora con
`UtilObjeto.esNulo`, y varios módulos `shared:` (`jpa`, `redis`, `amqp`, `web`) **no declaran
`shared:util`**: meterlo ahí añade una arista al grafo de módulos a cambio de nada.

## Git y commits

Conventional Commits en español: `feat(contexto): descripción corta`. La rama se crea **desde
`develop`** y el PR va **hacia `develop`** (`main` es la rama estable), con nombre
`<prefijo>/<id>-<descripcion_snake_case>` y prefijos `feature/ fix/ refactor/ hotfix/ docs/ test/
chore/ spike/`. El PR usa `.github/PULL_REQUEST_TEMPLATE.md` y requiere 1 aprobación. Ver
`CONTRIBUTING.md`.
