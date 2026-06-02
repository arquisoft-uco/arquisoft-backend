# PLAN: Consultar información de una Ficha Perfil generada por el Coordinador

## Metadata
- **ID Historia:** HU-160
- **Bounded Context:** `fichas`
- **Tipo de Use Case:** Consulta — listado paginado, sin modificación de estado, sin eventos
- **¿Usa AggregateRoot?:** Sí — `FichaPerfil` extiende `AggregateRoot` de `shared:domain`. Esta es la primera HU del contexto `fichas`, por lo que se crea la entidad raíz completa (incluyendo `FichaPerfilCreadaEvent`) aunque el use case actual no emita eventos. El evento queda disponible para futuras HUs de escritura.
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-05-03
- **Rama sugerida:** `feature/HU-160-consultar_fichas_perfil_coordinador`
- **Fuentes consultadas del repo de documentación:**
  - `.workspace/hu/HU-160.md` (proporcionado directamente por el usuario)
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** Ninguna

---

## 1. Resumen Funcional

HU-160 expone un endpoint `GET /fichas-perfil/coordinador` que devuelve un listado paginado de todas las fichas de perfil registradas en el sistema, incluyendo para cada una su título y los datos del asesor asignado (nombre y correo). El acceso está restringido al rol `COORDINADOR`. La HU **no** crea, actualiza ni elimina fichas; tampoco emite eventos ni requiere integración con sistemas externos. Al ser la primera HU del contexto `fichas`, se crea la arquitectura completa (dominio, aplicación, infraestructura) desde cero, incluyendo el `AggregateRoot` `FichaPerfil` y el evento `FichaPerfilCreadaEvent` que utilizarán futuras HUs de escritura.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | El coordinador autenticado llama `GET /fichas-perfil/coordinador` | HTTP 200 con body `PageResponseDTO<FichaPerfilResumenDTO>` |
| 2 | La respuesta incluye `id`, `titulo` (alias de `tituloProyecto`) y objeto `asesor` con `id`, `nombre`, `email` | Body con estructura definida en el contrato de API |
| 3 | Paginación por defecto: `page=0`, `size=10` | Si no se pasan query params, se usan los defaults |
| 4 | Si no existen fichas registradas, el listado retorna vacío con `totalElements=0` | HTTP 200 con `content: []` |
| 5 | Un usuario sin rol `COORDINADOR` intenta acceder | HTTP 403 |
| 6 | Solicitud sin JWT | HTTP 401 |
| 7 | Parámetros de paginación inválidos (`page < 0` o `size <= 0`) | HTTP 400 |

---

## 3. Reglas de Negocio

- **FichaPerfil-POL-01:** Solo el rol `COORDINADOR` puede consultar el listado completo de fichas de perfil. No hay filtro por coordinador — la visibilidad es total sobre todas las fichas del sistema.
- El resultado se devuelve paginado con defaults `page=0`, `size=10`.
- Cada ficha del listado expone su `tituloProyecto` bajo el alias `titulo` en la capa de presentación.
- Cada ficha incluye su `AsesorFicha` con `id`, `nombre` y `email` para identificación rápida.
- `AsesorFicha` es una entidad gestionada directamente en el contexto `fichas` (no es réplica de otro contexto).

---

## 4. Modelo DDD del Contexto

### Aggregate Root

- **Entidad raíz:** `FichaPerfil` (extiende `AggregateRoot` de `com.arquisoft.shared.domain`)
- **ID:** `UUID`
- **Entidades hijas:** `AsesorFicha` (entidad con identidad propia, gestionada en `fichas`)
- **Value Objects:** ninguno adicional para esta HU
- **Enums / Sealed types:** ninguno para esta HU

### Atributos por objeto de dominio (extraídos del modelo enriquecido)

#### `FichaPerfil`

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí (`UUID.randomUUID()` en `build`) | Identifica el agregado |
| `tituloProyecto` | `String` | 1–100 | Sí | TBD futura HU | No | Limpiar espacios en `build`; alias `titulo` en DTO |
| `asesorFicha` | `AsesorFicha` | — | Sí | TBD futura HU | No | Entidad hija, relación N:1 |

**Combinaciones únicas (Restricciones):**
- `tituloProyecto` debe ser único en el sistema → `UNIQUE` en Flyway + constraint en tabla `ficha_perfil`.

#### `AsesorFicha`

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Depende de la HU de creación | Identidad del asesor |
| `nombre` | `String` | 1–50 | Sí | TBD futura HU | No | Nombre completo del asesor |
| `email` | `String` | 1–50 | Sí | TBD futura HU | No | Correo institucional |

### Traducción del modelo enriquecido a código

