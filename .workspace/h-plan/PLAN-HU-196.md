# PLAN: Modificar Revisión Ítem

## Metadata
- **ID Historia:** HU-196
- **Bounded Context:** `fichas`
- **Tipo de Use Case:** Escritura
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`, `shared:message`
- **Fecha de plan:** 2026-08-28
- **Rama:** `feature/HU-196-modificar_revision_item` — **ya creada** por el orquestador a partir de
  `feature/HU-195-agregar_revision_item` (commit `a0afbe8c`, ya mergeado con `develop`). No se
  requiere ningún paso de merge/rebase: el implementador trabaja directo sobre esta rama.
- **Fuentes consultadas:**
  - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md` (HU196, Sprint 3)
  - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md` (sección "Revision Item"
    → comando "Modificar Revision Item")
  - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    (§16 Revision Item)
  - `mer/03_tablas_fichas_perfil.sql` (tablas `revision_item`, `estado_revision`)
  - `mer/data/03_data_fichas_perfil.sql` (catálogo `estado_revision`)
  - Código real (rama actual): `RevisionItemDomain`, `AgregacionRevisionItemDomain`,
    `AgregarRevisionItem*` (cadena completa HU-195), `CambiarAsesorFicha*` (patrón de retorno 204 +
    actualización parcial vía `OutputPort`), `ModificarItemFichaPerfil*` (patrón de objeto de acción
    `Modificacion*Domain` + dueño vía JWT), `FichasAuthorities`, `FichasApiMessages`, `FichasCodes`,
    `FichasFields`, `FichasLimits`, `RevisionItemKey`, `EstadoRevision`, `rutas-fichas.yml`,
    `catalogo/fichas.properties`, `InteractoresFichasTest`
- **Observaciones del usuario:**
  - Rama ya recreada por el orquestador desde `feature/HU-195-agregar_revision_item`; todo lo de esa
    HU (`RevisionItemDomain`, `AgregacionRevisionItemDomain`, `EstadoRevision`,
    `AsesorFichaPropietarioRule`, etc.) se trata como código YA EXISTENTE.
  - El evento se emite (lado productor completo), pero **NO** se implementan las 6 piezas del lado
    `notificaciones` (cola/binding, `Payload`, `Consumer`, `TipoNotificacion`, `PlantillaKey`) — queda
    explícitamente fuera de alcance de esta HU.
  - Retorno del endpoint: 204 sin body (no hay indicación explícita en la documentación; se usa el
    patrón `CambiarAsesorFichaController`).
  - Ruta: `PATCH /fichas-perfil/items/{itemId}/revisiones` — sin `revisionItemId` en la URL (modelo
    1:1, una revisión por ítem).
  - Cualquier valor válido del catálogo `EstadoRevision` es un destino aceptado (sin máquina de
    estados restringida); modificar al mismo valor que ya tiene es un no-op válido.
  - `RevisionItem-POL-02` (no duplicados) **NO** aplica a este flujo — es la regla de unicidad de la
    creación, no de la modificación de la fila ya existente.

## 1. Resumen Funcional

Permite al Asesor Ficha modificar el `EstadoRevision` de la revisión ya existente de un ítem de una
ficha de perfil que él mismo asesora. La revisión se direcciona por `itemId` (no por su propio id,
dado el modelo 1:1 "una revisión por ítem" ya vigente desde HU-195). No cubre: crear una revisión
nueva (HU-195, `Agregar Revision Item`), modificar el `item` asociado o la `fechaCreacion` (el MER
los marca no modificables), ni las observaciones del ítem (tabla `observacion_item`, fuera de esta
HU). No requiere migración Flyway: reutiliza `revision_item` y `estado_revision`, creadas en la
migración de HU-195. El evento de dominio se publica (lado productor completo); la reacción del
contexto `notificaciones` queda fuera de alcance por decisión explícita del usuario — ver nota en
Sección 10.

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|---|---|
| 1 | El Asesor Ficha dueño de la ficha del ítem modifica el estado de la revisión existente a un valor válido del catálogo `EstadoRevision` | 204 sin body; la fila de `revision_item` queda con el nuevo `estado_revision_id` |
| 2 | El nuevo estado es igual al que ya tenía la revisión | 204 sin body — no-op válido, sin error |
| 3 | No existe una revisión de `EstadoRevision` para el `itemId` dado (nunca se agregó, o el ítem no existe) | 422 `RevisionItemNoEncontradaException` |
| 4 | El Asesor Ficha autenticado no es el asesor de la ficha a la que pertenece el ítem | 422 `FichaNoPerteneceAsesorException` |
| 5 | El `estadoRevision` enviado no es un valor del catálogo `EstadoRevision` | 422 (acumulado en `DomainValidationException` vía `ModificacionRevisionItemDomain`) |
| 6 | `estadoRevision` viene vacío, en blanco o excede `FichasLimits.RevisionItem.ESTADO_MAX` (50) | 400 `ApplicationValidationException` (formato, desde `ModificarRevisionItemCommand.crear`) |
| 7 | Usuario sin el client role `fichas:revision-item:update` | 403 |
| 8 | Usuario no autenticado | 401 |

## 3. Reglas de Negocio

> Invariante LOCAL (formato, longitud, obligatoriedad, pertenencia al catálogo) → dentro del objeto
> de acción `ModificacionRevisionItemDomain`, acumulado en `ValidationResult` → 422 con
> `fieldErrors[]`. Restricción de CONJUNTO (existencia contra BD, propiedad) → `Rule` de dominio
> alimentada por lo que el `UseCase` ya consultó, orquestada por `ModificarRevisionItemValidator` →
> 422 con su propia `DomainException`. **Nunca `if/throw` en el use case.**

| # | Regla | Dónde se valida | Finder que trae el dato | Excepción → HTTP |
|---|---|---|---|---|
| 1 | `item`, `estadoRevision`, `asesorFicha` obligatorios; `estadoRevision` ≤ 50 caracteres | `ModificarRevisionItemCommand.crear(...)` (formato) | — | `ApplicationValidationException` → 400 |
| 2 | `estadoRevision` debe pertenecer al catálogo `EstadoRevision` | `ModificacionRevisionItemDomain.crear(...)` (mismo patrón que `RevisionItemDomain.setEstadoRevision`) | — (enum estático, sin I/O) | `DomainValidationException` (fieldErrors) → 422 |
| 3 | Debe existir una revisión ya registrada para ese `item` | `RevisionItemExisteRule` (NUEVA) sobre `ExistenciaRevisionItem(item, existe)` | `RevisionesDelItemFinder` (existente, reusado — `obtener(item) > 0`) | `RevisionItemNoEncontradaException` (NUEVA) → 422 |
| 4 | El asesor autenticado debe ser el dueño de la ficha del ítem | `AsesorFichaPropietarioRule` (existente, reusada) sobre `PropiedadAsesorFicha(fichaPerfil, asesorFicha, esPropietario)` | `FichaPerfilDelItemFinder` + `FichaPerfilFinder` (existentes, reusados) | `FichaNoPerteneceAsesorException` (existente) → 422 |
| 5 | Modificar al mismo valor que ya tiene la revisión es válido (no-op) | No hay Rule — la actualización SQL es idempotente ante el mismo valor | — | — |
| 6 | `RevisionItem-POL-02` (no duplicados) | **No aplica a este flujo.** Es la unicidad de la creación (HU-195, `RevisionItemNoDuplicadaRule`); modificar actualiza la fila ya existente, no crea una nueva. Confirmado por el usuario — no se instancia esa Rule aquí | — | — |

Orden de ejecución en el `Validator` (la de existencia primero, para que la de propiedad confíe en
que ya se lanzó si la revisión no existe): `RevisionItemExisteRule` → `AsesorFichaPropietarioRule`.

No se valida `EstadoFichaPerfilEnTerminalRule`: el comando hermano de esta misma feature,
`Agregar Revision Item`, tampoco la aplica (ver `AgregarRevisionItemValidatorImpl`), y el usuario no
la mencionó al confirmar las reglas de negocio de la Sección 3 de preguntas. Se mantiene consistencia
dentro de la feature `revisionitem` en vez de copiar el validator de la feature `itemfichaperfil`
(que sí la aplica, por una razón propia de esa feature).

## 4. Modelo DDD del Contexto

### Entidad raíz

`RevisionItemDomain` — **ya existe** (HU-195), no se modifica. Este flujo no la reconstruye ni la
crea: el `UseCase` actualiza el campo `estado_revision_id` directamente vía `OutputPort`, siguiendo
el mismo patrón que `CambiarAsesorFichaUseCaseImpl.ejecutar(...)` usa sobre `FichaPerfilDomain`
(actualización parcial sin pasar por `crear`/`reconstruir`).

### Objeto de acción

- **Clase:** `ModificacionRevisionItemDomain` (nominalización de "Modificar", junto al agregado, sin
  subpaquete — mismo patrón que `ModificacionItemFichaPerfilDomain` y `ModificacionFichaPerfilDomain`)
- **Atributos:** `item: UUID`, `estadoRevision: EstadoRevision`, `asesorFicha: UUID`
- **Validación propia (setters privados, mismo patrón que `RevisionItemDomain.setEstadoRevision`):**
  `item` no nulo; `estadoRevision` no en blanco + longitud máxima 50 + pertenencia al catálogo
  (`EstadoRevision.esValido`/`desde`); `asesorFicha` no nulo.
- Lo construye `ModificarRevisionItemMapper` (`command/primaryport/mapper/`) a partir del `Command`.

### Atributos por objeto de dominio

No se agrega ningún objeto de dominio nuevo con tabla propia — `RevisionItemDomain` y
`EstadoRevision` ya existen con sus atributos documentados en HU-195. `ModificacionRevisionItemDomain`
no persiste por sí mismo (no tiene tabla ni `Entity`): es un objeto de acción interno.

**Combinaciones únicas:** ninguna nueva — la de `revision_item` (`item_id`, `fecha_creacion`) ya
existe y no la toca este flujo, porque `fecha_creacion` no se modifica.

### Eventos de Dominio

| Evento | Clase | `EVENT_TOPIC` | Consumidor | Cuándo |
|---|---|---|---|---|
| Revisión de ítem modificada | `RevisionItemModificadoEvent` (NUEVO, `domain/revisionitem/event/`) | `fichas.revision_item.modificado` | `notificaciones` (consumidor **NO** implementado en esta HU — ver Sección 10) | Tras persistir el nuevo `estadoRevision` |

**Publicación:** directa desde `ModificarRevisionItemUseCaseImpl` tras `revisionItemOutputPort.actualizarEstado(...)`,
vía `EventPublisher` inyectado — `eventPublisher.publish(new RevisionItemModificadoEvent(...))`. Sin
acumulación en el agregado (es plano, como manda la convención).

## 5. Integraciones Externas

Ninguna — no hay sistema externo distinto de PostgreSQL/RabbitMQ.

## 6. Árbol de Archivos a Crear / Modificar

| Capa | Ruta completa desde raíz del repo | Tipo | Responsabilidad |
|---|---|---|---|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/ModificacionRevisionItemDomain.java` | **NUEVO** | Objeto de acción — valida `item`, `estadoRevision` (formato + catálogo), `asesorFicha` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/model/ExistenciaRevisionItem.java` | **NUEVO** | `record(UUID item, boolean existe)` — entrada de `RevisionItemExisteRule` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/rules/RevisionItemExisteRule.java` | **NUEVO** | Interfaz `DomainRule<ExistenciaRevisionItem>` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/rules/impl/RevisionItemExisteRuleImpl.java` | **NUEVO** | Lanza `RevisionItemNoEncontradaException` si `!existe` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/exception/RevisionItemNoEncontradaException.java` | **NUEVO** | `DomainException` → 422 |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/event/RevisionItemModificadoEvent.java` | **NUEVO** | `DomainEvent`, `EVENT_TOPIC = "fichas.revision_item.modificado"` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/primaryport/model/ModificarRevisionItemCommand.java` | **NUEVO** | `record` + `crear(...)` (formato) |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/primaryport/mapper/ModificarRevisionItemMapper.java` | **NUEVO** | `Command` → `ModificacionRevisionItemDomain` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/primaryport/interactor/ModificarRevisionItemInteractor.java` | **NUEVO** | `VoidInteractor<ModificarRevisionItemCommand>` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/primaryport/interactor/impl/ModificarRevisionItemInteractorImpl.java` | **NUEVO** | `@Transactional(transactionManager = "fichasTransactionManager")` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/usecase/ModificarRevisionItemUseCase.java` | **NUEVO** | `VoidUseCase<ModificacionRevisionItemDomain>` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/usecase/impl/ModificarRevisionItemUseCaseImpl.java` | **NUEVO** | Orquesta finders → validator → `actualizarEstado` → evento → log |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/validator/ModificarRevisionItemValidator.java` | **NUEVO** | Interfaz |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/validator/impl/ModificarRevisionItemValidatorImpl.java` | **NUEVO** | Puro, `new` de las 2 Rules en constructor sin argumentos |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/secondaryport/RevisionItemOutputPort.java` | MODIFICAR | Agrega `void actualizarEstado(UUID item, String estadoRevision)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/command/primaryadapter/web/ModificarRevisionItemController.java` | **NUEVO** | `PATCH /fichas-perfil/items/{itemId}/revisiones`, 204 |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/command/primaryadapter/web/dto/ModificarRevisionItemRequestDTO.java` | **NUEVO** | `record(String estadoRevision)`, sin anotaciones |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/command/primaryadapter/web/mapper/ModificarRevisionItemRequestMapper.java` | **NUEVO** | `toCommand(dto, itemId, asesorFichaId)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/command/secondaryadapter/repository/RevisionItemCommandRepository.java` | MODIFICAR | Agrega `@Modifying @Query actualizarEstadoRevision(...)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/command/secondaryadapter/repository/RevisionItemCommandOutputAdapter.java` | MODIFICAR | Implementa `actualizarEstado`; agrega `AppLogger` al constructor (ver nota Sección 7) |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/security/FichasAuthorities.java` | MODIFICAR | Agrega `REVISION_ITEM_UPDATE` + `HAS_REVISION_ITEM_UPDATE` |
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/constant/FichasCodes.java` | MODIFICAR | Agrega `RevisionItem.NO_ENCONTRADA` |
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/key/fichas/RevisionItemKey.java` | MODIFICAR | Agrega `ERROR_NO_ENCONTRADA` (1 parámetro), `LOG_MODIFICADO` (1 parámetro) |
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/annotation/FichasApiMessages.java` | MODIFICAR | Agrega `RevisionItem.MODIFICAR_*` (Swagger) |
| catálogo | `catalogo/fichas.properties` | MODIFICAR | Agrega los 2 textos de `RevisionItemKey` nuevos |
| test wiring | `fichas/application/src/test/java/com/arquisoft/fichas/application/InteractoresFichasTest.java` | MODIFICAR | Agrega `ModificarRevisionItemInteractorImpl.class` a la lista parametrizada |

