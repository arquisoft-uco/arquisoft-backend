# PLAN: HU034 — Remover información de un Ítem

## Metadata
- **ID Historia:** HU-034
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura (eliminación física)
- **¿Usa AggregateRoot?:** No — la HU no emite eventos, es CRUD interno sin consumidores ni auditoría. La entidad `ItemFichaPerfilAggregate` ya existe como clase plana con factories `crear`/`reconstruir` y seguirá sin extender `AggregateRoot`.
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-07-15
- **Rama sugerida:** `feature/HU-034-remover-informacion-item`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** Eliminación física (DELETE en BD), sin emisión de eventos, respuesta HTTP 204 No Content, validar POL-05 con COUNT de revisiones.

---

## 1. Resumen Funcional

Como **Estudiante**, necesito eliminar físicamente un ítem de mi ficha de perfil cuando ya no es necesario, siempre que el ítem no haya sido revisado por un asesor. El sistema valida que soy propietario de la ficha, que el ítem existe, y que no tiene revisiones asociadas. Si todas las validaciones pasan, el ítem se elimina de la base de datos (DELETE físico) y el sistema responde con HTTP 204 No Content. Esta HU **NO** emite eventos de dominio — es un CRUD interno sin consumidores ni casos de auditoría identificados.

**Alcance:** eliminación de un único ítem por request.  
**NO cubre:** eliminación masiva de ítems, eliminación lógica, recuperación de ítems eliminados.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | Estudiante autenticado elimina un ítem de su ficha sin revisiones | HTTP 204 No Content, ítem eliminado de BD |
| 2 | Estudiante intenta eliminar un ítem que no existe | HTTP 400 Bad Request con `errorCode: "ITEM_NO_ENCONTRADO"` |
| 3 | Estudiante intenta eliminar un ítem de una ficha que no le pertenece | HTTP 403 Forbidden con `errorCode: "FICHA_NO_PROPIA"` |
| 4 | Estudiante intenta eliminar un ítem que tiene revisiones | HTTP 422 Unprocessable Entity con `fieldErrors[{field: "revisiones", errorCode: "ITEM_CON_REVISIONES"}]` |
| 5 | Usuario sin rol `estudiante` intenta eliminar un ítem | HTTP 403 Forbidden |
| 6 | Usuario no autenticado intenta eliminar un ítem | HTTP 401 Unauthorized |

---

## 3. Reglas de Negocio

| # | Regla | Dónde se valida | Estado que el use case lee y pasa | Excepción → HTTP |
|---|-------|-----------------|-----------------------------------|------------------|
| 1 | El ítem debe existir en BD antes de eliminarlo | `RemoverItemFichaPerfilUseCase` (orquestación — existencia) | `ItemFichaPerfilOutputPort.buscarPorId(itemId)` → `Optional.empty()` → rechazar | `ItemFichaPerfilNoEncontradoException` (extiende `ApplicationException`) → 400 |
| 2 | El estudiante debe ser propietario de la ficha a la que pertenece el ítem | `RemoverItemFichaPerfilUseCase` (orquestación — propiedad) | `FichaPerfilQueryOutputPort.esEstudiantePropietario(item.getFichaPerfilId(), estudianteId)` → `false` → rechazar | `FichaNoPropietarioException` (extiende `AuthorizationException`) → 403 |
| 3 | Item-POL-05: No se puede remover un ítem si existe una RevisionItem generada por AsesorFicha | `ItemFichaPerfilAggregate.removerse(totalRevisiones)` (invariante de dominio) | Use case obtiene `revisionQueryPort.contarPorItem(itemId)` y lo pasa al aggregate | `DomainValidationException` (lanzada por `ValidationResult.throwIfHasErrors()`) → 422 con `fieldErrors[]` |

**Orden de validaciones en el use case (crítico para códigos HTTP correctos):**
1. `var item = itemOutputPort.buscarPorId(command.itemId()).orElseThrow(() -> new ItemFichaPerfilNoEncontradoException(command.itemId()))` — existencia (400).
2. `if (!fichaQueryPort.esEstudiantePropietario(item.getFichaPerfilId(), command.estudianteId()))` → lanzar `FichaNoPropietarioException` (403).
3. `long totalRevisiones = revisionQueryPort.contarPorItem(command.itemId())` — leer dato.
4. `item.removerse(totalRevisiones)` — el aggregate decide (invariante → 422 si hay revisiones).
5. `itemOutputPort.eliminarPorId(command.itemId())` — eliminar físicamente.

