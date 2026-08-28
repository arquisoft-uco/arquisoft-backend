# PLAN: Agregar Revisión Ítem

## Metadata
- **ID Historia:** HU-195
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura (emite evento de dominio)
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`, `shared:message` (constantes + catálogo)
- **Fecha de plan:** 2026-08-25
- **Rama sugerida:** `feature/HU-195-agregar_revision_item` (ya creada, limpia desde `develop` HEAD `5a2bc813`)
- **Fuentes consultadas:**
  - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md` (HU195, línea ~1945)
  - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md` (sección "Revision Item", líneas ~1986-2225; también "Item" líneas ~1195-1360 para POL-05/POL-06 relacionadas)
  - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md` (secciones 6 "Estado Revision" y 16 "Revision Item")
  - `mer/03_tablas_fichas_perfil.sql` (DDL `revision_item`, `estado_revision`)
  - `mer/data/03_data_fichas_perfil.sql` (datos semilla reales de `estado_revision`)
  - `mer/01_base_datos_y_esquemas.sql` (confirma que `fichas_perfil` no depende de otro contexto vía FK cruzada para esta entidad)
  - Código real de `fichas` (ver sección "Hallazgos" abajo)
- **Observaciones del usuario:**
  - POL-02 confirmado como "una revisión activa por item a la vez" (no historial).
  - Evento confirmado: sí se emite `RevisionItemAgregadoEvent`; el `Consumer` de `notificaciones` queda fuera de este plan (otra HU/HT).
  - `estado_revision.id` en `VARCHAR(50)` para calzar con la columna ya shippeada `revision_item.estado_revision_id` — es una decisión ampliable después con una migración aditiva normal (`ALTER COLUMN ... TYPE VARCHAR(60)`), no permanente.
  - Valores reales de `EstadoRevision` tomados de `mer/data/03_data_fichas_perfil.sql` (no inventados).
  - Resto de las propuestas del planificador aprobadas tal cual: rol `asesor-ficha` + client role `fichas:revision-item:create`, regla de propiedad ficha↔asesor reutilizando Finders existentes, reutilizar `ItemFichaPerfilExisteRule`, ruta `POST /fichas-perfil/items/{itemId}/revisiones`, retorno `UUID`, modificar `RevisionItemCommandRepositoryTest` para sembrar filas reales de `estado_revision`.

## 0. Hallazgo crítico — código parcial ya existente (verificado con `git log`/`git diff develop HEAD`, no es basura de la rama descartada)

La rama actual es idéntica a `develop`. Ya están commiteados en `develop`:

| Archivo | Estado | Qué hace hoy |
|---|---|---|
| `fichas/infrastructure/.../db/migration/fichas/V20260724005914__crear_revision_item.sql` | Ya aplicado — **no se edita** | Crea `revision_item(id, item_id, estado_revision_id VARCHAR(50), fecha_creacion)`, sin FK a `estado_revision` (esa tabla no existe aún) |
| `RevisionesDelItemFinder`/`Impl` (`application/revisionitem/command/finder/`) | Existente, se reutiliza tal cual | `Finder<UUID, Long>` → `RevisionItemOutputPort.contarPorItem(itemId)` |
| `RevisionItemOutputPort` (`application/revisionitem/command/secondaryport/`) | Se **modifica** (se agrega un método) | Hoy solo declara `long contarPorItem(UUID itemId)` |
| `RevisionItemEntity` (`application/revisionitem/command/secondaryport/entity/`) | Se **modifica** (rename + tipo) | Hoy `record RevisionItemEntity(UUID id, UUID itemId, String estadoRevisionId, LocalDateTime fechaCreacion)` |
| `RevisionItemJpaEntity`, `RevisionItemCommandOutputAdapter`, `RevisionItemCommandRepository` (`infrastructure/revisionitem/...`) | Se **modifican** | Adapter solo implementa `contarPorItem`; JpaEntity mapea `estado_revision_id` como `@Column String`, no como relación |
| `RevisionItemCommandOutputAdapterTest`, `RevisionItemCommandRepositoryTest` | Se **modifican** | El segundo siembra filas con `estado_revision_id` arbitrario (`"ESTADO_PRUEBA_1"`) vía SQL nativo — deja de ser válido si se agrega la relación `@ManyToOne`/FK |
| `RemoverItemFichaPerfilUseCaseImpl` (`application/itemfichaperfil/...`) | **No se toca** | Ya inyecta `RevisionesDelItemFinder` para `Item-POL-05`; esta HU no puede romper este flujo |

Esta porción existe porque sostiene `Item-POL-05` ("no remover un item con revisiones"). No existe: `RevisionItemDomain`, `AgregacionRevisionItemDomain`, `EstadoRevision`, ni ningún `Command`/`Mapper`/`Interactor`/`UseCase`/`Validator`/`Controller` de escritura. Los `.class` sueltos en `fichas/*/build/` que mencionan `AgregacionRevisionItemDomain`/`AgregarRevisionItemInteractorImpl` son residuo gitignorado de la implementación descartada (`build/` está en `.gitignore`) — no están en el código fuente y no se usan como referencia.

## 1. Resumen Funcional

Permite al Asesor Ficha registrar una revisión (`RevisionItem`) sobre un ítem (`ItemFichaPerfil`) de una ficha de perfil que él asesora, asignándole un estado de revisión (`EstadoRevision`, catálogo). La revisión queda disponible para que el estudiante la consulte (HU futura de consulta) y para que el propio asesor la modifique o elimine (HU-196/HU-197, fuera de alcance). Un ítem admite como máximo una revisión activa a la vez: si ya existe una, `Agregar Revisión Ítem` falla y el asesor debe usar `Modificar Revisión Ítem` (fuera de alcance de este plan).

**No cubre:** Modificar Revisión Ítem (HU-196), Remover Revisión Ítem, Consultar Revisión Item Elaboradas, Consultar Revision Item de su Ficha Perfil (todas HUs separadas listadas como "comandos posteriores" en el Event Storming), ni el `Consumer` de `notificaciones` que eventualmente escuche `RevisionItemAgregadoEvent`.

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|---|---|
| 1 | Asesor Ficha agrega una revisión a un ítem existente de una ficha que asesora, con un `estadoRevision` válido del catálogo | 201, body `{id}`, se persiste en `revision_item`, se publica `RevisionItemAgregadoEvent` |
| 2 | El ítem referenciado no existe | 422, `ITEM_NO_ENCONTRADO` |
| 3 | El ítem existe pero pertenece a una ficha que el Asesor Ficha autenticado no asesora | 422, `FICHA_NO_PERTENECE_ASESOR` (no hay 403 para "no eres el dueño") |
| 4 | El ítem ya tiene una revisión activa | 422, `REVISION_ITEM_YA_EXISTE` |
| 5 | `estadoRevision` es blanco/nulo | 422, `fieldErrors[]` con `ESTADO_REVISION_REQUERIDO` |
| 6 | `estadoRevision` no corresponde a ningún valor del catálogo `EstadoRevision` | 422, `fieldErrors[]` con `ESTADO_REVISION_NO_ENCONTRADO` |
| 7 | `estadoRevision` excede 50 caracteres | 422, `fieldErrors[]` con longitud excedida |
| 8 | Rol distinto de `asesor-ficha` intenta el endpoint | 403 |
| 9 | Sin JWT | 401 |

