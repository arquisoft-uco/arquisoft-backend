# PLAN: Registrar Nueva Información Ficha Perfil

## Metadata
- **ID Historia:** HU-208
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura
- **¿Usa EventEmittingEntity?:** Sí (la entidad `FichaPerfil` ya extiende `EventEmittingEntity` y existe desde HUs previas de consulta)
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-05-05
- **Rama sugerida:** `feature/HU-208-registrar-ficha-perfil`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/01_base_datos_y_esquemas.sql`
    - `mer/03_tablas_fichas_perfil.sql`
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** Ninguna. El contexto fichas ya tiene implementación parcial con entidad FichaPerfil, AsesorFicha, y el caso de uso de consulta paginada (ConsultarFichasPerfilUseCase). Esta es la primera HU de escritura.

---

## 1. Resumen Funcional

Permite que un **Asesor Ficha** o **Coordinador** registre una nueva Ficha de Perfil de proyecto de grado. El endpoint recibe título del proyecto, ID del asesor y una lista de 1 a 3 IDs de estudiantes. Al registrar, el sistema valida la unicidad del título, la existencia del asesor y estudiantes en la BD local (`fichas_perfil`), y crea automáticamente el primer `EstadoFichaPerfil` con estado "En elaboración". **No emite eventos de dominio** (CRUD interno sin consumidores conocidos).

**NO cubre:** modificación, eliminación, ni consulta de fichas (estas ya existen o son HUs separadas).

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | `POST /fichas-perfil` con datos válidos por parte de COORDINADOR o ASESOR_FICHA | 201 Created + `{ "id": "..." }` |
| 2 | Enviar formulario con título vacío, sin asesor, o sin estudiantes | 400 Bad Request |
| 3 | Enviar `idEstudiantes` vacío, con más de 3, o con duplicados | 400 Bad Request |
| 4 | Enviar título que ya existe en el sistema | 409 Conflict con mensaje informativo |
| 5 | Enviar `idAsesorFicha` que no existe en la BD local | 404 Not Found |
| 6 | Enviar `idEstudiantes` que no existen en la BD local | 400 Bad Request (estudiantes no encontrados) |
| 7 | Al registrar exitosamente, se crea `EstadoFichaPerfil` con estado "En elaboración" | Existe registro en tabla `estado_ficha_perfil` con FK al estado "En elaboración" |
| 8 | Usuario no autenticado | 401 Unauthorized |
| 9 | Usuario autenticado sin rol ASESOR_FICHA ni COORDINADOR | 403 Forbidden |

---

## 3. Reglas de Negocio

- **FichaPerfil-POL-01**: Solo el Asesor Ficha y el Coordinador pueden registrar fichas. → `@PreAuthorize("hasAnyRole('ASESOR_FICHA','COORDINADOR')")`
- **FichaPerfil-POL-02**: El `titulo` del proyecto debe ser único en el sistema. → Validación de unicidad previa a guardar; excepción `TituloFichaDuplicadoException` (409).
- **FichaPerfil-POL-03**: Se deben asignar entre 1 y 3 estudiantes por ficha. → Validación `@Size(min=1, max=3)` en DTO + validación en use case.
- **FichaPerfil-POL-04**: No se puede asignar el mismo estudiante más de una vez en la misma ficha. → Validación de duplicados en use case (Set.size() == list.size()).
- Al registrar la ficha, el sistema crea automáticamente el primer `EstadoFichaPerfil` con el estado inicial "En elaboración". → Se inserta un registro en `estado_ficha_perfil` con FK al estado `ESTADO_FICHA_EN_ELABORACION_ID`.
- Cuando el actor es ASESOR_FICHA, el `idAsesorFicha` debe coincidir con el ID del usuario autenticado (extraído del JWT). → Validación en controller/use case.

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `FichaPerfil` (YA EXISTE — extiende `EventEmittingEntity` de `shared:domain`)
- **ID:** `UUID`
- **Value Objects / Entidades hijas NUEVAS:** `Estudiante`, `EstudianteFichaPerfil`, `EstadoFicha`, `EstadoFichaPerfil`

### Atributos por objeto de dominio

#### `FichaPerfil` (YA EXISTE — sin cambios en esta HU)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí (`UUID.randomUUID()`) | Identifica el registro |
| `tituloProyecto` | `String` | 1–100 | Sí | No | No | UNIQUE, limpiar espacios |
| `asesorFicha` | `AsesorFicha` | — | Sí | No | No | Referencia al asesor |

**Combinaciones únicas:** `tituloProyecto` → UNIQUE en BD + validación previa en use case.

#### `Estudiante` (NUEVO — réplica local de `usuarios`)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | No | Identifica el registro (viene de seguridad) |
| `identificador` | `String` | 4–30 | Sí | No | No | Limpiar espacios |
| `nombre` | `String` | 2–50 | Sí | No | No | Limpiar espacios |
| `email` | `String` | 6–50 | Sí | No | No | Formato email, limpiar espacios |

#### `EstudianteFichaPerfil` (NUEVO — entidad hija del aggregate)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | |
| `fichaPerfilId` | `UUID` | — | Sí | No | No | FK a ficha_perfil |
| `estudianteId` | `UUID` | — | Sí | No | No | FK a estudiante |

**Combinaciones únicas:** `(fichaPerfilId, estudianteId)` → UNIQUE en BD.

#### `EstadoFicha` (NUEVO — catálogo de estados)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | |
| `nombre` | `String` | 1–20 | Sí | No | No | UNIQUE |
| `descripcion` | `String` | 1–200 | Sí | No | No | |

#### `EstadoFichaPerfil` (NUEVO — trazabilidad de estados)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | |
| `fichaPerfilId` | `UUID` | — | Sí | No | No | FK a ficha_perfil |
| `estadoFichaId` | `UUID` | — | Sí | No | No | FK a estado_ficha |
| `fechaActualizacion` | `LocalDateTime` | — | Sí | No | Sí (now) | |

### Eventos de Dominio que emite

Eventos: ninguno.
Razón: CRUD interno sin consumidores conocidos ni casos de auditoría identificados. El usuario confirmó "No emitir eventos".
Implicación: el factory `build(...)` NO llama a `publishEvent(...)`. El use case NO inyecta `EventPublisher`.

---

## 5. Integraciones Externas

No aplica. Esta HU solo usa PostgreSQL (a través de los adaptadores de persistencia existentes) y autenticación JWT de Keycloak (ya configurada en Spring Security). No requiere nuevos puertos externos.

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/in/RegistrarFichaPerfilUseCase.java` | Interface (puerto in) | Contrato del caso de uso de registro de ficha |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/AsesorFichaRepositoryPort.java` | Interface (puerto out) | Contrato para consultar existencia de asesor en BD local |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/EstudianteRepositoryPort.java` | Interface (puerto out) | Contrato para consultar existencia de estudiantes en BD local |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/Estudiante.java` | Entidad hija | Réplica local de estudiante — `rebuild(id, identificador, nombre, email)` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/EstudianteFichaPerfil.java` | Entidad hija | Relación entre ficha y estudiante — `build(fichaPerfilId, estudianteId)` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/EstadoFicha.java` | Entidad hija / Value Object | Catálogo de estados — `rebuild(id, nombre, descripcion)` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/EstadoFichaPerfil.java` | Entidad hija | Trazabilidad de cambio de estado — `build(fichaPerfilId, estadoFichaId, fechaActualizacion)` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/exception/TituloFichaDuplicadoException.java` | Exception | extiende `DomainException` — errorCode `TITULO_FICHA_DUPLICADO` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/exception/AsesorFichaNoEncontradoException.java` | Exception | extiende `DomainException` — errorCode `ASESOR_FICHA_NO_ENCONTRADO` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/exception/EstudiantesInvalidosException.java` | Exception | extiende `DomainException` — errorCode `ESTUDIANTES_INVALIDOS` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/dto/RegistrarFichaPerfilRequestDTO.java` | DTO | Request body con `@NotBlank titulo`, `@NotNull idAsesorFicha`, `@Size(min=1,max=3) idEstudiantes` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/dto/FichaPerfilCreadaResponseDTO.java` | DTO | Response con `UUID id` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/usecase/RegistrarFichaPerfilUseCaseImpl.java` | UseCase Impl | Orquesta: validar unicidad, validar asesor/estudiantes, construir FichaPerfil, guardar, crear EstudianteFichaPerfil y EstadoFichaPerfil |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/AsesorFichaJpaRepository.java` | JPA Repo | `existsById(UUID)` — para validar existencia de asesor |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/AsesorFichaRepositoryAdapter.java` | Adapter | Implementa `AsesorFichaRepositoryPort` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstudianteJpaEntity.java` | JPA Entity | `@Table(name = "estudiante")` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstudianteJpaRepository.java` | JPA Repo | `existsById(UUID)`, `findAllById(List<UUID>)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstudianteRepositoryAdapter.java` | Adapter | Implementa `EstudianteRepositoryPort` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstudianteFichaPerfilJpaEntity.java` | JPA Entity | `@Table(name = "estudiante_ficha_perfil")` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstudianteFichaPerfilJpaRepository.java` | JPA Repo | `saveAll(List<>)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstadoFichaJpaEntity.java` | JPA Entity | `@Table(name = "estado_ficha")` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstadoFichaJpaRepository.java` | JPA Repo | `findByNombre(String)`, `existsById(UUID)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstadoFichaPerfilJpaEntity.java` | JPA Entity | `@Table(name = "estado_ficha_perfil")` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstadoFichaPerfilJpaRepository.java` | JPA Repo | `save(EstadoFichaPerfilJpaEntity)` |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/V1__1__crear_tablas_soporte_fichas.sql` | Flyway | Crea tablas `estudiante`, `estudiante_ficha_perfil`, `estado_ficha`, `estado_ficha_perfil` + seed "En elaboración" |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/FichaPerfilRepositoryPort.java` | Añadir métodos: `FichaPerfil guardar(FichaPerfil)`, `boolean existePorTitulo(String)`, `Optional<FichaPerfil> findById(UUID)` |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilRepositoryAdapter.java` | Implementar `guardar`, `existePorTitulo`, `findById` |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilJpaRepository.java` | Añadir `boolean existsByTituloProyectoIgnoreCase(String tituloProyecto)` |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichaPerfilController.java` | Añadir endpoint `POST /fichas-perfil` con `@PreAuthorize("hasAnyRole('ASESOR_FICHA','COORDINADOR')")` |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichasGlobalExceptionHandler.java` | Añadir `@ExceptionHandler` para `TituloFichaDuplicadoException` (409), `AsesorFichaNoEncontradoException` (404), `EstudiantesInvalidosException` (400) |