| Característica del modelo | Traducción en código |
|---|---|
| `tituloProyecto` longitud 1–100 | `@Size(max=100)` en DTO + `@Column(length=100)` en JPA + `VARCHAR(100)` en Flyway + validación en `build(...)` |
| `tituloProyecto` obligatorio | `@NotBlank` en DTO + `@Column(nullable=false)` en JPA + `NOT NULL` en Flyway + validación en `build(...)` |
| `tituloProyecto` único | `UNIQUE` constraint en Flyway (columna `titulo_proyecto`) |
| `tituloProyecto` limpiar espacios | `.trim()` dentro de `build(...)` antes de validar |
| `nombre` / `email` obligatorios | `NOT NULL` en Flyway + `@Column(nullable=false)` en JPA |
| `asesorFichaId` FK | `@ManyToOne(fetch = FetchType.EAGER)` en `FichaPerfilJpaEntity` + FK en Flyway |
| ID autogenerado | `UUID.randomUUID()` en `build(...)` |

### Eventos de Dominio que emite

> Aunque HU-160 es de consulta (no llama a `build`), el evento se define aquí porque esta es la primera HU del contexto `fichas`. Quedará disponible para las futuras HUs de escritura que creen fichas.

| Evento | Clase | Routing Key RabbitMQ (futuro) | Cuándo se emite |
|---|---|---|---|
| Ficha de perfil creada | `FichaPerfilCreadaEvent` (extiende `DomainEvent`) | `fichas.ficha_perfil.creada` | En `FichaPerfil.build(...)` — futuras HUs de escritura |

> **Regla:** el dominio acumula eventos con `publishEvent(...)`. El use case los drena con `getUnPublishedEvents()` tras persistir, los publica vía `EventPublisher` (shared:amqp) y llama a `clearUnPublishedEvents()`. HU-160 (consulta) no realiza este ciclo.

---

## 5. Integraciones Externas

