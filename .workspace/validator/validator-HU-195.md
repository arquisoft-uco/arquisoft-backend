# Reporte de Validación — HU-195 (segunda pasada)

## Metadata
- **Bounded Context:** fichas
- **Fecha:** 2026-08-27 · **Rama:** `feature/HU-195-agregar_revision_item`

## Score
| Nivel | Checks | Pasados | Fallados | Score |
|---|---|---|---|---|
| 1 — Completitud | 14 | 14 | 0 | 100% |
| 2 — Convenciones DDD + Arquisoft | 71 | 70 | 0 (1 ⚠️) | 99% |
| 3 — Compilación | 4 módulos | 4 | 0 | 100% |
| 4 — Tests | 525 tests / 3 módulos | 525 | 0 | 100% |
| **Total** | | | | **99/100** |

**Bloqueantes:** 0 · **Menores:** 1

## Estado Final
✅ **APROBADO** — sin bloqueantes.

## Verificación del bloqueante de la primera pasada (tests faltantes) — CERRADO
Se agregaron 13 tests nuevos: `AgregarRevisionItemControllerTest` (8 — `@WebMvcTest` real, JWT con `FichasAuthorities.REVISION_ITEM_CREATE`, los 4 casos 422 pasan realmente por `GlobalAppExceptionHandler` con assert sobre `status()` y `$.errorCode`), `RevisionItemJpaMapperTest` (2, incluida la construcción de la referencia `@ManyToOne` solo-con-id), `EstadoRevisionJpaMapperTest` (3). Verificado con `--rerun-tasks` (sin caché): 525 tests, 0 fallos. Cobertura: `fichas:domain` 80.79%, `fichas:application` 84.49%, `fichas:infrastructure` 80.71% — los 3 CUMPLEN ≥75%.

## Verificación de los 3 menores de la primera pasada — CERRADOS
1. `FichaNoPerteneceAsesorException` ahora recibe el UUID correcto de la ficha (no el del ítem), resuelto vía `fichaPerfilDelItemFinder` en `AgregarRevisionItemUseCaseImpl`.
2. Ruta `item-revisiones: /items/{itemId}/revisiones` agregada a `rutas-fichas.yml`.
3. Constantes `FichasFields.RevisionItem.REVISION_ITEM`/`ASESOR_FICHA` creadas y usadas correctamente en `AgregacionRevisionItemDomain` y `AgregarRevisionItemCommand` (barrido completo confirmó cero casos restantes de constante prestada de otro feature).

## Verificación del cambio de firma del Validator
`AgregarRevisionItemValidator.validar(entrada, itemExiste, fichaPerfil, esPropietario, revisionYaExiste)` sigue siendo puro (sin `if`, sin dependencias inyectadas), orden de validación intacto (existencia → propiedad → unicidad), y el patrón de centinela `UtilUUID.obtenerUUIDPorDefecto()` es consistente con `ModificarItemFichaPerfilUseCaseImpl`/`RemoverItemFichaPerfilUseCaseImpl`.

## Errores Menores
### [Nivel 2.10] — `estado_revision.id` en VARCHAR(50) en vez de VARCHAR(60)
Decisión explícita del usuario (no un descuido), documentada en el plan con ruta de reversión clara (`ALTER COLUMN ... TYPE VARCHAR(60)`, migración aditiva). No bloquea.

## Corrección adicional post-análisis (encontrada en pruebas manuales, 2026-08-27/28)
Al correr `bootRun` contra la base compartida, la migración `V20260826232538__crear_estado_revision.sql` falló en el `ALTER TABLE ... ADD CONSTRAINT fk_rev_estado`: existía una fila real en `revision_item` con `estado_revision_id = 'APROBADO'` (resto de una prueba manual contra el intento de implementación previo, totalmente descartado de git, que usaba la primera propuesta de catálogo rechazada por el usuario). Como la migración nunca quedó registrada como aplicada en ningún `flyway_schema_history` (Flyway hizo rollback del intento fallido), se editó el archivo agregando `DELETE FROM revision_item WHERE estado_revision_id NOT IN (SELECT id FROM estado_revision)` antes del `ALTER TABLE` — idempotente, verificado con una transacción de prueba (`BEGIN; ...; ROLLBACK;`) contra la base real antes de aplicarlo de verdad. Confirmado funcionando en un `bootRun` posterior: `Successfully applied 1 migration to schema "public", now at version v20260826232538`. Detalle completo en la fila "Migración (corrección datos)" de la sección 14 del plan.

## Pruebas manuales end-to-end (Postman/Swagger, 2026-08-28)
El usuario ejecutó manualmente los 9 caminos del endpoint (201 feliz, 401, 403, 400×2, 422×4) contra la base compartida real, con datos de prueba insertados a propósito (asesor, ficha propia, ficha ajena, ítem con revisión previa). Confirmó que la funcionalidad se comporta como se esperaba en todos los casos.

## Datos para la entrega
**Mensaje:** `feat(fichas): agregar revisión a un ítem de ficha de perfil`
**Rama:** `feature/HU-195-agregar_revision_item`
**Archivos a incluir:** todo el árbol de la sección 6 del plan + `.workspace/h-plan/PLAN-HU-195.md` + `.workspace/validator/validator-HU-195.md`