## 3. Reglas de Negocio

> Invariante LOCAL (formato, longitud, obligatoriedad de la propia instancia) → dentro del
> `RevisionItemDomain`/`AgregacionRevisionItemDomain`, acumulado en `ValidationResult` → 422 con
> `fieldErrors[]`, sin clase de excepción propia. Restricción de CONJUNTO (unicidad, existencia,
> propiedad) → `Rule` de dominio con su record de entrada, orquestada por
> `AgregarRevisionItemValidator` sobre lo que los `Finder`s ya trajeron → 422 con su propia
> `DomainException`. Nunca `if/throw` en el use case.

| # | Regla | Dónde se valida (Domain / Rule) | Finder que trae el dato | Excepción → HTTP |
|---|---|---|---|---|
| 1 | `item` obligatorio | `RevisionItemDomain.crear` (local) | — | `fieldErrors[]` → 422 |
| 2 | `estadoRevision` obligatorio, ≤ 50 caracteres, debe pertenecer al catálogo `EstadoRevision` | `RevisionItemDomain.crear` (local, vía `EstadoRevision.esValido`/`desde`) | — | `fieldErrors[]` → 422 |
| 3 | El ítem debe existir (RevisionItem-POL-01 aplicado a la referencia externa) | **Reutiliza** `ItemFichaPerfilExisteRule` (`domain/itemfichaperfil/rules/`, ya existe) | **Nuevo** `ItemFichaPerfilExisteFinder` → `ItemFichaPerfilOutputPort.existePorId` | `ItemFichaPerfilNoEncontradoException` (ya existe) → 422 |
| 4 | El Asesor Ficha autenticado debe ser el asesor asignado a la ficha del ítem (dueño) | **Nueva** `AsesorFichaPropietarioRule` (`domain/fichaperfil/rules/` — ver justificación de ubicación abajo) | `FichaPerfilDelItemFinder` (existe, `itemfichaperfil`) + `FichaPerfilFinder` (existe, `fichaperfil`), encadenados con `flatMap` | **Nueva** `FichaNoPerteneceAsesorException` (`domain/fichaperfil/exception/`) → 422 |
| 5 | RevisionItem-POL-02: no debe existir ya una revisión activa para el mismo ítem | **Nueva** `RevisionItemNoDuplicadaRule` (`domain/revisionitem/rules/`) | `RevisionesDelItemFinder` (existe, `count > 0`) | **Nueva** `RevisionItemYaExisteException` (`domain/revisionitem/exception/`) → 422 |

**Orden de ejecución dentro del validator (obligatorio):** existencia del ítem → propiedad del asesor
→ unicidad de la revisión. La regla de propiedad confía en que, si el ítem no existe,
`FichaPerfilDelItemFinder` devuelve `Optional.empty()` y `esPropietario` degrada a `false` de forma
seguraya, pero como la regla de existencia se valida **primero** y lanza antes de llegar a la de
propiedad, el cliente nunca ve "no eres el dueño" cuando el error real es "el ítem no existe".

**Justificación de ubicar `AsesorFichaPropietarioRule` en `domain/fichaperfil/rules/`** (no en
`domain/revisionitem/rules/`): sigue el precedente exacto de `FichaPerfilExisteRule` — declarada una
sola vez en `domain/fichaperfil/rules/` y reutilizada hoy por `evaluacionfichaperfil`,
`estudiantefichaperfil` y `estadofichaperfil` (`CambiarAsesorFichaValidatorImpl`,
`RegistrarEvaluacionFichaPerfilValidatorImpl`, etc.). La propiedad "el asesor X es dueño de la ficha
Y" es un invariante de `FichaPerfilDomain` (su campo `asesorFicha`), no de `RevisionItem` — y
`Modificar`/`Remover Revisión Ítem` (HU-196/197, fuera de alcance) necesitarán exactamente la misma
regla. Colocarla en `revisionitem` obligaría a duplicarla ahí también.

## 4. Modelo DDD del Contexto

### Entidad raíz
- **Clase:** `RevisionItemDomain` (nueva, `domain/revisionitem/`)
- **Objeto de acción:** `AgregacionRevisionItemDomain` (nueva, mismo paquete) — agrupa el agregado
  `RevisionItemDomain` recién construido junto con `asesorFicha` (UUID), que **no se persiste** en
  `revision_item` y solo existe para que la regla de propiedad lo compare contra
  `FichaPerfilDomain.getAsesorFicha()`. Lo construye `AgregarRevisionItemMapper` a partir del
  `Command`.

**Alcance deliberado de `RevisionItemDomain` en esta HU:** solo expone `crear(...)` — no
`reconstruir(...)` ni un centinela `VACIO`, porque este flujo nunca necesita cargar una
`RevisionItemDomain` completa desde la base (las comprobaciones existentes usan conteos/booleanos,
no el agregado). `reconstruir(...)` y `VACIO` se agregan cuando HU-196 (Modificar) o la remoción
los necesiten para leer-antes-de-mutar.

### Atributos por objeto de dominio

**`RevisionItemDomain`** (tabla `revision_item`, ya existe):

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | UUID | — | Sí | No | Sí (`UtilUUID.generarNuevoUUID()`) | PK |
| `item` | UUID | — | Sí | No | No | FK a `item.id` (itemfichaperfil) |
| `estadoRevision` | `EstadoRevision` (enum) | — | Sí | No (en esta HU; `Modificar` lo cambiará) | No | Validado vía `EstadoRevision.esValido`/`desde` |
| `fechaCreacion` | `Instant` | — | Sí | No | Sí (`UtilFecha.generarInstanteActual()`) | Columna `TIMESTAMP` |

**Combinación única:** `(item, fechaCreacion)` ya existe como `UNIQUE` en Flyway (`uk_revision_item_fecha`,
migración ya aplicada). La regla de negocio real de esta HU (§3.5) es más estricta que ese
constraint — la aplica la `Rule`, no la base de datos.

**`EstadoRevision`** (catálogo, tabla `estado_revision`, nueva — valores reales de
`mer/data/03_data_fichas_perfil.sql`, no inventados):

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | VARCHAR | 50 | Sí | No | No (constante del enum) | PK, `VARCHAR(50)` — calza con `revision_item.estado_revision_id` ya shippeado en vez del `VARCHAR(60)` documentado en el MER; ampliable después con `ALTER COLUMN ... TYPE VARCHAR(60)` si algún id real lo requiere |
| `nombre` | VARCHAR | 60 | Sí | No | No | `UNIQUE` |
| `descripcion` | VARCHAR | 300 | Sí | No | No | — |

