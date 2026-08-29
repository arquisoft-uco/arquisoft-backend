# Reporte de Validación — HU-196

Este archivo contiene dos rondas de validación: la validación original completa de la implementación
de HU-196, y una re-validación enfocada posterior sobre un refactor puntual aplicado sobre esa misma
rama. Ambas rondas concluyeron **✅ APROBADO**.

---

# Ronda 1 — Validación Original (2026-08-28)

## Metadata
- **Bounded Context:** `fichas`
- **Fecha:** 2026-08-28 · **Rama propuesta:** `feature/HU-196-modificar_revision_item` (ya existe, commit `ebc78a61`)

## Score

| Nivel                            | Checks                                                       | Pasados | Fallados | Score        |
|----------------------------------|--------------------------------------------------------------|---------|----------|--------------|
| 1 — Completitud                  | 14 aplicables                                                | 14      | 0        | 100%         |
| 2 — Convenciones DDD + Arquisoft | ~45 aplicables                                               | 44      | 0 (1 ⚠️) | 98%          |
| 3 — Compilación                  | 2 (`fichas:*:build`, `shared:message:build`)                 | 2       | 0        | 100%         |
| 4 — Tests                        | Suite completa (13 archivos, `check` verde en los 3 módulos) | —       | —        | ✅ Completado |
| **Total**                        |                                                              |         |          | **99/100**   |

**Bloqueantes:** 0 · **Menores:** 1

## Estado Final
> ✅ APROBADO — sin bloqueantes.

## Errores Bloqueantes

Ninguno.

## Errores Menores

### [Nivel 2.1] — Método de escritura sin log en `RevisionItemCommandOutputAdapter`
- **Archivo:** `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/command/secondaryadapter/repository/RevisionItemCommandOutputAdapter.java`
- **Problema:** `registrarRevision(...)` (método de escritura) no invoca `logger.debug(...)`, mientras que el nuevo `actualizarEstado(...)` sí lo hace. La regla de CLAUDE.md exige log en todo método de escritura del `CommandOutputAdapter`.
- **Contexto que atenúa la severidad:** el plan (Sección 7) documenta explícitamente esta decisión: `AgregarRevisionItemUseCaseImpl` ya logea `RevisionItemKey.LOG_AGREGADO` desde el `UseCase` tras `registrarRevision(...)` (confirmado leyendo el archivo — línea `logger.info(Mensajes.obtener(RevisionItemKey.LOG_AGREGADO), ...)`), así que agregar el log también en el adaptador habría sido una duplicación literal del mismo evento. La HU-196 solo agregó `AppLogger` al adaptador para poder loguear su propio método nuevo (`actualizarEstado`), y el plan pidió verificar este punto antes de replicar el log en `registrarRevision` — se verificó y se dejó sin duplicar, tal como el plan anticipaba como resultado válido.
- **Referencia:** Nivel 2.1, tabla "Método de escritura del `CommandOutputAdapter` sin log ⚠️".

## Tests

- **Estado:** ✅ Completado (fila `Tests` del plan).
- **Conteo:** 13 archivos de test nuevos para HU-196 (dominio: 3, application: 5, infrastructure: 5), dentro del presupuesto declarado (20-28 tests para tamaño "Pequeña").
- **Anti-patrones (Nivel 2.13):** ninguno detectado. Revisé los 7 patrones prohibidos — no hay tests de getter/setter Lombok, no hay un test por campo obligatorio (los tests de `Command`/`Domain` acumulan `fieldErrors[]` en un solo test, ej. `debeAcumularLosTresErrores_cuandoTodosLosCamposSonInvalidos`), no hay tests de métodos `private`, no hay duplicación de Act sin consolidar asserts, no hay tests de excepción con solo `super(...)`, no hay tests de equals/hashCode/toString de Lombok. Los dos tests de `ModificarRevisionItemInteractorImplTest` que usan solo `verify(...)` son la única forma posible de asertar un `VoidInteractor` (no hay valor de retorno) y el segundo ya complementa con `ArgumentCaptor` sobre el mapeo — no califican como "delegación pura vacía".
- **Tests que afirman 500:** ninguno. El `@WebMvcTest` cubre 204/400/401/403/422 correctamente, sin ningún caso que espere `isInternalServerError()` para un input inválido.
- **Coherencia Tipo de UC:** Metadata declara **Escritura** con evento (`RevisionItemModificadoEvent`, lado productor). `ModificarRevisionItemUseCaseImplTest.debePublicarElEventoConLosDatosDeLaModificacion...` verifica `eventPublisher.publish(...)` con `ArgumentCaptor` — correcto, coherente con la HU.
- **Slice de infraestructura:** `ModificarRevisionItemControllerTest` importa `AppLoggerConfig`, `GlobalAppExceptionHandler`, `TrazabilidadConfig` y su `TestSecurityConfig` local, exactamente como `RegistrarFichaPerfilControllerTest`. Usa `@MockitoBean` (no `@MockBean`) y `SecurityMockMvcRequestPostProcessors.jwt().authorities(...)` con la authority exacta (no `@WithMockUser`).
- **`@DataJpaTest`:** `RevisionItemCommandRepositoryTest` siembra con `EntityManager`/SQL nativo y verifica tanto el `UPDATE` (`actualizarEstadoRevision`) como que no afecta otras filas — cubre el caso "no debe afectar otras filas" que corresponde al aislamiento de la actualización parcial.
- **Cobertura reportada por el plan:** domain 79.95%, application 81.76%, infrastructure 76.44% — todas ≥75%. El `check` de los tres módulos corrió `UP-TO-DATE` en mi verificación, confirmando que ya se había ejecutado exitosamente.

