# PLAN: HU190 — Registrar nueva evaluación de ficha de perfil

## Metadata
- **ID Historia:** HU-190
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura
- **¿Usa AggregateRoot?:** No — la HU no emite eventos. `EvaluacionFichaPerfilAggregate` es una clase plana con factories `crear`/`reconstruir`.
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-07-06
- **Rama sugerida:** `feature/HU-190-registrar-evaluacion-ficha-perfil`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** El endpoint no recibe request body — el `fichaPerfilId` se toma únicamente del path param `{fichaId}` (se eliminó el `RequestDTO` redundante).

---

## 1. Resumen Funcional

Esta HU permite al Representante del Comité de Currículum registrar una nueva evaluación sobre una ficha de perfil existente. El sistema debe validar que la ficha exista, que el representante exista (extraído del JWT del usuario autenticado), y que el representante no tenga ya una evaluación para esa misma ficha (unicidad por par representante+ficha). La evaluación se crea sin estado inicial ni observaciones — esos flujos corresponden a HUs posteriores. El sistema retorna el UUID de la evaluación creada.

**Qué NO cubre:** gestión de estados de evaluación, observaciones, consultas de evaluaciones existentes, aprobación/rechazo de fichas.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | Representante autenticado con rol `representante-comite` puede registrar evaluación para una ficha existente | Sistema crea evaluación y retorna UUID con 201 Created |
| 2 | Representante intenta registrar segunda evaluación para la misma ficha | Sistema rechaza con 400 Bad Request — evaluación duplicada |
| 3 | Representante intenta evaluar ficha inexistente | Sistema rechaza con 400 Bad Request — ficha no encontrada |
| 4 | Usuario autenticado no tiene rol `representante-comite` | Sistema rechaza con 403 Forbidden |
| 5 | Usuario no autenticado intenta registrar evaluación | Sistema rechaza con 401 Unauthorized |
| 6 | `fichaId` del path malformado (no es UUID válido) | Sistema rechaza con 400 Bad Request |

---

## 3. Reglas de Negocio

