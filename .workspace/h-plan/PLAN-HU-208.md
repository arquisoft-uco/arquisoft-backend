

# PLAN: Registrar Nueva Información Ficha Perfil

## Metadata
- **ID Historia:** HU-208
- **Bounded Context:** `fichas`
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-04-25
- **Rama sugerida:** `feature/HU-208-registrar_nueva_ficha_perfil`
- **Fuentes consultadas del repo de documentación:**
  - Información proporcionada directamente por el usuario (HU completa, Event Storming, modelo de dominio, modelo ER, contrato API, criterios de aceptación y respuestas de clarificación)
- **Observaciones del usuario:**
  - El bounded context es `fichas`; el schema PostgreSQL es `fichas_perfil` (mapeo especial según AGENTS.md).
  - La entidad `AsesorFicha` ya debe existir en el contexto (tabla `asesor_ficha` en schema `fichas_perfil`); incluir verificación de existencia del asesor via repositorio.
  - `EstadoFicha` es un catálogo; el estado inicial "En elaboración" se busca por nombre al crear la ficha.
  - `EstudianteFichaPerfil` y `EstadoFichaPerfil` son entidades hijas del Aggregate Root `FichaPerfil` (no son Aggregate Roots propios).
  - El Aggregate Root `FichaPerfil` contiene la lista de `EstudianteFichaPerfil` y el `EstadoFichaPerfil` inicial como parte de su estado interno.
  - La respuesta del endpoint solo devuelve el `id` de la ficha creada.
  - Usar `@PreAuthorize("hasAnyRole('ASESOR_FICHA', 'COORDINADOR')")` en el controller.
  - No se valida si los estudiantes existen o tienen ficha activa en este bounded context — esa responsabilidad es del frontend o de otro contexto.

---

## 1. Resumen Funcional

Esta HU implementa el caso de uso de creación de una nueva `FichaPerfil` en el bounded context `fichas`. Un Asesor Ficha o Coordinador autenticado puede registrar una ficha indicando un título único, su propio ID de asesor y entre 1 y 3 estudiantes sin duplicados. Al persistir la ficha, el sistema crea automáticamente el primer `EstadoFichaPerfil` con estado "En elaboración" y publica el evento `NuevaFichaPerfilAsignadaEvent` vía RabbitMQ. Esta HU **no** cubre la consulta, edición ni eliminación de fichas, ni la validación de existencia de estudiantes en otros contextos.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | Se envía una petición válida con título, asesorFichaId y 1–3 estudianteIds únicos | HTTP 201 con `{ "id": "<uuid>" }` |
| 2 | El título enviado ya existe en el sistema | HTTP 409 con mensaje de error informativo |
| 3 | El asesorFichaId no existe en el repositorio | HTTP 404 con mensaje de error |
| 4 | El título está vacío o supera 100 caracteres | HTTP 400 con mensaje de validación |
| 5 | `idAsesorFicha` es nulo | HTTP 400 con mensaje de validación |
| 6 | `idEstudiantes` está vacío o tiene más de 3 elementos | HTTP 400 con mensaje de validación |
| 7 | `idEstudiantes` contiene el mismo estudiante más de una vez | HTTP 400 con mensaje de validación |
| 8 | El actor no tiene rol `ASESOR_FICHA` ni `COORDINADOR` | HTTP 403 |
| 9 | La petición no incluye JWT válido | HTTP 401 |
| 10 | Registro exitoso: se crea `FichaPerfil`, 1–3 `EstudianteFichaPerfil` y 1 `EstadoFichaPerfil` con estado "En elaboración" | Persistencia correcta en las 3 tablas del schema `fichas_perfil` |
| 11 | Registro exitoso: se publica evento `NuevaFichaPerfilAsignadaEvent` en RabbitMQ | Evento publicado en exchange `arquisoft.events` con routing key `fichas.ficha_perfil.creada` |

---

## 3. Reglas de Negocio

- **FichaPerfil-POL-01:** Solo los roles `ASESOR_FICHA` y `COORDINADOR` pueden registrar fichas. Aplicado via `@PreAuthorize` en el controller.
- **FichaPerfil-POL-02:** El título del proyecto debe ser único en el sistema. Si ya existe, lanzar `FichaPerfilTituloDuplicadoException` → HTTP 409.
- **FichaPerfil-POL-03:** Se deben asignar entre 1 y 3 estudiantes por ficha. Listas vacías o con más de 3 elementos son inválidas → HTTP 400.
- **FichaPerfil-POL-04:** No se puede asignar el mismo estudiante más de una vez en la misma ficha (sin duplicados en `idEstudiantes`) → HTTP 400.
- **Regla implícita — Estado inicial:** Al registrar la ficha, el sistema crea automáticamente el primer `EstadoFichaPerfil` con el estado "En elaboración". El `EstadoFicha` se busca por nombre en el catálogo; si no existe, lanzar `EstadoFichaNoEncontradoException` → HTTP 500 (error de configuración del sistema).
- **Regla implícita — Existencia del asesor:** El `asesorFichaId` debe referenciar un `AsesorFicha` existente en el repositorio. Si no existe, lanzar `AsesorFichaNoEncontradoException` → HTTP 404.
- **Regla implícita — Evento de dominio:** El Aggregate Root `FichaPerfil` emite `FichaPerfilCreadaEvent` al ser construido via `build(...)`. El publisher de infraestructura lo transforma en `NuevaFichaPerfilAsignadaEvent` para RabbitMQ.

---

## 4. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