### Manejo de Errores HTTP (`@ExceptionHandler`)

| Capa | Ruta | Tipo | Acción | Mapeo HTTP |
|------|------|------|--------|------------|
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichasGlobalExceptionHandler.java` | Handler | MODIFICAR — añadir 3 handlers | `TituloFichaDuplicadoException` → 409, `AsesorFichaNoEncontradoException` → 404, `EstudiantesInvalidosException` → 400 |

---

## 7. Detalle por Archivo

### `RegistrarFichaPerfilUseCase.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.in`
- **Tipo:** Interface (puerto de entrada)
- **Responsabilidad:** Contrato del caso de uso que registra una nueva FichaPerfil con sus estudiantes y estado inicial.
- **Métodos principales:**
    - `FichaPerfilCreadaResponseDTO ejecutar(RegistrarFichaPerfilRequestDTO request, UUID idAsesorAutenticado)` — ejecuta el registro
- **Dependencias:** `FichaPerfilCreadaResponseDTO`, `RegistrarFichaPerfilRequestDTO`, `java.util.UUID`

### `AsesorFichaRepositoryPort.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.out`
- **Tipo:** Interface (puerto de salida)
- **Responsabilidad:** Consultar existencia de asesores en BD local.
- **Métodos principales:**
    - `boolean existePorId(UUID id)` — verifica si el asesor existe en `asesor_ficha`

### `EstudianteRepositoryPort.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.out`
- **Tipo:** Interface (puerto de salida)
- **Responsabilidad:** Consultar existencia de estudiantes en BD local.
- **Métodos principales:**
    - `boolean existenTodos(List<UUID> ids)` — verifica que todos los estudiantes existen en `estudiante`

### `Estudiante.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad hija (inmutable, sin EventEmittingEntity porque no es Aggregate Root)
- **Responsabilidad:** Réplica local de un estudiante del contexto `seguridad`.
- **Features Java 21 aplicables:** Ninguna específica — clase inmutable tradicional.
- **Métodos principales:**
    - `static Estudiante rebuild(UUID id, String identificador, String nombre, String email)` — reconstruye desde persistencia
