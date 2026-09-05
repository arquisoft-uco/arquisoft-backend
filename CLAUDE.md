# CLAUDE.md

Guía operativa del repositorio para Claude Code.

**Este archivo es un índice, no el tratado de arquitectura.** La fuente de verdad de convenciones
está en las skills de `.claude/skills/`, verificadas contra el código real:

| Necesitas | Carga |
|---|---|
| Capas, paquetes, puertos, eventos, CQRS, sufijos | `arquisoft-arquitectura` |
| Notification Pattern, validación, catálogo de mensajes, excepciones, logs, testing, estilo | `arquisoft-estandares` |
| MCPs preferidos y sus fallbacks | `arquisoft-mcps` |
| IDs de Context7 por tecnología del stack | `context7-stack` |
| Leer HU/HT, MER y ADRs de `arquisoft-docs` | `gh-docs-reader` |

El detalle largo y humano vive en `docs/ARQUITECTURA_Y_ESTRUCTURA.md`. **Si algo aquí contradice a
una skill, gana la skill.**

## Commands

```bash
# Build
./gradlew build
./gradlew build -x test        # skip tests

# Test
./gradlew test
./gradlew seguridad:infrastructure:test   # single module

# Lint (Checkstyle 10.12.5 — required by CI)
./gradlew checkstyleMain checkstyleTest

# Coverage (minimum 75%)
./gradlew jacocoTestReport

# Run locally (dev profile)
./gradlew bootRun --args='--spring.profiles.active=dev'

# List all modules
./gradlew projects
```

El gate real es `check` (tests + checkstyle + `jacocoTestCoverageVerification` + la tarea
`verificarCapasHexagonales` del build raíz), no `test`.

## Local Environment

```bash
cp .env.example .env
docker-compose up postgres rabbitmq redis keycloak  # infra only
./gradlew bootRun --args='--spring.profiles.active=dev'
```

`docker-compose up` levanta todo, backend incluido, en el puerto 8080. Swagger UI en
`http://localhost:8080/api/swagger-ui/index.html` (deshabilitado en prod). Los cargadores del
catálogo (`catalogo/cargar.sh`) y de las plantillas (`plantillas/cargar.sh`) corren **antes** del
backend (`service_completed_successfully`).

**Dev:** logging DEBUG, rate limiting deshabilitado, Swagger habilitado.
**Prod:** logging INFO a archivo, rate limiting 60 req/min global, Swagger deshabilitado.

Guía completa: [docs/EJECUCION_LOCAL.md](docs/EJECUCION_LOCAL.md).

## Architecture

Arquitectura hexagonal (Ports & Adapters) con **9 bounded contexts** y **14 módulos compartidos**.
Los contextos se comunican **exclusivamente** por eventos de dominio en RabbitMQ — nunca se importan
entre sí. Dirección de dependencias: `domain ← application ← infrastructure`, impuesta por el grafo
de módulos de Gradle y verificada por `verificarCapasHexagonales` (cuelga de `check`).

### Bounded Contexts

Con código hoy: `seguridad`, `usuarios`, `fichas`, `notificaciones` y `evaluaciones`. Los otros
cuatro (`proyectos`, `artefactos`, `repositorio_artefactos`, `entregables`) son andamiaje: solo su
`{Contexto}DataSourceConfig`.

| Context | Database |
|---------|----------|
| `seguridad` | *(sin DB — auth vía Keycloak + Redis)* |
| `usuarios` | `usuarios` |
| `fichas` | `fichas_perfil` |
| `notificaciones` | `notificaciones` |
| `proyectos` | `proyectos_grado` |
| `artefactos` | `artefactos` |
| `repositorio_artefactos` | `repositorio_artefactos` |
| `entregables` | `entregables` |
| `evaluaciones` | `evaluaciones` |

**Contexto de referencia: `fichas`** — el único completo (escritura, consulta, eventos, consumidor).
Cada uno de los otros cuatro aporta algo distinto y tiene un límite conocido; ver
`arquisoft-arquitectura`, que los enumera con su límite exacto.

**Son bases de datos separadas, no schemas.** `init-db.sql` hace un `CREATE DATABASE` por contexto y
cada `{Contexto}DataSourceConfig` apunta su propio `DataSource`, `EntityManagerFactory`,
`TransactionManager` y bean de Flyway a una URL distinta. Dos consecuencias no negociables:

