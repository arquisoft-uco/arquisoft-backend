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
- **El filtro por `rol` acepta únicamente valores del enum `UsuarioRole`: `ESTUDIANTE`, `ASESOR`, `COORDINADOR`, `JURADO`, `BIBLIOTECARIO`, `ADMINISTRADOR`, `ASESOR_FICHA`, `REPRESENTANTE_COMITE_CURRICULUM`.
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
  - `UUID keycloakUserId` — vínculo con Keycloak (`sub` claim del JWT). Inmutable tras la creación.
  - `String nombre`
  - `String apellido`
  - `String email`
  - `String identificador` (código institucional)
  - `EstadoUsuario estado` — enum: `ACTIVO`, `INACTIVO`
  - `List<UsuarioRole> roles` — lista de enums (NO strings), valores del enum `UsuarioRole`
- **Value Objects / Enums:**
  - `EstadoUsuario` — enum Java con valores `ACTIVO`, `INACTIVO` (NUEVO)
  - `UsuarioRole` — enum Java con todos los roles del sistema (REUTILIZAR, ubicado en `seguridad/domain/model/UsuarioRole.java`)

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
| — | `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/UsuarioRole.java` | Enum (EXISTENTE) | Roles contextuales del sistema. Ubicado en esta ruta desde el inicio del proyecto. |
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
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioJpaEntity.java` | JPA Entity | `@Entity @Table(schema = "usuarios", name = "usuario")`. Mapea todos los campos del dominio incluido `keycloak_user_id`. Relación `@ManyToMany` hacia `RolJpaEntity`. |
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/RolJpaEntity.java` | JPA Entity | `@Entity @Table(schema = "usuarios", name = "rol")`. Mapea el catálogo de roles. El campo `nombre` coincide con `UsuarioRole.getCode()`. |
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioJpaRepository.java` | JPA Repository | Extiende `JpaRepository<UsuarioJpaEntity, UUID>` y `JpaSpecificationExecutor<UsuarioJpaEntity>` para filtros dinámicos con `Specification`. |
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioRepositoryAdapter.java` | Adapter de repositorio | Implementa `UsuarioRepositoryPort`. Traduce entre `UsuarioJpaEntity` y `Usuario` de dominio usando `rebuild(...)`. Construye `Specification` dinámica. Convierte `RolJpaEntity.nombre` → `UsuarioRole.fromCode()`. |
| infrastructure | `seguridad/infrastructure/src/main/resources/db/migration/V1__crear_schema_usuarios.sql` | Flyway — migración inicial | Crea schema `usuarios`, tablas `usuario`, `rol`, `usuario_rol`, índices y los 8 INSERT de datos iniciales de roles. |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---|---|
| `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/EstadoUsuario.java` | Agregar `code` y `description` al enum (alinear con `UsuarioRole`): constructor con parámetros y métodos `getCode()`, `getDescription()`, `fromCode(String)` — ✅ ya implementado |
| `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioJpaEntity.java` | Reemplazar `@Enumerated + @Column(estado)` por `@ManyToOne` hacia `EstadoUsuarioJpaEntity` — ✅ ya implementado |
| `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioRepositoryAdapter.java` | Actualizar `toDomain()`: traducir `EstadoUsuarioJpaEntity.nombre` → `EstadoUsuario.fromCode()` en lugar de leer el enum directamente — ✅ ya implementado |
| `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/in/web/GlobalExceptionHandler.java` | **DEUDA TÉCNICA — agregar `@ExceptionHandler(ParametroFiltroInvalidoException.class)`** mapeado a HTTP 400. Sin este handler, la excepción cae en `handleGeneral` y retorna 500. El controller test `debeRetornar400_cuandoFiltroInvalido` requiere este handler para pasar. |

### Archivos NUEVOS adicionales (normalización estado)

