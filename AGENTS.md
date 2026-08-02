# AGENTS.md — Arquisoft Backend

## Stack Real (verificado en gradle.properties)

- **Java 21**, **Spring Boot 4.0.5**, **Gradle 9.0.0**, **JUnit 6.0.3**
- PostgreSQL 18, RabbitMQ 4.2.5, Redis 7, Keycloak 26.6 (OAuth2/OIDC Resource Server)
- Drivers en `gradle.properties`: las líneas `postgresVersion`, `rabbitmqVersion`, `redisVersion` están **comentadas** — Spring Boot gestiona las versiones de drivers via BOM. `keycloakVersion=25.0.3` (adapter) es la única línea activa.
- `RestTemplateBuilder` fue eliminado en Spring Boot 4.x — usar `SimpleClientHttpRequestFactory` directamente (ver `RestTemplateConfig.java`)

## Comandos Esenciales

```bash
./gradlew build                    # Compilar + tests
./gradlew build -x test            # Compilar sin tests
./gradlew clean build              # Limpiar y compilar
./gradlew bootRun --args='--spring.profiles.active=dev'
./gradlew projects                 # Listar los 28 subproyectos
./gradlew test                     # Todos los tests
./gradlew seguridad:infrastructure:test   # Tests de un modulo
./gradlew test --tests "*.NombreTest.nombreMetodo"
./gradlew jacocoTestReport         # Cobertura (minimo 75%)
```

## Estructura de Modulos

28 subproyectos Gradle (7 contextos × 3 capas + 7 módulos shared). Cada bounded context tiene 3 capas:

```
{contexto}/domain        ← Java puro, sin Spring, sin Lombok
{contexto}/application   ← DTOs, use cases (Lombok OK)
{contexto}/infrastructure ← Controllers, adapters, config, filters (Spring completo)
```

**Contextos:**
- `shared` → `domain`, `amqp`, `exceptions`, `postgres`, `redis`, `validation`, `web`
- `seguridad`, `fichas`, `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables`, `evaluaciones`

**Dirección de dependencias (estricta via Gradle):** `domain ← application ← infrastructure`
Los contextos nunca dependen entre sí — se comunican via RabbitMQ (`shared:amqp`).

## Arquitectura Hexagonal — Convenciones Clave

| Elemento | Convención |
|---|---|
| Entidades de dominio | Inmutables, constructor privado, campos `final`, solo getters. `build(UUID.randomUUID())` para nuevas, `rebuild(uuid)` desde persistencia. Sin Lombok ni Spring. |
| Aggregate Root | Entidades raíz extienden `AggregateRoot` de `shared:domain` (`com.arquisoft.shared.domain`). Gestiona eventos de dominio no publicados via `publishEvent(DomainEvent)`. |
| Eventos de dominio | Extienden `DomainEvent` de `shared:domain`. Cada subclase declara sus **propios campos** con nombres semánticamente correctos (e.g. `usuarioId`, `fichaId`). `DomainEvent` asigna `eventId` (UUID), `occurredAt`, `eventType` y `eventTopic` automáticamente. El constructor recibe `(eventTopic, eventType)` — sin `aggregateId` genérico. |
| Puertos de entrada | `{Accion}{Entidad}Interactor` en `application/{feature}/command/interactor/` (lado comando, es lo que inyecta el adapter) y `{Accion}{Entidad}UseCase` en `application/{feature}/{command\|query}/usecase/` |
| Puertos de salida | `{Entidad}RepositoryPort` en `domain/port/out/` |
| Use cases | `{Accion}{Entidad}UseCaseImpl` en `application/{feature}/{command\|query}/usecase/impl/`; el interactor en `application/{feature}/command/interactor/impl/` |
| DTOs | Sufijo `DTO`, con `toDomain()` y `static fromDomain(...)`. `@Data @NoArgsConstructor @AllArgsConstructor @Builder`. |
| Excepciones de dominio | Extienden `DomainException` (shared) con campo `errorCode` |
| IDs | Siempre `UUID` — nunca `Long` ni `Integer` |
| Interactor (lado comando) | `{Accion}{Entidad}InteractorImpl` implementa `{Accion}{Entidad}Interactor` y declara `@Transactional(transactionManager = "{contexto}TransactionManager")`; delega en el use case, que ya no implementa el puerto ni maneja la transacción. Aplicado en `fichas` y `usuarios`; `seguridad` tiene interactor pero sin `@Transactional` (no tiene DataSource propio: Keycloak + Redis) |
| Resultados de comando | Un comando devuelve `UUID` o `void`; si devuelve algo mas rico, el record vive en `application/{feature}/command/result/` con sufijo `Result` — nunca anidado en la interfaz `UseCase` ni en `model/`, que es solo entrada |
| Reglas de dominio | `{Regla}Rule` en `domain/{feature}/rules/` con su `{Regla}RuleImpl` en `rules/impl/` (POJO sin Spring); se registran como bean en `{Contexto}DomainRulesConfig` de infrastructure |
| Validators | Existencia, unicidad y propiedad en `{Feature}Validator` (`application/{feature}/command/validator/`), reutilizables entre features — no bloques `if/throw` dentro del use case |
| Orden de validación | 1) integridad del dato (formato, obligatoriedad, longitud, duplicados en la petición), 2) existencia/unicidad en BD, 3) reglas de negocio del agregado |
| Identificadores en el body | Se reciben como `String` con `@UuidValido` (`shared:web`) y se convierten a `UUID` en `toCommand()`; nunca tipados `UUID` en el DTO |
| Nombres del contrato | Objetuales: `asesorFicha`, `estudiantes` — no `asesorFichaId`, `estudiantesIds` |
| Literales en adapters | `ApiCodes` (códigos HTTP), `FichasApiKeys` (textos Swagger), `FichasAuthorities` (authorities), `FichasRoutes` (rutas) — nada quemado inline |
| Mensajes y logs | `MessageCatalog` inyectado (dominio: fachada estática `Messages`); textos en `shared:message/resources/messages/*.properties` con clave `contexto.capa.objeto.tipo.descripcion` |