- **Las migraciones viven en `db/migration/{contexto}/`, nunca sueltas en `db/migration/`.** Todos
  los contextos comparten un classpath en runtime: un archivo en la raíz lo recoge el Flyway de
  *cada* contexto y lo aplica en la base equivocada.
- **Una FK entre contextos es imposible.** Se modela como tabla réplica local poblada por eventos
  (`asesor_ficha`, `estudiante` en `fichas`).

`baselineOnMigrate` está en `false` en los cuatro contextos con `DataSource`. La versión es un
**timestamp**, `V{yyyyMMddHHmmss}__descripcion.sql`; nunca se retrocede un timestamp ni se edita una
migración ya aplicada.

### Shared Modules

`shared:util`, `shared:exception`, `shared:validation`, `shared:domain`, `shared:application`,
`shared:query`, `shared:logger`, `shared:tracing`, `shared:redis`, `shared:amqp`, `shared:web`,
`shared:minio`, `shared:jpa`, `shared:message`

Lo que hay que saber de cada uno para no romper el grafo:

| Módulo | Contiene | Restricción que sostiene el diseño |
|---|---|---|
| `shared:exception` | Las 5 bases de excepción + `BaseException`/`BaseError` | **Cero dependencias** — es hoja, para que `shared:message` pueda extender `InfrastructureException` sin ciclo |
| `shared:domain` | Solo `DomainEvent` (`…shared.events`) y `DomainRule` (`…shared.rules`) | Es lo único que ve `{contexto}/domain` |
| `shared:application` | `UseCase`/`VoidUseCase`/`SupplierUseCase`, `Interactor`/`VoidInteractor`/`SupplierInteractor`, `Finder`, puerto `EventPublisher` | Declara `api shared:domain`. **Un `{contexto}/domain` nunca lo declara** — si parece necesitarlo, el tipo está en la capa equivocada |
| `shared:validation` | Familia `Validator*` + `ValidationResult` + las dos excepciones de validación | Módulo propio, no un paquete de `shared:domain`: lo usan domain y application por igual |
| `shared:query` | Todo el vocabulario de lectura (`QueryCriteria`, `NodoFiltro`, `pagination/`, `dto/`, `ConsultaCriteriaQuery`) | **Cero Spring**; solo anotaciones Jackson, nunca `databind` |
| `shared:jpa` | Lo irreduciblemente atado a Spring Data (`PageableMapper`, `PaginationMapper`, `CampoSpec`, `QueryJpaSpecification`, `QueryRepository`) | Separado de `shared:query` para no imponer Spring a quien solo declara un criteria |
| `shared:util` | `UtilTexto`, `UtilUUID`, `UtilColeccion`, `UtilFecha`, `UtilNumero`, `UtilObjeto`, `UtilEnum` | Prefijo `Util` en inglés, el resto en español |
| `shared:tracing` | Contexto de traza completo (ver *Correlación*) | Único `shared:*` con capas hexagonales internas; **no** depende de spring-web ni spring-security |
| `shared:message` | Códigos, campos, límites, textos Swagger, `EventTopics`, enums `{Feature}Key`, fachada `Mensajes` | — |

**Un `shared:*` con un solo consumidor no es compartido.** Exige dos consumidores reales antes de
crear uno; `shared:notification` se disolvió dentro de `notificaciones` justo por esto.

### Layer Structure per Context

