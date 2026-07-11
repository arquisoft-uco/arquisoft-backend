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
    │   ├── {Action}{Entity}UseCase.java       # Use case implementation
    │   ├── port/in/
    │   │   └── {Action}{Entity}InputPort.java
    │   └── model/
    │       └── {Action}{Entity}Command.java
    └── query/
        ├── {Consult}{Entity}UseCase.java
        ├── port/in/
        │   └── {Consult}{Entity}InputPort.java
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
    │   │   ├── {Action}{Entity}InputAdapter.java
    │   │   └── dto/
    │   │       └── {Action}{Entity}RequestDTO.java
    │   └── adapter/out/persistence/
    │       └── {Feature}CommandOutputAdapter.java
    ├── query/
    │   ├── adapter/in/web/
    │   │   └── {Consult}{Entity}InputAdapter.java
    │   └── adapter/out/persistence/
    │       └── {Feature}QueryOutputAdapter.java
    └── persistence/
        ├── {Feature}JpaEntity.java
        ├── {Feature}JpaRepository.java
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

**Transactional (command):** Always use `@Transactional(transactionManager = "{context}TransactionManager")` with explicit qualifier in command use cases that publish events — required for outbox atomicity. Example: `@Transactional(transactionManager = "seguridadTransactionManager")`.

**Transactional (query):** Query use cases annotate the class with `@Transactional(readOnly = true, transactionManager = "{context}TransactionManager")`. The qualifier is mandatory here too: `usuariosTransactionManager` is the `@Primary` bean, so a bare `@Transactional` does not fail at startup — it silently binds to the `usuarios` transaction manager.

**Input ports:** Interfaces in `application/{feature}/command/port/in/` or `application/{feature}/query/port/in/`, suffix `InputPort` (e.g., `RegistrarFichaPerfilInputPort`, `ConsultarFichasPerfilInputPort`).

**Output ports:** Write-side in `domain/{feature}/port/out/`; read-side in `application/{feature}/query/port/out/`, suffix `OutputPort` (e.g., `FichaPerfilOutputPort`, `FichaPerfilQueryOutputPort`).

**Input adapters:** REST controllers in `infrastructure/{feature}/command/adapter/in/web/`; AMQP consumers in `infrastructure/{feature}/command/adapter/in/amqp/`, suffix `InputAdapter` (e.g., `RegistrarFichaPerfilInputAdapter`, `UsuarioCreadoInputAdapter`).

**Output adapters:** JPA repositories, Redis, Keycloak, MinIO integrations in `infrastructure/{feature}/command/adapter/out/persistence/` (or appropriate sub-package for non-JPA), suffix `OutputAdapter` (e.g., `FichaPerfilCommandOutputAdapter`, `KeycloakAuthOutputAdapter`). Implement the corresponding `OutputPort` interface.

**Use case implementations:** `{Action}{Entity}UseCase` (e.g., `RegistrarFichaPerfilUseCase`, `AutenticarUsuarioUseCase`), implement the corresponding `InputPort`. Annotated `@Component` — **never `@Service`**, which is not used anywhere in this project.

**Commands:** Input data for use cases in `application/{feature}/command/model/`, suffix `Command` (e.g., `RegistrarFichaPerfilCommand`). Implemented as Java `record`. DTOs in `infrastructure/{feature}/command/adapter/in/web/dto/` own a `toCommand()` factory method.

**ReadModels:** Flat query projections in `application/{feature}/query/readmodel/`, suffix `ReadModel` (e.g., `FichaPerfilReadModel`). Implemented as Java `record`. The read side projects straight from the JPA entity via the mapper — there is no `fromDomain(Aggregate)` factory.

**DTOs:** `RequestDTO` is a Java `record` with Jakarta annotations (`@NotBlank`, `@NotNull`) and a `toCommand()` method, living in `infrastructure/{feature}/command/adapter/in/web/dto/`. Generic technical DTOs (`ErrorResponseDTO`, `PageResponseDTO<T>`) come from `shared:web` — never redefine them per context.

**Exceptions:** Every context exception extends one of five bases from `com.arquisoft.shared.exception`: `DomainException` (422), `ApplicationException` (400), `AuthorizationException` (403), `InfrastructureException` (503), `DomainValidationException` (422 + `fieldErrors[]`). Never `RuntimeException` directly. The constructor signature is `super(message, errorCode)` — both are `String`, so swapping them compiles and silently swaps the two fields in the response. `GlobalAppExceptionHandler` (`shared:web`) resolves the HTTP status by walking the superclass chain; contexts do not define their own handler (only `seguridad` does, for a name clash with Spring Security).

**Business rules:** Validated inside the aggregate (→ 422), never with `if/throw` in the use case. The use case reads the state a rule needs via a port and passes it as a parameter to the factory. Existence, DB-duplicate and resource-ownership checks do belong in the use case (→ 400 / 403).

**Naming:** Spanish for business concepts (`crearFicha`, `FichaException`), English for technical suffixes (`Aggregate`, `InputPort`, `OutputPort`, `InputAdapter`, `OutputAdapter`, `UseCase`, `ReadModel`, `DTO`, `Command`).

**Injection:** Always constructor injection via `@RequiredArgsConstructor` — never `@Autowired`.

**Logging:** `@Slf4j`. `warn` for 4xx, `error` for 5xx. Structured JSON via `shared:logger` (includes `traceId`, `userId`).

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

- JWT validated against Keycloak JWK Set (configured in `seguridad/infrastructure/config/SecurityConfig`)
- Rate limiting via Bucket4j: per-IP buckets in `ConcurrentHashMap` (100 req/min global dev, 60 prod; 5 login/min)
- `AuditFilter` logs all requests with METHOD, URI, USER, TIME, STATUS (skips Swagger paths)
- CORS default origins: `localhost:3000`, `4200`, `5173` (configurable via `CORS_ALLOWED_ORIGINS`)
- CSRF disabled, sessions stateless

## Testing

- **Unit:** JUnit 6 + Mockito + AssertJ, `@ExtendWith(MockitoExtension.class)`, no Spring context loaded
- **Repository slice:** `@DataJpaTest` with H2 (`org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`). `@SpringBootTest` is not used anywhere in this repo
- **Controller slice:** `@WebMvcTest` (`org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`) + `@Import(GlobalAppExceptionHandler.class)` — without that import every exception surfaces as 500. Mock the `InputPort` with `@MockitoBean` (not `@MockBean`), authenticate with `SecurityMockMvcRequestPostProcessors.jwt().authorities(...)` using the exact client role (not `@WithMockUser`)
- Spring Boot 4 relocated the slice-test packages; the Spring Boot 3 `org.springframework.boot.test.autoconfigure.*` paths do not exist
- Method naming: `debeHacerAlgo_cuandoCondicion()`
- Pattern: Arrange / Act / Assert

## Reference Documentation

- [AGENTS.md](AGENTS.md) — comprehensive project guide and ADR index
- [docs/ARQUITECTURA_Y_ESTRUCTURA.md](docs/ARQUITECTURA_Y_ESTRUCTURA.md) — 900-line architecture reference
- [docs/EJECUCION_LOCAL.md](docs/EJECUCION_LOCAL.md) — full local setup guide
- [CONTRIBUTING.md](CONTRIBUTING.md) — git workflow and branch naming