- **Dependencias:** `java.util.UUID`

### `EstudianteFichaPerfil.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad hija (inmutable, sin EventEmittingEntity)
- **Responsabilidad:** Relación entre una FichaPerfil y un Estudiante (tabla de asociación).
- **Métodos principales:**
    - `static EstudianteFichaPerfil build(UUID fichaPerfilId, UUID estudianteId)` — crea con UUID autogenerado
    - `static EstudianteFichaPerfil rebuild(UUID id, UUID fichaPerfilId, UUID estudianteId)` — reconstruye desde persistencia
- **Dependencias:** `java.util.UUID`

### `EstadoFicha.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad de referencia (inmutable)
- **Responsabilidad:** Catálogo de estados posibles de una ficha de perfil (ej. "En elaboración").
- **Métodos principales:**
    - `static EstadoFicha rebuild(UUID id, String nombre, String descripcion)`
- **Dependencias:** `java.util.UUID`

### `EstadoFichaPerfil.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad hija (inmutable)
- **Responsabilidad:** Trazabilidad de cambios de estado de una ficha de perfil.
- **Métodos principales:**
    - `static EstadoFichaPerfil build(UUID fichaPerfilId, UUID estadoFichaId)` — crea con UUID autogenerado y fecha actual
    - `static EstadoFichaPerfil rebuild(UUID id, UUID fichaPerfilId, UUID estadoFichaId, LocalDateTime fechaActualizacion)` — reconstruye