- **RN1 (Unicidad):** Un representante del comité NO puede tener dos evaluaciones para la misma ficha de perfil. Constraint UNIQUE en BD: `(representante_comite_id, ficha_perfil_id)`. Una ficha SÍ puede tener varias evaluaciones de distintos representantes.
- **RN2 (Validación de existencia — Ficha):** La ficha de perfil debe existir en la BD del contexto `fichas` antes de crear la evaluación. Validación vía `FichaPerfilOutputPort.existsById(fichaId)`.
- **RN3 (Validación de existencia — Representante):** El representante del comité debe existir como réplica local en la tabla `representante_comite_curriculum`. El `representante_comite_id` NO viene en el body del request — se extrae del claim `sub` del JWT token del usuario autenticado. Validación vía `RepresentanteComiteQueryOutputPort.existsById(representanteId)`.
- **RN4 (Sin estado inicial):** La evaluación se crea sin estado. El estado se registra en un flujo/HU posterior mediante la tabla `estado_evaluacion_ficha` (no cubierta en esta HU).
- **RN5 (Sin observaciones iniciales):** La evaluación se crea sin observaciones. Las observaciones se agregan en un flujo/HU posterior mediante la tabla `observacion_evaluacion` (no cubierta en esta HU).
- **RN6 (Autogeneración de UUID y timestamp):** El `id` de la evaluación se autogenera con `UtilUUID.generateNewUUID()`. La `fechaCreacion` se autogenera con `UtilDate.generateNewInstantNow()`.

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `EvaluacionFichaPerfilAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** No — la HU no emite eventos. Es una clase plana con factories `crear`/`reconstruir`.
- **ID:** `UUID`

### Atributos por objeto de dominio

#### `EvaluacionFichaPerfil`

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí (vía `UtilUUID.generateNewUUID()`) | Identifica el registro |
| `representanteComiteId` | `UUID` | — | Sí | No | No | FK ajena a tabla `representante_comite_curriculum` (réplica local). Extraído del JWT (`sub` claim), NO viene en el request body |
| `fichaPerfilId` | `UUID` | — | Sí | No | No | FK local a tabla `ficha_perfil` |
| `fechaCreacion` | `Instant` | — | Sí | No | Sí (vía `UtilDate.generateNewInstantNow()`) | Timestamp de creación |

**Combinaciones únicas (Restricciones):**
- **Evaluación única por representante y ficha:** `(representanteComiteId, fichaPerfilId)` → traducción: `UNIQUE` constraint en Flyway + validación de duplicado previo en use case antes de guardar.

#### `RepresentanteComiteCurriculum` (réplica local — vista materializada del contexto `usuarios`)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | No | PK — valor que llega del evento `UsuarioCreadoEvent` del contexto `usuarios` |
| `identificador` | `String` | 4-30 | Sí | Sí | No | Identificador del usuario (Limpiar espacios) |
| `nombre` | `String` | 2-50 | Sí | Sí | No | Nombre del usuario (Limpiar espacios) |
| `email` | `String` | 6-50 | Sí | Sí | No | Email del usuario (Limpiar espacios, formato email) |

> **Nota sobre réplicas locales:** Esta tabla se puebla escuchando eventos del contexto `usuarios`. La configuración del consumer AMQP (`UsuarioCreadoInputAdapter` análogo al existente en el contexto `fichas` para estudiantes/asesores) NO está cubierta en esta HU — se asume que se implementará en una HT de infraestructura o HU futura.

### Traducción del modelo enriquecido a código

| Característica del modelo | Traducción en código |
|---|---|
| Obligatorio (UUID) | `@NotNull` en DTO + `@Column(nullable=false)` en JPA + `NOT NULL` en Flyway + validación con `DomainValidator.notNull` en constructor del Aggregate |
| No modificable | NO se genera setter ni método `cambiar{Atributo}()` en la entidad |
| Autogenerado (UUID) | `UtilUUID.generateNewUUID()` dentro del setter `setId()`, no en el cuerpo de `crear(...)` |
| Autogenerado (Instant) | `UtilDate.generateNewInstantNow()` dentro del setter `setFechaCreacion()` |
| Combinación única | `UNIQUE` constraint en Flyway + validación de unicidad en use case antes de persistir (`evaluacionFichaPerfilOutputPort.existsByRepresentanteAndFicha(...)`) |

### Eventos de Dominio que emite

**Eventos: ninguno.**

**Razón:** CRUD interno sin consumidores conocidos ni casos de auditoría identificados. Esta es la primera HU del flujo de evaluación de fichas de perfil; futuras HUs (gestión de estados, observaciones, aprobación/rechazo) podrían introducir eventos cuando haya consumidores claros.

**Implicaciones:**
- La entidad raíz `EvaluacionFichaPerfilAggregate` NO extiende `AggregateRoot` — es una clase plana con factories `crear`/`reconstruir`.
- El factory `crear(...)` NO acumula eventos (no existe `publishEvent`).
- El use case NO inyecta `EventPublisher`, no hay drenado de eventos.
- No se crean archivos en `domain/evaluacionfichaperfil/event/`.

---

## 5. Integraciones Externas (solo si la HU lo requiere)

**No aplica.** Esta HU solo requiere persistencia PostgreSQL local. El `representante_comite_id` se extrae del JWT token del usuario autenticado (Spring Security ya lo resuelve), no hay llamada a Keycloak API ni otros sistemas externos.

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS — caso de uso write (Registrar evaluación)

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/evaluacionfichaperfil/aggregate/EvaluacionFichaPerfilAggregate.java` | Aggregate Root (clase plana, NO extiende `AggregateRoot`) | Factory `crear(...)` con Notification Pattern + `reconstruir(...)` sin validar. Campos final, constructor privado, setters privados con validación. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/evaluacionfichaperfil/port/out/EvaluacionFichaPerfilOutputPort.java` | Interface | Puerto de salida write. Métodos: `void guardar(EvaluacionFichaPerfilAggregate)`, `boolean existsById(UUID)`, `boolean existsByRepresentanteAndFicha(UUID representanteId, UUID fichaId)` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/evaluacionfichaperfil/command/model/RegistrarEvaluacionFichaPerfilCommand.java` | `record` | Campos: `UUID fichaPerfilId`, `UUID representanteComiteId` (extraído del JWT, no del request body) |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/evaluacionfichaperfil/command/port/in/RegistrarEvaluacionFichaPerfilInputPort.java` | Interface (vacía) | Extiende `InputPort<RegistrarEvaluacionFichaPerfilCommand, UUID>` de `shared:domain` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/evaluacionfichaperfil/exception/EvaluacionFichaPerfilDuplicadaException.java` | Exception | Extiende `ApplicationException` (duplicado → 400). Va en `application/{entidad}/exception/`, sin anidar en `command/`. Constructor: `(UUID representanteId, UUID fichaId)` con mensaje parametrizado del catálogo. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/evaluacionfichaperfil/exception/RepresentanteComiteNoEncontradoException.java` | Exception | Extiende `ApplicationException` (no encontrado → 400). Constructor: `(UUID representanteId)` con mensaje parametrizado del catálogo. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/evaluacionfichaperfil/command/RegistrarEvaluacionFichaPerfilUseCase.java` | UseCase | `@Component` + `@RequiredArgsConstructor` + `@Slf4j` + `@Transactional(transactionManager = "fichasTransactionManager")`. Implementa el `InputPort`. Patrón: validar existencia ficha → validar existencia representante → validar no duplicado → crear aggregate → guardar → log → retornar UUID |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/representantecomite/query/port/out/RepresentanteComiteQueryOutputPort.java` | Interface | Puerto de salida read (vive en `application`, no en domain). Método: `boolean existsById(UUID id)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/evaluacionfichaperfil/command/adapter/in/web/RegistrarEvaluacionFichaPerfilInputAdapter.java` | `@RestController` | Inyecta el `InputPort` + extrae `representanteComiteId` del JWT con `@AuthenticationPrincipal Jwt jwt` → `UUID.fromString(jwt.getSubject())` (mismo patrón de `ModificarFichaPerfilInputAdapter`). Endpoint: `POST /fichas-perfil/{fichaId}/evaluaciones` con `@PreAuthorize("hasAuthority('fichas:evaluacion-ficha-perfil:create')")`. **SIN request body ni RequestDTO** — el `fichaPerfilId` se toma del path param `{fichaId}`. Construye el `Command` directamente con `(fichaId, representanteComiteId)`. Retorna `ResponseEntity<RegistrarEvaluacionFichaPerfilResponseDTO>` con `201 Created` y body `{"id": "<uuid>"}` (mismo patrón que `RegistrarFichaPerfilResponseDTO`). **SIN** header `Location` (según respuesta del usuario). ADR-011: `@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/evaluacionfichaperfil/command/adapter/in/web/dto/RegistrarEvaluacionFichaPerfilResponseDTO.java` | `record` | Response DTO: `record RegistrarEvaluacionFichaPerfilResponseDTO(UUID id)` — envuelve el UUID creado para responder `{"id": "<uuid>"}` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/evaluacionfichaperfil/persistence/EvaluacionFichaPerfilJpaEntity.java` | JPA Entity | `@Table(name = "evaluacion_ficha_perfil")` (sin atributo `schema`). Campos: `id`, `representanteComiteId`, `fichaPerfilId`, `fechaCreacion`. Sin relaciones JPA `@ManyToOne` — solo UUIDs planos. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/evaluacionfichaperfil/persistence/EvaluacionFichaPerfilJpaRepository.java` | `JpaRepository` | Extiende `JpaRepository<EvaluacionFichaPerfilJpaEntity, UUID>`. Métodos custom: `boolean existsByRepresentanteComiteIdAndFichaPerfilId(UUID representanteId, UUID fichaId)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/evaluacionfichaperfil/persistence/EvaluacionFichaPerfilMapper.java` | `@Component` | Mapea `EvaluacionFichaPerfilAggregate` ↔ `EvaluacionFichaPerfilJpaEntity`. Métodos: `toJpaEntity(aggregate)`, `toDomain(entity)` (usa `reconstruir(...)`). |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/evaluacionfichaperfil/command/adapter/out/persistence/EvaluacionFichaPerfilCommandOutputAdapter.java` | Adapter | `@Component` + `@RequiredArgsConstructor`. Implementa `EvaluacionFichaPerfilOutputPort`. Métodos: `guardar(...)` vía mapper + repository, `existsById(...)`, `existsByRepresentanteAndFicha(...)` vía repository. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/representantecomite/persistence/RepresentanteComiteJpaEntity.java` | JPA Entity | `@Table(name = "representante_comite_curriculum")`. Campos: `id`, `identificador`, `nombre`, `email`. Sin relaciones JPA. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/representantecomite/persistence/RepresentanteComiteJpaRepository.java` | `JpaRepository` | Extiende `JpaRepository<RepresentanteComiteJpaEntity, UUID>`. Sin métodos custom. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/representantecomite/query/adapter/out/persistence/RepresentanteComiteQueryOutputAdapter.java` | Adapter | `@Component` + `@RequiredArgsConstructor`. Implementa `RepresentanteComiteQueryOutputPort`. Método: `existsById(...)` vía repository. |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.4__crear_evaluacion_ficha_perfil.sql` | Flyway | Versión = `V1.4` (siguiente tras `V1.3`). Crear tablas: `representante_comite_curriculum`, `evaluacion_ficha_perfil`. **NO** crear tablas `estado_evaluacion`, `estado_evaluacion_ficha`, `observacion_evaluacion` (HUs posteriores). |

