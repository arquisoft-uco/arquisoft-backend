# PLAN: Consultar información de los usuarios (Administrador)

## Metadata
- **ID Historia:** HU-260
- **Bounded Context:** `seguridad`
- **¿Usa AggregateRoot?:** No — el contexto `seguridad` delega la autenticación en Keycloak. La entidad `Usuario` es una entidad de dominio plana (sin `AggregateRoot`) porque no emite eventos de dominio en esta HU de consulta, y la gestión de identidad vive en Keycloak. Los datos de negocio (nombre, estado, roles contextuales) se persisten en PostgreSQL schema `usuarios`.
- **Módulos Gradle afectados:** `seguridad:domain`, `seguridad:application`, `seguridad:infrastructure`
- **Fecha de plan:** 2026-04-26
- **Rama sugerida:** `feature/HU-260-consultar_usuarios_administrador`
- **Fuentes consultadas del repo de documentación:**
  - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
  - `artefactos/estrategicos/event-storming/Usuario - Event Storming.md`
  - `artefactos/estrategicos/modelo-dominio/anemico/modelo_dominio_anemico.md`
  - `artefactos/estrategicos/modelo-dominio/anemico/documentacion/05_delimitar_contextos_usuarios.md`
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:**
  - La información de negocio (nombre, contacto, estado ACTIVO/INACTIVO, roles contextuales) vive en PostgreSQL Arquisoft, schema `usuarios`.
  - Keycloak solo se consulta en flujos de autenticación y para sincronizar el estado de cuenta — **no** para listar usuarios con lógica de negocio.
  - Es la **primera HU** del contexto `seguridad` → crear schema desde cero con migración Flyway.
  - Esta es una operación de **solo lectura** (GET). No crea ni modifica datos.
  - Paginación obligatoria con headers `X-Total-Count`, `X-Page-Number`, `X-Page-Size`.

---

## 1. Resumen Funcional

El Administrador necesita consultar la lista de usuarios registrados en el sistema Arquisoft, aplicando filtros por nombre/email, estado (ACTIVO/INACTIVO) y rol contextual. La consulta retorna resultados paginados con los campos: `id`, `nombre`, `apellido`, `email`, `identificador`, `estado` y `roles`. Esta HU **no** crea ni modifica usuarios — es exclusivamente de lectura. No cubre la gestión de usuarios en Keycloak ni la sincronización de cuentas.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | El Administrador autenticado realiza `GET /api/seguridad/usuarios` sin filtros | Retorna lista paginada de todos los usuarios con HTTP 200 y headers de paginación |
| 2 | El Administrador filtra por nombre o email (ej. `?nombre=Juan`) | Retorna solo usuarios cuyo nombre o email contenga el valor (búsqueda parcial, case-insensitive) |
| 3 | El Administrador filtra por estado (ej. `?estado=ACTIVO`) | Retorna solo usuarios con ese estado |
| 4 | El Administrador filtra por rol (ej. `?rol=ESTUDIANTE`) | Retorna solo usuarios que tengan ese rol contextual asignado |
| 5 | El Administrador combina filtros (ej. `?nombre=Ana&estado=ACTIVO&rol=COORDINADOR`) | Retorna usuarios que cumplan todos los filtros simultáneamente |
| 6 | Se solicita una página específica (ej. `?pagina=2&tamano=10`) | Retorna la página correcta con los headers `X-Total-Count`, `X-Page-Number`, `X-Page-Size` |
| 7 | Un usuario sin rol `ADMINISTRADOR` intenta acceder | Retorna HTTP 403 Forbidden |
| 8 | Un usuario no autenticado intenta acceder | Retorna HTTP 401 Unauthorized |
| 9 | Se envían parámetros de filtro con formato inválido (ej. estado desconocido) | Retorna HTTP 400 Bad Request con mensaje descriptivo |
| 10 | No existen usuarios que coincidan con los filtros | Retorna HTTP 200 con lista vacía y `X-Total-Count: 0` |

---

## 3. Reglas de Negocio

Extraídas del Event Storming — comando **"Consultar información de los usuarios"** (contexto `Usuario`):

- **Usuario-POL-04:** Asegurar que los datos que fueron enviados como filtro para llevar a cabo la acción sean válidos a nivel de tipo de dato, longitud, obligatoriedad, formato y rango.
- Los filtros son **opcionales** — si no se envía ninguno, se retornan todos los usuarios paginados.
- El filtro por `nombre` aplica búsqueda parcial (LIKE) sobre `nombre` y `apellido` y `email` de forma combinada (OR).
- El filtro por `estado` acepta únicamente los valores del enum `EstadoUsuario`: `ACTIVO` o `INACTIVO`.
- **El filtro por `rol` acepta únicamente valores del enum `UsuarioRole` (renombrado de `UserRole`): `ESTUDIANTE`, `ASESOR`, `COORDINADOR`, `JURADO`, `BIBLIOTECARIO`, `ADMINISTRADOR`, `ASESOR_FICHA`, `REPRESENTANTE_COMITE_CURRICULUM`.
- La paginación es obligatoria en la respuesta. Valores por defecto: `pagina=0`, `tamano=20`.
- Solo el rol `ADMINISTRADOR` puede ejecutar esta acción (validado con `@PreAuthorize`).
- La información de negocio se consulta **exclusivamente** desde PostgreSQL (schema `usuarios`). No se consulta Keycloak en este flujo.