```
{context}/domain/
└── {feature}/
    ├── {Entity}Domain.java          # Agregado (sufijo Domain, sustantivo) — directo aquí, sin subpaquete
    ├── {Action}{Entity}Domain.java  # Objeto de acción — al lado del agregado, condicional
    ├── model/                # Value objects + el record de entrada de cada Rule
    ├── rules/impl/           # {Regla}RuleImpl — puras: sin Spring, sin dependencias de constructor
    ├── event/                # Eventos de dominio (extienden DomainEvent)
    ├── exception/            # Excepciones de dominio (→ 422)
    └── message/              # Constantes de mensaje de dominio (opcional)

{context}/application/
└── {feature}/
    ├── command/
    │   ├── primaryport/                    # Contrato primario del comando
    │   │   ├── interactor/impl/            # Dueño de @Transactional
    │   │   ├── model/{Action}{Entity}Command.java
    │   │   └── mapper/                     # Command → dominio. OBLIGATORIO en toda escritura
    │   ├── usecase/impl/                   # Colaborador interno — NO bajo primaryport/
    │   ├── validator/impl/                 # @Component, puro, sin if
    │   ├── finder/                         # Uno por consulta, implementa Finder<T,R>
    │   ├── secondaryport/                  # OutputPort + entity/ (record plano) + mapper/
    │   └── result/                         # Solo si el comando no devuelve UUID ni void
    └── query/
        ├── primaryport/                    # interactor/impl (@Transactional readOnly), model/, mapper/
        ├── usecase/impl/                   # Recibe el Criteria, no el Query
        ├── secondaryport/{Feature}QueryOutputPort.java
        ├── criteria/
        └── readmodel/

{context}/infrastructure/
└── {feature}/
    ├── exception/                          # Excepciones de infraestructura (→ 503)
    ├── command/
    │   ├── primaryadapter/
    │   │   ├── web/                        # Un Controller por acción + dto/ + mapper/
    │   │   └── amqp/{productor}/{entidad}/ # {Evento}Consumer + {Evento}Payload
    │   └── secondaryadapter/               # entity/ (JpaEntity) + mapper/ + repository/
    │                                       #   (o keycloak/, redis/, jwt/, smtp/…)
    └── query/
        ├── primaryadapter/web/             # Controller + dto/ + mapper/
        └── secondaryadapter/repository/    # JpaQueryEntity (@Subselect) + Specification +
                                            #   SortMapper + QueryOutputAdapter + QueryRepository
config/     # {Context}DataSourceConfig, {Contexto}Queues, *QueueConfig
security/   # {Context}Authorities
handler/    # {Context}GlobalExceptionHandler (@RestControllerAdvice) — solo seguridad tiene uno
filter/     # Filtros HTTP del contexto
src/main/resources/db/migration/{contexto}/   # Flyway — subcarpeta propia, siempre
```

**Cada paquete se llama como el sufijo de las clases que aloja** (`mapper/` → `*Mapper`,
`exception/` → `*Exception`). De ahí sale la regla que más se equivoca: un `@RestControllerAdvice`
va en `handler/`, nunca en `exception/`.

## Key Conventions — resumen

Cada punto está desarrollado, con su porqué y su archivo de referencia real, en las skills. Aquí solo
el enunciado, para reconocer una desviación de un vistazo.

**Dominio**
- Agregado: constructor privado, campos privados **no-`final`** asignados por setters privados
  (Notification Pattern), solo getters, sufijo `Domain` sustantivo. `crear(...)`/`reconstruir(...)`,
  nunca `build`/`rebuild`. Sin Lombok, sin Spring, sin `record`.
- Centinela `public static final X VACIO` + `esVacio()` (identidad) cuando el agregado puede llegar
  ausente. **Nada de `Optional` en records de dominio ni en firmas de `Validator`.**
- Objeto de acción `{Accion}{Entidad}Domain` solo si la acción arrastra más que el agregado; por
  defecto sus campos son `UUID` y escalares.
- Las `Rule` son puras y **no son beans**; el `{Accion}{Entidad}ValidatorImpl` las construye con
  `new` en un constructor sin argumentos y **no contiene un solo `if`**.
- Invariantes locales → `ValidationResult` acumulado → una `DomainValidationException`. Restricciones
  de conjunto (existencia, unicidad, propiedad) → `Rule` → `DomainException` 422. Nunca `if/throw`
  en el use case.
- **Orden de validación:** 1) integridad del dato → 2) existencia/unicidad contra BD → 3) reglas de
  negocio. Nunca se consulta la BD sobre un dato cuya integridad no se validó.
- **IDs siempre `UUID`.** Enums de catálogo: `desde(String)`/`esValido(String)`/`getId()`, nunca
  `valueOf` fuera del enum.

**Aplicación**
- El `Interactor` es el punto de entrada y dueño de
  `@Transactional(transactionManager = "{contexto}TransactionManager")` — qualifier **siempre**
  explícito (`usuariosTransactionManager` es `@Primary` y enlaza en silencio si se omite). Lectura:
  `@Transactional(readOnly = true, ...)`. `seguridad` no lleva ninguno: no tiene `DataSource`.