### Catálogo de mensajes (`shared:message`) — MODIFICAR

| Capa | Ruta completa | Tipo | Acción | Detalles |
|------|---------------|------|--------|----------|
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Catálogo | MODIFICAR | Agregar `public static final class EvaluacionFichaPerfil` con constructor privado vacío, y dentro las constantes nuevas en el orden de las 5 secciones. |

**Inventario de constantes a agregar al catálogo en esta HU:**

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.EvaluacionFichaPerfil.CAMPO_REPRESENTANTE_COMITE_ID` | Campos | `String` | `"representanteComiteId"` | `DomainValidator.notNull` en `EvaluacionFichaPerfilAggregate` |
| `FichasMessages.EvaluacionFichaPerfil.CAMPO_FICHA_PERFIL_ID` | Campos | `String` | `"fichaPerfilId"` | `DomainValidator.notNull` en `EvaluacionFichaPerfilAggregate` |
| `FichasMessages.EvaluacionFichaPerfil.EVALUACION_DUPLICADA` | Códigos de error | `String` | `"EVALUACION_DUPLICADA"` | `EvaluacionFichaPerfilDuplicadaException` (errorCode) |
| `FichasMessages.EvaluacionFichaPerfil.EVALUACION_DUPLICADA_MSG` | Mensajes de error | `String` | `"El representante %s ya tiene una evaluación para la ficha %s"` | `EvaluacionFichaPerfilDuplicadaException` (mensaje, `.formatted(representanteId, fichaId)`) |
| `FichasMessages.EvaluacionFichaPerfil.REPRESENTANTE_REQUERIDO` | Códigos de error | `String` | `"REPRESENTANTE_REQUERIDO"` | `DomainValidator.notNull` en `EvaluacionFichaPerfilAggregate` |
| `FichasMessages.EvaluacionFichaPerfil.FICHA_REQUERIDA` | Códigos de error | `String` | `"FICHA_REQUERIDA"` | `DomainValidator.notNull` en `EvaluacionFichaPerfilAggregate` |
| `FichasMessages.EvaluacionFichaPerfil.LOG_REGISTRADA` | Logs | `String` | `"Evaluación de ficha de perfil registrada — id={}, representante={}, ficha={}"` | `log.info` en `RegistrarEvaluacionFichaPerfilUseCase` |
| `FichasMessages.RepresentanteComite.REPRESENTANTE_NO_ENCONTRADO` | Códigos de error | `String` | `"REPRESENTANTE_NO_ENCONTRADO"` | `RepresentanteComiteNoEncontradoException` (errorCode) |
| `FichasMessages.RepresentanteComite.REPRESENTANTE_NO_ENCONTRADO_MSG` | Mensajes de error | `String` | `"Representante del comité no encontrado: %s"` | `RepresentanteComiteNoEncontradoException` (mensaje, `.formatted(representanteId)`) |

> **Estructura de `FichasMessages.java` tras modificación:**
> ```java
> public static final class EvaluacionFichaPerfil {
>     private EvaluacionFichaPerfil() {}
>     // Campos
>     public static final String CAMPO_REPRESENTANTE_COMITE_ID = "representanteComiteId";
>     public static final String CAMPO_FICHA_PERFIL_ID = "fichaPerfilId";
>     // Códigos de error
>     public static final String EVALUACION_DUPLICADA = "EVALUACION_DUPLICADA";
>     public static final String REPRESENTANTE_REQUERIDO = "REPRESENTANTE_REQUERIDO";
>     public static final String FICHA_REQUERIDA = "FICHA_REQUERIDA";
>     // Mensajes de error
>     public static final String EVALUACION_DUPLICADA_MSG = "El representante %s ya tiene una evaluación para la ficha %s";
>     // Logs
>     public static final String LOG_REGISTRADA = "Evaluación de ficha de perfil registrada — id={}, representante={}, ficha={}";
> }
>
> public static final class RepresentanteComite {
>     private RepresentanteComite() {}
>     // Códigos de error
>     public static final String REPRESENTANTE_NO_ENCONTRADO = "REPRESENTANTE_NO_ENCONTRADO";
>     // Mensajes de error
>     public static final String REPRESENTANTE_NO_ENCONTRADO_MSG = "Representante del comité no encontrado: %s";
> }
> ```

### Archivos a MODIFICAR

**No aplica.** Esta HU crea funcionalidad completamente nueva. No hay archivos existentes que modificar.

---

## 7. Detalle por Archivo

### `EvaluacionFichaPerfilAggregate.java`
- **Paquete:** `com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate`
- **Tipo:** Aggregate Root (clase plana, NO extiende `AggregateRoot`)
- **Responsabilidad:** Encapsula las reglas de negocio del registro de evaluación. Valida que `representanteComiteId` y `fichaPerfilId` sean obligatorios. Autogenera `id` (UUID) y `fechaCreacion` (Instant) en `crear(...)`.
- **Features Java 21 aplicables:** `var` para variables locales evidentes
- **Métodos principales:**
    - `crear(UUID representanteComiteId, UUID fichaPerfilId): EvaluacionFichaPerfilAggregate` — Factory para entidad nueva. Valida con Notification Pattern, autogenera `id` y `fechaCreacion`. NO emite eventos.
    - `reconstruir(UUID id, UUID representanteComiteId, UUID fichaPerfilId, Instant fechaCreacion): EvaluacionFichaPerfilAggregate` — Factory para reconstruir desde persistencia. Constructor directo sin validar.
- **Dependencias:** `ValidationResult`, `DomainValidator`, `UtilUUID`, `UtilDate`, `FichasMessages.EvaluacionFichaPerfil`

### `EvaluacionFichaPerfilOutputPort.java`
- **Paquete:** `com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out`
- **Tipo:** Interface (puerto de salida write)
- **Responsabilidad:** Define el contrato para persistir y consultar duplicados del aggregate `EvaluacionFichaPerfilAggregate`
- **Métodos principales:**
    - `void guardar(EvaluacionFichaPerfilAggregate evaluacion)` — Persiste el aggregate
    - `boolean existsById(UUID id)` — Valida si existe evaluación por ID
    - `boolean existsByRepresentanteAndFicha(UUID representanteComiteId, UUID fichaPerfilId)` — Valida unicidad representante+ficha
- **Dependencias:** `EvaluacionFichaPerfilAggregate`, `UUID`

### `RegistrarEvaluacionFichaPerfilCommand.java`
- **Paquete:** `com.arquisoft.fichas.application.evaluacionfichaperfil.command.model`
- **Tipo:** `record` (Command — intención de negocio)
- **Responsabilidad:** Encapsula los datos necesarios para registrar una evaluación
- **Features Java 21 aplicables:** `record` para inmutabilidad + `equals`/`hashCode` gratis
- **Campos:**
    - `UUID fichaPerfilId` — ID de la ficha a evaluar
    - `UUID representanteComiteId` — ID del representante que evalúa (extraído del JWT)
- **Dependencias:** `UUID`

### `RegistrarEvaluacionFichaPerfilInputPort.java`
- **Paquete:** `com.arquisoft.fichas.application.evaluacionfichaperfil.command.port.in`
- **Tipo:** Interface (puerto de entrada write — vacío)
- **Responsabilidad:** Contrato del use case de registro
- **Métodos principales:**
    - Hereda `UUID ejecutar(RegistrarEvaluacionFichaPerfilCommand)` de `InputPort<RegistrarEvaluacionFichaPerfilCommand, UUID>`
- **Dependencias:** `InputPort` (de `shared:domain`), `RegistrarEvaluacionFichaPerfilCommand`, `UUID`

### `EvaluacionFichaPerfilDuplicadaException.java`
- **Paquete:** `com.arquisoft.fichas.application.evaluacionfichaperfil.exception`
- **Tipo:** Exception (extiende `ApplicationException` → 400 Bad Request)
- **Responsabilidad:** Se lanza cuando un representante intenta registrar segunda evaluación para la misma ficha
- **Constructor:**
    - `(UUID representanteId, UUID fichaId)` — Mensaje parametrizado con `FichasMessages.EvaluacionFichaPerfil.EVALUACION_DUPLICADA_MSG.formatted(representanteId, fichaId)`, errorCode = `FichasMessages.EvaluacionFichaPerfil.EVALUACION_DUPLICADA`
- **Dependencias:** `ApplicationException`, `FichasMessages.EvaluacionFichaPerfil`, `UUID`

### `RepresentanteComiteNoEncontradoException.java`
- **Paquete:** `com.arquisoft.fichas.application.evaluacionfichaperfil.exception`
- **Tipo:** Exception (extiende `ApplicationException` → 400 Bad Request)
- **Responsabilidad:** Se lanza cuando el representante autenticado no existe en la réplica local
- **Constructor:**
    - `(UUID representanteId)` — Mensaje parametrizado con `FichasMessages.RepresentanteComite.REPRESENTANTE_NO_ENCONTRADO_MSG.formatted(representanteId)`, errorCode = `FichasMessages.RepresentanteComite.REPRESENTANTE_NO_ENCONTRADO`
- **Dependencias:** `ApplicationException`, `FichasMessages.RepresentanteComite`, `UUID`

### `RegistrarEvaluacionFichaPerfilUseCase.java`
- **Paquete:** `com.arquisoft.fichas.application.evaluacionfichaperfil.command`
- **Tipo:** UseCase (`@Component`)
- **Responsabilidad:** Orquesta el registro de evaluación: valida existencias, valida no duplicado, crea aggregate, persiste, loguea
- **Métodos principales:**
    - `UUID ejecutar(RegistrarEvaluacionFichaPerfilCommand command)` — Flujo completo. Anotado con `@Transactional(transactionManager = "fichasTransactionManager")`.
- **Dependencias:** `FichaPerfilOutputPort`, `RepresentanteComiteQueryOutputPort`, `EvaluacionFichaPerfilOutputPort`, `FichaPerfilNoEncontradaException`, `RepresentanteComiteNoEncontradoException`, `EvaluacionFichaPerfilDuplicadaException`, `EvaluacionFichaPerfilAggregate`, `FichasMessages.EvaluacionFichaPerfil`
- **Flujo:**
    1. Validar que ficha existe (`fichaPerfilOutputPort.existsById(fichaPerfilId)`)
    2. Validar que representante existe (`representanteComiteQueryOutputPort.existsById(representanteComiteId)`)
    3. Validar que no hay duplicado (`evaluacionFichaPerfilOutputPort.existsByRepresentanteAndFicha(...)`)
    4. Crear aggregate con `EvaluacionFichaPerfilAggregate.crear(...)`
    5. Guardar vía `evaluacionFichaPerfilOutputPort.guardar(...)`
    6. Loguear con `log.info(FichasMessages.EvaluacionFichaPerfil.LOG_REGISTRADA, ...)`
    7. Retornar UUID de la evaluación creada

### `RepresentanteComiteQueryOutputPort.java`
- **Paquete:** `com.arquisoft.fichas.application.representantecomite.query.port.out`
- **Tipo:** Interface (puerto de salida read — vive en `application`)
- **Responsabilidad:** Consultas de solo lectura sobre representantes del comité
- **Métodos principales:**
    - `boolean existsById(UUID id)` — Valida si existe representante por ID
- **Dependencias:** `UUID`

### `RegistrarEvaluacionFichaPerfilInputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.in.web`
- **Tipo:** `@RestController` (Adaptador REST de entrada)
- **Responsabilidad:** Expone endpoint `POST /fichas-perfil/{fichaId}/evaluaciones`. **Sin request body** — el `fichaPerfilId` se toma del path param y el `representanteComiteId` del JWT. Serializa `UUID` a JSON.
- **`@Tag`:** `name = "Evaluaciones de Ficha de Perfil"`, `description = "Gestión de evaluaciones de fichas de perfil por el comité de currículum"`
- **Endpoints documentados:**

| Método del Controller | `@Operation(summary)` | Códigos `@ApiResponse` | `@SecurityRequirement` |
|-----------------------|-----------------------|------------------------|------------------------|
| `registrarEvaluacion` | `"Registrar nueva evaluación de ficha de perfil"` | 201, 400, 401, 403 | `bearerAuth` |

- **Métodos principales:**
    - `ResponseEntity<RegistrarEvaluacionFichaPerfilResponseDTO> registrarEvaluacion(@PathVariable UUID fichaId, @AuthenticationPrincipal Jwt jwt)` — Extrae `representanteComiteId` con `UUID.fromString(jwt.getSubject())` (mismo patrón que `ModificarFichaPerfilInputAdapter` y `AgregarItemFichaPerfilInputAdapter`), construye `new RegistrarEvaluacionFichaPerfilCommand(fichaId, representanteComiteId)`, llama al `InputPort`, retorna `201 Created` con body `{"id": "<uuid>"}` vía `RegistrarEvaluacionFichaPerfilResponseDTO`. **SIN** header `Location`.
- **Dependencias:** `RegistrarEvaluacionFichaPerfilInputPort`, `RegistrarEvaluacionFichaPerfilCommand`, `RegistrarEvaluacionFichaPerfilResponseDTO`, `Jwt` + `@AuthenticationPrincipal`, `UUID`, `@PreAuthorize`, ADR-011 annotations

### `RegistrarEvaluacionFichaPerfilResponseDTO.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.in.web.dto`
- **Tipo:** `record` (Response DTO)
- **Responsabilidad:** Envuelve el UUID de la evaluación creada para responder `{"id": "<uuid>"}` — mismo patrón que `RegistrarFichaPerfilResponseDTO`
- **Campos:** `UUID id`
- **Dependencias:** `UUID`

### `EvaluacionFichaPerfilJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Mapeo ORM de `evaluacion_ficha_perfil`
- **Anotaciones:** `@Entity`, `@Table(name = "evaluacion_ficha_perfil")`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- **Campos:**
    - `@Id UUID id`
    - `@Column(nullable = false) UUID representanteComiteId`
    - `@Column(nullable = false) UUID fichaPerfilId`
    - `@Column(nullable = false) Instant fechaCreacion`
