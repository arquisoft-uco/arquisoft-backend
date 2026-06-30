# PLAN: HU-31 — Agregar ítem a ficha de perfil

## Metadata
- **ID Historia:** HU-31
- **Bounded Context:** `fichas`
- **Tipo de Use Case:** Escritura (nuevo endpoint, nuevo aggregate)
- **¿Usa AggregateRoot?:** NO — `ItemFichaPerfilAggregate` es una clase plana `final` sin eventos (CRUD interno sin consumidores conocidos).
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-06-27
- **Rama sugerida:** `feature/HU-31-agregar-item-ficha-perfil`
- **ADR aplicados:** ADR-011 (Swagger), ADR-012 (PK semántica VARCHAR en catálogos)
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:**
    - **ADR-012:** `tipo_item.id` es `VARCHAR(50)` cuyo valor coincide exactamente con `TipoItem.name()`. No se usan UUIDs en la tabla de catálogo.
    - Unicidad de `TipoItem` por `FichaPerfil` validada en el use case (POL-02); **sin** constraint `UNIQUE` en la migración (per DDL acordado).
    - Autoría: el estudiante solo puede agregar ítems a **su propia FichaPerfil** (POL-04).
    - `contenido` máximo: `VARCHAR(7000)` — textos académicos pueden ser extensos.
    - `tipo_item.descripcion`: `VARCHAR(500)` — las descripciones del seed superan 200 chars.
    - Tabla en BD: `item` (no `item_ficha_perfil`).

---

## 1. Resumen Funcional

Esta HU permite a un **Estudiante** agregar un ítem de contenido (objetivo general, objetivo específico, estado del arte, antecedentes, justificación o referencias) a **su propia ficha de perfil**. El sistema valida que el estudiante es el propietario de la ficha, que el tipo de ítem solicitado aún no existe en la ficha (unicidad por `TipoItem`), y que el tipo de ítem sea un valor válido del enum de dominio.

La HU crea un nuevo endpoint REST `POST /fichas-perfil/{fichaPerfilId}/items`, persiste el `ItemFichaPerfilAggregate` y retorna el UUID del ítem creado con `201 Created`.

**No cubre:** edición de ítems, eliminación de ítems, consulta de ítems, cambio de propietario.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | Estudiante agrega un ítem válido a su propia ficha | Sistema retorna UUID del ítem con `201 Created` |
| 2 | Estudiante intenta agregar un tipo de ítem que ya existe en su ficha | Sistema rechaza con `400 Bad Request` + errorCode `ITEM_TIPO_DUPLICADO` |
| 3 | Estudiante intenta agregar un ítem a una ficha que no le pertenece | Sistema rechaza con `403 Forbidden` + errorCode `ITEM_FICHA_NO_AUTORIZADA` |
| 4 | Estudiante envía un `tipoItemCode` que no corresponde a ningún valor del enum | Sistema rechaza con `400 Bad Request` + errorCode `TIPO_ITEM_INVALIDO` |
| 5 | El `fichaPerfilId` del path no existe en el sistema | Sistema rechaza con `400 Bad Request` + errorCode `FICHA_NO_ENCONTRADA` |
| 6 | Usuario no autenticado intenta agregar un ítem | Sistema rechaza con `401 Unauthorized` |
| 7 | Usuario autenticado sin rol `estudiante` intenta agregar un ítem | Sistema rechaza con `403 Forbidden` |
| 8 | Request con datos inválidos (contenido vacío, `tipoItemCode` nulo) | Sistema rechaza con `422 Unprocessable Content` + `fieldErrors` detallando los campos |

---

## 3. Reglas de Negocio

- **POL-01:** Validar que los datos requeridos sean válidos (tipo de dato, longitud, obligatoriedad).
- **POL-02:** Una ficha perfil NO puede tener más de UN ítem del mismo `TipoItem` (unicidad de `(fichaPerfilId, tipoItemId)` a nivel de use case; sin constraint en BD per decisión de equipo).
- **POL-03:** El `tipoItemCode` enviado debe corresponder a un valor del enum `TipoItem`. Validado en el aggregate via `TipoItem.valueOf(tipoItemCode)` — **sin consulta a BD**. Si lanza `IllegalArgumentException`, se acumula en el `ValidationResult` con `addError` (no se lanza excepción dedicada).
- **POL-04:** El estudiante solo puede agregar ítems a **su propia** ficha (ownership via `userId` del JWT).

**Traducción a código:**
- POL-01 → validaciones Jakarta en `AgregarItemFichaPerfilRequestDTO` + Notification Pattern en `ItemFichaPerfilAggregate.crear(...)`.
- POL-02 → `ItemFichaPerfilOutputPort.existsPorFichaYTipoItem(fichaPerfilId, tipoItemCode)` en el use case antes de persistir.
- POL-03 → `TipoItem.valueOf(tipoItemCode)` dentro de `ItemFichaPerfilAggregate.crear(...)`. Si lanza `IllegalArgumentException`, se acumula en `ValidationResult` con `result.addError(...)` y se lanza `DomainValidationException` al final (400).
- POL-04 → `EstudianteFichaPerfilQueryOutputPort.existePorEstudianteYFicha(estudianteId, fichaPerfilId)` en el use case; `estudianteId` viene del JWT.

---

## 4. Modelo DDD del Contexto