No aplica. HU-160 solo accede a PostgreSQL (base de datos `fichas_perfil` del contexto `fichas`). No requiere Keycloak admin API, SMTP, S3, Redis con lógica propia ni servicios HTTP externos.

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/FichaPerfil.java` | Entidad (Aggregate Root) | Entidad raíz del contexto fichas. Campos `id`, `tituloProyecto`, `asesorFicha`. Métodos `build(...)` (genera UUID + publica `FichaPerfilCreadaEvent`) y `rebuild(...)` (reconstrye desde BD sin evento). Extiende `AggregateRoot`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/AsesorFicha.java` | Entidad hija | Entidad con identidad propia (no AggregateRoot). Campos `id`, `nombre`, `email`. Factory `of(UUID, String, String)`. Java puro, sin Spring/Lombok. |
| domain | `shared:domain` → `com.arquisoft.shared.domain.Page<T>` *(no se crea archivo local)* | Value Object (record genérico, compartido) | Se usa el record `Page<T>` de `shared:domain` en lugar de crear `PaginaResultado.java` local. Provee `content: List<T>`, `totalElements: long`, `page: int`, `size: int` y `totalPages()`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/event/FichaPerfilCreadaEvent.java` | Evento de dominio | Extiende `DomainEvent`. Constructor recibe `aggregateId` y `tituloProyecto`. Emitido desde `FichaPerfil.build(...)` en futuras HUs de escritura. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/in/ConsultarFichasPerfilUseCase.java` | Interface (puerto de entrada) | Contrato del caso de uso de consulta. Método: `PaginaResultado<FichaPerfil> ejecutar(int pagina, int tamanio)`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/FichaPerfilRepositoryPort.java` | Interface (puerto de salida) | Contrato del repositorio. Método: `PaginaResultado<FichaPerfil> consultarTodas(int pagina, int tamanio)`. **Retorna entidades de dominio, NUNCA DTOs.** |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/dto/AsesorResumenDTO.java` | DTO | DTO anidado para asesor en la respuesta: `id (UUID)`, `nombre (String)`, `email (String)`. Factory `fromDomain(AsesorFicha)`. Anotaciones Lombok. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/dto/FichaPerfilResumenDTO.java` | DTO de respuesta | ReadModel de la HU: `id (UUID)`, `titulo (String)` — alias de `tituloProyecto`, `asesor (AsesorResumenDTO)`. Factory `fromDomain(FichaPerfil)`. Anotaciones Lombok + `@JsonInclude`. |
| application | `shared:web` → `com.arquisoft.shared.web.PageResponseDTO` *(no se crea archivo local)* | DTO de paginación (compartido) | Se usa el `PageResponseDTO` genérico de `shared:web`. Campos en inglés: `content`, `totalElements`, `totalPages`, `page`, `size`. Factory estático `fromPage(Page<T>, Function<T,R>)`. |
| application | `shared:web` → `com.arquisoft.shared.web.ErrorResponseDTO` *(no se crea archivo local)* | DTO de error (compartido) | Se usa el `ErrorResponseDTO` genérico de `shared:web`. Campos: `error`, `errorCode`, `message`, `status`, `path`. Usado por `FichasGlobalExceptionHandler` y por `GlobalAppExceptionHandler`. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/usecase/ConsultarFichasPerfilUseCaseImpl.java` | UseCase Impl | Implementa `ConsultarFichasPerfilUseCase`. Llama a `repositoryPort.consultarTodas(pagina, tamanio)` → `PaginaResultado<FichaPerfil>`. **No drena eventos** (HU de consulta). Anotaciones `@Component`, `@RequiredArgsConstructor`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichaPerfilController.java` | Controller REST | `@RestController`, `@RequestMapping("/fichas-perfil")`. Expone `GET /coordinador`. Recibe `@RequestParam` `page` y `size`. Llama al use case y convierte `PaginaResultado<FichaPerfil>` a `PageResponseDTO<FichaPerfilResumenDTO>`. Anotaciones ADR-011: `@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement(name="bearerAuth")`. `@PreAuthorize("hasRole('COORDINADOR')")`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichasGlobalExceptionHandler.java` | Exception Handler | `@RestControllerAdvice(basePackages="com.arquisoft.fichas")`. Primer handler del contexto. Maneja excepciones de dominio propias de `fichas`: `DomainException` → 422. Las excepciones genéricas (`ConstraintViolationException` → 400, `Exception` → 500) son manejadas por `GlobalAppExceptionHandler` en `shared:web`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/AsesorFichaJpaEntity.java` | JPA Entity | `@Entity @Table(name = "asesor_ficha")`. Campos: `id (UUID)`, `nombre (String, length=50)`, `email (String, length=50)`. Sin atributo `schema`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilJpaEntity.java` | JPA Entity | `@Entity @Table(name = "ficha_perfil")`. Campos: `id (UUID)`, `tituloProyecto (String, length=100, unique)`, `asesorFicha (AsesorFichaJpaEntity, @ManyToOne EAGER)`. Sin atributo `schema`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilJpaRepository.java` | JPA Repository | Extiende `JpaRepository<FichaPerfilJpaEntity, UUID>`. Usa el método heredado `findAll(Pageable pageable)` de Spring Data. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilRepositoryAdapter.java` | Repository Adapter | Implementa `FichaPerfilRepositoryPort`. Recibe `int pagina, int tamanio` → construye `PageRequest.of(pagina, tamanio)` → llama `jpaRepository.findAll(pageable)`. **Convierte `FichaPerfilJpaEntity` → `FichaPerfil` usando `FichaPerfil.rebuild(...)`**. Nunca convierte JPA Entity → DTO directamente. |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/V1.0__crear_tablas_fichas_perfil.sql` | Flyway | Crea tablas `asesor_ficha` y `ficha_perfil` en la BD `fichas_perfil`. Sin prefijo de schema en SQL. |

> ## ⚠️ Regla DDD inviolable: el flujo de datos siempre pasa por el dominio
>
> ```
> FichaPerfilJpaEntity  →  FichaPerfil (dominio, vía rebuild)  →  FichaPerfilResumenDTO
>          ↑                        ↑                                        ↑
>       adapter             adapter (rebuild)                          controller
> ```
>
> **Saltarse el dominio en consultas (JPA Entity → DTO directamente) es una violación de DDD estricto.** El adapter convierte JPA Entity → `FichaPerfil` via `rebuild`. El controller convierte `FichaPerfil` → `FichaPerfilResumenDTO` via `FichaPerfilResumenDTO.fromDomain(...)`.

### Manejo de Errores HTTP (`@ExceptionHandler`) — OBLIGATORIO

> Esta es la primera excepción handler del contexto `fichas`. Se crea el `GlobalExceptionHandler` completo.

| Capa | Ruta | Tipo | Acción | Mapeo HTTP |
|------|------|------|--------|------------|
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichasGlobalExceptionHandler.java` | Handler | CREAR (primer handler del contexto fichas) | `DomainException` → 422. `ConstraintViolationException` → 400 y `Exception` → 500 delegados a `GlobalAppExceptionHandler` en `shared:web` |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/config/FichasDataSourceConfig.java` | Agregar `@EnableJpaRepositories` con `entityManagerFactoryRef="fichasEntityManagerFactory"` y `transactionManagerRef="fichasTransactionManager"`. Agregar beans: `LocalContainerEntityManagerFactoryBean fichasEntityManagerFactory()` (escaneando paquete `com.arquisoft.fichas.infrastructure.adapter.out.persistence` para entities) y `JpaTransactionManager fichasTransactionManager()`. Agregar bean `Flyway fichasFlyway()` apuntando a `fichasDataSource` con location `classpath:db/migration`. |

### Archivos de MENSAJERÍA RabbitMQ

No aplica en HU-160 (use case de consulta, no publica eventos).

---

## 7. Detalle por Archivo

### `FichaPerfil.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad (Aggregate Root)
- **Responsabilidad:** Entidad raíz del bounded context `fichas`. Representa una ficha de perfil de proyecto de grado con su asesor asignado. Implementa el ciclo de vida completo (build/rebuild) para garantizar la integridad del dominio.
- **Features Java 21 aplicables:** ninguna especial para esta clase (entidad clásica con campos `final` y constructor privado).
- **Métodos principales:**
  - `build(String tituloProyecto, AsesorFicha asesorFicha): FichaPerfil` — genera `UUID.randomUUID()`, valida `tituloProyecto` (no nulo, no vacío, máx 100 chars tras `.trim()`), publica `FichaPerfilCreadaEvent`. **Para futuras HUs de escritura.**
  - `rebuild(UUID id, String tituloProyecto, AsesorFicha asesorFicha): FichaPerfil` — reconstruye desde persistencia sin emitir evento. **Usado por el adapter en HU-160.**
  - `getId(): UUID`, `getTituloProyecto(): String`, `getAsesorFicha(): AsesorFicha` — getters sin lógica.