---

## 4. Modelo DDD del Contexto

> **Nota:** El contexto `seguridad` **NO usa AggregateRoot**. La entidad `Usuario` es una entidad de dominio plana con constructor privado, campos `final` y factory methods `build` / `rebuild`. No emite eventos de dominio porque esta HU es de solo lectura. Las entidades de rol (`Estudiante`, `Coordinador`, etc.) son entidades secundarias que representan la asignación de un rol contextual a un usuario.

### Entidad Principal de Dominio

- **Entidad:** `Usuario` — entidad de dominio plana (NO extiende `AggregateRoot`)
- **ID:** `UUID`
- **Campos:**
  - `UUID id`
  - `String nombre`
  - `String apellido`
  - `String email`
  - `String identificador` (número de documento)
  - `EstadoUsuario estado` — enum: `ACTIVO`, `INACTIVO`
  - `List<UsuarioRole> roles` — lista de enums (NO strings), valores del enum `UsuarioRole`
- **Value Objects / Enums:**
  - `EstadoUsuario` — enum Java con valores `ACTIVO`, `INACTIVO` (NUEVO)
  - `UsuarioRole` — enum Java existente (RENOMBRADO de `UserRole`) con todos los roles del sistema (REUTILIZAR)

### Eventos de Dominio que emite

> Esta HU es de **solo lectura**. No emite eventos de dominio. La sección aplica a HUs de escritura futuras (registrar, modificar, eliminar usuario).

---

## 5. Integraciones Externas

> Esta HU **no requiere** integración con sistemas externos más allá de PostgreSQL. Keycloak no se consulta en este flujo de negocio. No aplica esta sección.

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

> Esta es la primera HU del contexto `seguridad`. Se crean todos los archivos desde cero.