- **Dependencias:** `java.util.UUID`, `java.time.LocalDateTime`

### `TituloFichaDuplicadoException.java`
- **Paquete:** `com.arquisoft.fichas.domain.exception`
- **Tipo:** Exception (extiende `DomainException`)
- **Responsabilidad:** Indica que el título de la ficha ya existe. HTTP 409.
- **Dependencias:** `com.arquisoft.shared.exceptions.DomainException`

### `AsesorFichaNoEncontradoException.java`
- **Paquete:** `com.arquisoft.fichas.domain.exception`
- **Tipo:** Exception (extiende `DomainException`)
- **Responsabilidad:** Indica que el asesor referenciado no existe en BD local. HTTP 404.
- **Dependencias:** `com.arquisoft.shared.exceptions.DomainException`

### `EstudiantesInvalidosException.java`
- **Paquete:** `com.arquisoft.fichas.domain.exception`
- **Tipo:** Exception (extiende `DomainException`)
- **Responsabilidad:** Indica que los estudiantes son inválidos (no existen, duplicados, fuera de rango). HTTP 400.
- **Dependencias:** `com.arquisoft.shared.exceptions.DomainException`

### `RegistrarFichaPerfilRequestDTO.java`
- **Paquete:** `com.arquisoft.fichas.application.dto`
- **Tipo:** DTO (`@Data @NoArgsConstructor @AllArgsConstructor @Builder`)
- **Responsabilidad:** Recibir datos del request POST.
- **Campos con validación Jakarta:**
    - `@NotBlank @Size(min=1, max=100) String titulo`
    - `@NotNull UUID idAsesorFicha`
    - `@NotNull @Size(min=1, max=3) List<@NotNull UUID> idEstudiantes`
- **Features Java 21 aplicables:** Ninguna específica.
- **Dependencias:** Jakarta Validation, Jackson, Lombok

