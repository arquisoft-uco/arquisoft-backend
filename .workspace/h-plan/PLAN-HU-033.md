# PLAN: HU-033 Modificar ítem de ficha de perfil

## Metadata
- **ID Historia:** HU-033
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura
- **¿Usa AggregateRoot?:** No — la entidad `ItemFichaPerfilAggregate` ya existe y NO extiende `AggregateRoot` porque el CRUD de ítems no emite eventos de dominio
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-07-03
- **Rama sugerida:** `feature/HU-033-modificar-item-ficha-perfil`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** Confirmado que el límite de longitud del contenido es 7000 caracteres (según MER, VARCHAR(7000)). Sobre validación de estado de ficha que impida modificaciones: se debe revisar cómo lo manejan HU-161/HU-162 ya implementadas y aplicar un patrón similar.

---

## 1. Resumen Funcional

HU-033 permite a un estudiante **modificar el contenido de un ítem existente** de su propia ficha de perfil. El estudiante puede actualizar únicamente el campo `contenido` del ítem; los campos `tipo_item_id` y `ficha_perfil_id` son inmutables. El sistema valida que el ítem exista, que el estudiante sea propietario de la ficha, que el contenido sea válido (no vacío, máximo 7000 caracteres), y que la ficha se encuentre en un estado que permita modificaciones. Esta HU **NO** crea ítems nuevos — esa responsabilidad pertenece a HU-031. La HU **NO** emite eventos de dominio porque es un CRUD interno sin consumidores conocidos.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | El estudiante puede modificar el contenido de un ítem de su propia ficha | El contenido del ítem se actualiza en la base de datos |
| 2 | El sistema valida que el ítem exista | Si el ítem no existe, devuelve 400 con `ItemNoEncontradoException` |
| 3 | El sistema valida que el estudiante sea propietario de la ficha | Si el estudiante no es propietario, devuelve **403** con `ItemFichaNoPropiaException` (extiende `AuthorizationException` — autorización a nivel de recurso) |
| 4 | El sistema valida que el contenido sea válido | Contenido obligatorio (no vacío), máximo 7000 caracteres → 422 con `fieldErrors` |
| 5 | Los campos `tipo_item_id` y `ficha_perfil_id` son inmutables | No se modifican aunque el cliente los envíe en el request |
| 6 | El sistema valida que la ficha esté en un estado modificable | Si la ficha está en estado terminal (`APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA`), devuelve **422** con `DomainValidationException` y `fieldErrors[0].errorCode = ESTADO_FICHA_NO_MODIFICABLE`. Es invariante de negocio → dominio → 422, **no** 400 |
| 7 | La ficha del ítem tiene un estado registrado | Si no existe ningún registro de estado para la ficha, devuelve 400 con `FichaPerfilNoEncontradaException` |

---

## 3. Reglas de Negocio

- El ítem debe existir antes de modificarlo (validación por `ItemFichaPerfilOutputPort.existsById`).
- Solo el estudiante propietario de la ficha puede modificar sus ítems (validación por `FichaPerfilQueryOutputPort.esEstudiantePropietario`).
- El contenido es obligatorio, debe limpiarse de espacios y no puede exceder 7000 caracteres.
- Los campos `tipoItem` y `fichaPerfilId` del aggregate **NO** se modifican — son inmutables tras la creación inicial.
- La ficha debe estar en un estado que permita modificaciones. Los estados terminales (`APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA`) bloquean la modificación.

### Ubicación de la regla de estado — Tell, Don't Ask

La restricción de estado es una **invariante de negocio**, así que se evalúa en el dominio (→ 422). Evaluarla con un `if` en el use case sería una fuga de lógica.