| Capa | Ruta completa | Tipo | Responsabilidad |
|---|---|---|---|
| infrastructure | `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/EstadoUsuarioJpaEntity.java` | JPA Entity | `@Entity @Table(schema = "usuarios", name = "estado_usuario")`. Mapea el catálogo de estados. Campo `nombre` coincide con `EstadoUsuario.getCode()`. |
| infrastructure | `seguridad/infrastructure/src/main/resources/db/migration/V2__normalizar_estado_usuario.sql` | Flyway — migración V2 | Crea tabla `estado_usuario`, inserta los 2 registros (`ACTIVO`, `INACTIVO`), agrega columna `estado_id UUID FK`, migra datos desde `estado` VARCHAR, elimina columna `estado` y el CHECK anterior. |

### Archivos de MENSAJERÍA RabbitMQ

> No aplica — esta HU es de solo lectura. No publica ni consume eventos RabbitMQ.

---

## 7. Detalle por Archivo

---

### `Usuario.java`
- **Paquete:** `com.arquisoft.seguridad.domain.model`
- **Tipo:** Entidad de dominio plana (NO extiende `AggregateRoot`)
- **Responsabilidad:** Representa un usuario del sistema con sus datos de negocio. Inmutable, constructor privado, campos `final`.
- **Features Java 21 aplicables:** Ninguna especial para esta entidad.
- **Métodos principales:**
  - `build(UUID id, UUID keycloakUserId, String nombre, String apellido, String email, String identificador, EstadoUsuario estado, List<UsuarioRole> roles): Usuario` — factory para crear nuevas instancias (uso futuro en HUs de escritura)
  - `rebuild(UUID id, UUID keycloakUserId, String nombre, String apellido, String email, String identificador, EstadoUsuario estado, List<UsuarioRole> roles): Usuario` — factory para reconstruir desde persistencia (uso en esta HU)
  - Getters: `getId()`, `getKeycloakUserId()`, `getNombre()`, `getApellido()`, `getEmail()`, `getIdentificador()`, `getEstado()`, `getRoles()`
- **Dependencias:** `EstadoUsuario`, `UsuarioRole`

---

### `EstadoUsuario.java`
- **Paquete:** `com.arquisoft.seguridad.domain.model`
- **Tipo:** Enum Java
- **Responsabilidad:** Representa los estados posibles de un usuario en el sistema.
- **Valores:** `ACTIVO`, `INACTIVO`
- **Dependencias:** Ninguna

---

### `UsuarioRole.java` (EXISTENTE)
- **Paquete:** `com.arquisoft.seguridad.domain.model`
- **Tipo:** Enum Java
- **Ubicación:** `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/UsuarioRole.java`
- **Responsabilidad:** Representa los roles contextuales que puede tener un usuario en el sistema Arquisoft. REUTILIZAR este enum existente.
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
  - ✅ COMPLETADO: Enum `UsuarioRole.java` ya existe y está siendo utilizado
  - ✅ COMPLETADO: Test `UsuarioRoleTest.java` actualizado
  - ✅ COMPLETADO: `RoleAuthorityMapper.java` importa y usa `UsuarioRole`
- **Ventajas:**
  - ✅ Nomenclatura consistente en español para el dominio (`UsuarioRole`)
  - ✅ Sincronización automática con Keycloak (códigos coinciden con JWT)
  - ✅ Reutiliza lógica existente
  - ✅ Completamente testado
- **Dependencias:** Ninguna

---

### `EstadoUsuario.java` — MODIFICAR (normalización)
- **Paquete:** `com.arquisoft.seguridad.domain.model`
- **Estado actual:** Enum simple con solo `ACTIVO` e `INACTIVO` sin campos adicionales.
- **Cambio requerido:** Alinear con el patrón de `UsuarioRole` — agregar `code` y `description` para que el adapter pueda traducir `EstadoUsuarioJpaEntity.nombre` → `EstadoUsuario` usando `fromCode()`.
- **Valores tras el cambio:**
  - `ACTIVO("ACTIVO", "Usuario habilitado para operar en el sistema")`
  - `INACTIVO("INACTIVO", "Usuario deshabilitado; no puede iniciar sesión")`