- **Dependencias:** `AggregateRoot` (shared:domain), `FichaPerfilCreadaEvent`, `AsesorFicha`.
- **Invariantes en `build`:** `tituloProyecto != null && !blank && length <= 100`.

### `AsesorFicha.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad hija (no AggregateRoot)
- **Responsabilidad:** Representa al asesor de una ficha de perfil con su identificación (`id`, `nombre`, `email`). Gestionado directamente en el contexto `fichas`.
- **Features Java 21 aplicables:** podría ser un `record` si fuera Value Object, pero tiene identidad (`id`) → clase con constructor privado y factory.
- **Métodos principales:**
  - `of(UUID id, String nombre, String email): AsesorFicha` — factory estático, sin validación adicional (datos ya validados en la capa de persistencia/creación).
  - `getId(): UUID`, `getNombre(): String`, `getEmail(): String` — getters.
- **Dependencias:** solo `java.util.UUID`.

### `PaginaResultado.java` — ⚠️ NO SE CREA (reemplazado por `shared:domain.Page<T>`)
> Se usa el record `Page<T>` de `com.arquisoft.shared.domain` para evitar duplicar lógica de paginación. El puerto `FichaPerfilRepositoryPort` y el use case `ConsultarFichasPerfilUseCase` tipan su retorno como `Page<FichaPerfil>` (de shared:domain).

### `FichaPerfilCreadaEvent.java`
- **Paquete:** `com.arquisoft.fichas.domain.event`
- **Tipo:** Evento de dominio
- **Responsabilidad:** Evento emitido cuando se crea una nueva `FichaPerfil`. Definido aquí aunque HU-160 no lo emita — esta es la primera HU del contexto y el dominio debe quedar completo.
- **Métodos principales:**
  - `FichaPerfilCreadaEvent(String aggregateId, String tituloProyecto)` — llama a `super(aggregateId)`, guarda `tituloProyecto`.
  - `getTituloProyecto(): String`.
- **Dependencias:** `DomainEvent` (shared:domain).

### `ConsultarFichasPerfilUseCase.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.in`
- **Tipo:** Interface (puerto de entrada)
- **Responsabilidad:** Contrato del caso de uso de consulta paginada de fichas de perfil.
- **Métodos principales:**
  - `ejecutar(int page, int size): Page<FichaPerfil>` — retorna entidades de dominio, no DTOs. Usa `Page<T>` de `shared:domain`.
- **Dependencias:** `FichaPerfil`, `Page` (shared:domain).

### `FichaPerfilRepositoryPort.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.out`
- **Tipo:** Interface (puerto de salida)
- **Responsabilidad:** Contrato abstracto del repositorio. La implementación concreta vive en la capa infrastructure. Retorna entidades de dominio, nunca DTOs de aplicación.
- **Métodos principales:**
  - `consultarPaginadas(int page, int size): Page<FichaPerfil>` — usa `Page<T>` de `shared:domain`.
- **Dependencias:** `FichaPerfil`, `Page` (shared:domain).

### `AsesorResumenDTO.java`
- **Paquete:** `com.arquisoft.fichas.application.dto`
- **Tipo:** DTO de respuesta (anidado)
- **Responsabilidad:** Representación de `AsesorFicha` en la respuesta JSON. Campos: `id (UUID)`, `nombre (String)`, `email (String)`.
- **Métodos principales:**
  - `fromDomain(AsesorFicha asesor): AsesorResumenDTO` — factory estático.
