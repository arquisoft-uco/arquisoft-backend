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

**Sin `Optional` en records de dominio ni en firmas de validator.** `Optional` es tipo de retorno de
un `Finder` y nada más: el `UseCase` lo desenvuelve. Un agregado ausente viaja como su centinela
`VACIO` (`.orElse(FichaPerfilDomain.VACIO)`, con `esVacio()` comparando identidad); un valor suelto
viaja como el valor más un `boolean` explícito (`boolean asesorExiste`) dentro de su record
`Existencia{Concepto}`.

## Identificadores y DTOs

Los IDs en el body HTTP llegan como `String`, nunca `UUID` tipado. Su formato **nunca** se valida
con una anotación Jakarta — ni custom ni de librería — sino en `Command.crear(...)` vía
`ValidatorUUID.uuidValido(...)`, convirtiendo con `UtilUUID.generarUUIDDesdeTexto`. El `Command`
sí está tipado `UUID`. Los `@PathVariable` sí son `UUID`. Ver
`fichas/application/.../fichaperfil/command/primaryport/model/RegistrarFichaPerfilCommand.java`.

Dos convenciones de DTO coexisten — no mezclar dentro de un mismo contexto:
- Contextos pequeños (`seguridad`, `usuarios`): el DTO lleva `@NotBlank`/`@NotNull` y su propio `toCommand()`.
- **Contextos grandes (`fichas` — el patrón a seguir):** el DTO es un `record` sin ninguna
  anotación; un `{Accion}{Entidad}RequestMapper` externo (`final`, constructor privado, `static
  toCommand`) llama a `Command.crear(...)`. Ver
  `fichas/infrastructure/.../fichaperfil/command/primaryadapter/web/mapper/RegistrarFichaPerfilRequestMapper.java`.

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

**Dónde vive un enum de catálogo es una decisión abierta del proyecto** — hoy coexisten
`domain/{catalogo}/` (cuando tiene tabla propia: `EstadoFicha`, `TipoItem`, `EstadoEvaluacion`) y
`domain/{feature}/model/` (cuando no la tiene). Un enum nuevo sigue lo que ya use su contexto; no
declares "settled" una convención que no lo está.

## Excepciones (4 bases, en `com.arquisoft.shared.exception`)

| Base | HTTP | Cuándo | Dónde vive |
|---|---|---|---|
| `DomainException` | 422 | Invariante, "no encontrado", duplicado, **propiedad/no-propietario** | `domain/{feature}/exception/` |
| `DomainValidationException` | 422 + `fieldErrors[]` | Notification Pattern con varios errores | la lanza `ValidationResult` |
| `ApplicationException` | 400 | Orquestación de application; también `FiltroException`/`FiltroInvalidoException` de `shared:query` | `application/{feature}/exception/` |
| `InfrastructureException` | 503 | Fallo real de infraestructura (BD caída, timeout) | la levantan los `OutputAdapter` |

Nunca `RuntimeException` directa. Constructor `super(message, errorCode)` — ambos `String`, así que
invertirlos compila y produce un bug silencioso. **No hay un caso 403 propio para "no eres el
dueño"**: se modela como otro 422 (`FichaNoPropietarioException` extiende `DomainException`). El
`GlobalAppExceptionHandler` de `shared:web` resuelve el status recorriendo la jerarquía; un contexto
no define handler propio (solo `seguridad`, por colisión de nombres con Spring Security).

## Checkstyle (obligatorio en CI — `config/checkstyle/checkstyle.xml`)

Línea máx. 150 caracteres · archivo máx. 500 líneas · método máx. 60 líneas · máx. 7 parámetros ·
sin tabs · sin wildcard imports · PascalCase tipos / camelCase métodos-campos / UPPER_SNAKE
constantes · `_` permitido solo en nombres de test.

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
  `*Aggregate`, `*DTO`, `*Command`, `*ReadModel`, `*Application`, `*Entity` (cubre `JpaEntity`/
  `JpaQueryEntity`) y `config/**`. Ojo: **`*Domain` NO está excluido** — el agregado cuenta para el
  umbral. Los módulos `shared:*` no aplican jacoco.

El gate real es `check` (tests + `checkstyleMain`/`checkstyleTest` + cobertura), no `test`.

## Inyección y logging

Constructor injection con `@RequiredArgsConstructor` — nunca `@Autowired`, nunca `@Service` (todo
use case y adaptador es `@Component`). Se inyectan interfaces, nunca implementaciones. Logging vía
el puerto `AppLogger` (`shared:logger`) inyectado por constructor — no `@Slf4j` (desviación conocida
en `seguridad`/`usuarios`; no replicarla). `warn` para 4xx, `error` para 5xx.

**Nunca loguear desde un método `@Bean` ni desde un `@PostConstruct`:** `Mensajes.instalar(...)`
ocurre dentro de un `@Bean`, así que cualquier bean construido antes resuelve la **clave cruda** y,
como esa clave no lleva `{}`, SLF4J descarta también los argumentos. Un log de arranque que reporte
configuración efectiva va en un `@EventListener(ApplicationReadyEvent.class)`.

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
commit o a `CLAUDE.md`.

## Git y commits

Conventional Commits en español: `feat(contexto): descripción corta`. La rama se crea **desde
`develop`** y el PR va **hacia `develop`** (`main` es la rama estable), con nombre
`<prefijo>/<id>-<descripcion_snake_case>` y prefijos `feature/ fix/ refactor/ hotfix/ docs/ test/
chore/ spike/`. El PR usa `.github/PULL_REQUEST_TEMPLATE.md` y requiere 1 aprobación. Ver
`CONTRIBUTING.md`.