- **Dependencias:** JPA annotations, Lombok, `UUID`, `Instant`

### `EvaluacionFichaPerfilJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence`
- **Tipo:** `JpaRepository`
- **Responsabilidad:** Acceso a BD de evaluaciones
- **Métodos custom:**
    - `boolean existsByRepresentanteComiteIdAndFichaPerfilId(UUID representanteComiteId, UUID fichaPerfilId)` — Query method Spring Data para unicidad
- **Dependencias:** `JpaRepository`, `EvaluacionFichaPerfilJpaEntity`, `UUID`

### `EvaluacionFichaPerfilMapper.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence`
- **Tipo:** `@Component` (Mapper)
- **Responsabilidad:** Traduce `EvaluacionFichaPerfilAggregate` ↔ `EvaluacionFichaPerfilJpaEntity`
- **Métodos principales:**
    - `EvaluacionFichaPerfilJpaEntity toJpaEntity(EvaluacionFichaPerfilAggregate aggregate)` — Builder de JPA entity
    - `EvaluacionFichaPerfilAggregate toDomain(EvaluacionFichaPerfilJpaEntity entity)` — Usa `EvaluacionFichaPerfilAggregate.reconstruir(...)`
- **Dependencias:** `EvaluacionFichaPerfilAggregate`, `EvaluacionFichaPerfilJpaEntity`

### `EvaluacionFichaPerfilCommandOutputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.out.persistence`
- **Tipo:** Adapter (`@Component`)
- **Responsabilidad:** Implementa `EvaluacionFichaPerfilOutputPort` delegando en JPA repository
- **Métodos principales:**
    - `void guardar(EvaluacionFichaPerfilAggregate evaluacion)` — Convierte a JPA entity vía mapper, guarda
    - `boolean existsById(UUID id)` — Delega en `repository.existsById(id)`
    - `boolean existsByRepresentanteAndFicha(UUID representanteComiteId, UUID fichaPerfilId)` — Delega en `repository.existsByRepresentanteComiteIdAndFichaPerfilId(...)`
- **Dependencias:** `EvaluacionFichaPerfilOutputPort`, `EvaluacionFichaPerfilJpaRepository`, `EvaluacionFichaPerfilMapper`

### `RepresentanteComiteJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.representantecomite.persistence`
- **Tipo:** JPA Entity (réplica local)
- **Responsabilidad:** Mapeo ORM de `representante_comite_curriculum`
- **Anotaciones:** `@Entity`, `@Table(name = "representante_comite_curriculum")`, `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- **Campos:**
    - `@Id UUID id`
    - `@Column(nullable = false, length = 30) String identificador`
    - `@Column(nullable = false, length = 50) String nombre`
    - `@Column(nullable = false, length = 50) String email`
- **Dependencias:** JPA annotations, Lombok, `UUID`

### `RepresentanteComiteJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.representantecomite.persistence`
- **Tipo:** `JpaRepository`
- **Responsabilidad:** Acceso a BD de representantes (réplica local)
- **Métodos custom:** Ninguno — solo heredados de `JpaRepository`
- **Dependencias:** `JpaRepository`, `RepresentanteComiteJpaEntity`, `UUID`

### `RepresentanteComiteQueryOutputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.representantecomite.query.adapter.out.persistence`
- **Tipo:** Adapter (`@Component`)
- **Responsabilidad:** Implementa `RepresentanteComiteQueryOutputPort` delegando en JPA repository
- **Métodos principales:**
    - `boolean existsById(UUID id)` — Delega en `repository.existsById(id)`
- **Dependencias:** `RepresentanteComiteQueryOutputPort`, `RepresentanteComiteJpaRepository`

### `V1.4__crear_evaluacion_ficha_perfil.sql`
- **Archivo:** `V1.4__crear_evaluacion_ficha_perfil.sql` (en `fichas/infrastructure/src/main/resources/db/migration/fichas/`)
- **Base de datos:** `fichas_perfil` (BD del contexto `fichas`)
- **Sin schemas:** tablas sin prefijo
- **Sin FKs cruzadas entre BDs:** cada contexto es autónomo. La tabla `representante_comite_curriculum` es réplica local, no tiene FK al contexto `usuarios`.
- **Cambios:**
    - Crear tabla `representante_comite_curriculum` (réplica local): columnas `id`, `identificador`, `nombre`, `email`
    - Crear tabla `evaluacion_ficha_perfil`: columnas `id`, `representante_comite_id`, `ficha_perfil_id`, `fecha_creacion`
    - FK de `evaluacion_ficha_perfil.ficha_perfil_id` → `ficha_perfil.id` (ON DELETE CASCADE)
    - FK de `evaluacion_ficha_perfil.representante_comite_id` → `representante_comite_curriculum.id`
    - UNIQUE constraint: `(representante_comite_id, ficha_perfil_id)` — unicidad por par representante+ficha
- **NO se crean en esta migración:** tablas `estado_evaluacion`, `estado_evaluacion_ficha`, `observacion_evaluacion` (HUs posteriores)

---

## 8. Endpoints REST

### Estado del endpoint

- [x] **Endpoint NUEVO** — crear `RegistrarEvaluacionFichaPerfilInputAdapter.java` desde cero.

### Contrato del endpoint

| Método | Ruta | Request Body / Params | Response | Código HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|----------------------|----------|-------------|----------------------|-------------------------------|
| POST | `/fichas-perfil/{fichaId}/evaluaciones` | **Sin body.** Path param: `fichaId` (UUID de la ficha a evaluar); `representanteComiteId` se extrae del JWT | `RegistrarEvaluacionFichaPerfilResponseDTO` — body `{"id": "<uuid>"}`. **SIN** header `Location` | 201 | `fichas:evaluacion-ficha-perfil:create` | `@Operation(summary="Registrar nueva evaluación de ficha de perfil")` + `@ApiResponses(201, 400, 401, 403)` + `@SecurityRequirement(name="bearerAuth")` |

**Sin request body:** el `fichaPerfilId` se toma exclusivamente del path parameter `{fichaId}`. No existe `RequestDTO` para este endpoint — el adapter construye el `Command` directamente.

---

## 9. Seguridad y Autorización (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Roles realm que lo poseen | Endpoint(s) que lo requieren | Descripción funcional |
|---|---|---|---|
| `fichas:evaluacion-ficha-perfil:create` | `representante-comite` | `POST /fichas-perfil/{fichaId}/evaluaciones` | Permite al representante del comité de currículum registrar una nueva evaluación sobre una ficha de perfil existente |

### Reglas de uso

1. **Formato del client role:** `fichas:evaluacion-ficha-perfil:create` — todo en minúscula, palabras del recurso separadas por guiones (`-`).
2. **Conversión de nombres:** la entidad `EvaluacionFichaPerfil` se convierte a `evaluacion-ficha-perfil` en el client role.
3. **Roles realm en kebab-case:** `representante-comite`.
4. **Un client role puede pertenecer a varios roles realm.** En este caso, solo `representante-comite` tiene acceso.
5. **Cada endpoint REST tiene exactamente un `@PreAuthorize("hasAuthority('...')")`** con un único client role.
6. **NO se usa `hasRole(...)`** ni roles realm directamente en endpoints — siempre `hasAuthority(...)` con client role.

### Configuración requerida en Keycloak (instrucciones para equipo de seguridad)

Para el client role `fichas:evaluacion-ficha-perfil:create`:

1. En el cliente `arquisoft-api`: crear el client role con el nombre exacto `fichas:evaluacion-ficha-perfil:create`.
2. Asignar el client role al rol realm `representante-comite`.
3. Verificar que los usuarios de prueba con rol realm `representante-comite` reciban el client role en su JWT (`resource_access.arquisoft-api.roles`).

---

## 10. Eventos RabbitMQ (si aplica)

**Eventos: ninguno.**

**Razón:** Esta HU es CRUD interno sin consumidores conocidos ni casos de auditoría identificados. El use case NO inyecta `EventPublisher`. Futuras HUs del flujo de evaluación (gestión de estados, aprobación/rechazo) podrían introducir eventos cuando haya consumidores concretos.

---

## 11. Migración de Base de Datos

- **Archivo:** `V1.4__crear_evaluacion_ficha_perfil.sql` (en `fichas/infrastructure/src/main/resources/db/migration/fichas/`. Versión = `V1.4` — siguiente número tras `V1.3`)
- **Base de datos:** `fichas_perfil` (BD del contexto `fichas`)
- **Sin schemas:** las tablas se crean sin prefijo (ej. `CREATE TABLE evaluacion_ficha_perfil (...)`)
- **Sin FKs cruzadas entre BDs:** la tabla `representante_comite_curriculum` es réplica local, no tiene FK al contexto `usuarios`
- **Cambios:**
    - **Tabla `representante_comite_curriculum`:**
        - `id` UUID PRIMARY KEY
        - `identificador` VARCHAR(30) NOT NULL
        - `nombre` VARCHAR(50) NOT NULL
        - `email` VARCHAR(50) NOT NULL
    - **Tabla `evaluacion_ficha_perfil`:**
        - `id` UUID PRIMARY KEY
        - `representante_comite_id` UUID NOT NULL
        - `ficha_perfil_id` UUID NOT NULL
        - `fecha_creacion` TIMESTAMP NOT NULL
        - FK `representante_comite_id` → `representante_comite_curriculum(id)`
        - FK `ficha_perfil_id` → `ficha_perfil(id)` ON DELETE CASCADE
        - UNIQUE constraint: `(representante_comite_id, ficha_perfil_id)` — unicidad por par representante+ficha
- **NO se crean en esta migración:** tablas `estado_evaluacion`, `estado_evaluacion_ficha`, `observacion_evaluacion` (HUs posteriores)

---

## 12. Casos de Prueba Sugeridos

### Presupuesto orientativo

| Tipo de HU | Tests esperados |
|---|---|
| Pequeña (1 endpoint, 1 entidad) | 15 - 25 |

**Esta HU es pequeña:** 1 endpoint POST, 1 entidad raíz (`EvaluacionFichaPerfilAggregate`), 1 réplica local (`RepresentanteComite`). Presupuesto: **17 - 22 tests**.

---

### Caso A — Use Case de ESCRITURA

#### Tests capa `domain` (Aggregate Root)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EvaluacionFichaPerfilAggregateTest` | `debeConstruirEntidad_cuandoDatosValidos` | `crear(...)` crea entidad con UUID no nulo, `representanteComiteId` y `fichaPerfilId` asignados, `fechaCreacion` no nulo |
| `EvaluacionFichaPerfilAggregateTest` | `debeReconstruirSinValidar_cuandoReconstruirEsInvocado` | `reconstruir(...)` acepta cualquier dato sin lanzar excepción |
| `EvaluacionFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoRepresentanteComiteIdEsNulo` | constructor lanza `DomainValidationException` si `representanteComiteId` es nulo |
| `EvaluacionFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoFichaPerfilIdEsNulo` | constructor lanza `DomainValidationException` si `fichaPerfilId` es nulo |

