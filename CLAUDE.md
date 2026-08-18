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

Hexagonal Architecture (Ports & Adapters) with **9 bounded contexts** and **14 shared modules**. Contexts communicate exclusively via RabbitMQ domain events — they never import each other.

### Bounded Contexts

Only `seguridad`, `usuarios`, `fichas` and `notificaciones` have real implementation today; `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables` and `evaluaciones` are scaffolding — each has only a `{Contexto}DataSourceConfig` in `infrastructure/config/`, no domain/application code yet.

| Context | DB Schema |
|---------|-----------|
| `seguridad` | *(sin DB — auth vía Keycloak + Redis)* |
| `usuarios` | `usuarios` |
| `fichas` | `fichas_perfil` |
| `notificaciones` | `notificaciones` |
| `proyectos` | `proyectos_grado` |
| `artefactos` | `artefactos` |
| `repositorio_artefactos` | `repositorio_artefactos` |
| `entregables` | `entregables` |
| `evaluaciones` | `evaluaciones` |

### Shared Modules

`shared:util`, `shared:exception`, `shared:validation`, `shared:domain`, `shared:query`, `shared:logger`, `shared:tracing`, `shared:redis`, `shared:amqp`, `shared:web`, `shared:minio`, `shared:jpa`, `shared:message`, `shared:notification`

`shared:query` owns the entire read-side vocabulary, in one package root (`com.arquisoft.shared.query`): `QueryCriteria`/`SortOrder`/`NodoFiltro`/`FiltroOperador`/`FiltroConector` at the root, `pagination/` (`PaginatedResult`, `PaginationRequest`, `SortDirection`), `dto/` (the `QueryCriteriaRequestDTO` + `NodoFiltroDTO` hierarchy the controllers bind), and `exception/`. It has **no Spring dependency at all** — only `shared:exception`/`shared:message` (`api`), `shared:util`, and Jackson *annotations* (never `databind`, never the web starter), so it stays usable from any layer. The split from `shared:jpa` is deliberate: everything in `shared:jpa` is irreducibly bound to Spring Data JPA (`Specification`, `Root`, `Pageable`, `Repository`), and merging the two would force that dependency on every module that only needs to declare a criteria.