#### Capa `domain`

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/FichaPerfil.java` | Aggregate Root | Entidad raíz inmutable. Contiene `titulo`, `asesorFichaId`, lista de `EstudianteFichaPerfil` y `EstadoFichaPerfil` inicial. Factory methods `build(...)` y `rebuild(...)`. Emite `FichaPerfilCreadaEvent` en `build`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/EstudianteFichaPerfil.java` | Entidad hija | Entidad inmutable hija del Aggregate Root. Campos: `id`, `fichaPerfilId`, `estudianteId`. Factory methods `build(...)` y `rebuild(...)`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/EstadoFichaPerfil.java` | Entidad hija | Entidad inmutable hija del Aggregate Root. Campos: `id`, `fichaPerfilId`, `estadoFichaId`, `fechaActualizacion`. Factory methods `build(...)` y `rebuild(...)`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/EstadoFicha.java` | Value Object / Entidad catálogo | Catálogo de estados. Campos: `id`, `nombre`. Factory methods `build(...)` y `rebuild(...)`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/AsesorFicha.java` | Entidad de referencia | Entidad de referencia local. Campos: `id`, `nombre`, `email`. Factory methods `build(...)` y `rebuild(...)`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/event/FichaPerfilCreadaEvent.java` | Domain Event | Evento de dominio emitido por `FichaPerfil.build(...)`. Extiende `DomainEvent` de `shared:domain`. Payload: `fichaPerfilId`, `titulo`, `asesorFichaId`, `estudianteIds`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/in/RegistrarFichaPerfilUseCase.java` | Puerto de entrada | Interface del caso de uso. Método: `registrar(RegistrarFichaPerfilCommand): UUID`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/in/RegistrarFichaPerfilCommand.java` | Command (Value Object) | Objeto de comando inmutable. Campos: `titulo`, `asesorFichaId`, `estudianteIds`. Encapsula la intención del caso de uso. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/FichaPerfilRepositoryPort.java` | Puerto de salida | Interface de repositorio. Métodos: `guardar(FichaPerfil): FichaPerfil`, `existePorTitulo(String): boolean`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/AsesorFichaRepositoryPort.java` | Puerto de salida | Interface de repositorio. Método: `buscarPorId(UUID): Optional<AsesorFicha>`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/EstadoFichaRepositoryPort.java` | Puerto de salida | Interface de repositorio. Método: `buscarPorNombre(String): Optional<EstadoFicha>`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/exception/FichaPerfilTituloDuplicadoException.java` | Exception | Lanzada cuando el título ya existe (POL-02). Extiende `DomainException` de `shared:exceptions`. HTTP 409. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/exception/AsesorFichaNoEncontradoException.java` | Exception | Lanzada cuando el `asesorFichaId` no existe. Extiende `DomainException`. HTTP 404. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/exception/EstadoFichaNoEncontradoException.java` | Exception | Lanzada cuando el catálogo no tiene el estado "En elaboración". Extiende `DomainException`. HTTP 500. |

#### Capa `application`

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/dto/RegistrarFichaPerfilRequestDTO.java` | DTO de entrada | Recibe `titulo`, `idAsesorFicha`, `idEstudiantes`. Incluye anotaciones Jakarta Validation. Método `toCommand(): RegistrarFichaPerfilCommand`. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/dto/RegistrarFichaPerfilResponseDTO.java` | DTO de salida | Contiene solo `id: UUID`. Método estático `fromDomain(UUID id)`. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/usecase/RegistrarFichaPerfilUseCaseImpl.java` | UseCase Impl | Implementa `RegistrarFichaPerfilUseCase`. Orquesta validaciones de negocio, construcción del Aggregate Root y delegación al repositorio. Usa `@RequiredArgsConstructor`. |

#### Capa `infrastructure`

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichaPerfilController.java` | Controller REST | Expone `POST /fichas-perfil`. Incluye `@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`, `@PreAuthorize`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilJpaEntity.java` | JPA Entity | Mapea tabla `fichas_perfil.ficha_perfil`. Relaciones `@OneToMany` con `EstudianteFichaPerfilJpaEntity` y `EstadoFichaPerfilJpaEntity`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstudianteFichaPerfilJpaEntity.java` | JPA Entity | Mapea tabla `fichas_perfil.estudiante_ficha_perfil`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstadoFichaPerfilJpaEntity.java` | JPA Entity | Mapea tabla `fichas_perfil.estado_ficha_perfil`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstadoFichaJpaEntity.java` | JPA Entity | Mapea tabla `fichas_perfil.estado_ficha` (catálogo). |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/AsesorFichaJpaEntity.java` | JPA Entity | Mapea tabla `fichas_perfil.asesor_ficha`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilJpaRepository.java` | Spring Data JPA | Interface que extiende `JpaRepository<FichaPerfilJpaEntity, UUID>`. Método: `existsByTituloProyecto(String): boolean`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/AsesorFichaJpaRepository.java` | Spring Data JPA | Interface que extiende `JpaRepository<AsesorFichaJpaEntity, UUID>`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstadoFichaJpaRepository.java` | Spring Data JPA | Interface que extiende `JpaRepository<EstadoFichaJpaEntity, UUID>`. Método: `findByNombre(String): Optional<EstadoFichaJpaEntity>`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilRepositoryAdapterImpl.java` | Adapter | Implementa `FichaPerfilRepositoryPort`. Traduce entre entidades JPA y de dominio. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/AsesorFichaRepositoryAdapterImpl.java` | Adapter | Implementa `AsesorFichaRepositoryPort`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/EstadoFichaRepositoryAdapterImpl.java` | Adapter | Implementa `EstadoFichaRepositoryPort`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/messaging/FichaPerfilEventPublisher.java` | Publisher RabbitMQ | Escucha `FichaPerfilCreadaEvent` del Aggregate Root (via `ApplicationEventPublisher` de Spring) y publica `NuevaFichaPerfilAsignadaEvent` en RabbitMQ. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/messaging/NuevaFichaPerfilAsignadaEvent.java` | Mensaje RabbitMQ | POJO serializable. Campos: `fichaPerfilId`, `titulo`, `asesorFichaId`, `estudianteIds`, `fechaCreacion`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/config/RabbitMQFichaPerfilConfig.java` | Config | Define exchange `arquisoft.events`, queue `fichas.ficha_perfil.creada.queue` y binding con routing key `fichas.ficha_perfil.creada`. |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/V1__crear_tablas_fichas_perfil.sql` | Flyway | Crea las tablas `estado_ficha`, `asesor_ficha`, `ficha_perfil`, `estudiante_ficha_perfil`, `estado_ficha_perfil` en el schema `fichas_perfil`. Inserta el registro inicial del catálogo "En elaboración". |

### Archivos a MODIFICAR (si aplica)

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `shared/exceptions/src/main/java/com/arquisoft/shared/exceptions/GlobalExceptionHandler.java` | Agregar handlers para `FichaPerfilTituloDuplicadoException` (→ 409), `AsesorFichaNoEncontradoException` (→ 404) y `EstadoFichaNoEncontradoException` (→ 500). |

### Archivos de MENSAJERÍA RabbitMQ

| Capa | Ruta completa | Tipo | Evento / Cola |
|------|---------------|------|---------------|
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/messaging/FichaPerfilEventPublisher.java` | Publisher | Routing key: `fichas.ficha_perfil.creada` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/config/RabbitMQFichaPerfilConfig.java` | Config | Exchange: `arquisoft.events` / Queue: `fichas.ficha_perfil.creada.queue` |

---

## 5. Detalle por Archivo

---

### `FichaPerfil.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Aggregate Root (extiende `AggregateRoot` de `shared:domain`)
- **Responsabilidad:** Entidad raíz del bounded context `fichas`. Encapsula el título único, el ID del asesor, la lista de estudiantes asignados (1–3, sin duplicados) y el estado inicial. Emite `FichaPerfilCreadaEvent` al construirse.
- **Campos (todos `private final`):**
  - `UUID id`
  - `String titulo`
  - `UUID asesorFichaId`
  - `List<EstudianteFichaPerfil> estudiantes` (inmutable, 1–3 elementos)
  - `EstadoFichaPerfil estadoInicial`