## Outbox Pattern — Spring Modulith 2.0.0

Los eventos de dominio se publican usando el **Event Publication Registry** de Spring Modulith, que garantiza atomicidad entre el `save` del aggregate y la publicación al broker.

### Flujo obligatorio en use cases que publican eventos

```java
@Transactional(transactionManager = "xxxTransactionManager")  // qualifier explícito obligatorio
@Override
public UUID ejecutar(CrearXxxCommand command) {
    XxxAggregate aggregate = XxxAggregate.crear(...);
    xxxOutputPort.save(aggregate);
    aggregate.drainUnPublishedEvents().forEach(eventPublisher::publish);
    return aggregate.getId();
}
```

- `eventPublisher` es `SpringModulithEventPublisher` (en `shared/amqp`), que delega a `ApplicationEventPublisher`.
- Spring Modulith intercepta el `publishEvent` y persiste el evento en la tabla `event_publication` de la BD del propio contexto **dentro de la misma transacción** — atomicidad garantizada.
- Tras el commit, lo publica a RabbitMQ con el `eventTopic` como routing key.

### Outbox por contexto — `event_publication` distribuida

Cada contexto que publique eventos tiene su propia tabla `event_publication` en su BD. No existe una BD centralizada para el outbox. `ContextAwareEventPublicationRepository` (`src/main/config/outbox`) auto-detecta al arranque qué DataSources tienen la tabla y enruta el INSERT a la transacción activa.

Para habilitar el outbox en un nuevo contexto, agregar la migración Flyway en su BD:

```sql
-- {contexto}/infrastructure/.../db/migration/{contexto}/V{N}__crear_event_publication.sql
CREATE TABLE event_publication (
    id UUID NOT NULL, listener_id TEXT NOT NULL, event_type TEXT NOT NULL,
    serialized_event TEXT NOT NULL, publication_date TIMESTAMPTZ NOT NULL,
    completion_date TIMESTAMPTZ, status TEXT, completion_attempts INT,
    last_resubmission_date TIMESTAMPTZ, PRIMARY KEY (id)
);
```

Sin cambios en código Java — la detección es automática.

### Convención de eventos de dominio

```java
public class UsuarioCreadoEvent extends DomainEvent {
    public static final String EVENT_TOPIC = "seguridad.usuario.creado";
    public static final String EVENT_TYPE  = "UsuarioCreadoEvent";

    private final UUID usuarioId;  // campo propio tipado — no usar aggregateId del padre
    private final String email;
    private final String rol;

    public UsuarioCreadoEvent(UUID usuarioId, String email, String rol) {
        super(EVENT_TOPIC, EVENT_TYPE);  // sin aggregateId — DomainEvent ya no lo recibe
        this.usuarioId = usuarioId;
        this.email = email;
        this.rol = rol;
    }
}
```

`DomainEvent` **no** tiene campo `aggregateId`. Cada evento declara sus propios campos.

### Retry automático