## Datos para la entrega

**Mensaje:** `feat(fichas): modificar estado de revisión de ítem de ficha de perfil`
**Cuerpo:**
- Agrega `PATCH /fichas-perfil/items/{itemId}/revisiones` para que el Asesor Ficha dueño modifique el `EstadoRevision` de una revisión ya existente (modelo 1:1 por `itemId`).
- Capas: `domain` (`ModificacionRevisionItemDomain`, `RevisionItemExisteRule`/`Impl`, `RevisionItemNoEncontradaException`, `RevisionItemModificadoEvent`), `application` (`ModificarRevisionItemCommand`/`Mapper`/`Interactor`/`UseCase`/`Validator`, `RevisionItemOutputPort.actualizarEstado`), `infrastructure` (`ModificarRevisionItemController`/DTO/RequestMapper, `RevisionItemCommandRepository.actualizarEstadoRevision`, `RevisionItemCommandOutputAdapter` con `AppLogger` agregado).
- Evento `RevisionItemModificadoEvent` publicado (lado productor completo, `fichas.revision_item.modificado`); consumidor en `notificaciones` fuera de alcance por decisión explícita del usuario (sin cola/binding, no falla nada, tampoco notifica hasta HU de seguimiento).
- Sin migración Flyway — reutiliza `revision_item`/`estado_revision` de HU-195.
- Client role nuevo: `fichas:revision-item:update`.

**Rama:** `feature/HU-196-modificar_revision_item`
**Archivos a incluir:** los 38 archivos nuevos/modificados listados en `git diff --stat a0afbe8c..ebc78a61` (domain/application/infrastructure/shared:message/catalogo de HU-196) + `.workspace/h-plan/PLAN-HU-196.md` + `.workspace/validator/validator-HU-196.md`
**Endpoints documentados:** Sí (`@Tag`/`@Operation`/`@ApiResponses`/`@SecurityRequirement` completos en `ModificarRevisionItemController`)

---

# Ronda 2 — Re-Validación del Refactor (2026-08-29)

## Metadata
- **Bounded Context:** fichas
- **Fecha:** 2026-08-29 · **Rama:** `feature/HU-196-modificar_revision_item`
- **Plan validado:** `.workspace/h-plan/PLAN-HU-196.md` (vigente)
- **Alcance de esta validación:** re-validación enfocada sobre un refactor puntual aplicado tras la validación previa (99/100, APROBADO — Ronda 1 arriba). Cubre exactamente los 8 archivos modificados por el diff.

## Qué cambió

`boolean revisionExiste` → `long cantidadRevisiones` en toda la cadena `Finder → Validator → Rule`, moviendo la decisión "¿cuántas revisiones cuentan como existencia?" del `UseCaseImpl` a `RevisionItemExisteRuleImpl.validar` (`if (existencia.cantidadRevisiones() <= 0) throw ...`). El `ModificarRevisionItemUseCaseImpl` ya no calcula el `> 0`; solo pasa el `long` crudo del finder.

## Score (checks aplicables al diff)