**Both query exceptions are `ApplicationException` (400), never `DomainException`.** `FiltroException` (bad sort/filter field, unknown operator or connector, missing value) and `FiltroInvalidoException` (the value does not parse as the column's type) both live in `shared:query/exception/`. A malformed query is a bad *request*, not a violated business invariant — 422 would tell the client their payload was semantically understood when it was not.

`shared:validation` is its own module (not a package inside `shared:domain`) — the `Validator*` family plus `ValidationResult` live there, in the `com.arquisoft.shared.validation` package, together with `DomainValidationException`/`ApplicationValidationException`; it depends on `shared:exception` (`api`) for the exception types it extends, and on `shared:message`/`shared:util` (`implementation`) internally. `shared:exception` holds the five base exception classes plus `BaseException`/`BaseError` and has zero dependencies of its own — it is deliberately a leaf module so `shared:message` (which extends `InfrastructureException`) can depend on it without creating a cycle back through `shared:domain`. `shared:util` holds the stateless helpers referenced throughout this document (`UtilTexto`, `UtilUUID`, `UtilColeccion`, `UtilFecha`, `UtilNumero`, `UtilObjeto`) — the `Util` prefix stays in English, everything after it is Spanish (`UtilUUID` is the exception: `UUID` is a technical acronym, not a translatable word). Method names inside them are Spanish too (e.g. `UtilTexto.aplicarTrim`, `UtilUUID.generarUUIDDesdeTexto`). `shared:notification` defines `EnvioNotificacionOutputPort` and the SMTP adapter consumed by the `notificaciones` context. `shared:tracing` owns the trace context (MDC keys, id generation, W3C `traceparent`, propagation headers) and is the only shared module with internal hexagonal layers — see *Correlation* below; it depends on `slf4j-api`, `shared:util` and `spring-context`, deliberately **not** on spring-web or spring-security, so `shared:amqp` can consume it without pulling in the web stack.

The validators are split by the **type they validate**, mirroring `shared:util`: `ValidatorObjeto` (`noNulo`), `ValidatorTexto` (`noEnBlanco`, `correoValido`), `ValidatorLongitud` (`longitudMaxima`, `longitudMinima`, `longitudEntre`), `ValidatorNumero` (`valorMinimo`, `valorMaximo`, `valorEntre`), `ValidatorUUID` (`uuidValido`), `ValidatorColeccion` (`noVacia`, `tamanioMaximo`, `sinDuplicados`). All are `final`, private constructor, static methods with the uniform signature `(valor, …, campo, codigoError, ValidationResult) → boolean`. The `Validator` prefix stays English like `Util`, everything after it is Spanish. There is no `DomainValidator` any more: the name implied the helpers were domain-only, when a `Command.crear(...)` in the application layer calls exactly the same ones — dropping the prefix is what makes that shared use honest. The range methods (`longitudEntre`, `valorEntre`) accumulate **one** error naming both bounds, not two; the single-bound methods remain for the cases that only have one. Note these are the low-level *helpers*, not to be confused with `{Action}{Entity}Validator` in `application/{feature}/command/validator/`, which orchestrates domain `Rule`s.

### Layer Structure per Context

```
{context}/domain/
└── {feature}/
    ├── {Entity}Domain.java   # Aggregate root (suffix Domain, noun) — no Lombok, no Spring; lives directly here, no subpackage
    ├── model/                # Value objects — no Lombok, no Spring. Includes the input record of each Rule
    ├── rules/                # Business rule interfaces (suffix Rule), reusable across use cases
    │   └── impl/
    │       └── {Regla}RuleImpl.java   # Pure: no ports, no constructor dependencies
    └── message/              # Domain message constants (optional)
event/                        # Domain events shared across features (extend DomainEvent)
exception/                    # Domain exceptions shared across features (extend DomainException)

{context}/application/
└── {feature}/
    ├── command/
    │   ├── primaryport/                                  # Primary contract of the command: interactor + Command + mapper
    │   │   ├── interactor/
    │   │   │   ├── {Action}{Entity}Interactor.java        # Entry-point interface
    │   │   │   └── impl/
    │   │   │       └── {Action}{Entity}InteractorImpl.java  # Owns @Transactional
    │   │   ├── model/
    │   │   │   └── {Action}{Entity}Command.java   # Command input
    │   │   └── mapper/                            # Optional — Command → domain object mapping when it's not the root
    │   │       └── {Action}{Entity}Mapper.java     # toDomain(command)
    │   ├── usecase/                                # NOT nested under primaryport — internal collaborator, not the primary contract
    │   │   ├── {Action}{Entity}UseCase.java
    │   │   └── impl/
    │   │       └── {Action}{Entity}UseCaseImpl.java   # Business orchestration (no transaction)
    │   ├── validator/
    │   │   └── {Action}{Entity}Validator.java  # Pure: receives the already-fetched data and orchestrates the Rules
    │   ├── finder/                             # One Finder per query, implementing shared.rules.Finder<T,R>
    │   │   ├── {Concepto}Finder.java
    │   │   └── impl/
    │   ├── secondaryport/                      # Output port write-side (suffix OutputPort)
    │   │   ├── {Feature}OutputPort.java
    │   │   ├── entity/                          # Plain record — the port's persistence shape, no JPA/Lombok
    │   │   │   └── {Feature}Entity.java
    │   │   └── mapper/                          # Domain ↔ Entity
    │   │       └── {Feature}Mapper.java
    │   └── result/
    │       └── {Concepto}Result.java          # Command output, only when it is not UUID/void
    └── query/
        ├── primaryport/
        │   └── usecase/                        # Primary contract of the query: there is no interactor on this side
        │       ├── {Consult}{Entity}UseCase.java
        │       └── impl/
        │           └── {Consult}{Entity}UseCaseImpl.java
        ├── secondaryport/
        │   └── {Feature}QueryOutputPort.java  # Output port read-side
        ├── criteria/
        │   └── {Feature}Criteria.java
        └── readmodel/
            └── {Feature}ReadModel.java

{context}/infrastructure/
└── {feature}/
    ├── command/
    │   ├── primaryadapter/
    │   │   ├── web/
    │   │   │   ├── {Action}{Entity}Controller.java
    │   │   │   ├── dto/
    │   │   │   │   └── {Action}{Entity}RequestDTO.java
    │   │   │   └── mapper/                     # Optional — see "DTOs" below for when this exists
    │   │   │       └── {Action}{Entity}RequestMapper.java
    │   │   └── amqp/                            # Optional — AMQP consumers (input adapters)
    │   │       └── {Evento}Consumer.java
    │   └── secondaryadapter/
    │       ├── entity/                            # JPA @Entity — the real persistence shape, Lombok
    │       │   └── {Feature}JpaEntity.java
    │       ├── mapper/                            # Entity ↔ JpaEntity (+ toReferencia(id) if this
    │       │   └── {Feature}JpaMapper.java         #   feature is a @ManyToOne target of another one)
    │       └── repository/                       # Or another sub-package for non-JPA integrations (keycloak/, redis/, jwt/, ...)
    │           ├── {Feature}CommandOutputAdapter.java
    │           └── {Feature}CommandRepository.java   # Spring Data — only the write-side methods
    └── query/
        ├── primaryadapter/
        │   └── web/
        │       └── {Consult}{Entity}Controller.java
        └── secondaryadapter/
            └── repository/
                ├── {Feature}QueryOutputAdapter.java
                └── {Feature}QueryRepository.java    # Spring Data — only the read-side methods
config/              # Spring configuration shared within context
filter/              # HTTP filters (if applicable to context)
db/migration/        # Flyway migrations
```

Dependency direction is strictly enforced: `domain ← application ← infrastructure`.

## Key Conventions

**Aggregate roots:** No public constructor, private non-`final` fields assigned by private setters (Notification Pattern — a `final` field cannot be assigned from a method), only public getters, extend `AggregateRoot` (`shared:domain`), suffix `Domain` — a noun, not the infinitive verb of the action that creates it (e.g., `UsuarioDomain`, `FichaPerfilDomain`, not `RegistrarFichaPerfilDomain`). The class lives directly under `domain/{feature}/`, with no `aggregate/` or `model/` subpackage of its own. Use `crear(...)` for new instances, `reconstruir(...)` for reconstructing from DB — **not** `build()`/`rebuild()`. Entity package segments are all-lowercase with no separators (`domain/fichaperfil/`, not `domain/fichaPerfil/`).

**IDs:** Always UUID — never `Long` or `Integer`.

**Domain events:** Extend `DomainEvent`. After persisting an aggregate, drain its unpublished events and publish via `EventPublisher` (RabbitMQ, publisher confirms, manual ACK, prefetch=1). An event carries everything its consumers need to act, even when that duplicates data the producer already owns — `AsesorFichaCambiadoEvent` ships `asesorNombre` and `asesorEmail` so `notificaciones` never calls back into `fichas` to find out whom to notify. A thin event that forces the consumer to query the producer reintroduces the coupling the events exist to remove.

**Interactor (command side):** The interactor — not the use case — is the entry point and owns the transaction: `{Action}{Entity}InteractorImpl` implements `{Action}{Entity}Interactor`, annotates `ejecutar` with `@Transactional(transactionManager = "{context}TransactionManager")` and delegates to the use case. Rationale: an operation may lean on several use cases, so the unit of work belongs one layer above; it also guarantees the right transaction manager is active when domain events reach the outbox (`ContextAwareEventPublicationRepository`). Applied in `fichas` and `usuarios`. `seguridad` also has the interactor layer, but its interactors declare no `@Transactional`: the context has no `DataSource` of its own (Keycloak + Redis), so there is no unit of work to delimit. The use case must **not** implement the `Interactor` interface — two beans for the same port make injection ambiguous.

**Transactional (command):** the qualifier is always explicit — `@Transactional(transactionManager = "{context}TransactionManager")` — and lives on the interactor. It is required for outbox atomicity when the operation publishes events. Example: `@Transactional(transactionManager = "usuariosTransactionManager")`. `seguridad` is the exception: no `DataSource`, so no annotation.

**Transactional (query):** Query use cases annotate the class with `@Transactional(readOnly = true, transactionManager = "{context}TransactionManager")`. The qualifier is mandatory here too: `usuariosTransactionManager` is the `@Primary` bean, so a bare `@Transactional` does not fail at startup — it silently binds to the `usuarios` transaction manager.

**Input ports:** The entry point of a command is the `Interactor` interface in `application/{feature}/command/primaryport/interactor/`, implemented by `{Action}{Entity}InteractorImpl` in the nested `impl/` package — together with its `Command` (`primaryport/model/`) and, where the action maps to a non-root domain object, its `Mapper` (`primaryport/mapper/`), this is the "primary port" of the command. The use case it delegates to is the `UseCase` interface in `application/{feature}/command/usecase/` — **not** nested under `primaryport/`, since it is an internal collaborator, not the primary contract — or in `application/{feature}/query/primaryport/usecase/` on the read side, where the `UseCase` **is** the primary contract because there is no interactor. Both are implemented by `{Action}{Entity}UseCaseImpl`/`{Consult}{Entity}UseCaseImpl` in the nested `impl/` package. There is no `port/in/` package on the application layer — interfaces and implementations live together under `interactor/` and `usecase/`.

**Output ports:** Both sides live in the application layer — write-side in `application/{feature}/command/secondaryport/`, read-side in `application/{feature}/query/secondaryport/`, suffix `OutputPort` (e.g., `FichaPerfilOutputPort`, `FichaPerfilQueryOutputPort`). **The domain layer declares no ports and performs no I/O**; `{context}/domain` depends only on `shared:domain`, so a port under `domain/` would invert the module dependency and not compile. **Output ports speak `Entity`, never `Domain`** — `registrarFicha(FichaPerfilEntity)`, `buscarPorId(...) → Optional<FichaPerfilEntity>` — because infrastructure must not see the domain layer at all.

**Entities and mappers:** `{Feature}Entity` (`application/{feature}/command/secondaryport/entity/`) is a **plain, technology-agnostic `record`** — no JPA, no Lombok, no annotations of any kind. It carries only the fields the port needs, and a `@ManyToOne`-shaped relationship to another feature's entity is stored as the bare id of that entity (`UUID` or `String`, matching the target's `@Id` type — never a nested `{Other}Entity`), the same way most entities already store plain foreign keys. Its mapper, `{Feature}Mapper` (sibling `mapper/` package, `final`, private constructor, **static** methods `toDomain`/`toEntity` — no Spring, no injection), converts `Entity ↔ Domain`; with the record flattened, this mapper never builds a nested reference itself. The real JPA entity lives in infrastructure: `{Feature}JpaEntity` in `infrastructure/{feature}/command/secondaryadapter/entity/` (suffix `JpaEntity`, the same `@Entity`/`@Table`/`@Column`/Lombok `@Getter/@Builder/@NoArgsConstructor/@AllArgsConstructor` the old application-layer entity used to carry), with a `{Feature}JpaMapper` (sibling `mapper/` package, `final`, private constructor, static `toEntity`/`toJpaEntity`) converting `Entity ↔ JpaEntity` — this is the only place either module needs a JPA/Hibernate dependency, and `application` no longer declares `jakarta.persistence-api` at all. If a `@ManyToOne` on the `JpaEntity` points at another feature's `JpaEntity`, that other feature's `{Other}JpaMapper` exposes the id-only detached-reference helper (`toReferencia(id)`, see "Where the conversion happens" below) — only features that are actually a FK *target* need one. Both the `Entity` and its `JpaEntity` sit under `command/` even when the read side uses them: an entity is not command- or query-specific, and the query side imports what it needs from there rather than duplicating it. **`toReadModel` does not live here** — a `ReadModel` is a query artifact and the read side has its own entity (below), so the mapping lives in infrastructure next to that entity. The command side never imports anything under `query/`. Applied throughout `fichas` and `notificaciones`.