**Invariante en el aggregate (`ItemFichaPerfilAggregate.removerse`):**
```java
public void removerse(long totalRevisiones) {
    var result = new ValidationResult();
    if (totalRevisiones > 0) {
        result.addError(FichasMessages.ItemFichaPerfil.CAMPO_REVISIONES,
                FichasMessages.ItemFichaPerfil.ITEM_CON_REVISIONES,
                FichasMessages.ItemFichaPerfil.ITEM_CON_REVISIONES_MSG.formatted(id));
    }
    result.throwIfHasErrors();
}
```

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `ItemFichaPerfilAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** No — la HU no emite eventos, es CRUD interno sin consumidores ni auditoría. La entidad ya existe como clase plana con factories `crear`/`reconstruir` y seguirá así.
- **ID:** `UUID`

> **Coherencia obligatoria — verificado:** La entidad `ItemFichaPerfilAggregate` existe en `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java` y actualmente NO extiende `AggregateRoot` (es `public final class`). Esta HU mantiene ese estado porque no emite eventos.

### Atributos por objeto de dominio

#### `ItemFichaPerfil`

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | Identifica el ítem |
| `fichaPerfilId` | `UUID` | — | Sí | No | No | FK a FichaPerfil |
| `tipoItem` | `TipoItem` (enum) | — | Sí | No | No | Tipo de ítem (objetivo, pregunta, etc.) |
| `contenido` | `String` | 1-7000 | Sí | Sí | No | Información del ítem — limpiar espacios |

**Combinaciones únicas (Restricciones):**
- Combinación única `(fichaPerfilId, tipoItem, contenido)` documentada en el modelo enriquecido — **no se replica en el MER** (comentario TODO en `03_tablas_fichas_perfil.sql`). Esta HU no la valida porque solo elimina, no crea ni modifica contenido.

### Traducción del modelo enriquecido a código

Esta HU NO modifica el aggregate ni la JPA Entity — ambos ya existen. Solo se agregan:
- Puerto de salida con método `eliminarPorId(UUID itemId)`.
- Puerto de consulta cross-aggregate con `contarPorItem(UUID itemId)` y `obtenerFichaIdPorItem(UUID itemId)`.
- Excepciones de aplicación.

### Eventos de Dominio que emite

**Eventos: ninguno.**

**Razón:** CRUD interno sin consumidores conocidos ni casos de auditoría identificados. El Event Storming documenta "Información Item Removida" pero el usuario confirmó que no hay necesidad de consumirlo.

**Implicaciones:**
- La entidad raíz `ItemFichaPerfilAggregate` NO extiende `AggregateRoot` — es una clase plana con factories `crear`/`reconstruir`.
- El factory `crear(...)` NO acumula eventos (no existe `publishEvent`).
- El use case NO inyecta `EventPublisher`, no hay drenado de eventos.
- No se crean archivos en `domain/itemfichaperfil/event/`.

---

## 5. Integraciones Externas

**No aplica.** Esta HU solo interactúa con PostgreSQL (vía puertos de persistencia).

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS — caso de uso write (Eliminar)

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java` | **MODIFICAR** | Agregar método `removerse(long totalRevisiones)` con invariante POL-05 usando `ValidationResult` → `DomainValidationException` → 422 |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/exception/ItemFichaPerfilNoEncontradoException.java` | Exception | Extiende `ApplicationException`. Lanzada cuando el ítem no existe → 400 |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/model/RemoverItemFichaPerfilCommand.java` | `record` | Intención de negocio: `record RemoverItemFichaPerfilCommand(UUID itemId, UUID estudianteId) {}` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/port/in/RemoverItemFichaPerfilInputPort.java` | Interface (vacía) | Extiende `VoidInputPort<RemoverItemFichaPerfilCommand>` de `shared:domain` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/RemoverItemFichaPerfilUseCase.java` | UseCase | `@Component` que implementa el `InputPort`. Patrón: existencia (→ 400) → propiedad (→ 403) → obtener dato → delegar invariante al aggregate (→ 422) → eliminar |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/query/port/out/RevisionItemQueryOutputPort.java` | Interface | Puerto de salida read cross-aggregate. Método: `long contarPorItem(UUID itemId)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/RemoverItemFichaPerfilInputAdapter.java` | `@RestController` | Inyecta el `InputPort`. Respuesta: `ResponseEntity<Void>` con `204 No Content`. Extrae `estudianteId` del JWT con `@AuthenticationPrincipal Jwt jwt`. ADR-011 (`@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`) |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/query/adapter/out/persistence/RevisionItemQueryOutputAdapter.java` | Adapter | Implementa `RevisionItemQueryOutputPort`. Método `contarPorItem(itemId)` usa JPA repository `countByItemId(itemId)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/persistence/RevisionItemJpaEntity.java` | JPA Entity | Mapea la tabla `revision_item`. Sin aggregate ni use case propio en esta HU — solo existe para que el repository pueda consultar. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/persistence/RevisionItemJpaRepository.java` | `JpaRepository` | Método derived query: `long countByItemId(UUID itemId)` |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.8__crear_revision_item.sql` | Flyway | Crea tabla `revision_item` con FK a `item(id) ON DELETE CASCADE`. FK a `estado_revision` diferida a HU posterior. |