- **Métodos principales:**
  - `build(String titulo, UUID asesorFichaId, List<UUID> estudianteIds, EstadoFicha estadoInicial): FichaPerfil` — factory estático para nuevas fichas. Genera `UUID.randomUUID()`. Construye los `EstudianteFichaPerfil` hijos. Construye el `EstadoFichaPerfil` inicial. Llama a `publishEvent(new FichaPerfilCreadaEvent(...))`.
  - `rebuild(UUID id, String titulo, UUID asesorFichaId, List<EstudianteFichaPerfil> estudiantes, EstadoFichaPerfil estadoInicial): FichaPerfil` — factory estático para reconstruir desde persistencia. No emite eventos.
  - Getters para todos los campos.
- **Dependencias:** `AggregateRoot` (shared:domain), `FichaPerfilCreadaEvent`, `EstudianteFichaPerfil`, `EstadoFichaPerfil`, `EstadoFicha`
- **Nota:** Sin Lombok, sin Spring. Constructor privado. Campos `final`.

---

### `EstudianteFichaPerfil.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad hija (no es Aggregate Root)
- **Responsabilidad:** Representa la asociación entre una `FichaPerfil` y un estudiante. Entidad inmutable.
- **Campos (todos `private final`):**
  - `UUID id`
  - `UUID fichaPerfilId`
  - `UUID estudianteId`
- **Métodos principales:**
  - `build(UUID fichaPerfilId, UUID estudianteId): EstudianteFichaPerfil` — genera `UUID.randomUUID()` para `id`.
  - `rebuild(UUID id, UUID fichaPerfilId, UUID estudianteId): EstudianteFichaPerfil` — reconstruye desde persistencia.
  - Getters para todos los campos.
- **Dependencias:** Ninguna externa.

---

### `EstadoFichaPerfil.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad hija (no es Aggregate Root)
- **Responsabilidad:** Representa el estado actual de una `FichaPerfil` en un momento dado. Inmutable.
- **Campos (todos `private final`):**
  - `UUID id`
  - `UUID fichaPerfilId`
  - `UUID estadoFichaId`
  - `LocalDateTime fechaActualizacion`
- **Métodos principales:**
  - `build(UUID fichaPerfilId, UUID estadoFichaId): EstadoFichaPerfil` — genera `UUID.randomUUID()` para `id`, asigna `LocalDateTime.now()` a `fechaActualizacion`.
  - `rebuild(UUID id, UUID fichaPerfilId, UUID estadoFichaId, LocalDateTime fechaActualizacion): EstadoFichaPerfil`
  - Getters para todos los campos.
- **Dependencias:** Ninguna externa.

---

### `EstadoFicha.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad catálogo (no es Aggregate Root)
- **Responsabilidad:** Representa un estado del catálogo de estados de ficha (ej. "En elaboración"). Solo lectura desde el dominio.
- **Campos (todos `private final`):**
  - `UUID id`
  - `String nombre`
- **Métodos principales:**
  - `rebuild(UUID id, String nombre): EstadoFicha` — reconstruye desde persistencia.
  - Getters para todos los campos.
- **Dependencias:** Ninguna externa.

---

### `AsesorFicha.java`
- **Paquete:** `com.arquisoft.fichas.domain.model`
- **Tipo:** Entidad de referencia (no es Aggregate Root)
- **Responsabilidad:** Representa al asesor de ficha en el dominio local. Solo se usa para verificar existencia.
- **Campos (todos `private final`):**
  - `UUID id`
  - `String nombre`
  - `String email`
- **Métodos principales:**
  - `rebuild(UUID id, String nombre, String email): AsesorFicha`
  - Getters para todos los campos.
- **Dependencias:** Ninguna externa.

---

### `FichaPerfilCreadaEvent.java`
- **Paquete:** `com.arquisoft.fichas.domain.event`
- **Tipo:** Domain Event (extiende `DomainEvent` de `shared:domain`)
- **Responsabilidad:** Evento emitido por `FichaPerfil.build(...)` para notificar la creación de una nueva ficha. El constructor de `DomainEvent` asigna automáticamente `eventId`, `occurredAt` y `eventType`.
- **Campos adicionales (además de los heredados):**
  - `UUID fichaPerfilId`
  - `String titulo`
  - `UUID asesorFichaId`
  - `List<UUID> estudianteIds`
- **Métodos principales:**
  - Constructor: `FichaPerfilCreadaEvent(UUID fichaPerfilId, String titulo, UUID asesorFichaId, List<UUID> estudianteIds)` — pasa `fichaPerfilId` como `aggregateId` al super constructor.
  - Getters para todos los campos.
- **Dependencias:** `DomainEvent` (shared:domain)

---

### `RegistrarFichaPerfilUseCase.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.in`
- **Tipo:** Interface (Puerto de entrada)
- **Responsabilidad:** Define el contrato del caso de uso de registro de ficha perfil.
- **Métodos principales:**
  - `registrar(RegistrarFichaPerfilCommand command): UUID` — retorna el ID de la ficha creada.
- **Dependencias:** `RegistrarFichaPerfilCommand`

---