- El enum `EstadoFicha` responde por sí mismo: `esTerminal()` / `permiteModificacion()` — espejo de `EstadoEvaluacion.esTerminal()` (HU-191).
- El estado actual vive en **otro aggregate** (`EstadoFichaPerfilAggregate`, tabla de trazabilidad de HU-206). El use case lo recupera vía `EstadoFichaPerfilOutputPort.obtenerEstadoActual(fichaPerfilId)` y **se lo entrega** al aggregate.
- El aggregate decide: `item.modificarContenido(contenido, estadoFichaActual)`. El use case **no pregunta** por el estado ni ramifica sobre él — solo lo pasa. Es el mismo patrón ya implementado en `AgregarEstadoEvaluacionFichaUseCase` → `EstadoEvaluacionFichaAggregate.crearConEstado(..., ultimoEstado)`.
- Si la ficha no tiene estado registrado, el use case lanza `FichaPerfilNoEncontradaException` (400): eso es una precondición de aplicación, no una invariante.

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `ItemFichaPerfilAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** No — verificado en el archivo existente `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java` (línea 12: `public final class ItemFichaPerfilAggregate` sin extensión). La HU no emite eventos, es CRUD interno sin consumidores.
- **ID:** `UUID`

### Atributos por objeto de dominio (extraídos del modelo enriquecido)

#### `Item`

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | Identifica el registro |
| `tipoItem` | `TipoItem` (enum de dominio) | — | Sí | **No** — inmutable | No | FK a catálogo `tipo_item` (PK semántica VARCHAR(50), ADR-012) |
| `contenido` | `String` | 1-7000 | Sí | **Sí** — único campo modificable | No | Limpiar espacios (inicio/fin) |
| `fichaPerfilId` | `UUID` | — | Sí | **No** — inmutable | No | FK a `ficha_perfil` |

**Combinaciones únicas (Restricciones):**
- Ninguna adicional a las ya existentes en la tabla `item` (la restricción `tipo_item_id + contenido + ficha_perfil_id` única está comentada como `TODO` en el MER y no se implementa).

### Traducción del modelo enriquecido a código

| Característica del modelo | Traducción en código |
|---|---|
| Longitud máxima 7000 | `@Size(max=7000)` en DTO + validación en `setContenido()` del Aggregate con `DomainValidator.maxLength(..., 7000, ...)` + `@Column(length=7000)` en JPA (ya existe) + `VARCHAR(7000)` en Flyway (ya existe en V1.3) |
| Obligatorio | `@NotBlank` en DTO + validación en `setContenido()` con `DomainValidator.notBlank(...)` + `@Column(nullable=false)` en JPA (ya existe) |
| Limpiar espacios | `UtilText.applyTrim(...)` dentro del setter `setContenido()` del aggregate |
| `tipoItem` y `fichaPerfilId` inmutables | **NO** se generan setters privados para ellos en el método `modificarContenido()` — solo se modifica `contenido` |

### Estados y tipos: enum de dominio

El atributo `tipoItem` es un **enum de dominio** (`TipoItem`) con PK semántica VARCHAR(50) en el catálogo `tipo_item` (ya implementado en V1.3). El aggregate guarda el enum directamente. **No se planea aggregate/query port/query adapter para `TipoItem`** — el enum es suficiente.

### Eventos de Dominio que emite

Eventos: ninguno.

Razón: CRUD interno sin consumidores conocidos ni casos de auditoría identificados — confirmado por el usuario en la pregunta 5 de FASE 3.

Implicaciones:
- La entidad raíz `ItemFichaPerfilAggregate` NO extiende `AggregateRoot` — es una clase plana con factories `crear`/`reconstruir` (confirmado al leer el archivo existente).
- El método `modificarContenido(...)` NO acumula eventos (no existe `publishEvent`).
- El use case `ModificarItemFichaPerfilUseCase` NO inyecta `EventPublisher`, no hay drenado de eventos.
- No se crean archivos nuevos en `domain/itemfichaperfil/event/`.

---

## 5. Integraciones Externas (solo si la HU lo requiere)

No aplica — la HU solo usa PostgreSQL y no requiere integraciones externas más allá de las estándar del proyecto.

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/exception/ItemNoEncontradoException.java` | Exception del use case | Extiende `ApplicationException` (item no encontrado → 400) |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/model/ModificarItemFichaPerfilCommand.java` | `record` | Intención de negocio. Campos: `itemId` (UUID), `contenido` (String), `estudianteId` (UUID) |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/port/in/ModificarItemFichaPerfilInputPort.java` | Interface (vacía) | Extiende `VoidInputPort<ModificarItemFichaPerfilCommand>` de `shared:domain` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/ModificarItemFichaPerfilUseCase.java` | UseCase | `@Component` que implementa el `InputPort`. Patrón: `obtener item → validar propietario → modificar contenido → save` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/dto/ModificarItemFichaPerfilRequestDTO.java` | `record` | `record` con anotaciones Jakarta (`@NotBlank`, `@Size(max=7000)`). Método `toCommand(UUID itemId, UUID estudianteId)` que produce el `Command` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/ModificarItemFichaPerfilInputAdapter.java` | `@RestController` | Inyecta el `InputPort`. Retorna `ResponseEntity<Void>` con `204 No Content`. ADR-011 (`@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`) |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java` | Agregar método de negocio `modificarContenido(String nuevoContenido, EstadoFicha estadoFichaActual)` que valida con Notification Pattern la regla de estado modificable + el contenido, y actualiza solo el campo `contenido` (no modifica `tipoItem` ni `fichaPerfilId`). Si el estado es terminal, acumula `ESTADO_FICHA_NO_MODIFICABLE` y **no** aplica el cambio. |
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadoficha/EstadoFicha.java` | Agregar `esTerminal()` y `permiteModificacion()` — la regla vive en el enum (Tell, Don't Ask). |
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadofichaperfil/port/out/EstadoFichaPerfilOutputPort.java` | Agregar `Optional<EstadoFicha> obtenerEstadoActual(UUID fichaPerfilId)` (espejo de `EstadoEvaluacionFichaOutputPort.obtenerUltimoEstado`). |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadofichaperfil/persistence/EstadoFichaPerfilJpaRepository.java` | Agregar método derivado `findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(UUID)`. |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadofichaperfil/command/adapter/out/persistence/EstadoFichaPerfilCommandOutputAdapter.java` | Implementar `obtenerEstadoActual(...)` mapeando la JPA entity más reciente a `EstadoFicha.valueOf(...)`. |
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/port/out/ItemFichaPerfilOutputPort.java` | Agregar método `boolean existsById(UUID itemId)` para validar existencia del ítem antes de modificarlo. |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/out/persistence/ItemFichaPerfilCommandOutputAdapter.java` | Implementar método `existsById(UUID itemId)` delegando a `itemFichaPerfilJpaRepository.existsById(itemId)`. |
| `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/query/port/out/FichaPerfilQueryOutputPort.java` | Agregar método `boolean esEstudiantePropietario(UUID fichaPerfilId, UUID estudianteId)` para validar que el estudiante sea dueño de la ficha (lookup FK cross-aggregate — vive en application, no en domain). |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/query/adapter/out/persistence/FichaPerfilQueryOutputAdapter.java` | Implementar método `esEstudiantePropietario(UUID fichaPerfilId, UUID estudianteId)` consultando la tabla `estudiante_ficha_perfil` (existe registro con ambos UUIDs). |
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Agregar constantes nuevas en la nested class `ItemFichaPerfil` (ver sub-sección "Catálogo de mensajes" abajo). |

### Catálogo de mensajes (`shared:message`) — fila obligatoria si la HU introduce texto nuevo

| Capa | Ruta completa | Tipo | Acción | Detalles |
|------|---------------|------|--------|----------|
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Catálogo | MODIFICAR | Agregar constantes nuevas en `public static final class ItemFichaPerfil` (ya existe) en el orden de las 5 secciones. Solo las secciones con al menos una constante nueva. Nada de JavaDoc. |

Inventario de constantes a agregar al catálogo en esta HU:

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO` | Códigos de error | `String` | `"ITEM_NO_ENCONTRADO"` | `ItemNoEncontradoException` (errorCode) |
| `FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO_MSG` | Mensajes de error | `String` | `"El ítem con id %s no existe"` | `ItemNoEncontradoException` (mensaje, `.formatted(itemId)`) |
| `FichasMessages.ItemFichaPerfil.LOG_MODIFICADO` | Logs | `String` | `"Ítem modificado — id={}"` | `log.info` en `ModificarItemFichaPerfilUseCase` |
| `FichasMessages.ItemFichaPerfil.CAMPO_ESTADO_FICHA` | Campos | `String` | `"estadoFicha"` | `DomainValidator.notNull` / `addError` en `ItemFichaPerfilAggregate` |
| `FichasMessages.ItemFichaPerfil.ESTADO_FICHA_REQUERIDO` | Códigos de error | `String` | `"ITEM_ESTADO_FICHA_REQUERIDO"` | `esFichaModificable()` (errorCode) |
| `FichasMessages.ItemFichaPerfil.ESTADO_FICHA_REQUERIDO_MSG` | Mensajes de error | `String` | `"El estado actual de la ficha es obligatorio"` | `esFichaModificable()` (mensaje) |
| `FichasMessages.ItemFichaPerfil.ESTADO_FICHA_NO_MODIFICABLE` | Códigos de error | `String` | `"ESTADO_FICHA_NO_MODIFICABLE"` | `esFichaModificable()` (errorCode, POL de estado terminal) |
| `FichasMessages.ItemFichaPerfil.ESTADO_FICHA_NO_MODIFICABLE_MSG` | Mensajes de error | `String` | `"No se puede modificar el ítem porque la ficha está en estado: %s"` | `esFichaModificable()` (mensaje, `.formatted(estado.getNombre())`) |