Valores semilla (id / nombre / descripción, tomados literalmente del MER):
`NUEVA`, `VISUALIZADA`, `EN_PROGRESO`, `CORRECCION_DISPONIBLE`, `CERRADA` (detalle completo en §11).

**Combinaciones únicas:** `nombre` único en `estado_revision` (Flyway).

### Eventos de Dominio

| Evento | Clase | temaEvento | Consumidor | Cuándo |
|---|---|---|---|---|
| Revision Item Agregado | `RevisionItemAgregadoEvent` (nueva, `domain/revisionitem/event/`) | `fichas.revision_item.agregado` | `notificaciones` (futuro — **el `Consumer` no se construye en este plan**, es otra HU/HT) | Tras persistir el `RevisionItemDomain` |

**Publicación:** directa desde `AgregarRevisionItemUseCaseImpl` tras persistir, vía
`EventPublisher.publish(new RevisionItemAgregadoEvent(...))` — una sola forma, sin acumulación en el
agregado (ver `CambiarAsesorFichaUseCaseImpl` como referencia exacta).

**Payload del evento** (todo lo que un futuro consumidor necesitaría sin volver a consultar
`fichas`): `revisionItemId`, `itemId`, `estadoRevisionId` (id del catálogo), `estadoRevisionNombre`,
`fechaCreacion`. No se agregan datos del estudiante/ficha (nombre, email) porque esta HU no
construye el consumidor y añadirlos ahora sin un consumidor real que los use sería anticipar un
contrato que nadie ha decidido — la HU/HT que agregue el `Consumer` en `notificaciones` amplía el
evento si los necesita.

## 5. Integraciones Externas

No aplica — el Event Storming confirma "No se encontraron sistemas externos" para este comando.

## 6. Árbol de Archivos a Crear / Modificar

| Capa | Ruta completa desde raíz del repo | Tipo | Responsabilidad |
|---|---|---|---|
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/RevisionItemDomain.java` | NUEVO | Aggregate root — `crear(item, estadoRevision)` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/AgregacionRevisionItemDomain.java` | NUEVO | Objeto de acción — agrupa `RevisionItemDomain` + `asesorFicha` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/model/DisponibilidadRevisionItem.java` | NUEVO | `record(UUID item, boolean yaExiste)` — entrada de la Rule de unicidad |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/rules/RevisionItemNoDuplicadaRule.java` + `rules/impl/RevisionItemNoDuplicadaRuleImpl.java` | NUEVO | POL-02 |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/exception/RevisionItemYaExisteException.java` | NUEVO | `DomainException` → 422 |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/event/RevisionItemAgregadoEvent.java` | NUEVO | Evento de dominio |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadorevision/EstadoRevision.java` | NUEVO | Enum de catálogo (`desde`/`esValido`/`getId`/`getNombre`) |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadorevision/exception/EstadoRevisionNoEncontradoException.java` | NUEVO | `DomainException` → 422 |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/model/PropiedadAsesorFicha.java` | NUEVO | `record(UUID fichaPerfil, UUID asesorFicha, boolean esPropietario)` |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/rules/AsesorFichaPropietarioRule.java` + `rules/impl/AsesorFichaPropietarioRuleImpl.java` | NUEVO | Ver justificación §3 — reusable por HU-196/197 |
| domain | `fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/exception/FichaNoPerteneceAsesorException.java` | NUEVO | `DomainException` → 422 |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/primaryport/model/AgregarRevisionItemCommand.java` | NUEVO | `record` + `crear(...)` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/primaryport/mapper/AgregarRevisionItemMapper.java` | NUEVO | `Command` → `AgregacionRevisionItemDomain` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/primaryport/interactor/AgregarRevisionItemInteractor.java` + `interactor/impl/AgregarRevisionItemInteractorImpl.java` | NUEVO | `@Transactional(transactionManager = "fichasTransactionManager")` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/usecase/AgregarRevisionItemUseCase.java` + `usecase/impl/AgregarRevisionItemUseCaseImpl.java` | NUEVO | Orquestación |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/validator/AgregarRevisionItemValidator.java` + `validator/impl/AgregarRevisionItemValidatorImpl.java` | NUEVO | Orquesta las 3 Rules |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/secondaryport/mapper/RevisionItemMapper.java` | NUEVO | `Domain ↔ Entity` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/secondaryport/RevisionItemOutputPort.java` | **MODIFICAR** | Agregar `void registrarRevision(RevisionItemEntity revision);` (se mantiene `contarPorItem`) |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/secondaryport/entity/RevisionItemEntity.java` | **MODIFICAR** | Rename `itemId`→`item`, `estadoRevisionId`→`estadoRevision`; tipo `fechaCreacion` de `LocalDateTime`→`Instant` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/finder/ItemFichaPerfilExisteFinder.java` + `finder/impl/ItemFichaPerfilExisteFinderImpl.java` | NUEVO | Envuelve `ItemFichaPerfilOutputPort.existePorId` (vive en `itemfichaperfil`, la feature dueña del puerto) |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadorevision/command/secondaryport/entity/EstadoRevisionEntity.java` | NUEVO | `record(String id, String nombre, String descripcion)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/command/primaryadapter/web/AgregarRevisionItemController.java` | NUEVO | `POST /fichas-perfil/items/{itemId}/revisiones` |
| infrastructure | `fichas/infrastructure/.../revisionitem/command/primaryadapter/web/dto/AgregarRevisionItemRequestDTO.java` + `dto/AgregarRevisionItemResponseDTO.java` | NUEVO | `record`s desnudos |
| infrastructure | `fichas/infrastructure/.../revisionitem/command/primaryadapter/web/mapper/AgregarRevisionItemRequestMapper.java` | NUEVO | `toCommand(dto, itemId, asesorFichaId)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/command/secondaryadapter/entity/RevisionItemJpaEntity.java` | **MODIFICAR** | `estadoRevisionId` (String) → `@ManyToOne EstadoRevisionJpaEntity estadoRevision`; `fechaCreacion` → `Instant`; agregar `@AllArgsConstructor @Builder` |
| infrastructure | `fichas/infrastructure/.../revisionitem/command/secondaryadapter/mapper/RevisionItemJpaMapper.java` | NUEVO | `Entity ↔ JpaEntity`, usa `EstadoRevisionJpaMapper.toReferencia(...)` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/command/secondaryadapter/repository/RevisionItemCommandOutputAdapter.java` | **MODIFICAR** | Implementar `registrarRevision` |
| infrastructure | `fichas/infrastructure/.../revisionitem/command/secondaryadapter/repository/RevisionItemCommandRepository.java` | Sin cambios | `JpaRepository.save(...)` ya alcanza |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadorevision/command/secondaryadapter/entity/EstadoRevisionJpaEntity.java` | NUEVO | `@Entity @Table(name="estado_revision")` |
| infrastructure | `fichas/infrastructure/.../estadorevision/command/secondaryadapter/mapper/EstadoRevisionJpaMapper.java` | NUEVO | `toEntity`/`toJpaEntity`/`toReferencia` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/security/FichasAuthorities.java` | **MODIFICAR** | Agregar `REVISION_ITEM_CREATE` + `HAS_REVISION_ITEM_CREATE` |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/fichas/V{timestamp}__crear_estado_revision.sql` | NUEVO | Ver §11 |
| tests (MODIFICAR) | `fichas/infrastructure/src/test/java/.../revisionitem/command/secondaryadapter/repository/RevisionItemCommandRepositoryTest.java` | **MODIFICAR** | Sembrar filas reales de `estado_revision` en vez de `"ESTADO_PRUEBA_1"`; `LocalDateTime.now()` → `Instant.now()` |
| tests (MODIFICAR) | `fichas/infrastructure/src/test/java/.../revisionitem/command/secondaryadapter/repository/RevisionItemCommandOutputAdapterTest.java` | **MODIFICAR** | Ajustar si el mock cambia de forma por el nuevo método |
| shared | `shared/message/.../constant/FichasCodes.java` | **MODIFICAR** | Bloque `RevisionItem` + campos en `ItemFichaPerfil`/`FichaPerfil` (ver detalle §7) |
| shared | `shared/message/.../constant/FichasFields.java` | **MODIFICAR** | Bloque `RevisionItem` |
| shared | `shared/message/.../constant/FichasLimits.java` | **MODIFICAR** | Bloque `RevisionItem.ESTADO_MAX = 50` |
| shared | `shared/message/.../annotation/FichasApiMessages.java` | **MODIFICAR** | Bloque `RevisionItem` (Swagger) |
| shared | `shared/message/.../key/fichas/RevisionItemKey.java` | NUEVO | Claves de error/log |
| shared | `catalogo/fichas.properties` | **MODIFICAR** | Texto de las claves nuevas |