No hay fila de `rutas-fichas.yml`: la propiedad `rutas.fichas.fichas-perfil.item-revisiones` ya
existe (creada en HU-195 para el `POST` de `AgregarRevisionItemController`) y este `PATCH` la
reutiliza tal cual.

## 7. Detalle por Archivo

**`ModificacionRevisionItemDomain`** (`domain/revisionitem/`) — clase final, constructor privado,
`crear(UUID item, String estadoRevision, UUID asesorFicha)` estático. Setters privados que cortan con
`return` al fallar: `setItem` (`ValidatorObjeto.noNulo`), `setEstadoRevision` (`ValidatorTexto.noEnBlanco`
→ `ValidatorLongitud.longitudMaxima(FichasLimits.RevisionItem.ESTADO_MAX)` → `EstadoRevision.esValido`
con `result.agregarError(...)` manual si falla, igual que `RevisionItemDomain.setEstadoRevision`),
`setAsesorFicha` (`ValidatorObjeto.noNulo`). Cierra con `result.lanzarSiTieneErrores()` → 422 con
`fieldErrors[]`. Getters: `getItem()`, `getEstadoRevision()` (retorna `EstadoRevision`), `getAsesorFicha()`.

**`ExistenciaRevisionItem`** — `record(UUID item, boolean existe)`.