| Check | Resultado |
|---|---|
| `Rule` pura, sin dependencias de constructor, no bean (`RevisionItemExisteRuleImpl`) | ✅ |
| `Validator` sin un solo `if`, constructor sin argumentos con `new` de sus 2 Rules | ✅ |
| `UseCaseImpl` no decide — pasa el dato crudo del finder, sin `> 0` propio | ✅ |
| Orden de validación (existencia → propiedad) preservado y testeado explícitamente | ✅ |
| `long` explícito en el `UseCaseImpl` (no `var`) al recibir el resultado del finder | ✅ |
| Tests del `Rule`: sin Mockito (pura), naming `debeHacerAlgo_cuandoCondicion`, AAA | ✅ |
| Tests del `Validator`: sin mocks (orquesta Rules reales), incluye test de orden con doble fallo | ✅ |
| Tests del `UseCase`: `InOrder`, `ArgumentCaptor`, casos de excepción — coherentes con el nuevo tipo `long` | ✅ |
| Sin referencias huérfanas al `boolean` antiguo en el resto del módulo | ✅ |
| Compilación + tests + Checkstyle + Jacoco (`:fichas:domain:build :fichas:application:build :fichas:infrastructure:build`) | ✅ BUILD SUCCESSFUL |

**Bloqueantes:** 0 · **Menores:** 1 (heredado de la validación anterior, no introducido por este refactor)

## Estado Final

> ✅ **APROBADO** — sin bloqueantes.

## Errores Menores

### [Nivel 2.13] — Tests duplicados con el mismo Act sin consolidar (pre-existente)
- **Archivo:** `fichas/application/src/test/java/com/arquisoft/fichas/application/revisionitem/command/usecase/impl/ModificarRevisionItemUseCaseImplTest.java`
- **Problema:** `debeActualizarElEstado_cuandoDatosValidos()` y `debeActualizarElEstado_cuandoEsElMismoValorYaVigente()` tienen Arrange y Act idénticos; el segundo solo agrega `verify(eventPublisher)`. Podrían consolidarse.
- **Nota:** confirmado por `git diff` que ya existía antes del refactor (no fue introducido ni señalado en la validación original). No afecta el veredicto.

## Consistencia `ExistenciaRevisionItem` vs. `DisponibilidadRevisionItem`

Ambos records son estructuralmente idénticos (`UUID item, long cantidadRevisiones`) y viven en `domain/revisionitem/model/`, pero alimentan Rules semánticamente distintas (`RevisionItemExisteRule` vs `RevisionItemNoDuplicadaRule`). No es una inconsistencia a corregir — es el patrón ya establecido en el proyecto de nombrar el record de entrada por lo que la Rule decide, no por su forma. El refactor deja a `ModificarRevisionItemValidatorImpl` alineado con el patrón que ya usaba `AgregarRevisionItemValidatorImpl` (HU-195) en el mismo contexto — antes del cambio, Modificar era el único de los dos flujos desviado.

## Tests

Los 3 archivos de test tocados (`RevisionItemExisteRuleImplTest`, `ModificarRevisionItemValidatorTest`, `ModificarRevisionItemUseCaseImplTest`) actualizados coherentemente con el cambio `boolean`→`long`. No hay anti-patrones nuevos. No toca capa web ni contratos HTTP.

## Datos para la entrega

**Mensaje:** `refactor(fichas): mueve la decisión de existencia de revisión del use case al Rule de dominio`
**Cuerpo:**
- `ExistenciaRevisionItem` pasa de `boolean existe` a `long cantidadRevisiones`, siguiendo el mismo patrón de `DisponibilidadRevisionItem`.
- `RevisionItemExisteRuleImpl` ahora decide `cantidadRevisiones <= 0` en vez de recibir un booleano ya calculado.
- `ModificarRevisionItemValidator`/`Impl` y `ModificarRevisionItemUseCaseImpl` actualizados para pasar el `long` crudo del finder, sin ningún `if` en el use case.
- Tests actualizados a la nueva firma.
- Sin cambios de eventos, endpoints ni migración.

**Rama:** `feature/HU-196-modificar_revision_item`
**Archivos de este refactor:**
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/model/ExistenciaRevisionItem.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/revisionitem/rules/impl/RevisionItemExisteRuleImpl.java`
- `fichas/domain/src/test/java/com/arquisoft/fichas/domain/revisionitem/rules/impl/RevisionItemExisteRuleImplTest.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/validator/ModificarRevisionItemValidator.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/validator/impl/ModificarRevisionItemValidatorImpl.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/command/usecase/impl/ModificarRevisionItemUseCaseImpl.java`
- `fichas/application/src/test/java/com/arquisoft/fichas/application/revisionitem/command/validator/ModificarRevisionItemValidatorTest.java`
- `fichas/application/src/test/java/com/arquisoft/fichas/application/revisionitem/command/usecase/impl/ModificarRevisionItemUseCaseImplTest.java`