`FailedEventRetryConfig` (`src/main/config`) llama a `FailedEventPublications.resubmit()` cada 5 minutos para reintentar eventos con estado `FAILED` (e.g., broker caído). El staleness checker de Spring Modulith solo **marca** eventos como `FAILED` — no los reintenta.

---

## Configuracion: Virtual Threads (ADR-008)

- Habilitados en `application.yml`: `spring.threads.virtual.enabled: true`
- Java 21 + Spring Boot 4.x: aplica a Tomcat, `@Async` y listeners de RabbitMQ automáticamente
- Beneficio: operaciones I/O bloqueantes (Keycloak, JDBC, Redis) sin consumo de OS threads
- **No requiere configuración adicional en código** — Spring Boot gestiona el executor automáticamente
- Impacto en tests: los tests de integración pueden observar comportamiento concurrente diferente al de OS threads

## Configuracion: Seguridad

### SecurityConfig (`seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/config/SecurityConfig.java`)

- `@EnableWebSecurity` + `@EnableMethodSecurity(prePostEnabled = true)`
- JWT decodificado via JWK Set URI de Keycloak: `{keycloak-url}/realms/{realm}/protocol/openid-connect/certs`
- CSRF deshabilitado, sesiones stateless
- **Endpoints públicos (permit-all):**
  - `POST /auth/login`, `POST /auth/refresh`, `POST /auth/validate`
  - `/actuator/health/**`
  - `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-resources/**`

### CorsConfig (`seguridad/infrastructure/.../config/CorsConfig.java`)

- Lee de propiedades `security.cors.*`
- Orígenes por defecto: `localhost:3000`, `localhost:4200`
- Expone headers: `Authorization`, `Content-Type`, `X-Total-Count`, `X-Page-Number`, `X-Page-Size`
- Credenciales habilitadas, registrado en `/**`

### RateLimitConfig (`seguridad/infrastructure/.../config/RateLimitConfig.java`)

- Usa **Bucket4j** (`bucket4j-core:7.6.0`) con per-IP buckets en `ConcurrentHashMap`
- Propiedades: `security.rate-limit.enabled`, `requests-per-minute` (default 100), `login-requests-per-minute` (default 5)
- **En `application-prod.yml`: habilitado con 60/min global, 3/min login**

### application-security.properties (`seguridad/infrastructure/src/main/resources/`)

Variables de entorno requeridas:
```
KEYCLOAK_URL         → https://localhost:8443/auth
KEYCLOAK_REALM       → arquisoft
KEYCLOAK_CLIENT_ID   → arquisoft-app
KEYCLOAK_CLIENT_SECRET
```

## Configuracion: Swagger / OpenAPI (ADR-011)

La documentación de la API se gestiona con **springdoc-openapi 2.x** (Spring Boot 4.x compatible).

| Capa | Configuracion |
|---|---|
| `build.gradle` raíz | Plugin `org.springdoc.openapi-gradle-plugin` version `1.9.0` + dependencia `springdoc-openapi-starter-webmvc-ui:2.8.8` |
| `src/main/java/com/arquisoft/config/OpenApiConfig.java` | `@OpenAPIDefinition` + `@SecurityScheme` global (bearerAuth JWT) |
| `application.yml` | `springdoc.swagger-ui.enabled: true` + `springdoc.api-docs.enabled: true` |
| `application-prod.yml` | Ambos **deshabilitados** en prod |
| `SecurityConfig.java` | `/swagger-ui/**`, `/v3/api-docs/**` son permit-all |
| `AuditFilter.java` | Paths de Swagger excluidos del log de auditoría |

URL en dev: `http://localhost:8080/api/swagger-ui/index.html` — sin autenticación.

### Convenciones de documentación de Controllers (ADR-011)

Todo `@RestController` en capa `infrastructure` **DEBE** incluir anotaciones OpenAPI:

```java
// A nivel de clase
@Tag(name = "NombreContexto", description = "Descripcion del grupo de endpoints")

// A nivel de método
@Operation(
    summary = "Resumen corto (< 10 palabras)",
    description = "Descripcion larga opcional",
    security = @SecurityRequirement(name = "bearerAuth")  // omitir solo en endpoints públicos
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Operacion exitosa",
        content = @Content(schema = @Schema(implementation = MiResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Datos invalidos",
        content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
    @ApiResponse(responseCode = "401", description = "No autenticado"),
    @ApiResponse(responseCode = "403", description = "Sin permisos"),
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
})
```

Para endpoints **públicos** (login, refresh, validate) omitir `@SecurityRequirement`.

### Configuración global

