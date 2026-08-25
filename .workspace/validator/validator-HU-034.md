# Reporte de Validación — HU-034

## Metadata
- **ID Historia:** HU-034
- **Bounded Context:** fichas
- **Usa AggregateRoot:** No — la HU no emite eventos, es CRUD interno sin consumidores ni auditoría. La entidad `ItemFichaPerfilAggregate` ya existe como clase plana con factories `crear`/`reconstruir` y NO extiende `AggregateRoot`.
- **Fecha de análisis:** 2026-07-24
- **Rama propuesta:** `feature/HU-034-remover-informacion-item`
- **Analizado por:** agente validator-analyze (04a-validator-analyze)
- **Skill arquisoft-context cargado:** ✅

---

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 28 | 28 | 0 | 100/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 127 | 127 | 0 | 100/100 |
| Nivel 3 — Compilación | 3 | 3 | 0 | 100/100 |
| Nivel 4 — Tests | 8 | 8 | 0 | 100/100 |
| **Total** | **166** | **166** | **0** | **100/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 0

---

## Estado Final

> ✅ APROBADO — Score: 100/100. Sin checks bloqueantes ni menores.
> Análisis listo para persistir en disco.

**Estado:** ✅ EJECUTADO  
**Hash:** 3fa0237  
**Fecha de ejecución:** 2026-07-24

**Corrección post-commit (2026-07-24):** el hallazgo menor original (check 2.14 — endpoint sin JWT respondía 403 en vez de 401) fue corregido. La causa raíz era el `TestSeguridadConfig` de `RemoverItemFichaPerfilInputAdapterTest`, que usaba `.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())` — esto dejaba pasar la request sin autenticación hasta el `@PreAuthorize`, que ante un usuario anónimo lanza `AuthorizationDeniedException` → 403 en vez de disparar el flujo de autenticación. Se corrigió alineando el `TestSeguridadConfig` con el patrón ya establecido en el proyecto (p. ej. `ModificarItemFichaPerfilInputAdapterTest`): `.anyRequest().authenticated()` + `.exceptionHandling(...)` con `authenticationEntryPoint` (401) y `accessDeniedHandler` (403) explícitos. Con el fix, `debe401_cuandoNoAutenticado` verifica `status().isUnauthorized()` y pasa correctamente. Verificado con `./gradlew :fichas:infrastructure:test --tests "*RemoverItemFichaPerfilInputAdapterTest*"` (6/6 tests OK) y `./gradlew :fichas:infrastructure:check` (BUILD SUCCESSFUL, gate de cobertura y checkstyle incluido).

---

## Errores Bloqueantes (deben corregirse antes del commit)

**Ninguno detectado.**

---

## Errores Menores (se pueden corregir en PR o tarea separada)

**Ninguno.** El hallazgo original (check 2.14) fue corregido — ver "Corrección post-commit" en la sección Estado Final.

---

## Tests

✅ Tests ejecutados según trazabilidad del plan (sección 14).

**Tests totales detectados:** 17 tests en 6 archivos
**Presupuesto orientativo:** 15-25 tests (HU pequeña — 1 endpoint, 1 entidad)
**Estado de presupuesto:** ✅ dentro del rango

| Archivo | Tests |
|---------|-------|
| `ItemFichaPerfilAggregateTest` (ampliado) | 2 nuevos (POL-05) + 13 existentes = 15 total |
| `RemoverItemFichaPerfilUseCaseTest` | 4 |
| `ItemFichaPerfilCommandOutputAdapterTest` (ampliado) | 1 nuevo (eliminarPorId) + 6 existentes = 7 total |
| `RevisionItemQueryOutputAdapterTest` | 2 |
| `RevisionItemJpaRepositoryTest` (`@DataJpaTest`) | 2 |
| `RemoverItemFichaPerfilInputAdapterTest` (`@WebMvcTest`) | 6 |

**Anti-patrones detectados (sección 2.11):** Ninguno detectado.

**Tests que afirman 500 (sección 2.12):** Ninguno detectado — todas las excepciones extienden la clase base correcta (`ApplicationException` → 400, `AuthorizationException` → 403, `DomainValidationException` → 422).

