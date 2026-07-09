# PLAN: Agregar información de un nuevo Estado Evaluación Ficha

## Metadata
- **ID Historia:** HU-191
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura (sin eventos de dominio)
- **¿Usa AggregateRoot?:** No — la HU no emite eventos de dominio. Es un CRUD interno puro sin consumidores conocidos ni casos de auditoría identificados. La entidad raíz `EstadoEvaluacionFichaAggregate` es una clase plana con factories `crear`/`reconstruir`.
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-07-08 (actualizado 2026-07-08 con ajuste de inicialización automática)
- **Rama sugerida:** `feature/hu-191-agregar-estado-evaluacion-ficha`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
- **Fuentes consultadas del código:**
    - `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/command/RegistrarFichaPerfilUseCase.java` (patrón de estado inicial automático)
    - `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadofichaperfil/aggregate/EstadoFichaPerfilAggregate.java` (factory `crear(UUID padre)` con estado hardcodeado)
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** Escritura sin eventos de dominio (CRUD interno puro). Ruta del endpoint manual: `POST /fichas-perfil/estado-evaluacion-ficha`. **Incluye ajuste de HU-190 para inicialización automática del estado EN_EVALUACION** al registrar evaluación (patrón análogo a FichaPerfil).

---

## 1. Resumen Funcional

Esta HU implementa la trazabilidad completa de cambios de estado por los que pasa una evaluación de ficha de perfil, cubriendo **DOS flujos de creación de `EstadoEvaluacionFicha`**:

### (a) Flujo automático — inicialización del estado EN_EVALUACION (ajuste de HU-190)

**Al registrar una nueva `EvaluacionFichaPerfil`** (HU-190), el sistema crea automáticamente un `EstadoEvaluacionFicha` inicial con estado `EN_EVALUACION` — análogo al patrón ya establecido en `RegistrarFichaPerfilUseCase`, que inicializa `EstadoFichaPerfil` con estado `EN_CONSTRUCCION`. Este ajuste se implementa modificando `RegistrarEvaluacionFichaPerfilUseCase` (línea 54 del código actual) para llamar a `asignarEstadoInicialEvaluacion(evaluacion.getId())` tras persistir la evaluación.

### (b) Flujo manual — registro de estados posteriores por representante-comite

**Posteriormente**, el Representante del Comité de Currículum puede registrar cambios de estado vía el endpoint REST `POST /fichas-perfil/estado-evaluacion-ficha`. Cada vez que el estado de una evaluación cambia, se crea un nuevo registro de `EstadoEvaluacionFicha` vinculado a la evaluación existente y al catálogo de estados. El sistema valida que no se repitan estados para la misma evaluación (POL-04) y que no se permitan transiciones desde estados terminales (POL-05).

**Cobertura de la HU:**
- ✅ Estado inicial automático al registrar evaluación (flujo a)
- ✅ Registro manual de estados posteriores (flujo b)
- ❌ Consulta de historiales de estados (fuera de alcance)
- ❌ Modificación de estados ya creados (fuera de alcance)

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | **[Automático]** Registrar evaluación nueva | Sistema crea automáticamente un `EstadoEvaluacionFicha` con estado `EN_EVALUACION` al persistir la evaluación |
| 2 | **[Manual]** Crear registro de estado con datos válidos | Sistema retorna 201 Created con el UUID del estado creado |
| 3 | **[Manual]** Validar que la evaluación existe | Sistema retorna 400 si la evaluación no existe |
| 4 | **[Manual]** Validar que el estado del catálogo existe | Sistema retorna 400 si el estado no existe en el catálogo |
| 5 | **[Manual]** Rechazar estados duplicados | Sistema retorna 400 si la evaluación ya tiene ese estado asignado |
| 6 | **[Manual]** Rechazar transiciones desde estados terminales | Sistema retorna 422 si la evaluación tiene estado Aprobada/No Aprobada/Descartada |
| 7 | **[Manual]** Validar que el primer estado sea EN_EVALUACION | Sistema retorna 422 si es el primer estado manual y no es EN_EVALUACION (no debería ocurrir porque el automático ya lo crea) |
| 8 | **[Manual]** Autenticar y autorizar | Sistema retorna 401 si no hay token, 403 si no es representante-comite |
| 9 | **[Ambos]** Auditar acción | Sistema loguea la creación del estado con id, evaluacionId, estadoId |

---

## 3. Reglas de Negocio