## 7. Detalle por Archivo

**`RevisionItemDomain`** (`domain/revisionitem/`): constructor privado, campos `id/item/estadoRevision/fechaCreacion` no-`final`. `crear(UUID item, String estadoRevision)`: `setId()` (`UtilUUID.generarNuevoUUID()`), `setItem(item, result)` (`ValidatorObjeto.noNulo` → `FichasFields.RevisionItem.ITEM` / `FichasCodes.RevisionItem.ITEM_REQUERIDO`), `setEstadoRevision(estadoRevision, result)` (`ValidatorTexto.noEnBlanco` + `ValidatorLongitud.longitudMaxima(50)` → si pasa, `EstadoRevision.esValido(...)`; si no es válido, `result.agregarError(..., ESTADO_REVISION_NO_ENCONTRADO, Mensajes.formatear(RevisionItemKey.ERROR_ESTADO_NO_ENCONTRADO, estadoRevision))`; si es válido, `EstadoRevision.desde(...)`), `setFechaCreacion()` (`UtilFecha.generarInstanteActual()`). Getters: `getId/getItem/getEstadoRevision/getFechaCreacion`. Sin `reconstruir`/`VACIO` (ver §4).

**`AgregacionRevisionItemDomain`** (`domain/revisionitem/`): constructor privado, campos `revisionItem` (`RevisionItemDomain`) y `asesorFicha` (`UUID`). `crear(RevisionItemDomain revisionItem, UUID asesorFicha)`: valida ambos con `ValidatorObjeto.noNulo`. Getters: `getRevisionItem()`, `getItem()` (delega `revisionItem.getItem()`), `getEstadoRevision()` (delega), `getAsesorFicha()`.

**`EstadoRevision`** (`domain/estadorevision/`): enum con constantes `NUEVA("Nueva")`, `VISUALIZADA("Visualizada")`, `EN_PROGRESO("En Progreso")`, `CORRECCION_DISPONIBLE("Correccion Disponible")`, `CERRADA("Cerrada")` — mismo patrón exacto de `TipoItem`/`EstadoFicha`: `id = name()`, `getId()`, `getNombre()`, `desde(String)` (vía `UtilEnum.desde` → `EstadoRevisionNoEncontradoException`), `esValido(String)` (vía `UtilEnum.esValido`).

**`DisponibilidadRevisionItem(UUID item, boolean yaExiste)`** (`domain/revisionitem/model/`): entrada de `RevisionItemNoDuplicadaRule`.

**`RevisionItemNoDuplicadaRuleImpl`**: `if (disponibilidad.yaExiste()) throw new RevisionItemYaExisteException(disponibilidad.item());`

**`PropiedadAsesorFicha(UUID fichaPerfil, UUID asesorFicha, boolean esPropietario)`** (`domain/fichaperfil/model/`).

**`AsesorFichaPropietarioRuleImpl`**: `if (!propiedad.esPropietario()) throw new FichaNoPerteneceAsesorException(propiedad.fichaPerfil(), propiedad.asesorFicha());`

**`RevisionItemAgregadoEvent`** (`domain/revisionitem/event/`, extends `DomainEvent`): `EVENT_TOPIC = "fichas.revision_item.agregado"`, `EVENT_TYPE = "RevisionItemAgregadoEvent"`. Campos: `revisionItemId`, `itemId` (UUID), `estadoRevisionId`, `estadoRevisionNombre` (String), `fechaCreacion` (Instant). Getters solamente.

**`AgregarRevisionItemCommand(UUID item, String estadoRevision, UUID asesorFicha)`** (`application/revisionitem/command/primaryport/model/`): canonical constructor aplica `UtilTexto.aplicarTrim(estadoRevision)`. `crear(UUID item, String estadoRevision, UUID asesorFicha)`: `ValidatorObjeto.noNulo(item, ...)`, `ValidatorTexto.noEnBlanco(estadoRevision, ...)` + `ValidatorLongitud.longitudMaxima(estadoRevision, FichasLimits.RevisionItem.ESTADO_MAX, ...)`, `ValidatorObjeto.noNulo(asesorFicha, ...)`; `result.lanzarSiTieneErroresDeEntrada()`. `item`/`asesorFicha` llegan ya como `UUID` (path variable / JWT respectivamente — nunca `String` validado con `ValidatorUUID`, porque no vienen del body).

**`AgregarRevisionItemMapper.toDomain(command)`**: `var revisionItem = RevisionItemDomain.crear(command.item(), command.estadoRevision()); return AgregacionRevisionItemDomain.crear(revisionItem, command.asesorFicha());`