> Las constantes de validación de dominio (`CAMPO_CONTENIDO`, `CONTENIDO_MAX`, `CONTENIDO_REQUERIDO`, `CONTENIDO_DEMASIADO_LARGO`) ya existen en el catálogo — fueron agregadas por HU-031 (Agregar ítem). No se duplican.

---

## 7. Detalle por Archivo

### `ItemFichaPerfilAggregate.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.domain.itemfichaperfil.aggregate`
- **Tipo:** Aggregate (clase plana, NO extiende `AggregateRoot`)
- **Responsabilidad:** Encapsula las reglas de negocio del ítem de ficha de perfil. Valida invariantes al crear/modificar.
- **Features Java 21 aplicables:** Ninguna nueva — el archivo ya usa `final class`, getters estándar, setters privados.
- **Métodos principales:**
    - `crear(...)` — ya existe, no se modifica.
    - `reconstruir(...)` — ya existe, no se modifica.
    - **`modificarContenido(String nuevoContenido): void`** — NUEVO. Valida con Notification Pattern (`DomainValidator.notBlank`, `DomainValidator.maxLength`) y actualiza solo el campo `contenido`. Lanza `DomainValidationException` si la validación falla.
- **Dependencias:** `TipoItem`, `UtilText`, `UtilUUID`, `DomainValidator`, `ValidationResult`, `FichasMessages.ItemFichaPerfil`

---

### `ItemFichaPerfilOutputPort.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.domain.itemfichaperfil.port.out`
- **Tipo:** Interface (puerto de salida write-side)
- **Responsabilidad:** Contrato de persistencia del aggregate. Vive en `domain/`.
- **Métodos principales:**
    - `guardar(ItemFichaPerfilAggregate item)` — ya existe, no se modifica.
    - **`boolean existsById(UUID itemId)`** — NUEVO. Valida existencia del ítem antes de modificarlo.
- **Dependencias:** `ItemFichaPerfilAggregate`, `UUID`

---

