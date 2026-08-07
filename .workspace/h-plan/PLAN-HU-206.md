# PLAN: HU206 - Agregar Estado Ficha Perfil

> ## ⚠️ CORRECCIÓN POSTERIOR (2026-07-30) — LEER ANTES DE USAR ESTE PLAN COMO REFERENCIA
>
> **Este plan contiene valores de catálogo que NO existen y que no deben reutilizarse.**
>
> El plan enumera para `estado_ficha` dos valores inventados:
>
> - **`EN_REVISION` / "En Revision"** — nunca existió en el MER. Llegó a implementarse como constante del enum `EstadoFicha`, quedó sin fila en la tabla (la migración V1.2 sí se hizo bien) y por tanto era inalcanzable en producción. **Eliminada del código el 2026-07-30.**
> - **"Rechazada"** (criterio de aceptación #2) — no existe en el MER ni se implementó nunca. El estado equivalente real es `NO_APROBADA`.
>
> El catálogo autoritativo está en `mer/data/03_data_fichas_perfil.sql` del repositorio `arquisoft-uco/arquisoft-docs` y define **exactamente 5 estados**:
>
> | id | nombre |
> |---|---|
> | `EN_CONSTRUCCION` | En Construccion |
> | `DISPONIBLE_PARA_EVALUACION` | Disponible Para Evaluacion |
> | `APROBADA` | Aprobada |
> | `APROBADA_CON_OBSERVACIONES` | Aprobada Con Observaciones |
> | `NO_APROBADA` | No Aprobada |
>
> Las tablas de las secciones 2, 4 y 7 de este plan **no fueron editadas**: se conservan como registro histórico de lo que se planificó. No las copies. Valida siempre los catálogos contra `mer/data/*.sql`, nunca contra un plan previo.

## Metadata
- **ID Historia:** HU-206
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura (extensión de use case existente, sin endpoint propio)
- **¿Usa AggregateRoot?:** No — `FichaPerfilAggregate` no extiende `AggregateRoot` (CRUD interno sin eventos, clase plana con factories `crear`/`reconstruir`)
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-06-23 (revisado 2026-06-26 — ADR-012: PK semántica `VARCHAR(50)` en `estado_ficha`)
- **Rama sugerida:** `feature/HU-206-agregar-estado-ficha-perfil`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:**
    - FichaPerfilAggregate ya existe (creado en HU-208 "Registrar Ficha Perfil"). Solo se modifica.
    - Es use case de ESCRITURA, pero NO tiene endpoint propio. La lógica se integra dentro del use case existente `RegistrarFichaPerfilUseCase`.
    - Al registrar la ficha perfil, automáticamente se agrega el estado inicial "En Construccion".
    - NO emite eventos de dominio. Es CRUD interno sin publicación a RabbitMQ.
    - EstadoFicha es catálogo nativo (tabla propia en el schema `fichas_perfil`).

---

## 1. Resumen Funcional

Esta HU extiende el flujo de creación de una Ficha Perfil (HU-208) para que, tras persistir el aggregate `FichaPerfilAggregate`, el use case automáticamente registre un estado inicial "En Construccion" en la tabla `estado_ficha_perfil`. **No se crea endpoint nuevo** — la lógica se integra dentro del use case `RegistrarFichaPerfilUseCase` existente. El estado no es parte del aggregate — es un registro de trazabilidad que vive en su propia tabla y se persiste mediante un nuevo output port `EstadoFichaPerfilOutputPort`.

**No cubre:** cambio de estado manual, consulta de historial de estados, ni eventos de dominio.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | Al registrar una ficha perfil, el sistema automáticamente crea un registro en `estado_ficha_perfil` con estado "En Construccion" | Tabla `estado_ficha_perfil` tiene una fila con `ficha_perfil_id`, `estado_ficha_id` (del catálogo) y `fecha_actualizacion = UtilDate.generateNewInstantNow()` |
| 2 | El catálogo `estado_ficha` contiene las filas iniciales (datos de referencia) | ⚠️ ~~Tabla `estado_ficha` poblada con los estados: "En Construccion", "En Revision", "Aprobada", "Rechazada"~~ **INCORRECTO — ver corrección al inicio.** Los estados reales son: "En Construccion", "Disponible Para Evaluacion", "Aprobada", "Aprobada Con Observaciones", "No Aprobada" |
| 3 | La creación del estado inicial es transaccional con la creación de la ficha | Si falla la asignación del estado, se revierte la ficha completa |

---

## 3. Reglas de Negocio

- El estado inicial de toda ficha perfil es **"En Construccion"**.
- La fecha de actualización (`fechaActualizacion`) se genera automáticamente al momento del registro (`UtilDate.generateNewInstantNow()`).
- El catálogo `estado_ficha` es de solo lectura en runtime — se puebla vía Flyway durante la migración inicial.
- La tabla `estado_ficha_perfil` es una **tabla de trazabilidad** independiente del aggregate `FichaPerfilAggregate` — no forma parte del estado del aggregate.

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `FichaPerfilAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** No — la HU no emite eventos, es una clase plana con factories `crear`/`reconstruir`. Confirmado leyendo `fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/aggregate/FichaPerfilAggregate.java`: la clase NO extiende `AggregateRoot`.
- **ID:** `UUID`

> **Coherencia verificada:** el aggregate NO extiende `AggregateRoot`, la HU NO emite eventos (sección 4), el use case NO inyecta `EventPublisher` ni drena eventos. Consistencia completa.

### Atributos por objeto de dominio (extraídos del modelo enriquecido)

#### `EstadoFichaPerfil` (nueva entidad — tabla de trazabilidad)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí (`UUID.randomUUID()`) | Identifica el registro de trazabilidad |
| `fichaPerfilId` | `UUID` | — | Sí | No | No | FK a `ficha_perfil.id` |
| `estadoFicha` | `EstadoFicha` | — | Sí | No | No | Enum Java del domain. El adapter usa `estadoFicha.getId()` + `EntityManager.getReference()` para construir la FK `VARCHAR(50)` sin SELECT (ADR-012). |
| `fechaActualizacion` | `Instant` | — | Sí | No | Sí (`UtilDate.generateNewInstantNow()`) | Timestamp del registro |

**Combinaciones únicas (Restricciones):**
- `(fichaPerfilId, estadoFichaId, fechaActualizacion)` — comentada en el MER como irrelevante. **No se implementa en esta HU.**

#### `EstadoFicha` (enum Java en domain + tabla de catálogo en BD)

El estado se modela en dos capas:
- **Domain:** enum `EstadoFicha` con campos `id: String` (= constante del enum, e.g. `"EN_CONSTRUCCION"`) y `nombre: String` (legible, e.g. `"En Construccion"`). `getId()` expone la clave semántica que coincide con `estado_ficha.id` en BD; `getNombre()` expone el nombre legible.
- **Base de datos:** tabla `estado_ficha` con `id VARCHAR(50) PK` (valor = constante del enum, e.g. `EN_CONSTRUCCION`), `nombre VARCHAR(20) NOT NULL`, `descripcion VARCHAR(200) NOT NULL`. **NO se consulta en runtime para el comando** — el adapter usa `EntityManager.getReference(EstadoFichaJpaEntity.class, estadoFicha.getId())` para construir la FK sin SELECT (PK semántica, ADR-012).

| Constante enum | `getId()` (= `estado_ficha.id` en BD) | `getNombre()` (= `estado_ficha.nombre`) | Descripción |
|---|---|---|---|
| `EN_CONSTRUCCION` | `"EN_CONSTRUCCION"` | `"En Construccion"` | La ficha de perfil se encuentra en construcción o desarrollo |
| ⚠️ ~~`EN_REVISION`~~ | ~~`"EN_REVISION"`~~ | ~~`"En Revision"`~~ | **NO EXISTE — eliminado el 2026-07-30, ver corrección al inicio** |
| `DISPONIBLE_PARA_EVALUACION` | `"DISPONIBLE_PARA_EVALUACION"` | `"Disponible Para Evaluacion"` | **(referencial)** |
| `APROBADA` | `"APROBADA"` | `"Aprobada"` | **(referencial)** |
| `APROBADA_CON_OBSERVACIONES` | `"APROBADA_CON_OBSERVACIONES"` | `"Aprobada Con Observaciones"` | **(referencial)** |
| `NO_APROBADA` | `"NO_APROBADA"` | `"No Aprobada"` | **(referencial)** |

**Flujo de resolución (implementado — ADR-012):**
1. `var efp = EstadoFichaPerfilAggregate.crear(fichaPerfilId)` — el aggregate asigna internamente `this.estadoFicha = EstadoFicha.EN_CONSTRUCCION`
2. `estadoFichaPerfilOutputPort.guardar(efp)` — el adapter (`EstadoFichaPerfilCommandOutputAdapter`) llama a `entityManager.getReference(EstadoFichaJpaEntity.class, efp.getEstadoFicha().getId())` para obtener un proxy JPA sin SELECT. El mapper construye la entidad JPA usando el proxy en el campo `@ManyToOne`.

> **Nota de diseño (ADR-012):** la PK de `estado_ficha` es semántica (`VARCHAR(50)` = constante del enum). Esto elimina por completo el `findByNombre` del adapter. `EntityManager.getReference()` crea un proxy sin emitir SELECT, garantizando una FK válida. Si el catálogo no contiene la fila, `jpaRepository.save(...)` lanzará `ConstraintViolationException` en commit (garantía de Flyway, no regla de negocio).

### Traducción del modelo enriquecido a código

| Característica del modelo | Traducción en código |
|---|---|
| Obligatorio | `@NotNull` en DTO (si aplica) + `@Column(nullable=false)` en JPA + `NOT NULL` en Flyway + validación en constructor del domain object |
| No modificable | NO se genera setter ni método `cambiar{Atributo}()` |
| Autogenerado (UUID) | `UUID.randomUUID()` dentro de `crear(...)` |
| Autogenerado (Instant) | `UtilDate.generateNewInstantNow()` dentro de `crear(...)` |
| FK a catálogo | `entityManager.getReference(EstadoFichaJpaEntity.class, estadoFicha.getId())` en el adapter — sin SELECT, sin lookup previo. PK semántica `VARCHAR(50)` garantiza FK válida si el catálogo existe (Flyway). |

### Eventos de Dominio que emite

Eventos: ninguno.

Razón: CRUD interno sin consumidores conocidos ni casos de auditoría identificados. La HU solo extiende la lógica de registro para incluir un estado inicial de trazabilidad — no hay necesidad de publicar eventos de dominio.

Implicaciones:
- La entidad raíz `FichaPerfilAggregate` NO extiende `AggregateRoot` — es una clase plana con factories `crear`/`reconstruir` (confirmado en el código existente).
- El factory `crear(...)` NO acumula eventos (no existe `publicarEvento`).
- El use case `RegistrarFichaPerfilUseCase` NO inyecta `EventPublisher`, no hay drenado de eventos.
- No se crean archivos en `domain/fichaperfil/event/`.

---

## 5. Integraciones Externas (solo si la HU lo requiere)

Esta HU NO requiere integraciones externas más allá de PostgreSQL (ya gestionado por output ports). No se crea esta sección.

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadoficha/EstadoFicha.java` | `enum` | Enum Java con los 6 valores de estado, campo `id: String` (= constante del enum, e.g. `"EN_CONSTRUCCION"`) y `nombre: String` legible. `getId()` expone la clave semántica para `EntityManager.getReference()`; `getNombre()` para trazabilidad. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadofichaperfil/aggregate/EstadoFichaPerfilAggregate.java` | Aggregate (NO extiende `AggregateRoot`) | Factory `crear(UUID fichaPerfilId)` asigna `estadoFicha = EN_CONSTRUCCION` internamente. `getEstadoFicha().getId()` expone el id semántico para `EntityManager.getReference()` en el adapter. `reconstruir(...)` desde persistencia. |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadofichaperfil/port/out/EstadoFichaPerfilOutputPort.java` | Interface | Puerto de salida write. Método `guardar(EstadoFichaPerfilAggregate): void` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadoficha/aggregate/EstadoFichaAggregate.java` | Aggregate (catálogo — NO extiende `AggregateRoot`) | Factory `reconstruir(String id, String nombre, String descripcion)` únicamente. `id` es `String` (PK semántica ADR-012). Representa una fila del catálogo `estado_ficha`. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoficha/query/readmodel/EstadoFichaReadModel.java` | `record` | Proyección plana del catálogo: `String id`, `String nombre`, `String descripcion`. `id` es `String` (PK semántica ADR-012). |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoficha/query/port/out/EstadoFichaQueryOutputPort.java` | Interface | Puerto de salida read. Método `buscarPorNombre(String nombre): Optional<EstadoFichaReadModel>` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadofichaperfil/persistence/EstadoFichaPerfilJpaEntity.java` | JPA Entity | `@Table(name = "estado_ficha_perfil")`. Campo `estadoFicha: EstadoFichaJpaEntity` con `@ManyToOne(fetch=LAZY) @JoinColumn(name="estado_ficha_id", nullable=false)`. FK `VARCHAR(50)` resuelta vía `EntityManager.getReference()` (ADR-012). |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadofichaperfil/persistence/EstadoFichaPerfilJpaRepository.java` | `JpaRepository` | Repositorio JPA para `EstadoFichaPerfilJpaEntity` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadofichaperfil/persistence/EstadoFichaPerfilMapper.java` | `@Component` | Mapea `EstadoFichaPerfilAggregate` ↔ `EstadoFichaPerfilJpaEntity`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadofichaperfil/command/adapter/out/persistence/EstadoFichaPerfilCommandOutputAdapter.java` | Adapter | Implementa `EstadoFichaPerfilOutputPort`. Inyecta `EntityManager` (jakarta.persistence). Usa `entityManager.getReference(EstadoFichaJpaEntity.class, aggregate.getEstadoFicha().getId())` sin SELECT. `EstadoFichaJpaRepository` ya NO es dependencia. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoficha/persistence/EstadoFichaJpaEntity.java` | JPA Entity | `@Table(name = "estado_ficha")`. Campos: `String id` (`@Id @Column(nullable=false, length=50)` — valor = constante del enum, e.g. `"EN_CONSTRUCCION"`), `String nombre`, `String descripcion`. PK semántica ADR-012. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoficha/persistence/EstadoFichaJpaRepository.java` | `JpaRepository<EstadoFichaJpaEntity, String>` | Repositorio JPA para `EstadoFichaJpaEntity`. PK es `String` (ADR-012). Método: `Optional<EstadoFichaJpaEntity> findByNombre(String nombre)` (usado únicamente por `EstadoFichaQueryOutputAdapter`). |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoficha/persistence/EstadoFichaMapper.java` | `@Component` | Mapea `EstadoFichaJpaEntity` → `EstadoFichaReadModel`. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoficha/query/adapter/out/persistence/EstadoFichaQueryOutputAdapter.java` | Adapter | Implementa `EstadoFichaQueryOutputPort`. Método `buscarPorNombre(String)`. |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.2__crear_estado_ficha_y_estado_ficha_perfil.sql` | Flyway | Crea tabla `estado_ficha` (5 filas iniciales del MER) y tabla `estado_ficha_perfil` con FK a `estado_ficha.id` y FK a `ficha_perfil.id`. |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/command/RegistrarFichaPerfilUseCase.java` | **Inyectar:** solo `EstadoFichaPerfilOutputPort`. **Modificar:** tras `fichaPerfilOutputPort.guardar(ficha)`, crear `EstadoFichaPerfilAggregate.crear(ficha.getId())` y persistir vía `estadoFichaPerfilOutputPort.guardar(...)`. El adapter resuelve la FK `VARCHAR(50)` vía `EntityManager.getReference()` internamente. Todo dentro del mismo `@Transactional`. |
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | **Agregar:** nested class `EstadoFichaPerfil` con constantes de campos, errores y logs. La nested class `EstadoFicha` **ya no se necesita** — el adapter no hace lookup y no lanza excepción de dominio por estado no encontrado. |

### Catálogo de mensajes (`shared:message`) — constantes a agregar

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.EstadoFichaPerfil.CAMPO_ID` | Campos | `String` | `"id"` | `DomainValidator.notNull` en `EstadoFichaPerfilAggregate` |
| `FichasMessages.EstadoFichaPerfil.CAMPO_FICHA_PERFIL_ID` | Campos | `String` | `"fichaPerfilId"` | `DomainValidator.notNull` en `EstadoFichaPerfilAggregate` |
| `FichasMessages.EstadoFichaPerfil.CAMPO_ESTADO_FICHA_ID` | Campos | `String` | `"estadoFichaId"` | `DomainValidator.notNull` en `EstadoFichaPerfilAggregate` |
| `FichasMessages.EstadoFichaPerfil.CAMPO_FECHA_ACTUALIZACION` | Campos | `String` | `"fechaActualizacion"` | `DomainValidator.notNull` en `EstadoFichaPerfilAggregate` |
| `FichasMessages.EstadoFichaPerfil.ID_REQUERIDO` | Códigos de error | `String` | `"ESTADO_FICHA_PERFIL_ID_REQUERIDO"` | Constructor de `EstadoFichaPerfilAggregate` |
| `FichasMessages.EstadoFichaPerfil.FICHA_PERFIL_ID_REQUERIDO` | Códigos de error | `String` | `"ESTADO_FICHA_PERFIL_FICHA_PERFIL_ID_REQUERIDO"` | Constructor de `EstadoFichaPerfilAggregate` |
| `FichasMessages.EstadoFichaPerfil.ESTADO_FICHA_ID_REQUERIDO` | Códigos de error | `String` | `"ESTADO_FICHA_PERFIL_ESTADO_FICHA_ID_REQUERIDO"` | Constructor de `EstadoFichaPerfilAggregate` |
| `FichasMessages.EstadoFichaPerfil.FECHA_ACTUALIZACION_REQUERIDA` | Códigos de error | `String` | `"ESTADO_FICHA_PERFIL_FECHA_ACTUALIZACION_REQUERIDA"` | Constructor de `EstadoFichaPerfilAggregate` |
| `FichasMessages.EstadoFichaPerfil.LOG_CREADO` | Logs | `String` | `"Estado ficha perfil creado — id={}, fichaPerfilId={}, estadoFichaId={}"` | `log.info` en `RegistrarFichaPerfilUseCase` tras persistir |

> **Nota ADR-012:** las constantes `FichasMessages.EstadoFicha.ESTADO_NO_ENCONTRADO` y `NOMBRE_NO_ENCONTRADO_MENSAJE` **fueron eliminadas**. Con PK semántica el adapter ya no hace `findByNombre` y no lanza excepción de dominio. La nested class `EstadoFicha` en `FichasMessages` **no se crea**.

---

## 7. Detalle por Archivo

### `EstadoFicha.java`
- **Paquete:** `com.arquisoft.fichas.domain.estadoficha`
- **Tipo:** `enum`
- **Responsabilidad:** Enum Java que nombra semánticamente los estados del ciclo de vida de una ficha perfil. Expone `getId()` con la clave semántica que coincide con `estado_ficha.id` en BD (ADR-012) y `getNombre()` con el nombre legible.
- **Campos:** `private final String id` (= constante del enum, inicializado con `this.name()`), `private final String nombre`.
- **Métodos:** `getId(): String`, `getNombre(): String`
- **Valores:**

| Constante enum | `getId()` (= `estado_ficha.id`) | `getNombre()` (= `estado_ficha.nombre`) |
|---|---|---|
| `EN_CONSTRUCCION` | `"EN_CONSTRUCCION"` | `"En Construccion"` |
| ⚠️ ~~`EN_REVISION`~~ | ~~`"EN_REVISION"`~~ | ~~`"En Revision"`~~ **NO EXISTE — eliminado el 2026-07-30** |
| `DISPONIBLE_PARA_EVALUACION` | `"DISPONIBLE_PARA_EVALUACION"` | `"Disponible Para Evaluacion"` |
| `APROBADA` | `"APROBADA"` | `"Aprobada"` |
| `APROBADA_CON_OBSERVACIONES` | `"APROBADA_CON_OBSERVACIONES"` | `"Aprobada Con Observaciones"` |
| `NO_APROBADA` | `"NO_APROBADA"` | `"No Aprobada"` |

### `EstadoFichaPerfilAggregate.java`
- **Paquete:** `com.arquisoft.fichas.domain.estadofichaperfil.aggregate`
- **Tipo:** Aggregate (NO extiende `AggregateRoot` — clase plana con factories)
- **Responsabilidad:** Representa un registro de trazabilidad de estado. El aggregate almacena el enum `EstadoFicha` — la resolución de la FK `VARCHAR(50)` para la BD la realiza el adapter de infraestructura vía `EntityManager.getReference()` (ADR-012).
- **Campos:** `UUID id`, `UUID fichaPerfilId`, `EstadoFicha estadoFicha`, `Instant fechaActualizacion`
- **Métodos principales:**
    - `crear(UUID fichaPerfilId): EstadoFichaPerfilAggregate` — factory que genera UUID, asigna `this.estadoFicha = EstadoFicha.EN_CONSTRUCCION` y `this.fechaActualizacion = UtilDate.generateNewInstantNow()`. Valida con Notification Pattern.
    - `reconstruir(UUID id, UUID fichaPerfilId, EstadoFicha estadoFicha, Instant fechaActualizacion): EstadoFichaPerfilAggregate` — factory desde persistencia, sin validar.
    - Getters: `getId()`, `getFichaPerfilId()`, `getEstadoFicha()`, `getFechaActualizacion()`
- **Dependencias:** `EstadoFicha`, `ValidationResult`, `DomainValidator`, `FichasMessages.EstadoFichaPerfil`, `UtilDate`

### `EstadoFichaPerfilOutputPort.java`
- **Paquete:** `com.arquisoft.fichas.domain.estadofichaperfil.port.out`
- **Tipo:** Interface
- **Responsabilidad:** Puerto de salida write. Contrato de persistencia del aggregate de trazabilidad.
- **Métodos principales:**
    - `guardar(EstadoFichaPerfilAggregate): void` — persiste el estado de trazabilidad

### `EstadoFichaPerfilJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Mapeo de la tabla `estado_ficha_perfil` en PostgreSQL.
- **Anotaciones:** `@Entity`, `@Table(name = "estado_ficha_perfil")`, `@Id`, `@Column(nullable = false)` en campos escalares
- **Campos:** `UUID id`, `UUID fichaPerfilId`, `EstadoFichaJpaEntity estadoFicha` (`@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="estado_ficha_id", nullable=false)`), `Instant fechaActualizacion`
- **Nota (ADR-012):** `estadoFicha` apunta al catálogo con PK `VARCHAR(50)`. El adapter inyecta un proxy vía `entityManager.getReference(EstadoFichaJpaEntity.class, estadoFicha.getId())` sin emitir SELECT. El mapper `toDomain` reconstruye el enum vía `EstadoFicha.valueOf(entity.getEstadoFicha().getId())`.

### `EstadoFichaPerfilJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence`
- **Tipo:** `JpaRepository<EstadoFichaPerfilJpaEntity, UUID>`
- **Responsabilidad:** Repositorio Spring Data JPA para `EstadoFichaPerfilJpaEntity`.

### `EstadoFichaPerfilMapper.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence`
- **Tipo:** Clase utilitaria estática (sin `@Component`, sin inyección Spring)
- **Responsabilidad:** Conversión pura entre `EstadoFichaPerfilAggregate` ↔ `EstadoFichaPerfilJpaEntity`. NO realiza consultas a BD — recibe los datos ya resueltos como parámetros.
- **Métodos principales:**
    - `static toJpaEntity(EstadoFichaPerfilAggregate aggregate, EstadoFichaJpaEntity estadoFichaRef): EstadoFichaPerfilJpaEntity` — recibe el proxy JPA (`getReference`) y lo asigna al campo `@ManyToOne`. NO recibe UUID.
    - `static toDomain(EstadoFichaPerfilJpaEntity entity): EstadoFichaPerfilAggregate` — resuelve el enum vía `EstadoFicha.valueOf(entity.getEstadoFicha().getId())` y llama a `reconstruir(...)`. No requiere parámetro `nombre` adicional.
- **Dependencias:** ninguna (clase pura de mapeo)

### `EstadoFichaPerfilCommandOutputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadofichaperfil.command.adapter.out.persistence`
- **Tipo:** Adapter (`@Component`)
- **Responsabilidad:** Implementa `EstadoFichaPerfilOutputPort`. Usa `entityManager.getReference(EstadoFichaJpaEntity.class, aggregate.getEstadoFicha().getId())` para obtener un proxy sin SELECT (PK semántica ADR-012). Delega al mapper estático para construir la entidad JPA. Sin `findByNombre`, sin manejo de `Optional`.
- **Dependencias:** `EstadoFichaPerfilJpaRepository`, `EntityManager` (jakarta.persistence)
- **Nota:** `EstadoFichaJpaRepository` ya **NO** es dependencia de este adapter.

### `EstadoFichaJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadoficha.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Mapeo de la tabla `estado_ficha` (catálogo).
- **Campos:** `String id` (`@Id @Column(nullable=false, length=50)` — valor = constante del enum, e.g. `"EN_CONSTRUCCION"`), `String nombre`, `String descripcion`
- **Anotaciones:** `@Entity`, `@Table(name = "estado_ficha")`, `@Column(unique = true)` en `nombre`

### `EstadoFichaJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadoficha.persistence`
- **Tipo:** `JpaRepository<EstadoFichaJpaEntity, String>` — PK es `String` (ADR-012)
- **Responsabilidad:** Repositorio Spring Data JPA para el catálogo. Usado únicamente por `EstadoFichaQueryOutputAdapter` — el command adapter ya no lo necesita.
- **Métodos:** `Optional<EstadoFichaJpaEntity> findByNombre(String nombre)`

### `EstadoFichaMapper.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadoficha.persistence`
- **Tipo:** `@Component`
- **Responsabilidad:** Mapea `EstadoFichaJpaEntity` → `EstadoFichaReadModel`.
- **Métodos:** `toReadModel(EstadoFichaJpaEntity): EstadoFichaReadModel`

### `EstadoFichaQueryOutputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.out.persistence`
- **Tipo:** Adapter (`@Component`)
- **Responsabilidad:** Implementa `EstadoFichaQueryOutputPort`. Delega a `EstadoFichaJpaRepository.findByNombre(nombre)` y mapea resultado.
- **Dependencias:** `EstadoFichaJpaRepository`, `EstadoFichaMapper`

### `RegistrarFichaPerfilUseCase.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.application.fichaperfil.command`
- **Tipo:** UseCase (`@Component`)
- **Responsabilidad:** Orquesta el registro de una ficha perfil Y la asignación del estado inicial.
- **Cambio requerido:** Inyectar solo `EstadoFichaPerfilOutputPort`. Tras persistir `FichaPerfilAggregate`, el flujo es:
    1. `var efp = EstadoFichaPerfilAggregate.crear(ficha.getId())` — el aggregate asigna `estadoFicha = EN_CONSTRUCCION` internamente
    2. `estadoFichaPerfilOutputPort.guardar(efp)` — el adapter usa `entityManager.getReference(EstadoFichaJpaEntity.class, efp.getEstadoFicha().getId())` para obtener un proxy sin SELECT; el mapper construye la entidad JPA con el proxy en el campo `@ManyToOne`
    3. Loguear con `FichasMessages.EstadoFichaPerfil.LOG_CREADO`
- **`EstadoFichaQueryOutputPort` NO se inyecta en el use case** — la validación de existencia del catálogo es responsabilidad del adapter (garantía de infraestructura/Flyway, no regla de negocio).
- **Dependencias nuevas:** `EstadoFichaPerfilOutputPort`

### `V1.2__crear_estado_ficha_y_estado_ficha_perfil.sql`
- **Ubicación:** `fichas/infrastructure/src/main/resources/db/migration/fichas/`
- **Tipo:** Migración Flyway
- **Responsabilidad:** Crear tablas `estado_ficha` y `estado_ficha_perfil`, poblar `estado_ficha` con 4 filas iniciales.

---

## 8. Endpoints REST (si aplica)

### Estado del endpoint

- [x] **Endpoint EXISTENTE** — la HU NO crea endpoint nuevo. La lógica se integra dentro del use case `RegistrarFichaPerfilUseCase`, que es invocado por el endpoint `POST /api/fichas-perfil` ya existente (creado en HU-208).
    - **Archivo a modificar:** ninguno (no hay cambio en el `RegistrarFichaPerfilInputAdapter` — el contrato HTTP no cambia).
    - **Qué cambia:** el use case orquesta dos persistencias (ficha + estado inicial) en la misma transacción. El cliente no percibe diferencia — recibe el mismo `201 Created` con el UUID de la ficha.

### Contrato del endpoint

No aplica — esta HU no modifica el contrato HTTP del endpoint existente `POST /api/fichas-perfil`.

---

## 9. Seguridad y Autorización (Keycloak)

No aplica — esta HU no crea endpoints nuevos ni modifica la autorización del endpoint existente.

---

## 10. Eventos RabbitMQ (si aplica)

Eventos: ninguno.

Razón: CRUD interno sin consumidores conocidos ni casos de auditoría identificados. El use case NO inyecta `EventPublisher`.

---

## 11. Migración de Base de Datos (si aplica)

- **Archivo:** `V1.2__crear_estado_ficha_y_estado_ficha_perfil.sql` (siguiente número tras `V1.1__crear_estudiante_y_estudiante_ficha_perfil.sql`)
- **Base de datos:** `fichas_perfil` (BD del contexto `fichas` según tabla de mapeo del skill `arquisoft-context`)
- **Sin schemas:** las tablas se crean sin prefijo (ej. `CREATE TABLE estado_ficha (...)`)
- **Cambios:**
    - Crear tabla `estado_ficha` (catálogo — PK semántica `VARCHAR(50)` según ADR-012): `id VARCHAR(50) PRIMARY KEY` (valor = constante del enum), `nombre VARCHAR(20) NOT NULL`, `descripcion VARCHAR(200) NOT NULL`, `CONSTRAINT uk_estado_ficha_nombre UNIQUE (nombre)`
    - Poblar `estado_ficha` con **5 filas** (PK semántica: id = constante del enum):
        ```sql
        CREATE TABLE estado_ficha (
            id          VARCHAR(50)  PRIMARY KEY,
            nombre      VARCHAR(20)  NOT NULL,
            descripcion VARCHAR(200) NOT NULL,
            CONSTRAINT uk_estado_ficha_nombre UNIQUE (nombre)
        );

        INSERT INTO estado_ficha (id, nombre, descripcion) VALUES
        ('APROBADA',                   'Aprobada',                   'Se refiere a que la ficha de perfil paso por revisión del comite de curriculum y tuvo una calificación mayor de 3.0'),
        ('APROBADA_CON_OBSERVACIONES', 'Aprobada Con Observaciones', 'Se refiere a que la ficha de perfil paso por la revisión del comite del curriculum, pero debe ser revisado debido a que necesita una mejora'),
        ('NO_APROBADA',                'No Aprobada',                'Se refiere a que la ficha de perfil paso por la revisión del comite del curriculum, pero la ficha de perfil no obtuvo una calificación mayor a 3.0'),
        ('EN_CONSTRUCCION',            'En Construccion',            'Se refiere a que la ficha de perfil se encuentra en construcción o desarrollo.'),
        ('DISPONIBLE_PARA_EVALUACION', 'Disponible Para Evaluacion', 'Se refiere a que la ficha de perfil se encuentra disponible para ser evaluada por los representantes del comite de curriculum.');
        ```
    - Crear tabla `estado_ficha_perfil` (trazabilidad) — FK `VARCHAR(50)` según ADR-012:
        ```sql
        CREATE TABLE estado_ficha_perfil (
            id                  UUID        PRIMARY KEY,
            ficha_perfil_id     UUID        NOT NULL,
            estado_ficha_id     VARCHAR(50) NOT NULL,
            fecha_actualizacion TIMESTAMP   NOT NULL,
            CONSTRAINT fk_efp_traz_ficha  FOREIGN KEY (ficha_perfil_id) REFERENCES ficha_perfil(id) ON DELETE CASCADE,
            CONSTRAINT fk_efp_traz_estado FOREIGN KEY (estado_ficha_id) REFERENCES estado_ficha(id)
            -- CONSTRAINT uk_trazabilidad_ficha_estado UNIQUE (ficha_perfil_id, estado_ficha_id, fecha_actualizacion) -- irrelevante según MER
        );
        ```

---

## 12. Casos de Prueba Sugeridos (condicional según tipo de Use Case)

**Tipo de Use Case:** Escritura (extensión de use case existente).

### Presupuesto orientativo

| Tipo de HU | Tests esperados |
|---|---|
| HU pequeña (1 endpoint, 1 entidad) | 15 - 25 |

**Esta HU es pequeña:** extiende un use case existente, agrega dos aggregates nuevos (uno de trazabilidad, uno de catálogo), sin endpoint nuevo. Estimación: **18-22 tests**.

---

### Tests capa `domain` (Aggregate Root)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EstadoFichaPerfilAggregateTest` | `debeConstruirEntidad_cuandoDatosValidos` | `crear(fichaPerfilId, estadoFichaId, Instant)` crea entidad con UUID no nulo |
| `EstadoFichaPerfilAggregateTest` | `debeReconstruirSinValidar_cuandoReconstruirEsInvocado` | `reconstruir(...)` no lanza excepciones (clase plana sin `AggregateRoot`) |
| `EstadoFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoFichaPerfilIdEsNulo` | `crear(null, ...)` lanza excepción de dominio |
| `EstadoFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoEstadoFichaIdEsNulo` | `crear(..., null, ...)` lanza excepción de dominio |
| `EstadoFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoFechaActualizacionEsNula` | `crear(..., ..., null)` lanza excepción de dominio |
| `EstadoFichaAggregateTest` | `debeReconstruir_cuandoDatosValidos` | catálogo solo tiene `reconstruir(...)` — no lanza |

---

### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `RegistrarFichaPerfilUseCaseTest` | `debeCrearEstadoInicial_cuandoFichaEsRegistrada` | flujo exitoso: persiste ficha Y persiste estado; el adapter usa `getReference()` con `EstadoFicha.EN_CONSTRUCCION.getId()` — sin `buscarPorNombre` |
| `RegistrarFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoRepositorioEstadoFalla` | propaga error de `estadoFichaPerfilOutputPort.guardar(...)` |

---

### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EstadoFichaPerfilCommandOutputAdapterTest` | `debeGuardar_cuandoEntidadEsValida` | persistencia OK — mock de `EntityManager.getReference()` retorna proxy de `EstadoFichaJpaEntity` |
| `EstadoFichaPerfilCommandOutputAdapterTest` | `debeUsarGetReference_conIdSemantico_cuandoGuarda` | verifica que se llama `entityManager.getReference(EstadoFichaJpaEntity.class, "EN_CONSTRUCCION")` sin `findByNombre` |
| `EstadoFichaQueryOutputAdapterTest` | `debeBuscarPorNombre_cuandoEstadoExiste` | `buscarPorNombre("En Construccion")` retorna `Optional.of(...)` |
| `EstadoFichaQueryOutputAdapterTest` | `debeRetornarVacio_cuandoEstadoNoExiste` | `buscarPorNombre("NoExiste")` retorna `Optional.empty()` |
| `EstadoFichaPerfilJpaRepositoryTest` | `debeGuardar_cuandoEntidadJpaEsValida` | test de integración con H2 |
| `EstadoFichaJpaRepositoryTest` | `debeBuscarPorNombre_cuandoEstadoExiste` | test de integración con H2 |

---

### Reglas de consolidación

- **NO testear el endpoint existente** — HU-208 ya tiene tests del `RegistrarFichaPerfilInputAdapter`. Esta HU solo extiende el use case.
- **NO testear el enum `EstadoFicha` directamente** — los enums no requieren tests unitarios propios.
- **NO duplicar tests de getters/setters** generados por Lombok (JPA Entities).

---

## 13. Checklist de Implementación

- [ ] **Enum `EstadoFicha`** creado en `fichas/domain/.../estadoficha/EstadoFicha.java` con 6 valores, campo `id: String` (= constante del enum, `getId()`) que coincide con `estado_ficha.id` en BD, y `nombre: String` (`getNombre()`) que coincide con `estado_ficha.nombre` (ADR-012)
- [ ] **DDD:** `EstadoFichaPerfilAggregate` NO extiende `AggregateRoot` (clase plana con factories `crear`/`reconstruir`)
- [ ] Aggregate inmutable: constructor privado, campos `final`, sin Lombok
- [ ] `EstadoFichaPerfilAggregate` tiene campo `estadoFicha: EstadoFicha` (enum) — **NO** tiene `estadoFichaId: UUID`
- [ ] Factory `crear(UUID fichaPerfilId)` asigna `estadoFicha = EN_CONSTRUCCION` internamente
- [ ] `EstadoFichaPerfilJpaEntity` tiene `estadoFicha: EstadoFichaJpaEntity` con `@ManyToOne(fetch=LAZY) @JoinColumn(name="estado_ficha_id")` — la FK `VARCHAR(50)` se resuelve vía `EntityManager.getReference()` en el adapter (ADR-012)
- [ ] `EstadoFichaPerfilMapper` es clase utilitaria estática (sin `@Component`, sin JPA). `toJpaEntity(aggregate, estadoFichaRef)` recibe el proxy `EstadoFichaJpaEntity` ya resuelto vía `getReference()`. `toDomain(entity)` usa `EstadoFicha.valueOf(entity.getEstadoFicha().getId())` — sin parámetro `nombre`
- [ ] `EstadoFichaPerfilCommandOutputAdapter` inyecta `EntityManager` (jakarta.persistence) — **NO** inyecta `EstadoFichaJpaRepository`. Usa `entityManager.getReference(EstadoFichaJpaEntity.class, aggregate.getEstadoFicha().getId())` para obtener proxy sin SELECT
- [ ] **NO hay eventos de dominio** — no se crean archivos en `domain/estadofichaperfil/event/`
- [ ] IDs de aggregates de trazabilidad siempre `UUID` (nunca `Long` / `Integer`). Excepción permitida por ADR-012: catálogos con PK semántica usan `String` (`estado_ficha.id`)
- [ ] Puerto de salida write (`EstadoFichaPerfilOutputPort`) definido en `domain/estadofichaperfil/port/out/`
- [ ] Puerto de salida read (`EstadoFichaQueryOutputPort`) definido en `application/estadoficha/query/port/out/`
- [ ] `RegistrarFichaPerfilUseCase` inyecta `EstadoFichaPerfilOutputPort` únicamente — **NO** inyecta `EstadoFichaQueryOutputPort`
- [ ] Use case **NO hace lookup**: delega todo al adapter. No hay magic strings en el use case
- [ ] Use case usa `@Transactional(transactionManager = "fichasTransactionManager")` con qualifier explícito
- [ ] Use case **NO inyecta `EventPublisher`**, no hay drenado de eventos
- [ ] `EstadoFichaPerfilJpaEntity` tiene `estadoFicha: EstadoFichaJpaEntity` con `@ManyToOne(fetch=LAZY) @JoinColumn(name="estado_ficha_id", nullable=false)` — **no** `@Enumerated`, **no** campo `UUID estadoFichaId`
- [ ] Migración `V1.2__crear_estado_ficha_y_estado_ficha_perfil.sql`: crea `estado_ficha` (`id VARCHAR(50) PK` semántica, `nombre VARCHAR(20)`, 5 filas con id = constante del enum), crea `estado_ficha_perfil` con `estado_ficha_id VARCHAR(50) FK` a `estado_ficha.id` y FK a `ficha_perfil.id` (ADR-012)
- [ ] Constantes de mensajes en `FichasMessages.java`: solo nested class `EstadoFichaPerfil` (campos, errores, logs). La nested class `EstadoFicha` **no se crea** — el adapter ya no hace lookup ni lanza excepción de dominio
- [ ] Tests unitarios con patrón AAA (cobertura ≥ 75%)
- [ ] **NO testear el enum directamente** — no necesita tests propios
- [ ] **NO crear tests del controller** — HU-208 ya los tiene
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot)
- [ ] Commit: `feat(fichas): agregar estado inicial a ficha perfil`

---

## 14. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-06-26 | ADR-012 aplicado: PK semántica VARCHAR(50), EntityManager.getReference, @ManyToOne en EstadoFichaPerfilJpaEntity. Build -x test: sin errores |
| Tests      | @tester             | ✅ Completado | 2026-06-24 | 10 tests generados: 6 domain (ya existentes) + 4 infrastructure. Todos pasan. |
| Validación | @validator-analyze  | ✅ Completado | 2026-06-26 | Score: 99/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-06-26 | /.workspace/validator/validator-HU-206.md |
| Commit     | @commit             | ✅ Completado | 2026-06-24 | Hash: 0961be2 |
| Revisión   | usuario             | 🔄 Pendiente  | 2026-06-26 | Cambios ADR-012: PK semántica `VARCHAR(50)` en `estado_ficha`. Requiere reimplementación de `EstadoFichaJpaEntity`, `EstadoFichaPerfilJpaEntity`, `EstadoFichaPerfilCommandOutputAdapter`, migración Flyway y enum `EstadoFicha`. |