- **POL-01:** El primer estado de una evaluación debe ser "EN_EVALUACION" — **garantizado automáticamente** en el flujo (a) al registrar la evaluación.
- **POL-04:** Una evaluación no puede tener el mismo estado asignado más de una vez (validación de duplicado antes de guardar en ambos flujos).
- **POL-05:** Una vez que el estado de la evaluación es "APROBADA", "NO_APROBADA" o "DESCARTADA", no se puede agregar ningún otro estado (validación de estado terminal en flujo b).
- **POL-06:** Asegurar que el estado inicial sea "EN_EVALUACION" para continuar con los otros estados (redundante con POL-01 — implementado como estado hardcodeado en el factory `crear(UUID evaluacionId)` del aggregate, sin parámetro de estado).
- La `fechaActualizacion` se genera automáticamente al momento de crear el registro (usa `UtilDate.generateNewInstantNow()`).
- El catálogo `EstadoEvaluacion` tiene PK semántica VARCHAR (ADR-012) — el `id` es la constante del enum en SCREAMING_CASE (ej. `"EN_EVALUACION"`).
- El aggregate NO extiende `AggregateRoot` — es un CRUD interno sin eventos de dominio, sin consumidores conocidos ni casos de auditoría.

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `EstadoEvaluacionFichaAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** No — la HU no emite eventos, es CRUD interno puro. La entidad es una clase plana con factories `crear`/`reconstruir`.
- **ID:** `UUID`

### Atributos por objeto de dominio (extraídos del modelo enriquecido)

#### `EstadoEvaluacionFicha`

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | Autogenerado con `UtilUUID.generateNewUUID()` dentro del setter `setId()` |
| `evaluacionFichaPerfilId` | `UUID` | — | Sí | No | No | FK a `evaluacion_ficha_perfil` — valida existencia con `EvaluacionFichaPerfilQueryOutputPort.existsById(...)` en flujo manual (b); en flujo automático (a) se recibe el UUID recién creado |
| `estadoEvaluacion` | `EstadoEvaluacion` (enum) | — | Sí | No | **Sí en flujo (a), No en flujo (b)** | En flujo automático: hardcodeado `EstadoEvaluacion.EN_EVALUACION` en el setter sin parámetro. En flujo manual: recibido como enum desde el use case (convertido con `EstadoEvaluacion.valueOf(...)`) |
| `fechaActualizacion` | `Instant` | — | Sí | No | Sí | Autogenerado con `UtilDate.generateNewInstantNow()` dentro del setter `setFechaActualizacion()` |

**Combinaciones únicas (Restricciones):**

El MER documenta una restricción única comentada como TODO revisar: `UNIQUE (evaluacion_ficha_perfil_id, estado_evaluacion_id, fecha_actualizacion)`. Sin embargo, esta restricción NO es replicable en el código porque `fechaActualizacion` es autogenerado con precisión de milisegundos — dos creaciones consecutivas del mismo estado tendrían timestamps distintos y pasarían la restricción, violando POL-04.

**Restricción efectiva implementada en el código (POL-04):**
- Combinación única: `evaluacionFichaPerfilId + estadoEvaluacionId` → validación de duplicado en el use case antes de persistir, lanzando `EstadoEvaluacionDuplicadoException` si ya existe (aplica tanto al flujo automático como al manual).

### Traducción del modelo enriquecido a código

| Característica del modelo | Traducción en código |
|---|---|
| `id` autogenerado | `UtilUUID.generateNewUUID()` dentro del setter `setId()` del aggregate |
| `evaluacionFichaPerfilId` obligatorio | `DomainValidator.notNull(...)` en el setter del aggregate + validación de existencia en use case manual con `EvaluacionFichaPerfilQueryOutputPort.existsById(...)` (flujo a no valida porque el UUID viene recién creado) |
| `estadoEvaluacion` obligatorio | **Flujo (a):** hardcodeado `EstadoEvaluacion.EN_EVALUACION` (enum) en `setEstadoEvaluacionInicial()` sin parámetro (llamado desde factory `crear(UUID)`). **Flujo (b):** `DomainValidator.notNull(...)` en el setter con parámetro `setEstadoEvaluacion(EstadoEvaluacion, ValidationResult)` (llamado desde factory `crearConEstado(UUID, EstadoEvaluacion)`) — la conversión desde `String` del Command a enum se hace en el use case con `EstadoEvaluacion.valueOf(...)` + validación de existencia del catálogo |
| `fechaActualizacion` autogenerado | `UtilDate.generateNewInstantNow()` dentro del setter `setFechaActualizacion()` del aggregate |
| Duplicado (POL-04) | `EstadoEvaluacionFichaOutputPort.existsByEvaluacionAndEstado(...)` invocado en use case antes de persistir (aplica a ambos flujos — en flujo a valida que no exista ya `EN_EVALUACION`, aunque no debería) |
| Estado terminal (POL-05) | `EvaluacionFichaPerfilQueryOutputPort.obtenerUltimoEstado(UUID evaluacionId): Optional<String>` retorna el ID del último estado; use case manual valida que no sea terminal |
| Primer estado = EN_EVALUACION (POL-01) | Garantizado automáticamente en flujo (a) con factory `crear(UUID evaluacionId)` que hardcodea `"EN_EVALUACION"`. El flujo (b) usa factory `crearConEstado(UUID, String)` y valida con `contarEstadosPorEvaluacion` aunque ya no debería ser el primero |

### Estados y tipos: planearlos como enum de dominio

El catálogo `EstadoEvaluacion` es un conjunto cerrado de valores documentados. Se planea como **enum en el dominio** (`domain/estadoevaluacion/EstadoEvaluacion.java`) con PK semántica VARCHAR (ADR-012):

- **Enum:** `EstadoEvaluacion` con constantes `EN_EVALUACION`, `APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA`, `DESCARTADA`. Cada constante tiene `id` (= `name()`) y `nombre` (texto legible).
- **Tabla catálogo:** PK `VARCHAR(50)` poblada por Flyway con las constantes del enum. El aggregate guarda el enum `EstadoEvaluacion`, el mapper JPA convierte a/desde String (columna VARCHAR).
- **Persistencia sin consulta al catálogo:** el `CommandOutputAdapter` inyecta `EstadoEvaluacionJpaRepository` y usa `repository.getReferenceById(aggregate.getEstadoEvaluacion().name())` (conversión enum→String) sin viaje a BD — el `Mapper` reconstruye con `EstadoEvaluacion.valueOf(entity.getEstadoEvaluacion().getId())` (conversión String→enum).
- **NO se planean:** `EstadoEvaluacionAggregate`, `EstadoEvaluacionQueryOutputPort`, `EstadoEvaluacionQueryOutputAdapter`, `EstadoEvaluacionReadModel` — el enum los reemplaza.

### Eventos de Dominio que emite

**Eventos: ninguno.**

**Razón:** CRUD interno puro sin consumidores conocidos ni casos de auditoría identificados. La HU solo registra trazabilidad de cambios de estado para consulta posterior.

**Implicaciones:**
- La entidad raíz `EstadoEvaluacionFichaAggregate` **NO extiende `AggregateRoot`** — es una clase plana con factories `crear`/`reconstruir`.
- Los factories `crear(...)` **NO acumulan eventos** (no existe `publishEvent`).
- Los use cases **NO inyectan `EventPublisher`**, no hay drenado de eventos.
- No se crean archivos en `domain/estadoevaluacionficha/event/`.

---

## 5. Patrón de Inicialización Automática de Estado (Dual Flow)

Esta HU implementa un **patrón dual** análogo al ya establecido en `FichaPerfil`:

### Patrón existente en el proyecto (FichaPerfil)

```java
// RegistrarFichaPerfilUseCase.java (líneas 56-58)
fichaPerfilOutputPort.guardar(ficha);
asignarEstadoInicial(ficha.getId());  // ← crea EstadoFichaPerfilAggregate automático

// Método privado (líneas 93-100)
private void asignarEstadoInicial(UUID fichaPerfilId) {
    var estadoInicial = EstadoFichaPerfilAggregate.crear(fichaPerfilId);  // ← factory sin parámetro de estado
    estadoFichaPerfilOutputPort.guardar(estadoInicial);
    log.info(...);
}

// EstadoFichaPerfilAggregate.java (líneas 66-68)
private void setEstadoFicha() {
    this.estadoFicha = EstadoFicha.EN_CONSTRUCCION;  // ← estado hardcodeado
}
```

### Patrón aplicado a EvaluacionFichaPerfil (esta HU)

```java
// RegistrarEvaluacionFichaPerfilUseCase.java (MODIFICAR — línea 54 actual)
evaluacionFichaPerfilOutputPort.guardar(evaluacion);
asignarEstadoInicialEvaluacion(evaluacion.getId());  // ← AGREGAR esta llamada tras persistir
log.info(...);

// Método privado a AGREGAR al final del use case
private void asignarEstadoInicialEvaluacion(UUID evaluacionFichaPerfilId) {
    var estadoInicial = EstadoEvaluacionFichaAggregate.crear(evaluacionFichaPerfilId);  // ← factory sin parámetro de estado
    estadoEvaluacionFichaOutputPort.guardar(estadoInicial);
    log.info(FichasMessages.EstadoEvaluacionFicha.LOG_CREADO_AUTOMATICO,
             estadoInicial.getId(),
             estadoInicial.getEvaluacionFichaPerfilId(),
             estadoInicial.getEstadoEvaluacionId());
}

// EstadoEvaluacionFichaAggregate.java — factories sin sobrecarga (patrón del proyecto)

// Factory flujo (a) automático — estado hardcodeado
public static EstadoEvaluacionFichaAggregate crear(UUID evaluacionFichaPerfilId) {
    var aggregate = new EstadoEvaluacionFichaAggregate();
    var result = new ValidationResult();
    aggregate.setId();
    aggregate.setEvaluacionFichaPerfilId(evaluacionFichaPerfilId, result);
    aggregate.setEstadoEvaluacionInicial();  // ← setter sin parámetro, hardcodea el enum
    aggregate.setFechaActualizacion();
    result.throwIfHasErrors();
    return aggregate;
}