**Tests apropiados para Tipo de UC (sección 2.13):**
- Tipo de UC declarado en plan: **Escritura**
- ✅ Tests apropiados — **NO** se detectaron tests de ciclo de eventos del Aggregate (`publicarEvento`, `obtenerEventosSinPublicar`, `extraerEventosSinPublicar`) ni `verify(eventPublisher)` en ningún archivo, lo cual es correcto porque el plan declara explícitamente "Eventos: ninguno" (sección 4). El aggregate NO extiende `AggregateRoot` y el use case NO inyecta `EventPublisher`.

---

## Datos para el commit

**Mensaje:** feat(fichas): remover ítem de ficha perfil — HU-034

**Cuerpo del mensaje:**
- Implementa DELETE físico de un ítem de ficha perfil con validación POL-05 (no remover si tiene revisiones)
- Endpoint REST: `DELETE /fichas-perfil/items/{itemId}` → 204 No Content (autorización por client role `fichas:item-ficha-perfil:delete` + propiedad de la ficha)
- Dominio: método `ItemFichaPerfilAggregate.removerse(long totalRevisiones)` con invariante POL-05 usando `ValidationResult` → `DomainValidationException` → 422 con `fieldErrors[]`
- Application: `RemoverItemFichaPerfilUseCase` orquesta existencia (→ 400), propiedad (→ 403), invariante (→ 422) y eliminación física
- Infrastructure: `RevisionItemJpaEntity` + `RevisionItemJpaRepository.countByItemId(itemId)` + `RevisionItemQueryOutputAdapter` para validar POL-05. `ItemFichaPerfilCommandOutputAdapter.eliminarPorId(itemId)` delega a `jpaRepository.deleteById`
- Migración Flyway: `V1.8__crear_revision_item.sql` con FK `ON DELETE CASCADE` sobre `item(id)`, constraint unique `(item_id, fecha_creacion)`
- Catálogo `shared:message`: constantes agregadas a `FichasMessages.ItemFichaPerfil` (CAMPO_REVISIONES, ITEM_NO_ENCONTRADO, ITEM_NO_ENCONTRADO_MSG, ITEM_CON_REVISIONES, ITEM_CON_REVISIONES_MSG, LOG_REMOVIDO)
- Tests: 17 tests (2 domain, 4 application, 11 infrastructure) — cobertura: domain 94%, application 89%, infrastructure 88% (todas ≥75%, gate `check` pasó)
- Sin eventos de dominio — la entidad `ItemFichaPerfilAggregate` NO extiende `AggregateRoot` (es clase plana con factories `crear`/`reconstruir`, sin `publicarEvento`)

**Tipo:** `feat`
**Rama:** `feature/HU-034-remover-informacion-item`
**Archivos a incluir:**
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/port/out/ItemFichaPerfilOutputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/model/RemoverItemFichaPerfilCommand.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/port/in/RemoverItemFichaPerfilInputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/RemoverItemFichaPerfilUseCase.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/exception/ItemFichaPerfilNoEncontradoException.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/revisionitem/query/port/out/RevisionItemQueryOutputPort.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/RemoverItemFichaPerfilInputAdapter.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/out/persistence/ItemFichaPerfilCommandOutputAdapter.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/query/adapter/out/persistence/RevisionItemQueryOutputAdapter.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/persistence/RevisionItemJpaEntity.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/revisionitem/persistence/RevisionItemJpaRepository.java`
- `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.8__crear_revision_item.sql`
- `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java`
- `fichas/domain/src/test/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregateTest.java`
- `fichas/application/src/test/java/com/arquisoft/fichas/application/itemfichaperfil/command/RemoverItemFichaPerfilUseCaseTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/out/persistence/ItemFichaPerfilCommandOutputAdapterTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/revisionitem/query/adapter/out/persistence/RevisionItemQueryOutputAdapterTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/revisionitem/persistence/RevisionItemJpaRepositoryTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/RemoverItemFichaPerfilInputAdapterTest.java`
- `.workspace/h-plan/PLAN-HU-034.md`