**Read-side entities (`{Feature}JpaQueryEntity`):** a feature that projects a `ReadModel` outward does **not** read through the command-side `Entity`. It declares its own JPA entity in `infrastructure/{feature}/query/secondaryadapter/repository/`, suffix `JpaQueryEntity` — the `Jpa` prefix marks it as a technical artifact of the adapter, not an application type. It is mapped with Hibernate's `@Subselect` (the read query inline) plus `@Immutable` and `@Synchronize({"tabla", ...})`, which lists the tables it derives from so Hibernate flushes pending writes before querying. `@Subselect` needs `hibernate-core`, which only the infrastructure module has — this is why the entity cannot live in `application`. The subselect resolves the joins, so the entity is **flat — no `@ManyToOne`, no `@EntityGraph`, no lazy loading** — and `Specification`/sort paths address the flat columns (`asesorNombre`, not `asesorFicha.nombre`). Its mapper, `{Feature}QueryMapper` (`final`, private constructor, static `toReadModel`), sits in the sibling `mapper/` package; the query output adapter delegates to it and never declares a private `toReadModel`. Because the SQL travels in the Java, `@DataJpaTest` exercises the real join against H2: seed the underlying command tables with `TestEntityManager` and read through the port. Both the `EntityManagerFactory` (`{Contexto}DataSourceConfig`) and the `@DataJpaTest` anchor's `@EntityScan` must therefore scan `com.arquisoft.{contexto}.infrastructure` in addition to `.application`. **CQRS isolation is absolute at the infrastructure/JPA level: `query`'s `secondaryadapter` never imports anything from `command`'s `secondaryadapter`, not even the command side's `{Feature}JpaEntity`.** A feature whose query-side need is genuinely a `boolean`/`count`/lookup **and nothing else** gets its own minimal `{Feature}JpaQueryEntity` — a `@Subselect`/`@Immutable`/`@Synchronize` entity declaring only the columns that query needs (often just `id`), never a `@ManyToOne`, never reusing the command side's `JpaEntity` as its generic type: that would bind the read path to the write path's Hibernate mapping (its `@ManyToOne`s, its cascade config), exactly what CQRS separation exists to prevent. If the query-side method returns entity data rather than a `boolean`/`long`, a `{Feature}QueryMapper` (sibling `mapper/` package, `final`, private constructor, static `toEntity`/`toReadModel`) converts the `JpaQueryEntity` to whatever the application-layer `{Feature}QueryOutputPort` declares.