- El `UseCase` de escritura recibe **un objeto de dominio, nunca el `Command`**. El de consulta
  recibe el `Criteria`; el `Interactor` de consulta recibe **siempre un `Query`**
  (`ConsultaCriteriaQuery` genérico, o un `{Consulta}{Entidad}Query` propio que lo compone) y lo
  convierte con un `query/primaryport/mapper/`.
- Sin entrada → `SupplierInteractor<O>`/`SupplierUseCase<O>`. **`Void` como tipo de entrada está
  prohibido en todo el repo.**
- Los puertos hablan `Entity` (record plano), nunca `Domain`. `UseCase` mapea `Domain → Entity`;
  el `Finder` mapea de vuelta; el adaptador hace `Entity ↔ JpaEntity`.
- Un `Finder` **siempre devuelve valor** y nunca lanza por "no encontrado".
- Un fallo que el negocio registra es un **valor** (`sealed interface` de desenlace), no una
  excepción: cero `try/catch` en `application`.
- El que llama compone: todos los pasos encadenados cuelgan del mismo orquestador, y cada llamado
  recibe el objeto de dominio más estrecho que lee.
- Use cases y adaptadores son `@Component` — **nunca `@Service`**. Inyección por
  `@RequiredArgsConstructor`, nunca `@Autowired`, y siempre interfaces.

**Infraestructura**
- Un `Controller` por acción. `RequestDTO` = `record` **sin ninguna anotación** +
  `{Accion}{Entidad}RequestMapper` que llama a `Command.crear(...)`. Los identificadores del body
  llegan como `String` y se validan con `ValidatorUUID`, **nunca con una anotación Jakarta**.
- Ni el `ReadModel` ni el `Result` se serializan directo: van a un `ResponseDTO` por su
  `ResponseMapper`.
- El `CommandOutputAdapter` es pura delegación: **cero `try/catch`**, `save` (no `saveAndFlush`),
  `boolean` primitivo en los métodos de existencia, `logger.debug` solo en los de escritura.
- Aislamiento CQRS absoluto: `query/secondaryadapter` no importa nada de `command/secondaryadapter`,
  ni siquiera el `JpaEntity`. El `QueryRepository` **no extiende `JpaRepository`**.
- Un paquete `query/` solo existe si hay una lectura real detrás de un `primaryport`. Un chequeo de
  existencia para una `Rule` de comando va en el `OutputPort` de `command/`, vía `Finder`.
- Sin literales: códigos en `{Contexto}Codes`, campos en `{Contexto}Fields`, límites en
  `{Contexto}Limits`, Swagger en `{Contexto}ApiMessages`/`ApiCodes`/`ApiSecurity`, autorización en
  `{Contexto}Authorities.Expresiones.HAS_*`. **Las rutas son la excepción** y se quedan inline como
  placeholder de propiedad (`"${rutas.seguridad.auth.login:/login}"`); no existe `{Contexto}Routes`.
  Nunca el prefijo `/api`: ya es el `context-path`.
- **Un client role por endpoint, propio y distinto** — nunca se reutiliza el de otro. Dos endpoints
  sobre el mismo recurso se diferencian con un calificador
  (`fichas:ficha-perfil-coordinador:view` vs `fichas:ficha-perfil-asesor:view`).

**Eventos**
- El `UseCase` inyecta la **interfaz** `EventPublisher` y publica tras persistir. El agregado es una
  clase plana: no acumula eventos ni los drena.
- Un evento por **hecho de negocio**, no por destinatario, emitido por el use case dueño del hecho
  aunque otro lo orqueste. Carga todo lo que su consumidor necesita.
- La routing key se declara **una sola vez** en `EventTopics`; el nombre de cola se **deriva**
  (`{Contexto}Queues.PREFIJO + topic`). Una cola se declara con un `@Bean Declarables` que devuelve
  `ColaEvento.declarar(...)`, no bean a bean.
- Una transición de estado notifica (consumidor: `notificaciones`) salvo que la HU diga lo
  contrario — con la excepción del estado que es paso interno. Son **ocho piezas** en dos contextos;
  la lista está en `arquisoft-arquitectura`.

**Excepciones**
- Cuatro bases en `com.arquisoft.shared.exception`: `DomainException` (422),
  `DomainValidationException` (422 + `fieldErrors[]`), `ApplicationException` (400),
  `InfrastructureException` (503). Nunca `RuntimeException` directa. Constructor
  `super(message, errorCode)` — invertirlos compila y produce un bug silencioso.