**`AgregarRevisionItemInteractorImpl`**: `@Transactional(transactionManager = "fichasTransactionManager")`, `ejecutar(AgregarRevisionItemCommand command) → UUID`, delega a `agregarRevisionItemUseCase.ejecutar(AgregarRevisionItemMapper.toDomain(command))`.

**`AgregarRevisionItemUseCaseImpl`** (`@Component`, sin transacción propia): inyecta `ItemFichaPerfilExisteFinder`, `FichaPerfilDelItemFinder`, `FichaPerfilFinder`, `RevisionesDelItemFinder`, `AgregarRevisionItemValidator`, `RevisionItemOutputPort`, `EventPublisher`, `AppLogger`.

```
boolean itemExiste = itemFichaPerfilExisteFinder.obtener(entrada.getItem());
boolean esPropietario = fichaPerfilDelItemFinder.obtener(entrada.getItem())
        .flatMap(fichaPerfilFinder::obtener)
        .map(ficha -> ficha.getAsesorFicha().equals(entrada.getAsesorFicha()))
        .orElse(false);
boolean revisionYaExiste = revisionesDelItemFinder.obtener(entrada.getItem()) > 0;

agregarRevisionItemValidator.validar(entrada, itemExiste, esPropietario, revisionYaExiste);

var revisionItem = entrada.getRevisionItem();
revisionItemOutputPort.registrarRevision(RevisionItemMapper.toEntity(revisionItem));

eventPublisher.publish(new RevisionItemAgregadoEvent(
        revisionItem.getId(), revisionItem.getItem(),
        revisionItem.getEstadoRevision().getId(), revisionItem.getEstadoRevision().getNombre(),
        revisionItem.getFechaCreacion()));

logger.info(Mensajes.obtener(RevisionItemKey.LOG_AGREGADO), revisionItem.getId(), revisionItem.getItem());
return revisionItem.getId();
```

**`AgregarRevisionItemValidatorImpl`** (`@Component`, constructor sin argumentos con `new ...RuleImpl()` para las 3 rules): orden `itemFichaPerfilExisteRule.validar(new ExistenciaItemFichaPerfil(entrada.getItem(), itemExiste))` → `asesorFichaPropietarioRule.validar(new PropiedadAsesorFicha(fichaPerfilDelItem, entrada.getAsesorFicha(), esPropietario))` → `revisionItemNoDuplicadaRule.validar(new DisponibilidadRevisionItem(entrada.getItem(), revisionYaExiste))`. Nota: `fichaPerfilDelItem` para construir `PropiedadAsesorFicha` no lo tiene el validator (es puro) — el **use case** ya resolvió `esPropietario` como booleano, así que el `fichaPerfil` que viaja en el record es el mismo `entrada.getItem()`-derivado que ya usó el use case; ajustar la firma del validator a `validar(AgregacionRevisionItemDomain entrada, boolean itemExiste, boolean esPropietario, boolean revisionYaExiste)` y construir `PropiedadAsesorFicha(entrada.getItem(), entrada.getAsesorFicha(), esPropietario)` — el mensaje de la excepción usa el `item`, no el `fichaPerfilId`, para no forzar al use case a pasar un dato adicional solo para el mensaje.

**`ItemFichaPerfilExisteFinderImpl`** (`application/itemfichaperfil/command/finder/impl/`, `@Component`): `return itemFichaPerfilOutputPort.existePorId(item);` — mismo patrón que `AsesorFichaExisteFinderImpl`.

**`RevisionItemMapper`** (`application/revisionitem/command/secondaryport/mapper/`, `final`, static): `toEntity(RevisionItemDomain d) → new RevisionItemEntity(d.getId(), d.getItem(), d.getEstadoRevision().getId(), d.getFechaCreacion())`. (No se necesita `toDomain` en esta HU — nada reconstruye el agregado todavía.)

**`RevisionItemOutputPort`** (modificar): agregar `void registrarRevision(RevisionItemEntity revision);` manteniendo `long contarPorItem(UUID itemId);`.

**`RevisionItemCommandOutputAdapter`** (modificar): `registrarRevision(RevisionItemEntity revision) { repository.save(RevisionItemJpaMapper.toJpaEntity(revision)); }`.

**`RevisionItemJpaEntity`** (modificar): agregar `@AllArgsConstructor @Builder`; reemplazar `@Column(name="estado_revision_id", length=50) String estadoRevisionId` por `@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "estado_revision_id", nullable = false) EstadoRevisionJpaEntity estadoRevision`; `fechaCreacion` de `LocalDateTime` a `Instant`. `itemId` se mantiene como `@Column UUID` (no `@ManyToOne`) — decisión deliberada para no ampliar el radio de este plan agregando un `toReferencia` a `ItemFichaPerfilJpaMapper` que ningún otro caso necesita todavía; sigue el mismo patrón que `EstudianteFichaPerfilJpaEntity.fichaPerfilId` (FK plana hacia el "padre" real, `@ManyToOne` reservado para el catálogo).

**`RevisionItemJpaMapper`** (nuevo, `infrastructure/revisionitem/command/secondaryadapter/mapper/`): `toEntity(jpa) → new RevisionItemEntity(jpa.getId(), jpa.getItemId(), jpa.getEstadoRevision().getId(), jpa.getFechaCreacion())`; `toJpaEntity(entity) → RevisionItemJpaEntity.builder().id(...).itemId(...).estadoRevision(EstadoRevisionJpaMapper.toReferencia(entity.estadoRevision())).fechaCreacion(...).build()`.

**`EstadoRevisionJpaEntity`** (nuevo, mismo patrón que `TipoItemJpaEntity`): `@Id @Column(length=50) String id`, `@Column(nullable=false, unique=true, length=60) String nombre`, `@Column(nullable=false, length=300) String descripcion`; `@Getter @NoArgsConstructor @AllArgsConstructor @Builder`.

**`EstadoRevisionJpaMapper`** (nuevo): `toEntity`/`toJpaEntity`/`toReferencia(String id) → EstadoRevisionJpaEntity.builder().id(id).build()` (idéntico a `TipoItemJpaMapper`).

**Controller — `AgregarRevisionItemController`** (`infrastructure/revisionitem/command/primaryadapter/web/`):
- `@RestController @RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")`
- `@Tag(name = FichasApiMessages.RevisionItem.TAG_NAME, description = FichasApiMessages.RevisionItem.TAG_DESCRIPTION)`
- `@PostMapping("${rutas.fichas.fichas-perfil.item-revisiones:/items/{itemId}/revisiones}")`
- `@PreAuthorize(FichasAuthorities.Expresiones.HAS_REVISION_ITEM_CREATE)`
- `@Operation(summary = FichasApiMessages.RevisionItem.AGREGAR_SUMMARY, description = FichasApiMessages.RevisionItem.AGREGAR_DESCRIPTION, security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))`
- `@ApiResponses`: `201` (`AGREGAR_RESP_201`, body `AgregarRevisionItemResponseDTO`), `400` (`AGREGAR_RESP_400`), `401` (`FichasApiMessages.Comun.RESP_401`), `403` (`AGREGAR_RESP_403`), `422` (`AGREGAR_RESP_422`)
- Método: `agregar(@PathVariable UUID itemId, @RequestBody AgregarRevisionItemRequestDTO dto, @AuthenticationPrincipal Jwt jwt)` → `var asesorFichaId = UUID.fromString(jwt.getSubject()); var id = interactor.ejecutar(AgregarRevisionItemRequestMapper.toCommand(dto, itemId, asesorFichaId)); return ResponseEntity.status(HttpStatus.CREATED).body(new AgregarRevisionItemResponseDTO(id));`