**But a `query/` package only exists for a feature that has a genuine read use case reached through a `primaryport` — a `UseCase`/`Controller` driven by a `Criteria`, a listing, a lookup a client actually calls.** An existence or lookup check that a *command-side* `Validator`/`Rule` needs (`¿existe este estudiante?`, `¿es este representante el dueño de la evaluación?`, `¿cuál es el estado actual de la ficha?`) is a command-side concern even when it only reads: it belongs on that feature's own command `{Feature}OutputPort`, consumed by a command `Finder` — never duplicated under `query` "just in case," and never wired to nothing. Before adding a `{Feature}QueryOutputPort`, confirm something under `primaryport` actually calls it; if the only candidate caller is a command `Finder`, the check belongs in `command/secondaryport`, full stop. `estudiante`, `representantecomite`, `evaluacionfichaperfil`, and `estadofichaperfil` have no `query/` package at all today for exactly this reason — their only "reads" are existence/state checks a command `Finder` already gets from the command `OutputPort`.

**Where the conversion happens:** the **use case** maps `Domain → Entity` before calling the port, and the **finder** maps `Entity → Domain` after it returns — both via the application-layer `{Feature}Mapper`, working on the plain record. The **output adapter** is where `Entity ↔ JpaEntity` happens (via `{Feature}JpaMapper`), immediately around each repository call — `registrarFicha(FichaPerfilEntity)` becomes `repository.save(FichaPerfilJpaMapper.toJpaEntity(ficha))`, `buscarPorId(id)` becomes `repository.findById(id).map(FichaPerfilJpaMapper::toEntity)`. A `JpaMapper` needing a `@ManyToOne` association builds it as an id-only detached instance via the target feature's own `{Other}JpaMapper.toReferencia(id)` (e.g. `AsesorFichaJpaMapper.toReferencia(asesorFicha)`); with no cascade configured, Hibernate writes just the foreign key, which is what replaces the old `getReferenceById` proxy the adapter used to resolve. A port method that only ever needed a FK id (e.g. `actualizarAsesor(UUID fichaPerfil, AsesorFichaEntity nuevoAsesorFicha)`, where the entity carried nothing but an id) is simplified to take that id directly (`actualizarAsesor(UUID fichaPerfil, UUID nuevoAsesorFicha)`); the adapter builds the `toReferencia(...)` reference internally before calling the repository. Enums are the application-layer mapper's job too — see "Catalog enums" below: `EstadoFicha.desde(entity.estadoFicha())`, never a bare `valueOf`.

**Catalog enums:** a domain enum validates its own text form, the same way an aggregate validates its own invariants — **`valueOf` is never called directly outside the enum**. Each catalog enum exposes `desde(String)`, which returns the constant or throws its own `{Enum}NoEncontradoException` (`DomainException` → 422) in `domain/{catalogo}/exception/`; a blank, null, or unrecognized id is a domain error, never a raw `IllegalArgumentException` that `GlobalAppExceptionHandler` would surface as a 500. Enums whose value also arrives through an aggregate's `crear(...)` additionally expose `esValido(String)`, so the aggregate can accumulate the error into its `ValidationResult` (Notification Pattern → `fieldErrors[]`) instead of aborting on the first one — the aggregate calls `esValido(...)` to decide, then `desde(...)` to convert. Both delegate to `UtilEnum.desde(Class, String)` (`shared:util`), which owns the "blank or unknown → absent" policy and the trimming in exactly one place. Every catalog enum also exposes `getId()` returning `name()` — mappers persist `getId()`, **never a bare `.name()`**. Applied to `EstadoFicha`, `EstadoEvaluacion`, `TipoItem`, `EstadoNotificacion`, `TipoNotificacion` and `UsuarioRole`. `UsuarioRole` is the one variant: its external identifier is the Keycloak role (`asesor-ficha`), not `name()`, so it keeps `getCodigo()` and its lookup pair is `desdeCodigo(String)`/`esCodigoValido(String)` matching case-insensitively on that field instead of delegating to `UtilEnum`.

**No `Optional` in domain records or validator signatures:** `Optional` is a *finder* return type and nothing else. It is unwrapped in the use case and never reaches a `Validator` parameter nor a domain `Rule`'s input record. Two shapes carry absence past that boundary, and which one applies depends on what was fetched:
- **An aggregate** travels as its `VACIO` sentinel: the finder returns `Optional<{Entity}Domain>`, the use case does `.orElse({Entity}Domain.VACIO)`, and the validator takes the plain aggregate and asks `esVacio()`. Every such aggregate declares `public static final X VACIO` populated with the zero value of each field (`UtilUUID.obtenerUUIDPorDefecto()`, `UtilTexto.VACIO`, `UtilFecha.VACIO`, `EstadoFicha.VACIO`) so its getters stay readable, plus `esVacio()` returning `this == VACIO` — identity, not field comparison.
- **A bare value** (a `UUID`, a count) travels as the value plus an explicit `boolean`: `validar(UUID item, UUID estudiante, UUID fichaDelItem, boolean itemExiste, …)`, with the use case filling the absent case with the zero value (`.orElse(UtilUUID.obtenerUUIDPorDefecto())`). This is the dominant validator shape — `boolean asesorExiste`, `boolean fichaExiste`, `boolean esPropietario`.

Either way the rule that consumes existence gets its own `Existencia{Concepto}(… , boolean existe)` record, and the value rules run only after that boolean says the data is there.

**Enums in ports and finders:** once the mapper has validated the text against the catalog, the enum stays an enum — do not convert it back to `String` to carry it across the application layer. Absence is modeled the way every other rule models it: as a `boolean` in its own existence record (`ExistenciaEstadoFichaPerfil(UUID fichaPerfil, boolean existe)` → `EstadoFichaPerfilExisteRule`), while the value rule takes the plain enum (`EstadoActualFicha(UUID fichaPerfil, EstadoFicha estadoActual)`) and the validator guards it behind `esVacio()`. Note the two distinct errors this keeps separate: `EstadoFichaPerfilNoEncontradoException` (no row for that ficha — it can name the ficha) versus the catalog-level `EstadoFichaNoEncontradoException` (the stored id is not a known constant).

**Repositories:** Spring Data interfaces stay in infrastructure and are **split by side** — `{Feature}CommandRepository` in `command/secondaryadapter/repository/` and `{Feature}QueryRepository` in `query/secondaryadapter/repository/`, each declaring only the methods its side uses. The write side extends `JpaRepository`; the read side extends `QueryRepository<T, ID>` (or `SpecificationQueryRepository<T, ID>` when it needs `Specification`) from `shared:jpa` — both `@NoRepositoryBean` interfaces over Spring Data's `Repository`, exposing only `findById`/`existsById`/`findAll`/`count`. A query repository must never inherit `save`/`delete`/`flush`: the compiler, not the convention, keeps CQRS honest. Tests that need to seed data in a `@DataJpaTest` use `TestEntityManager`, never a query repository. There is no `infrastructure/{feature}/persistence/` package any more.