- **Dependencias:** `AsesorFicha` (domain), Lombok (`@Data @Builder @NoArgsConstructor @AllArgsConstructor`).

### `FichaPerfilResumenDTO.java`
- **Paquete:** `com.arquisoft.fichas.application.dto`
- **Tipo:** DTO de respuesta (ReadModel)
- **Responsabilidad:** Proyección de `FichaPerfil` para el listado. Campos: `id (UUID)`, `titulo (String)` — alias de `tituloProyecto`, `asesor (AsesorResumenDTO)`.
- **Métodos principales:**
  - `fromDomain(FichaPerfil ficha): FichaPerfilResumenDTO` — mapea `ficha.getTituloProyecto()` al campo `titulo`.
- **Dependencias:** `FichaPerfil`, `AsesorFicha`, `AsesorResumenDTO`, Lombok, `@JsonInclude`.

### `PageResponseDTO.java` — ⚠️ NO SE CREA localmente (usa `shared:web.PageResponseDTO`)
> Se usa el `PageResponseDTO` genérico de `com.arquisoft.shared.web`. Campos en inglés: `content`, `totalElements`, `totalPages`, `page`, `size`.

### `ErrorResponseDTO.java` — ⚠️ NO SE CREA localmente (usa `shared:web.ErrorResponseDTO`)
> Se usa el `ErrorResponseDTO` genérico de `com.arquisoft.shared.web`. Usado por `FichasGlobalExceptionHandler` y por `GlobalAppExceptionHandler`.

### `ConsultarFichasPerfilUseCaseImpl.java`
- **Paquete:** `com.arquisoft.fichas.application.usecase`
- **Tipo:** UseCase Impl
- **Responsabilidad:** Implementa `ConsultarFichasPerfilUseCase`. Orquesta la consulta paginada de fichas de perfil. **No drena ni publica eventos** (use case de consulta).
- **Features Java 21 aplicables:** `var` para variables locales evidentes. Stream con `.map()` si se añade transformación futura.
- **Métodos principales:**
  - `ejecutar(int page, int size): Page<FichaPerfil>` — delega a `fichaPerfilRepositoryPort.consultarPaginadas(page, size)` y retorna el resultado directamente.
- **Dependencias:** `FichaPerfilRepositoryPort`, `ConsultarFichasPerfilUseCase`, Lombok (`@RequiredArgsConstructor`), `@Component`, `@Transactional(readOnly=true)`.

### `FichaPerfilController.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.in.web`
- **Tipo:** Controller REST
- **Responsabilidad:** Recibe `GET /fichas-perfil/coordinador`, valida parámetros de paginación, llama al use case, convierte el resultado a `PageResponseDTO<FichaPerfilResumenDTO>` y devuelve HTTP 200.
- **`@Tag`:** `name = "Fichas Perfil"`, `description = "Gestión de fichas de perfil de proyectos de grado"`
- **Endpoints documentados:**

  | Método | `@Operation(summary)` | Códigos `@ApiResponse` | `@SecurityRequirement` |
  |--------|----------------------|------------------------|------------------------|
  | `consultarFichasCoordinador` | `"Listar fichas de perfil paginadas"` | 200, 400, 401, 403 | `bearerAuth` |

- **Métodos principales:**
  - `consultarFichasCoordinador(@RequestParam(defaultValue="0") @Min(0) int page, @RequestParam(defaultValue="10") @Positive int size): PageResponseDTO<FichaPerfilResumenDTO>` — anotado con `@GetMapping("/coordinador")` y `@ResponseStatus(HttpStatus.OK)`.
- **Dependencias:** `ConsultarFichasPerfilUseCase`, `FichaPerfilResumenDTO`, `PageResponseDTO`, Lombok (`@RequiredArgsConstructor`, `@Slf4j`), Spring MVC, Spring Security (`@PreAuthorize`), springdoc-openapi.
- **Nota:** el controller usa `@Validated` a nivel de clase para activar la validación de `@Min` y `@Positive` en `@RequestParam`. El `GlobalExceptionHandler` captura `ConstraintViolationException` → 400.

### `FichasGlobalExceptionHandler.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.in.web`
- **Tipo:** Exception Handler (`@RestControllerAdvice`)
- **Responsabilidad:** Primer handler del contexto `fichas`. Mapea excepciones de dominio propias de fichas a respuestas HTTP. Alcance limitado al paquete `com.arquisoft.fichas`.
- **Métodos principales:**
  - `handleDomainGeneric(DomainException ex, HttpServletRequest req): ResponseEntity<ErrorResponseDTO>` → 422.