### Aggregate Root

- **Entidad raíz:** `ItemFichaPerfilAggregate`
- **¿Extiende `AggregateRoot`?:** NO — sin eventos de dominio.
- **ID:** `UUID` (autogenerado con `UUID.randomUUID()` en `crear(...)`)

### Enum de Catálogo: `TipoItem` (ADR-012)

Sigue exactamente el patrón de `EstadoFicha`:

```java
// domain/tipoitem/TipoItem.java
public enum TipoItem {
    OBJETIVO_GENERAL("Objetivo General"),
    OBJETIVO_ESPECIFICO("Objetivo Especifico"),
    ESTADO_DEL_ARTE("Estado Del Arte"),
    ANTECEDENTES("Antecedentes"),
    JUSTIFICACION("Justificacion"),
    REFERENCIAS("Referencias");

    private final String id;
    private final String nombre;

    TipoItem(String nombre) {
        this.id = this.name();   // "OBJETIVO_GENERAL", etc. — coincide con PK en BD
        this.nombre = nombre;
    }

    public String getId()     { return id; }
    public String getNombre() { return nombre; }
}
```

**Regla ADR-012:** `TipoItem.OBJETIVO_GENERAL.getId()` → `"OBJETIVO_GENERAL"` = PK en tabla `tipo_item`. El enum es la única fuente de verdad para los valores válidos. No hay UUIDs hardcodeados.

### Atributos del Aggregate

| Atributo | Tipo Java | Obligatorio | Autogenerado | Notas |
|---|---|---|---|---|
| `id` | `UUID` | Sí | Sí | `UUID.randomUUID()` en `crear(...)` |
| `fichaPerfilId` | `UUID` | Sí | No | FK a `ficha_perfil.id` |
| `tipoItem` | `TipoItem` (enum) | Sí | No | Resuelto via `TipoItem.valueOf(tipoItemCode)` en `crear(...)` |
| `contenido` | `String` | Sí | No | Máx 7000 chars, trimmed |

### Validación de POL-03 en el Aggregate

```java
public static ItemFichaPerfilAggregate crear(UUID fichaPerfilId, String tipoItemCode, String contenido) {
    var result = new ValidationResult();
    DomainValidator.notNull(fichaPerfilId, "fichaPerfilId", "ITEM_FICHA_PERFIL_ID_REQUERIDO", result);
    DomainValidator.notBlank(tipoItemCode, "tipoItemCode", "ITEM_TIPO_ITEM_CODE_REQUERIDO", result);
    DomainValidator.notBlank(contenido, "contenido", "ITEM_CONTENIDO_REQUERIDO", result);
    if (contenido != null) {
        DomainValidator.maxLength(contenido.trim(), 7000, "contenido", "ITEM_CONTENIDO_DEMASIADO_LARGO", result);
    }
    result.throwIfHasErrors();

    TipoItem tipoItem = null;
    try {
        tipoItem = TipoItem.valueOf(tipoItemCode.trim());
    } catch (IllegalArgumentException e) {
        result.addError(
                FichasMessages.ItemFichaPerfil.CAMPO_TIPO_ITEM_CODE,
                FichasMessages.ItemFichaPerfil.TIPO_ITEM_INVALIDO,
                FichasMessages.ItemFichaPerfil.TIPO_ITEM_INVALIDO_MSG.formatted(tipoItemCode));
    }
    result.throwIfHasErrors();

    return new ItemFichaPerfilAggregate(UUID.randomUUID(), fichaPerfilId, tipoItem, contenido.trim());
}
```

### Validación de Ownership

`userId` del JWT → `EstudianteFichaPerfilQueryOutputPort.existePorEstudianteYFicha(userId, fichaPerfilId)`. El adapter de entrada pasa el `userId` como parte del command.

### Eventos de Dominio

**Ninguno.** CRUD interno sin consumidores conocidos. `ItemFichaPerfilAggregate` NO extiende `AggregateRoot`. El use case NO inyecta `EventPublisher`.

---

## 5. Integraciones Externas

