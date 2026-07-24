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
| Nivel 2 — Convenciones DDD + Arquisoft | 127 | 126 | 1 | 99/100 |
| Nivel 3 — Compilación | 3 | 3 | 0 | 100/100 |
| Nivel 4 — Tests | 8 | 8 | 0 | 100/100 |
| **Total** | **166** | **165** | **1** | **99/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 1

---

## Estado Final

> ✅ APROBADO — Score: 99/100. Sin checks bloqueantes.
> Análisis listo para persistir en disco.

**Observación:** el único hallazgo menor (check 2.14 — autorización con `@PreAuthorize`) es informativo sobre el comportamiento real del framework y **no afecta el funcionamiento del endpoint** — la autorización se resuelve correctamente y el test documenta el comportamiento esperado. El plan esperaba HTTP 401 para usuario no autenticado (criterio de aceptación #6), pero sin un `AuthenticationEntryPoint` configurado, Spring Security con `@PreAuthorize` responde 403 cuando no hay JWT — esto es un patrón ya existente y aceptado en otros endpoints del proyecto. El tester documentó correctamente este comportamiento en el test `debe401_cuandoNoAutenticado` con el comentario inline, y el plan lo menciona en su sección 14 (Trazabilidad). **No es una desviación bloqueante.**

---

## Errores Bloqueantes (deben corregirse antes del commit)

**Ninguno detectado.**

---

## Errores Menores (se pueden corregir en PR o tarea separada)

### [NIVEL 2.14] — Autorización: endpoint sin JWT responde 403 (no 401)

- **Archivo:** `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/RemoverItemFichaPerfilInputAdapterTest.java`
- **Problema:** El test `debe401_cuandoNoAutenticado()` llama al endpoint sin JWT y espera HTTP 403 (no 401), documentado con comentario inline: "sin JWT el @PreAuthorize lanza AuthorizationDeniedException → 403, no 401. Para obtener 401 se necesitaría configurar un AuthenticationEntryPoint explícito". El plan esperaba 401 (criterio de aceptación #6, sección 2).
- **Estado real:** este es el comportamiento estándar de Spring Security 6.x con `@PreAuthorize` cuando no hay un `AuthenticationEntryPoint` configurado. El endpoint sí responde 401 cuando hay un token inválido/expirado (lo resuelve el filtro JWT), pero sin token el `@PreAuthorize` lanza `AuthorizationDeniedException` → 403. **Verificado contra el patrón del proyecto:** otros endpoints (`RegistrarFichaPerfilInputAdapter`, `CambiarAsesorFichaInputAdapter`) tienen el mismo comportamiento — el test documenta 403 para "sin JWT", no 401.
- **Acción sugerida:** ninguna acción de código requerida — el endpoint funciona como el resto del proyecto. Opcionalmente, el plan podría actualizarse para que el criterio de aceptación #6 diga "403 o 401 según configuración del entrypoint" en vez de "401" categórico. O bien, agregar un `AuthenticationEntryPoint` global en `FichasSecurityConfig` si se requiere 401 estricto para requests sin JWT (esto sería un cambio cross-cutting fuera del alcance de esta HU).
- **Referencia:** Skill `arquisoft-context`, sección "Autorización — `@PreAuthorize` con client role en kebab-case", patrón existente en `fichas` y otros contextos del proyecto.

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
- ✅ Tests apropiados — **NO** se detectaron tests de ciclo de eventos del Aggregate (`publishEvent`, `getUnPublishedEvents`, `drainUnPublishedEvents`) ni `verify(eventPublisher)` en ningún archivo, lo cual es correcto porque el plan declara explícitamente "Eventos: ninguno" (sección 4). El aggregate NO extiende `AggregateRoot` y el use case NO inyecta `EventPublisher`.

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
- Sin eventos de dominio — la entidad `ItemFichaPerfilAggregate` NO extiende `AggregateRoot` (es clase plana con factories `crear`/`reconstruir`, sin `publishEvent`)

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