#### Capa `domain`

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/Usuario.java` | Entidad de dominio | Entidad plana con campos `final`, constructor privado, factory methods `build` y `rebuild`. Sin Lombok, sin Spring. |
| domain | `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/EstadoUsuario.java` | Enum | Estados posibles del usuario: `ACTIVO`, `INACTIVO` |
| — | `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/UsuarioRole.java` | Enum (EXISTENTE - RENOMBRADO) | Roles contextuales del sistema — **YA EXISTE como `UserRole`, renombrado a `UsuarioRole`**. Ubicado en esta ruta. |
| domain | `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/port/in/ConsultarUsuariosUseCase.java` | Interface (puerto de entrada) | Define el contrato para consultar usuarios con filtros y paginación |
| domain | `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/port/out/UsuarioRepositoryPort.java` | Interface (puerto de salida) | Define el contrato de persistencia: buscar usuarios con filtros y paginación |
| domain | `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/exception/ParametroFiltroInvalidoException.java` | Exception | Extiende `DomainException` de `shared:exceptions`. Se lanza cuando un parámetro de filtro tiene formato o valor inválido. Incluye campo `errorCode`. |

#### Capa `application`

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| application | `seguridad/application/src/main/java/com/arquisoft/seguridad/application/dto/UsuarioFiltroDTO.java` | DTO de entrada | Encapsula los parámetros de filtro y paginación: `nombre`, `email`, `estado`, `rol`, `pagina`, `tamano`. Con anotaciones Jakarta Validation. |
| application | `seguridad/application/src/main/java/com/arquisoft/seguridad/application/dto/UsuarioResponseDTO.java` | DTO de salida | Representa un usuario en la respuesta: `id`, `nombre`, `apellido`, `email`, `identificador`, `estado`, `roles`. Con método estático `fromDomain(Usuario)`. |
| application | `seguridad/application/src/main/java/com/arquisoft/seguridad/application/dto/PaginaResponseDTO.java` | DTO de paginación | Encapsula metadatos de paginación: `totalElementos`, `numeroPagina`, `tamanoPagina`, `contenido`. Con método estático `fromPage(...)`. |
| application | `seguridad/application/src/main/java/com/arquisoft/seguridad/application/usecase/ConsultarUsuariosUseCaseImpl.java` | UseCase Impl | Implementa `ConsultarUsuariosUseCase`. Valida filtros (POL-04), delega en `UsuarioRepositoryPort`, retorna lista paginada de `UsuarioResponseDTO`. Anotado con `@RequiredArgsConstructor` y `@Transactional(readOnly = true)`. |

#### Capa `infrastructure`

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/in/web/UsuarioController.java` | Controller REST | Expone `GET /seguridad/usuarios`. Recibe parámetros de filtro y paginación como `@RequestParam`. Escribe headers de paginación en la respuesta. Anotado con `@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`. |
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioJpaEntity.java` | JPA Entity | `@Entity @Table(schema = "usuarios", name = "usuario")`. Mapea todos los campos del dominio. Incluye relación con tabla `usuario_rol`. |
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioJpaRepository.java` | JPA Repository | Extiende `JpaRepository<UsuarioJpaEntity, UUID>`. Define método de consulta con filtros dinámicos usando `Specification<UsuarioJpaEntity>` o `@Query` JPQL. |
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioRepositoryAdapter.java` | Adapter de repositorio | Implementa `UsuarioRepositoryPort`. Traduce entre `UsuarioJpaEntity` y `Usuario` de dominio usando `rebuild(...)`. Construye `Specification` dinámica con los filtros recibidos. |
| infrastructure | `seguridad/infrastructure/src/main/resources/db/migration/V1__crear_schema_usuarios.sql` | Flyway — migración inicial | Crea el schema `usuarios` y la tabla `usuario` con todos sus campos y la tabla `usuario_rol` para los roles. |

### Archivos a MODIFICAR

> No aplica — esta es la primera HU del contexto `seguridad`. No hay archivos previos que modificar.

### Archivos de MENSAJERÍA RabbitMQ

> No aplica — esta HU es de solo lectura. No publica ni consume eventos RabbitMQ.

---

## 7. Detalle por Archivo

---

### `Usuario.java`
- **Paquete:** `com.arquisoft.seguridad.domain.model`
- **Tipo:** Entidad de dominio plana (NO extiende `AggregateRoot`)
- **Responsabilidad:** Representa un usuario del sistema con sus datos de negocio. Inmutable, constructor privado, campos `final`.
- **Features Java 21 aplicables:** Ninguna especial para esta entidad. Los enums `EstadoUsuario` y `RolUsuario` son tipos cerrados y bien definidos.
- **Métodos principales:**
  - `build(UUID id, String nombre, String apellido, String email, String identificador, EstadoUsuario estado, List<RolUsuario> roles): Usuario` — factory method para crear nuevas instancias (uso futuro en HUs de escritura)
  - `rebuild(UUID id, String nombre, String apellido, String email, String identificador, EstadoUsuario estado, List<RolUsuario> roles): Usuario` — factory method para reconstruir desde persistencia (uso en esta HU)
  - Getters para todos los campos: `getId()`, `getNombre()`, `getApellido()`, `getEmail()`, `getIdentificador()`, `getEstado()`, `getRoles()`
- **Dependencias:** `EstadoUsuario`, `RolUsuario`

---

### `EstadoUsuario.java`
- **Paquete:** `com.arquisoft.seguridad.domain.model`
- **Tipo:** Enum Java
- **Responsabilidad:** Representa los estados posibles de un usuario en el sistema.
- **Valores:** `ACTIVO`, `INACTIVO`
- **Dependencias:** Ninguna

---

### `UsuarioRole.java` (EXISTENTE - RENOMBRADO)
- **Paquete:** `com.arquisoft.seguridad.domain.model`
- **Tipo:** Enum Java (renombrado de `UserRole`)
- **Ubicación actual:** `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/UsuarioRole.java`
- **Responsabilidad:** Representa los roles contextuales que puede tener un usuario. **Renombrar `UserRole` → `UsuarioRole` para mejor legibilidad en español del dominio.**
- **Valores (8 en total):**
  - `ESTUDIANTE` → code: `ESTUDIANTE`, Spring role: `ROLE_ESTUDIANTE`
  - `ASESOR` → code: `ASESOR`, Spring role: `ROLE_ASESOR`
  - `COORDINADOR` → code: `COORDINADOR`, Spring role: `ROLE_COORDINADOR`
  - `JURADO` → code: `JURADO`, Spring role: `ROLE_JURADO`
  - `BIBLIOTECARIO` → code: `BIBLIOTECARIO`, Spring role: `ROLE_BIBLIOTECARIO`
  - `ADMINISTRADOR` → code: `ADMINISTRADOR`, Spring role: `ROLE_ADMINISTRADOR`
  - `ASESOR_FICHA` → code: `ASESOR_FICHA`, Spring role: `ROLE_ASESOR_FICHA`
  - `REPRESENTANTE_COMITE_CURRICULUM` → code: `REPRESENTANTE_COMITE_CURRICULUM`, Spring role: `ROLE_REPRESENTANTE_COMITE_CURRICULUM`
- **Métodos principales:**
  - `getCode()` — retorna el código exacto (coincide con JWT `realm_access.roles`)
  - `getSpringRole()` — retorna `ROLE_XXX` para Spring Security
  - `fromCode(String code)` — factory para buscar por código, lanza `IllegalArgumentException` si no existe
  - `getDescription()` — retorna descripción legible
- **Cambios requeridos:**
  - ✅ Renombrar `UserRole.java` → `UsuarioRole.java`
  - ✅ Renombrar clase `public enum UserRole` → `public enum UsuarioRole`
  - ✅ Actualizar método `fromCode()` para usar `UsuarioRole.values()`
  - ✅ Actualizar test `UserRoleTest.java` → `UsuarioRoleTest.java`
  - ✅ Actualizar `RoleAuthorityMapper.java` para importar `UsuarioRole`
- **Ventajas del cambio:**
  - ✅ Nomenclatura consistente en español para el dominio (`UsuarioRole` vs `UserRole`)
  - ✅ Sincronización automática con Keycloak (códigos coinciden con JWT)
  - ✅ Reutiliza lógica existente
- **Dependencias:** Ninguna

---

### `ConsultarUsuariosUseCase.java`
- **Paquete:** `com.arquisoft.seguridad.domain.port.in`
- **Tipo:** Interface (puerto de entrada)
- **Responsabilidad:** Define el contrato de la operación de consulta de usuarios con filtros y paginación.
- **Métodos principales:**
  - `ejecutar(UsuarioFiltroDTO filtro): PaginaResponseDTO<UsuarioResponseDTO>` — recibe los filtros y retorna la página de resultados
- **Dependencias:** `UsuarioFiltroDTO` (application), `PaginaResponseDTO` (application), `UsuarioResponseDTO` (application), `UsuarioRole` (domain)

> **Nota de arquitectura:** Los DTOs de application son visibles desde domain/port/in porque la dirección de dependencia es `domain ← application`. El puerto de entrada puede referenciar DTOs de application como parámetros de su contrato.

---

### `UsuarioRepositoryPort.java`
- **Paquete:** `com.arquisoft.seguridad.domain.port.out`
- **Tipo:** Interface (puerto de salida)
- **Responsabilidad:** Define el contrato de persistencia para consultar usuarios. Solo usa tipos del dominio.
- **Métodos principales:**
  - `buscarConFiltros(String nombreOEmail, EstadoUsuario estado, UsuarioRole rol, int pagina, int tamano): List<Usuario>` — retorna la página de usuarios que cumplen los filtros
  - `contarConFiltros(String nombreOEmail, EstadoUsuario estado, UsuarioRole rol): long` — retorna el total de usuarios que cumplen los filtros (para `X-Total-Count`)
- **Dependencias:** `Usuario`, `EstadoUsuario`, `UsuarioRole`

---

### `ParametroFiltroInvalidoException.java`
- **Paquete:** `com.arquisoft.seguridad.domain.exception`
- **Tipo:** Exception de dominio
- **Responsabilidad:** Se lanza cuando un parámetro de filtro enviado por el cliente tiene un valor inválido (ej. estado desconocido, rol inexistente).
- **Herencia:** Extiende `DomainException` de `shared:exceptions`
- **Campos:** `String errorCode` (ej. `"FILTRO_ESTADO_INVALIDO"`, `"FILTRO_ROL_INVALIDO"`)
- **Métodos principales:**
  - Constructor `ParametroFiltroInvalidoException(String errorCode, String mensaje)`
- **Dependencias:** `DomainException` (shared:exceptions)

---

### `UsuarioFiltroDTO.java`
- **Paquete:** `com.arquisoft.seguridad.application.dto`
- **Tipo:** DTO de entrada (filtros de consulta)
- **Responsabilidad:** Encapsula los parámetros opcionales de filtro y paginación que llegan desde el Controller.
- **Features Java 21 aplicables:** Puede modelarse como `record` de Java 21 dado que es inmutable y solo transporta datos.
- **Campos:**
  - `String nombreOEmail` — filtro parcial sobre nombre, apellido o email (opcional)
  - `String estado` — valor string del enum `EstadoUsuario` (opcional, se valida en use case)
  - `String rol` — valor string del enum `UsuarioRole` (opcional, se valida en use case usando `UsuarioRole.fromCode()`)
  - `int pagina` — número de página (default: 0)
  - `int tamano` — tamaño de página (default: 20, max: 100)
- **Anotaciones Jakarta Validation:**
  - `@Min(0)` en `pagina`
  - `@Min(1) @Max(100)` en `tamano`
- **Dependencias:** Ninguna de dominio

---

### `UsuarioResponseDTO.java`
- **Paquete:** `com.arquisoft.seguridad.application.dto`
- **Tipo:** DTO de salida
- **Responsabilidad:** Representa un usuario en la respuesta de la API. Incluye método de mapeo desde dominio.
- **Features Java 21 aplicables:** Puede modelarse como `record` de Java 21.
- **Campos:**
  - `UUID id`
  - `String nombre`
  - `String apellido`
  - `String email`
  - `String identificador`
  - `String estado` — representación string del enum `EstadoUsuario`
  - `List<String> roles` — lista de strings de los códigos de los roles (de `UsuarioRole`)
- **Métodos principales:**
  - `static fromDomain(Usuario usuario): UsuarioResponseDTO` — mapea desde entidad de dominio
- **Dependencias:** `Usuario`, `EstadoUsuario`, `UsuarioRole`

---

### `PaginaResponseDTO.java`
- **Paquete:** `com.arquisoft.seguridad.application.dto`
- **Tipo:** DTO genérico de paginación
- **Responsabilidad:** Encapsula los metadatos de paginación junto con el contenido de la página. Reutilizable en otros contextos.
- **Features Java 21 aplicables:** Puede modelarse como `record` genérico de Java 21: `record PaginaResponseDTO<T>(long totalElementos, int numeroPagina, int tamanoPagina, List<T> contenido)`.
- **Campos:**
  - `long totalElementos` — total de registros que cumplen los filtros
  - `int numeroPagina` — página actual (0-indexed)
  - `int tamanoPagina` — tamaño de la página
  - `List<T> contenido` — lista de DTOs de la página actual
- **Métodos principales:**
  - `static <T> fromData(long total, int pagina, int tamano, List<T> contenido): PaginaResponseDTO<T>`
- **Dependencias:** Ninguna de dominio

---

### `ConsultarUsuariosUseCaseImpl.java`
- **Paquete:** `com.arquisoft.seguridad.application.usecase`
- **Tipo:** UseCase Implementation
- **Responsabilidad:** Implementa `ConsultarUsuariosUseCase`. Valida los parámetros de filtro (POL-04), convierte strings a enums del dominio, delega la consulta en `UsuarioRepositoryPort` y construye la respuesta paginada.
- **Anotaciones Spring:** `@Service`, `@RequiredArgsConstructor`, `@Transactional(readOnly = true)`
- **Métodos principales:**
  - `ejecutar(UsuarioFiltroDTO filtro): PaginaResponseDTO<UsuarioResponseDTO>` — flujo principal:
    1. Validar `filtro.estado()` → convertir a `EstadoUsuario` o lanzar `ParametroFiltroInvalidoException`
    2. Validar `filtro.rol()` → convertir a `UserRole` usando `UserRole.fromCode()` o lanzar `ParametroFiltroInvalidoException`
    3. Llamar `repositoryPort.buscarConFiltros(...)` para obtener la lista de `Usuario`
    4. Llamar `repositoryPort.contarConFiltros(...)` para obtener el total
    5. Mapear cada `Usuario` a `UsuarioResponseDTO.fromDomain(...)`
    6. Retornar `PaginaResponseDTO.fromData(total, pagina, tamano, lista)`
- **Dependencias:** `UsuarioRepositoryPort`, `UsuarioFiltroDTO`, `UsuarioResponseDTO`, `PaginaResponseDTO`, `ParametroFiltroInvalidoException`, `EstadoUsuario`, `UserRole`

---

### `UsuarioController.java`
- **Paquete:** `com.arquisoft.seguridad.infrastructure.adapter.in.web`
- **Tipo:** Controller REST
- **Responsabilidad:** Expone el endpoint `GET /seguridad/usuarios`. Recibe parámetros de filtro como `@RequestParam`, invoca el use case, y escribe los headers de paginación en la respuesta HTTP.
- **Anotaciones Spring:** `@RestController`, `@RequestMapping("/seguridad/usuarios")`, `@RequiredArgsConstructor`, `@PreAuthorize("hasRole('ADMINISTRADOR')")`
- **`@Tag`:** `name = "Seguridad - Usuarios"`, `description = "Gestión de usuarios del sistema"`
- **Endpoints documentados:**

  | Método del Controller | `@Operation(summary)` | Códigos `@ApiResponse` | `@SecurityRequirement` |
  |-----------------------|-----------------------|------------------------|------------------------|
  | `consultarUsuarios(...)` | `"Consultar usuarios con filtros y paginación"` | 200, 400, 401, 403 | `bearerAuth` |

- **Métodos principales:**
  - `consultarUsuarios(@RequestParam(required=false) String nombreOEmail, @RequestParam(required=false) String estado, @RequestParam(required=false) String rol, @RequestParam(defaultValue="0") int pagina, @RequestParam(defaultValue="20") int tamano, HttpServletResponse response): ResponseEntity<List<UsuarioResponseDTO>>`
    - Construye `UsuarioFiltroDTO` con los parámetros
    - Llama `consultarUsuariosUseCase.ejecutar(filtro)`
    - Escribe headers: `response.setHeader("X-Total-Count", ...)`, `response.setHeader("X-Page-Number", ...)`, `response.setHeader("X-Page-Size", ...)`
    - Retorna `ResponseEntity.ok(pagina.contenido())`
- **Dependencias:** `ConsultarUsuariosUseCase`, `UsuarioFiltroDTO`, `PaginaResponseDTO`, `UsuarioResponseDTO`

---

### `UsuarioJpaEntity.java`
- **Paquete:** `com.arquisoft.seguridad.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Mapea la tabla `usuarios.usuario` de PostgreSQL. Incluye la relación con la tabla `usuarios.usuario_rol` para los roles.
- **Anotaciones JPA clave:**
  - `@Entity`
  - `@Table(schema = "usuarios", name = "usuario")`
  - `@Id @Column(name = "id", columnDefinition = "uuid")` sobre el campo `UUID id`
  - `@Enumerated(EnumType.STRING)` sobre el campo `EstadoUsuario estado`
  - `@ElementCollection(fetch = FetchType.EAGER)` + `@CollectionTable(schema = "usuarios", name = "usuario_rol", joinColumns = @JoinColumn(name = "usuario_id"))` + `@Column(name = "rol")` + `@Enumerated(EnumType.STRING)` sobre `List<UsuarioRole> roles`