Solo PostgreSQL. No hay Keycloak API, SMTP, MinIO ni RabbitMQ. La identidad del usuario llega desde el JWT ya validado.

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/tipoitem/TipoItem.java` | Enum (ADR-012) | Catálogo de tipos de ítem; `id = this.name()` — paquete propio `tipoitem/` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java` | Aggregate (clase plana) | Factory `crear(UUID, String tipoItemCode, String contenido)` + `reconstruir(...)` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/port/out/ItemFichaPerfilOutputPort.java` | Interface (puerto write) | `guardar(ItemFichaPerfilAggregate)`, `existsPorFichaYTipoItem(UUID, String)` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/AgregarItemFichaPerfilUseCase.java` | UseCase | Orquesta: ficha existe → ownership → unicidad tipo → crear aggregate → persistir → retornar UUID |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/port/in/AgregarItemFichaPerfilInputPort.java` | Interface (InputPort) | `UUID ejecutar(AgregarItemFichaPerfilCommand)` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/model/AgregarItemFichaPerfilCommand.java` | `record` (Command) | `UUID fichaPerfilId`, `String tipoItemCode`, `String contenido`, `UUID estudianteId` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/query/port/out/EstudianteFichaPerfilQueryOutputPort.java` | Interface (query port) | `boolean existePorEstudianteYFicha(UUID estudianteId, UUID fichaPerfilId)` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/exception/ItemTipoDuplicadoException.java` | Exception (→ 400) | POL-02: tipo ya existe en la ficha |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/exception/ItemFichaNoPropiaException.java` | Exception (→ 403) | POL-04: ficha no pertenece al estudiante |
| ~~application~~ | ~~`TipoItemInvalidoException.java`~~ | ~~Exception~~ | ~~Eliminada — POL-03 usa `ValidationResult.addError()` en el aggregate; resultado propagado como `DomainValidationException` (400)~~ |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/exception/FichaPerfilNoEncontradaException.java` | Exception (→ 400) | Ficha no encontrada por `fichaPerfilId` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/tipoitem/persistence/TipoItemJpaEntity.java` | JPA Entity | `@Id String id` (VARCHAR 50 = `TipoItem.name()`) — ADR-012 |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/tipoitem/persistence/TipoItemJpaRepository.java` | `JpaRepository<TipoItemJpaEntity, String>` | Sin métodos custom — solo necesaria para `entityManager.getReference()` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/persistence/ItemFichaPerfilJpaEntity.java` | JPA Entity | Tabla `item`; `@ManyToOne(LAZY) TipoItemJpaEntity tipoItem` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/persistence/ItemFichaPerfilJpaRepository.java` | `JpaRepository<ItemFichaPerfilJpaEntity, UUID>` | `existsByFichaPerfilIdAndTipoItemId(UUID, String)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/persistence/ItemFichaPerfilMapper.java` | Clase utilitaria estática | `toJpaEntity(aggregate, tipoItemRef)`, `toDomain(entity)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/out/persistence/ItemFichaPerfilCommandOutputAdapter.java` | Adapter (`@Component`) | Implementa `ItemFichaPerfilOutputPort`; usa `entityManager.getReference(TipoItemJpaEntity.class, aggregate.getTipoItem().getId())` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/AgregarItemFichaPerfilInputAdapter.java` | REST Controller | `POST /fichas-perfil/{fichaPerfilId}/items` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/dto/AgregarItemFichaPerfilRequestDTO.java` | DTO request | `tipoItemCode: String`, `contenido: String`; método `toCommand(UUID, UUID)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/dto/AgregarItemFichaPerfilResponseDTO.java` | DTO response | `UUID id` — body del `201 Created` |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.3__crear_tipo_item_e_item.sql` | Flyway | Crea `tipo_item` (VARCHAR PK) + `item`, pobla catálogo con 6 tipos |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/query/adapter/out/persistence/EstudianteFichaPerfilQueryOutputAdapter.java` | Adapter (`@Component`) | Implementa `EstudianteFichaPerfilQueryOutputPort` — reutiliza `EstudianteFichaPerfilJpaRepository` (HU-161) |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Agregar nested classes `ItemFichaPerfil` y constantes `FichaPerfil.FICHA_NO_ENCONTRADA` (ver sección de mensajes) |
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/port/out/FichaPerfilOutputPort.java` | Verificar que exista `boolean existsById(UUID id)` — agregar si no existe |

### Catálogo de mensajes (`shared:message`) — constantes a agregar

| Constante | Valor | Usado por |
|---|---|---|
| `FichasMessages.ItemFichaPerfil.CAMPO_FICHA_PERFIL_ID` | `"fichaPerfilId"` | `DomainValidator` en aggregate |
| `FichasMessages.ItemFichaPerfil.CAMPO_TIPO_ITEM_CODE` | `"tipoItemCode"` | `DomainValidator` en aggregate |
| `FichasMessages.ItemFichaPerfil.CAMPO_CONTENIDO` | `"contenido"` | `DomainValidator` en aggregate |
| `FichasMessages.ItemFichaPerfil.CONTENIDO_MAX` | `7000` | `DomainValidator.maxLength` en aggregate |
| `FichasMessages.ItemFichaPerfil.FICHA_PERFIL_ID_REQUERIDO` | `"ITEM_FICHA_PERFIL_ID_REQUERIDO"` | `DomainValidator` |
| `FichasMessages.ItemFichaPerfil.TIPO_ITEM_CODE_REQUERIDO` | `"ITEM_TIPO_ITEM_CODE_REQUERIDO"` | `DomainValidator` |
| `FichasMessages.ItemFichaPerfil.CONTENIDO_REQUERIDO` | `"ITEM_CONTENIDO_REQUERIDO"` | `DomainValidator` |
| `FichasMessages.ItemFichaPerfil.CONTENIDO_DEMASIADO_LARGO` | `"ITEM_CONTENIDO_DEMASIADO_LARGO"` | `DomainValidator` |
| `FichasMessages.ItemFichaPerfil.ITEM_TIPO_DUPLICADO` | `"ITEM_TIPO_DUPLICADO"` | `ItemTipoDuplicadoException` |
| `FichasMessages.ItemFichaPerfil.ITEM_FICHA_NO_AUTORIZADA` | `"ITEM_FICHA_NO_AUTORIZADA"` | `ItemFichaNoPropiaException` |
| `FichasMessages.ItemFichaPerfil.TIPO_ITEM_INVALIDO` | `"TIPO_ITEM_INVALIDO"` | `TipoItemInvalidoException` |
| `FichasMessages.ItemFichaPerfil.TIPO_ITEM_INVALIDO_MSG` | `"El tipo de ítem '%s' no es válido"` | `TipoItemInvalidoException` |
| `FichasMessages.ItemFichaPerfil.TIPO_ITEM_DUPLICADO_MSG` | `"La ficha ya tiene un ítem del tipo: %s"` | `ItemTipoDuplicadoException` |
| `FichasMessages.ItemFichaPerfil.FICHA_NO_AUTORIZADA_MSG` | `"El estudiante no es propietario de la ficha: %s"` | `ItemFichaNoPropiaException` |
| `FichasMessages.ItemFichaPerfil.LOG_AGREGADO` | `"Ítem agregado — id={}, fichaPerfilId={}, tipoItem={}"` | `log.info` en use case |
| `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA` | `"FICHA_NO_ENCONTRADA"` | `FichaPerfilNoEncontradaException` |
| `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA_MSG` | `"No se encontró la ficha de perfil con id: %s"` | `FichaPerfilNoEncontradaException` |

---

## 7. Detalle por Archivo

### `TipoItem.java` (NUEVO — domain/tipoitem/)
- **Patrón:** idéntico a `EstadoFicha.java` (referencia canónica).
- Ubicado en su propio paquete `com.arquisoft.fichas.domain.tipoitem` (no bajo `itemfichaperfil/model/`).
- `id = this.name()` → coincide exactamente con la PK `VARCHAR(50)` en tabla `tipo_item`.
- Valores: `OBJETIVO_GENERAL`, `OBJETIVO_ESPECIFICO`, `ESTADO_DEL_ARTE`, `ANTECEDENTES`, `JUSTIFICACION`, `REFERENCIAS`.
- Métodos públicos: `getId()`, `getNombre()`. Sin `fromCode()` — usar `TipoItem.valueOf(code)` de Java nativo.

### `ItemFichaPerfilAggregate.java` (NUEVO)
- **Clase:** `public final class ItemFichaPerfilAggregate`
- **Campos:** `UUID id`, `UUID fichaPerfilId`, `TipoItem tipoItem`, `String contenido` — todos `private final`.
- **`crear(UUID fichaPerfilId, String tipoItemCode, String contenido)`:**
    1. Notification Pattern con `DomainValidator` para `fichaPerfilId`, `tipoItemCode`, `contenido`.
    2. `result.throwIfHasErrors()` antes del `valueOf`.
    3. `TipoItem.valueOf(tipoItemCode.trim())` — si lanza `IllegalArgumentException`, acumular en `result` con `addError(CAMPO_TIPO_ITEM_CODE, TIPO_ITEM_INVALIDO, MSG)` y luego `result.throwIfHasErrors()` (resulta en `DomainValidationException`, 400).
    4. `contenido.trim()` antes de asignar.
    5. Retorna nueva instancia con `UUID.randomUUID()`.
- **`reconstruir(UUID id, UUID fichaPerfilId, TipoItem tipoItem, String contenido)`:** reconstruye sin validar.
- **Sin Lombok, sin Spring, sin AggregateRoot.**

### `ItemFichaPerfilOutputPort.java` (NUEVO — domain/itemfichaperfil/port/out/)
- `void guardar(ItemFichaPerfilAggregate item)`
- `boolean existsPorFichaYTipoItem(UUID fichaPerfilId, String tipoItemCode)` — valida POL-02. El `tipoItemCode` es el `String` del enum.

### `AgregarItemFichaPerfilCommand.java` (NUEVO)
- `record` con: `UUID fichaPerfilId`, `String tipoItemCode`, `String contenido`, `UUID estudianteId`.
- `tipoItemCode` es el String que envía el cliente (ej. `"OBJETIVO_GENERAL"`).

### `AgregarItemFichaPerfilUseCase.java` (NUEVO)
- **`@Transactional(transactionManager = "fichasTransactionManager")`**
- **Flujo `ejecutar(AgregarItemFichaPerfilCommand cmd)`:**
    1. `fichaPerfilOutputPort.existsById(cmd.fichaPerfilId())` → si false, lanzar `FichaPerfilNoEncontradaException`.
    2. `estudianteFichaPort.existePorEstudianteYFicha(cmd.estudianteId(), cmd.fichaPerfilId())` → si false, lanzar `ItemFichaNoPropiaException` (POL-04).
    3. `itemPort.existsPorFichaYTipoItem(cmd.fichaPerfilId(), cmd.tipoItemCode())` → si true, lanzar `ItemTipoDuplicadoException` (POL-02).
    4. `ItemFichaPerfilAggregate.crear(cmd.fichaPerfilId(), cmd.tipoItemCode(), cmd.contenido())` — POL-01 + POL-03 resueltos aquí.
    5. `itemPort.guardar(item)`.
    6. `log.info(FichasMessages.ItemFichaPerfil.LOG_AGREGADO, item.getId(), item.getFichaPerfilId(), item.getTipoItem())`.
    7. `return item.getId()`.
- **NO inyecta `EventPublisher`.**

### Excepciones de aplicación (NUEVAS)

| Clase | Extiende | Constructor |
|---|---|---|
| `ItemTipoDuplicadoException` | `ApplicationException` (→ 400) | `(String tipoItemCode)` → `super(MSG.formatted(tipoItemCode), ITEM_TIPO_DUPLICADO)` |
| `ItemFichaNoPropiaException` | `ForbiddenException` (→ 403) | `(UUID fichaPerfilId)` → verificar clase base en `GlobalExceptionHandler` |
| ~~`TipoItemInvalidoException`~~ | ~~eliminada~~ | ~~POL-03 usa `result.addError()` en el aggregate → `DomainValidationException`~~ |
| `FichaPerfilNoEncontradaException` | `ApplicationException` (→ 400) | `(UUID fichaPerfilId)` |

### `TipoItemJpaEntity.java` (NUEVO — infrastructure/tipoitem/persistence/)
- **Patrón:** idéntico a `EstadoFichaJpaEntity.java` (referencia canónica).
```java
@Entity @Table(name = "tipo_item")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoItemJpaEntity {
    @Id
    @Column(nullable = false, length = 50)
    private String id;              // valor = TipoItem.name() — ADR-012

    @Column(nullable = false, unique = true, length = 20)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;
}
```

### `TipoItemJpaRepository.java` (NUEVO)
- `JpaRepository<TipoItemJpaEntity, String>` — PK es `String`, no UUID.
- Sin métodos custom — solo necesaria para que `entityManager.getReference()` pueda crear el proxy.

### `ItemFichaPerfilJpaEntity.java` (NUEVO — infrastructure/itemfichaperfil/persistence/)
- **Patrón:** idéntico a `EstadoFichaPerfilJpaEntity.java` (referencia canónica).
```java
@Entity @Table(name = "item")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemFichaPerfilJpaEntity {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "ficha_perfil_id", nullable = false, columnDefinition = "UUID")
    private UUID fichaPerfilId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_item_id", nullable = false)
    private TipoItemJpaEntity tipoItem;   // FK VARCHAR(50) → tipo_item.id

    @Column(name = "contenido", nullable = false, length = 7000)
    private String contenido;
}
```

### `ItemFichaPerfilJpaRepository.java` (NUEVO)
- `JpaRepository<ItemFichaPerfilJpaEntity, UUID>`
- Método custom: `boolean existsByFichaPerfilIdAndTipoItemId(UUID fichaPerfilId, String tipoItemId)`

### `ItemFichaPerfilMapper.java` (NUEVO)
- **Patrón:** idéntico a `EstadoFichaPerfilMapper.java` (referencia canónica).
```java
public final class ItemFichaPerfilMapper {
    private ItemFichaPerfilMapper() {}