- **Nota:** `ConstraintViolationException` → 400 y `Exception` → 500 son manejados por `GlobalAppExceptionHandler` en `shared:web`.
- **Dependencias:** `ErrorResponseDTO` (shared:web), `DomainException` (shared:exceptions), Lombok (`@Slf4j`), Spring.

### `AsesorFichaJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Representación JPA de la tabla `asesor_ficha`. Campos: `id (UUID, @Id)`, `nombre (String, length=50, nullable=false)`, `email (String, length=50, nullable=false)`.
- **Anotaciones:** `@Entity @Table(name = "asesor_ficha")` — **sin atributo `schema`**.
- **Dependencias:** `jakarta.persistence.*`, Lombok (`@Data @NoArgsConstructor @AllArgsConstructor @Builder`).

### `FichaPerfilJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Representación JPA de la tabla `ficha_perfil`. Campos: `id (UUID, @Id)`, `tituloProyecto (String, length=100, nullable=false, unique=true)`, `asesorFicha (@ManyToOne(fetch=EAGER) AsesorFichaJpaEntity)`.
- **Anotaciones:** `@Entity @Table(name = "ficha_perfil")` — **sin atributo `schema`**. `@JoinColumn(name = "asesor_ficha_id", nullable=false)` en la relación.
- **Dependencias:** `AsesorFichaJpaEntity`, `jakarta.persistence.*`, Lombok.

### `FichaPerfilJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Repository (Spring Data)
- **Responsabilidad:** Acceso a datos para `FichaPerfilJpaEntity`. Hereda `findAll(Pageable pageable): Page<FichaPerfilJpaEntity>` de `JpaRepository`.
- **Dependencias:** `JpaRepository<FichaPerfilJpaEntity, UUID>`, Spring Data JPA.

### `FichaPerfilRepositoryAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** Repository Adapter
- **Responsabilidad:** Implementa `FichaPerfilRepositoryPort`. Traduce del mundo JPA al dominio. Convierte `int page, int size` → `PageRequest.of(page, size)`. Llama a `jpaRepository.findAll(pageable)`. **Por cada `FichaPerfilJpaEntity` llama a `FichaPerfil.rebuild(entity.getId(), entity.getTituloProyecto(), AsesorFicha.of(entity.getAsesorFicha().getId(), entity.getAsesorFicha().getNombre(), entity.getAsesorFicha().getEmail()))`.** Construye `Page<FichaPerfil>` (shared:domain) con el contenido y la metadata de paginación de Spring's `Page`.
- **Dependencias:** `FichaPerfilRepositoryPort`, `FichaPerfilJpaRepository`, `FichaPerfil`, `AsesorFicha`, `PaginaResultado`, Spring Data JPA (`Pageable`, `PageRequest`), Lombok (`@RequiredArgsConstructor`), `@Component`.

### `V1.0__crear_tablas_fichas_perfil.sql`
- **Ruta:** `fichas/infrastructure/src/main/resources/db/migration/`
- **Base de datos:** `fichas_perfil` (según tabla de mapeo del skill: `fichas` → `fichas_perfil`).
- **Contenido:** crea primero `asesor_ficha` (tabla referenciada) y luego `ficha_perfil` con FK. Sin prefijo de schema en el SQL.

### `FichasDataSourceConfig.java` (MODIFICAR)
- **Ruta:** `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/config/FichasDataSourceConfig.java`
- **Cambios requeridos:**
  1. Agregar `@EnableJpaRepositories(basePackages = "com.arquisoft.fichas.infrastructure.adapter.out.persistence", entityManagerFactoryRef = "fichasEntityManagerFactory", transactionManagerRef = "fichasTransactionManager")` a nivel de clase.
  2. Agregar bean `LocalContainerEntityManagerFactoryBean fichasEntityManagerFactory(EntityManagerFactoryBuilder builder)` que use el `fichasDataSource()` y escanee el paquete de entities `com.arquisoft.fichas.infrastructure.adapter.out.persistence`.
  3. Agregar bean `JpaTransactionManager fichasTransactionManager(EntityManagerFactory fichasEntityManagerFactory)`.
  4. Agregar bean `Flyway fichasFlyway()` con datasource `fichasDataSource()`, location `classpath:db/migration`, y `baselineOnMigrate=true`.
- **Nota:** como la app principal tiene múltiples datasources, la anotación `@Primary` NO debe usarse en los beans de `fichas` — se usa en el datasource principal (seguridad o el definido como primario).

---

## 8. Endpoints REST