- **Campos:**
  - `UUID id`
  - `String nombre`
  - `String apellido`
  - `String email`
  - `String identificador`
  - `EstadoUsuario estado`
  - `List<UsuarioRole> roles`
- **Dependencias:** `EstadoUsuario`, `UsuarioRole`

---

### `UsuarioJpaRepository.java`
- **Paquete:** `com.arquisoft.seguridad.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Repository
- **Responsabilidad:** Acceso a datos de `UsuarioJpaEntity`. Extiende `JpaRepository` y `JpaSpecificationExecutor` para soportar filtros dinámicos con `Specification`.
- **Herencia:** `JpaRepository<UsuarioJpaEntity, UUID>`, `JpaSpecificationExecutor<UsuarioJpaEntity>`
- **Métodos principales:** Los heredados de `JpaRepository` y `JpaSpecificationExecutor` son suficientes. No se requieren métodos adicionales.
- **Dependencias:** `UsuarioJpaEntity`

---

### `UsuarioRepositoryAdapter.java`
- **Paquete:** `com.arquisoft.seguridad.infrastructure.adapter.out.persistence`
- **Tipo:** Adapter de repositorio
- **Responsabilidad:** Implementa `UsuarioRepositoryPort`. Construye `Specification<UsuarioJpaEntity>` dinámicamente con los filtros recibidos. Traduce `UsuarioJpaEntity` → `Usuario` de dominio usando `rebuild(...)`. Nunca usa `build(...)` al reconstruir desde persistencia.
- **Anotaciones Spring:** `@Component`, `@RequiredArgsConstructor`
- **Métodos principales:**
  - `buscarConFiltros(String nombreOEmail, EstadoUsuario estado, UsuarioRole rol, int pagina, int tamano): List<Usuario>`
    - Construye `Specification` combinando predicados opcionales:
      - Si `nombreOEmail != null`: `LOWER(nombre) LIKE %valor% OR LOWER(apellido) LIKE %valor% OR LOWER(email) LIKE %valor%`
      - Si `estado != null`: `estado = :estado`
      - Si `rol != null`: `rol IN (SELECT r FROM usuario_rol WHERE usuario_id = id AND rol = :rol)`
    - Ejecuta `jpaRepository.findAll(spec, PageRequest.of(pagina, tamano))`
    - Mapea cada `UsuarioJpaEntity` a `Usuario` con `rebuild(...)`
  - `contarConFiltros(String nombreOEmail, EstadoUsuario estado, UsuarioRole rol): long`
    - Construye la misma `Specification` y ejecuta `jpaRepository.count(spec)`
- **Dependencias:** `UsuarioJpaRepository`, `UsuarioJpaEntity`, `Usuario`, `EstadoUsuario`, `UsuarioRole`

---

### `V1__crear_schema_usuarios.sql`
- **Ruta:** `seguridad/infrastructure/src/main/resources/db/migration/V1__crear_schema_usuarios.sql`
- **Tipo:** Migración Flyway — primera migración del contexto `seguridad`
- **Schema PostgreSQL:** `usuarios` (según tabla de mapeo: contexto `seguridad` → schema `usuarios`)
- **Cambios:**
  - Crear schema `usuarios` si no existe
  - Crear tabla `usuarios.usuario` con columnas: `id UUID PRIMARY KEY`, `nombre VARCHAR(100) NOT NULL`, `apellido VARCHAR(100) NOT NULL`, `email VARCHAR(150) NOT NULL UNIQUE`, `identificador VARCHAR(50) NOT NULL UNIQUE`, `estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'`
  - Crear tabla `usuarios.usuario_rol` con columnas: `usuario_id UUID NOT NULL REFERENCES usuarios.usuario(id) ON DELETE CASCADE`, `rol VARCHAR(50) NOT NULL`, `PRIMARY KEY (usuario_id, rol)`
  - Crear índices: `idx_usuario_email` sobre `email`, `idx_usuario_estado` sobre `estado`, `idx_usuario_nombre_apellido` sobre `(nombre, apellido)`, `idx_usuario_rol` sobre `usuario_rol(rol)`