    public static ItemFichaPerfilJpaEntity toJpaEntity(
            ItemFichaPerfilAggregate aggregate,
            TipoItemJpaEntity tipoItemRef) {        // proxy sin SELECT
        return ItemFichaPerfilJpaEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .tipoItem(tipoItemRef)
                .contenido(aggregate.getContenido())
                .build();
    }

    public static ItemFichaPerfilAggregate toDomain(ItemFichaPerfilJpaEntity entity) {
        TipoItem tipoItem = TipoItem.valueOf(entity.getTipoItem().getId()); // String → enum
        return ItemFichaPerfilAggregate.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                tipoItem,
                entity.getContenido()
        );
    }
}
```

### `ItemFichaPerfilCommandOutputAdapter.java` (NUEVO)
- **Patrón:** idéntico a `EstadoFichaPerfilCommandOutputAdapter.java` (referencia canónica).
```java
@Slf4j @Component @RequiredArgsConstructor
public class ItemFichaPerfilCommandOutputAdapter implements ItemFichaPerfilOutputPort {

    private final ItemFichaPerfilJpaRepository jpaRepository;

    @PersistenceContext(unitName = "fichas")
    private EntityManager entityManager;

    @Override
    public void guardar(ItemFichaPerfilAggregate aggregate) {
        var tipoItemRef = entityManager.getReference(
                TipoItemJpaEntity.class,
                aggregate.getTipoItem().getId()   // "OBJETIVO_GENERAL" — cero SELECT
        );
        var entity = ItemFichaPerfilMapper.toJpaEntity(aggregate, tipoItemRef);
        jpaRepository.save(entity);
    }