- **Métodos a agregar:**
  - `getCode(): String` — retorna el nombre exacto coincidente con la tabla `estado_usuario.nombre`
  - `getDescription(): String` — descripción legible
  - `fromCode(String code): EstadoUsuario` — factory, lanza `IllegalArgumentException` si no existe
- **Impacto:** El `ConsultarUsuariosUseCaseImpl` ya usa `EstadoUsuario.valueOf()` — puede mantenerse o migrarse a `fromCode()`. El adapter usará `fromCode()` al traducir desde la entidad JPA.

---

### `EstadoUsuarioJpaEntity.java` — NUEVO
- **Paquete:** `com.arquisoft.seguridad.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Mapea la tabla `usuarios.estado_usuario`. Representa el catálogo de estados pre-poblado. El campo `nombre` coincide exactamente con `EstadoUsuario.getCode()`.
- **Anotaciones JPA clave:**
  - `@Entity`
  - `@Table(schema = "usuarios", name = "estado_usuario")`
  - `@Id @Column(name = "id", columnDefinition = "uuid")` sobre `UUID id`
  - `@Column(name = "nombre", nullable = false, unique = true)` sobre `String nombre`
- **Campos:**
  - `UUID id`
  - `String nombre` — coincide con `EstadoUsuario.getCode()` (ej. `"ACTIVO"`)
  - `String descripcion`
- **Dependencias:** Ninguna

---

### `UsuarioJpaEntity.java` — MODIFICAR (normalización)
- **Paquete:** `com.arquisoft.seguridad.infrastructure.adapter.out.persistence`
- **Estado actual:** Tiene `@Enumerated(EnumType.STRING) @Column(name = "estado") EstadoUsuario estado`.
- **Cambio requerido:** Reemplazar ese campo por una relación `@ManyToOne` hacia `EstadoUsuarioJpaEntity`, mapeando la columna `estado_id` (FK de la nueva tabla).
- **Cambio específico:**
  ```
  // ELIMINAR:
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false, length = 20)
  private EstadoUsuario estado;

  // AGREGAR:
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "estado_id", nullable = false)
  private EstadoUsuarioJpaEntity estado;
  ```
- **Dependencias nuevas:** `EstadoUsuarioJpaEntity`

---

### `UsuarioRepositoryAdapter.java` — MODIFICAR (normalización)
- **Estado actual:** `toDomain()` lee `entity.getEstado()` directamente como `EstadoUsuario` (enum).
- **Cambio requerido:** Traducir `entity.getEstado().getNombre()` → `EstadoUsuario.fromCode()`, igual que se hace con los roles.
- **Cambio específico en `toDomain()`:**
  ```
  // ANTES:
  entity.getEstado()

  // DESPUÉS:
  EstadoUsuario.fromCode(entity.getEstado().getNombre())
  ```
- **Dependencias nuevas:** `EstadoUsuario.fromCode()` (método a agregar en el enum)

---

### `V2__normalizar_estado_usuario.sql` — NUEVO
- **Ruta:** `seguridad/infrastructure/src/main/resources/db/migration/V2__normalizar_estado_usuario.sql`
- **Tipo:** Flyway — migración de normalización
- **Responsabilidad:** Introduce la tabla `estado_usuario` como catálogo, migra los datos de la columna `estado VARCHAR` a la FK `estado_id`, y elimina la columna y constraint anteriores. **No pierde datos existentes.**
- **Pasos en orden:**
  1. Crear tabla `estado_usuario`
  2. Insertar los 2 registros (`ACTIVO`, `INACTIVO`)
  3. Agregar columna `estado_id UUID NULL` en `usuario`
  4. Poblar `estado_id` desde el `estado` VARCHAR existente
  5. Aplicar `NOT NULL` a `estado_id`
  6. Agregar FK `fk_usuario_estado`
  7. Eliminar columna `estado` y constraint `ck_usuario_estado`
  8. Agregar índice `idx_usuario_estado` sobre `estado_id`

```sql
SET search_path TO usuarios;

-- 1. Catálogo de estados
CREATE TABLE estado_usuario (
    id          UUID        PRIMARY KEY,
    nombre      VARCHAR(20) NOT NULL,
    descripcion VARCHAR(200),
    CONSTRAINT uk_estado_usuario_nombre UNIQUE (nombre)
);