- Viven **dentro del slice vertical del feature**, en la capa de su clase base. No hay `exception/` a
  nivel de contexto, y una subclase nunca va en distinta capa que su padre.
- `GlobalAppExceptionHandler` (`shared:web`) resuelve el status recorriendo la jerarquía; un contexto
  no define handler propio (solo `seguridad`, por colisión de nombres con Spring Security).

**Estilo**
- Español para el concepto de negocio, inglés para el sufijo técnico. Paquete de feature todo en
  minúsculas y sin separadores (`fichaperfil`).
- Nombres objetuales en contratos: `asesorFicha`, no `asesorFichaId`.
- `var` para locales cuando el lado derecho ya nombra el tipo; **no** con diamante, ni con clases
  anónimas, ni cuando el tipo declarado es deliberadamente una interfaz.
- Comprobación de nulidad **siempre** con `UtilObjeto.esNulo`/`noEsNulo`, nunca `== null` crudo, y
  sin declarar un `tieneX()` en un `record` para envolverlo.
- **Sin Javadoc y sin comentarios que repitan el código.** `domain/` y `application/` no llevan
  ninguno. En infraestructura, solo para lo que el código no puede mostrar; si cabe, va al mensaje de
  commit o a la skill.

## Catálogo de mensajes

Dos mundos que no se mezclan:

- **Constantes Java** (`shared:message`) — lo que el compilador exige constante o una herramienta
  matchea exacto: códigos, nombres de campo, límites, textos de Swagger, routing keys, nombres de
  cola/exchange/bean/header, marcadores de log greppables, etiquetas de display de un enum (su fuente
  es el MER). Un identificador usado **solo dentro de una clase** se queda `private static final` de
  esa clase; se promueve cuando aparece un segundo lector.
- **Catálogo en Redis** — la prosa que lee un humano (errores y logs). Texto en
  `catalogo/{contexto}.properties`, cargado por `catalogo/cargar.sh` (ADR-013), referenciado por un
  enum `{Feature}Key` que declara clave y **aridad**, registrado en `ClavesCatalogo`. Clave con
  formato `contexto.capa.objeto.tipo.descripcion`.

Se resuelve **siempre** por la fachada estática `Mensajes` — no hay bean inyectable. Marcadores `%s`
para el cliente (`Mensajes.formatear`), `{}` para logs (al `AppLogger` se le pasa **la clave**, nunca
el texto ya resuelto). **Nunca `Mensajes.obtener(clave).formatted(args)`.** `CatalogoCargaTest` rompe
el build ante una clave sin texto, un texto sin clave, un enum ausente de `ClavesCatalogo` o una
aridad que contradice los marcadores. Procedimiento en [catalogo/README.md](catalogo/README.md).

**Las plantillas de correo viven en Redis también**, en su propio espacio de claves
(`plantilla.correo-base`, subido por `plantillas/cargar.sh`) y con refresco periódico validado por
`HuecosPlantillaCorreo.verificar`. El prefijo `plantilla.` no es negociable: `catalogo/podar.sh`
barre `<contexto>.*`. Ver [plantillas/README.md](plantillas/README.md).

## Logging y correlación

**Logging:** inyecta el puerto `AppLogger` (`shared:logger`) por constructor — no `@Slf4j`, del que
no queda ninguno en los cinco contextos con código. `warn` para 4xx, `error` para 5xx. La estructura
exacta por tipo de flujo (escritura: tres líneas; lectura: dos `debug`; evento: dos `INFO` que pone
el adaptador) está en `arquisoft-estandares`.

**Nunca loguees desde un método `@Bean` ni desde un `@PostConstruct`.** `Mensajes.instalar(...)`
ocurre dentro de un `@Bean` de `CatalogoMensajesRedisConfig`, así que cualquier bean construido antes
resuelve **la clave cruda** y, como esa clave no lleva `{}`, SLF4J descarta también los argumentos:
se pierden a la vez el texto y los valores. Un log de arranque que reporte configuración efectiva va
en un `@EventListener(ApplicationReadyEvent.class)`. La excepción es la rama que aborta el arranque:
ahí el log se queda, porque `ApplicationReadyEvent` no llega a dispararse.