    @Override
    public boolean existsPorFichaYTipoItem(UUID fichaPerfilId, String tipoItemCode) {
        return jpaRepository.existsByFichaPerfilIdAndTipoItemId(fichaPerfilId, tipoItemCode);
    }
}
```

### `AgregarItemFichaPerfilRequestDTO.java` (NUEVO)
- `@Data @NoArgsConstructor @AllArgsConstructor @Builder`
- Campos:
    - `@NotBlank(message = "El tipo de ítem es obligatorio") String tipoItemCode` — ej. `"OBJETIVO_GENERAL"`
    - `@NotBlank @Size(max = 7000, message = "El contenido no puede superar 7000 caracteres") String contenido`
- Método: `AgregarItemFichaPerfilCommand toCommand(UUID fichaPerfilId, UUID estudianteId)`

### `AgregarItemFichaPerfilInputAdapter.java` (NUEVO)
- `@RestController @RequestMapping("/fichas-perfil") @RequiredArgsConstructor @Slf4j`
- `@PreAuthorize("hasAuthority('fichas:item-ficha-perfil:create')")`
- Extrae `userId` del JWT: `@AuthenticationPrincipal Jwt jwt` → `UUID.fromString(jwt.getSubject())`
- Endpoint: `POST /fichas-perfil/{fichaPerfilId}/items` → `201 Created` + UUID body
- `@Tag(name = "Fichas Perfil")` (reutilizar tag existente)

---

## 8. Endpoints REST

| Método | Ruta | Request Body | Path Params | Response | HTTP | Client role |
|--------|------|--------------|-------------|----------|------|-------------|
| POST | `/api/fichas-perfil/{fichaPerfilId}/items` | `{ tipoItemCode: String, contenido: String }` | `fichaPerfilId: UUID` | `{ "id": "uuid" }` | 201 | `fichas:item-ficha-perfil:create` |

### Response Body

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

Implementado con un DTO de respuesta:

```java
// infrastructure/itemfichaperfil/command/adapter/in/web/dto/AgregarItemFichaPerfilResponseDTO.java
@Data @AllArgsConstructor
public class AgregarItemFichaPerfilResponseDTO {
    private UUID id;
}
```

El adapter retorna:
```java
return ResponseEntity.status(HttpStatus.CREATED)
    .body(new AgregarItemFichaPerfilResponseDTO(inputPort.ejecutar(dto.toCommand(fichaPerfilId, estudianteId))));
