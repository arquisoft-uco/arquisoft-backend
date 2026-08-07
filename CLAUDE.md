# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

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

## Local Environment

```bash
cp .env.example .env
docker-compose up postgres rabbitmq redis keycloak  # infra only
./gradlew bootRun --args='--spring.profiles.active=dev'
```

`docker-compose up` starts everything including the backend on port 8080. Swagger UI is available at `http://localhost:8080/api/swagger-ui/index.html` (disabled in prod).

**Dev profile:** DEBUG logging, rate limiting disabled, Swagger enabled.  
**Prod profile:** INFO logging to file, rate limiting 60 req/min global, Swagger disabled.

## Architecture

Hexagonal Architecture (Ports & Adapters) with **8 bounded contexts** and **8 shared modules**. Contexts communicate exclusively via RabbitMQ domain events — they never import each other.

### Bounded Contexts

| Context | DB Schema |
|---------|-----------|
| `seguridad` | *(sin DB — auth vía Keycloak + Redis)* |
| `usuarios` | `usuarios` |
| `fichas` | `fichas_perfil` |
| `proyectos` | `proyectos_grado` |
| `artefactos` | `artefactos` |
| `repositorio_artefactos` | `repositorio_artefactos` |
| `entregables` | `entregables` |
| `evaluaciones` | `evaluaciones` |

### Shared Modules

`shared:domain`, `shared:amqp`, `shared:logger`, `shared:redis`, `shared:web`, `shared:minio`, `shared:postgres`, `shared:message`

There is no `shared:validation` module — `DomainValidator` / `ValidationResult` live in the `com.arquisoft.shared.validation` **package** inside `shared:domain`.

### Layer Structure per Context

```
{context}/domain/
└── {feature}/
    ├── aggregate/   # Aggregate roots (suffix Aggregate) — no Lombok, no Spring
    ├── port/out/    # Output port interfaces write-side (suffix OutputPort)
    ├── model/       # Value objects (optional) — no Lombok, no Spring
    └── message/     # Domain message constants (optional)
event/               # Domain events shared across features (extend DomainEvent)
exception/           # Domain exceptions shared across features (extend DomainException)

{context}/application/
└── {feature}/
    ├── command/
    │   ├── interactor/
    │   │   ├── {Action}{Entity}Interactor.java        # Entry-point interface
    │   │   └── impl/
    │   │       └── {Action}{Entity}InteractorImpl.java  # Owns @Transactional
    │   ├── usecase/
    │   │   ├── {Action}{Entity}UseCase.java           # Use case interface
    │   │   └── impl/
    │   │       └── {Action}{Entity}UseCaseImpl.java   # Business orchestration (no transaction)
    │   ├── validator/
    │   │   └── {Feature}Validator.java        # Reusable existence/uniqueness/ownership checks
    │   ├── model/
    │   │   └── {Action}{Entity}Command.java   # Command input
    │   └── result/
    │       └── {Concepto}Result.java          # Command output, only when it is not UUID/void
    └── query/
        ├── usecase/
        │   ├── {Consult}{Entity}UseCase.java
        │   └── impl/
        │       └── {Consult}{Entity}UseCaseImpl.java
        ├── port/out/
        │   └── {Feature}QueryOutputPort.java  # Output port read-side
        ├── criteria/
        │   └── {Feature}Criteria.java
        └── readmodel/
            └── {Feature}ReadModel.java

{context}/infrastructure/
└── {feature}/
    ├── command/
    │   ├── adapter/in/web/
    │   │   ├── {Action}{Entity}Controlador.java
    │   │   └── dto/
    │   │       └── {Action}{Entity}RequestDTO.java
    │   └── adapter/out/persistence/
    │       └── {Feature}CommandOutputAdapter.java
    ├── query/
    │   ├── adapter/in/web/
    │   │   └── {Consult}{Entity}Controlador.java
    │   └── adapter/out/persistence/
    │       └── {Feature}QueryOutputAdapter.java
    └── persistence/
        ├── {Feature}Entity.java
        ├── {Feature}Repository.java
        └── {Feature}Mapper.java
config/              # Spring configuration shared within context
filter/              # HTTP filters (if applicable to context)
db/migration/        # Flyway migrations
```

Dependency direction is strictly enforced: `domain ← application ← infrastructure`.

## Key Conventions