**Correlación — `shared:tracing` es dueño de todo el contexto de traza.** Es el único `shared:*` con
capas hexagonales internas (`domain/traza` → `application/traza/{primaryport,secondaryport}` →
`infrastructure/traza`), de modo que `MdcContextoDiagnosticoOutputAdapter` es la única clase del repo
que toca `org.slf4j.MDC` y el dominio (`Traceparent`, `IdentificadorTraza`, `ClienteIp`) queda en
Java puro.

- `TrazaDomain` guarda solo lo común a todo origen; lo específico va en `DetalleOrigenTraza`
  (`sealed`, un record por origen: `DetalleHttpTraza`, `DetalleEventoTraza`,
  `DetalleProgramadoTraza`). El pattern-matching exhaustivo del adaptador impide que un campo
  HTTP-only se filtre a una línea `EVENTO`, y obliga al compilador a cubrir cada origen nuevo.
- **Ningún otro módulo implementa un contrato de `shared:tracing`**: se inyecta `GestorTraza`, se
  abre un alcance y se llaman métodos con nombre.
  `try (var alcance = gestorTraza.abrir(SolicitudTraza.paraHttp(...))) { … }` —
  `AlcanceTraza` es `AutoCloseable` y **debe** usarse con try-with-resources: `close()` restaura el
  MDC capturado, no hace un `remove`, para que un alcance anidado devuelva el valor externo. No hay
  `registrarAtributo(clave, valor)` genérico a propósito: cada campo nuevo cuesta una clave en
  `TrazaKeys` más un método con nombre, que es lo que impide que tokens y PII acaben en el MDC.
- `correlacionId` agrupa la transacción entre saltos y se reutiliza **verbatim** si llega en
  `X-Correlation-Id` (normalizarlo rompería la correlación con quien llama); `transaccionId` se
  regenera en cada salto; `transaccionPadreId` nombra qué salto originó este. `ErrorResponseDTO` los
  expone como `traceId` y `transaccionId` (`traceId` conserva el nombre: es contrato con el front).
- **El MDC no es ambiente:** solo tiene datos dentro de un `AlcanceTraza` abierto.
  `TrazabilidadFilter` abre uno por petición HTTP y `AbstractEventConsumer` uno por consumo AMQP,
  pero un `@Scheduled` **no recibe ninguno**: debe abrir el suyo con
  `gestorTraza.abrir(SolicitudTraza.paraProgramado())` o sus líneas salen sin traza.
- **Dos filtros, y el orden importa:** `TrazabilidadFilter` (`shared:web`, `@Order(-300)`) es el más
  externo y dueño del alcance y de la línea `AUDIT`; `IdentidadTrazaFilter`
  (`seguridad:infrastructure`) solo añade el usuario y se registra **dentro** de la cadena de Spring
  Security (`addFilterAfter(BearerTokenAuthenticationFilter.class)`) — en `LOWEST_PRECEDENCE`
  quedaría tras `AuthorizationFilter` y todo 403 se auditaría como anónimo. Va declarado como `@Bean`
  más un `FilterRegistrationBean` deshabilitado, porque si no el servlet container lo registraría
  también y `OncePerRequestFilter` saltaría la segunda pasada.
- **MDC a través de `@Async`:** la externalización de eventos de Spring Modulith a RabbitMQ corre en
  otro hilo (`applicationTaskExecutor`, tras el commit), donde el MDC estaría vacío y
  `TrazaMessagePostProcessor` fabricaría una correlación nueva. `MdcTaskDecorator`
  (`shared:tracing/infrastructure/traza/config/`) cierra ese hueco capturando el contexto vía
  `ContextoDiagnosticoOutputPort` al encolar la tarea y restaurándolo dentro del ejecutor. Se
  registra como `TaskDecorator` `@Bean` en `TrazabilidadConfig`; Spring Boot lo aplica solo, por eso
  no hay `@EnableAsync` ni executor propio en el proyecto. Es un problema cruzando **hilos** del
  mismo proceso, distinto de la propagación entre procesos por headers AMQP.
- `servicioNombre`/`version` **no** son claves de MDC: son constantes de proceso añadidas como
  miembros JSON estáticos con `logging.structured.json.add.*`.

## Technology Stack