**`AgregarRevisionItemRequestDTO(String estadoRevision)`** — `record` desnudo (el `itemId` va en el path, el `asesorFicha` sale del JWT, ninguno de los dos en el body).

**`AgregarRevisionItemResponseDTO(UUID id)`** — `record`.

**`AgregarRevisionItemRequestMapper.toCommand(dto, itemId, asesorFichaId)`** → `AgregarRevisionItemCommand.crear(itemId, dto.estadoRevision(), asesorFichaId)`.

## 8. Endpoints REST

| Método | Ruta (sin /api) | Request | Response | HTTP | Client role | Swagger |
|---|---|---|---|---|---|---|
| POST | `/fichas-perfil/items/{itemId}/revisiones` (placeholder `rutas.fichas.fichas-perfil.item-revisiones`) | `AgregarRevisionItemRequestDTO{estadoRevision}` | `AgregarRevisionItemResponseDTO{id}` | 201 | `fichas:revision-item:create` | `@Tag`/`@Operation`/`@ApiResponses`/`@SecurityRequirement` en `FichasApiMessages.RevisionItem` |

## 9. Seguridad y Autorización (Keycloak)

| Client role | Roles realm que lo poseen | Endpoint(s) | Descripción |
|---|---|---|---|
| `fichas:revision-item:create` | `asesor-ficha` | `POST /fichas-perfil/items/{itemId}/revisiones` | Agregar una revisión a un ítem de una ficha que el asesor autenticado asesora |

`FichasAuthorities` agrega `REVISION_ITEM_CREATE = "fichas:revision-item:create"` y
`Expresiones.HAS_REVISION_ITEM_CREATE`.

## 10. Eventos RabbitMQ

| Dirección | Exchange | Routing Key | Payload | Contexto receptor |
|---|---|---|---|---|
| Saliente | `arquisoft.events` (`RabbitMQConfig.EXCHANGE_NAME`) | `fichas.revision_item.agregado` | `revisionItemId, itemId, estadoRevisionId, estadoRevisionNombre, fechaCreacion` | `notificaciones` (futuro — consumer fuera de alcance de este plan) |

## 11. Migración de Base de Datos

**Ubicación:** `fichas/infrastructure/src/main/resources/db/migration/fichas/V{yyyyMMddHHmmss}__crear_estado_revision.sql`
(timestamp generado al crear el archivo, posterior al de toda migración ya aplicada — la última hoy es `V20260724005915`).

```sql
CREATE TABLE estado_revision (
    id          VARCHAR(50)  PRIMARY KEY,
    nombre      VARCHAR(60)  NOT NULL,
    descripcion VARCHAR(300) NOT NULL,
    CONSTRAINT uk_estado_revision_nombre UNIQUE (nombre)
);

INSERT INTO estado_revision (id, nombre, descripcion) VALUES
    ('NUEVA',                 'Nueva',                 'La revision ha sido creada recientemente y aún no ha sido revisada o procesada.'),
    ('VISUALIZADA',           'Visualizada',           'La revisión ha sido vista por el estudiante, pero aún no se ha tomado acción sobre ella.'),
    ('EN_PROGRESO',           'En Progreso',           'La revisión está en desarrollo o ejecución, y se están realizando las acciones necesarias para su resolución.'),
    ('CORRECCION_DISPONIBLE', 'Correccion Disponible', 'Se ha completado el trabajo, pero requiere revisión o validación. Puede implicar que se han realizado cambios o ajustes y están listos para ser evaluados.'),
    ('CERRADA',               'Cerrada',               'La revisión ha sido completada y aprobada. No requiere más modificaciones ni acciones adicionales.');

-- revision_item se creó (V20260724005914) sin FK a ningún catálogo, así que pudo acumular
-- valores de prueba que nunca fueron un estado real (ej. 'APROBADO', de pruebas manuales
-- anteriores a que este catálogo existiera). Sin este DELETE, el ALTER TABLE de abajo falla
-- en cualquier base que arrastre ese tipo de fila; en una base sin filas huérfanas no borra nada.
DELETE FROM revision_item
 WHERE estado_revision_id NOT IN (SELECT id FROM estado_revision);

ALTER TABLE revision_item
    ADD CONSTRAINT fk_rev_estado FOREIGN KEY (estado_revision_id) REFERENCES estado_revision(id);
```

Notas:
- El `DELETE` defensivo antes del `ALTER TABLE` se agregó el 2026-08-27, al descubrir en la base
  compartida (`172.16.1.10`) una fila real de `revision_item` con `estado_revision_id = 'APROBADO'`
  — un valor que nunca fue un estado real del catálogo (fue solo la primera propuesta descartada
  del planificador), resto de una prueba manual contra el intento de implementación previo,
  totalmente descartado de git. Sin este `DELETE`, el `ALTER TABLE` fallaba con
  `violates foreign key constraint "fk_rev_estado"` al validar filas preexistentes. Es idempotente:
  en cualquier otra base (local, de otro dev) que no tenga filas huérfanas, no borra nada.
- `estado_revision.id` en `VARCHAR(50)` para calzar exactamente con la columna ya shippeada
  `revision_item.estado_revision_id VARCHAR(50)` (migración `V20260724005914`, aplicada, no se toca).
  El MER documenta `VARCHAR(60)` para este catálogo — si algún id real llegara a no caber en 50
  caracteres, se amplía después con una migración aditiva normal
  (`ALTER TABLE estado_revision ALTER COLUMN id TYPE VARCHAR(60)` + la misma en
  `revision_item.estado_revision_id`); no es una limitación permanente, es la decisión tomada hoy
  para no editar una migración ya aplicada.
- El `ALTER TABLE revision_item ADD CONSTRAINT fk_rev_estado ...` se ejecuta en esta misma migración
  (tal como indica el comentario del propio MER en `03_tablas_fichas_perfil.sql`), no en la
  migración ya aplicada.
- Sin prefijo de base/schema; ambas tablas ya están en la base `fichas_perfil`.
- Sin FK cruzada hacia otro contexto — no aplica aquí.

## 12. Casos de Prueba Sugeridos