> **Archivos eliminados respecto a borrador inicial:** `ItemFichaPerfilConRevisionesException.java` (la invariante la maneja `DomainValidationException` vía `ValidationResult`, no una clase propia) y `ItemFichaPerfilQueryOutputPort.java` con su adapter (innecesarios al usar `buscarPorId` que ya existe en `ItemFichaPerfilOutputPort` y devuelve el aggregate con `getFichaPerfilId()`).

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java` | Agregar método `public void removerse(long totalRevisiones)` con invariante POL-05 usando `ValidationResult` |
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/port/out/ItemFichaPerfilOutputPort.java` | Agregar método `void eliminarPorId(UUID itemId)` |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/out/persistence/ItemFichaPerfilCommandOutputAdapter.java` | Implementar `eliminarPorId(itemId)` → `jpaRepository.deleteById(itemId)` |
| `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/query/port/out/FichaPerfilQueryOutputPort.java` | Ya existe el método `boolean esEstudiantePropietario(UUID fichaPerfilId, UUID estudianteId)` — **NO se modifica** |

> **Verificación obligatoria antes de finalizar el plan:** El método `esEstudiantePropietario` fue introducido en HU-164 (cambiar asesor) y existe en `FichaPerfilQueryOutputPort`. El plan asume su existencia — si no existe, el implementador debe crearlo en `FichaPerfilQueryOutputPort` + `FichaPerfilQueryOutputAdapter`.

### Catálogo de mensajes (`shared:message`) — fila obligatoria

| Capa | Ruta completa | Tipo | Acción | Detalles |
|------|---------------|------|--------|----------|
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Catálogo | MODIFICAR | Agregar constantes dentro de `public static final class ItemFichaPerfil` (si no existe la nested class, crearla con constructor privado vacío). Agregar en el orden de las 5 secciones: `// Campos` → `// Códigos de error` → `// Mensajes de error` → `// Logs`. |