**Query output adapters are pure delegation.** The translation work sits in `shared:jpa/util/`, in two symmetric mappers: `PageableMapper.toPageable(criteria, traductorDeCampo)` converts `QueryCriteria` → `Pageable` on the way in, `PaginationMapper.toResult(page)` converts `Page` → `PaginatedResult` on the way out. The adapter supplies only the feature-specific piece — a method reference to its `{Feature}SortMapper::traducir` — and never builds a `PageRequest`/`Sort` itself. `shared:jpa` holds only what genuinely needs Spring Data JPA: these two mappers, `CampoSpec`/`QueryJpaSpecification`, and the `QueryRepository`/`SpecificationQueryRepository` interfaces. Everything Spring-free lives in `shared:query`.

**Do not catch Spring Data exceptions in an adapter to re-map them as 4xx.** Invalid sort and filter fields are already rejected up front, before any SQL runs: `QueryCriteria.BaseBuilder` validates `ordenamiento` against `camposOrdenables()` and `raiz` against `camposFiltrables()` (throwing `FiltroException` → 400), and `QueryJpaSpecification` rejects unknown filter fields. So a `PropertyReferenceException` or `InvalidDataAccessApiUsageException` reaching the adapter can only mean the feature's own `SortMapper`/`Specification` points at a column the entity does not have — a mapping defect. Translating that to a 400 tells the client their field was wrong when it was not, and hides the bug; let it surface as a 500. What guards the mapping instead is a test: `{Feature}SortMapperTest` asserts that `Campo.esValidoParaOrdenar(clave)` and `{Feature}SortMapper.traducir(clave) != null` agree for every field, so the two declarations of "what is sortable" cannot drift apart.

**Input adapters:** REST controllers in `infrastructure/{feature}/command/primaryadapter/web/` (or `query/.../web/`), suffix `Controller` (e.g., `RegistrarFichaPerfilController`) — there is no Spanish exception for this suffix anymore. AMQP consumers in `infrastructure/{feature}/command/primaryadapter/amqp/`, suffix `Consumer` (e.g., `UsuarioCreadoConsumer`) — matching the `AbstractEventConsumer` base class they extend; `Controller` is reserved for HTTP entry points.

**Output adapters:** JPA repositories, Redis, Keycloak, MinIO integrations in `infrastructure/{feature}/command/secondaryadapter/repository/` (or an appropriately named sub-package for non-JPA integrations, e.g. `secondaryadapter/keycloak/`, `secondaryadapter/redis/`, `secondaryadapter/jwt/` in `seguridad`), suffix `OutputAdapter` (e.g., `FichaPerfilCommandOutputAdapter`, `KeycloakAuthOutputAdapter`). Implement the corresponding `OutputPort` interface.

**Use case implementations:** `{Action}{Entity}UseCaseImpl` in `usecase/impl/`, implementing `{Action}{Entity}UseCase` (e.g., `RegistrarFichaPerfilUseCaseImpl`, `AutenticarUsuarioUseCaseImpl`). Annotated `@Component` — **never `@Service`**, which is not used anywhere in this project. The use case is a plain collaborator invoked by its interactor — the adapter always injects the `Interactor`, never the `UseCase`.

**Validators:** `{Action}{Entity}Validator` (`application/{feature}/command/validator/`, `@Component`) accumulates every business rule of one use case. It is **pure**: it injects only domain `Rule` beans — never an `OutputPort`, never a `Finder`. Its `validar(...)` signature takes the domain input plus the data the use case already fetched (`boolean asesorExiste`, `List<UUID> estudiantesExistentes`, `Optional<EstadoFicha> estadoActual`, …); from those it builds each Rule's input record and invokes the Rules in validation order. **A validator never decides, it only orchestrates — it contains no `if` at all.** Rules run in sequence and each one throws on its own violation, so a rule that depends on a previous one simply trusts that the previous one already threw: guarding it is dead code, because the existence rule aborts the flow before the dependent rule is reached. When absence must change what a rule concludes, that decision lives **inside the rule** — `EstadoFichaPerfilEnTerminalRule` is invoked unconditionally and returns silently on `EstadoFicha.VACIO`. Tests that need to prove a dependent rule does not run must stub the preceding rule to throw (as it does in production), never rely on a defensive branch in the validator.

**Domain rules:** A single existence/uniqueness/comparison/ownership check is an interface `{Concepto}Rule` extending `com.arquisoft.shared.rules.DomainRule<T>`, in `domain/{feature}/rules/`, with a plain implementation (no Lombok, no Spring, **no constructor dependencies at all**) in the nested `impl/` package. `T` is a record in `domain/{feature}/model/` carrying the already-fetched data plus whatever the error message needs — e.g. `ExistenciaAsesorFicha(UUID asesorFicha, boolean existe)`, `DisponibilidadTituloFicha(String tituloProyecto, boolean yaExiste)`, `PropiedadFicha(UUID fichaPerfil, UUID estudiante, boolean esPropietario)`. `validar(T)` is a pure decision over that record: it does not query anything, it only throws. Rules are wired as no-arg beans from `{Contexto}DomainRulesConfig` in infrastructure. Their unit tests need no Mockito.

**Finders:** every query the rules need is one `{Concepto}Finder` extending `com.arquisoft.shared.rules.Finder<T, R>` in `application/{feature}/command/finder/`, with `@Component` `{Concepto}FinderImpl` in `impl/` that delegates to an `OutputPort`. A Finder **always returns a value and never throws for "not found"**: existence queries return `Boolean`, counts `Long`, aggregate lookups `Optional<T>`. Deciding what an absent value means is the Rule's job, not the Finder's. Failures of the query itself (DB down, timeout) propagate as the adapter's `InfrastructureException` (503) — finders do not catch or re-wrap them.