private void setEstadoEvaluacionInicial() {
    this.estadoEvaluacion = EstadoEvaluacion.EN_EVALUACION;  // ← asignación directa del enum
}

// Factory flujo (b) manual — estado parametrizado (OTRO NOMBRE, no sobrecarga)
public static EstadoEvaluacionFichaAggregate crearConEstado(UUID evaluacionFichaPerfilId, EstadoEvaluacion estadoEvaluacion) {
    var aggregate = new EstadoEvaluacionFichaAggregate();
    var result = new ValidationResult();
    aggregate.setId();
    aggregate.setEvaluacionFichaPerfilId(evaluacionFichaPerfilId, result);
    aggregate.setEstadoEvaluacion(estadoEvaluacion, result);  // ← setter con parámetro tipo enum
    aggregate.setFechaActualizacion();
    result.throwIfHasErrors();
    return aggregate;
}

private void setEstadoEvaluacion(EstadoEvaluacion estadoEvaluacion, ValidationResult result) {
    DomainValidator.notNull(estadoEvaluacion,
            FichasMessages.EstadoEvaluacionFicha.CAMPO_ESTADO_EVALUACION,
            FichasMessages.EstadoEvaluacionFicha.ESTADO_REQUERIDO,
            result);
    this.estadoEvaluacion = estadoEvaluacion;
}
```

### Resumen del patrón

| Aspecto | Flujo (a) automático | Flujo (b) manual |
|---|---|---|
| **Invocador** | `RegistrarEvaluacionFichaPerfilUseCase.asignarEstadoInicialEvaluacion(UUID)` | `AgregarEstadoEvaluacionFichaUseCase.ejecutar(Command)` |
| **Factory** | `crear(UUID evaluacionId)` — único factory sin parámetro de estado | `crearConEstado(UUID evaluacionId, EstadoEvaluacion estado)` — factory con nombre distinto que recibe el enum |
| **Estado asignado** | Hardcodeado `EstadoEvaluacion.EN_EVALUACION` (enum) en setter privado `setEstadoEvaluacionInicial()` sin parámetro | Parametrizado como enum desde el use case (convertido con `EstadoEvaluacion.valueOf(...)`), validado en setter `setEstadoEvaluacion(EstadoEvaluacion, ValidationResult)` |
| **Validaciones POL** | Solo obligatoriedad del UUID (no valida POL-04/05 — no debería haber estados previos) | POL-04 (duplicado), POL-05 (terminal), POL-01 (primer estado — aunque ya no aplica) |
| **Inyecciones del use case** | `EstadoEvaluacionFichaOutputPort` | `EstadoEvaluacionFichaOutputPort`, `EvaluacionFichaPerfilQueryOutputPort`, `EstadoEvaluacionJpaRepository` |
| **Autorización** | N/A (transacción interna del registro de evaluación) | `@PreAuthorize("hasAuthority('fichas:estado-evaluacion-ficha:create')")` |

---

## 6. Integraciones Externas (solo si la HU lo requiere)

**No aplica.** La HU solo interactúa con PostgreSQL (persistencia) — no hay integraciones externas adicionales.

---

## 7. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS — caso de uso write (Agregar manual)

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadoevaluacion/EstadoEvaluacion.java` | Enum | Catálogo de estados de evaluación con PK semántica. Constantes: `EN_EVALUACION`, `APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA`, `DESCARTADA`. Métodos: `getId()` (= `name()`), `getNombre()`. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadoevaluacionficha/aggregate/EstadoEvaluacionFichaAggregate.java` | Aggregate Root | **NO extiende `AggregateRoot`** (clase plana). **DOS factories sin sobrecarga:** `crear(UUID evaluacionId)` para flujo automático (estado hardcodeado `EstadoEvaluacion.EN_EVALUACION` en setter sin parámetro) + `crearConEstado(UUID evaluacionId, EstadoEvaluacion estado)` para flujo manual (estado parametrizado como enum en setter con parámetro). Factory `reconstruir(...)` sin validar. Campo `EstadoEvaluacion estadoEvaluacion` (enum, no String). |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadoevaluacionficha/port/out/EstadoEvaluacionFichaOutputPort.java` | Interface | Puerto de salida write. Métodos: `guardar(EstadoEvaluacionFichaAggregate)`, `existsByEvaluacionAndEstado(UUID, String): boolean`, `contarEstadosPorEvaluacion(UUID): long`. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoevaluacionficha/command/model/AgregarEstadoEvaluacionFichaCommand.java` | `record` | Intención de negocio (flujo manual). Campos: `UUID evaluacionFichaPerfilId`, `String estadoEvaluacionId` (PK semántica del catálogo). |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoevaluacionficha/command/port/in/AgregarEstadoEvaluacionFichaInputPort.java` | Interface (vacía) | Extiende `InputPort<AgregarEstadoEvaluacionFichaCommand, UUID>` de `shared:domain`. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoevaluacionficha/exception/EvaluacionFichaPerfilNoEncontradaException.java` | Exception | Extiende `ApplicationException` (400). Lanzada cuando la evaluación no existe (flujo manual). |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoevaluacionficha/exception/EstadoEvaluacionNoEncontradoException.java` | Exception | Extiende `ApplicationException` (400). Lanzada cuando el estado del catálogo no existe (flujo manual). |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoevaluacionficha/exception/EstadoEvaluacionDuplicadoException.java` | Exception | Extiende `ApplicationException` (400). Lanzada cuando la evaluación ya tiene ese estado (violación POL-04 — aplica a ambos flujos). |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoevaluacionficha/command/AgregarEstadoEvaluacionFichaUseCase.java` | UseCase | **Flujo manual (b).** `@Component` + `@Transactional(transactionManager = "fichasTransactionManager")`. Inyecta: `EstadoEvaluacionFichaOutputPort`, `EvaluacionFichaPerfilQueryOutputPort`. Flujo: validar FK → validar duplicado (POL-04) → convertir String a enum → pasar último estado al factory → crear aggregate con `crearConEstado(UUID, EstadoEvaluacion, EstadoEvaluacion)` (POL-04 y POL-05 validadas en aggregate via Notification Pattern) → guardar → retornar id. **NO inyecta `EventPublisher`**. Las validaciones de dominio (POL-04: no EN_EVALUACION manual, POL-05: no transición desde terminal) se realizan en el aggregate usando `ValidationResult.addError(...)` y lanzan `DomainValidationException` (422) genérica via `throwIfHasErrors()` — no existe clase de excepción custom. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/evaluacionfichaperfil/query/port/out/EvaluacionFichaPerfilQueryOutputPort.java` | Interface | Puerto de salida read (cross-aggregate lookup). Métodos: `existsById(UUID): boolean`, `obtenerUltimoEstado(UUID): Optional<String>`. **Verificar si ya existe antes de crear — si ya existe, agregar solo `obtenerUltimoEstado`.** |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoevaluacionficha/command/adapter/in/web/dto/AgregarEstadoEvaluacionFichaRequestDTO.java` | `record` | Campos: `UUID evaluacionFichaPerfilId` (`@NotNull`), `String estadoEvaluacionId` (`@NotBlank @Size(max=50)`). Método `toCommand()`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoevaluacionficha/command/adapter/in/web/dto/AgregarEstadoEvaluacionFichaResponseDTO.java` | `record` | Un único campo: `UUID id`. Serializa como `{"id": "..."}`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoevaluacionficha/command/adapter/in/web/AgregarEstadoEvaluacionFichaInputAdapter.java` | `@RestController` | **Flujo manual (b).** Ruta: `POST /fichas-perfil/estado-evaluacion-ficha`. Retorna `ResponseEntity<AgregarEstadoEvaluacionFichaResponseDTO>` con `201 Created` + body `{"id": "..."}`. ADR-011: `@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoevaluacion/persistence/EstadoEvaluacionJpaEntity.java` | JPA Entity | `@Table(name = "estado_evaluacion")`. Campos: `@Id @Column(name = "id") String id`, `@Column(name = "nombre") String nombre`, `@Column(name = "descripcion") String descripcion`. Sin `@GeneratedValue` (PK semántica). |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoevaluacion/persistence/EstadoEvaluacionJpaRepository.java` | `JpaRepository` | `JpaRepository<EstadoEvaluacionJpaEntity, String>`. Compartido por todas las features que usan el catálogo. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoevaluacionficha/persistence/EstadoEvaluacionFichaJpaEntity.java` | JPA Entity | `@Table(name = "estado_evaluacion_ficha")`. Campos: `@Id @Column(name = "id") UUID id`, `@ManyToOne @JoinColumn(name = "evaluacion_ficha_perfil_id") EvaluacionFichaPerfilJpaEntity evaluacionFichaPerfil`, `@ManyToOne @JoinColumn(name = "estado_evaluacion_id") EstadoEvaluacionJpaEntity estadoEvaluacion`, `@Column(name = "fecha_actualizacion") Instant fechaActualizacion`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoevaluacionficha/persistence/EstadoEvaluacionFichaJpaRepository.java` | `JpaRepository` | `JpaRepository<EstadoEvaluacionFichaJpaEntity, UUID>`. Métodos derivados: `existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(UUID, String): boolean`, `countByEvaluacionFichaPerfilId(UUID): long`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoevaluacionficha/persistence/EstadoEvaluacionFichaMapper.java` | `@Component` | Mapea Aggregate ↔ JpaEntity. `toDomain(JpaEntity)` usa `reconstruir(...)` pasando `EstadoEvaluacion.valueOf(entity.getEstadoEvaluacion().getId())` (conversión String→enum). `toEntity(Aggregate)` inyecta `EstadoEvaluacionJpaRepository` y usa `getReferenceById(aggregate.getEstadoEvaluacion().name())` para obtener la entidad del catálogo. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoevaluacionficha/command/adapter/out/persistence/EstadoEvaluacionFichaCommandOutputAdapter.java` | Adapter | Implementa `EstadoEvaluacionFichaOutputPort`. Inyecta `EstadoEvaluacionFichaJpaRepository` y `EstadoEvaluacionFichaMapper`. Usa `reconstruir(...)` al reconstruir. |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.5__crear_estado_evaluacion_y_estado_evaluacion_ficha.sql` | Flyway | **Versión V1.5** (siguiente tras V1.4). Crea tabla catálogo `estado_evaluacion` (PK VARCHAR, poblada con INSERT) + tabla `estado_evaluacion_ficha` (UUID, FKs a evaluacion_ficha_perfil y estado_evaluacion). |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/application/src/main/java/com/arquisoft/fichas/application/evaluacionfichaperfil/command/RegistrarEvaluacionFichaPerfilUseCase.java` | **AJUSTE DE HU-190 — Flujo automático (a):** (1) Inyectar `EstadoEvaluacionFichaOutputPort` en el constructor. (2) Tras `evaluacionFichaPerfilOutputPort.guardar(evaluacion);` (línea 54), agregar llamada `asignarEstadoInicialEvaluacion(evaluacion.getId());`. (3) Agregar método privado `asignarEstadoInicialEvaluacion(UUID evaluacionFichaPerfilId)` al final del use case (análogo a `RegistrarFichaPerfilUseCase.asignarEstadoInicial`), que crea `EstadoEvaluacionFichaAggregate.crear(evaluacionFichaPerfilId)` sin parámetro de estado, lo guarda y loguea. |
| `fichas/application/src/main/java/com/arquisoft/fichas/application/evaluacionfichaperfil/query/port/out/EvaluacionFichaPerfilQueryOutputPort.java` | **Verificar si ya existe antes de modificar.** Si NO existe (porque la HU-190 no creó query side aún), crear el puerto desde cero con `existsById(UUID): boolean` + `obtenerUltimoEstado(UUID): Optional<String>`. Si YA existe, agregar solo el método `obtenerUltimoEstado(UUID): Optional<String>` (retorna el ID del último estado de la evaluación ordenado por `fechaActualizacion DESC`, o `Optional.empty()` si no tiene estados). |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/evaluacionfichaperfil/query/adapter/out/persistence/EvaluacionFichaPerfilQueryOutputAdapter.java` | **Verificar si ya existe.** Si NO existe, crear desde cero implementando `EvaluacionFichaPerfilQueryOutputPort`. Si YA existe, agregar implementación de `obtenerUltimoEstado(UUID)`: consulta JPA derivada del repositorio `EstadoEvaluacionFichaJpaRepository.findFirstByEvaluacionFichaPerfilIdOrderByFechaActualizacionDesc(UUID): Optional<EstadoEvaluacionFichaJpaEntity>`, mapear a `.getEstadoEvaluacion().getId()`. Inyecta `EstadoEvaluacionFichaJpaRepository`. |
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Agregar `public static final class EstadoEvaluacionFicha` con constructor privado vacío (si no existe), y dentro las constantes según inventario abajo. |