---

## 8. Endpoints REST

| Método | Ruta | Request Body | Response | Código HTTP | Roles permitidos | Anotaciones Swagger (ADR-011) |
|--------|------|--------------|----------|-------------|-----------------|-------------------------------|
| GET | `/api/seguridad/usuarios` | — (query params: `nombreOEmail`, `estado`, `rol`, `pagina`, `tamano`) | `List<UsuarioResponseDTO>` + headers de paginación | 200 | `ADMINISTRADOR` | `@Operation(summary="Consultar usuarios con filtros y paginación")` + `@SecurityRequirement(name="bearerAuth")` |

**Query Parameters:**

| Parámetro | Tipo | Requerido | Default | Descripción |
|-----------|------|-----------|---------|-------------|
| `nombreOEmail` | `String` | No | — | Búsqueda parcial en nombre, apellido o email |
| `estado` | `String` | No | — | Filtro por estado: `ACTIVO` o `INACTIVO` |
| `rol` | `String` | No | — | Filtro por rol contextual (ver enum `RolUsuario`) |
| `pagina` | `int` | No | `0` | Número de página (0-indexed) |
| `tamano` | `int` | No | `20` | Tamaño de página (1-100) |

**Response Headers:**

| Header | Descripción |
|--------|-------------|
| `X-Total-Count` | Total de usuarios que cumplen los filtros |
| `X-Page-Number` | Número de página actual |
| `X-Page-Size` | Tamaño de la página |