**`RevisionItemExisteRule`/`Impl`** — `validar(ExistenciaRevisionItem existencia)`: si
`!existencia.existe()`, lanza `new RevisionItemNoEncontradaException(existencia.item())`. Sin
dependencias de constructor.

**`RevisionItemNoEncontradaException`** — `DomainException`, mensaje
`Mensajes.formatear(RevisionItemKey.ERROR_NO_ENCONTRADA, item)`, código
`FichasCodes.RevisionItem.NO_ENCONTRADA`.

**`RevisionItemModificadoEvent`** — `EVENT_TOPIC = "fichas.revision_item.modificado"`,
`EVENT_TYPE = "RevisionItemModificadoEvent"`. Campos: `itemId` (UUID), `estadoRevisionId` (String, el
`.getId()` del nuevo `EstadoRevision`), `estadoRevisionNombre` (String, el `.getNombre()`). No incluye
`revisionItemId`: el flujo entero direcciona por `itemId` (no hay `revisionItemId` en el `Command`,
consistente con la decisión de ruta del usuario), y no se agrega una consulta adicional solo para
obtenerlo cuando nada en el alcance de esta HU lo necesita.

**`ModificarRevisionItemCommand`** (`application/.../primaryport/model/`) — `record(UUID item, String
estadoRevision, UUID asesorFicha)` con constructor compacto que aplica `UtilTexto.aplicarTrim` a
`estadoRevision`. `crear(...)` valida solo formato (mismo nivel que `AgregarRevisionItemCommand`):
`item` no nulo, `estadoRevision` no en blanco + longitud máxima, `asesorFicha` no nulo →
`result.lanzarSiTieneErroresDeEntrada()` → `ApplicationValidationException` (400). La pertenencia al
catálogo se revalida después, en `ModificacionRevisionItemDomain` (422) — misma redundancia
deliberada que ya existe entre `AgregarRevisionItemCommand` y `RevisionItemDomain`.