| Método | Ruta | Request Body | Response | Código HTTP | Roles permitidos | Anotaciones Swagger (ADR-011) |
|--------|------|--------------|----------|-------------|-----------------|-------------------------------|
| GET | `/fichas-perfil/coordinador` | — | `PageResponseDTO<FichaPerfilResumenDTO>` | 200 | `COORDINADOR` | `@Operation(summary="Listar fichas de perfil paginadas")` + `@SecurityRequirement(name="bearerAuth")` |

### Query Parameters

| Parámetro | Tipo | Default | Validación | Error |
|-----------|------|---------|------------|-------|
| `page` | `int` | `0` | `@Min(0)` | 400 si negativo |
| `size` | `int` | `10` | `@Positive` | 400 si ≤ 0 |

### Response Body (200 OK)

```json
{
  "content": [
    {
      "id": "10c355a4-56d0-484e-aa0d-8823e0a782cf",
      "titulo": "Arquisoft",
      "asesor": {
        "id": "98e9a2ec-0d93-420e-b5f5-81bd3b444b92",
        "nombre": "Juan Esteban Salazar Ramirez",
        "email": "juan.salazar1234@soyuco.edu.co"
      }
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "page": 0,
  "size": 10
}
```

---

## 9. Eventos RabbitMQ

No aplica. HU-160 es un use case de consulta puro. No se publican eventos. El evento `FichaPerfilCreadaEvent` se define en el dominio para futuras HUs de escritura pero no se usa en este flujo.

---

## 10. Migración de Base de Datos

- **Archivo:** `V1.0__crear_tablas_fichas_perfil.sql`
- **Base de datos:** `fichas_perfil` (mapeo: contexto `fichas` → BD `fichas_perfil`)
- **Sin schemas:** las tablas se crean sin prefijo de schema.

```sql
-- Tabla del asesor (debe crearse primero por la FK en ficha_perfil)
CREATE TABLE asesor_ficha (
    id    UUID         NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    email  VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

-- Tabla principal de fichas de perfil
CREATE TABLE ficha_perfil (
    id               UUID          NOT NULL,
    titulo_proyecto  VARCHAR(100)  NOT NULL,
    asesor_ficha_id  UUID          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_ficha_perfil_titulo UNIQUE (titulo_proyecto),
    CONSTRAINT fk_ficha_perfil_asesor FOREIGN KEY (asesor_ficha_id)
        REFERENCES asesor_ficha(id)
);
```

---

## 11. Casos de Prueba Sugeridos — Caso B (Use Case de CONSULTA)

> ⚠️ Esta HU es de consulta. **No se incluyen tests de ciclo de eventos** (`publishEvent`, `getUnPublishedEvents`, `clearUnPublishedEvents`, `verify(eventPublisher).publish(...)`). No aplican.

### Presupuesto orientativo

HU pequeña (1 endpoint, 1 entidad) → 10–15 tests esperados. Esta HU crea la arquitectura completa del contexto, por lo que incluye algunos tests de dominio adicionales (rebuild/invariantes de la entidad que se crea aquí por primera vez).

---

### Tests capa `domain` (solo porque la entidad se crea en esta HU)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `FichaPerfilTest` | `debeReconstruirSinEventos_cuandoRebuildEsInvocado` | `rebuild(...)` retorna entidad correcta y `getUnPublishedEvents()` está vacío |
| `FichaPerfilTest` | `debeLanzarExcepcion_cuandoTituloProyectoEsNuloOVacioEnBuild` | `build(null, ...)` y `build("", ...)` lanzan excepción — consolidado en un solo test con múltiples asserts |

> No se crea `FichaPerfilCreadaEventTest` — el evento solo hace `super(aggregateId)` + guarda `tituloProyecto`. Sin lógica adicional que testear. Se verifica implícitamente en futuros tests de `build(...)` en HUs de escritura.

---

### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `ConsultarFichasPerfilUseCaseImplTest` | `debeRetornarFichasPaginadas_cuandoExistenFichas` | repositorio retorna fichas → use case retorna `PaginaResultado` con contenido y metadata correctos |
| `ConsultarFichasPerfilUseCaseImplTest` | `debeRetornarVacio_cuandoNoHayFichas` | repositorio retorna lista vacía → `totalElementos=0`, `contenido=[]` |

---

### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `FichaPerfilRepositoryAdapterTest` | `debeRetornarFichasConRebuild_cuandoExistenEnBD` | JPA devuelve entities → adapter usa `FichaPerfil.rebuild(...)` y retorna `PaginaResultado<FichaPerfil>` |
| `FichaPerfilRepositoryAdapterTest` | `debeRetornarVacio_cuandoNoHayFichasEnBD` | JPA devuelve `Page` vacía → `PaginaResultado` con `contenido=[]` |
| `FichaPerfilControllerTest` | `debe200_cuandoConsultaExitosa` | GET con params válidos → 200 + body con estructura correcta |
| `FichaPerfilControllerTest` | `debe400_cuandoParametrosPaginacionInvalidos` | `page=-1` o `size=0` → 400 |
| `FichaPerfilControllerTest` | `debe401_cuandoNoAutenticado` | request sin JWT → 401 |
| `FichaPerfilControllerTest` | `debe403_cuandoRolInsuficiente` | JWT con rol `ASESOR_FICHA` → 403 |