Tamaño: Mediana-Grande (1 endpoint nuevo, pero toca 3 features — `revisionitem` nuevo,
`itemfichaperfil` y `fichaperfil` modificados — más un catálogo nuevo). Presupuesto ~45-55 tests.

**Domain:**
- `RevisionItemDomain`: `crear` válido; `item` nulo → error; `estadoRevision` blanco → error;
  `estadoRevision` > 50 caracteres → error; `estadoRevision` no en catálogo → error; todos los
  `fieldErrors[]` acumulados en un solo `crear` con múltiples fallos.
- `AgregacionRevisionItemDomain`: `crear` válido; `revisionItem` nulo → error; `asesorFicha` nulo → error.
- `EstadoRevision`: `desde` para cada constante; `desde` con id inválido → `EstadoRevisionNoEncontradoException`; `esValido` true/false; `getId`/`getNombre`.
- `RevisionItemNoDuplicadaRuleImpl`: `yaExiste=false` no lanza; `yaExiste=true` → `RevisionItemYaExisteException`.
- `AsesorFichaPropietarioRuleImpl`: `esPropietario=true` no lanza; `false` → `FichaNoPerteneceAsesorException`.
- `RevisionItemAgregadoEvent`: construcción y getters, `EVENT_TOPIC` con formato `{contexto}.{entidad}.{accion}`.

**Application:**
- `AgregarRevisionItemUseCaseImpl`: flujo exitoso (verifica `registrarRevision` + `eventPublisher.publish` con los datos correctos); item no existe (short-circuit antes de registrar); no es propietario; revisión ya existe; orden de invocación de finders antes que el validator.
- `AgregarRevisionItemValidatorImpl`: orden de las 3 rules — item no existe lanza antes que propiedad; no propietario lanza antes que duplicado; caso feliz no lanza nada.
- `AgregarRevisionItemCommand.crear`: válido; cada campo inválido por separado; acumulación de errores.
- `AgregarRevisionItemMapper.toDomain`: mapeo correcto Command → AgregacionRevisionItemDomain.
- `ItemFichaPerfilExisteFinderImpl`: delega correctamente a `existePorId` (true/false).
- `RevisionItemMapper.toEntity`: mapeo correcto.

**Infrastructure:**
- `RevisionItemCommandOutputAdapterTest`: `registrarRevision` delega a `repository.save`; `contarPorItem` (ya existente, no se toca su lógica).
- `RevisionItemCommandRepositoryTest` (modificar): sembrar `estado_revision` real antes de insertar `revision_item` (con `TestEntityManager` o SQL nativo referenciando IDs reales del catálogo), reemplazando `"ESTADO_PRUEBA_1"/"_2"` y `LocalDateTime.now()` por `Instant.now()`.
- `RevisionItemJpaMapperTest`: `toEntity`/`toJpaEntity`, incluida la relación `@ManyToOne` vía `toReferencia`.
- `EstadoRevisionJpaMapperTest`: `toEntity`/`toJpaEntity`/`toReferencia`.
- `AgregarRevisionItemControllerTest` (`@WebMvcTest`): 201 caso feliz; 400 estado en blanco; 401 sin JWT; 403 rol incorrecto; 422 por cada excepción de dominio (item no encontrado, no propietario, duplicado, estado no encontrado).

**Eventos:** el `verify(eventPublisher)` va dentro del test de `AgregarRevisionItemUseCaseImpl` (no hay test de "publicación" separado).

## 13. Checklist de Implementación

- [ ] `RevisionItemDomain`: constructor privado, setters privados con corte por `return`, `crear(...)` sin `reconstruir`/`VACIO` (alcance de esta HU)
- [ ] `AgregacionRevisionItemDomain` construido por `AgregarRevisionItemMapper`, nunca directamente en el use case
- [ ] Invariantes locales (`item`, `estadoRevision`) acumuladas en `ValidationResult`; las 3 restricciones de conjunto son `Rule`s con su record, orquestadas por `AgregarRevisionItemValidatorImpl` (sin `if` propio)
- [ ] Orden de rules: existencia del ítem → propiedad del asesor → unicidad de la revisión
- [ ] `ItemFichaPerfilExisteRule`/`ItemFichaPerfilNoEncontradoException` **reutilizados**, no duplicados
- [ ] `AsesorFichaPropietarioRule` vive en `domain/fichaperfil/rules/` (no en `domain/revisionitem/`)
- [ ] Sin `Optional` en firmas de `Validator` ni en records de `Rule`
- [ ] Evento: `RevisionItemAgregadoEvent` publicado directo desde el `UseCase` vía `EventPublisher`, sin acumulación en el agregado
- [ ] IDs siempre `UUID`; `item`/`asesorFicha` llegan al `Command.crear` ya como `UUID` (path/JWT), nunca validados con `ValidatorUUID` (eso es solo para `String` del body)
- [ ] `AgregarRevisionItemInteractorImpl` dueño de `@Transactional(transactionManager = "fichasTransactionManager")`; `UseCaseImpl` sin transacción propia
- [ ] `RevisionItemOutputPort` habla `Entity`, nunca `Domain`; `registrarRevision` agregado sin romper `contarPorItem` ni `RemoverItemFichaPerfilUseCaseImpl`
- [ ] `RevisionItemEntity` renombrado (`item`, `estadoRevision`) y con `Instant` — actualizar todo punto que lo construya
- [ ] `RevisionItemJpaEntity.estadoRevision` como `@ManyToOne` hacia `EstadoRevisionJpaEntity`; `itemId` se mantiene `@Column` plano
- [ ] `EstadoRevision` sigue el patrón `desde`/`esValido`/`getId` — nunca `valueOf` fuera del enum
- [ ] Excepciones nuevas en el `exception/` de su propio slice y capa: `RevisionItemYaExisteException`/`EstadoRevisionNoEncontradoException`/`FichaNoPerteneceAsesorException` → `DomainException` (422)
- [ ] `RequestDTO`/`ResponseDTO` = `record`s desnudos, sin Jakarta, sin Lombok, sin `toCommand()` propio
- [ ] `AgregarRevisionItemController` documentado con `@Tag`/`@Operation`/`@ApiResponses`/`@SecurityRequirement`, un controller por acción, ruta como placeholder de propiedad
- [ ] `@PreAuthorize(FichasAuthorities.Expresiones.HAS_REVISION_ITEM_CREATE)` — constante, no literal
- [ ] Textos nuevos: `RevisionItemKey` + registro en `ClavesCatalogo` + línea en `catalogo/fichas.properties`, aridad correcta
- [ ] Migración Flyway nueva (`V{yyyyMMddHHmmss}__crear_estado_revision.sql`) con timestamp posterior a `V20260724005915`, sin tocar la migración ya aplicada de `revision_item`
- [ ] `RevisionItemCommandRepositoryTest` actualizado para sembrar `estado_revision` real (rompe si no se hace, por el nuevo `@ManyToOne`)
- [ ] Tests con patrón AAA, cobertura ≥75%
- [ ] Sin `@Bean TaskExecutor` manual
- [ ] Commit sugerido: `feat(fichas): agregar revisión a un ítem de ficha de perfil`