**Command flow:** `InteractorImpl` (`@Transactional`) → `UseCaseImpl` → finders fetch every piece of state (mapping the `Entity` the port returns back to `Domain`) → `validator.validar(entrada, …datos)` → rules decide → `{Feature}Mapper.toEntity(aggregate)` → `OutputPort` persists. All I/O of a command lives in the use case; the validator and the rules are pure functions of what it fetched. When a query depends on the result of a previous one (item → its ficha → ownership of that ficha), chain it with `map`/`flatMap` on the `Optional` so the absent case degrades to a safe default and the first rule reports the real error.

**Validation order (mandatory):** 1) data integrity (format, required, length, duplicates within the payload — accumulable), 2) existence and uniqueness against the DB, 3) business rules in the aggregate. Never query the database on data whose integrity has not been established first.

**Command results:** a command normally returns `UUID` or `void`, and then it needs no type of its own. When it does return something richer (only `seguridad` today), the record lives in `application/{feature}/command/result/` with suffix `Result` (e.g., `AutenticacionResult`, `RefrescoTokenResult`, `ValidacionTokenResult`) — never nested inside the `UseCase` interface, and never in `model/`, which is reserved for input. This mirrors the read side, where `criteria/` is the input and `readmodel/` the output.

**Commands:** Input data for use cases in `application/{feature}/command/primaryport/model/`, suffix `Command` (e.g., `RegistrarFichaPerfilCommand`). Implemented as Java `record`, with a static `crear(...)` factory that runs data-integrity validation (`Validator*`/`ValidationResult`, accumulable) before returning the typed instance — this is where request-level format validation lives today, not on the DTO. DTOs live in `infrastructure/{feature}/command/primaryadapter/web/dto/`; see "DTOs" below for how they convert to a `Command`.

**ReadModels:** Flat query projections in `application/{feature}/query/readmodel/`, suffix `ReadModel` (e.g., `FichaPerfilReadModel`). Implemented as Java `record`. The read side projects straight from the JPA entity via the mapper — there is no `fromDomain(...)` factory from the aggregate root. A `ReadModel` is **never serialized directly**: it carries no Jackson annotation and no Lombok, so the JSON contract cannot drift into the secondary port's return type. The query controller maps it to a `{Entity}ResponseDTO` (`record`, in `infrastructure/{feature}/query/primaryadapter/web/dto/`) through a `{Entity}ResponseMapper` (`final`, private constructor, static `toResponse`) in the sibling `mapper/` — the read-side counterpart of the command's `{Action}{Entity}RequestMapper`. Serialization policy (`@JsonInclude`, field naming) lives on that DTO. For paginated endpoints, convert before wrapping: `PageResponseDTO.from(resultado.map({Entity}ResponseMapper::toResponse))`.

**DTOs:** `RequestDTO` is a Java `record` living in `infrastructure/{feature}/command/primaryadapter/web/dto/`. Generic technical DTOs (`ErrorResponseDTO`, `PageResponseDTO<T>`) come from `shared:web` — never redefine them per context. The DTO is a pass-through: it carries data and guarantees its integrity, it does not run business rules. Two conventions coexist for the DTO → Command conversion, chosen by context size/complexity — pick whichever the rest of that context already uses, don't mix both within one context:
- **Small context (`seguridad`, `usuarios`):** the DTO carries Jakarta annotations (`@NotBlank`, `@NotNull`) for presence/shape checks and owns its own `toCommand()` instance method — except identifier fields, which are never annotated; see "Identifiers in request bodies" below.
- **Larger, more heavily validated context (`fichas`):** the DTO is a bare record with no annotations at all; a sibling `{Action}{Entity}RequestMapper` (`final`, private constructor, static `toCommand(dto)`) in `primaryadapter/web/mapper/` performs the conversion, delegating all format validation to the Command's own `crear(...)` factory (see "Commands" above) instead of to Jakarta Bean Validation.

**Identifiers in request bodies:** received as `String`, never as `UUID` — a malformed `UUID` field would otherwise surface as a blind Jackson deserialization error. **Format validation of an identifier is never a Jakarta Bean Validation annotation — neither a custom one nor a library-provided one** (`org.hibernate.validator.constraints.UUID` and equivalents are off the table too). This is a hard rule for every context, present or future, not a per-context choice: the check always happens at the Command boundary — `ValidatorUUID.uuidValido(...)` (`shared:validation`), called from `{Command}.crear(...)` in the "larger context" DTO convention or from the DTO's own `toCommand()` in the "small context" one (see "DTOs" above) — accumulating into the same `fieldErrors[]` shape either way. `shared:web` used to ship a custom `@UuidValido` constraint for this; it was removed because a Jakarta annotation on the DTO and a `Command`-level check are two competing places to look for the same rule, and the annotation could silently go unused (as it did) while the DTO looked validated. `seguridad` and `usuarios` have no identifier fields in their request DTOs today — `fichas` is simply the context where this rule already has something to show. Conversion to `UUID` happens via `UtilUUID.generarUUIDDesdeTexto`, in `toCommand()` or in `{Command}.crear()` depending on which DTO convention the context uses; the Command itself stays typed `UUID`. Path variables remain `UUID`.

**Objectual naming in contracts:** DTO and Command components carry what they represent, not the column name: `asesorFicha` (not `asesorFichaId`), `estudiantes` (not `estudiantesIds`). Field-name constants live in `FichasFields.{Entity}.*`.

**No hardcoded literals in adapters:** response codes come from `ApiCodes` (`shared:web`), Swagger texts from `FichasApiKeys` (`shared:message`), authorities from `FichasAuthorities` (`@PreAuthorize(FichasAuthorities.Expresiones.HAS_...)`, with the raw authority available for tests), base paths from `FichasRoutes`. Length limits come from `FichasLimits`, passed as a plain argument to `ValidatorLongitud`/`ValidatorColeccion` inside `{Command}.crear()` — never a Jakarta `@Size`/`@Max` annotation, since the "larger context" DTO convention (see "DTOs" above) carries no annotations at all. Changing a length must never require touching two places.

**Message catalog:** runtime texts (errors, logs) live in `.properties` under `shared:message` and resolve through the injectable `CatalogoMensajes` port — `private final CatalogoMensajes catalogo;` in application/infrastructure, the static `Mensajes` facade in domain (aggregates and exceptions have no injection point). Keys follow `contexto.capa.objeto.tipo.descripcion`. What the compiler forces to stay a constant — error codes (`*Codes`), limits (`*Limits`), field names (`*Fields`) — does not go to the bundle, because annotation values must be constant expressions (JLS §9.7.1). `CatalogoMensajesClavesTest` fails the build on a key with no text or a text with no key. See [shared/message/README.md](shared/message/README.md).