-- 2. Datos iniciales
INSERT INTO estado_usuario (id, nombre, descripcion) VALUES
    (gen_random_uuid(), 'ACTIVO',   'Usuario habilitado para operar en el sistema'),
    (gen_random_uuid(), 'INACTIVO', 'Usuario deshabilitado; no puede iniciar sesión');

-- 3. Nueva columna FK (nullable para poder migrar datos primero)
ALTER TABLE usuario ADD COLUMN estado_id UUID;

-- 4. Poblar estado_id desde el VARCHAR existente
UPDATE usuario u
SET estado_id = eu.id
FROM estado_usuario eu
WHERE eu.nombre = u.estado;

-- 5. NOT NULL ahora que todos los registros tienen FK
ALTER TABLE usuario ALTER COLUMN estado_id SET NOT NULL;

-- 6. FK constraint
ALTER TABLE usuario
    ADD CONSTRAINT fk_usuario_estado
    FOREIGN KEY (estado_id) REFERENCES estado_usuario(id);

-- 7. Eliminar columna y constraint obsoletos
ALTER TABLE usuario DROP CONSTRAINT ck_usuario_estado;
ALTER TABLE usuario DROP COLUMN estado;

-- 8. Índice sobre la FK
CREATE INDEX idx_usuario_estado ON usuario(estado_id);
```

---
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
    2. Validar `filtro.rol()` → convertir a `UsuarioRole` usando `UsuarioRole.fromCode()` o lanzar `ParametroFiltroInvalidoException`
    3. Llamar `repositoryPort.buscarConFiltros(...)` para obtener la lista de `Usuario`
    4. Llamar `repositoryPort.contarConFiltros(...)` para obtener el total
    5. Mapear cada `Usuario` a `UsuarioResponseDTO.fromDomain(...)`
    6. Retornar `PaginaResponseDTO.fromData(total, pagina, tamano, lista)`
- **Dependencias:** `UsuarioRepositoryPort`, `UsuarioFiltroDTO`, `UsuarioResponseDTO`, `PaginaResponseDTO`, `ParametroFiltroInvalidoException`, `EstadoUsuario`, `UsuarioRole`

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
- **Responsabilidad:** Mapea la tabla `usuarios.usuario`. Incluye `keycloak_user_id` y relación `@ManyToMany` hacia `RolJpaEntity` a través de la tabla `usuarios.usuario_rol`.
- **Anotaciones JPA clave:**
  - `@Entity`
  - `@Table(schema = "usuarios", name = "usuario")`
  - `@Id @Column(name = "id", columnDefinition = "uuid")` sobre `UUID id`
  - `@Column(name = "keycloak_user_id", nullable = false, unique = true, columnDefinition = "uuid")` sobre `UUID keycloakUserId`
  - `@Enumerated(EnumType.STRING)` sobre `EstadoUsuario estado`
  - `@ManyToMany(fetch = FetchType.EAGER)` + `@JoinTable(schema = "usuarios", name = "usuario_rol", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))` sobre `List<RolJpaEntity> roles`
- **Campos:**
  - `UUID id`
  - `UUID keycloakUserId`
  - `String nombre`
  - `String apellido`
  - `String email`
  - `String identificador`
  - `EstadoUsuario estado`
  - `List<RolJpaEntity> roles`
- **Dependencias:** `EstadoUsuario`, `RolJpaEntity`

---

### `RolJpaEntity.java`
- **Paquete:** `com.arquisoft.seguridad.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Mapea la tabla `usuarios.rol`. Representa el catálogo de roles pre-poblado. El campo `nombre` coincide exactamente con `UsuarioRole.getCode()`.
- **Anotaciones JPA clave:**
  - `@Entity`
  - `@Table(schema = "usuarios", name = "rol")`
  - `@Id @Column(name = "id", columnDefinition = "uuid")` sobre `UUID id`
  - `@Column(name = "nombre", nullable = false, unique = true)` sobre `String nombre`