- `OpenApiConfig.java` en `src/main/java/com/arquisoft/config/` (paquete config de la app principal) — **no duplicar** en módulos individuales. Reside aquí porque es el único punto que ensambla todos los contextos y tiene visibilidad completa de la API.
- Si un contexto necesita un **grupo** de API separado, crear `{Contexto}OpenApiGroupConfig.java` en `{contexto}/infrastructure/config/` usando `@Bean GroupedOpenApi`.

## Configuracion: application.yml

- Servidor: puerto `8080`, context path `/api`
- Carga `.env.properties` opcional para secretos locales
- DB: `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}` — Flyway habilitado
- RabbitMQ: `localhost:5672` / guest/guest por defecto
- Redis: `localhost:6379`
- CORS permitidos por defecto: `localhost:3000`, `localhost:4200`, `localhost:5173`
- Actuator expone: `health, info, metrics, prometheus`

## Esquemas de Base de Datos

El nombre del schema **NO coincide** con el nombre del contexto en 3 casos:

| Contexto Gradle | Schema PostgreSQL |
|---|---|
| `seguridad` | `usuarios` |
| `fichas` | `fichas_perfil` |
| `proyectos` | `proyectos_grado` |
| Los demás | igual al nombre del contexto |

Migraciones Flyway en `{contexto}/infrastructure/src/main/resources/db/migration/`.

## Estilo de Código

### Nomenclatura

| Elemento | Ejemplo |
|---|---|
| Clases / Interfaces | `AuthController`, `FichaRepositoryPort` |
| Implementaciones | `KeycloakAuthServiceImpl` |
| DTOs | `LoginRequestDTO` |
| Excepciones | `InvalidCredentialsException` |
| Métodos de test | `debeHacerAlgo_cuandoCondicion` |

**Regla bilingüe:** español para dominio/negocio (`ProyectoGrado`, `crearFicha`), inglés para sufijos técnicos (`UseCase`, `Adapter`, `DTO`, `Controller`).

### Inyección de dependencias

- Siempre por constructor con `@RequiredArgsConstructor` (Lombok)
- Nunca `@Autowired`
- Inyectar interfaces (puertos), nunca implementaciones

### Logging

- Puerto `AppLogger` (`shared:logger`) inyectado por constructor: `private final AppLogger logger;` — desacopla la aplicación de SLF4J (implementación por defecto `Slf4jAppLogger`, bean prototype de `AppLoggerConfig`). Aplicado en `fichas`; el resto de contextos aún usa `@Slf4j`.
- `logger.warn()` para 4xx, `logger.error()` para 5xx
- En slices `@WebMvcTest` añadir `AppLoggerConfig.class` al `@Import`; en tests unitarios, mock por constructor

### Trazabilidad

- `TraceIdFilter` (`shared:web`) reutiliza el header `X-Correlation-Id` entrante, cae al trace-id de `traceparent` (W3C) y solo si no hay ninguno genera un UUID
- El id resuelto va al MDC, se devuelve en el header `X-Correlation-Id` de la respuesta y viaja como `traceId` en `ErrorResponseDTO`, de modo que un error reportado se puede reconstruir desde los logs

## Testing

- Stack: JUnit 6 + Mockito + AssertJ. H2 para repositorios. `spring-security-test` para controllers.
- Tests unitarios: `@ExtendWith(MockitoExtension.class)`, sin contexto Spring
- Tests de integración de repositorio: `@SpringBootTest` con H2
- Patrón AAA: Arrange / Act / Assert

## Git y PRs

- Commits: `feat(contexto): descripcion corta` (Conventional Commits en español)
- Ramas desde `develop`: `feature/HU-XXX-descripcion_snake_case`
- PRs hacia `develop`, 1 aprobación requerida, usar `.github/PULL_REQUEST_TEMPLATE.md`

## Pipeline de Agentes (.opencode/)

El repositorio usa un pipeline de 5 agentes:

1. `@planificador` — genera `PLAN-{HU|HT}-{ID}.md` en `.workspace/h-plan/`
2. `@implementador` — lee el plan como contrato, implementa capa a capa
3. `@tester` — genera tests JUnit 6 + Mockito por capa
4. `@validator` — verifica criterios, genera reporte en `.workspace/validator/`, propone commit (sin ejecutar git)
5. `@commit` — lee el reporte aprobado por `@validator` y ejecuta el commit git

**Antes de planificar cualquier HU:** usar skill `gh-docs-reader` para leer la HU del repo de documentación.
**Antes de generar cualquier archivo Java:** usar skill `context7-stack` para obtener IDs de Context7 actualizados.