**What is text and what is an identifier.** The catalog holds prose a human reads. It does **not** hold strings that tooling or a contract matches exactly — translating those silently breaks the thing that reads them. Identifiers stay Java constants next to their use: error codes (`*Codes`), MDC field names and sentinel values (`TrazaKeys`, `TrazaValores.ANONIMO/SISTEMA/EVENTO`), HTTP and AMQP header names (`TrazaHeaders`), log markers greppable in Loki (`TrazabilidadFilter`'s `AUDIT`), infrastructure names (`RabbitMQConfig.EXCHANGE_NAME`, bean names referenced by both `@Bean` and `@Qualifier`), and external API codes (MinIO's `NoSuchKey`). The display labels of catalog enums (`EstadoFicha.getNombre()`) also stay in Java: their source of truth is the MER, and a copy in `.properties` would be a second one.

The rule covers `shared:*` too, not only the contexts — `shared:query` and `shared:jpa` resolve the dynamic-query texts from `ConsultaKey` + `AppCodes.Consulta`. Library classes there (records, sealed interfaces, static utilities, DTOs deserialized by Jackson) have no injection point either, so they use the static `Mensajes` facade like the domain does. Two things that look like text but are **not** catalog material: error codes, and infrastructure identifiers such as `RabbitMQConfig.EXCHANGE_NAME` — both are contract, not presentation.

**Where an identifier lives depends on its reach, and the default is narrow.** A `*Codes`/`*Fields`/`*Routes` class in `shared:message` is for identifiers that cross a boundary — something outside the declaring class asserts on them. An identifier used **only inside one class** stays a `private static final` field of that class: the JSON keys a controller assembles (`MinioGuiaController.CAMPO_BUCKET`), the protocol parameters an adapter sends (`KeycloakAuthOutputAdapter.PARAM_GRANT_TYPE`), the URL templates and public route patterns of a config (`SeguridadConfig.PLANTILLA_ISSUER`), the format skeletons of a message (`ValidationResult.SEPARADOR_ERRORES`), the regexes of a utility (`UtilFecha.PATRON_FECHA`). Promoting those to a shared catalog widens their reach for no gain and invites use from places that should not know them. The trigger for promotion is a **second reader**: `AppCodes.Validacion.DOMINIO` moved out of `DomainValidationException` because a controller test asserts on it, so the literal existed in two modules at once.

Naming these private constants is not cosmetic. In `KeycloakAuthOutputAdapter`, `"refresh_token"` is simultaneously a grant type, a request parameter and a response field, and `"password"` is both a grant type and a parameter name — three constants with distinct names for the same literal, because the reader cannot otherwise tell which role is meant. Extract when the literal repeats, carries a role the name clarifies, or is a magic value (`EXPIRACION_POR_DEFECTO`); a single-use `", "` inside one `Collectors.joining` is not worth a constant.

Substitution is `String.formatted`, so patterns use `%s`, **not** `{0}`; log patterns keep SLF4J's `{}` and are passed through `obtener`, never `formatear`. `CatalogoMensajesClavesTest` does not check that a pattern declares as many `%s` as its call site passes — one too few throws `MissingFormatArgumentException` at runtime. Only a test that asserts on the **rendered** text closes that gap (`FiltroMensajesTest`, `CampoSpecMensajesTest`).

**Exceptions:** Every context exception extends one of four bases from `com.arquisoft.shared.exception`: `DomainException` (422), `ApplicationException` (400), `InfrastructureException` (503), `DomainValidationException` (422 + `fieldErrors[]`). Never `RuntimeException` directly. The constructor signature is `super(message, errorCode)` — both are `String`, so swapping them compiles and silently swaps the two fields in the response. `GlobalAppExceptionHandler` (`shared:web`) resolves the HTTP status by walking the superclass chain; contexts do not define their own handler (only `seguridad` does, for a name clash with Spring Security).

**Business rules:** Invariants of a single aggregate are validated inside the aggregate (→ 422), never with `if/throw` in the use case. Checks that need state from outside the aggregate — existence, DB duplicates, ownership — are domain `Rule`s fed by the use case (see "Domain rules" above), never inline `if/throw`.

**Where each exception lives:** an exception thrown by a `Rule` lives in `domain/{feature}/exception/` and extends `DomainException` (→ 422) — including "not found", "duplicate", and ownership checks (`FichaNoPropietarioException`, `ItemFichaNoPropiaException`, `EvaluacionFichaNoPropiaException`); there is no separate 403 case for "you are not the owner" — it is modeled as another invalid-state 422. An exception thrown by application-layer orchestration lives in `application/{feature}/exception/` and extends `ApplicationException` (→ 400). Infrastructure failures stay `InfrastructureException` (→ 503) and are raised by the output adapters.

**Naming:** Spanish for business concepts (`crearFicha`, `FichaException`), English for technical suffixes (`Domain`, `Interactor`, `UseCase`, `Impl`, `OutputPort`, `Consumer`, `OutputAdapter`, `Controller`, `ReadModel`, `DTO`, `Command`) — no Spanish exception remains; REST controllers were renamed from `Controlador` to `Controller` for consistency.

**Injection:** Always constructor injection via `@RequiredArgsConstructor` — never `@Autowired`.

**Logging:** inject the `AppLogger` port (`shared:logger`) by constructor — `private final AppLogger logger;` — instead of `@Slf4j`, so application and infrastructure code does not depend on SLF4J. `Slf4jAppLogger` is the default strategy, wired as a prototype bean by `AppLoggerConfig` (each class gets a logger named after itself). Applied throughout `fichas`; `seguridad` and `usuarios` still use `@Slf4j` (pending migration). `warn` for 4xx, `error` for 5xx. Structured JSON via `shared:logger`; the trace context itself comes from `shared:tracing` (see *Correlation* below). In `@WebMvcTest` slices add `AppLoggerConfig.class` to `@Import`; in unit tests pass a mock.