**Total estimado: 10 tests.** Apropiado para HU pequeña de consulta con arquitectura nueva.

---

### Reglas de consolidación aplicadas

- `FichaPerfilTest.debeLanzarExcepcion_cuandoTituloProyectoEsNuloOVacioEnBuild` → consolida null y blank en un solo test (mismo "Act" con variantes de input, mismos asserts).
- No se incluyen tests de getters/setters de Lombok.
- No se incluyen tests de cada validación Jakarta por separado.
- No se incluye `FichaPerfilCreadaEventTest` — excepción sin lógica propia (anti-patrón 6 del skill).

---

## 12. Checklist de Implementación

- [ ] **DDD:** `FichaPerfil` extiende `AggregateRoot` de `com.arquisoft.shared.domain`
- [ ] `FichaPerfil` inmutable: constructor privado, campos `final`, factory methods `build` / `rebuild`, sin Lombok
- [ ] `AsesorFicha` inmutable: constructor privado, campos `final`, factory `of(...)`, sin Lombok
- [ ] `Page<T>` de `shared:domain` usado como envoltorio de paginación en puertos y use cases (no se crea `PaginaResultado.java` local)
- [ ] Evento `FichaPerfilCreadaEvent` en `domain/event/`, extiende `DomainEvent`
- [ ] Factory `build(...)` llama `publishEvent(new FichaPerfilCreadaEvent(id.toString(), tituloProyecto))`
- [ ] IDs siempre `UUID` (nunca `Long` / `Integer`)
- [ ] Puerto de entrada (`ConsultarFichasPerfilUseCase`) definido en `domain/port/in/`
- [ ] Puerto de salida (`FichaPerfilRepositoryPort`) definido en `domain/port/out/`, retorna `Page<FichaPerfil>` (shared:domain) — nunca DTOs
- [ ] **Flujo DDD inviolable:** `FichaPerfilJpaEntity` → `FichaPerfil.rebuild(...)` → `FichaPerfilResumenDTO.fromDomain(...)`. El adapter NUNCA mapea JPA Entity → DTO.
- [ ] DTOs (`FichaPerfilResumenDTO`, `AsesorResumenDTO`) con Lombok + anotaciones Jakarta donde aplica. `PageResponseDTO` y `ErrorResponseDTO` se importan de `shared:web`.
- [ ] Caso de uso `ConsultarFichasPerfilUseCaseImpl` con `@Component`, `@RequiredArgsConstructor`, `@Transactional(readOnly=true)`
- [ ] Controller `FichaPerfilController` con `@Validated` (para activar `@Min`/`@Positive` en `@RequestParam`), `@PreAuthorize("hasRole('COORDINADOR')")`, documentación ADR-011 completa
- [ ] `FichasGlobalExceptionHandler` del contexto `fichas` creado con `@RestControllerAdvice(basePackages="com.arquisoft.fichas")` — maneja `DomainException` → 422. Genéricas delegadas a `GlobalAppExceptionHandler` (shared:web)
- [ ] JPA entities con `@Table(name = "...")` **sin atributo `schema`**
- [ ] `FichasDataSourceConfig` modificado para incluir `@EnableJpaRepositories`, `EntityManagerFactory`, `TransactionManager`, y Flyway
- [ ] Migración Flyway `V1.0__crear_tablas_fichas_perfil.sql` en BD `fichas_perfil`, sin prefijo de schema
- [ ] Tests con patrón AAA (cobertura ≥ 75%)
- [ ] Tests de controller con `@MockitoBean` (Spring Boot 4.x) y `spring-security-test`
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads gestionados por Spring Boot automáticamente)
- [ ] Commit: `feat(fichas): implementar consulta paginada de fichas de perfil para coordinador`

---

## 13. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-05-03 | Build -x test: sin errores |
| Tests      | @tester             | ✅ Completado | 2026-05-04 | 10 tests pasando (3 domain + 2 application + 2 repository + 4 controller) — JaCoCo no configurado en submódulos |
| Validación | @validator-analyze  | ✅ Completado | 2026-05-04 | Score: 98/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-05-04 | /.workspace/validator/validator-HU-160.md |
| Commit     | @commit             | ✅ Completado | 2026-05-04 | Hash: e8b486c |