| Component | Version |
|-----------|---------|
| Java | 21 (Virtual Threads activos automáticamente) |
| Spring Boot | 4.0.5 |
| Gradle | 9.0.0 |
| PostgreSQL | 18 (una **base** por contexto, con su DataSource, EntityManagerFactory y Flyway) |
| RabbitMQ | 4.2.5 |
| Redis | 7 (Lettuce) |
| Keycloak | 26.6 (OAuth2/OIDC Resource Server) |
| Flyway | 12.4.0 |
| JUnit | 6.0.3 (Jupiter) |
| Spring Modulith | 2.0.0 |
| Jackson | 3 — `tools.jackson.databind.*` |
| Lombok | 1.18.36 |
| Bucket4j | 8.18.0 (`com.bucket4j:bucket4j_jdk17-core`) |

La autoconfiguración de `DataSource` está excluida globalmente. Ninguno es `@Primary` salvo
`usuarios`, cuyo `usuariosTransactionManager` **sí** lo es — de ahí que el qualifier de
`@Transactional` sea obligatorio.

Jackson 3 movió `databind` a `tools.jackson.databind.*`;
`com.fasterxml.jackson.databind.ObjectMapper` **no resuelve**. Las **anotaciones** siguen en
`com.fasterxml.jackson.annotation.*`.

**Nunca crear un `@Bean TaskExecutor` manual** (ADR-008 — Virtual Threads ya gestionados).

## Security

- JWT validado contra el JWK Set de Keycloak (`seguridad/infrastructure/config/security/SeguridadConfig`).
- CORS por defecto: `localhost:3000`, `4200`, `5173` (`CORS_ALLOWED_ORIGINS`). CSRF deshabilitado,
  sesiones stateless.
- `TrazabilidadFilter` emite una línea `AUDIT` por petición con método, URI, usuario, duración y
  status (salta Swagger/actuator). Al ser el filtro más externo, también audita 401, 403 y 429.
- **Rate limiting con Bucket4j**, buckets por IP en Redis (`RedisBucketResolver`, Lettuce). Defaults:
  100 req/min global en dev, 60 en prod; 5 login/min dev, 3 prod. Ambas cuotas recargan
  **`greedily`** — con recarga por lotes, `getNanosToWaitForRefill()` reportaría el tiempo de
  reponer la ventana entera y `X-Rate-Limit-Retry-After-Seconds` diría al cliente que espere 60 s
  cuando solo necesita un token.

  **Ante un error de Redis el limitador degrada a cuota local — no falla abierto ni cerrado.** Fallar
  cerrado convertía cualquier caída en la denegación de servicio que el limitador existe para
  evitar; fallar abierto borraba el límite y dejaba `/login` expuesto a fuerza bruta mientras durara.
  Así que replica lo que `CatalogoMensajesRedis` hace con el catálogo: un `AtomicBoolean degradado`,
  un respaldo en memoria, y `MonitorLimiteSolicitudes` (`@Scheduled`, con su propio `AlcanceTraza`)
  restaurando la cuota distribuida. Tres consecuencias:
  - **El resolver consume, no reparte buckets.** `BucketResolver.consumir(ip, esLogin)` sustituyó a
    `resolveBucket`/`resolveLoginBucket`, y el filtro perdió su `try/catch`. Lo obliga que
    `proxyManager.getProxy` sea perezoso: contacta Redis solo al consumir, así que quien reparte el
    bucket nunca se entera de la caída. El fallo hay que capturarlo donde vive el estado que
    reacciona a él.
  - **La bandera de degradado no es una optimización.** Sin ella, cada petición de la caída sigue
    intentando Redis y paga su timeout, agotando hilos y pool: la misma autolesión que fallar
    cerrado, por otra ruta.
  - **La caché local está acotada y debe seguirlo.** `BucketsLocales` es un `LinkedHashMap` por orden
    de acceso, con techo en `security.rate-limit.max-tracked-ips` (10 000 por defecto). Sin techo,
    una caída de Redis se convierte en vector de agotamiento de memoria. Mientras degrada, la cuota
    es por instancia: N réplicas dan N× cuota — sigue siendo un límite, y es el precio de no tener
    estado compartido justo cuando el estado compartido es lo que falló.

  `JwtBlacklistFilter` **sí** falla abierto, y ahí es lo correcto por una razón que no aplica arriba:
  el radio de impacto está acotado porque la entrada de la lista negra expira con el propio token
  (5–15 min), así que una caída solo puede honrar un token revocado hasta que habría expirado igual.
  Detalle: [docs/fail-open-vs-fail-closed.md](docs/fail-open-vs-fail-closed.md).