**`ModificarRevisionItemMapper`** — `toDomain(command)` → `ModificacionRevisionItemDomain.crear(command.item(),
command.estadoRevision(), command.asesorFicha())`.

**`ModificarRevisionItemInteractor`/`Impl`** — `VoidInteractor<ModificarRevisionItemCommand>`;
`@Transactional(transactionManager = "fichasTransactionManager")` en `ejecutar`; delega a
`ModificarRevisionItemUseCase.ejecutar(ModificarRevisionItemMapper.toDomain(command))`.

**`ModificarRevisionItemUseCase`** — `VoidUseCase<ModificacionRevisionItemDomain>`.

**`ModificarRevisionItemUseCaseImpl`** — `@Component`, `@RequiredArgsConstructor`. Inyecta:
`RevisionesDelItemFinder` (existente, reusado para el conteo → `existe`), `FichaPerfilDelItemFinder`
(existente), `FichaPerfilFinder` (existente), `ModificarRevisionItemValidator`, `RevisionItemOutputPort`,
`EventPublisher`, `AppLogger`. Flujo `ejecutar(entrada)`: `revisionExiste = revisionesDelItemFinder.obtener(entrada.getItem()) > 0`;
`fichaPerfilDelItem = fichaPerfilDelItemFinder.obtener(entrada.getItem())`; `fichaPerfil =
fichaPerfilDelItem.orElse(UtilUUID.obtenerUUIDPorDefecto())`; `esPropietario =
fichaPerfilDelItem.flatMap(fichaPerfilFinder::obtener).map(f -> f.getAsesorFicha().equals(entrada.getAsesorFicha())).orElse(false)`;
`modificarRevisionItemValidator.validar(entrada, revisionExiste, fichaPerfil, esPropietario)`;
`revisionItemOutputPort.actualizarEstado(entrada.getItem(), entrada.getEstadoRevision().getId())`;
publica `RevisionItemModificadoEvent`; log info `RevisionItemKey.LOG_MODIFICADO`. Idéntico patrón de
resolución de dependencias que `AgregarRevisionItemUseCaseImpl`.