```

### Anotaciones Swagger (ADR-011)

```java
@Operation(summary = "Agregar ítem a ficha de perfil")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Ítem agregado",
        content = @Content(schema = @Schema(implementation = AgregarItemFichaPerfilResponseDTO.class))),
    @ApiResponse(responseCode = "400", description = "Tipo inválido, tipo duplicado o ficha no encontrada"),
    @ApiResponse(responseCode = "401", description = "No autenticado"),
    @ApiResponse(responseCode = "403", description = "Sin permiso o ficha no propia"),
    @ApiResponse(responseCode = "422", description = "Datos inválidos — fieldErrors")
})
```

---

## 9. Seguridad y Autorización (Keycloak)

| Client role | Roles realm | Endpoint | Descripción |
|---|---|---|---|
| `fichas:item-ficha-perfil:create` | `estudiante` | `POST /api/fichas-perfil/{fichaPerfilId}/items` | Agregar ítems a la propia ficha de perfil |

---

## 10. Eventos RabbitMQ

**Ninguno.** El use case NO inyecta `EventPublisher`.

---

## 11. Migración de Base de Datos

- **Archivo:** `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.3__crear_tipo_item_e_item.sql`
- **Versión:** `V1.3` (secuencia: V1.0 fichas_perfil / V1.1 estudiante / V1.2 estado_ficha / V1.3 tipo_item + item)

```sql
-- =========================================================================
-- V1.3 — Crear tabla tipo_item (catálogo ADR-012) y tabla item
-- Bounded Context: fichas | HU-31: Agregar ítem a ficha de perfil
-- =========================================================================

-- ADR-012: PK semántica VARCHAR(50) — valor = TipoItem.name()
CREATE TABLE tipo_item (
    id          VARCHAR(50)  NOT NULL,
    nombre      VARCHAR(20)  NOT NULL,
    descripcion VARCHAR(500) NOT NULL,   -- aumentado: descripciones del seed superan 200 chars
    PRIMARY KEY (id),
    CONSTRAINT uk_tipo_item_nombre UNIQUE (nombre)
);

INSERT INTO tipo_item (id, nombre, descripcion) VALUES
    ('OBJETIVO_GENERAL',    'Objetivo General',    'Expresa de manera clara y concisa el propósito principal del proyecto. Debe describir qué se pretende lograr con la investigación o desarrollo y su impacto esperado.'),
    ('OBJETIVO_ESPECIFICO', 'Objetivo Especifico', 'Son metas concretas y detalladas que permiten alcanzar el objetivo general. Deben ser medibles, alcanzables y estar ordenados lógicamente, describiendo las acciones o pasos que se seguirán en el proyecto.'),
    ('ESTADO_DEL_ARTE',     'Estado Del Arte',     'Es una revisión de los estudios, tecnologías y desarrollos previos relacionados con el tema del proyecto. Permite identificar avances, tendencias y posibles vacíos que justifiquen la investigación.'),
    ('ANTECEDENTES',        'Antecedentes',        'Se refiere a estudios, investigaciones o proyectos previos que han abordado problemáticas similares. Ayudan a contextualizar el proyecto y a demostrar su relevancia y originalidad.'),
    ('JUSTIFICACION',       'Justificacion',       'Explica la importancia y pertinencia del proyecto. Se debe argumentar por qué es necesario llevarlo a cabo, a quién beneficiará y qué impacto puede tener en el área de estudio.'),
    ('REFERENCIAS',         'Referencias',         'Lista de fuentes bibliográficas, artículos científicos, libros, informes y otros documentos utilizados como base para la investigación. Se presentan en un formato de citación estandarizado (APA, IEEE, etc.).');