## Testing

- **Unitario:** JUnit 6 + Mockito + AssertJ, `@ExtendWith(MockitoExtension.class)`, sin contexto
  Spring. Los tests de `Rule` y `Validator` no necesitan Mockito — las reglas son puras.
- **Slice de repositorio:** `@DataJpaTest`
  (`org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`) + H2, sembrando con
  `TestEntityManager`. **`@SpringBootTest` no se usa en ningún test de este repo.**
- **Slice de controller:** `@WebMvcTest`
  (`org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`) con
  `@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class, …})`
  — sin `GlobalAppExceptionHandler` toda excepción sale 500; sin `AppLoggerConfig` falta el bean
  `AppLogger`. Mocks con `@MockitoBean` (nunca `@MockBean`), autenticación con
  `SecurityMockMvcRequestPostProcessors.jwt().authorities(...)` usando la constante de
  `{Contexto}Authorities` — nunca `@WithMockUser` (prefija `ROLE_`).
- Spring Boot 4 reubicó los paquetes de slice test: las rutas
  `org.springframework.boot.test.autoconfigure.*` de Boot 3 no existen.
- Nombres `debeHacerAlgo_cuandoCondicion()`, patrón Arrange / Act / Assert con sus marcadores, sin
  Javadoc.
- **Cobertura mínima 75%**, verificada por `check`. Excluidos: `*DTO`, `*Command`, `*ReadModel`,
  `*Application`, `*Entity` y `config/**`. **`*Domain` NO está excluido.** Los `shared:*` no aplican
  JaCoCo.

## Desviaciones conocidas — presentes en el código, NO copiar

| Qué | Dónde | Convención que rompe |
|---|---|---|
| `CrearUsuarioRequestDTO` con anotaciones Jakarta + `toCommand()` | `usuarios/…/web/dto/` | Debería ser un `record` desnudo + `CrearUsuarioRequestMapper` |
| `EstadoEvaluacionCommandRepository` | `fichas/…/estadoevaluacion/…/repository/` | Código muerto: ningún `OutputPort`/`OutputAdapter` lo consume |
| `UsuarioCommandOutputAdapter` no persiste | `usuarios/…/repository/` | **Deliberado**, no una tarea pendiente: `usuarios` es contexto de ejemplo. Consecuencia: `existePorEmail` siempre da `false` y `UsuarioEmailUnicoRule` nunca dispara |
| `fichas/application/usuario` | `command/usecase/RegistrarUsuarioUseCase` | Stub: por eso no tiene `Interactor` ni `@Transactional` y el `Consumer` inyecta el `UseCase` |
| `UsuarioCreadoConsumer` en `amqp/` plano | `fichas/…/usuario/…/amqp/` | Le faltan los dos segmentos `{productor}/{entidad}/`. En vías de retirarse |
| Los cuatro `*ResponseDTO` como clases Lombok | `seguridad/…/auth/…/web/dto/` | Los `ResponseDTO` son `record`s. Copia de ahí la cadena `Result → ResponseMapper → ResponseDTO`, no la forma del DTO |
| Enums de catálogo en dos ubicaciones | `domain/{catalogo}/` vs `domain/{feature}/model/` | **Decisión abierta del proyecto, no la "arregles".** Un enum nuevo sigue lo que ya use su contexto |

## Reference Documentation

- [docs/ARQUITECTURA_Y_ESTRUCTURA.md](docs/ARQUITECTURA_Y_ESTRUCTURA.md) — referencia larga de arquitectura
- [docs/ARQUITECTURA_ASINCRONICO_ARQUISOFT.md](docs/ARQUITECTURA_ASINCRONICO_ARQUISOFT.md) — eventos de dominio + outbox
- [docs/PATRON_QUERY_OBJECT_FILTROS_DINAMICOS.md](docs/PATRON_QUERY_OBJECT_FILTROS_DINAMICOS.md) — Query Object + Specification
- [docs/EJECUCION_LOCAL.md](docs/EJECUCION_LOCAL.md) — setup local completo
- [CONTRIBUTING.md](CONTRIBUTING.md) — flujo git y nomenclatura de ramas

Los tres primeros son de lectura humana: no los cargues enteros en contexto — las skills ya traen lo
que un agente necesita.