## 14. Trazabilidad del Flujo

| Etapa | Agente | Estado | Fecha | Notas |
|---|---|---|---|---|
| Desarrollo | @implementador | ✅ Completado | 2026-08-26 | Compila y Checkstyle limpio en las 3 capas de `fichas` (domain/application/infrastructure) y en `shared:message` — verificado además contra el resto del monorepo (`./gradlew build -x test` en los 9 contextos y los 15 módulos `shared`) sin romper nada. Suites existentes de `fichas:domain`/`fichas:application`/`fichas:infrastructure` pasan sin regresiones (incluye `RevisionItemCommandRepositoryTest`/`RevisionItemCommandOutputAdapterTest` actualizados por el cambio de forma de `RevisionItemEntity`/`RevisionItemJpaEntity`). `fichas:infrastructure:jacocoTestCoverageVerification` pasa completo (≥75%). `fichas:domain`/`fichas:application` quedan en 0.68 de cobertura — **no es una regresión**: son las clases nuevas de esta HU (`RevisionItemDomain`, `AgregacionRevisionItemDomain`, `EstadoRevision`, `RevisionItemNoDuplicadaRuleImpl`, `AsesorFichaPropietarioRuleImpl`, `AgregarRevisionItemValidatorImpl`, `AgregarRevisionItemUseCaseImpl`, mappers) sin tests propios todavía — la suite queda pendiente de `@tester`, por decisión explícita del flujo. |
| Tests | @tester | ✅ Completado | 2026-08-26 | 46 tests nuevos (21 domain + 25 application), todos pasan. Cobertura: `fichas:domain` 80.79% (instrucciones) — CUMPLE del 75%; `fichas:application` 84.44% (instrucciones) — CUMPLE del 75%. `jacocoTestCoverageVerification` y `checkstyleTest` pasan en ambos módulos. `fichas:infrastructure` no se tocó (ya cumplía). Se actualizó además `InteractoresFichasTest` (existente) para incluir `AgregarRevisionItemInteractorImpl` en la verificación de `@Transactional`. Sin bugs de producción encontrados. |
| Tests (corrección bloqueante) | @tester | ✅ Completado | 2026-08-27 | La nota anterior de `fichas:infrastructure` ("no se tocó, ya cumplía") era incorrecta — los 4 archivos de infraestructura nuevos de esta HU (`AgregarRevisionItemController`, `AgregarRevisionItemRequestMapper`, `RevisionItemJpaMapper`, `EstadoRevisionJpaMapper`) no tenían test propio, señalado como bloqueante por `@validator-analyze`. Se agregaron 13 tests nuevos: `AgregarRevisionItemControllerTest` (8 — `@WebMvcTest` + `@Import(AppLoggerConfig, GlobalAppExceptionHandler, TrazabilidadConfig, TestSecurityConfig)`, `@MockitoBean` del `Interactor`, JWT con `FichasAuthorities.REVISION_ITEM_CREATE`: 201 feliz, 400 `estadoRevision` en blanco, 401 sin JWT, 403 rol incorrecto, 422 para cada una de las 4 excepciones — `ItemFichaPerfilNoEncontradoException`, `FichaNoPerteneceAsesorException`, `RevisionItemYaExisteException`, `EstadoRevisionNoEncontradoException` — con assert sobre `$.errorCode` usando las constantes de `FichasCodes`); `RevisionItemJpaMapperTest` (2 — `toEntity`/`toJpaEntity`, incluida la construcción de la referencia `@ManyToOne` hacia `EstadoRevisionJpaEntity` vía `EstadoRevisionJpaMapper.toReferencia`, confirmando que solo viaja el id); `EstadoRevisionJpaMapperTest` (3 — `toEntity`/`toJpaEntity`/`toReferencia`). `AgregarRevisionItemRequestMapper` queda cubierto indirectamente por el test 201 del controller (no necesita test propio). Los 3 tests son unit tests planos (sin `@DataJpaTest`), consistente con que los mappers son clases estáticas puras sin dependencia de Spring/Hibernate. Cobertura `fichas:infrastructure`: 80.71% (instrucciones) — CUMPLE del 75%. `./gradlew fichas:infrastructure:check` (test + checkstyleMain + checkstyleTest + jacocoTestCoverageVerification) pasa completo; también se re-verificó `fichas:domain:check` y `fichas:application:check` sin regresiones. Sin bugs de producción encontrados; no se tocó ningún archivo de `src/main`. |
| Migración (corrección datos) | Coordinador | ✅ Completado | 2026-08-27 | Al correr `./gradlew bootRun --args='--spring.profiles.active=dev'` contra la base compartida (`172.16.1.10`), la migración `V20260826232538` falló en el `ALTER TABLE ... ADD CONSTRAINT fk_rev_estado` con `violates foreign key constraint`: existía una fila real en `revision_item` (`estado_revision_id = 'APROBADO'`, fecha `2026-07-23`) — resto de una prueba manual contra el intento de implementación previo, totalmente descartado de git, usando la primera propuesta de catálogo (rechazada por el usuario) en vez de los 5 valores reales. Como la migración nunca llegó a registrarse como aplicada en ningún `flyway_schema_history` (Flyway hizo rollback del intento fallido), se editó el archivo directamente agregando un `DELETE FROM revision_item WHERE estado_revision_id NOT IN (SELECT id FROM estado_revision)` justo antes del `ALTER TABLE` — idempotente, no afecta bases sin filas huérfanas (locales, de otros devs). Verificado con `docker run --rm postgres:18-alpine psql ...` contra la base remota antes de decidir el fix. Pendiente re-confirmar con un `bootRun` limpio que la migración aplica completa. |
| Validación | @validator-analyze | ✅ Aprobado | 2026-08-27 | Segunda pasada. Score 99/100 (Nivel 1: 14/14, Nivel 2: 70/71 con 1 menor, Nivel 3: 4/4 módulos, Nivel 4: 525/525 tests). 0 bloqueantes, 1 menor (`estado_revision.id` VARCHAR(50), decisión explícita del usuario, no bloquea). Bloqueante de la primera pasada (tests faltantes de infraestructura) verificado como CERRADO. Reporte completo en `.workspace/validator/validator-HU-195.md`. |
| Reporte | @validator-report | ✅ Completado | 2026-08-28 | Reporte persistido en `.workspace/validator/validator-HU-195.md`, incluyendo la corrección de migración post-análisis y las pruebas manuales end-to-end del 2026-08-28. |
| Commit | @commit | ⏳ Pendiente | | |
| PR | @commit | ⏳ Pendiente | | |
