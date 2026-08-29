# Reporte de Validación — HU-196

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