### `FichaPerfilCreadaResponseDTO.java`
- **Paquete:** `com.arquisoft.fichas.application.dto`
- **Tipo:** DTO (`@Data @NoArgsConstructor @AllArgsConstructor @Builder`)
- **Responsabilidad:** Retornar el ID de la ficha recién creada.
- **Campos:** `UUID id`
- **Dependencias:** Lombok

### `RegistrarFichaPerfilUseCaseImpl.java`
- **Paquete:** `com.arquisoft.fichas.application.usecase`
- **Tipo:** UseCase Impl (`@Service`, `@RequiredArgsConstructor`, `@Slf4j`, `@Transactional`)
- **Responsabilidad:** Orquestar el registro completo:
    1. Validar unicidad del título → `fichaPerfilRepositoryPort.existePorTitulo(titulo)` → si true, lanza `TituloFichaDuplicadoException`
    2. Validar que el asesor existe → `asesorFichaRepositoryPort.existePorId(idAsesorFicha)` → si false, lanza `AsesorFichaNoEncontradoException`
    3. Validar que los estudiantes existen (todos) → `estudianteRepositoryPort.existenTodos(idEstudiantes)` → si false, lanza `EstudiantesInvalidosException`
    4. Validar que no hay duplicados en `idEstudiantes` → `new HashSet<>(idEstudiantes).size() == idEstudiantes.size()`
    5. Construir `FichaPerfil` con `build(titulo, asesorFicha)` (el AsesorFicha se reconstruye consultando la BD)
    6. Guardar `FichaPerfil` → `fichaPerfilRepositoryPort.guardar(ficha)`
    7. Crear `EstudianteFichaPerfil` por cada estudiante
    8. Crear `EstadoFichaPerfil` con estado "En elaboración" (consultando `estadoFichaJpaRepository.findByNombre("En elaboración")`)
    9. Retornar `FichaPerfilCreadaResponseDTO` con el ID generado
- **NO inyecta `EventPublisher`** (sin eventos).
- **Dependencias:** `FichaPerfilRepositoryPort`, `AsesorFichaRepositoryPort`, `EstudianteRepositoryPort`, puertos JPA de las entidades hijas (para guardar estudiantes y estados)

### `AsesorFichaJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Repository (`extends JpaRepository<AsesorFichaJpaEntity, UUID>`)
- **Responsabilidad:** Acceso a datos de la tabla `asesor_ficha`.

### `AsesorFichaRepositoryAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** Adapter (`@Component`, `@RequiredArgsConstructor`)
- **Responsabilidad:** Implementar `AsesorFichaRepositoryPort.existePorId(UUID)` delegando en `AsesorFichaJpaRepository.existsById(UUID)`.

### `EstudianteJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "estudiante")`)
- **Campos:** `id` (UUID PK), `identificador` (VARCHAR 30), `nombre` (VARCHAR 50), `email` (VARCHAR 50)
- **Método:** `Estudiante toDomain()` → `Estudiante.rebuild(id, identificador, nombre, email)`

### `EstudianteJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Repository (`extends JpaRepository<EstudianteJpaEntity, UUID>`)
- **Métodos:** `boolean existsById(UUID id)`, `List<EstudianteJpaEntity> findAllById(Iterable<UUID> ids)`, `long countByIdIn(Collection<UUID> ids)`

### `EstudianteRepositoryAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** Adapter (`@Component`, `@RequiredArgsConstructor`)
- **Responsabilidad:** Implementar `EstudianteRepositoryPort.existenTodos(List<UUID>)` comparando `countByIdIn(ids) == ids.size()`.

### `EstudianteFichaPerfilJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "estudiante_ficha_perfil")`)
- **Campos:** `id` (UUID PK), `fichaPerfilId` (UUID, FK a ficha_perfil), `estudianteId` (UUID, FK a estudiante)
- **Constraints:** `@Column(nullable=false)` en todos los campos. El UNIQUE se define en la migración Flyway.

### `EstudianteFichaPerfilJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Repository (`extends JpaRepository<EstudianteFichaPerfilJpaEntity, UUID>`)
- **Usado por:** `RegistrarFichaPerfilUseCaseImpl` para `saveAll(...)`.

### `EstadoFichaJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "estado_ficha")`)
- **Campos:** `id` (UUID PK), `nombre` (VARCHAR 20, nullable=false), `descripcion` (VARCHAR 200, nullable=false)

### `EstadoFichaJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Repository (`extends JpaRepository<EstadoFichaJpaEntity, UUID>`)
- **Métodos:** `Optional<EstadoFichaJpaEntity> findByNombre(String nombre)`

### `EstadoFichaPerfilJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "estado_ficha_perfil")`)
- **Campos:** `id` (UUID PK), `fichaPerfilId` (UUID, FK), `estadoFichaId` (UUID, FK), `fechaActualizacion` (TIMESTAMP, nullable=false)

### `EstadoFichaPerfilJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Repository (`extends JpaRepository<EstadoFichaPerfilJpaEntity, UUID>`)

### `V1__1__crear_tablas_soporte_fichas.sql`
- **Archivo:** `fichas/infrastructure/src/main/resources/db/migration/V1__1__crear_tablas_soporte_fichas.sql`
- **Base de datos:** `fichas_perfil`
- **Contenido:** Creación de tablas `estudiante`, `estudiante_ficha_perfil`, `estado_ficha`, `estado_ficha_perfil` + inserción del seed "En elaboración" en `estado_ficha`.

---

## 8. Endpoints REST

| Método | Ruta | Request Body | Response | Código HTTP | Roles permitidos | Anotaciones Swagger (ADR-011) |
|--------|------|--------------|----------|-------------|-----------------|-------------------------------|
| POST | `/api/fichas-perfil` | `RegistrarFichaPerfilRequestDTO` | `FichaPerfilCreadaResponseDTO` | 201 | `ASESOR_FICHA`, `COORDINADOR` | `@Operation(summary="Registrar nueva ficha de perfil")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses` con 201, 400, 401, 403, 404, 409 |

---

## 9. Eventos RabbitMQ

No aplica. Esta HU no emite eventos de dominio (decisión confirmada por el usuario).

---

## 10. Migración de Base de Datos

- **Archivo:** `V1__1__crear_tablas_soporte_fichas.sql`
- **Base de datos:** `fichas_perfil`
- **Sin schemas:** las tablas se crean sin prefijo (ej. `CREATE TABLE estudiante (...)`)
- **Cambios:**

```sql
-- Tabla de estudiantes (réplica local de usuarios/seguridad)
CREATE TABLE estudiante (
    id            UUID         NOT NULL,
    identificador VARCHAR(30)  NOT NULL,
    nombre        VARCHAR(50)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id)
);

-- Tabla de asociación ficha ↔ estudiante
CREATE TABLE estudiante_ficha_perfil (
    id              UUID NOT NULL,
    ficha_perfil_id UUID NOT NULL,
    estudiante_id   UUID NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_estudiante_ficha UNIQUE (ficha_perfil_id, estudiante_id),
    CONSTRAINT fk_efp_ficha FOREIGN KEY (ficha_perfil_id) REFERENCES ficha_perfil(id) ON DELETE CASCADE,
    CONSTRAINT fk_efp_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiante(id)
);

-- Catálogo de estados de ficha
CREATE TABLE estado_ficha (
    id          UUID         NOT NULL,
    nombre      VARCHAR(20)  NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_estado_ficha_nombre UNIQUE (nombre)
);

-- Trazabilidad de cambios de estado
CREATE TABLE estado_ficha_perfil (
    id                  UUID      NOT NULL,
    ficha_perfil_id     UUID      NOT NULL,
    estado_ficha_id     UUID      NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_efp_traz_ficha FOREIGN KEY (ficha_perfil_id) REFERENCES ficha_perfil(id) ON DELETE CASCADE,
    CONSTRAINT fk_efp_traz_estado FOREIGN KEY (estado_ficha_id) REFERENCES estado_ficha(id)
);

-- Seed: estado inicial "En elaboración" (requerido por regla de negocio)
INSERT INTO estado_ficha (id, nombre, descripcion)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'En elaboracion', 'La ficha de perfil se encuentra en proceso de elaboración por los estudiantes');
```