### Catálogo de mensajes (`shared:message`) — inventario de constantes a agregar

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.EstadoEvaluacionFicha.CAMPO_EVALUACION_ID` | Campos | `String` | `"evaluacionFichaPerfilId"` | `DomainValidator.notNull` en aggregate |
| `FichasMessages.EstadoEvaluacionFicha.CAMPO_ESTADO_EVALUACION` | Campos | `String` | `"estadoEvaluacion"` | `DomainValidator.notNull` en aggregate (flujo manual) |
| `FichasMessages.EstadoEvaluacionFicha.EVALUACION_NO_ENCONTRADA` | Códigos de error | `String` | `"EVALUACION_NO_ENCONTRADA"` | `EvaluacionFichaPerfilNoEncontradaException` (errorCode) |
| `FichasMessages.EstadoEvaluacionFicha.EVALUACION_NO_ENCONTRADA_MSG` | Mensajes de error | `String` | `"Evaluación de ficha de perfil no encontrada con id: %s"` | `EvaluacionFichaPerfilNoEncontradaException` (mensaje, `.formatted(id)`) |
| `FichasMessages.EstadoEvaluacionFicha.ESTADO_NO_ENCONTRADO` | Códigos de error | `String` | `"ESTADO_NO_ENCONTRADO"` | `EstadoEvaluacionNoEncontradoException` (errorCode) |
| `FichasMessages.EstadoEvaluacionFicha.ESTADO_NO_ENCONTRADO_MSG` | Mensajes de error | `String` | `"Estado de evaluación no encontrado: %s"` | `EstadoEvaluacionNoEncontradoException` (mensaje, `.formatted(estadoId)`) |
| `FichasMessages.EstadoEvaluacionFicha.ESTADO_DUPLICADO` | Códigos de error | `String` | `"ESTADO_DUPLICADO"` | `EstadoEvaluacionDuplicadoException` (errorCode) |
| `FichasMessages.EstadoEvaluacionFicha.ESTADO_DUPLICADO_MSG` | Mensajes de error | `String` | `"La evaluación %s ya tiene el estado %s asignado"` | `EstadoEvaluacionDuplicadoException` (mensaje, `.formatted(evaluacionId, estadoId)`) |
| `FichasMessages.EstadoEvaluacionFicha.TRANSICION_INVALIDA` | Códigos de error | `String` | `"TRANSICION_INVALIDA"` | `ValidationResult.addError(...)` en aggregate (POL-05) → lanza `DomainValidationException` (422) |
| `FichasMessages.EstadoEvaluacionFicha.TRANSICION_DESDE_TERMINAL_SIMPLE_MSG` | Mensajes de error | `String` | `"No se puede agregar un nuevo estado cuando la evaluación ya alcanzó un estado terminal"` | `ValidationResult.addError(...)` en aggregate POL-05 → lanza `DomainValidationException` (422) |
| `FichasMessages.EstadoEvaluacionFicha.ESTADO_EN_EVALUACION_NO_MANUAL` | Códigos de error | `String` | `"ESTADO_EN_EVALUACION_NO_MANUAL"` | `ValidationResult.addError(...)` en aggregate (POL-04) → lanza `DomainValidationException` (422) |
| `FichasMessages.EstadoEvaluacionFicha.ESTADO_EN_EVALUACION_NO_MANUAL_MSG` | Mensajes de error | `String` | `"El estado EN_EVALUACION se asigna al momento de registrar la evaluación y no puede volver a registrarse"` | `ValidationResult.addError(...)` en aggregate POL-04 → lanza `DomainValidationException` (422) |
| `FichasMessages.EstadoEvaluacionFicha.EVALUACION_REQUERIDA` | Códigos de error | `String` | `"EVALUACION_REQUERIDA"` | Validación de dominio en aggregate |
| `FichasMessages.EstadoEvaluacionFicha.ESTADO_REQUERIDO` | Códigos de error | `String` | `"ESTADO_REQUERIDO"` | Validación de dominio en aggregate (flujo manual) |
| `FichasMessages.EstadoEvaluacionFicha.LOG_AGREGADO` | Logs | `String` | `"Estado evaluación ficha agregado manualmente — id={}, evaluacionId={}, estadoId={}"` | `log.info` en use case manual |
| `FichasMessages.EstadoEvaluacionFicha.LOG_CREADO_AUTOMATICO` | Logs | `String` | `"Estado evaluación ficha creado automáticamente — id={}, evaluacionId={}, estadoId={}"` | `log.info` en método privado `asignarEstadoInicialEvaluacion` de `RegistrarEvaluacionFichaPerfilUseCase` |

---

## 8. Detalle por Archivo (selección — archivos clave con cambios respecto al plan inicial)

### `EstadoEvaluacionFichaAggregate.java`
- **Paquete:** `com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate`
- **Tipo:** Aggregate Root (clase plana — **NO extiende `AggregateRoot`**)
- **Responsabilidad:** Entidad raíz con **DOS factories sin sobrecarga** (nombres distintos) para los dos flujos de creación — patrón del proyecto. **Campo tipo enum:** usa `EstadoEvaluacion estadoEvaluacion` (enum, NO String) siguiendo el patrón de `EstadoFichaPerfilAggregate`. El mapper JPA convierte entre String (columna BD VARCHAR) ↔ enum (campo domain).
- **Features Java 21 aplicables:** N/A (clase inmutable con fields `final`)
- **Métodos principales:**
    - `crear(UUID evaluacionFichaPerfilId): EstadoEvaluacionFichaAggregate` — **Flujo (a) automático:** factory sin parámetro de estado, hardcodea `EstadoEvaluacion.EN_EVALUACION` (enum) en setter privado `setEstadoEvaluacionInicial()` sin parámetro. Solo valida obligatoriedad del UUID. Análogo a `EstadoFichaPerfilAggregate.crear(UUID)`.
    - `crearConEstado(UUID evaluacionFichaPerfilId, EstadoEvaluacion estadoEvaluacion): EstadoEvaluacionFichaAggregate` — **Flujo (b) manual:** factory con nombre distinto (no sobrecarga) que recibe estado como enum, valida obligatoriedad del UUID y del enum con `setEstadoEvaluacion(EstadoEvaluacion, ValidationResult)` que llama a `DomainValidator.notNull`.
    - `reconstruir(UUID id, UUID evaluacionFichaPerfilId, EstadoEvaluacion estadoEvaluacion, Instant fechaActualizacion): EstadoEvaluacionFichaAggregate` — factory sin validar, usado por el mapper (recibe el enum directamente).
    - `setEstadoEvaluacionInicial()` (sin parámetro) — setter privado para flujo automático, hardcodea `this.estadoEvaluacion = EstadoEvaluacion.EN_EVALUACION;`.
    - `setEstadoEvaluacion(EstadoEvaluacion estadoEvaluacion, ValidationResult result)` (con parámetro) — setter privado para flujo manual, valida y asigna el enum.
    - Getters: `getId()`, `getEvaluacionFichaPerfilId()`, `getEstadoEvaluacion()` (retorna `EstadoEvaluacion` enum), `getFechaActualizacion()`
- **Dependencias:** `com.arquisoft.shared.validation.*`, `com.arquisoft.shared.util.{UtilUUID, UtilDate}`, `com.arquisoft.shared.message.FichasMessages`, `EstadoEvaluacion` (enum del mismo domain)

### `RegistrarEvaluacionFichaPerfilUseCase.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.application.evaluacionfichaperfil.command`
- **Tipo:** UseCase (ya existente, de HU-190)
- **Responsabilidad:** Registrar nueva evaluación de ficha perfil + **ajuste: inicializar estado EN_EVALUACION automáticamente** (análogo a `RegistrarFichaPerfilUseCase`).
- **Cambios requeridos:**
    1. **Inyección adicional:** agregar `private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;` al constructor (ya tiene `@RequiredArgsConstructor`).
    2. **Llamada post-persistencia (línea 54):** tras `evaluacionFichaPerfilOutputPort.guardar(evaluacion);`, agregar `asignarEstadoInicialEvaluacion(evaluacion.getId());`.
    3. **Método privado nuevo (al final del use case):**
       ```java
       private void asignarEstadoInicialEvaluacion(UUID evaluacionFichaPerfilId) {
           var estadoInicial = EstadoEvaluacionFichaAggregate.crear(evaluacionFichaPerfilId);  // factory sin parámetro de estado
           estadoEvaluacionFichaOutputPort.guardar(estadoInicial);
           log.info(FichasMessages.EstadoEvaluacionFicha.LOG_CREADO_AUTOMATICO,
                    estadoInicial.getId(),
                    estadoInicial.getEvaluacionFichaPerfilId(),
                    estadoInicial.getEstadoEvaluacion());  // retorna enum
       }
       ```
- **Dependencias adicionales:** `EstadoEvaluacionFichaOutputPort`, `EstadoEvaluacionFichaAggregate`, `FichasMessages.EstadoEvaluacionFicha`

### `AgregarEstadoEvaluacionFichaUseCase.java`
- **Paquete:** `com.arquisoft.fichas.application.estadoevaluacionficha.command`
- **Tipo:** UseCase
- **Responsabilidad:** **Flujo (b) manual** — orquesta la creación de estado evaluación con validaciones POL-01, POL-04, POL-05.
- **Anotaciones:** `@Component`, `@RequiredArgsConstructor`, `@Slf4j`, `@Transactional(transactionManager = "fichasTransactionManager")`
- **Métodos principales:**
    - `ejecutar(AgregarEstadoEvaluacionFichaCommand): UUID` — implementa `InputPort<Command, UUID>`. Flujo: validar FK evaluación → convertir String del command a enum con `EstadoEvaluacion.valueOf(command.estadoEvaluacionId())` (throws `IllegalArgumentException` si no existe) → validar POL-04 (duplicado, query con String) → validar POL-05 (terminal, compara enum con constantes) → validar POL-01 (primer estado — aunque ya no debería serlo) → crear aggregate con factory `crearConEstado(UUID, EstadoEvaluacion enum)` → guardar → loguear con `LOG_AGREGADO` → retornar id
- **Dependencias:** `EstadoEvaluacionFichaOutputPort`, `EvaluacionFichaPerfilQueryOutputPort`, `EstadoEvaluacionJpaRepository`, excepciones del paquete, `EstadoEvaluacionFichaAggregate`, `EstadoEvaluacion` (enum para conversión), `FichasMessages`
- **NO inyecta `EventPublisher`** — sin eventos de dominio

*(El resto de archivos siguen la descripción del plan inicial — se omiten por brevedad)*

---

## 9. Endpoints REST

### Estado del endpoint

- [x] **Endpoint NUEVO** — crear `AgregarEstadoEvaluacionFichaInputAdapter.java` desde cero (flujo manual b).

### Contrato del endpoint

| Método | Ruta | Request Body / Params | Response | Código HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|----------------------|----------|-------------|----------------------|-------------------------------|
| POST | `/fichas-perfil/estado-evaluacion-ficha` | `AgregarEstadoEvaluacionFichaRequestDTO` | `AgregarEstadoEvaluacionFichaResponseDTO` (body `{"id": "..."}`) | 201 | `fichas:estado-evaluacion-ficha:create` | `@Operation(summary="Agregar estado evaluación ficha")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses` (201, 400, 401, 403, 422) |

**Nota:** El flujo automático (a) NO expone endpoint REST — es una transacción interna del `RegistrarEvaluacionFichaPerfilUseCase`.

---

## 10. Seguridad y Autorización (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Roles realm que lo poseen | Endpoint(s) que lo requieren | Descripción funcional |
|---|---|---|---|
| `fichas:estado-evaluacion-ficha:create` | `representante-comite` | `POST /fichas-perfil/estado-evaluacion-ficha` | Permite agregar un nuevo registro de estado manualmente a la trazabilidad de una evaluación de ficha de perfil (flujo b) |

**Nota:** El flujo automático (a) no requiere autorización específica — hereda la autorización del endpoint `POST /api/evaluaciones-ficha-perfil` de HU-190.

### Reglas de uso

1. **Formato del client role:** `fichas:estado-evaluacion-ficha:create` — contexto + recurso + acción, todo en kebab-case.
2. El recurso `estado-evaluacion-ficha` es multi-palabra separada por guiones (no `estadoEvaluacionFicha` en camelCase).
3. Solo el rol realm `representante-comite` recibe este client role.
4. El endpoint tiene exactamente un `@PreAuthorize("hasAuthority('fichas:estado-evaluacion-ficha:create')")`.

### Configuración requerida en Keycloak (instrucciones para equipo de seguridad)

1. En el cliente `arquisoft-api`: crear el client role `fichas:estado-evaluacion-ficha:create`.
2. Asignar el client role al rol realm `representante-comite`.
3. Verificar que los usuarios de prueba con rol `representante-comite` reciban el client role en su JWT (`resource_access.arquisoft-api.roles`).

---

## 11. Eventos RabbitMQ

**Eventos: ninguno.**

**Razón:** CRUD interno puro sin consumidores conocidos ni casos de auditoría identificados. La HU solo registra trazabilidad de cambios de estado para consulta posterior. Ninguno de los dos use cases (`RegistrarEvaluacionFichaPerfilUseCase` modificado ni `AgregarEstadoEvaluacionFichaUseCase` nuevo) inyecta `EventPublisher`.

---

## 12. Migración de Base de Datos

- **Archivo:** `V1.5__crear_estado_evaluacion_y_estado_evaluacion_ficha.sql`
- **Base de datos:** `fichas_perfil` (contexto `fichas` → BD `fichas_perfil` según tabla de mapeo del skill)
- **Sin schemas:** las tablas se crean sin prefijo (`CREATE TABLE estado_evaluacion`, no `CREATE TABLE fichas_perfil.estado_evaluacion`)
- **Sin FKs cruzadas entre BDs:** la FK a `evaluacion_ficha_perfil` está en la misma BD, no hay FKs cruzadas
- **Cambios:**
    - Crear tabla catálogo `estado_evaluacion` con PK semántica `VARCHAR(50)` (ADR-012), campos `nombre` y `descripcion`.
    - Poblar con 5 estados:
        - `INSERT INTO estado_evaluacion VALUES ('EN_EVALUACION', 'En Evaluación', 'La evaluación está en proceso de revisión');`
        - `INSERT INTO estado_evaluacion VALUES ('APROBADA', 'Aprobada', 'La evaluación ha sido aprobada sin observaciones');`
        - `INSERT INTO estado_evaluacion VALUES ('APROBADA_CON_OBSERVACIONES', 'Aprobada Con Observaciones', 'La evaluación ha sido aprobada con observaciones que requieren seguimiento');`
        - `INSERT INTO estado_evaluacion VALUES ('NO_APROBADA', 'No Aprobada', 'La evaluación no ha sido aprobada');`
        - `INSERT INTO estado_evaluacion VALUES ('DESCARTADA', 'Descartada', 'La evaluación ha sido descartada');`
    - Crear tabla `estado_evaluacion_ficha` con PK UUID, FK a `evaluacion_ficha_perfil` (ON DELETE CASCADE) y FK a `estado_evaluacion`, campo `fecha_actualizacion` TIMESTAMP NOT NULL.
    - Índice opcional: `CREATE INDEX idx_estado_evaluacion_ficha_evaluacion ON estado_evaluacion_ficha(evaluacion_ficha_perfil_id);` para optimizar lookups de último estado.

---

## 13. Casos de Prueba Sugeridos

**Tipo de HU:** Escritura (sin eventos de dominio) con **patrón dual** (flujo automático + flujo manual)

**Presupuesto orientativo:** Pequeña-Mediana (1 endpoint nuevo + 1 modificación de use case existente + 1 entidad nueva + 1 catálogo) → **30-40 tests**

### Tests capa `domain` (Aggregate Root — SIN eventos, DOS factories)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EstadoEvaluacionFichaAggregateTest` | `debeConstruirEntidadAutomatica_cuandoFactoryCrear` | `crear(UUID)` crea entidad con UUID e Instant no nulos, `estadoEvaluacion == EstadoEvaluacion.EN_EVALUACION` hardcodeado |
| `EstadoEvaluacionFichaAggregateTest` | `debeConstruirEntidadManual_cuandoFactoryCrearConEstado` | `crearConEstado(UUID, EstadoEvaluacion)` crea entidad con UUID, Instant y enum parametrizado no nulos |
| `EstadoEvaluacionFichaAggregateTest` | `debeReconstruirSinValidar_cuandoReconstruirEsInvocado` | `reconstruir(...)` no lanza excepción con datos inválidos (no valida) |
| `EstadoEvaluacionFichaAggregateTest` | `debeLanzarExcepcion_cuandoEvaluacionIdEsNulEnFactoryCrear` | factory `crear(UUID)` con evaluacionId nulo lanza |
| `EstadoEvaluacionFichaAggregateTest` | `debeLanzarExcepcion_cuandoEvaluacionIdEsNulEnFactoryCrearConEstado` | factory `crearConEstado(UUID, EstadoEvaluacion)` con evaluacionId nulo lanza |
| `EstadoEvaluacionFichaAggregateTest` | `debeLanzarExcepcion_cuandoEstadoIdEsNulEnFactoryCrearConEstado` | factory `crearConEstado(UUID, EstadoEvaluacion)` con enum nulo lanza |