**`ModificarRevisionItemValidator`/`Impl`** — puro, constructor sin argumentos:
`this.revisionItemExisteRule = new RevisionItemExisteRuleImpl(); this.asesorFichaPropietarioRule = new
AsesorFichaPropietarioRuleImpl();`. `validar(entrada, revisionExiste, fichaPerfil, esPropietario)`
invoca en orden: `revisionItemExisteRule.validar(new ExistenciaRevisionItem(entrada.getItem(), revisionExiste))`
→ `asesorFichaPropietarioRule.validar(new PropiedadAsesorFicha(fichaPerfil, entrada.getAsesorFicha(), esPropietario))`.
Cero `if`.

**`RevisionItemOutputPort`** (MODIFICAR) — agrega `void actualizarEstado(UUID itemId, String
estadoRevision);` a la interfaz existente (junto a `registrarRevision`, `contarPorItem`).

**`ModificarRevisionItemController`** — `@RestController`, mismo `@RequestMapping` base que
`AgregarRevisionItemController` (`${rutas.fichas.fichas-perfil.base:/fichas-perfil}`), mismo `@Tag`
(`FichasApiMessages.RevisionItem.TAG_NAME/TAG_DESCRIPTION` — reusa el tag existente, no crea uno
nuevo). `@PatchMapping("${rutas.fichas.fichas-perfil.item-revisiones:/items/{itemId}/revisiones}")`.
`@PreAuthorize(FichasAuthorities.Expresiones.HAS_REVISION_ITEM_UPDATE)`. `@Operation` con
`summary`/`description` = `FichasApiMessages.RevisionItem.MODIFICAR_SUMMARY/MODIFICAR_DESCRIPTION`,
`security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH)`. `@ApiResponses`: 204
(`MODIFICAR_RESP_204`), 400 (`MODIFICAR_RESP_400`), 401 (`FichasApiMessages.Comun.RESP_401`), 403
(`MODIFICAR_RESP_403`), 422 (`MODIFICAR_RESP_422`). Método `modificar(@PathVariable UUID itemId,
@RequestBody ModificarRevisionItemRequestDTO dto, @AuthenticationPrincipal Jwt jwt)`: extrae
`asesorFichaId = UUID.fromString(jwt.getSubject())` (nunca del body — el ítem tiene dueño, pregunta
12), llama al interactor, retorna `ResponseEntity.noContent().build()`.

**`ModificarRevisionItemRequestDTO`** — `record(String estadoRevision)`, sin anotaciones (mismo
shape que `AgregarRevisionItemRequestDTO`).

**`ModificarRevisionItemRequestMapper`** — `toCommand(dto, itemId, asesorFichaId)` →
`ModificarRevisionItemCommand.crear(itemId, dto.estadoRevision(), asesorFichaId)`.

**`RevisionItemCommandRepository`** (MODIFICAR) — agrega, junto a `countByItemId`:
```
@Modifying(clearAutomatically = true)
@Query("UPDATE RevisionItemJpaEntity r SET r.estadoRevision = :estadoRevision WHERE r.itemId = :item")
int actualizarEstadoRevision(@Param("item") UUID item, @Param("estadoRevision") EstadoRevisionJpaEntity estadoRevision);
```
Mismo patrón exacto que `FichaPerfilCommandRepository.actualizarAsesorFicha`.

**`RevisionItemCommandOutputAdapter`** (MODIFICAR) — implementa `actualizarEstado(itemId,
estadoRevision)`: `repository.actualizarEstadoRevision(itemId, EstadoRevisionJpaMapper.toReferencia(estadoRevision));`
seguido de `logger.debug(Mensajes.obtener(RevisionItemKey.LOG_MODIFICADO), itemId);`. **Nota de
consistencia:** este adaptador hoy no inyecta `AppLogger` (deviación de HU-195 respecto al estándar
de `FichaPerfilCommandOutputAdapter`, que sí loguea cada método de escritura). Como esta HU agrega un
método de escritura nuevo al mismo adaptador y CLAUDE.md prohíbe un método de escritura sin log, el
plan agrega `AppLogger` al constructor (`@RequiredArgsConstructor`) y — para no dejar el archivo con
un método logueado y otro no — también agrega el log correspondiente a `registrarRevision` (ya
existente), usando la clave `RevisionItemKey.LOG_AGREGADO` que ya existe en el catálogo (2 parámetros:
`revision.id()`, `revision.item()`) pero que hoy nadie invoca desde el adaptador — el `UseCase` la usa
directo. Verificar en implementación si `AgregarRevisionItemUseCaseImpl` sigue logueando lo mismo por
su cuenta (no debe duplicarse); si es así, dejar el log solo en el `UseCase` y no repetirlo aquí.