- **Campos:**
  - `UUID id`
  - `String nombre` — coincide con el `code` de `UsuarioRole` (ej. `"ESTUDIANTE"`)
  - `String descripcion`
- **Dependencias:** ninguna

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
- **Responsabilidad:** Implementa `UsuarioRepositoryPort`. Construye `Specification<UsuarioJpaEntity>` dinámicamente. Traduce `UsuarioJpaEntity` → `Usuario` de dominio usando `rebuild(...)`. Convierte `RolJpaEntity.nombre` → `UsuarioRole.fromCode()` al reconstruir roles. Nunca usa `build(...)`.
- **Anotaciones Spring:** `@Component`, `@RequiredArgsConstructor`
- **Métodos principales:**
  - `buscarConFiltros(String nombreOEmail, EstadoUsuario estado, UsuarioRole rol, int pagina, int tamano): List<Usuario>`
    - Construye `Specification` combinando predicados opcionales:
      - Si `nombreOEmail != null`: `LOWER(nombre) LIKE %valor% OR LOWER(apellido) LIKE %valor% OR LOWER(email) LIKE %valor%`
      - Si `estado != null`: `estado = :estado`
      - Si `rol != null`: JOIN con `usuario_rol` y `rol` filtrando `rol.nombre = :rolCode`
    - Ejecuta `jpaRepository.findAll(spec, PageRequest.of(pagina, tamano))`
    - Mapea: `jpaEntity.getRoles().stream().map(r -> UsuarioRole.fromCode(r.getNombre())).toList()`
    - Llama `rebuild(id, keycloakUserId, nombre, apellido, email, identificador, estado, roles)` — todos los UUIDs ya son `UUID`, no strings
  - `contarConFiltros(String nombreOEmail, EstadoUsuario estado, UsuarioRole rol): long`
    - Misma `Specification`, ejecuta `jpaRepository.count(spec)`
- **Dependencias:** `UsuarioJpaRepository`, `UsuarioJpaEntity`, `RolJpaEntity`, `Usuario`, `EstadoUsuario`, `UsuarioRole`

---

### `GlobalExceptionHandler.java` — MODIFICAR (deuda técnica)
- **Paquete:** `com.arquisoft.seguridad.infrastructure.adapter.in.web`
- **Tipo:** `@RestControllerAdvice` — manejador global de excepciones del contexto `seguridad`
- **Estado actual:** ya existe con handlers para `InvalidCredentialsException`, `InvalidTokenException`, `AuthenticationException`, `AccessDeniedException`, `MethodArgumentNotValidException`, `MissingServletRequestParameterException`, `ResourceAccessException` y `Exception` (fallback 500).
- **Problema:** `ParametroFiltroInvalidoException` **no está registrada** → cae en `handleGeneral` → HTTP 500. Esto hace que el test `debeRetornar400_cuandoFiltroInvalido` falle aunque el use case lance la excepción correctamente.
- **Cambio requerido:** Añadir el siguiente `@ExceptionHandler` **antes** del fallback `Exception.class`:
  ```java
  @ExceptionHandler(ParametroFiltroInvalidoException.class)
  public ResponseEntity<ErrorResponseDTO> handleParametroFiltroInvalido(
          ParametroFiltroInvalidoException ex,
          HttpServletRequest request) {
      log.warn("Filtro inválido [{}]: {}", ex.getErrorCode(), ex.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(ErrorResponseDTO.builder()
                      .error("Bad Request")
                      .message(ex.getMessage())
                      .status(HttpStatus.BAD_REQUEST.value())
                      .path(request.getRequestURI())
                      .build());
  }
  ```
- **Import a agregar:** `com.arquisoft.seguridad.domain.exception.ParametroFiltroInvalidoException`
- **Dependencias:** `ParametroFiltroInvalidoException`, `ErrorResponseDTO`, `HttpServletRequest`

---

### `V1__crear_schema_usuarios.sql`
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
| `rol` | `String` | No | — | Filtro por rol contextual (ver enum `UsuarioRole`) |
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
- **Fuente:** `mer/02_tablas_usuarios.sql` del repositorio `arquisoft-docs` (validado)
- **Cambios detallados:**