### `RegistrarFichaPerfilCommand.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.in`
- **Tipo:** Command / Value Object
- **Responsabilidad:** Encapsula los datos de entrada del caso de uso. Inmutable.
- **Campos (todos `private final`):**
  - `String titulo`
  - `UUID asesorFichaId`
  - `List<UUID> estudianteIds`
- **Métodos principales:**
  - Constructor con todos los campos.
  - Getters para todos los campos.
- **Dependencias:** Ninguna externa.

---

### `FichaPerfilRepositoryPort.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.out`
- **Tipo:** Interface (Puerto de salida)
- **Responsabilidad:** Contrato de persistencia para el Aggregate Root `FichaPerfil`.
- **Métodos principales:**
  - `guardar(FichaPerfil fichaPerfil): FichaPerfil` — persiste la ficha y retorna la entidad guardada.
  - `existePorTitulo(String titulo): boolean` — verifica unicidad del título (POL-02).
- **Dependencias:** `FichaPerfil`

---

### `AsesorFichaRepositoryPort.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.out`
- **Tipo:** Interface (Puerto de salida)
- **Responsabilidad:** Contrato de consulta para verificar existencia del asesor.
- **Métodos principales:**
  - `buscarPorId(UUID id): Optional<AsesorFicha>`
- **Dependencias:** `AsesorFicha`

---

### `EstadoFichaRepositoryPort.java`
- **Paquete:** `com.arquisoft.fichas.domain.port.out`
- **Tipo:** Interface (Puerto de salida)
- **Responsabilidad:** Contrato de consulta del catálogo de estados.
- **Métodos principales:**
  - `buscarPorNombre(String nombre): Optional<EstadoFicha>`
- **Dependencias:** `EstadoFicha`

---

### `FichaPerfilTituloDuplicadoException.java`
- **Paquete:** `com.arquisoft.fichas.domain.exception`
- **Tipo:** Exception (extiende `DomainException` de `shared:exceptions`)
- **Responsabilidad:** Señala que el título de la ficha ya existe en el sistema (POL-02). Mapeada a HTTP 409 en `GlobalExceptionHandler`.
- **Campos:** `errorCode = "FICHA_PERFIL_TITULO_DUPLICADO"`
- **Dependencias:** `DomainException` (shared:exceptions)

---

### `AsesorFichaNoEncontradoException.java`
- **Paquete:** `com.arquisoft.fichas.domain.exception`
- **Tipo:** Exception (extiende `DomainException` de `shared:exceptions`)
- **Responsabilidad:** Señala que el `asesorFichaId` no existe en el repositorio. Mapeada a HTTP 404.
- **Campos:** `errorCode = "ASESOR_FICHA_NO_ENCONTRADO"`
- **Dependencias:** `DomainException` (shared:exceptions)

---

### `EstadoFichaNoEncontradoException.java`
- **Paquete:** `com.arquisoft.fichas.domain.exception`
- **Tipo:** Exception (extiende `DomainException` de `shared:exceptions`)
- **Responsabilidad:** Señala que el catálogo no contiene el estado requerido (error de configuración). Mapeada a HTTP 500.
- **Campos:** `errorCode = "ESTADO_FICHA_NO_ENCONTRADO"`
- **Dependencias:** `DomainException` (shared:exceptions)

---

### `RegistrarFichaPerfilRequestDTO.java`
- **Paquete:** `com.arquisoft.fichas.application.dto`
- **Tipo:** DTO de entrada
- **Responsabilidad:** Recibe y valida los datos del request HTTP. Convierte al command de dominio.
- **Campos:**
  - `@NotBlank @Size(min=1, max=100) String titulo`
  - `@NotNull UUID idAsesorFicha`
  - `@NotNull @Size(min=1, max=3) List<@NotNull UUID> idEstudiantes`
- **Anotaciones de clase:** `@Data @NoArgsConstructor @AllArgsConstructor @Builder`
- **Métodos principales:**
  - `toCommand(): RegistrarFichaPerfilCommand` — construye el command validando duplicados en `idEstudiantes` (lanza `IllegalArgumentException` si hay duplicados, que el use case convierte a `DomainException`).
- **Dependencias:** `RegistrarFichaPerfilCommand`
- **Nota sobre duplicados:** La validación de duplicados en `idEstudiantes` puede hacerse en `toCommand()` comparando `Set.size()` vs `List.size()`, o delegarse al use case. Se recomienda hacerla en el use case para mantener la lógica de negocio centralizada.

---

### `RegistrarFichaPerfilResponseDTO.java`
- **Paquete:** `com.arquisoft.fichas.application.dto`
- **Tipo:** DTO de salida
- **Responsabilidad:** Encapsula el ID de la ficha creada para la respuesta HTTP 201.
- **Campos:**
  - `UUID id`
- **Anotaciones de clase:** `@Data @NoArgsConstructor @AllArgsConstructor @Builder`
- **Métodos principales:**
  - `static fromDomain(UUID id): RegistrarFichaPerfilResponseDTO`
- **Dependencias:** Ninguna de dominio.

---

### `RegistrarFichaPerfilUseCaseImpl.java`
- **Paquete:** `com.arquisoft.fichas.application.usecase`
- **Tipo:** UseCase Impl (`@Service`, `@RequiredArgsConstructor`)
- **Responsabilidad:** Orquesta el flujo completo de registro de una ficha perfil.
- **Dependencias inyectadas (via constructor):**
  - `FichaPerfilRepositoryPort fichaPerfilRepositoryPort`
  - `AsesorFichaRepositoryPort asesorFichaRepositoryPort`
  - `EstadoFichaRepositoryPort estadoFichaRepositoryPort`
- **Flujo del método `registrar(RegistrarFichaPerfilCommand command): UUID`:**
  1. Verificar unicidad del título: `fichaPerfilRepositoryPort.existePorTitulo(command.getTitulo())` → si `true`, lanzar `FichaPerfilTituloDuplicadoException`.
  2. Verificar existencia del asesor: `asesorFichaRepositoryPort.buscarPorId(command.getAsesorFichaId())` → si vacío, lanzar `AsesorFichaNoEncontradoException`.
  3. Validar lista de estudiantes: si `estudianteIds.size() < 1` o `> 3`, lanzar `DomainException` con código `ESTUDIANTES_INVALIDOS`. Si hay duplicados (`Set.size() != List.size()`), lanzar `DomainException` con código `ESTUDIANTES_DUPLICADOS`.
  4. Obtener estado inicial: `estadoFichaRepositoryPort.buscarPorNombre("En elaboración")` → si vacío, lanzar `EstadoFichaNoEncontradoException`.
  5. Construir el Aggregate Root: `FichaPerfil.build(titulo, asesorFichaId, estudianteIds, estadoFicha)`.
  6. Persistir: `fichaPerfilRepositoryPort.guardar(fichaPerfil)`.
  7. Retornar `fichaPerfil.getId()`.