**`FichasAuthorities`** (MODIFICAR) — agrega:
```java
public static final String REVISION_ITEM_UPDATE = "fichas:revision-item:update";
```
y en `Expresiones`:
```java
public static final String HAS_REVISION_ITEM_UPDATE =
        HAS_AUTHORITY_INICIO + REVISION_ITEM_UPDATE + HAS_AUTHORITY_FIN;
```

**`FichasCodes.RevisionItem`** (MODIFICAR) — agrega `public static final String NO_ENCONTRADA =
"REVISION_ITEM_NO_ENCONTRADA";`.

**`RevisionItemKey`** (MODIFICAR) — agrega:
```java
ERROR_NO_ENCONTRADA("fichas.dominio.revisionitem.error.no-encontrada", 1),
LOG_MODIFICADO("fichas.aplicacion.revisionitem.log.modificado", 1);
```
(conservando `ERROR_ESTADO_NO_ENCONTRADO`, `ERROR_YA_EXISTE`, `LOG_AGREGADO` existentes).

**`FichasApiMessages.RevisionItem`** (MODIFICAR) — agrega, junto a las constantes de `AGREGAR_*`:
```java
public static final String MODIFICAR_SUMMARY = "Modificar revisión de un ítem";
public static final String MODIFICAR_DESCRIPTION =
        "Permite al asesor asignado a la ficha modificar el estado de la revisión existente de un "
                + "ítem de esa ficha, con un estado del catálogo EstadoRevision.";
public static final String MODIFICAR_RESP_204 = "Revisión modificada exitosamente";
public static final String MODIFICAR_RESP_400 = "Datos inválidos";
public static final String MODIFICAR_RESP_403 = "Sin permiso para modificar revisiones";
public static final String MODIFICAR_RESP_422 =
        "No existe una revisión para el ítem, la ficha no está asesorada por el usuario autenticado, "
                + "o el estado de revisión no es válido";
```

**`catalogo/fichas.properties`** (MODIFICAR) — agrega junto a las líneas de `revisionitem`:
```properties
fichas.dominio.revisionitem.error.no-encontrada=No existe una revisión registrada para el ítem %s
fichas.aplicacion.revisionitem.log.modificado=Revisión modificada — itemId={}
```

**`InteractoresFichasTest`** (MODIFICAR) — agrega el import de `ModificarRevisionItemInteractorImpl`
y la entrada `ModificarRevisionItemInteractorImpl.class` a la lista `@MethodSource`/`@ValueSource`
que verifica `@Transactional` en todo interactor de comando.

## 8. Endpoints REST

| Método | Ruta (sin `/api`) | Request | Response | HTTP | Client role | Swagger |
|---|---|---|---|---|---|---|
| PATCH | `/fichas-perfil/items/{itemId}/revisiones` (placeholder `rutas.fichas.fichas-perfil.item-revisiones`, ya existente) | `ModificarRevisionItemRequestDTO(String estadoRevision)` | — (sin body) | 204 / 400 / 401 / 403 / 422 | `fichas:revision-item:update` | `@Tag` reusa `RevisionItem.TAG_NAME`; `@Operation` con `MODIFICAR_SUMMARY/DESCRIPTION` |

## 9. Seguridad y Autorización (Keycloak)

| Client role | Roles realm que lo poseen | Endpoint(s) | Descripción |
|---|---|---|---|
| `fichas:revision-item:update` (NUEVO) | `asesor-ficha` (mismo actor que `fichas:revision-item:create`, HU-195 — Actor de la HU es "Asesor Ficha") | `PATCH /fichas-perfil/items/{itemId}/revisiones` | Permite modificar el estado de una revisión de ítem ya existente |

La asignación de `asesor-ficha` → `fichas:revision-item:update` se hace en la configuración del realm
de Keycloak (fuera del repositorio de código); el plan solo declara el client role y su expresión SpEL.

## 10. Eventos RabbitMQ

| Dirección | Exchange | Routing Key | Payload | Contexto receptor |
|---|---|---|---|---|
| Publica | exchange de dominio de `fichas` (`shared:amqp`) | `fichas.revision_item.modificado` | `RevisionItemModificadoEvent(itemId, estadoRevisionId, estadoRevisionNombre)` | `notificaciones` (consumidor no implementado en esta HU) |