```sql
SET search_path TO usuarios;

-- Tabla principal de usuarios
CREATE TABLE usuario (
    id                 UUID         PRIMARY KEY,
    keycloak_user_id   UUID         NOT NULL,              -- Sub claim de Keycloak (sub del JWT)
    nombre             VARCHAR(100) NOT NULL,
    apellido           VARCHAR(100) NOT NULL,
    email              VARCHAR(150) NOT NULL,
    identificador      VARCHAR(50)  NOT NULL,
    estado             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',

    CONSTRAINT uk_usuario_keycloak UNIQUE (keycloak_user_id),
    CONSTRAINT uk_usuario_email    UNIQUE (email),
    CONSTRAINT uk_usuario_ident    UNIQUE (identificador),
    CONSTRAINT ck_usuario_estado   CHECK  (estado IN ('ACTIVO', 'INACTIVO'))
);

-- Catálogo inmutable de roles del sistema
CREATE TABLE rol (
    id          UUID         PRIMARY KEY,
    nombre      VARCHAR(30)  NOT NULL,
    descripcion VARCHAR(200),

    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
);

-- Relación N:M usuario ↔ rol
CREATE TABLE usuario_rol (
    usuario_id UUID NOT NULL,
    rol_id     UUID NOT NULL,

    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol     FOREIGN KEY (rol_id)     REFERENCES rol(id)
);

-- Índices
CREATE INDEX idx_usuario_keycloak ON usuario(keycloak_user_id);
CREATE INDEX idx_usuario_email    ON usuario(email);
CREATE INDEX idx_usuario_ident    ON usuario(identificador);
CREATE INDEX idx_ur_rol           ON usuario_rol(rol_id);

-- Datos iniciales: 8 roles del sistema (coinciden con UsuarioRole.getCode())
INSERT INTO rol (id, nombre, descripcion) VALUES
    (gen_random_uuid(), 'ESTUDIANTE',                     'Estudiante activo con proyecto de grado en curso'),
    (gen_random_uuid(), 'ASESOR',                         'Docente asesor de proyectos de grado'),
    (gen_random_uuid(), 'ASESOR_FICHA',                   'Asesor asignado a la ficha perfil de un proyecto'),
    (gen_random_uuid(), 'JURADO',                         'Evaluador externo asignado como jurado de sustentación'),
    (gen_random_uuid(), 'COORDINADOR',                    'Coordinador del programa académico'),
    (gen_random_uuid(), 'ADMINISTRADOR',                  'Administrador general del sistema'),
    (gen_random_uuid(), 'BIBLIOTECARIO',                  'Responsable de la gestión de la biblioteca digital'),
    (gen_random_uuid(), 'REPRESENTANTE_COMITE_CURRICULUM', 'Representante del comité de currículo ante el programa');
```

> **Nota:** Esta es la migración `V1` — primera del contexto `seguridad`. Los roles son datos de referencia inmutables del sistema: se insertan aquí y no se crean/borran por HUs de negocio. Las HUs futuras (registrar, modificar, eliminar usuario) usarán `V2`, `V3`, etc.

---

## 11. Casos de Prueba Sugeridos

> **Tipo de Use Case: CONSULTA** — no aplican tests de ciclo de eventos (`publishEvent`, `getUnPublishedEvents`, `clearUnPublishedEvents`, ni `verify(eventPublisher).publish(...)`).

### Presupuesto orientativo

Esta HU es **mediana** (1 endpoint con filtros, paginación y múltiples escenarios): se esperan entre 15 y 25 tests en total.

---

### Tests capa `domain` (solo Value Objects nuevos)