**Correlation:** `shared:tracing` owns the whole trace context. It is the one shared module laid out in hexagonal layers internally (`domain/traza` → `application/traza/{primaryport,secondaryport}` → `infrastructure/traza`), so `MdcContextoDiagnosticoOutputAdapter` is the only class in the codebase that touches `org.slf4j.MDC` and the domain (`Traceparent`, `IdentificadorTraza`, `ClienteIp`, `CorrelacionEntrante`) stays pure Java.

**Other modules never implement a contract of `shared:tracing`** — the module exposes the mechanism instead. Inject `GestorTraza`, open a scope, and call named methods on it:

```java
try (var alcance = gestorTraza.abrir(SolicitudTraza.paraHttp(...))) { ... }   // cierra y limpia el MDC
gestorTraza.registrarUsuario(jwt.getSubject());                              // enriquece el alcance abierto
```

`AlcanceTraza` is `AutoCloseable` and **must** be used with try-with-resources: `close()` restores the previously captured MDC map (not a `remove`, so nested scopes — a consumer inside a consumer — give back the outer value). There is deliberately **no generic `registrarAtributo(clave, valor)`**: a free-form put is how tokens and PII end up in the MDC, so each new field costs a key in `TrazaKeys` plus a named method.

`correlacionId` groups the whole transaction across hops; `transaccionId` is regenerated on every hop (a request, an AMQP consumption), so the consumer of an event shares the producer's correlation but gets its own transaction. An incoming `X-Correlation-Id` is reused **verbatim** (never normalised to 32 hex — rewriting it breaks correlation with the caller), falling back to the W3C `traceparent` trace-id and only then generating one. Incoming values are sanitised against a whitelist (log injection). `ErrorResponseDTO` carries both as `traceId` and `transaccionId` — `traceId` keeps its name because it is a contract with the frontend.

Two filters, and the split is not cosmetic: `TrazabilidadFilter` (`shared:web`, `@Order(-300)`) is the outermost one and owns the scope plus the `AUDIT` line; `IdentidadTrazaFilter` (`seguridad:infrastructure`) only adds the user, and is registered **inside the Spring Security chain** via `addFilterAfter(BearerTokenAuthenticationFilter.class)` — at `LOWEST_PRECEDENCE` it would sit after `AuthorizationFilter` and every 403 would be audited as anonymous. It is declared as a `@Bean` plus a disabled `FilterRegistrationBean`, because any `Filter` bean is also auto-registered in the servlet chain and `OncePerRequestFilter` would then skip the second pass entirely.

`servicioNombre`/`version` are **not** MDC keys: they are process constants added as static JSON members through `logging.structured.json.add.*`. Spring Boot's logstash formatter only serialises the MDC map and the event's key-value pairs — never Logback context properties — so `<springProperty scope="context">` would not surface them.

**No Lombok in domain layer.**

**Comments and Javadoc.** Do not write Javadoc, and do not comment code that already says what it does — a comment restating the method name, the type, or the control flow is noise that goes stale. `domain/` and `application/` carry **no** Javadoc and no comments at all; the names of the aggregate, the rule, and the use case are the documentation. Elsewhere a comment is justified only when it records something the code cannot show: an external constraint, a non-obvious consequence, or why an obvious alternative was rejected. Prefer putting that reasoning in the commit message or in this file rather than in the source. Test method names follow `debeHacerAlgo_cuandoCondicion()` and the `// Arrange` / `// Act` / `// Assert` markers stay — they are structure, not explanation.

**`var` for local variables.** Prefer `var` when the right-hand side already names the type — `var config = new HikariConfig()`, `var id = UUID.fromString(...)`, `var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto)` — and when it removes a long generic repetition, as in `var resultado = useCase.ejecutar(criteria)` instead of restating `PaginatedResult<FichaPerfilReadModel>`. It applies only to locals: fields, parameters, and return types stay explicit.

Three cases where the explicit type stays, and they are not style preferences:
- **Diamond on the right.** `var m = new LinkedHashMap<>()` infers `LinkedHashMap<Object, Object>` and the error surfaces far from here. Either keep the declared type or spell the type arguments inside the diamond.
- **Anonymous classes.** `var x = new X509TrustManager() { … }` infers the anonymous type, hiding the interface that the surrounding code actually consumes.
- **The declared type is deliberately an interface** the right-hand side does not name — `CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto()` states the contract on purpose.

Short type names (`String`, `int`, `UUID` when the right side is not a factory) gain nothing from `var`; leave them.

## Technology Stack

| Component | Version |
|-----------|---------|
| Java | 21 (Virtual Threads active automatically) |
| Spring Boot | 4.0.5 |
| Gradle | 9.0.0 |
| PostgreSQL | 18 (8 schemas, separate DataSource per context) |
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

- JWT validated against Keycloak JWK Set (configured in `seguridad/infrastructure/config/security/SeguridadConfig`)
- Rate limiting via Bucket4j: per-IP buckets backed by Redis (`RedisBucketResolver`, Lettuce) — no in-memory map, no `max-tracked-ips` (removed when the resolver migrated off `ConcurrentHashMap`). Defaults: 100 req/min global dev, 60 prod; 5 login/min dev, 3 login/min prod. On a Redis error, `resolveBucket`/`resolveLoginBucket` fail closed — return an already-exhausted bucket instead of propagating the exception.
- `TrazabilidadFilter` (`shared:web`) emits an `AUDIT` line per request with method, URI, user, duration and status (skips Swagger/actuator paths). Being the outermost filter, it also audits 401, 403 and 429 — the old `AuditFilter` sat inside `FilterChainProxy` and never saw them
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
- [docs/ARQUITECTURA_Y_ESTRUCTURA.md](docs/ARQUITECTURA_Y_ESTRUCTURA.md) — long-form architecture reference (human-oriented, not for agent context)
- [docs/ARQUITECTURA_ASINCRONICO_ARQUISOFT.md](docs/ARQUITECTURA_ASINCRONICO_ARQUISOFT.md) — domain events + outbox pattern in depth (human-oriented, not for agent context)
- [docs/PATRON_QUERY_OBJECT_FILTROS_DINAMICOS.md](docs/PATRON_QUERY_OBJECT_FILTROS_DINAMICOS.md) — the dynamic-filter Query Object + Specification pattern in depth (human-oriented, not for agent context)
- [docs/EJECUCION_LOCAL.md](docs/EJECUCION_LOCAL.md) — full local setup guide
- [CONTRIBUTING.md](CONTRIBUTING.md) — git workflow and branch naming
