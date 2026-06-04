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

Hexagonal Architecture (Ports & Adapters) with **7 bounded contexts** and **7 shared modules**. Contexts communicate exclusively via RabbitMQ domain events — they never import each other.

### Bounded Contexts

| Context | DB Schema |
|---------|-----------|
| `seguridad` | `usuarios` |
| `fichas` | `fichas_perfil` |
| `proyectos` | `proyectos_grado` |
| `artefactos` | `artefactos` |
| `repositorio_artefactos` | `repositorio_artefactos` |
| `entregables` | `entregables` |
| `evaluaciones` | `evaluaciones` |

### Shared Modules

`shared:domain`, `shared:amqp`, `shared:logger`, `shared:redis`, `shared:web`, `shared:validation`, `shared:postgres`

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

**Aggregate roots:** Immutable, final fields, no public constructor, suffix `Aggregate` (e.g., `UsuarioAggregate`, `FichaPerfilAggregate`). Use `build()` for new instances, `rebuild()` for reconstructing from DB.

**IDs:** Always UUID — never `Long` or `Integer`.

**Domain events:** Extend `DomainEvent`. After persisting an aggregate, drain its unpublished events and publish via `SharedEventPublisher` (RabbitMQ, publisher confirms, manual ACK, prefetch=1).

**Input ports:** Interfaces in `application/{feature}/command/port/in/` or `application/{feature}/query/port/in/`, suffix `InputPort` (e.g., `RegistrarFichaPerfilInputPort`, `ConsultarFichasPerfilInputPort`).

**Output ports:** Write-side in `domain/{feature}/port/out/`; read-side in `application/{feature}/query/port/out/`, suffix `OutputPort` (e.g., `FichaPerfilOutputPort`, `FichaPerfilQueryOutputPort`).

**Input adapters:** REST controllers in `infrastructure/{feature}/command/adapter/in/web/`; AMQP consumers in `infrastructure/{feature}/command/adapter/in/amqp/`, suffix `InputAdapter` (e.g., `RegistrarFichaPerfilInputAdapter`, `UsuarioCreadoInputAdapter`).

**Output adapters:** JPA repositories, Redis, Keycloak, MinIO integrations in `infrastructure/{feature}/command/adapter/out/persistence/` (or appropriate sub-package for non-JPA), suffix `OutputAdapter` (e.g., `FichaPerfilCommandOutputAdapter`, `KeycloakAuthOutputAdapter`). Implement the corresponding `OutputPort` interface.

**Use case implementations:** `{Action}{Entity}UseCase` (e.g., `RegistrarFichaPerfilUseCase`, `AutenticarUsuarioUseCase`), implement the corresponding `InputPort`.

**Commands:** Input data for use cases in `application/{feature}/command/model/`, suffix `Command` (e.g., `RegistrarFichaPerfilCommand`). Implemented as Java `record`. DTOs in `infrastructure/{feature}/command/adapter/in/web/dto/` own a `toCommand()` factory method.

**ReadModels:** Flat query projections in `application/{feature}/query/readmodel/`, suffix `ReadModel` (e.g., `FichaPerfilReadModel`). Own a static `fromDomain(Aggregate)` factory method.

**DTOs:** `@Data @NoArgsConstructor @AllArgsConstructor @Builder`, suffix `DTO`. Own `toDomain()` and `fromDomain()` static methods.

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
| JUnit | 6 (Jupiter) |

DataSource autoconfiguration is excluded globally — each context configures its own `DataSource`, `EntityManagerFactory`, and `Flyway` bean.

## Security

- JWT validated against Keycloak JWK Set (configured in `seguridad/infrastructure/config/SecurityConfig`)
- Rate limiting via Bucket4j: per-IP buckets in `ConcurrentHashMap` (100 req/min global dev, 60 prod; 5 login/min)
- `AuditFilter` logs all requests with METHOD, URI, USER, TIME, STATUS (skips Swagger paths)
- CORS default origins: `localhost:3000`, `4200`, `5173` (configurable via `CORS_ALLOWED_ORIGINS`)
- CSRF disabled, sessions stateless

## Testing

- **Unit:** JUnit 6 + Mockito + AssertJ, `@ExtendWith(MockitoExtension.class)`, no Spring context loaded
- **Integration:** `@SpringBootTest` with H2 for repositories
- Method naming: `debeHacerAlgo_cuandoCondicion()`
- Pattern: Arrange / Act / Assert

## Reference Documentation

- [AGENTS.md](AGENTS.md) — comprehensive project guide and ADR index
- [docs/ARQUITECTURA_Y_ESTRUCTURA.md](docs/ARQUITECTURA_Y_ESTRUCTURA.md) — 900-line architecture reference
- [docs/EJECUCION_LOCAL.md](docs/EJECUCION_LOCAL.md) — full local setup guide
- [CONTRIBUTING.md](CONTRIBUTING.md) — git workflow and branch naming