- **Nota sobre eventos:** El Aggregate Root emite `FichaPerfilCreadaEvent` internamente via `publishEvent(...)`. El `FichaPerfilEventPublisher` de infraestructura escucha este evento via `@TransactionalEventListener` y lo publica en RabbitMQ **después** de que la transacción se confirme.

---

### `FichaPerfilController.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.in.web`
- **Tipo:** Controller REST (`@RestController`, `@RequestMapping("/fichas-perfil")`)
- **Responsabilidad:** Expone el endpoint `POST /fichas-perfil`. Valida el request, delega al use case y retorna la respuesta.
- **`@Tag`:** `name = "Fichas Perfil"`, `description = "Gestión de fichas de perfil de proyectos de grado"`
- **Dependencias inyectadas:** `RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase`
- **Endpoints documentados:**

  | Método del Controller | `@Operation(summary)` | Códigos `@ApiResponse` | `@SecurityRequirement` |
  |-----------------------|-----------------------|------------------------|------------------------|
  | `registrar(@Valid @RequestBody RegistrarFichaPerfilRequestDTO dto)` | `"Registrar nueva ficha perfil"` | 201, 400, 401, 403, 404, 409 | `bearerAuth` |

- **Anotaciones de seguridad:** `@PreAuthorize("hasAnyRole('ASESOR_FICHA', 'COORDINADOR')")` en el método.
- **Retorno:** `ResponseEntity<RegistrarFichaPerfilResponseDTO>` con status `HttpStatus.CREATED`.

---

### `FichaPerfilJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "ficha_perfil", schema = "fichas_perfil")`)
- **Responsabilidad:** Mapea la tabla `fichas_perfil.ficha_perfil`.
- **Campos:**
  - `@Id UUID id`
  - `@Column(name = "titulo_proyecto", unique = true, nullable = false, length = 100) String tituloProyecto`
  - `@Column(name = "asesor_ficha_id", nullable = false) UUID asesorFichaId`
  - `@OneToMany(mappedBy = "fichaPerfil", cascade = CascadeType.ALL, orphanRemoval = true) List<EstudianteFichaPerfilJpaEntity> estudiantes`
  - `@OneToMany(mappedBy = "fichaPerfil", cascade = CascadeType.ALL, orphanRemoval = true) List<EstadoFichaPerfilJpaEntity> estados`
- **Anotaciones de clase:** `@Data @NoArgsConstructor @AllArgsConstructor @Builder`

---

### `EstudianteFichaPerfilJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "estudiante_ficha_perfil", schema = "fichas_perfil")`)
- **Responsabilidad:** Mapea la tabla `fichas_perfil.estudiante_ficha_perfil`.
- **Campos:**
  - `@Id UUID id`
  - `@ManyToOne @JoinColumn(name = "ficha_perfil_id", nullable = false) FichaPerfilJpaEntity fichaPerfil`
  - `@Column(name = "estudiante_id", nullable = false) UUID estudianteId`
- **Constraint único:** `@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"ficha_perfil_id", "estudiante_id"}))`
- **Anotaciones de clase:** `@Data @NoArgsConstructor @AllArgsConstructor @Builder`

---

### `EstadoFichaPerfilJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "estado_ficha_perfil", schema = "fichas_perfil")`)
- **Responsabilidad:** Mapea la tabla `fichas_perfil.estado_ficha_perfil`.
- **Campos:**
  - `@Id UUID id`
  - `@ManyToOne @JoinColumn(name = "ficha_perfil_id", nullable = false) FichaPerfilJpaEntity fichaPerfil`
  - `@ManyToOne @JoinColumn(name = "estado_ficha_id", nullable = false) EstadoFichaJpaEntity estadoFicha`
  - `@Column(name = "fecha_actualizacion", nullable = false) LocalDateTime fechaActualizacion`
- **Anotaciones de clase:** `@Data @NoArgsConstructor @AllArgsConstructor @Builder`

---

### `EstadoFichaJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "estado_ficha", schema = "fichas_perfil")`)
- **Responsabilidad:** Mapea el catálogo `fichas_perfil.estado_ficha`.
- **Campos:**
  - `@Id UUID id`
  - `@Column(name = "nombre", nullable = false, unique = true) String nombre`
- **Anotaciones de clase:** `@Data @NoArgsConstructor @AllArgsConstructor @Builder`

---

### `AsesorFichaJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "asesor_ficha", schema = "fichas_perfil")`)
- **Responsabilidad:** Mapea la tabla `fichas_perfil.asesor_ficha`.
- **Campos:**
  - `@Id UUID id`
  - `@Column(name = "nombre", nullable = false) String nombre`
  - `@Column(name = "email", nullable = false) String email`
- **Anotaciones de clase:** `@Data @NoArgsConstructor @AllArgsConstructor @Builder`

---

### `FichaPerfilJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** Spring Data JPA Repository
- **Responsabilidad:** Acceso a datos para `FichaPerfilJpaEntity`.
- **Métodos principales:**
  - `existsByTituloProyecto(String tituloProyecto): boolean` — derivado de nombre de campo.

---

### `AsesorFichaJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** Spring Data JPA Repository
- **Responsabilidad:** Acceso a datos para `AsesorFichaJpaEntity`. Hereda `findById`.

---

### `EstadoFichaJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** Spring Data JPA Repository
- **Responsabilidad:** Acceso a datos para `EstadoFichaJpaEntity`.
- **Métodos principales:**
  - `findByNombre(String nombre): Optional<EstadoFichaJpaEntity>`

---