**NO incluir:** tests de ciclo de eventos (`publishEvent`, `drainUnPublishedEvents`, `getUnPublishedEvents`) — el aggregate NO extiende `AggregateRoot`.

### Tests capa `application` (DOS use cases)

#### Tests de `RegistrarEvaluacionFichaPerfilUseCase` (MODIFICADO — flujo automático a)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `RegistrarEvaluacionFichaPerfilUseCaseTest` | `debeCrearEstadoInicialAutomatico_cuandoRegistrarEvaluacion` | tras `ejecutar(command)`, verificar que `estadoEvaluacionFichaOutputPort.guardar(...)` fue llamado con aggregate con `estadoEvaluacion == EstadoEvaluacion.EN_EVALUACION` |

*(Los tests existentes de HU-190 se mantienen — se agrega solo este test para el flujo automático)*

#### Tests de `AgregarEstadoEvaluacionFichaUseCase` (NUEVO — flujo manual b)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `AgregarEstadoEvaluacionFichaUseCaseTest` | `debeAgregar_cuandoDatosValidos` | flujo exitoso completo — retorna UUID no nulo |
| `AgregarEstadoEvaluacionFichaUseCaseTest` | `debeLanzarEvaluacionNoEncontrada_cuandoEvaluacionNoExiste` | `existsById` retorna `false` → lanza `EvaluacionFichaPerfilNoEncontradaException` |
| `AgregarEstadoEvaluacionFichaUseCaseTest` | `debeLanzarEstadoNoEncontrado_cuandoCatalogoNoExiste` | `IllegalArgumentException` al convertir String a enum → se captura y lanza `EstadoEvaluacionNoEncontradoException` |
| `AgregarEstadoEvaluacionFichaUseCaseTest` | `debeLanzarEstadoDuplicado_cuandoYaExiste` | `existsByEvaluacionAndEstado` retorna `true` → lanza `EstadoEvaluacionDuplicadoException` |
| `AgregarEstadoEvaluacionFichaUseCaseTest` | `debeLanzarDomainValidation_cuandoEstadoTerminal` | `obtenerUltimoEstado` retorna `"APROBADA"` (terminal) → aggregate valida con `result.addError(TRANSICION_INVALIDA, ...)` → lanza `DomainValidationException` con errorCode POL-05 |
| `AgregarEstadoEvaluacionFichaUseCaseTest` | `debeLanzarDomainValidation_cuandoIntentaEnEvaluacionManual` | `estadoId == "EN_EVALUACION"` → aggregate valida con `result.addError(ESTADO_EN_EVALUACION_NO_MANUAL, ...)` → lanza `DomainValidationException` con errorCode POL-04 |
| `AgregarEstadoEvaluacionFichaUseCaseTest` | `debePermitirSegundoEstado_cuandoYaExisteEnEvaluacion` | Ya existe EN_EVALUACION del flujo automático, se agrega otro estado válido (no EN_EVALUACION, no terminal previo) → OK |
| `AgregarEstadoEvaluacionFichaUseCaseTest` | `debeLanzarExcepcion_cuandoRepositorioFalla` | propaga error de repositorio |