**Ejemplo de respuesta exitosa (HTTP 200):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan.perez@universidad.edu.co",
    "identificador": "1234567890",
    "estado": "ACTIVO",
    "roles": ["ESTUDIANTE"]
  }
]
```

---

## 9. Eventos RabbitMQ

> No aplica — esta HU es de solo lectura. No publica ni consume eventos RabbitMQ.

---

## 10. Migración de Base de Datos

- **Archivo:** `V1__crear_schema_usuarios.sql`
- **Esquema PostgreSQL:** `usuarios` (contexto `seguridad` → schema `usuarios`, según tabla de mapeo del proyecto)
- **Base de datos:** `arquisoft` (local, ya conectada al proyecto)
- **Cambios detallados:**

```
SCHEMA:   usuarios

TABLA:    usuarios.usuario
  - id            UUID          PRIMARY KEY
  - nombre        VARCHAR(100)  NOT NULL
  - apellido      VARCHAR(100)  NOT NULL
  - email         VARCHAR(150)  NOT NULL  UNIQUE
  - identificador VARCHAR(50)   NOT NULL  UNIQUE
  - estado        VARCHAR(20)   NOT NULL  DEFAULT 'ACTIVO'
                  CHECK (estado IN ('ACTIVO', 'INACTIVO'))