**Inventario de constantes a agregar:**

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO` | Códigos de error | `String` | `"ITEM_NO_ENCONTRADO"` | `ItemFichaPerfilNoEncontradoException` (errorCode) |
| `FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO_MSG` | Mensajes de error | `String` | `"El ítem no existe: %s"` | `ItemFichaPerfilNoEncontradoException` (mensaje, `.formatted(itemId)`) |
| `FichasMessages.ItemFichaPerfil.CAMPO_REVISIONES` | Campos | `String` | `"revisiones"` | `result.addError(...)` en `ItemFichaPerfilAggregate.removerse` |
| `FichasMessages.ItemFichaPerfil.ITEM_CON_REVISIONES` | Códigos de error | `String` | `"ITEM_CON_REVISIONES"` | `result.addError(...)` en `ItemFichaPerfilAggregate.removerse` |
| `FichasMessages.ItemFichaPerfil.ITEM_CON_REVISIONES_MSG` | Mensajes de error | `String` | `"No se puede eliminar el ítem porque tiene revisiones: %s"` | `result.addError(...)` en `ItemFichaPerfilAggregate.removerse` (`.formatted(id)`) |
| `FichasMessages.ItemFichaPerfil.LOG_REMOVIDO` | Logs | `String` | `"Ítem removido — id={}, fichaPerfilId={}"` | `log.info` en `RemoverItemFichaPerfilUseCase` |

> **Nota:** `FichasMessages.FichaPerfil.FICHA_NO_PROPIA` ya existe (introducida en HU-164) — **NO se duplica**.

---

## 7. Detalle por Archivo

### `ItemFichaPerfilAggregate.java` — MODIFICAR
- **Ruta:** `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java`
- **Cambio:** Agregar método `public void removerse(long totalRevisiones)`.
- **Implementación:**
```java
public void removerse(long totalRevisiones) {
    var result = new ValidationResult();
    if (totalRevisiones > 0) {
        result.addError(FichasMessages.ItemFichaPerfil.CAMPO_REVISIONES,
                FichasMessages.ItemFichaPerfil.ITEM_CON_REVISIONES,
                FichasMessages.ItemFichaPerfil.ITEM_CON_REVISIONES_MSG.formatted(id));
    }
    result.throwIfHasErrors();
}
```
- **Patrón:** mismo que `esFichaModificable` en `FichaPerfilAggregate` — el use case lee el dato externo y lo pasa; el aggregate decide.
- **Dependencias:** `ValidationResult` de `shared:domain` — **sin Lombok, sin Spring** (capa domain).

---

### `ItemFichaPerfilNoEncontradoException.java`
- **Paquete:** `com.arquisoft.fichas.application.itemfichaperfil.exception`
- **Tipo:** Exception
- **Responsabilidad:** Extiende `ApplicationException` (de `shared:exception`) → 400. Se lanza cuando el ítem no existe en BD.
- **Métodos principales:**
    - Constructor: `ItemFichaPerfilNoEncontradoException(UUID itemId)` → llama `super(FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO_MSG.formatted(itemId), FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO)`
- **Dependencias:** `ApplicationException`, `FichasMessages.ItemFichaPerfil`

---

### `RemoverItemFichaPerfilCommand.java`
- **Paquete:** `com.arquisoft.fichas.application.itemfichaperfil.command.model`
- **Tipo:** `record`
- **Responsabilidad:** Intención de negocio. Transporta el `itemId` a eliminar y el `estudianteId` del actor.
- **Features Java 21 aplicables:** `record` inmutable
- **Definición:** `public record RemoverItemFichaPerfilCommand(UUID itemId, UUID estudianteId) {}`
- **Dependencias:** Ninguna

---

### `RemoverItemFichaPerfilInputPort.java`
- **Paquete:** `com.arquisoft.fichas.application.itemfichaperfil.command.port.in`
- **Tipo:** Interface (vacía)
- **Responsabilidad:** Extiende `VoidInputPort<RemoverItemFichaPerfilCommand>` de `shared:domain.port.in`. Define el contrato del use case sin retorno.
- **Métodos principales:** Heredados de `VoidInputPort` → `void ejecutar(RemoverItemFichaPerfilCommand command)`
- **Dependencias:** `VoidInputPort`, `RemoverItemFichaPerfilCommand`

---

### `RemoverItemFichaPerfilUseCase.java`
- **Paquete:** `com.arquisoft.fichas.application.itemfichaperfil.command`
- **Tipo:** UseCase
- **Responsabilidad:** Implementa `RemoverItemFichaPerfilInputPort`. Orquesta validaciones y eliminación física.
- **Features Java 21 aplicables:** `var` para variables locales evidentes
- **Métodos principales:**
    - `ejecutar(RemoverItemFichaPerfilCommand command): void` — flujo:
        1. `var item = itemOutputPort.buscarPorId(command.itemId()).orElseThrow(() -> new ItemFichaPerfilNoEncontradoException(command.itemId()))` — existencia (400).
        2. `if (!fichaQueryPort.esEstudiantePropietario(item.getFichaPerfilId(), command.estudianteId()))` → lanzar `FichaNoPropietarioException(...)` (403).
        3. `long totalRevisiones = revisionQueryPort.contarPorItem(command.itemId())` — leer dato externo.
        4. `item.removerse(totalRevisiones)` — el aggregate decide la invariante POL-05 (→ `DomainValidationException` 422 si hay revisiones).
        5. `itemOutputPort.eliminarPorId(command.itemId())` — eliminar físicamente.
        6. `log.info(FichasMessages.ItemFichaPerfil.LOG_REMOVIDO, command.itemId(), item.getFichaPerfilId())`.
- **Dependencias:** Inyecta `ItemFichaPerfilOutputPort`, `FichaPerfilQueryOutputPort`, `RevisionItemQueryOutputPort` vía constructor con `@RequiredArgsConstructor`.
- **Anotaciones:** `@Component`, `@Slf4j`, `@Transactional(transactionManager = "fichasTransactionManager")` (qualifier obligatorio — `usuariosTransactionManager` es `@Primary`)

---

### `RevisionItemQueryOutputPort.java`
- **Paquete:** `com.arquisoft.fichas.application.revisionitem.query.port.out`
- **Tipo:** Interface
- **Responsabilidad:** Puerto de salida read cross-aggregate para validar POL-05.
- **Métodos principales:**
    - `long contarPorItem(UUID itemId)` — retorna el count de revisiones del ítem.
- **Dependencias:** Ninguna

---

### `RemoverItemFichaPerfilInputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web`
- **Tipo:** `@RestController`
- **Responsabilidad:** Endpoint REST `DELETE /fichas-perfil/items/{itemId}`. Extrae `estudianteId` del JWT, construye el `Command` y delega al `InputPort`.
- **Features Java 21 aplicables:** `var` para variables locales evidentes
- **Métodos principales:**
    - `remover(@PathVariable UUID itemId, @AuthenticationPrincipal Jwt jwt): ResponseEntity<Void>` — extrae `estudianteId = UUID.fromString(jwt.getSubject())` (**la identidad del actor SIEMPRE sale del token, nunca del path ni del body**), invoca `inputPort.ejecutar(new RemoverItemFichaPerfilCommand(itemId, estudianteId))`, retorna `ResponseEntity.noContent().build()` (204).
- **Dependencias:** Inyecta `RemoverItemFichaPerfilInputPort` vía constructor con `@RequiredArgsConstructor`.
- **Anotaciones:** `@RestController`, `@RequestMapping("/fichas-perfil")`, `@RequiredArgsConstructor`, `@Tag(name = "Fichas", description = "Gestión de fichas de perfil")` (ADR-011).
- **`@Tag`:** `name = "Fichas"`, `description = "Gestión de fichas de perfil"`
- **Endpoints documentados:**

  | Método del Controller | `@Operation(summary)` | Códigos `@ApiResponse` | `@SecurityRequirement` |
  |-----------------------|-----------------------|------------------------|------------------------|
  | `remover` | `"Remover un ítem de la ficha"` | 204, 400, 401, 403, 422 | `bearerAuth` |

---

### `RevisionItemJpaEntity.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.revisionitem.persistence`
- **Tipo:** JPA Entity (`@Entity`, `@Table(name = "revision_item", schema = "fichas_perfil")`)
- **Responsabilidad:** Mapea la tabla `revision_item` únicamente para permitir el `countByItemId`. No tiene aggregate de dominio ni use case en esta HU — es infraestructura mínima para la validación POL-05.
- **Campos:**
  ```java
  @Id UUID id;
  @Column(name = "item_id") UUID itemId;
  @Column(name = "estado_revision_id") String estadoRevisionId;   // VARCHAR(50), sin FK por ahora
  @Column(name = "fecha_creacion") LocalDateTime fechaCreacion;
  ```
- **Anotaciones de clase:** `@Entity`, `@Table`, `@Getter`, `@NoArgsConstructor` (Lombok permitido en infrastructure).
- **Sin `@ManyToOne` hacia `item`** — la relación no se mapea en JPA, solo se usa `itemId` como columna plana para el derived query.

---

### `RevisionItemQueryOutputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.revisionitem.query.adapter.out.persistence`
- **Tipo:** Adapter
- **Responsabilidad:** Implementa `RevisionItemQueryOutputPort`. Traduce llamadas del puerto a consultas JPA.
- **Métodos principales:**
    - `contarPorItem(UUID itemId): long` — `return jpaRepository.countByItemId(itemId);`
- **Dependencias:** Inyecta `RevisionItemJpaRepository` (a crear) vía constructor con `@RequiredArgsConstructor`.
- **Anotaciones:** `@Component`, `@RequiredArgsConstructor`

---

### `RevisionItemJpaRepository.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.revisionitem.persistence`
- **Tipo:** `JpaRepository`
- **Responsabilidad:** Repositorio JPA para `RevisionItemJpaEntity`. Método derived query para contar revisiones por ítem.
- **Métodos principales:**
    - `long countByItemId(UUID itemId)` — derived query de Spring Data JPA.
- **Definición:** `public interface RevisionItemJpaRepository extends JpaRepository<RevisionItemJpaEntity, UUID> { long countByItemId(UUID itemId); }`
- **Dependencias:** Ninguna

---

### Modificación: `ItemFichaPerfilOutputPort.java`
- **Cambio:** Agregar método `void eliminarPorId(UUID itemId);`
- **Razón:** El puerto write del dominio expone la operación de eliminación. El adapter la implementa delegando a `jpaRepository.deleteById(itemId)`.

---

### Modificación: `ItemFichaPerfilCommandOutputAdapter.java`
- **Cambio:** Implementar método `eliminarPorId(UUID itemId)` → `jpaRepository.deleteById(itemId);`
- **Razón:** Traducción directa del dominio a JPA. La eliminación es física (DELETE en BD).

---

## 8. Endpoints REST

### Estado del endpoint

- [x] **Endpoint NUEVO** — crear `RemoverItemFichaPerfilInputAdapter.java` desde cero.

### Contrato del endpoint

| Método | Ruta | Request Body / Params | Response | Código HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|----------------------|----------|-------------|----------------------|-------------------------------|
| DELETE | `/fichas-perfil/items/{itemId}` | `@PathVariable UUID itemId` + `@AuthenticationPrincipal Jwt jwt` (el `estudianteId` sale del token, nunca del path ni del body) | `Void` (sin body) | 204 | `fichas:item-ficha-perfil:delete` | `@Operation(summary="Remover un ítem de la ficha")` + `@ApiResponses({@ApiResponse(204), @ApiResponse(400), @ApiResponse(401), @ApiResponse(403), @ApiResponse(422)})` + `@SecurityRequirement(name="bearerAuth")` |

> **Convención de respuesta:** `ResponseEntity<Void>` con `204 No Content`. El use case implementa `VoidInputPort<Command>` de `shared:domain.port.in`.

> **Ruta SIN anidar el padre (skill `arquisoft-context` §"Diseño de rutas REST"):** el `itemId` es UUID y basta para localizar el ítem — **el use case no usa ningún `fichaPerfilId` de la ruta**, así que no se anida (`/fichas-perfil/items/{itemId}`, no `/fichas-perfil/{fichaPerfilId}/items/{itemId}`). Anidarlo sería peso muerto: obligaría a validar el emparejamiento solo para justificar un parámetro agregado.
>
> **El padre en el path NO aportaría seguridad.** La autorización por instancia se resuelve sin él y no es manipulable por el cliente: el `estudianteId` sale del **JWT** (`UUID.fromString(jwt.getSubject())`) y el `fichaPerfilId` se **deriva del propio ítem** leído de BD (`obtenerFichaIdPorItem(itemId)`), no de la petición. Con eso, `esEstudiantePropietario(fichaDelItem, estudianteDelJwt)` → `false` ⇒ **403**. Lo único que el cliente controla es el `itemId`. Se mantiene la doble capa: `@PreAuthorize` autoriza por **rol**, `esEstudiantePropietario` por **instancia**.

> **Client role:** `fichas:item-ficha-perfil:delete` (kebab-case, recurso afectado = `item-ficha-perfil`, no `ficha-perfil`). Ver sección 9.

---

## 9. Seguridad y Autorización (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Roles realm que lo poseen | Endpoint(s) que lo requieren | Descripción funcional |
|---|---|---|---|
| `fichas:item-ficha-perfil:delete` | `estudiante` | `DELETE /fichas-perfil/items/{itemId}` | Permite a un estudiante eliminar un ítem de su ficha de perfil |

### Reglas de uso

1. **Formato del client role:** `{contexto}:{recurso}:{accion}` **en kebab-case** — `fichas:item-ficha-perfil:delete`.
2. **Conversión de nombres:** `ItemFichaPerfilAggregate` → recurso `item-ficha-perfil` (guiones, todo en minúsculas).
3. **Un client role, un rol realm:** Solo el rol `estudiante` puede eliminar ítems.
4. **Cada endpoint tiene exactamente un `@PreAuthorize("hasAuthority('...')")`** con el client role exacto.

### Configuración requerida en Keycloak (instrucciones para equipo de seguridad)

Para el client role `fichas:item-ficha-perfil:delete`:

1. En el cliente `arquisoft-api`: crear el client role con el nombre exacto `fichas:item-ficha-perfil:delete`.
2. Asignar el client role al rol realm `estudiante`.
3. Verificar que los usuarios de prueba con rol `estudiante` reciban el client role en su JWT (`resource_access.arquisoft-api.roles`).

---

## 10. Eventos RabbitMQ

**Eventos: ninguno.**

**Razón:** CRUD interno sin consumidores conocidos ni casos de auditoría identificados. El Event Storming documenta "Información Item Removida" pero el usuario confirmó que no hay necesidad de consumirlo en esta versión del proyecto. Si en el futuro aparece un consumidor, esa HU promoverá el aggregate a extender `AggregateRoot` y creará el evento `ItemFichaPerfilRemovidoEvent`.

---

## 11. Migración de Base de Datos

Se requiere **una** migración Flyway para crear la tabla `revision_item`. La FK hacia `estado_revision` se omite intencionalmente — se añadirá en una HU posterior cuando esa tabla exista.

| Archivo | Ruta |
|---------|------|
| `V1.8__crear_revision_item.sql` | `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.8__crear_revision_item.sql` |

> **Versión verificada:** la última migración existente en `fichas` es `V1.7__agregar_constraints_unicidad_faltantes.sql`, por lo que esta HU usa `V1.8`.

**Contenido de la migración:**
```sql
CREATE TABLE revision_item (
    id                 UUID         PRIMARY KEY,
    item_id            UUID         NOT NULL,
    estado_revision_id VARCHAR(50)  NOT NULL,
    fecha_creacion     TIMESTAMP    NOT NULL,
    CONSTRAINT fk_rev_item        FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE,
    CONSTRAINT uk_revision_item_fecha UNIQUE (item_id, fecha_creacion)
);
```

> `ON DELETE CASCADE` garantiza que al eliminar físicamente un `item`, sus `revision_item` asociadas también se borran — esto es consistente con la eliminación física de la HU y hace la validación POL-05 significativa (si existen revisiones, hay que rechazar antes de llegar al DELETE).

> La FK hacia `estado_revision` se añade en una migración posterior (`V1.x__agregar_fk_estado_revision_item.sql`) cuando esa tabla sea creada.

---

## 12. Casos de Prueba Sugeridos

### Presupuesto orientativo
**HU pequeña (1 endpoint, 1 entidad):** 15 - 25 tests.

---

### Caso A — Use Case de ESCRITURA (elimina)

#### Tests capa `domain`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `ItemFichaPerfilAggregateTest` | `debeLanzarDomainValidationException_cuandoItemTieneRevisiones` | `removerse(1)` → lanza `DomainValidationException` con `fieldErrors[{field:"revisiones", errorCode:"ITEM_CON_REVISIONES"}]` |
| `ItemFichaPerfilAggregateTest` | `debePermitirRemover_cuandoSinRevisiones` | `removerse(0)` → no lanza excepción |

---

#### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `RemoverItemFichaPerfilUseCaseTest` | `debeEliminar_cuandoDatosValidos` | `buscarPorId` retorna el aggregate, `esEstudiantePropietario` → `true`, `contarPorItem` → `0` → verifica que `eliminarPorId` se invoca |
| `RemoverItemFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoItemNoExiste` | `buscarPorId` retorna `Optional.empty()` → lanza `ItemFichaPerfilNoEncontradoException` + verifica errorCode; verifica que `eliminarPorId` NUNCA se invoca |
| `RemoverItemFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoEstudianteNoEsPropietario` | `esEstudiantePropietario` retorna `false` → lanza `FichaNoPropietarioException` + verifica errorCode; verifica que `contarPorItem` y `eliminarPorId` NUNCA se invocan |
| `RemoverItemFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoItemTieneRevisiones` | `contarPorItem` retorna `> 0` → `item.removerse(...)` propaga `DomainValidationException` con `fieldErrors[revisiones]`; verifica que `eliminarPorId` NUNCA se invoca |

> **NO se testea ciclo de eventos** — esta HU no emite eventos.
> **Nota para el tester:** el use case invoca un método real del aggregate (`removerse`), no un mock. Construir el `ItemFichaPerfilAggregate` con `reconstruir(...)` en el Arrange y devolverlo desde el stub de `buscarPorId`.

---

#### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `ItemFichaPerfilCommandOutputAdapterTest` | `debeEliminar_cuandoIdValido` | `eliminarPorId` delega a `jpaRepository.deleteById` correctamente (test unitario con Mockito — agregar al test existente del adapter) |
| `RevisionItemQueryOutputAdapterTest` | `debeRetornarCount_cuandoItemTieneRevisiones` | `contarPorItem` delega a `countByItemId` y retorna valor > 0 |
| `RevisionItemQueryOutputAdapterTest` | `debeRetornarCero_cuandoItemSinRevisiones` | `contarPorItem` retorna 0 |
| `RevisionItemJpaRepositoryTest` | `debeContarRevisiones_cuandoItemTieneRevisiones` | `@DataJpaTest` con H2: persistir 2 `RevisionItemJpaEntity` del mismo `itemId` → `countByItemId` retorna 2 |
| `RevisionItemJpaRepositoryTest` | `debeRetornarCero_cuandoItemSinRevisiones` | `@DataJpaTest` con H2: `countByItemId` de un UUID sin revisiones → 0 |
| `RemoverItemFichaPerfilInputAdapterTest` | `debe204_cuandoPeticionValida` | DELETE con JWT válido y rol correcto → 204 No Content |
| `RemoverItemFichaPerfilInputAdapterTest` | `debe400_cuandoItemNoExiste` | Mock del InputPort lanza `ItemFichaPerfilNoEncontradoException` → 400 + errorCode en body |
| `RemoverItemFichaPerfilInputAdapterTest` | `debe403_cuandoEstudianteNoEsPropietario` | Mock del InputPort lanza `FichaNoPropietarioException` → 403 + errorCode en body |
| `RemoverItemFichaPerfilInputAdapterTest` | `debe422_cuandoItemTieneRevisiones` | Mock del InputPort lanza `DomainValidationException` → 422 + `fieldErrors[{field:"revisiones"}]` en body |
| `RemoverItemFichaPerfilInputAdapterTest` | `debe401_cuandoNoAutenticado` | Sin token → 401 |
| `RemoverItemFichaPerfilInputAdapterTest` | `debe403_cuandoRolInsuficiente` | JWT con rol `asesor` (no `estudiante`) → 403 |

---

### Reglas de consolidación

- **NO crear tests de excepciones simples** — `ItemFichaPerfilNoEncontradoException` solo hace `super(mensaje, errorCode)` → su `errorCode` se verifica en el test del use case, no en test propio de la excepción.
- **NO crear test de `RemoverItemFichaPerfilCommand`** — es un `record` sin lógica.
- **NO crear test de `RemoverItemFichaPerfilInputPort`** — es una interfaz vacía.
- **NO crear test de `RevisionItemJpaEntity`** — solo tiene getters generados por Lombok.
- **NO testear propagación de `DataAccessException`** — es comportamiento del framework, no lógica propia.
- **Consolidar asserts:** el test `debe400_cuandoItemNoExiste` verifica tanto el tipo de excepción como el `errorCode` en un solo test (no dos tests separados).

### Conteo de tests planificados

| Capa | Clase | Tests |
|------|-------|-------|
| domain | `ItemFichaPerfilAggregateTest` (ampliar existente) | 2 |
| application | `RemoverItemFichaPerfilUseCaseTest` | 4 |
| infrastructure | `ItemFichaPerfilCommandOutputAdapterTest` (ampliar existente) | 1 |
| infrastructure | `RevisionItemQueryOutputAdapterTest` | 2 |
| infrastructure | `RevisionItemJpaRepositoryTest` (`@DataJpaTest`) | 2 |
| infrastructure | `RemoverItemFichaPerfilInputAdapterTest` (`@WebMvcTest`) | 6 |
| | **Total** | **17** |

Dentro del presupuesto orientativo de 15-25 tests para una HU pequeña.

---

## 13. Checklist de Implementación

- [ ] **DDD:** Entidad de dominio `ItemFichaPerfilAggregate` ya existe y **NO** extiende `AggregateRoot` (confirmado — clase plana con factories `crear`/`reconstruir`, sin eventos)
- [ ] **Eventos de dominio:** Ninguno — esta HU no emite eventos, no se crean archivos en `domain/itemfichaperfil/event/`, el use case NO inyecta `EventPublisher`
- [ ] IDs siempre `UUID` (ya cumplido en el aggregate existente)
- [ ] Puerto de entrada (`RemoverItemFichaPerfilInputPort`) extiende `VoidInputPort<Command>` — interfaz vacía definida
- [ ] Puerto de salida write (`ItemFichaPerfilOutputPort`) extendido con método `eliminarPorId(UUID itemId)`
- [ ] **NO se crea `ItemFichaPerfilQueryOutputPort`** — el use case usa `ItemFichaPerfilOutputPort.buscarPorId(itemId)` que ya existe y devuelve el aggregate con `getFichaPerfilId()`
- [ ] **`ItemFichaPerfilAggregate.removerse(long totalRevisiones)`** implementado con `ValidationResult` — sin Lombok ni Spring (capa domain)
- [ ] Puerto de salida read cross-aggregate (`RevisionItemQueryOutputPort`) creado con método `contarPorItem(UUID itemId): long`
- [ ] Excepciones extienden la clase base correcta: `ItemFichaPerfilNoEncontradoException` extiende `ApplicationException` → 400; `FichaNoPropietarioException` (ya existe) extiende `AuthorizationException` → 403. POL-05 → `DomainValidationException` vía `ValidationResult` → 422 (sin clase propia). `GlobalAppExceptionHandler` de `shared:web` resuelve el HTTP automáticamente. **NO se crea handler de contexto.**
- [ ] `Command` (`record` en `application/itemfichaperfil/command/model/`) creado — `RemoverItemFichaPerfilCommand(UUID itemId, UUID estudianteId)`
- [ ] **NO se crea `RequestDTO`** — el DELETE no tiene body, el `itemId` viaja en `@PathVariable`, el `estudianteId` se extrae del JWT
- [ ] Caso de uso (`RemoverItemFichaPerfilUseCase`) con `@Component`, `@Slf4j`, `@RequiredArgsConstructor`, `@Transactional(transactionManager = "fichasTransactionManager")` (qualifier obligatorio). Orden: existencia `buscarPorId` (→ 400) → propiedad `esEstudiantePropietario` (→ 403) → leer dato `contarPorItem` → `item.removerse(totalRevisiones)` (→ 422 si viola POL-05) → `eliminarPorId`
- [ ] Controller REST con autorización vía `@PreAuthorize("hasAuthority('fichas:item-ficha-perfil:delete')")` **en kebab-case**. Extrae `estudianteId` del JWT con `@AuthenticationPrincipal Jwt jwt` + `UUID.fromString(jwt.getSubject())`
- [ ] Controller documentado con `@Tag`, `@Operation`, `@ApiResponses({@ApiResponse(204), @ApiResponse(400), @ApiResponse(401), @ApiResponse(403), @ApiResponse(422)})`, `@SecurityRequirement(name = "bearerAuth")` (ADR-011)
- [ ] Adaptador de repositorio (`ItemFichaPerfilCommandOutputAdapter`) implementa `eliminarPorId(itemId)` → `jpaRepository.deleteById(itemId)` (eliminación física)
- [ ] Adapter query cross-aggregate (`RevisionItemQueryOutputAdapter`) implementa `contarPorItem` usando `RevisionItemJpaRepository.countByItemId(itemId)`
- [ ] `RevisionItemJpaRepository` creado con método derived query `long countByItemId(UUID itemId)`
- [ ] **Migración Flyway `V1.8__crear_revision_item.sql`** creada con `ON DELETE CASCADE` sobre `item(id)`. FK a `estado_revision` omitida intencionalmente (HU posterior). Verificado que `V1.7` ya está ocupada.
- [ ] **`RevisionItemJpaEntity`** creada con campos: `id`, `itemId`, `estadoRevisionId` (`String`), `fechaCreacion`. Sin `@ManyToOne` — relación no mapeada en JPA.
- [ ] **Catálogo `shared:message`:** constantes agregadas a `FichasMessages.ItemFichaPerfil` (nested class): `CAMPO_REVISIONES`, `ITEM_NO_ENCONTRADO`, `ITEM_NO_ENCONTRADO_MSG`, `ITEM_CON_REVISIONES`, `ITEM_CON_REVISIONES_MSG`, `LOG_REMOVIDO`
- [ ] Tests unitarios con patrón AAA (cobertura ≥ 75%), **sin tests de ciclo de eventos del Aggregate Root** (esta HU no emite eventos). Total planificado: **17 tests**
- [ ] Test de dominio de la invariante POL-05 (`ItemFichaPerfilAggregateTest.debeLanzarDomainValidationException_cuandoItemTieneRevisiones`) verificando `fieldErrors[{field:"revisiones", errorCode:"ITEM_CON_REVISIONES"}]`
- [ ] Tests del use case verifican que `eliminarPorId` **NUNCA** se invoca en los 3 caminos de error
- [ ] Tests de repositorio con H2 (`@DataJpaTest`) para `RevisionItemJpaRepository.countByItemId`
- [ ] Tests de controller con `@WebMvcTest` + `@Import(GlobalAppExceptionHandler.class)` + `@MockitoBean` del InputPort (Spring Boot 4.x), autenticación con `SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("fichas:item-ficha-perfil:delete"))`
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot)
- [ ] Commit: `feat(fichas): remover ítem de ficha perfil - HU-034`

---

## 14. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ⏳ Pendiente |       |       |
| Tests      | @tester             | ⏳ Pendiente |       |       |
| Validación | @validator-analyze  | ⏳ Pendiente |       |       |
| Reporte    | @validator-report   | ⏳ Pendiente |       |       |
| Commit     | @commit             | ⏳ Pendiente |       |       |