**Aggregate roots:** No public constructor, private non-`final` fields assigned by private setters (Notification Pattern — a `final` field cannot be assigned from a method), only public getters, suffix `Aggregate` (e.g., `UsuarioAggregate`, `FichaPerfilAggregate`). Use `crear(...)` for new instances, `reconstruir(...)` for reconstructing from DB — **not** `build()`/`rebuild()`. Entity package segments are all-lowercase with no separators (`domain/fichaperfil/`, not `domain/fichaPerfil/`).

**IDs:** Always UUID — never `Long` or `Integer`.

**Domain events:** Extend `DomainEvent`. After persisting an aggregate, drain its unpublished events and publish via `EventPublisher` (RabbitMQ, publisher confirms, manual ACK, prefetch=1).

**Interactor (command side):** The interactor — not the use case — is the entry point and owns the transaction: `{Action}{Entity}InteractorImpl` implements `{Action}{Entity}Interactor`, annotates `ejecutar` with `@Transactional(transactionManager = "{context}TransactionManager")` and delegates to the use case. Rationale: an operation may lean on several use cases, so the unit of work belongs one layer above; it also guarantees the right transaction manager is active when domain events reach the outbox (`ContextAwareEventPublicationRepository`). Applied in `fichas` and `usuarios`. `seguridad` also has the interactor layer, but its interactors declare no `@Transactional`: the context has no `DataSource` of its own (Keycloak + Redis), so there is no unit of work to delimit. The use case must **not** implement the `Interactor` interface — two beans for the same port make injection ambiguous.

**Transactional (command):** the qualifier is always explicit — `@Transactional(transactionManager = "{context}TransactionManager")` — and lives on the interactor. It is required for outbox atomicity when the operation publishes events. Example: `@Transactional(transactionManager = "usuariosTransactionManager")`. `seguridad` is the exception: no `DataSource`, so no annotation.

**Transactional (query):** Query use cases annotate the class with `@Transactional(readOnly = true, transactionManager = "{context}TransactionManager")`. The qualifier is mandatory here too: `usuariosTransactionManager` is the `@Primary` bean, so a bare `@Transactional` does not fail at startup — it silently binds to the `usuarios` transaction manager.

**Input ports:** The entry point of a command is the `Interactor` interface in `application/{feature}/command/interactor/`, implemented by `{Action}{Entity}InteractorImpl` in the nested `impl/` package. The use case it delegates to is the `UseCase` interface in `application/{feature}/command/usecase/` (or `application/{feature}/query/usecase/` on the read side), implemented by `{Action}{Entity}UseCaseImpl` in the nested `impl/` package. There is no `port/in/` package on the application layer — interfaces and implementations live together under `interactor/` and `usecase/`.

**Output ports:** Write-side in `domain/{feature}/port/out/`; read-side in `application/{feature}/query/port/out/`, suffix `OutputPort` (e.g., `FichaPerfilOutputPort`, `FichaPerfilQueryOutputPort`).

**Input adapters:** REST controllers in `infrastructure/{feature}/command/adapter/in/web/` (or `query/.../web/`), suffix `Controlador` (e.g., `RegistrarFichaPerfilControlador`). AMQP consumers in `infrastructure/{feature}/command/adapter/in/amqp/`, suffix `InputAdapter` (e.g., `UsuarioCreadoInputAdapter`).

**Output adapters:** JPA repositories, Redis, Keycloak, MinIO integrations in `infrastructure/{feature}/command/adapter/out/persistence/` (or appropriate sub-package for non-JPA), suffix `OutputAdapter` (e.g., `FichaPerfilCommandOutputAdapter`, `KeycloakAuthOutputAdapter`). Implement the corresponding `OutputPort` interface.

**Use case implementations:** `{Action}{Entity}UseCaseImpl` in `usecase/impl/`, implementing `{Action}{Entity}UseCase` (e.g., `RegistrarFichaPerfilUseCaseImpl`, `AutenticarUsuarioUseCaseImpl`). Annotated `@Component` — **never `@Service`**, which is not used anywhere in this project. The use case is a plain collaborator invoked by its interactor — the adapter always injects the `Interactor`, never the `UseCase`.

**Validators:** Existence, uniqueness and ownership checks live in `{Feature}Validator` (`application/{feature}/command/validator/`, `@Component`), not as inline `if/throw` blocks inside the use case — that keeps rules reusable across features (e.g., `EstudiantesFichaValidator` is shared by ficha registration and student assignment). Method names state the rule: `validarAsesorExiste`, `validarTituloUnico`, `validarSinDuplicados`.

**Validation order (mandatory):** 1) data integrity (format, required, length, duplicates within the payload — accumulable), 2) existence and uniqueness against the DB, 3) business rules in the aggregate. Never query the database on data whose integrity has not been established first.