> **NO crear test propio de `EvaluacionFichaPerfilCreadaEvent`** porque esta HU NO emite eventos. No hay archivo de evento que testear.

#### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `RegistrarEvaluacionFichaPerfilUseCaseTest` | `debeRegistrar_cuandoDatosValidos` | flujo exitoso: ficha existe, representante existe, no hay duplicado, se crea aggregate, se guarda, se retorna UUID |
| `RegistrarEvaluacionFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoFichaNoExiste` | lanza `FichaPerfilNoEncontradaException` si `fichaPerfilOutputPort.existsById(...)` retorna false |
| `RegistrarEvaluacionFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoRepresentanteNoExiste` | lanza `RepresentanteComiteNoEncontradoException` si `representanteComiteQueryOutputPort.existsById(...)` retorna false |
| `RegistrarEvaluacionFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoEvaluacionDuplicada` | lanza `EvaluacionFichaPerfilDuplicadaException` si `evaluacionFichaPerfilOutputPort.existsByRepresentanteAndFicha(...)` retorna true |
| `RegistrarEvaluacionFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoRepositorioFalla` | propaga error de repositorio (ej. `DataAccessException`) |

> **NO se verifica `eventPublisher.publish(...)`** porque esta HU NO emite eventos. La entidad NO extiende `AggregateRoot`.

#### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EvaluacionFichaPerfilCommandOutputAdapterTest` | `debeGuardar_cuandoEntidadEsValida` | persistencia OK, mapper convierte aggregate → JPA entity, repository guarda |
| `EvaluacionFichaPerfilCommandOutputAdapterTest` | `debeRetornarTrue_cuandoExistsByRepresentanteAndFicha` | `existsByRepresentanteAndFicha(...)` retorna true si hay registro con ese par |
| `EvaluacionFichaPerfilCommandOutputAdapterTest` | `debeRetornarFalse_cuandoNoExistsByRepresentanteAndFicha` | `existsByRepresentanteAndFicha(...)` retorna false si no hay registro con ese par |
| `RegistrarEvaluacionFichaPerfilInputAdapterTest` | `debe201_cuandoPeticionValida` | created OK — extrae `representanteComiteId` del JWT, llama al use case, retorna 201 con body `{"id": "<uuid>"}` |
| `RegistrarEvaluacionFichaPerfilInputAdapterTest` | `debe400_cuandoFichaIdMalformado` | el `fichaId` del path parameter no es un UUID válido |
| `RegistrarEvaluacionFichaPerfilInputAdapterTest` | `debe401_cuandoNoAutenticado` | sin token JWT |
| `RegistrarEvaluacionFichaPerfilInputAdapterTest` | `debe403_cuandoRolInsuficiente` | autenticado pero sin authority `fichas:evaluacion-ficha-perfil:create` |
| `RepresentanteComiteQueryOutputAdapterTest` | `debeRetornarTrue_cuandoExistsById` | `existsById(...)` retorna true si existe representante |
| `RepresentanteComiteQueryOutputAdapterTest` | `debeRetornarFalse_cuandoNoExistsById` | `existsById(...)` retorna false si no existe representante |