> La consulta solo lee entidades existentes con `rebuild(...)`. `ParametroFiltroInvalidoException` solo hace `super(errorCode, msg)` — no requiere test propio (se verifica implícitamente en el use case). `Usuario` no tiene invariantes adicionales más allá de campos no nulos, por lo que se limita a un test de reconstrucción.

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `UsuarioTest` | `debeReconstruirUsuario_cuandoDatosValidos` | `rebuild(...)` asigna correctamente id, nombre, apellido, email, identificador, estado y roles (asserts consolidados en un solo test) |
| `EstadoUsuarioTest` | `debeResolverDesdeCode_cuandoCodeEsValido` | `EstadoUsuario.fromCode("ACTIVO")` retorna `ACTIVO`; `fromCode("INACTIVO")` retorna `INACTIVO`; code inválido lanza `IllegalArgumentException` (asserts consolidados) |

---

### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `ConsultarUsuariosUseCaseImplTest` | `debeRetornarPagina_cuandoHayUsuarios` | Flujo principal: repositorio retorna lista con un usuario → `PaginaResponseDTO` con `totalElementos`, `numeroPagina`, `tamanoPagina` y `contenido` correctos (asserts consolidados) |
| `ConsultarUsuariosUseCaseImplTest` | `debeRetornarPaginaVacia_cuandoNoHayUsuarios` | Repositorio retorna lista vacía → `totalElementos = 0` y `contenido` vacío |
| `ConsultarUsuariosUseCaseImplTest` | `debeLanzarExcepcion_cuandoFiltrosInvalidos` | Estado inválido lanza `ParametroFiltroInvalidoException` con `errorCode = "FILTRO_ESTADO_INVALIDO"`; rol inválido lanza con `errorCode = "FILTRO_ROL_INVALIDO"` (dos escenarios, consolidar tipo + errorCode en cada assert) |

> **Nota de consolidación:** los dos escenarios de filtro inválido (estado y rol) tienen el mismo "Act" estructural — se incluyen como métodos separados porque el parámetro que falla es distinto, pero cada uno consolida la verificación de tipo de excepción + errorCode en un solo test, sin duplicar asserts.

---

### Tests capa `infrastructure`

#### Adapter de repositorio (H2 en memoria)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `UsuarioRepositoryAdapterTest` | `debeBuscarYReconstruirConRebuild_cuandoNoHayFiltros` | Sin filtros retorna todos los usuarios; verifica que el adapter usa `rebuild(...)` al mapear desde JPA (asserts de lista + tipo de factory consolidados) |
| `UsuarioRepositoryAdapterTest` | `debeFiltrarPorEstado_cuandoEstadoEsActivo` | Solo retorna usuarios con `estado = ACTIVO` |
| `UsuarioRepositoryAdapterTest` | `debeFiltrarPorNombreOEmail_cuandoBusquedaParcialCoincide` | Búsqueda parcial case-insensitive sobre nombre, apellido o email |
| `UsuarioRepositoryAdapterTest` | `debeFiltrarPorRol_cuandoRolEsCoordinador` | Solo retorna usuarios con rol `COORDINADOR` |
| `UsuarioRepositoryAdapterTest` | `debeContarYPaginar_cuandoHayMasRegistrosQueElTamano` | `contarConFiltros(...)` retorna el total correcto; la segunda página retorna los registros correctos (asserts de count + paginación consolidados) |

#### Controller (Spring Security Test + `@MockitoBean`)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `UsuarioControllerTest` | `debeRetornar200ConHeaders_cuandoAdministradorConsulta` | JWT con rol `ADMINISTRADOR` → HTTP 200 con lista de usuarios y headers `X-Total-Count`, `X-Page-Number`, `X-Page-Size` presentes (asserts de status + headers consolidados) |
| `UsuarioControllerTest` | `debeRetornar200ConListaVacia_cuandoNoHayCoincidencias` | Use case retorna página vacía → HTTP 200, lista vacía y `X-Total-Count: 0` |
| `UsuarioControllerTest` | `debeRetornar400_cuandoFiltroInvalido` | `?estado=INVALIDO` → use case lanza `ParametroFiltroInvalidoException` → HTTP 400 con mensaje descriptivo |
| `UsuarioControllerTest` | `debeRetornar401_cuandoNoHayToken` | Sin JWT → HTTP 401 |
| `UsuarioControllerTest` | `debeRetornar403_cuandoRolInsuficiente` | JWT con rol `ESTUDIANTE` → HTTP 403 |