### `FichaPerfilRepositoryAdapterImpl.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** Adapter (`@Repository`, `@RequiredArgsConstructor`)
- **Responsabilidad:** Implementa `FichaPerfilRepositoryPort`. Traduce entre `FichaPerfil` (dominio) y `FichaPerfilJpaEntity` (JPA).
- **Dependencias inyectadas:** `FichaPerfilJpaRepository fichaPerfilJpaRepository`
- **Métodos principales:**
  - `guardar(FichaPerfil fichaPerfil): FichaPerfil` — convierte a JPA entity, llama a `save(...)`, convierte de vuelta a dominio.
  - `existePorTitulo(String titulo): boolean` — delega a `fichaPerfilJpaRepository.existsByTituloProyecto(titulo)`.
- **Lógica de mapeo:** Construye `FichaPerfilJpaEntity` con sus colecciones hijas (`EstudianteFichaPerfilJpaEntity` y `EstadoFichaPerfilJpaEntity`). Al reconstruir el dominio, usa los factory methods `rebuild(...)`.

---

### `AsesorFichaRepositoryAdapterImpl.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** Adapter (`@Repository`, `@RequiredArgsConstructor`)
- **Responsabilidad:** Implementa `AsesorFichaRepositoryPort`.
- **Dependencias inyectadas:** `AsesorFichaJpaRepository asesorFichaJpaRepository`
- **Métodos principales:**
  - `buscarPorId(UUID id): Optional<AsesorFicha>` — delega a `findById`, mapea a dominio con `AsesorFicha.rebuild(...)`.

---

### `EstadoFichaRepositoryAdapterImpl.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.persistence`
- **Tipo:** Adapter (`@Repository`, `@RequiredArgsConstructor`)
- **Responsabilidad:** Implementa `EstadoFichaRepositoryPort`.
- **Dependencias inyectadas:** `EstadoFichaJpaRepository estadoFichaJpaRepository`
- **Métodos principales:**
  - `buscarPorNombre(String nombre): Optional<EstadoFicha>` — delega a `findByNombre`, mapea a dominio con `EstadoFicha.rebuild(...)`.

---

### `NuevaFichaPerfilAsignadaEvent.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.messaging`
- **Tipo:** POJO serializable (mensaje RabbitMQ)
- **Responsabilidad:** Payload del mensaje publicado en RabbitMQ. Serializable a JSON.
- **Campos:**
  - `UUID fichaPerfilId`
  - `String titulo`
  - `UUID asesorFichaId`
  - `List<UUID> estudianteIds`
  - `LocalDateTime fechaCreacion`
- **Anotaciones de clase:** `@Data @NoArgsConstructor @AllArgsConstructor @Builder`

---

### `FichaPerfilEventPublisher.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.adapter.out.messaging`
- **Tipo:** Component (`@Component`, `@RequiredArgsConstructor`)
- **Responsabilidad:** Escucha el evento de dominio `FichaPerfilCreadaEvent` (publicado por el Aggregate Root via `ApplicationEventPublisher` de Spring) y lo transforma en un mensaje RabbitMQ `NuevaFichaPerfilAsignadaEvent`.
- **Dependencias inyectadas:** `RabbitTemplate rabbitTemplate`
- **Métodos principales:**
  - `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) publicarEvento(FichaPerfilCreadaEvent evento): void` — construye `NuevaFichaPerfilAsignadaEvent` y llama a `rabbitTemplate.convertAndSend("arquisoft.events", "fichas.ficha_perfil.creada", evento)`.
- **Nota:** Usar `@TransactionalEventListener` con `AFTER_COMMIT` garantiza que el evento solo se publica si la transacción de base de datos se confirmó exitosamente.

---

### `RabbitMQFichaPerfilConfig.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.config`
- **Tipo:** Configuration (`@Configuration`)
- **Responsabilidad:** Declara el exchange, la queue y el binding para el evento de creación de ficha perfil.
- **Beans definidos:**
  - `TopicExchange arquisoftEventsExchange()` — nombre: `arquisoft.events`, durable: `true`.
  - `Queue fichaPerfilCreadaQueue()` — nombre: `fichas.ficha_perfil.creada.queue`, durable: `true`.
  - `Binding fichaPerfilCreadaBinding(Queue, TopicExchange)` — routing key: `fichas.ficha_perfil.creada`.
- **Nota:** Si el exchange `arquisoft.events` ya está declarado en otro módulo de configuración compartido, omitir el bean `arquisoftEventsExchange()` para evitar conflictos. Verificar con el implementador.

---

### `V1__crear_tablas_fichas_perfil.sql`
- **Ruta:** `fichas/infrastructure/src/main/resources/db/migration/V1__crear_tablas_fichas_perfil.sql`
- **Tipo:** Migración Flyway
- **Responsabilidad:** Crea el schema y todas las tablas necesarias para el bounded context `fichas`. Inserta el registro inicial del catálogo de estados.
- **Contenido detallado:** Ver Sección 8.

---

## 6. Endpoints REST

| Método | Ruta | Request Body | Response | Código HTTP | Roles permitidos | Anotaciones Swagger (ADR-011) |
|--------|------|--------------|----------|-------------|-----------------|-------------------------------|
| POST | `/fichas-perfil` | `RegistrarFichaPerfilRequestDTO` | `RegistrarFichaPerfilResponseDTO` | 201 | `ROLE_ASESOR_FICHA`, `ROLE_COORDINADOR` | `@Operation(summary="Registrar nueva ficha perfil")` + `@SecurityRequirement(name="bearerAuth")` |

**Respuestas de error:**

| Código HTTP | Condición | Descripción |
|-------------|-----------|-------------|
| 400 | Título vacío, > 100 chars, asesorFichaId nulo, lista vacía, > 3 estudiantes, estudiantes duplicados | Datos de entrada inválidos |
| 401 | JWT ausente o inválido | No autenticado |
| 403 | Rol no autorizado | Sin permisos |
| 404 | `asesorFichaId` no existe | Asesor no encontrado |
| 409 | Título ya existe en el sistema | Título duplicado (POL-02) |

> **Nota ADR-011:** El Controller **DEBE** incluir `@Tag` a nivel de clase y `@Operation`, `@ApiResponses`, `@SecurityRequirement(name="bearerAuth")` a nivel de método.

---

## 7. Eventos RabbitMQ