**Nota de alcance — decisión explícita del usuario:** esta HU implementa únicamente el lado
productor (fila 1 de la tabla de las 6 piezas de "Transición de estado ⇒ notificación" de
`arquisoft-arquitectura`): la clase de evento y su publicación desde el `UseCase`. Las 5 piezas
restantes del lado `notificaciones` — `Queue`+`Binding` en `NotificacionesFichasQueueConfig`,
`RevisionItemModificadoPayload`, `RevisionItemModificadoConsumer`, la constante nueva en
`TipoNotificacion`, y el par `PlantillaKey.ASUNTO_*`/`CUERPO_*` — **no se implementan aquí**. El
evento queda publicado en el exchange sin binding: no falla nada (RabbitMQ solo lo descarta al no
haber cola escuchando esa routing key), pero tampoco envía ningún correo hasta que una HU/HT
posterior agregue esas 5 piezas. Si se decide que este flujo sí debe notificar antes de cerrar el
sprint, requiere una HU/HT de seguimiento explícita — no se agrega por iniciativa en esta.

## 11. Migración de Base de Datos

Ninguna. Reutiliza `revision_item` y `estado_revision`, creadas en la migración de HU-195
(`db/migration/fichas/`, ya aplicada). Esta HU solo hace `UPDATE` sobre filas existentes de
`revision_item` — no agrega columnas, tablas ni constraints.

## 12. Casos de Prueba Sugeridos

Tamaño: Pequeña (1 endpoint, actualización parcial de 1 entidad ya existente) → 20-28 tests
esperados.

**Domain:**
- `ModificacionRevisionItemDomainTest`: `crear` válido; `fieldErrors[]` acumulados para `item` nulo,
  `estadoRevision` en blanco, `estadoRevision` > 50 caracteres, `estadoRevision` no perteneciente al
  catálogo, `asesorFicha` nulo; varios inválidos a la vez acumulan varios `fieldErrors`.
- `RevisionItemExisteRuleImplTest`: `existe = true` no lanza; `existe = false` lanza
  `RevisionItemNoEncontradaException` con el `item` correcto en el mensaje.
- `RevisionItemModificadoEventTest`: construcción, `EVENT_TOPIC` con formato `fichas.revision_item.modificado`,
  getters.

**Application:**
- `ModificarRevisionItemCommandTest`: `crear` válido con trim de `estadoRevision`; acumulación de
  errores de formato (item nulo, estado en blanco/demasiado largo, asesor nulo) → `ApplicationValidationException`.
- `ModificarRevisionItemMapperTest`: `toDomain` delega correctamente en `ModificacionRevisionItemDomain.crear`.
- `ModificarRevisionItemValidatorTest`: caso feliz (ambas Rules reales pasan); revisión no existe →
  lanza `RevisionItemNoEncontradaException` y no llega a evaluar propiedad; no es propietario → lanza
  `FichaNoPerteneceAsesorException`; orden verificado forzando ambas a fallar y comprobando cuál
  excepción sale.
- `ModificarRevisionItemUseCaseImplTest`: flujo exitoso (persiste vía `actualizarEstado`, publica
  `RevisionItemModificadoEvent` con los datos correctos, logea); mismo valor que el actual = flujo
  exitoso igual (no-op, sin rama especial); revisión no existe → 422 sin llamar a `actualizarEstado`
  ni publicar evento; no propietario → 422 sin persistir ni publicar; verifica orden de invocación de
  finders y validator con `InOrder` de Mockito.
- `ModificarRevisionItemInteractorImplTest`: delega en el `UseCase` con el objeto de dominio mapeado;
  `@Transactional` presente (cubierto también por `InteractoresFichasTest`).

**Infrastructure:**
- `RevisionItemCommandRepositoryTest` (`@DataJpaTest`): `actualizarEstadoRevision` cambia
  efectivamente `estado_revision_id` de la fila sembrada con `TestEntityManager`; no afecta otras
  filas.
- `RevisionItemCommandOutputAdapterTest`: `actualizarEstado` delega en el repositorio con la
  referencia `EstadoRevisionJpaMapper.toReferencia(...)` correcta y logea.
- `ModificarRevisionItemRequestMapperTest`: `toCommand` arma el `Command` esperado.
- `ModificarRevisionItemControllerTest` (`@WebMvcTest`): 204 caso feliz; 400 `estadoRevision` en
  blanco; 401 sin JWT; 403 sin el client role `fichas:revision-item:update`; 422 revisión no
  encontrada; 422 no propietario; 422 estado no perteneciente al catálogo.

## 13. Checklist de Implementación

- [ ] `ModificacionRevisionItemDomain`: constructor privado, setters privados que cortan con `return`,
      solo getters, sin Lombok, sin subcarpeta — mismo patrón que `ModificacionItemFichaPerfilDomain`
- [ ] Validación de catálogo de `estadoRevision` replicada exactamente como
      `RevisionItemDomain.setEstadoRevision` (mismo código de error, misma clave de mensaje)
- [ ] `RevisionItemExisteRule`/`Impl`: sin dependencias de constructor, no es bean
- [ ] `ModificarRevisionItemValidator`/`Impl`: constructor sin argumentos con `new` de las 2 Rules,
      cero `if`, orden existencia → propiedad
