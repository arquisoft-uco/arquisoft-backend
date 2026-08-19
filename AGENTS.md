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
./gradlew projects                 # Listar los 50 subproyectos
./gradlew checkstyleMain checkstyleTest  # Lint (Checkstyle 10.12.5, exigido por CI)
./gradlew test                     # Todos los tests
./gradlew seguridad:infrastructure:test   # Tests de un modulo
./gradlew test --tests "*.NombreTest.nombreMetodo"
./gradlew jacocoTestReport         # Cobertura (minimo 75%)
```

## Estructura de Modulos

51 subproyectos Gradle: 9 contextos × [3 capas + 1 agregador] = 36, + 14 módulos `shared` + 1 agregador `shared` = 15. Cada bounded context tiene 3 capas. Solo `seguridad`, `usuarios`, `fichas` y `notificaciones` tienen implementación real; `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables` y `evaluaciones` son scaffolding (solo `{Contexto}DataSourceConfig`, sin código de dominio/aplicación aún).

```
{contexto}/domain        ← Java puro, sin Spring, sin Lombok
{contexto}/application   ← DTOs, use cases (Lombok OK)
{contexto}/infrastructure ← Controllers, adapters, config, filters (Spring completo)
```

**Contextos:**
- `shared` → `util`, `exception`, `validation`, `domain`, `logger`, `tracing`, `redis`, `amqp`, `web`, `minio`, `jpa`, `query`, `message`, `notification`
- `seguridad`, `usuarios`, `fichas`, `notificaciones`, `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables`, `evaluaciones`

`shared:postgres` fue renombrado a `shared:jpa` (Specification, QueryRepository, PageableMapper — todo lo que necesita Spring Data JPA) y se extrajo `shared:query` (QueryCriteria/SortOrder/NodoFiltro/FiltroOperador, PaginatedResult, los DTO de filtro — vocabulario de consulta sin dependencia de Spring).

**Dirección de dependencias (estricta via Gradle):** `domain ← application ← infrastructure`
Los contextos nunca dependen entre sí — se comunican via RabbitMQ (`shared:amqp`).

## Arquitectura Hexagonal — Convenciones Clave

| Elemento | Convención |
|---|---|
| Entidades de dominio | Inmutables desde fuera, constructor privado, campos `final` no (Notification Pattern: no-`final` con setters privados), solo getters. `crear(...)` para nuevas, `reconstruir(...)` desde persistencia — **no** `build()`/`rebuild()`. Sin Lombok ni Spring. |
| Aggregate Root | Entidades raíz extienden `AggregateRoot` de `shared:domain` (`com.arquisoft.shared.domain`). Sufijo de la clase concreta es `Domain` (sustantivo, no el verbo de la acción), no `Aggregate` — e.g. `FichaPerfilDomain`, no `FichaPerfilAggregate` ni `RegistrarFichaPerfilDomain`. Vive directo en `domain/{feature}/`, sin subcarpeta `aggregate/` ni `model/`. Gestiona eventos de dominio no publicados via `publicarEvento(DomainEvent)`. |
| Eventos de dominio | Extienden `DomainEvent` de `shared:domain`. Cada subclase declara sus **propios campos** con nombres semánticamente correctos (e.g. `usuarioId`, `fichaId`). `DomainEvent` asigna `idEvento` (UUID), `ocurridoEn`, `tipoEvento` y `temaEvento` automáticamente. El constructor recibe `(temaEvento, tipoEvento)` — sin `aggregateId` genérico. |
| Puertos de entrada | `{Accion}{Entidad}Interactor` en `application/{feature}/command/primaryport/interactor/` (lado comando, es lo que inyecta el adapter) y `{Accion}{Entidad}UseCase` en `application/{feature}/command/usecase/` (lado comando, sin `primaryport/` — es un colaborador interno, no el contrato primario) y `{Consultar}{Entidad}Interactor` en `application/{feature}/query/primaryport/interactor/` (lado consulta, con su `UseCase` en `application/{feature}/query/usecase/`) |
| Puertos de salida | `{Entidad}OutputPort` en `application/{feature}/command/secondaryport/` (escritura) o `application/{feature}/query/secondaryport/` (lectura) — **nunca en `domain/`**: el dominio no declara puertos ni hace I/O, solo depende de `shared:domain` |
| Entity vs JpaEntity | `{Feature}Entity` (`application/{feature}/command/secondaryport/entity/`) es un **record plano sin JPA ni Lombok** — la forma de persistencia que habla el puerto. La forma JPA real vive en infraestructura: `{Feature}JpaEntity` (`infrastructure/{feature}/command/secondaryadapter/entity/`, con `@Entity`/Lombok) + `{Feature}JpaMapper` que convierte `Entity ↔ JpaEntity` en el adapter. `application` no depende de JPA en absoluto |
| Aislamiento CQRS | `query` nunca importa nada de `secondaryadapter` de `command`, ni siquiera el `JpaEntity` — cada feature con lectura real tiene su propio `{Feature}JpaQueryEntity` (`@Subselect`/`@Immutable`/`@Synchronize`). Un `existePor`/`obtener` que solo necesita un `Validator`/`Rule` de `command` pertenece al `OutputPort` de `command`, consumido por un `Finder` — nunca duplicado bajo `query/` sin un `primaryport` real detrás |
| Use cases | `{Accion}{Entidad}UseCaseImpl` en `application/{feature}/command/usecase/impl/` o `application/{feature}/query/usecase/impl/`; los interactores en `application/{feature}/command/primaryport/interactor/impl/` y `application/{feature}/query/primaryport/interactor/impl/` |
| DTOs | `record` con sufijo `DTO`, sin Lombok. En contextos pequeños (`seguridad`, `usuarios`) el propio DTO expone `toCommand()` y puede llevar anotaciones Jakarta (`@NotBlank`, `@NotNull`) para presencia/forma — excepto en campos de identificador, ver fila siguiente; en contextos grandes con validación más pesada (`fichas`) el DTO es un record sin anotaciones y un `{Feature}RequestMapper` externo (`primaryadapter/web/mapper/`) hace `toCommand(dto)`, delegando el formato a `{Command}.crear(...)` |
| Excepciones de dominio | Extienden `DomainException` (shared) con campo `errorCode` |
| IDs | Siempre `UUID` — nunca `Long` ni `Integer` |
| Interactor (lado comando) | `{Accion}{Entidad}InteractorImpl` implementa `{Accion}{Entidad}Interactor` y declara `@Transactional(transactionManager = "{contexto}TransactionManager")`; delega en el use case, que ya no implementa el puerto ni maneja la transacción. Aplicado en `fichas` y `usuarios`; `seguridad` tiene interactor pero sin `@Transactional` (no tiene DataSource propio: Keycloak + Redis) |
| Resultados de comando | Un comando devuelve `UUID` o `void`; si devuelve algo mas rico, el record vive en `application/{feature}/command/result/` con sufijo `Result` — nunca anidado en la interfaz `UseCase` ni en `model/`, que es solo entrada |
| Reglas de dominio | `{Regla}Rule` (extiende `shared.rules.DomainRule<T>`) en `domain/{feature}/rules/` con su `{Regla}RuleImpl` en `rules/impl/`: POJO sin Spring, sin Lombok y **sin ninguna dependencia de constructor** — `validar(T)` es una decisión pura sobre un record de `domain/{feature}/model/`, no consulta nada, solo lanza. Se registran como bean no-arg en `{Contexto}DomainRulesConfig` de infrastructure (e.g. `FichasDomainRulesConfig`, `UsuariosDomainRulesConfig`) |
| Finders | Toda consulta que necesita una regla es un `{Concepto}Finder` (extiende `shared.rules.Finder<T,R>`) en `application/{feature}/command/finder/` + `{Concepto}FinderImpl` `@Component` en `impl/` que delega en el `OutputPort`. **Siempre devuelve valor, nunca lanza por "no encontrado"**: `Boolean`, `Long` u `Optional`. Decidir qué significa la ausencia es trabajo de la `Rule` |
| Validators | Interfaz `{Accion}{Entidad}Validator` (`application/{feature}/command/validator/`) + `{Accion}{Entidad}ValidatorImpl` `@Component` en `impl/`. Se nombra por la **acción**, no por la entidad. Es **puro**: solo inyecta `Rule` de dominio, jamás un `OutputPort` ni un `Finder`, y **no contiene un solo `if`** — recibe el dato ya consultado y orquesta las `Rule` en orden |
| Objetos de acción de dominio | Cuando la acción no mapea al agregado raíz sino al conjunto que arrastra, se declara `{Accion}{Entidad}Domain` en `domain/{feature}/` (junto al agregado, sin subcarpeta), nombrado por nominalización del verbo: `RegistroFichaPerfilDomain`, `CambioAsesorFichaDomain`, `AgregacionItemFichaPerfilDomain`, `RemocionEstudianteFichaPerfilDomain`. Lo construye `{Accion}{Entidad}Mapper` de `command/primaryport/mapper/` y lo recibe el `UseCase` |
| Orden de validación | 1) integridad del dato (formato, obligatoriedad, longitud, duplicados en la petición), 2) existencia/unicidad en BD, 3) reglas de negocio del agregado |
| Identificadores en el body | Se reciben como `String`, nunca tipados `UUID` en el DTO. Regla general para todo contexto: el formato **nunca** se valida con una anotación Jakarta — ni propia ni de librería (`shared:web` ya no ofrece `@UuidValido`, se eliminó por esto); siempre en el `Command`, vía `ValidatorUUID.uuidValido(...)` (`shared:validation`), llamado desde `{Command}.crear(...)` o desde `toCommand()` según la convención de DTO del contexto. Convierte con `UtilUUID.generarUUIDDesdeTexto` |
| Nombres del contrato | Objetuales: `asesorFicha`, `estudiantes` — no `asesorFichaId`, `estudiantesIds` |
| Literales en adapters | `ApiCodes` (códigos HTTP), `FichasApiKeys` (textos Swagger), `FichasAuthorities` (authorities), `FichasRoutes` (rutas) — nada quemado inline |
| Mensajes y logs | `CatalogoMensajes` inyectado (dominio: fachada estática `Mensajes`); textos en `shared:message/resources/messages/*.properties` con clave `contexto.capa.objeto.tipo.descripcion` |

## Outbox Pattern — Spring Modulith 2.0.0

Los eventos de dominio se publican usando el **Event Publication Registry** de Spring Modulith, que garantiza atomicidad entre el `save` del aggregate y la publicación al broker.

### Flujo obligatorio en interactor + use case que publican eventos

La transacción **ya no se maneja a nivel de use case — vive en la implementación del interactor**. El interactor solo delega; el use case hace el trabajo real (crear el dominio, persistir, drenar y publicar eventos) sin su propia anotación `@Transactional`:

```java
// {Accion}{Entidad}InteractorImpl — dueño de la transacción, delega y nada más
@Component
@RequiredArgsConstructor
public class CrearXxxInteractorImpl implements CrearXxxInteractor {

    private final CrearXxxUseCase crearXxxUseCase;

    @Override
    @Transactional(transactionManager = "xxxTransactionManager")  // qualifier explícito obligatorio
    public UUID ejecutar(CrearXxxCommand command) {
        return crearXxxUseCase.ejecutar(command);
    }
}
```

```java
// {Accion}{Entidad}UseCaseImpl — sin @Transactional; corre dentro de la transacción abierta por el interactor
@Component
@RequiredArgsConstructor
public class CrearXxxUseCaseImpl implements CrearXxxUseCase {

    private final XxxOutputPort xxxOutputPort;
    private final EventPublisher eventPublisher;

    @Override
    public UUID ejecutar(CrearXxxCommand command) {
        XxxDomain xxx = XxxDomain.crear(...);
        xxxOutputPort.save(xxx);
        xxx.extraerEventosSinPublicar().forEach(eventPublisher::publish);
        return xxx.getId();
    }
}
```

- `eventPublisher` es `SpringModulithEventPublisher` (en `shared/amqp`), que delega a `ApplicationEventPublisher`.
- Spring Modulith intercepta el `publicarEvento` y persiste el evento en la tabla `event_publication` de la BD del propio contexto **dentro de la misma transacción** — atomicidad garantizada.
- Tras el commit, lo publica a RabbitMQ con el `temaEvento` como routing key.

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

### SeguridadConfig (`seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/config/security/SeguridadConfig.java`)

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

### LimiteSolicitudesConfig (`seguridad/infrastructure/.../config/ratelimit/LimiteSolicitudesConfig.java`)

- Usa **Bucket4j** (`com.bucket4j:bucket4j_jdk17-core:8.18.0`) con `RedisBucketResolver` (Lettuce) — buckets en Redis, no en memoria; `max-tracked-ips` se eliminó al migrar de `ConcurrentHashMap` a Redis
- Propiedades: `security.rate-limit.enabled`, `requests-per-minute`, `login-requests-per-minute` — sin default global, cada perfil los fija explícitamente
- `application-dev.yml`: deshabilitado (`enabled: false`), 100/min global, 5/min login
- **`application-prod.yml`: habilitado, 60/min global, 3/min login**
- Si Redis falla al resolver el bucket, `RedisBucketResolver` responde **fail-closed**: devuelve un bucket ya agotado (`createExhaustedBucket()`) en vez de dejar pasar la petición o propagar la excepción

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
| `SeguridadConfig.java` | `/swagger-ui/**`, `/v3/api-docs/**` son permit-all |
| `TrazabilidadFilter.java` (`shared:web`) | Paths de Swagger/actuator excluidos del log de auditoría (`arquisoft.trazas.rutas-excluidas-auditoria`) |

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

| Contexto Gradle | Schema PostgreSQL |
|---|---|
| `seguridad` | *(sin BD propia — auth vía Keycloak + Redis)* |
| `usuarios` | `usuarios` |
| `fichas` | `fichas_perfil` |
| `notificaciones` | `notificaciones` |
| `proyectos` | `proyectos_grado` |
| `artefactos`, `repositorio_artefactos`, `entregables`, `evaluaciones` | igual al nombre del contexto |

Migraciones Flyway en `{contexto}/infrastructure/src/main/resources/db/migration/`.

## Estilo de Código

### Nomenclatura

| Elemento | Ejemplo |
|---|---|
| Puertos de salida | `FichaPerfilOutputPort`, `AsesorFichaOutputPort` |
| Adapters de salida | `KeycloakAuthOutputAdapter`, `FichaPerfilCommandOutputAdapter` |
| Controllers | `RegistrarFichaPerfilController`, `ConsultarFichasPerfilController` — **uno por acción** |
| DTOs | `RegistrarFichaPerfilRequestDTO`, `RegistrarFichaPerfilResponseDTO`, `FichaPerfilResponseDTO` |
| Mappers (todos `final`, constructor privado, `static`, **no son beans**) | `RegistrarFichaPerfilRequestMapper` (DTO→Command), `RegistrarFichaPerfilMapper` (Command→dominio), `FichaPerfilMapper` (Entity↔Domain), `FichaPerfilJpaMapper` (Entity↔JpaEntity), `FichaPerfilQueryMapper` (JpaQueryEntity→ReadModel), `FichaPerfilResponseMapper` (ReadModel→DTO) |
| Excepciones | `FichaPerfilNoEncontradaException` |
| Métodos de test | `debeHacerAlgo_cuandoCondicion` |

**Desviaciones que existen hoy y no se deben copiar:** `NotificacionValidator` (debería nombrarse por la acción), `AutenticacionCommandController` y `UsuarioCommandController` (agregan varios endpoints en vez de uno por acción), `EstadoEvaluacionCommandRepository` (código muerto), `fichas/application/usuario` (stub sin `Interactor`), `UsuarioOutputPort`/`UsuarioCommandOutputAdapter` de `usuarios` (el puerto habla `UsuarioDomain` en vez de `Entity`, y el adapter es un mock que no persiste), y `@Slf4j` en `seguridad`/`usuarios` en vez del puerto `AppLogger`. La ubicación de los enums de catálogo (`domain/{catalogo}/` vs `domain/{feature}/model/`) es una **decisión abierta**: sigue la que ya use el contexto que estés tocando. Detalle completo en [docs/ARQUITECTURA_Y_ESTRUCTURA.md](docs/ARQUITECTURA_Y_ESTRUCTURA.md#desviaciones-conocidas-respecto-a-la-convención).

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

- `TrazabilidadFilter` (`shared:web`, orden -300) reutiliza el header `X-Correlation-Id` entrante **verbatim**, cae al trace-id de `traceparent` (W3C) y solo si no hay ninguno genera uno de 32 hex. Genera además un `transaccionId` nuevo por salto. El contexto lo gobierna `shared:tracing` vía `GestorTraza`/`AlcanceTraza`
- El id resuelto va al MDC, se devuelve en el header `X-Correlation-Id` de la respuesta y viaja como `traceId` en `ErrorResponseDTO`, de modo que un error reportado se puede reconstruir desde los logs
- El MDC no cruza hilos por sí solo: la externalización de eventos a RabbitMQ de Spring Modulith corre en un `@Async` propio (el `applicationTaskExecutor` autoconfigurado), distinto del hilo que abrió el `AlcanceTraza`. `MdcTaskDecorator` (`shared:tracing/infrastructure/traza/config/`, registrado como `@Bean TaskDecorator` en `TrazabilidadConfig`) copia el MDC del hilo que encola la tarea al hilo que la ejecuta, para que `TrazaMessagePostProcessor` inyecte el `correlacionId`/`transaccionId`/`usuarioId` reales en el mensaje AMQP en vez de generar unos nuevos

## Testing

- Stack: JUnit 6 + Mockito + AssertJ. H2 para repositorios. `spring-security-test` para controllers.
- Tests unitarios: `@ExtendWith(MockitoExtension.class)`, sin contexto Spring
- Tests de repositorio (slice): `@DataJpaTest` con H2 — **`@SpringBootTest` no se usa en ningún test de este repositorio**
- Tests de controller (slice): `@WebMvcTest` + `@Import(GlobalAppExceptionHandler.class)`, autenticando con `SecurityMockMvcRequestPostProcessors.jwt().authorities(...)`
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