**NO incluir:** `verify(eventPublisher).publish(...)` — ninguno de los dos use cases inyecta `EventPublisher`.

### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EstadoEvaluacionFichaCommandOutputAdapterTest` | `debeGuardar_cuandoEntidadEsValida` | persistencia OK |
| `EstadoEvaluacionFichaCommandOutputAdapterTest` | `debeReconstruirConReconstruir_cuandoFindByIdExiste` | adapter usa `reconstruir(...)` |
| `EstadoEvaluacionFichaCommandOutputAdapterTest` | `debeRetornarTrue_cuandoExisteByEvaluacionAndEstado` | método `existsByEvaluacionAndEstado` delega correctamente |
| `EstadoEvaluacionFichaCommandOutputAdapterTest` | `debeContarEstados_cuandoContarEstadosPorEvaluacion` | método `contarEstadosPorEvaluacion` delega correctamente |
| `AgregarEstadoEvaluacionFichaInputAdapterTest` | `debe201_cuandoPeticionValida` | created OK con body `{"id": "..."}` |
| `AgregarEstadoEvaluacionFichaInputAdapterTest` | `debe400_cuandoRequestInvalido` | validación Jakarta falla (evaluacionId nulo o estadoId vacío) |
| `AgregarEstadoEvaluacionFichaInputAdapterTest` | `debe400_cuandoEvaluacionNoExiste` | use case lanza `EvaluacionFichaPerfilNoEncontradaException` → GlobalAppExceptionHandler mapea a 400 |
| `AgregarEstadoEvaluacionFichaInputAdapterTest` | `debe400_cuandoEstadoDuplicado` | use case lanza `EstadoEvaluacionDuplicadoException` → 400 |
| `AgregarEstadoEvaluacionFichaInputAdapterTest` | `debe422_cuandoValidacionDominio` | aggregate lanza `DomainValidationException` (POL-04 o POL-05) → GlobalAppExceptionHandler mapea a 422 |
| `AgregarEstadoEvaluacionFichaInputAdapterTest` | `debe401_cuandoNoAutenticado` | sin token |
| `AgregarEstadoEvaluacionFichaInputAdapterTest` | `debe403_cuandoRolInsuficiente` | autenticado pero sin `fichas:estado-evaluacion-ficha:create` |
| `RegistrarEvaluacionFichaPerfilInputAdapterTest` | `debeCrearEstadoInicial_cuandoRegistrarEvaluacion` | **Test de integración adicional para HU-190:** tras registrar evaluación, verificar que existe un `EstadoEvaluacionFicha` con estado `EN_EVALUACION` asociado a la evaluación recién creada |