---

## 11. Casos de Prueba Sugeridos

### Presupuesto orientativo: ~25 tests (HU pequeña-mediana, 1 endpoint nuevo con validaciones)

---

### Tests capa `domain` (nuevos Value Objects y excepciones)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EstudianteFichaPerfilTest` | `debeConstruirConIdsValidos_cuandoBuildEsInvocado` | `build(fichaId, estudianteId)` crea entidad con UUID y campos correctos |
| `EstudianteFichaPerfilTest` | `debeNoAcumularEventos_cuandoBuildEsInvocado` | entidad hija NO es EventEmittingEntity — confirmar que no tiene `getUnPublishedEvents()` |
| `EstadoFichaPerfilTest` | `debeConstruirConFechaActual_cuandoBuildEsInvocado` | `build(fichaId, estadoFichaId)` asigna `fechaActualizacion = now()` |
| `EstudianteTest` | `debeReconstruir_cuandoRebuildEsInvocado` | `rebuild(id, identificador, nombre, email)` retorna instancia con datos correctos |

### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `RegistrarFichaPerfilUseCaseImplTest` | `debeRegistrarFicha_cuandoDatosValidos` | flujo exitoso: guarda ficha + estudianteFichaPerfil + estadoFichaPerfil, retorna ID |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarTituloDuplicado_cuandoTituloYaExiste` | `existePorTitulo` retorna true → `TituloFichaDuplicadoException` (errorCode + mensaje consolidados) |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarAsesorNoEncontrado_cuandoAsesorNoExiste` | `existePorId(asesor)` retorna false → `AsesorFichaNoEncontradoException` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarEstudiantesInvalidos_cuandoEstudiantesNoExisten` | `existenTodos(ids)` retorna false → `EstudiantesInvalidosException` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarEstudiantesInvalidos_cuandoListaVacia` | `idEstudiantes` vacía → `EstudiantesInvalidosException` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarEstudiantesInvalidos_cuandoHayDuplicados` | `idEstudiantes` con duplicados → `EstudiantesInvalidosException` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarEstudiantesInvalidos_cuandoMasDeTres` | validado por Jakarta en controller; use case puede tener defensa adicional |

### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `FichaPerfilRepositoryAdapterTest` | `debeGuardarFicha_cuandoEntidadEsValida` | `guardar(ficha)` persiste correctamente |
| `FichaPerfilRepositoryAdapterTest` | `debeRetornarTrue_cuandoTituloExiste` | `existePorTitulo("existente")` → true |
| `FichaPerfilRepositoryAdapterTest` | `debeRetornarFalse_cuandoTituloNoExiste` | `existePorTitulo("inexistente")` → false |
| `EstudianteRepositoryAdapterTest` | `debeRetornarTrue_cuandoTodosExisten` | `existenTodos(idsValidos)` → true |
| `EstudianteRepositoryAdapterTest` | `debeRetornarFalse_cuandoAlgunoNoExiste` | `existenTodos(idsConInexistente)` → false |
| `FichaPerfilControllerTest` | `debe201_cuandoPeticionValida` | POST con datos válidos → 201 + `FichaPerfilCreadaResponseDTO` |
| `FichaPerfilControllerTest` | `debe400_cuandoRequestInvalido` | título vacío / estudiantes vacíos → 400 |
| `FichaPerfilControllerTest` | `debe401_cuandoNoAutenticado` | sin token → 401 |
| `FichaPerfilControllerTest` | `debe403_cuandoRolInsuficiente` | token con rol ESTUDIANTE → 403 |
| `FichaPerfilControllerTest` | `debe409_cuandoTituloDuplicado` | use case lanza `TituloFichaDuplicadoException` → 409 |
| `FichaPerfilControllerTest` | `debe404_cuandoAsesorNoEncontrado` | use case lanza `AsesorFichaNoEncontradoException` → 404 |

### Reglas de consolidación aplicadas