- [ ] `RevisionItem-POL-02` (no duplicados) **NO** instanciada en este validator — confirmado con el
      usuario, es exclusiva de la creación
- [ ] `EstadoFichaPerfilEnTerminalRule` **NO** instanciada — consistente con el validator hermano
      `AgregarRevisionItemValidatorImpl`, no con `ModificarItemFichaPerfilValidatorImpl`
- [ ] `UseCase` sin `@Transactional` propio; `Interactor` con
      `@Transactional(transactionManager = "fichasTransactionManager")`
- [ ] `RevisionItemOutputPort.actualizarEstado` habla `String` (id del catálogo), nunca `EstadoRevision`
      cruzando la frontera de application → infrastructure
- [ ] `RevisionItemCommandOutputAdapter`: sin `try/catch`, usa `save`/`@Modifying` sin
      `saveAndFlush`, logea el método de escritura nuevo (y, por consistencia, el existente — ver nota
      Sección 7)
- [ ] Evento: `RevisionItemModificadoEvent extends DomainEvent`, `EVENT_TOPIC =
      "fichas.revision_item.modificado"`; publicado directo desde el `UseCase` vía `EventPublisher`
      inyectado (interfaz, nunca una de las dos implementaciones)
- [ ] **NO** se implementan las 6 piezas del lado `notificaciones` — decisión explícita del usuario,
      documentada en Sección 10
- [ ] `itemId` en el path es `UUID` (`@PathVariable UUID itemId`); `asesorFichaId` sale del JWT
      (`@AuthenticationPrincipal Jwt jwt`), nunca del body
- [ ] `RequestDTO` = `record` desnudo, sin anotaciones Jakarta; `RequestMapper` externo llama a
      `Command.crear(...)`
- [ ] `@PreAuthorize(FichasAuthorities.Expresiones.HAS_REVISION_ITEM_UPDATE)` — constante, no literal
- [ ] Un solo client role nuevo (`fichas:revision-item:update`), kebab-case, formato
      `{contexto}:{recurso}:{accion}`
- [ ] Controller retorna `ResponseEntity<Void>` con `ResponseEntity.noContent().build()` — 204, sin
      `ResponseEntity<UUID>` ni tipo `Result` (no aplica, el comando no retorna nada)
- [ ] Textos nuevos: `RevisionItemKey.ERROR_NO_ENCONTRADA` (1 parámetro) y `LOG_MODIFICADO` (1
      parámetro) con su línea en `catalogo/fichas.properties`, aridad verificada por
      `CatalogoCargaTest`
- [ ] Sin migración Flyway — no se agrega ningún archivo bajo `db/migration/fichas/`
- [ ] `InteractoresFichasTest` actualizado con `ModificarRevisionItemInteractorImpl.class`
- [ ] Tests con patrón AAA, cobertura ≥75% verificada con `check`
- [ ] Commit sugerido: `feat(fichas): modificar estado de revisión de ítem de ficha de perfil`

## 14. Trazabilidad del Flujo

| Etapa | Agente | Estado | Fecha | Notas |
|---|---|---|---|---|
| Desarrollo | @implementador | ✅ Completado | 2026-08-28 | Capas domain/application/infrastructure implementadas + tests (dominio, application, infrastructure incl. `@DataJpaTest`/`@WebMvcTest`). `./gradlew build`: sin errores (checkstyle + jacoco + tests de todo el repo) |
| Tests | @tester | ✅ Completado | 2026-08-28 | Revisión de los 13 archivos de tests ya escritos por @implementador (domain/application/infrastructure) contra el plan — cobertura de caminos de negocio completa, sin gaps (feliz, no-op, no-encontrada, no-propietario, estado no en catálogo, orden de validación, evento con `ArgumentCaptor`, `@WebMvcTest` con la authority exacta, `@DataJpaTest` con H2 real). No se agregó ningún test nuevo. `./gradlew :fichas:domain:test :fichas:application:test :fichas:infrastructure:test` y `:fichas:domain:check :fichas:application:check :fichas:infrastructure:check` — verdes. Cobertura: domain 79.95%, application 81.76%, infrastructure 76.44% — CUMPLE el 75% (clases nuevas de HU-196: 100% línea) |
| Validación | @validator-analyze | ✅ Completado | 2026-08-28 (re-validado 2026-08-29) | Ronda 1: Score 99/100 · 0 bloqueantes · 1 menor (log ausente en `registrarRevision`, atenuado). Ronda 2 (refactor `boolean`→`long` en `ExistenciaRevisionItem`): re-confirmado ✅ APROBADO, 0 bloqueantes, mismo menor heredado — ver reporte |
| Reporte | @validator-report | ✅ Completado | 2026-08-28 | Persistido en `.workspace/validator/validator-HU-196.md` |
| Commit | @commit | ⏳ Pendiente | | |
| PR | @commit | ⏳ Pendiente | | |