| Dirección | Exchange | Routing Key | Payload | Bounded Context receptor |
|-----------|----------|-------------|---------|--------------------------|
| Publica | `arquisoft.events` | `fichas.ficha_perfil.creada` | `NuevaFichaPerfilAsignadaEvent` { fichaPerfilId, titulo, asesorFichaId, estudianteIds, fechaCreacion } | Por definir (aún no especificado) |

**Flujo del evento:**
1. `FichaPerfil.build(...)` → `publishEvent(new FichaPerfilCreadaEvent(...))` (evento de dominio interno, via `AggregateRoot`).
2. Spring `ApplicationEventPublisher` propaga el evento al contexto de Spring.
3. `FichaPerfilEventPublisher.publicarEvento(...)` escucha con `@TransactionalEventListener(AFTER_COMMIT)`.
4. Transforma `FichaPerfilCreadaEvent` → `NuevaFichaPerfilAsignadaEvent`.
5. Publica en RabbitMQ via `rabbitTemplate.convertAndSend(...)`.

---

## 8. Migración de Base de Datos

- **Archivo:** `V1__crear_tablas_fichas_perfil.sql`
- **Schema PostgreSQL:** `fichas_perfil` (mapeo especial: contexto `fichas` → schema `fichas_perfil`)
- **Ruta completa:** `fichas/infrastructure/src/main/resources/db/migration/V1__crear_tablas_fichas_perfil.sql`

**Estructura SQL:**

```sql
-- Crear schema si no existe
CREATE SCHEMA IF NOT EXISTS fichas_perfil;

-- Catálogo de estados de ficha
CREATE TABLE fichas_perfil.estado_ficha (
    id              UUID         NOT NULL DEFAULT gen_random_uuid(),
    nombre          VARCHAR(100) NOT NULL,
    CONSTRAINT pk_estado_ficha PRIMARY KEY (id),
    CONSTRAINT uq_estado_ficha_nombre UNIQUE (nombre)
);

-- Tabla de asesores de ficha
CREATE TABLE fichas_perfil.asesor_ficha (
    id     UUID         NOT NULL DEFAULT gen_random_uuid(),
    nombre VARCHAR(200) NOT NULL,
    email  VARCHAR(200) NOT NULL,
    CONSTRAINT pk_asesor_ficha PRIMARY KEY (id)
);

-- Aggregate Root: Ficha Perfil
CREATE TABLE fichas_perfil.ficha_perfil (
    id               UUID         NOT NULL DEFAULT gen_random_uuid(),
    titulo_proyecto  VARCHAR(100) NOT NULL,
    asesor_ficha_id  UUID         NOT NULL,
    CONSTRAINT pk_ficha_perfil PRIMARY KEY (id),
    CONSTRAINT uq_ficha_perfil_titulo UNIQUE (titulo_proyecto),
    CONSTRAINT fk_ficha_perfil_asesor FOREIGN KEY (asesor_ficha_id)
        REFERENCES fichas_perfil.asesor_ficha (id)
);

-- Entidad hija: Estudiantes asignados a la ficha
CREATE TABLE fichas_perfil.estudiante_ficha_perfil (
    id              UUID NOT NULL DEFAULT gen_random_uuid(),
    ficha_perfil_id UUID NOT NULL,
    estudiante_id   UUID NOT NULL,
    CONSTRAINT pk_estudiante_ficha_perfil PRIMARY KEY (id),
    CONSTRAINT uq_estudiante_ficha UNIQUE (ficha_perfil_id, estudiante_id),
    CONSTRAINT fk_estudiante_ficha_perfil FOREIGN KEY (ficha_perfil_id)
        REFERENCES fichas_perfil.ficha_perfil (id)
);

-- Entidad hija: Estado actual de la ficha
CREATE TABLE fichas_perfil.estado_ficha_perfil (
    id                  UUID      NOT NULL DEFAULT gen_random_uuid(),
    ficha_perfil_id     UUID      NOT NULL,
    estado_ficha_id     UUID      NOT NULL,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_estado_ficha_perfil PRIMARY KEY (id),
    CONSTRAINT fk_estado_ficha_perfil_ficha FOREIGN KEY (ficha_perfil_id)
        REFERENCES fichas_perfil.ficha_perfil (id),
    CONSTRAINT fk_estado_ficha_perfil_estado FOREIGN KEY (estado_ficha_id)
        REFERENCES fichas_perfil.estado_ficha (id)
);

-- Datos iniciales del catálogo de estados
INSERT INTO fichas_perfil.estado_ficha (id, nombre) VALUES
    (gen_random_uuid(), 'En elaboración'),
    (gen_random_uuid(), 'En revisión'),
    (gen_random_uuid(), 'Aprobada'),
    (gen_random_uuid(), 'Rechazada');
```

---

## 9. Casos de Prueba Sugeridos

### Tests Unitarios — capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `RegistrarFichaPerfilUseCaseImplTest` | `debeRegistrarFicha_cuandoDatosValidos` | Happy path: título único, asesor existente, 2 estudiantes sin duplicados → retorna UUID |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarExcepcion_cuandoTituloDuplicado` | `existePorTitulo` retorna `true` → lanza `FichaPerfilTituloDuplicadoException` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarExcepcion_cuandoAsesorNoExiste` | `buscarPorId` retorna `Optional.empty()` → lanza `AsesorFichaNoEncontradoException` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarExcepcion_cuandoListaEstudiantesVacia` | `estudianteIds` vacío → lanza `DomainException` con código `ESTUDIANTES_INVALIDOS` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarExcepcion_cuandoMasDeTresEstudiantes` | `estudianteIds` con 4 elementos → lanza `DomainException` con código `ESTUDIANTES_INVALIDOS` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarExcepcion_cuandoEstudiantesDuplicados` | `estudianteIds` con mismo UUID repetido → lanza `DomainException` con código `ESTUDIANTES_DUPLICADOS` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeLanzarExcepcion_cuandoEstadoInicialNoExiste` | `buscarPorNombre("En elaboración")` retorna `Optional.empty()` → lanza `EstadoFichaNoEncontradoException` |
| `RegistrarFichaPerfilUseCaseImplTest` | `debeEmitirEventoDominio_cuandoFichaCreada` | Tras `build(...)`, el Aggregate Root tiene un evento `FichaPerfilCreadaEvent` en su lista de eventos no publicados |