### `ItemFichaPerfilCommandOutputAdapter.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.out.persistence`
- **Tipo:** Adapter (implementa `ItemFichaPerfilOutputPort`)
- **Responsabilidad:** Traduce entre JPA Entity y Aggregate. Usa `reconstruir(...)`.
- **Métodos principales:**
    - `guardar(ItemFichaPerfilAggregate item)` — ya existe, no se modifica.
    - **`existsById(UUID itemId)`** — NUEVO. Delega a `itemFichaPerfilJpaRepository.existsById(itemId)`.
- **Dependencias:** `ItemFichaPerfilJpaRepository`, `ItemFichaPerfilMapper`

---

### `FichaPerfilQueryOutputPort.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.application.fichaperfil.query.port.out`
- **Tipo:** Interface (puerto de salida read-side)
- **Responsabilidad:** Contrato de consulta de fichas de perfil. Vive en `application/` (read side no toca el aggregate).
- **Métodos principales:**
    - `existsById(UUID fichaPerfilId)` — ya existe (agregado en HU-161), no se modifica.
    - **`boolean esEstudiantePropietario(UUID fichaPerfilId, UUID estudianteId)`** — NUEVO. Valida que el estudiante sea propietario de la ficha (lookup FK cross-aggregate — inyectado tanto por command como query use cases).
- **Dependencias:** `UUID`

---

### `FichaPerfilQueryOutputAdapter.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence`
- **Tipo:** Adapter (implementa `FichaPerfilQueryOutputPort`)
- **Responsabilidad:** Consultas de lectura sobre fichas de perfil.
- **Métodos principales:**
    - `existsById(UUID fichaPerfilId)` — ya existe, no se modifica.
    - **`esEstudiantePropietario(UUID fichaPerfilId, UUID estudianteId)`** — NUEVO. Consulta la tabla `estudiante_ficha_perfil`: `SELECT COUNT(*) > 0 FROM estudiante_ficha_perfil WHERE ficha_perfil_id = ? AND estudiante_id = ?`. Retorna `true` si existe la relación.
- **Dependencias:** `EstudianteFichaPerfilJpaRepository`, `JPA Criteria API` o `@Query` nativa

---

### `ItemNoEncontradoException.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.itemfichaperfil.exception`
- **Tipo:** Exception del use case
- **Responsabilidad:** Representa el error "ítem no encontrado" (400). Extiende `ApplicationException`. Ubicación directa bajo `itemfichaperfil/exception/` — NO anidar en `command/` o `query/`.
- **Métodos principales:**
    - Constructor que recibe `itemId`, formatea el mensaje con `FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO_MSG.formatted(itemId)`, y pasa el código `FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO` al `super(mensaje, codigo)`.
- **Dependencias:** `ApplicationException`, `FichasMessages.ItemFichaPerfil`

---

### `ModificarItemFichaPerfilCommand.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.itemfichaperfil.command.model`
- **Tipo:** `record`
- **Responsabilidad:** Intención de negocio. Campos en español idénticos al aggregate.
- **Features Java 21 aplicables:** `record` para inmutabilidad + `equals`/`hashCode` gratis.
- **Métodos principales:**
    - Constructor compacto (automático del `record`).
- **Dependencias:** `UUID`
- **Campos:**
    - `UUID itemId` — identificador del ítem a modificar.
    - `String contenido` — nuevo contenido del ítem (será validado por el aggregate).
    - `UUID estudianteId` — identificador del estudiante que intenta modificar (para validar propiedad).

---

### `ModificarItemFichaPerfilInputPort.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.itemfichaperfil.command.port.in`
- **Tipo:** Interface (vacía)
- **Responsabilidad:** Marca el use case como puerto de entrada. Extiende `VoidInputPort<ModificarItemFichaPerfilCommand>` de `shared:domain.port.in`.
- **Dependencias:** `ModificarItemFichaPerfilCommand`, `VoidInputPort`

---

### `ModificarItemFichaPerfilUseCase.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.itemfichaperfil.command`
- **Tipo:** UseCase (`@Component`)
- **Responsabilidad:** Orquesta la modificación del contenido del ítem. Patrón: validar existencia → validar propietario → obtener aggregate → modificar contenido → persistir.
- **Features Java 21 aplicables:** Ninguna — lógica de orquestación estándar.
- **Métodos principales:**
    - `ejecutar(ModificarItemFichaPerfilCommand command): void` — implementa el `VoidInputPort`. 
      1. Valida que el ítem exista (`itemFichaPerfilOutputPort.existsById(itemId)` — lanza `ItemNoEncontradoException` si no existe).
      2. Obtiene el aggregate con `itemFichaPerfilOutputPort.buscarPorId(itemId).orElseThrow()` (reconstruir).
      3. Valida que el estudiante sea propietario de la ficha (`fichaPerfilQueryOutputPort.esEstudiantePropietario(item.getFichaPerfilId(), estudianteId)` — lanza `ItemFichaNoPropiaException` si no es propietario, esa excepción ya existe de HU-031; extiende `AuthorizationException` → 403).
      4. Recupera el estado actual de la ficha: `estadoFichaPerfilOutputPort.obtenerEstadoActual(item.getFichaPerfilId()).orElseThrow(() -> new FichaPerfilNoEncontradaException(...))`. **Solo lo recupera — no lo inspecciona ni ramifica sobre él.**
      5. Llama a `item.modificarContenido(command.contenido(), estadoActual)` — el aggregate decide si el estado permite la modificación y valida el contenido con Notification Pattern (puede lanzar `DomainValidationException` → 422).
      6. Persiste con `itemFichaPerfilOutputPort.guardar(item)`.
      7. Loguea con `log.info(FichasMessages.ItemFichaPerfil.LOG_MODIFICADO, itemId)`.