TABLA:    usuarios.usuario_rol
  - usuario_id    UUID          NOT NULL  FK → usuarios.usuario(id) ON DELETE CASCADE
  - rol           VARCHAR(50)   NOT NULL
                  CHECK (rol IN ('ESTUDIANTE','ASESOR','COORDINADOR','JURADO',
                                 'BIBLIOTECARIO','ADMINISTRADOR','ASESOR_FICHA',
                                 'REPRESENTANTE_COMITE_CURRICULUM'))
  - PRIMARY KEY (usuario_id, rol)

ÍNDICES:
  - idx_usuario_email          ON usuarios.usuario(email)
  - idx_usuario_estado         ON usuarios.usuario(estado)
  - idx_usuario_nombre_apellido ON usuarios.usuario(nombre, apellido)
  - idx_usuario_rol            ON usuarios.usuario_rol(rol)
```

> **Nota:** Esta es la migración `V1` — primera del contexto `seguridad`. Las HUs futuras (registrar, modificar, eliminar usuario) usarán `V2`, `V3`, etc.

---

## 11. Casos de Prueba Sugeridos

### Tests Unitarios — capa `domain`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `UsuarioTest` | `debeCrearUsuario_cuandoDatosValidos` | `rebuild(...)` crea entidad con todos los campos correctamente asignados |
| `UsuarioTest` | `debeRetornarRoles_cuandoTieneMultiplesRoles` | `getRoles()` retorna la lista completa de roles asignados |
| `UsuarioTest` | `debeRetornarEstadoActivo_cuandoEstadoEsActivo` | `getEstado()` retorna `EstadoUsuario.ACTIVO` |
| `ParametroFiltroInvalidoExceptionTest` | `debeCrearExcepcion_cuandoErrorCodeYMensajeProvistos` | La excepción contiene `errorCode` y mensaje correctos |

### Tests Unitarios — capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `ConsultarUsuariosUseCaseImplTest` | `debeRetornarPaginaVacia_cuandoNoHayUsuarios` | Repositorio retorna lista vacía → respuesta con `totalElementos = 0` y `contenido` vacío |
| `ConsultarUsuariosUseCaseImplTest` | `debeRetornarUsuariosFiltradosPorEstado_cuandoEstadoEsActivo` | Repositorio recibe `EstadoUsuario.ACTIVO` como parámetro |
| `ConsultarUsuariosUseCaseImplTest` | `debeRetornarUsuariosFiltradosPorRol_cuandoRolEsEstudiante` | Repositorio recibe `RolUsuario.ESTUDIANTE` como parámetro |
| `ConsultarUsuariosUseCaseImplTest` | `debeLanzarExcepcion_cuandoEstadoFiltroEsInvalido` | Se lanza `ParametroFiltroInvalidoException` con `errorCode = "FILTRO_ESTADO_INVALIDO"` |
| `ConsultarUsuariosUseCaseImplTest` | `debeLanzarExcepcion_cuandoRolFiltroEsInvalido` | Se lanza `ParametroFiltroInvalidoException` con `errorCode = "FILTRO_ROL_INVALIDO"` |
| `ConsultarUsuariosUseCaseImplTest` | `debeMapearCorrectamente_cuandoUsuarioDominioAResponseDTO` | `UsuarioResponseDTO.fromDomain(...)` mapea todos los campos correctamente |
| `ConsultarUsuariosUseCaseImplTest` | `debeUsarValoresPorDefecto_cuandoPaginaYTamanoNoProvistos` | Se usan `pagina=0` y `tamano=20` cuando no se especifican |

### Tests de Repositorio — capa `infrastructure` (H2 en memoria)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `UsuarioRepositoryAdapterTest` | `debeBuscarTodos_cuandoNoHayFiltros` | Sin filtros retorna todos los usuarios de la BD de prueba |
| `UsuarioRepositoryAdapterTest` | `debeFiltrarPorEstado_cuandoEstadoEsActivo` | Solo retorna usuarios con `estado = ACTIVO` |
| `UsuarioRepositoryAdapterTest` | `debeFiltrarPorNombreOEmail_cuandoNombreParcialesCoincidenConBusqueda` | Búsqueda parcial case-insensitive funciona correctamente |
| `UsuarioRepositoryAdapterTest` | `debeFiltrarPorRol_cuandoRolEsCoordinador` | Solo retorna usuarios que tienen el rol `COORDINADOR` |
| `UsuarioRepositoryAdapterTest` | `debeReconstruirConRebuild_cuandoEntidadJpaExiste` | El adapter llama `rebuild(...)`, no `build(...)`, al mapear desde JPA |
| `UsuarioRepositoryAdapterTest` | `debeContarCorrectamente_cuandoHayFiltros` | `contarConFiltros(...)` retorna el número correcto de registros |
| `UsuarioRepositoryAdapterTest` | `debePaginar_cuandoHayMasRegistrosQueElTamano` | La segunda página retorna los registros correctos |

### Tests de Controller — capa `infrastructure` (Spring Security Test)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `UsuarioControllerTest` | `debeRetornar200_cuandoAdministradorConsultaSinFiltros` | JWT con rol `ADMINISTRADOR` → HTTP 200 con lista y headers de paginación |
| `UsuarioControllerTest` | `debeRetornar403_cuandoUsuarioNoEsAdministrador` | JWT con rol `ESTUDIANTE` → HTTP 403 |
| `UsuarioControllerTest` | `debeRetornar401_cuandoNoHayToken` | Sin JWT → HTTP 401 |
| `UsuarioControllerTest` | `debeRetornar400_cuandoEstadoFiltroEsInvalido` | `?estado=INVALIDO` → HTTP 400 con mensaje de error |
| `UsuarioControllerTest` | `debeEscribirHeadersDePaginacion_cuandoConsultaExitosa` | Response contiene `X-Total-Count`, `X-Page-Number`, `X-Page-Size` |
| `UsuarioControllerTest` | `debeRetornarListaVacia_cuandoNoHayCoincidencias` | HTTP 200 con lista vacía y `X-Total-Count: 0` |
| `UsuarioControllerTest` | `debeAplicarFiltrosCombinados_cuandoSeEnvianVariosParametros` | `?estado=ACTIVO&rol=ESTUDIANTE` → use case recibe ambos filtros |

---

## 12. Checklist de Implementación

- [ ] **DDD:** Entidad `Usuario` es plana (NO extiende `AggregateRoot`) — justificado porque `seguridad` delega auth en Keycloak y esta HU es de solo lectura
- [ ] Entidad `Usuario` inmutable: constructor privado, campos `final`, factory methods `build` / `rebuild`, sin Lombok
- [ ] Enums: `EstadoUsuario` definido en `domain/model/` (NUEVO), `UsuarioRole` renombrado de `UserRole` (cambiar nombre en todos los archivos)
- [ ] Modelo `Usuario` con `List<UsuarioRole> roles` (NOT `List<String>`) — lista de enums tipados, no strings
- [ ] Puerto de entrada `ConsultarUsuariosUseCase` definido en `domain/port/in/`
- [ ] Puerto de salida `UsuarioRepositoryPort` definido en `domain/port/out/` — solo usa tipos del dominio (`UsuarioRole`)
- [ ] Excepción `ParametroFiltroInvalidoException` extiende `DomainException` y tiene `errorCode`
- [ ] DTOs `UsuarioFiltroDTO`, `UsuarioResponseDTO`, `PaginaResponseDTO` con anotaciones Jakarta Validation
- [ ] `UsuarioResponseDTO` tiene método estático `fromDomain(Usuario)` — convierte `List<UsuarioRole>` a `List<String>` (códigos de roles)
- [ ] `ConsultarUsuariosUseCaseImpl` con `@RequiredArgsConstructor`, `@Transactional(readOnly = true)` y validación POL-04 usando `UsuarioRole.fromCode()`
- [ ] Controller REST con `@Valid @RequestParam` y `@PreAuthorize("hasRole('ADMINISTRADOR')")`
- [ ] Controller documentado con `@Tag`, `@Operation`, `@ApiResponses` y `@SecurityRequirement(name="bearerAuth")` (ADR-011)
- [ ] `UsuarioJpaEntity` con `@Table(schema = "usuarios", name = "usuario")` y mapeo de `@ElementCollection` para roles (`List<UsuarioRole>`)
- [ ] `UsuarioJpaRepository` extiende `JpaSpecificationExecutor<UsuarioJpaEntity>` para filtros dinámicos
- [ ] `UsuarioRepositoryAdapter` usa `rebuild(...)` al reconstruir desde JPA — nunca `build(...)`
- [ ] `UsuarioRepositoryAdapter` construye `Specification` dinámica con predicados opcionales
- [ ] Controller escribe headers `X-Total-Count`, `X-Page-Number`, `X-Page-Size` en `HttpServletResponse`
- [ ] Migración Flyway `V1__crear_schema_usuarios.sql` en schema `usuarios` con tablas `usuario` y `usuario_rol` e índices (CHECK constraints usan valores exactos de `UsuarioRole`)
- [ ] Renombrar `UserRole.java` → `UsuarioRole.java` y actualizar test `UserRoleTest.java` → `UsuarioRoleTest.java`
- [ ] Actualizar `RoleAuthorityMapper.java` para importar y usar `UsuarioRole`
- [ ] No se publica ni consume eventos RabbitMQ (HU de solo lectura)
- [ ] Tests unitarios con patrón AAA (cobertura ≥ 75%)
- [ ] Tests de repositorio con H2 en memoria
- [ ] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4.x)
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads gestionados por Spring Boot)
- [ ] Compilar con `./gradlew build -x test` para verificar que el renombramiento no rompió el proyecto
- [ ] Commit: `feat(seguridad): renombrar UserRole a UsuarioRole y consultar usuarios con filtros`

---

## 13. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ⏳ Pendiente |       |       |
| Tests      | @tester             | ⏳ Pendiente |       |       |
| Validación | @validator-analyze  | ⏳ Pendiente |       |       |
| Reporte    | @validator-report   | ⏳ Pendiente |       |       |
| Commit     | @commit             | ⏳ Pendiente |       |       |