**Command results:** a command normally returns `UUID` or `void`, and then it needs no type of its own. When it does return something richer (only `seguridad` today), the record lives in `application/{feature}/command/result/` with suffix `Result` (e.g., `AutenticacionResult`, `RefrescoTokenResult`, `ValidacionTokenResult`) — never nested inside the `UseCase` interface, and never in `model/`, which is reserved for input. This mirrors the read side, where `criteria/` is the input and `readmodel/` the output.

**Commands:** Input data for use cases in `application/{feature}/command/model/`, suffix `Command` (e.g., `RegistrarFichaPerfilCommand`). Implemented as Java `record`. DTOs in `infrastructure/{feature}/command/adapter/in/web/dto/` own a `toCommand()` factory method.

**ReadModels:** Flat query projections in `application/{feature}/query/readmodel/`, suffix `ReadModel` (e.g., `FichaPerfilReadModel`). Implemented as Java `record`. The read side projects straight from the JPA entity via the mapper — there is no `fromDomain(Aggregate)` factory.

**DTOs:** `RequestDTO` is a Java `record` with Jakarta annotations (`@NotBlank`, `@NotNull`) and a `toCommand()` method, living in `infrastructure/{feature}/command/adapter/in/web/dto/`. Generic technical DTOs (`ErrorResponseDTO`, `PageResponseDTO<T>`) come from `shared:web` — never redefine them per context. The DTO is a pass-through: it carries data and guarantees its integrity, it does not run business rules.

**Identifiers in request bodies:** received as `String` annotated `@UuidValido` (`shared:web`), never as `UUID` — a malformed `UUID` field would otherwise surface as a blind Jackson deserialization error. Element-level constraints work on lists (`List<@UuidValido String> estudiantes`), so every offending element is reported in `fieldErrors[]`. Conversion happens in `toCommand()` via `UtilUUID.generateUUIDFromString`; the Command stays typed `UUID`. Path variables remain `UUID`.

**Objectual naming in contracts:** DTO and Command components carry what they represent, not the column name: `asesorFicha` (not `asesorFichaId`), `estudiantes` (not `estudiantesIds`). Field-name constants live in `FichasFields.{Aggregate}.*`.

**No hardcoded literals in adapters:** response codes come from `ApiCodes` (`shared:web`), Swagger texts from `FichasApiKeys` (`shared:message`), authorities from `FichasAuthorities` (`@PreAuthorize(FichasAuthorities.Expresiones.HAS_...)`, with the raw authority available for tests), base paths from `FichasRoutes`. Jakarta `message =` references `ValidationKeys` and `max =` references `FichasLimits` — changing a message or a length must never require touching a validation annotation and a catalog separately.

**Message catalog:** runtime texts (errors, logs) live in `.properties` under `shared:message` and resolve through the injectable `CatalogoMensajes` port — `private final CatalogoMensajes catalogo;` in application/infrastructure, the static `Mensajes` facade in domain (aggregates and exceptions have no injection point). Keys follow `contexto.capa.objeto.tipo.descripcion`. What the compiler forces to stay a constant — error codes (`*Codes`), limits (`*Limits`), field names (`*Fields`) — does not go to the bundle, because annotation values must be constant expressions (JLS §9.7.1). `CatalogoMensajesClavesTest` fails the build on a key with no text or a text with no key. See [shared/message/README.md](shared/message/README.md).

**Exceptions:** Every context exception extends one of five bases from `com.arquisoft.shared.exception`: `DomainException` (422), `ApplicationException` (400), `AuthorizationException` (403), `InfrastructureException` (503), `DomainValidationException` (422 + `fieldErrors[]`). Never `RuntimeException` directly. The constructor signature is `super(message, errorCode)` — both are `String`, so swapping them compiles and silently swaps the two fields in the response. `GlobalAppExceptionHandler` (`shared:web`) resolves the HTTP status by walking the superclass chain; contexts do not define their own handler (only `seguridad` does, for a name clash with Spring Security).

**Business rules:** Validated inside the aggregate (→ 422), never with `if/throw` in the use case. The use case reads the state a rule needs via a port and passes it as a parameter to the factory. Existence, DB-duplicate and resource-ownership checks do belong in the use case (→ 400 / 403).

**Naming:** Spanish for business concepts (`crearFicha`, `FichaException`), English for technical suffixes (`Aggregate`, `Interactor`, `UseCase`, `Impl`, `OutputPort`, `InputAdapter`, `OutputAdapter`, `ReadModel`, `DTO`, `Command`) — `Controlador` is the one Spanish exception, naming REST controllers.