- **Dependencias adicionales:** `EstadoFichaPerfilOutputPort`, `FichaPerfilNoEncontradaException`
- **Dependencias:** `ModificarItemFichaPerfilInputPort`, `ModificarItemFichaPerfilCommand`, `ItemFichaPerfilOutputPort`, `FichaPerfilQueryOutputPort`, `ItemNoEncontradoException`, `ItemFichaNoPropiaException`, `FichasMessages.ItemFichaPerfil`, `@Slf4j`, `@RequiredArgsConstructor`, `@Component`
- **Anotaciones:** `@Component`, `@RequiredArgsConstructor`, `@Slf4j`

---

### `ModificarItemFichaPerfilRequestDTO.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto`
- **Tipo:** `record`
- **Responsabilidad:** DTO de entrada HTTP con anotaciones Jakarta. Campos en español idénticos al `Command`.
- **Features Java 21 aplicables:** `record` para inmutabilidad.
- **Métodos principales:**
    - `toCommand(UUID itemId, UUID estudianteId): ModificarItemFichaPerfilCommand` — factory que produce el `Command` desde el DTO + los parámetros recibidos del path/JWT.
- **Dependencias:** `ModificarItemFichaPerfilCommand`, `@NotBlank`, `@Size`, `UUID`
- **Campos:**
    - `@NotBlank(message = "El contenido es obligatorio") @Size(max = 7000, message = "El contenido no puede exceder 7000 caracteres") String contenido` — único campo del body.

---

### `ModificarItemFichaPerfilInputAdapter.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web`
- **Tipo:** `@RestController`
- **Responsabilidad:** Endpoint REST que recibe la petición de modificación del ítem. Inyecta el `InputPort`. Retorna `ResponseEntity<Void>` con `204 No Content`. ADR-011 (`@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`).
- **Features Java 21 aplicables:** Ninguna — código estándar Spring.
- **Métodos principales:**
    - `modificarItem(@PathVariable UUID itemId, @Valid @RequestBody ModificarItemFichaPerfilRequestDTO dto, @AuthenticationPrincipal Jwt jwt): ResponseEntity<Void>` — extrae `estudianteId` del JWT (`jwt.getSubject()`), convierte DTO a `Command` con `dto.toCommand(itemId, estudianteId)`, ejecuta el `InputPort`, retorna `204 No Content` sin body.
- **Dependencias:** `ModificarItemFichaPerfilInputPort`, `ModificarItemFichaPerfilRequestDTO`, `@RestController`, `@RequestMapping`, `@PatchMapping`, `@PathVariable`, `@Valid`, `@RequestBody`, `@AuthenticationPrincipal`, `Jwt`, `ResponseEntity`, `HttpStatus`, `@Slf4j`, `@RequiredArgsConstructor`, anotaciones Swagger.
- **Anotaciones:** `@Slf4j`, `@RestController`, `@RequestMapping("/fichas-perfil")`, `@RequiredArgsConstructor`, `@Tag(name = "Fichas Perfil", description = "Gestión de fichas de perfil de proyectos de grado")`

#### Plantilla extendida para Controllers (ADR-011)

- **`@Tag`:** `name = "Fichas Perfil"`, `description = "Gestión de fichas de perfil de proyectos de grado"` — reutiliza el tag ya existente en `AgregarItemFichaPerfilInputAdapter`.
- **Endpoints documentados:**

  | Método del Controller | `@Operation(summary)` | Códigos `@ApiResponse` | `@SecurityRequirement` |
  |-----------------------|-----------------------|------------------------|------------------------|
  | `modificarItem` | `"Modificar contenido de ítem"` | 204, 400, 401, 403, 422 | `bearerAuth` |

---

## 8. Endpoints REST (si aplica)

### Estado del endpoint

- [x] **Endpoint NUEVO** — crear `ModificarItemFichaPerfilInputAdapter.java` desde cero.

### Contrato del endpoint

| Método | Ruta | Request Body / Params | Response | Código HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|----------------------|----------|-------------|----------------------|-------------------------------|
| PATCH | `/fichas-perfil/{itemId}/items/` | `ModificarItemFichaPerfilRequestDTO` (body: `contenido` String) + `itemId` (path param UUID) | `Void` (sin body) | 204 | `fichas:item-ficha-perfil:update` | `@Operation(summary="Modificar contenido de ítem", description="Permite a un estudiante modificar el contenido de un ítem de su propia ficha de perfil")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses` (204, 400, 401, 403, 422) — el 400 cubre tanto `ItemNoEncontradoException` como `ItemFichaNoPropiaException` (ambas extienden `ApplicationException`) |

> **Nota sobre el prefijo `/api`:** es global al proyecto (configurado en el `context-path` del servidor) — NO se declara explícitamente en `@RequestMapping`. La ruta declarada en el controller es relativa (`/fichas-perfil/{itemId}/items/`), sin prefijo `/api`, para evitar duplicarlo (`/api/api/...`).
>
> **Convención de respuesta (write):** Opción B — `ResponseEntity<Void>` con `204 No Content`, sin body. El use case implementa `VoidInputPort<ModificarItemFichaPerfilCommand>` de `shared:domain.port.in`. Es una actualización sin necesidad de devolver datos.