### Tests de Repositorio — capa `infrastructure` (H2 en memoria)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `FichaPerfilRepositoryAdapterImplTest` | `debeGuardarFicha_cuandoDatosValidos` | Persiste `FichaPerfil` con 2 estudiantes y 1 estado → verifica en H2 |
| `FichaPerfilRepositoryAdapterImplTest` | `debeRetornarTrue_cuandoTituloExiste` | Inserta ficha con título → `existePorTitulo` retorna `true` |
| `FichaPerfilRepositoryAdapterImplTest` | `debeRetornarFalse_cuandoTituloNoExiste` | `existePorTitulo` con título inexistente → retorna `false` |
| `AsesorFichaRepositoryAdapterImplTest` | `debeRetornarAsesor_cuandoIdExiste` | Inserta asesor → `buscarPorId` retorna `Optional` con el asesor |
| `AsesorFichaRepositoryAdapterImplTest` | `debeRetornarVacio_cuandoIdNoExiste` | `buscarPorId` con UUID inexistente → retorna `Optional.empty()` |
| `EstadoFichaRepositoryAdapterImplTest` | `debeRetornarEstado_cuandoNombreExiste` | Inserta estado "En elaboración" → `buscarPorNombre` retorna el estado |

### Tests de Controller — capa `infrastructure` (Spring Security Test)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `FichaPerfilControllerTest` | `debeRetornar201_cuandoRequestValido` | Request válido con rol `ASESOR_FICHA` → HTTP 201 con `{ "id": "..." }` |
| `FichaPerfilControllerTest` | `debeRetornar201_cuandoRolCoordinador` | Request válido con rol `COORDINADOR` → HTTP 201 |
| `FichaPerfilControllerTest` | `debeRetornar400_cuandoTituloVacio` | `titulo` vacío → HTTP 400 |
| `FichaPerfilControllerTest` | `debeRetornar400_cuandoTituloSuperaLongitud` | `titulo` con 101 caracteres → HTTP 400 |
| `FichaPerfilControllerTest` | `debeRetornar400_cuandoListaEstudiantesVacia` | `idEstudiantes` vacío → HTTP 400 |
| `FichaPerfilControllerTest` | `debeRetornar400_cuandoMasDeTresEstudiantes` | `idEstudiantes` con 4 UUIDs → HTTP 400 |
| `FichaPerfilControllerTest` | `debeRetornar401_cuandoSinAutenticacion` | Sin JWT → HTTP 401 |
| `FichaPerfilControllerTest` | `debeRetornar403_cuandoRolNoAutorizado` | JWT con rol `ESTUDIANTE` → HTTP 403 |
| `FichaPerfilControllerTest` | `debeRetornar404_cuandoAsesorNoExiste` | Use case lanza `AsesorFichaNoEncontradoException` → HTTP 404 |
| `FichaPerfilControllerTest` | `debeRetornar409_cuandoTituloDuplicado` | Use case lanza `FichaPerfilTituloDuplicadoException` → HTTP 409 |

---

## 10. Checklist de Implementación

- [ ] Entidad de dominio `FichaPerfil` creada (inmutable, factory methods `build` / `rebuild`, sin Lombok, extiende `AggregateRoot`)
- [ ] Entidades hijas `EstudianteFichaPerfil` y `EstadoFichaPerfil` creadas (inmutables, factory methods `build` / `rebuild`, sin Lombok)
- [ ] Entidades de referencia `AsesorFicha` y `EstadoFicha` creadas (inmutables, factory methods `rebuild`, sin Lombok)
- [ ] Evento de dominio `FichaPerfilCreadaEvent` creado (extiende `DomainEvent` de `shared:domain`)
- [ ] Puerto de entrada `RegistrarFichaPerfilUseCase` definido con `RegistrarFichaPerfilCommand`
- [ ] Puertos de salida `FichaPerfilRepositoryPort`, `AsesorFichaRepositoryPort`, `EstadoFichaRepositoryPort` definidos
- [ ] Excepciones de dominio `FichaPerfilTituloDuplicadoException`, `AsesorFichaNoEncontradoException`, `EstadoFichaNoEncontradoException` creadas (extienden `DomainException`)
- [ ] Excepciones registradas en `GlobalExceptionHandler` (shared:exceptions) con códigos HTTP correctos (409, 404, 500)
- [ ] DTOs `RegistrarFichaPerfilRequestDTO` y `RegistrarFichaPerfilResponseDTO` con anotaciones Jakarta Validation y métodos `toCommand()` / `fromDomain()`
- [ ] Caso de uso `RegistrarFichaPerfilUseCaseImpl` con `@Service @RequiredArgsConstructor` y flujo completo de validaciones
- [ ] Controller `FichaPerfilController` con `@Valid @RequestBody`, `@PreAuthorize("hasAnyRole('ASESOR_FICHA', 'COORDINADOR')")` y respuesta `ResponseEntity` con status 201
- [ ] Controller documentado con `@Tag`, `@Operation`, `@ApiResponses` y `@SecurityRequirement(name="bearerAuth")` (ADR-011)
- [ ] Entidades JPA creadas para las 5 tablas del schema `fichas_perfil`
- [ ] Repositorios Spring Data JPA creados con métodos derivados necesarios
- [ ] Adaptadores de repositorio creados con lógica de mapeo dominio ↔ JPA
- [ ] Publisher RabbitMQ `FichaPerfilEventPublisher` con `@TransactionalEventListener(AFTER_COMMIT)`
- [ ] Configuración RabbitMQ `RabbitMQFichaPerfilConfig` con exchange, queue y binding
- [ ] Migración Flyway `V1__crear_tablas_fichas_perfil.sql` en schema `fichas_perfil` con datos iniciales del catálogo
- [ ] Tests unitarios del use case con patrón AAA (cobertura ≥ 75%)
- [ ] Tests de repositorio con H2 en memoria
- [ ] Tests de controller con Spring Security Test
- [ ] Commit: `feat(fichas): registrar nueva ficha perfil HU-208`

---

## 11. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente         | Estado       | Fecha | Notas |
|------------|----------------|--------------|-------|-------|
| Desarrollo | @implementador | ⏳ Pendiente |       |       |
| Tests      | @tester        | ⏳ Pendiente |       |       |
| Validación | @validator     | ⏳ Pendiente |       |       |
| Commit     | @validator     | ⏳ Pendiente |       |       |