### Reglas de consolidación aplicadas

- No se crean tests separados para cada validación Jakarta (un solo test `debe400_cuandoRequestInvalido`).
- No se testean getters/setters generados por Lombok.
- No se testean métodos privados.
- No se crean tests duplicados con asserts distintos — se consolida en un solo test con múltiples asserts.

**Total estimado:** ~33 tests (6 domain + 9 application + 18 infrastructure) — dentro del rango 30-40 para HU pequeña-mediana con patrón dual.

---

## 14. Checklist de Implementación

- [ ] **DDD:** Entidad de dominio **NO extiende `AggregateRoot`** (CRUD sin eventos)
- [ ] Entidad inmutable: constructor privado, campos `final`, **DOS factories sin sobrecarga** (nombres distintos) `crear(UUID)` / `crearConEstado(UUID, String)` + `reconstruir`, sin Lombok — patrón del proyecto
- [ ] **NO se crean archivos en `domain/estadoevaluacionficha/event/`** — la HU no emite eventos
- [ ] Factory automático `crear(UUID)` **NO llama `publishEvent`** — hardcodea `"EN_EVALUACION"` en setter privado `setEstadoEvaluacionInicial()` sin parámetro
- [ ] Factory manual `crearConEstado(UUID, EstadoEvaluacion)` **NO llama `publishEvent`** — valida enum parametrizado en setter privado `setEstadoEvaluacion(EstadoEvaluacion, ValidationResult)` con `DomainValidator.notNull`
- [ ] IDs siempre `UUID` (nunca `Long` / `Integer`) — salvo PK semántica del catálogo (VARCHAR)
- [ ] Puerto de entrada manual (`AgregarEstadoEvaluacionFichaInputPort`) extiende `InputPort<Command, UUID>`
- [ ] Puerto de salida write (`EstadoEvaluacionFichaOutputPort`) definido en `domain/` con métodos `guardar`, `existsByEvaluacionAndEstado`, `contarEstadosPorEvaluacion`
- [ ] Puerto de salida read (`EvaluacionFichaPerfilQueryOutputPort`) definido en `application/` (cross-aggregate lookup) con `existsById` + `obtenerUltimoEstado`
- [ ] **Modificación de HU-190:** `RegistrarEvaluacionFichaPerfilUseCase` inyecta `EstadoEvaluacionFichaOutputPort` y llama a `asignarEstadoInicialEvaluacion(UUID)` tras persistir evaluación
- [ ] Excepciones de aplicación extienden `ApplicationException` (400) — `EvaluacionFichaPerfilNoEncontradaException`, `EstadoEvaluacionNoEncontradoException`, `EstadoEvaluacionDuplicadoException`
- [ ] Validaciones de dominio (POL-04: no EN_EVALUACION manual, POL-05: no transición desde terminal) usan Notification Pattern en aggregate: `ValidationResult.addError(...)` + `throwIfHasErrors()` → lanza `DomainValidationException` (422) genérica — **NO existe clase de excepción custom TransicionEstadoInvalidaException**.
- [ ] **Cada excepción extiende la clase base correcta** para que `GlobalAppExceptionHandler` resuelva su HTTP automáticamente. **NO se crea handler de contexto** — el handler global de `shared:web` basta.
- [ ] `Command` (`record` en `application/estadoevaluacionficha/command/model/`) y `RequestDTO` (`record` en `infrastructure/.../dto/`) creados con `toCommand()`. Campos en español idénticos al aggregate.
- [ ] Caso de uso manual (`AgregarEstadoEvaluacionFichaUseCase`) con `@RequiredArgsConstructor`, `@Transactional(transactionManager = "fichasTransactionManager")` **sin drenado de eventos** (no inyecta `EventPublisher`)
- [ ] Controller REST con `@Valid @RequestBody`, autorización vía `@PreAuthorize("hasAuthority('fichas:estado-evaluacion-ficha:create')")` **en kebab-case**, retorna `ResponseEntity<AgregarEstadoEvaluacionFichaResponseDTO>` con `201 Created` + body `{"id": "..."}`
- [ ] Controller documentado con `@Tag`, `@Operation`, `@ApiResponses` y `@SecurityRequirement` (ADR-011)
- [ ] Entidad JPA `EstadoEvaluacionFichaJpaEntity` con `@Table(name = "estado_evaluacion_ficha")` (sin atributo `schema`) y **todo `@Column`/`@JoinColumn` con `name` explícito (incluido el `@Id`)**
- [ ] Catálogo JPA `EstadoEvaluacionJpaEntity` con PK semántica `@Id String id` (sin `@GeneratedValue`)
- [ ] Adaptador de repositorio creado (`EstadoEvaluacionFichaCommandOutputAdapter`) — implementa el `OutputPort` write
- [ ] Migración Flyway `V1.5__crear_estado_evaluacion_y_estado_evaluacion_ficha.sql` (siguiente número secuencial del contexto tras V1.4) en `db/migration/fichas/`, BD `fichas_perfil`, sin prefijo de schema en el SQL
- [ ] **NO se publican eventos RabbitMQ** — ninguno de los dos use cases emite eventos
- [ ] Tests unitarios con patrón AAA (cobertura ≥ 75%), **sin tests de ciclo de eventos** (no aplican a CRUD sin `AggregateRoot`), con tests de ambos factories (`crear` y `crearConEstado`)
- [ ] Tests de repositorio con H2
- [ ] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4.x)
- [ ] **Test de integración adicional para HU-190:** verificar que al registrar evaluación se crea automáticamente el estado EN_EVALUACION
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot)
- [ ] Catálogo `shared:message` actualizado con las 14 constantes inventariadas arriba (agregar nested class `EstadoEvaluacionFicha` dentro de `FichasMessages.java`)
- [ ] Commit: `feat(fichas): agregar estado evaluacion ficha con inicializacion automatica (HU-191)`

---

## 15. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha      | Notas |
|------------|---------------------|--------------|------------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-07-08 | Build -x test -x jacocoTestCoverageVerification: sin errores. Cobertura JaCoCo pendiente de tests (fichas:domain 67%, fichas:application 64%). |
| Tests      | @tester             | ✅ Completado | 2026-07-09 | 33 tests generados (6 domain + 9 application + 18 infrastructure). `check` completo (test + checkstyle + cobertura ≥75%): ✅ VERDE en las 3 capas. |
| Validación | @validator-analyze  | ✅ Completado | 2026-07-09 | Score: 100/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-07-09 | /.workspace/validator/validator-HU-191.md |
| Commit     | @commit             | ⏳ Pendiente |            |       |