---

## 9. Seguridad y Autorización (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Roles realm que lo poseen | Endpoint(s) que lo requieren | Descripción funcional |
|---|---|---|---|
| `fichas:item-ficha-perfil:update` | `estudiante` | `PATCH /fichas-perfil/{itemId}/items/` | Permite al estudiante modificar el contenido de un ítem de su propia ficha de perfil |

### Reglas de uso

1. **Formato del client role:** `fichas:item-ficha-perfil:update` **en kebab-case** — todo en minúscula, palabras del recurso separadas por guiones (`-`).
2. **Rol realm en kebab-case:** `estudiante`.
3. **Un client role puede pertenecer a varios roles realm.** En este caso, solo `estudiante` posee este client role.
4. **Cada endpoint REST tiene exactamente un `@PreAuthorize("hasAuthority('fichas:item-ficha-perfil:update')")`** con un único client role.
5. **NO se usa `hasRole(...)`** ni roles realm directamente en endpoints — siempre `hasAuthority(...)` con client role.

### Configuración requerida en Keycloak (instrucciones para equipo de seguridad)

Para el client role `fichas:item-ficha-perfil:update`:

1. En el cliente `arquisoft-api`: crear el client role con el nombre exacto `fichas:item-ficha-perfil:update`.
2. Asignar el client role al rol realm `estudiante`.
3. Verificar que los usuarios de prueba con rol realm `estudiante` reciban el client role en su JWT (`resource_access.arquisoft-api.roles`).

---

## 10. Eventos RabbitMQ (si aplica)

Eventos: ninguno.

Razón: CRUD interno sin consumidores conocidos ni casos de auditoría identificados — confirmado por el usuario en la pregunta 5 de FASE 3.

El use case NO inyecta `EventPublisher`.

---

## 11. Migración de Base de Datos (si aplica)

No aplica — la tabla `item` ya existe (migración `V1.3__crear_tipo_item_e_item.sql` de HU-031). No se requieren cambios al esquema.

---

## 12. Casos de Prueba Sugeridos (condicional según tipo de Use Case)

### Presupuesto orientativo

| Tipo de HU | Tests esperados |
|---|---|
| Pequeña (1 endpoint, 1 entidad) | 15 - 25 |

Esta HU es pequeña (1 endpoint, 1 entidad modificada). Presupuesto: **18-22 tests** (abajo se listan 19).

---

### Caso A — Use Case de ESCRITURA (crea, actualiza, elimina)

#### Tests capa `domain` (Aggregate Root)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `ItemFichaPerfilAggregateTest` | `debeModificarContenido_cuandoContenidoValido` | `modificarContenido(...)` actualiza solo el campo `contenido` sin modificar `tipoItem` ni `fichaPerfilId` |
| `ItemFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoContenidoVacio` | `modificarContenido("")` lanza `DomainValidationException` con `fieldErrors` conteniendo `CAMPO_CONTENIDO` |
| `ItemFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoContenidoDemasiado Largo` | `modificarContenido(cadena de 7001 caracteres)` lanza `DomainValidationException` con `fieldErrors` |
| `ItemFichaPerfilAggregateTest` | `debeLimpiarEspacios_cuandoModificarContenido` | `modificarContenido("  texto  ", EN_CONSTRUCCION)` actualiza a `"texto"` (sin espacios inicio/fin) |
| `ItemFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoEstadoFichaEsTerminal` | `@ParameterizedTest` sobre `APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA` → `DomainValidationException` con `ESTADO_FICHA_NO_MODIFICABLE`; el contenido **no** cambia |
| `ItemFichaPerfilAggregateTest` | `debeModificarContenido_cuandoEstadoFichaNoEsTerminal` | `@ParameterizedTest` sobre `EN_CONSTRUCCION`, `EN_REVISION`, `DISPONIBLE_PARA_EVALUACION` → modifica sin error |
| `ItemFichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoEstadoFichaEsNulo` | `modificarContenido(..., null)` → `DomainValidationException` con `ESTADO_FICHA_REQUERIDO` |

> **NO se testean ciclo de eventos** (la entidad NO extiende `AggregateRoot`), ni `publishEvent`, ni `drainUnPublishedEvents`. Solo se testea el método de negocio `modificarContenido(...)`.

---

#### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `ModificarItemFichaPerfilUseCaseTest` | `debeModificar_cuandoDatosValidos` | flujo exitoso completo — ítem existe, estudiante es propietario, contenido válido |
| `ModificarItemFichaPerfilUseCaseTest` | `debeLanzarItemNoEncontrado_cuandoItemNoExiste` | `itemFichaPerfilOutputPort.existsById(itemId)` retorna `false` → lanza `ItemNoEncontradoException` con código `ITEM_NO_ENCONTRADO` |
| `ModificarItemFichaPerfilUseCaseTest` | `debeLanzarItemFichaNoPropia_cuandoEstudianteNoEsPropietario` | `fichaPerfilQueryOutputPort.esEstudiantePropietario(...)` retorna `false` → lanza `ItemFichaNoPropiaException` |
| `ModificarItemFichaPerfilUseCaseTest` | `debeLanzarDomainValidation_cuandoContenidoInvalido` | `item.modificarContenido(contenidoInvalido)` lanza `DomainValidationException` con `fieldErrors` — use case la propaga |
| `ModificarItemFichaPerfilUseCaseTest` | `debeLlamarGuardar_cuandoModificacionExitosa` | `verify(itemFichaPerfilOutputPort).guardar(item)` se llamó 1 vez tras `modificarContenido` exitoso |
| `ModificarItemFichaPerfilUseCaseTest` | `debePropagarExcepcion_cuandoRepositorioFalla` | `itemFichaPerfilOutputPort.guardar(item)` lanza excepción → se propaga sin capturar |
| `ModificarItemFichaPerfilUseCaseTest` | `debePropagarDomainValidation_cuandoFichaEnEstadoTerminal` | `obtenerEstadoActual(...)` retorna `APROBADA` → el aggregate lanza `DomainValidationException`; el use case la propaga y **nunca** llama a `guardar` |
| `ModificarItemFichaPerfilUseCaseTest` | `debeLanzarFichaPerfilNoEncontrada_cuandoFichaSinEstadoRegistrado` | `obtenerEstadoActual(...)` retorna `Optional.empty()` → lanza `FichaPerfilNoEncontradaException` (400) |

> **NO se verifica `eventPublisher.publish(...)`** — la HU no emite eventos.

---

#### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `ItemFichaPerfilCommandOutputAdapterTest` | `debeRetornarTrue_cuandoItemExiste` | `existsById(uuid)` retorna `true` si el ítem existe en BD |
| `ItemFichaPerfilCommandOutputAdapterTest` | `debeRetornarFalse_cuandoItemNoExiste` | `existsById(uuidInexistente)` retorna `false` |
| `ItemFichaPerfilCommandOutputAdapterTest` | `debeGuardarCambios_cuandoModificarContenido` | `guardar(item)` persiste el cambio de contenido en BD |
| `FichaPerfilQueryOutputAdapterTest` | `debeRetornarTrue_cuandoEstudianteEsPropietario` | `esEstudiantePropietario(fichaPerfilId, estudianteId)` retorna `true` si existe registro en `estudiante_ficha_perfil` |
| `FichaPerfilQueryOutputAdapterTest` | `debeRetornarFalse_cuandoEstudianteNoEsPropietario` | `esEstudiantePropietario(fichaPerfilId, estudianteIdAjeno)` retorna `false` |
| `ModificarItemFichaPerfilInputAdapterTest` | `debe204_cuandoPeticionValida` | modificación OK → `204 No Content` sin body |
| `ModificarItemFichaPerfilInputAdapterTest` | `debe400_cuandoItemNoExiste` | `ItemNoEncontradoException` → `400` con `errorCode` |
| `ModificarItemFichaPerfilInputAdapterTest` | `debe403_cuandoEstudianteNoEsPropietario` | `ItemFichaNoPropiaException` → `403` con `errorCode` (extiende `AuthorizationException`) |
| `ModificarItemFichaPerfilInputAdapterTest` | `debe422_cuandoFichaEnEstadoTerminal` | `DomainValidationException` con `ESTADO_FICHA_NO_MODIFICABLE` → `422` con `fieldErrors` |
| `EstadoFichaPerfilCommandOutputAdapterTest` | `debeRetornarEstadoMasReciente_cuandoFichaTieneVariosEstados` | dos filas de trazabilidad → `obtenerEstadoActual(...)` retorna la de `fechaActualizacion` mayor |
| `EstadoFichaPerfilCommandOutputAdapterTest` | `debeRetornarVacio_cuandoFichaNoTieneEstados` | ficha sin trazabilidad → `Optional.empty()` |
| `ModificarItemFichaPerfilInputAdapterTest` | `debe401_cuandoNoAutenticado` | sin token → `401` |
| `ModificarItemFichaPerfilInputAdapterTest` | `debe403_cuandoRolInsuficiente` | token sin `fichas:item-ficha-perfil:update` → `403` (autorización Spring Security, no excepción de dominio) |
| `ModificarItemFichaPerfilInputAdapterTest` | `debe422_cuandoContenidoInvalido` | `DomainValidationException` con `fieldErrors` → `422` con lista de errores |

---

### Reglas de consolidación

- **NO se incluyen tests de getters/setters** generados por Lombok.
- **NO se incluyen tests de validaciones Jakarta** una por una — un solo test "rechaza request inválido" basta (ya cubierto en el test `debe422_cuandoContenidoInvalido` del controller).
- **NO se incluyen tests de métodos `private`** del aggregate — se validan implícitamente desde `modificarContenido(...)`.
- **NO se incluye test propio de excepción** `ItemNoEncontradoException` si solo hace `super("CODE", "msg")`.

---

## 13. Checklist de Implementación