---

## 12. Checklist de Implementación

### ✅ Ya implementado (HU-260 base)
- [x] **DDD:** Entidad `Usuario` plana, inmutable, con `keycloakUserId UUID`, sin `AggregateRoot`
- [x] Enums `EstadoUsuario` y `UsuarioRole` definidos en `domain/model/`
- [x] Modelo `Usuario` con `List<UsuarioRole> roles` (tipado, no strings)
- [x] Puertos `ConsultarUsuariosUseCase` y `UsuarioRepositoryPort` definidos
- [x] Excepción `ParametroFiltroInvalidoException` extiende `DomainException`
- [x] DTOs `UsuarioFiltroDTO`, `UsuarioResponseDTO`, `PaginaResponseDTO`
- [x] `ConsultarUsuariosUseCaseImpl` con validación POL-04
- [x] `UsuarioController` con `@PreAuthorize("hasRole('ADMINISTRADOR')")` y ADR-011
- [x] `RolJpaEntity`, `UsuarioJpaEntity` con `@ManyToMany` y `keycloak_user_id UUID`
- [x] `UsuarioJpaRepository` con `JpaSpecificationExecutor`
- [x] `UsuarioRepositoryAdapter` con `Specification` dinámica y traducción `RolJpaEntity → UsuarioRole`
- [x] `V1__crear_schema_usuarios.sql` con tablas `usuario`, `rol`, `usuario_rol`, índices y 8 INSERT de roles
- [x] `UsuarioRole.java` compilado y testeado

### ✅ Completado (normalización estado) — 2026-04-27
- [x] **`EstadoUsuario.java`** — agregar constructor con `code`/`description` y métodos `getCode()`, `getDescription()`, `fromCode(String)`
- [x] **`EstadoUsuarioJpaEntity.java`** — crear: `@Entity @Table(schema = "usuarios", name = "estado_usuario")` con campos `UUID id`, `String nombre`, `String descripcion`
- [x] **`UsuarioJpaEntity.java`** — reemplazar `@Enumerated @Column(estado)` por `@ManyToOne` hacia `EstadoUsuarioJpaEntity` con columna `estado_id`
- [x] **`UsuarioRepositoryAdapter.java`** — actualizar `toDomain()`: `EstadoUsuario.fromCode(entity.getEstado().getNombre())` en lugar de `entity.getEstado()` directo
- [x] **`V2__normalizar_estado_usuario.sql`** — crear tabla `estado_usuario`, insertar 2 registros, agregar `estado_id UUID FK`, migrar datos desde `estado` VARCHAR, eliminar columna y CHECK anterior
### ✅ Completado (deuda técnica resuelta) — 2026-04-28
- [x] **`GlobalExceptionHandler.java`** — `@ExceptionHandler(ParametroFiltroInvalidoException.class)` → HTTP 400 registrado. `shared:exceptions` añadido al classpath de `seguridad:infrastructure` para resolver la jerarquía de herencia en compilación.
- [x] Compilado con `./gradlew :seguridad:infrastructure:compileJava` — sin errores
- [x] `./gradlew :seguridad:infrastructure:test` — todos los tests de controller pasan (incluido `debeRetornar400_cuandoFiltroInvalido`)
- [x] `./gradlew build -x test` (monorepo completo) — sin errores

---

## 13. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-04-28 | Handler ParametroFiltroInvalidoException → HTTP 400 registrado. shared:exceptions añadido a infra classpath. Build -x test: sin errores. |
| Tests      | @tester             | ✅ Completado | 2026-04-28 | 16 tests (2 domain, 4 application, 10 infrastructure). Todos pasan. JaCoCo no configurado por módulo — cobertura estimada por revisión manual. |
| Validación | @validator-analyze  | ✅ Completado | 2026-04-28 | Score: 94/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-04-28 | /.workspace/validator/validator-HU-260.md |
| Commit     | @commit             | ⏳ Pendiente |       |       |