- Las 3 excepciones simples (`TituloFichaDuplicadoException`, `AsesorFichaNoEncontradoException`, `EstudiantesInvalidosException`) **NO** tienen test propio — solo hacen `super("CODE", "msg")`. Su errorCode se verifica en el test del use case que las lanza (consolidado con el assert de tipo y mensaje).
- `EstudianteFichaPerfil` y `EstadoFichaPerfil` son entidades hijas simples sin lógica de validación → tests de construcción básica, sin duplicar asserts de getters.

---

## 12. Checklist de Implementación

- [ ] **DDD:** `FichaPerfil` ya extiende `EventEmittingEntity` — sin cambios. Nuevas entidades hijas NO extienden EventEmittingEntity.
- [ ] Entidades inmutables: constructor privado, campos `final`, factory methods `build` / `rebuild`, sin Lombok
- [ ] **Sin eventos de dominio:** `build(...)` NO llama a `publishEvent(...)`. Use case NO inyecta `EventPublisher`.
- [ ] IDs siempre `UUID` (nunca `Long` / `Integer`)
- [ ] Puerto de entrada (`RegistrarFichaPerfilUseCase`) definido en `domain/port/in/`
- [ ] Puertos de salida nuevos (`AsesorFichaRepositoryPort`, `EstudianteRepositoryPort`) definidos en `domain/port/out/`
- [ ] Puerto existente `FichaPerfilRepositoryPort` modificado con `guardar`, `existePorTitulo`, `findById` — **retorna entidades de dominio, no DTOs**
- [ ] Excepciones de dominio definidas (`TituloFichaDuplicadoException`, `AsesorFichaNoEncontradoException`, `EstudiantesInvalidosException`), extienden `DomainException` y tienen `errorCode`
- [ ] **Toda excepción nueva registrada en `FichasGlobalExceptionHandler`** con `@ExceptionHandler` y código HTTP correcto: 409, 404, 400 respectivamente
- [ ] DTOs con `toDomain()` / `fromDomain()` y anotaciones Jakarta Validation (`@NotBlank`, `@Size`, `@NotNull`)
- [ ] Caso de uso (`RegistrarFichaPerfilUseCaseImpl`) con `@RequiredArgsConstructor`, `@Transactional` y validación de reglas de negocio
- [ ] Controller REST extendido en `FichaPerfilController` con `@Valid @RequestBody` y `@PreAuthorize("hasAnyRole('ASESOR_FICHA','COORDINADOR')")`
- [ ] Controller documentado con `@Tag` (ya existe), `@Operation`, `@ApiResponses` y `@SecurityRequirement` (ADR-011) en el nuevo endpoint
- [ ] JPA Entities creadas: `EstudianteJpaEntity`, `EstudianteFichaPerfilJpaEntity`, `EstadoFichaJpaEntity`, `EstadoFichaPerfilJpaEntity`
- [ ] JPA Repositories y Adapters creados para asesor, estudiante, estudiante_ficha_perfil, estado_ficha, estado_ficha_perfil
- [ ] Adapter de repositorio modificado: `FichaPerfilRepositoryAdapter` implementa `guardar`, `existePorTitulo`, `findById`
- [ ] Migración Flyway `V1__1__crear_tablas_soporte_fichas.sql` en `fichas_perfil` con 4 tablas + seed "En elaboración"
- [ ] Sin eventos RabbitMQ (publicación ni consumo)
- [ ] Tests unitarios con patrón AAA (cobertura ≥ 75%)
- [ ] Tests de repositorio con H2
- [ ] Tests de controller con Spring Security Test y `@MockitoBean`
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot)
- [ ] Commit: `feat(fichas): registrar nueva ficha de perfil`

---

## 13. Trazabilidad del Flujo

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ⏳ Pendiente |       |       |
| Tests      | @tester             | ⏳ Pendiente |       |       |
| Validación | @validator-analyze  | ⏳ Pendiente |       |       |
| Reporte    | @validator-report   | ⏳ Pendiente |       |       |
| Commit     | @commit             | ⏳ Pendiente |       |       |