- [ ] **DDD:** Entidad de dominio `ItemFichaPerfilAggregate` ya existe y NO extiende `AggregateRoot` (confirmado — es clase plana, CRUD sin eventos).
- [ ] Método de negocio `modificarContenido(String nuevoContenido)` agregado al aggregate con Notification Pattern.
- [ ] IDs siempre `UUID` (ya se usa en el aggregate existente).
- [ ] Puerto de entrada (`ModificarItemFichaPerfilInputPort`) definido, extiende `VoidInputPort<ModificarItemFichaPerfilCommand>`.
- [ ] Puerto de salida write (`ItemFichaPerfilOutputPort`) extendido con método `existsById(UUID itemId)`.
- [ ] Puerto de salida read (`FichaPerfilQueryOutputPort`) extendido con método `esEstudiantePropietario(UUID fichaPerfilId, UUID estudianteId)`.
- [ ] Excepciones de application (`ItemNoEncontradoException`) definidas en `application/itemfichaperfil/exception/`, extienden `ApplicationException` con `errorCode`.
- [ ] `ItemFichaNoPropiaException` ya existe de HU-031 — se reutiliza.
- [ ] **Cada excepción nueva extiende la clase base correcta** (`ItemNoEncontradoException` → `ApplicationException` → 400) para que `GlobalAppExceptionHandler` de `shared:web` resuelva su HTTP automáticamente. **NO se crea handler de contexto** — no hay colisión de nombres ni HTTP status custom.
- [ ] `Command` (`record` en `application/itemfichaperfil/command/model/`) y `RequestDTO` (`record` en `infrastructure/itemfichaperfil/command/adapter/in/web/dto/`) creados. `RequestDTO` con anotaciones Jakarta + método `toCommand(UUID itemId, UUID estudianteId)`. Campos en español idénticos al aggregate.
- [ ] Caso de uso (`ModificarItemFichaPerfilUseCase`) con `@RequiredArgsConstructor`, sin `@Transactional` (uso manual en el implementador), sin drenado de eventos (no hay eventos).
- [ ] Controller REST con `@Valid @RequestBody` y autorización vía `@PreAuthorize("hasAuthority('fichas:item-ficha-perfil:update')")` **en kebab-case** — client role declarado en sección 9 del plan.
- [ ] Controller documentado con `@Tag`, `@Operation`, `@ApiResponses` y `@SecurityRequirement` (ADR-011).
- [ ] Entidad JPA `ItemFichaPerfilJpaEntity` y adaptador `ItemFichaPerfilCommandOutputAdapter` ya existen de HU-031 — se extienden con métodos nuevos.
- [ ] NO se crea migración Flyway — la tabla `item` ya existe (V1.3 de HU-031).
- [ ] NO se publican eventos RabbitMQ — la HU no emite eventos.
- [ ] Tests unitarios con patrón AAA (cobertura ≥ 75%), **SIN tests de ciclo de eventos** del Aggregate Root (no extiende `AggregateRoot`), **SIN** verificación de `eventPublisher.publish(...)` (no hay eventos).
- [ ] Tests de repositorio con H2 o Testcontainers.
- [ ] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4.x).
- [ ] Constantes de mensajes, códigos y logs agregadas a `shared:message` (`FichasMessages.ItemFichaPerfil`).
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot).
- [ ] Commit: `feat(fichas): modificar contenido de ítem de ficha de perfil`

---

## 14. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-07-03 | Build -x test: sin errores. Test existente `FichaPerfilQueryOutputAdapterTest` corregido (constructor con nuevo parámetro). |
| Tests      | @tester             | ✅ Completado | 2026-07-03 | 19 tests generados/ampliados (domain 4, application 6, infrastructure 9). Fix aplicado: `ModificarItemFichaPerfilInputAdapterTest` inyectaba `ObjectMapper` (no disponible en el slice `@WebMvcTest`) — reemplazado por JSON literal, igual que `AgregarItemFichaPerfilInputAdapterTest`. `./gradlew :fichas:domain:test :fichas:application:test :fichas:infrastructure:test` y checkstyleTest en verde. |
| Validación | @validator-analyze  | ✅ Completado | 2026-07-06 | Score: 100/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-07-06 | /.workspace/validator/validator-HU-033.md |
| Commit     | @commit             | ✅ Completado | 2026-07-06 | Hash: 5afa926 |

### Revisión post-auditoría (2026-07-09)

> La auditoría batch de arquitectura detectó que el **criterio de aceptación #6 nunca se implementó**: `ModificarItemFichaPerfilUseCase` no validaba el estado de la ficha. Se implementa aplicando Tell, Don't Ask (regla en `EstadoFicha` + decisión en `ItemFichaPerfilAggregate`), y se corrige el criterio #3 a 403 tras la introducción de `AuthorizationException`.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Planificación | @planificador    | ✅ Completado | 2026-07-09 | Criterios #3 (403) y #6 (422) corregidos; criterio #7 nuevo. Sección "Tell, Don't Ask" añadida. |
| Desarrollo | @implementador      | ✅ Completado | 2026-07-09 | `EstadoFicha.esTerminal()/permiteModificacion()`; `modificarContenido(contenido, estadoFichaActual)`; puerto `obtenerEstadoActual` + adapter + query derivada. Build y checkstyle en verde. |
| Tests      | @tester             | ✅ Completado | 2026-07-09 | +7 tests (domain 3 incl. 2 `@ParameterizedTest`, application 2, infrastructure 3). Suite `fichas` completa en verde. |
| Validación | @validator-analyze  | ✅ Completado | 2026-07-09 | Sin fugas de lógica: el use case no ramifica sobre el estado. Ver validator-HU-033.md |