CREATE TABLE item (
    id              UUID         NOT NULL,
    tipo_item_id    VARCHAR(50)  NOT NULL,
    contenido       VARCHAR(7000) NOT NULL,  -- textos largos (objetivos, justificaciones, etc.)
    ficha_perfil_id UUID         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_tipo  FOREIGN KEY (tipo_item_id)    REFERENCES tipo_item(id),
    CONSTRAINT fk_item_ficha FOREIGN KEY (ficha_perfil_id) REFERENCES ficha_perfil(id) ON DELETE CASCADE
    -- CONSTRAINT uk_item_ficha_contenido UNIQUE (tipo_item_id, contenido, ficha_perfil_id)
    -- Revisado: combinación única no replicable en MER para este caso — POL-02 validada en capa de aplicación
);
```

---

## 12. Casos de Prueba Sugeridos

**Estimación:** ~28 tests (domain: 7, application: 9, infrastructure: 12).

### Tests `domain`

| Clase | Método | Escenario |
|---|---|---|
| `ItemFichaPerfilAggregateTest` | `debeConstruirItem_cuandoDatosValidos` | `crear(fichaId, "OBJETIVO_GENERAL", contenido)` → aggregate con UUID no nulo, tipoItem correcto, contenido trimmed |
| `ItemFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoFichaPerfilIdNulo` | `crear(null, code, contenido)` → `DomainValidationException` |
| `ItemFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoTipoItemCodeVacio` | `crear(fichaId, "", contenido)` → `DomainValidationException` |
| `ItemFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoContenidoVacio` | `crear(fichaId, code, "")` → `DomainValidationException` |
| `ItemFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoContenidoMuyLargo` | `crear(fichaId, code, "x".repeat(201))` → `DomainValidationException` |
| `ItemFichaPerfilAggregateTest` | `debeLanzarValidationException_cuandoTipoItemCodeInexistente` | `crear(fichaId, "INVALIDO", contenido)` → `DomainValidationException` con errorCode `TIPO_ITEM_INVALIDO` |
| `ItemFichaPerfilAggregateTest` | `debeAplicarTrim_cuandoContenidoTieneEspacios` | `crear(fichaId, code, "  texto  ")` → `getContenido() == "texto"` |

### Tests `application`

| Clase | Método | Escenario |
|---|---|---|
| `AgregarItemFichaPerfilUseCaseTest` | `debeAgregarItem_cuandoDatosValidos` | flujo exitoso → retorna UUID no nulo |
| `AgregarItemFichaPerfilUseCaseTest` | `debeLanzarFichaNoEncontrada_cuandoFichaNoExiste` | `existsById` → false → `FichaPerfilNoEncontradaException` |
| `AgregarItemFichaPerfilUseCaseTest` | `debeLanzarItemFichaNoPropia_cuandoEstudianteNoEsPropietario` | `existePorEstudianteYFicha` → false → `ItemFichaNoPropiaException` |
| `AgregarItemFichaPerfilUseCaseTest` | `debeLanzarItemTipoDuplicado_cuandoTipoYaExisteEnFicha` | `existsPorFichaYTipoItem` → true → `ItemTipoDuplicadoException` |
| `AgregarItemFichaPerfilUseCaseTest` | `debeGuardarItem_cuandoValidacionesExitosas` | `verify(itemPort).guardar(any())` — 1 invocación |
| `AgregarItemFichaPerfilUseCaseTest` | `debeValidarEnOrdenCorrecto` | `InOrder`: ficha → ownership → unicidad |
| `AgregarItemFichaPerfilUseCaseTest` | `debeRetornarUUID_cuandoExitoso` | UUID retornado == `item.getId()` |
| `AgregarItemFichaPerfilUseCaseTest` | `debePropagar_cuandoRepositorioFalla` | `guardar` lanza `RuntimeException` → se propaga |
| `AgregarItemFichaPerfilUseCaseTest` | `debeLoguear_cuandoExitoso` | `log.info` con constante `LOG_AGREGADO` invocado |

### Tests `infrastructure`

| Clase | Método | Escenario |
|---|---|---|
| `ItemFichaPerfilCommandOutputAdapterTest` | `debeGuardar_cuandoItemEsValido` | `guardar(aggregate)` → `jpaRepository.save(...)` con entity correcta |
| `ItemFichaPerfilCommandOutputAdapterTest` | `debeUsarGetReference_cuandoGuarda` | `entityManager.getReference(TipoItemJpaEntity.class, "OBJETIVO_GENERAL")` llamado — sin SELECT |
| `ItemFichaPerfilCommandOutputAdapterTest` | `debeRetornarTrue_cuandoParExiste` | `existsPorFichaYTipoItem` → `true` |
| `ItemFichaPerfilCommandOutputAdapterTest` | `debeRetornarFalse_cuandoParNoExiste` | `existsPorFichaYTipoItem` → `false` |
| `EstudianteFichaPerfilQueryOutputAdapterTest` | `debeRetornarTrue_cuandoPropietario` | `existePorEstudianteYFicha` → `true` |
| `EstudianteFichaPerfilQueryOutputAdapterTest` | `debeRetornarFalse_cuandoNoPropietario` | `existePorEstudianteYFicha` → `false` |
| `AgregarItemFichaPerfilInputAdapterTest` | `debe201_cuandoPeticionValida` | POST válido con JWT estudiante → `201 Created` + UUID |
| `AgregarItemFichaPerfilInputAdapterTest` | `debe422_cuandoRequestInvalido` | contenido vacío + tipoItemCode nulo → `422` + `fieldErrors[]` |
| `AgregarItemFichaPerfilInputAdapterTest` | `debe401_cuandoNoAutenticado` | sin token → `401` |
| `AgregarItemFichaPerfilInputAdapterTest` | `debe403_cuandoRolInsuficiente` | token coordinador → `403` |
| `AgregarItemFichaPerfilInputAdapterTest` | `debe400_cuandoTipoDuplicado` | `ItemTipoDuplicadoException` → `400` + `ITEM_TIPO_DUPLICADO` |
| `AgregarItemFichaPerfilInputAdapterTest` | `debe403_cuandoFichaNoPropia` | `ItemFichaNoPropiaException` → `403` + `ITEM_FICHA_NO_AUTORIZADA` |

---

## 13. Checklist de Implementación

- [ ] **ADR-012:** `TipoItem` enum con `id = this.name()` — sin UUIDs hardcodeados
- [ ] `TipoItem` enum en su propio paquete `com.arquisoft.fichas.domain.tipoitem` (no bajo `itemfichaperfil/model/`)
- [ ] `TipoItem` enum sigue el patrón exacto de `EstadoFicha` (referencia canónica)
- [ ] `ItemFichaPerfilAggregate` es clase plana `final`, sin `AggregateRoot`, sin Lombok, sin Spring
- [ ] Aggregate almacena `TipoItem tipoItem` (enum), no `UUID tipoItemId`
- [ ] Factory `crear(...)` aplica `.trim()` antes de validar `contenido`
- [ ] Factory `crear(...)` usa Notification Pattern — lanza `DomainValidationException` si hay errores de validación básica
- [ ] Factory `crear(...)` usa `TipoItem.valueOf(tipoItemCode.trim())` para POL-03; si falla acumula en `ValidationResult` con `addError(...)` y lanza `DomainValidationException` (400)
- [ ] `contenido` máximo: 7000 chars — `DomainValidator.maxLength(..., 7000, ...)` + `@Size(max=7000)` en DTO + `VARCHAR(7000)` en JPA + migración
- [ ] `TipoItemJpaEntity` con `@Id String id` (VARCHAR 50) — ADR-012, igual que `EstadoFichaJpaEntity`
- [ ] `TipoItemJpaRepository` es `JpaRepository<TipoItemJpaEntity, String>` (PK String)
- [ ] `ItemFichaPerfilJpaEntity` con `@ManyToOne(FetchType.LAZY) @JoinColumn TipoItemJpaEntity tipoItem` — igual que `EstadoFichaPerfilJpaEntity`
- [ ] `ItemFichaPerfilJpaRepository` con `existsByFichaPerfilIdAndTipoItemId(UUID, String)` (FK String)
- [ ] `ItemFichaPerfilCommandOutputAdapter` usa `entityManager.getReference(TipoItemJpaEntity.class, aggregate.getTipoItem().getId())` — cero SELECT
- [ ] `@PersistenceContext(unitName = "fichas")` en el adapter (igual que `EstadoFichaPerfilCommandOutputAdapter`)
- [ ] Mapper `toJpaEntity(aggregate, tipoItemRef)` recibe el proxy como parámetro (igual que `EstadoFichaPerfilMapper`)
- [ ] Mapper `toDomain(entity)` usa `TipoItem.valueOf(entity.getTipoItem().getId())`
- [ ] Use case con `@Transactional(transactionManager = "fichasTransactionManager")`
- [ ] Use case NO inyecta `EventPublisher`
- [ ] Flujo del use case en orden: ficha existe → ownership → unicidad tipo → crear aggregate → guardar → loguear → retornar UUID
- [ ] Controller REST con `@PreAuthorize("hasAuthority('fichas:item-ficha-perfil:create')")`
- [ ] Controller extrae `userId` via `@AuthenticationPrincipal Jwt jwt` → `UUID.fromString(jwt.getSubject())`
- [ ] DTO request con `tipoItemCode: String` (no UUID) — el cliente envía ej. `"OBJETIVO_GENERAL"`
- [ ] DTO response `AgregarItemFichaPerfilResponseDTO` con campo `UUID id` — body del `201 Created` es `{ "id": "uuid" }`
- [ ] Migración `V1.3__crear_tipo_item_e_item.sql`: tabla `tipo_item` (VARCHAR PK), 6 inserts, tabla `item` (sin unique constraint)
- [ ] **NO hay** `TipoItemQueryOutputPort` ni `TipoItemQueryOutputAdapter` — eliminados (POL-03 resuelto en dominio)
- [ ] `FichaPerfilOutputPort` tiene `boolean existsById(UUID id)` — agregar si no existe
- [ ] `EstudianteFichaPerfilQueryOutputAdapter` reutiliza `EstudianteFichaPerfilJpaRepository` de HU-161
- [ ] Tests siguen patrón AAA, naming `debeHacerAlgo_cuandoCondicion()`
- [ ] Cobertura ≥ 75%
- [ ] Commit: `feat(fichas): agregar ítem a ficha de perfil (HU-31)`

---

## 14. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-06-29 | Build -x test: sin errores. Agregado spring-boot-starter-oauth2-resource-server a fichas:infrastructure |
| Tests      | @tester             | ⏳ Pendiente  | — | — |
| Validación | @validator-analyze  | ✅ Completado | 2026-06-30 | Score: 98/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-06-30 | /.workspace/validator/validator-HU-31.md |
| Commit     | @commit             | ⏳ Pendiente  | — | — |