**Total estimado:** 4 (domain) + 5 (application) + 8 (infrastructure) = **17 tests**.

---

### Reglas de consolidación

- **Si dos tests tienen el mismo "Act" pero distintos asserts, consolídalos** en un solo test con múltiples asserts.
- **NO incluyas tests de getters/setters** generados por Lombok en JPA entities o DTOs.
- **NO incluyas tests de validaciones Jakarta** una por una — un solo test "rechaza request inválido" basta.
- **NO incluyas tests de métodos `private`** — se validan implícitamente desde los métodos públicos que los usan.
- **NO incluyas test propio de excepción** si la excepción solo hace `super("CODE", "msg")`.

---

## 13. Checklist de Implementación

- [ ] **DDD:** Entidad de dominio `EvaluacionFichaPerfilAggregate` NO extiende `AggregateRoot` (la HU no emite eventos)
- [ ] Entidad inmutable: constructor privado, campos `final`, factory methods `crear` / `reconstruir`, sin Lombok en domain
- [ ] **NO hay eventos de dominio** — no se crean archivos en `domain/evaluacionfichaperfil/event/`, factory `crear(...)` NO llama `publishEvent(...)`
- [ ] IDs siempre `UUID` (nunca `Long` / `Integer`)
- [ ] Puerto de entrada (`RegistrarEvaluacionFichaPerfilInputPort`) definido, extiende `InputPort<Command, UUID>`
- [ ] Puerto de salida write (`EvaluacionFichaPerfilOutputPort`) definido en `domain/evaluacionfichaperfil/port/out/`
- [ ] Puerto de salida read (`RepresentanteComiteQueryOutputPort`) definido en `application/representantecomite/query/port/out/`
- [ ] Excepciones de application definidas en `application/evaluacionfichaperfil/exception/` (sin anidar en `command/`), extienden `ApplicationException` para que `GlobalAppExceptionHandler` de `shared:web` resuelva su HTTP automáticamente (400). **NO se crea handler de contexto.**
- [ ] `Command` (`record` en `application/evaluacionfichaperfil/command/model/`) creado. **NO se crea `RequestDTO`** — el endpoint no recibe body; el adapter construye el `Command` con el `fichaId` del path y el `representanteComiteId` del JWT. Campos en español idénticos al aggregate.
- [ ] `ResponseDTO` (`record RegistrarEvaluacionFichaPerfilResponseDTO(UUID id)`) creado — la respuesta 201 es `{"id": "<uuid>"}`, no un UUID crudo.
- [ ] Caso de uso (`RegistrarEvaluacionFichaPerfilUseCase`) con `@RequiredArgsConstructor`, `@Transactional(transactionManager = "fichasTransactionManager")` con qualifier explícito. **NO inyecta `EventPublisher`** (la HU no emite eventos).
- [ ] Controller REST **sin `@RequestBody`** (solo `@PathVariable UUID fichaId` + `@AuthenticationPrincipal Jwt jwt`), extrae `representanteComiteId` con `UUID.fromString(jwt.getSubject())`, y autorización vía `@PreAuthorize("hasAuthority('fichas:evaluacion-ficha-perfil:create')")` en kebab-case — client role declarado en sección 9 del plan.
- [ ] Controller documentado con `@Tag`, `@Operation`, `@ApiResponses(201, 400, 401, 403)` y `@SecurityRequirement(name = "bearerAuth")` (ADR-011)
- [ ] Entidad JPA con `@Table(name = "evaluacion_ficha_perfil")` (sin atributo `schema`) y adaptadores de repositorio creados
- [ ] Migración Flyway `V1.4__crear_evaluacion_ficha_perfil.sql` en `db/migration/fichas/`, BD `fichas_perfil`, sin prefijo de schema en el SQL, UNIQUE constraint `(representante_comite_id, ficha_perfil_id)`
- [ ] **NO hay eventos RabbitMQ** — no se crea config ni consumer AMQP
- [ ] Tests unitarios con patrón AAA (cobertura ≥ 75%), **SIN tests de ciclo de eventos del Aggregate Root** (no hay eventos), **SIN verificación de `eventPublisher.publish(...)`**
- [ ] Tests de repositorio con H2
- [ ] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4.x)
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot)
- [ ] Constantes de mensajes agregadas a `shared:message` en `FichasMessages.java` (nested classes `EvaluacionFichaPerfil` y `RepresentanteComite`)
- [ ] Commit: `feat(fichas): implementar HU-190 registrar evaluacion ficha perfil`

---

## 14. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha      | Notas |
|------------|---------------------|--------------|------------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-07-06 | Build -x test: sin errores |
| Tests      | @tester             | ✅ Completado | 2026-07-07 | 17 tests (4 domain + 5 application + 8 infrastructure) — Cobertura ≥75% CUMPLE |
| Validación | @validator-analyze  | ✅ Completado | 2026-07-07 | Score: 98/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-07-07 | /.workspace/validator/validator-HU-190.md |
| Commit     | @commit             | ✅ Completado | 2026-07-07 | Hash: bca3e8c |