**Injection:** Always constructor injection via `@RequiredArgsConstructor` — never `@Autowired`.

**Logging:** inject the `AppLogger` port (`shared:logger`) by constructor — `private final AppLogger logger;` — instead of `@Slf4j`, so application and infrastructure code does not depend on SLF4J. `Slf4jAppLogger` is the default strategy, wired as a prototype bean by `AppLoggerConfig` (each class gets a logger named after itself). Applied throughout `fichas`; `seguridad` and `usuarios` still use `@Slf4j` (pending migration). `warn` for 4xx, `error` for 5xx. Structured JSON via `shared:logger` (includes `traceId`, `userId`). In `@WebMvcTest` slices add `AppLoggerConfig.class` to `@Import`; in unit tests pass a mock.

**Correlation:** `TraceIdFilter` (`shared:web`) reuses the incoming `X-Correlation-Id` header, falls back to the W3C `traceparent` trace-id, and only then generates a UUID. The resolved id goes into the MDC, is echoed back in the `X-Correlation-Id` response header, and is included as `traceId` in every `ErrorResponseDTO` — so a reported error can be traced back through the logs. Incoming values are sanitized against a whitelist (log injection).

**No Lombok in domain layer.**

## Technology Stack

| Component | Version |
|-----------|---------|
| Java | 21 (Virtual Threads active automatically) |
| Spring Boot | 4.0.5 |
| Gradle | 9.0.0 |
| PostgreSQL | 18 (7 schemas, separate DataSource per context) |
| RabbitMQ | 4.2.5 |
| Redis | 7 (Lettuce) |
| Keycloak | 26.6 (OAuth2/OIDC Resource Server) |
| Flyway | 12.4.0 |
| JUnit | 6.0.3 (Jupiter) |
| Spring Modulith | 2.0.0 |
| Jackson | 3 — `tools.jackson.databind.*` |
| Lombok | 1.18.36 |
| Bucket4j | 8.18.0 (`com.bucket4j:bucket4j_jdk17-core`) |

DataSource autoconfiguration is excluded globally — each context configures its own `DataSource`, `EntityManagerFactory`, and `Flyway` bean. None is `@Primary` except `usuarios`, whose `usuariosTransactionManager` **is** `@Primary`.

Jackson 3 moved `databind` to `tools.jackson.databind.*`; `com.fasterxml.jackson.databind.ObjectMapper` will not resolve. Jackson **annotations** remain at `com.fasterxml.jackson.annotation.*`.

## Security

- JWT validated against Keycloak JWK Set (configured in `seguridad/infrastructure/config/SeguridadConfig`)
- Rate limiting via Bucket4j: per-IP buckets in `ConcurrentHashMap` (100 req/min global dev, 60 prod; 5 login/min)
- `AuditFilter` logs all requests with METHOD, URI, USER, TIME, STATUS (skips Swagger paths)
- CORS default origins: `localhost:3000`, `4200`, `5173` (configurable via `CORS_ALLOWED_ORIGINS`)
- CSRF disabled, sessions stateless

## Testing

- **Unit:** JUnit 6 + Mockito + AssertJ, `@ExtendWith(MockitoExtension.class)`, no Spring context loaded
- **Repository slice:** `@DataJpaTest` with H2 (`org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`). `@SpringBootTest` is not used anywhere in this repo
- **Controller slice:** `@WebMvcTest` (`org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`) + `@Import(GlobalAppExceptionHandler.class)` — without that import every exception surfaces as 500. Mock the `Interactor` (or the `UseCase`, where no interactor exists) with `@MockitoBean` (not `@MockBean`), authenticate with `SecurityMockMvcRequestPostProcessors.jwt().authorities(...)` using the exact client role (not `@WithMockUser`)
- Spring Boot 4 relocated the slice-test packages; the Spring Boot 3 `org.springframework.boot.test.autoconfigure.*` paths do not exist
- Method naming: `debeHacerAlgo_cuandoCondicion()`
- Pattern: Arrange / Act / Assert

## Reference Documentation

- [AGENTS.md](AGENTS.md) — comprehensive project guide and ADR index
- [docs/ARQUITECTURA_Y_ESTRUCTURA.md](docs/ARQUITECTURA_Y_ESTRUCTURA.md) — 900-line architecture reference
- [docs/EJECUCION_LOCAL.md](docs/EJECUCION_LOCAL.md) — full local setup guide
- [CONTRIBUTING.md](CONTRIBUTING.md) — git workflow and branch naming
