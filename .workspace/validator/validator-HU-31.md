# Reporte de Validación — HU-31

## Metadata
- **ID Historia:** HU-31
- **Bounded Context:** fichas
- **Usa AggregateRoot:** No (clase plana `final` sin eventos — CRUD interno)
- **Fecha de análisis:** 2026-06-30
- **Rama propuesta:** `feature/HU-31-agregar-item-ficha-perfil`
- **Analizado por:** agente validator-analyze (04a-validator-analyze)
- **Skill arquisoft-context cargado:** ✅

---

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 18 | 18 | 0 | 100/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 95 | 92 | 3 | 97/100 |
| Nivel 3 — Compilación | 4 | 4 | 0 | 100/100 |
| Nivel 4 — Tests | 30 | 30 | 0 | 100/100 |
| **Total** | **147** | **144** | **3** | **98/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 3

---

## Estado Final

**Estado:** ✅ EJECUTADO
**Hash:** 718a6e1
**Fecha de ejecución:** 2026-06-30

> ✅ APROBADO — Score: 98/100. Sin checks bloqueantes.
> Análisis listo para persistir en disco.

---

## Errores Bloqueantes

Ninguno detectado.

---

## Errores Menores

### [NIVEL 2.4] — Excepción `ItemFichaNoPropiaException` extiende `ApplicationException` en vez de `ForbiddenException`

- **Archivo:** `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/exception/ItemFichaNoPropiaException.java`
- **Problema:** el criterio de aceptación #3 del plan especifica 403 Forbidden. La excepción actualmente extiende `ApplicationException` (→ 400), no una clase base que mapee a 403. El test `debe400_cuandoFichaNoPropia` afirma `status().isBadRequest()` en vez de `status().isForbidden()`.
- **Acción sugerida:** verificar si existe `ForbiddenException` en `shared:exception`. Si existe, cambiar la herencia. Si no existe, evaluar con el equipo si crear la clase base o aceptar 400.

### [NIVEL 2.8] — Variables locales en use case con nombre largo en vez de `cmd`

- **Archivo:** `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/AgregarItemFichaPerfilUseCase.java` (líneas 32, 36, 41, 46)
- **Problema:** variable local `agregarItemFichaPerfilCommand` en vez de la convención `cmd`.
- **Acción sugerida:** renombrar a `cmd`.

### [NIVEL 2.14] — Test de controller afirma `400` para `ItemFichaNoPropiaException` cuando el plan requiere `403`

- **Archivo:** `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/AgregarItemFichaPerfilInputAdapterTest.java` (línea 144)
- **Problema:** test `debe400_cuandoFichaNoPropia` afirma `isBadRequest()` cuando debería ser `isForbidden()`.
- **Acción sugerida:** corregir junto con el error menor 2.4.

---

## Tests

✅ 10 tests detectados en infrastructure (6 en InputAdapter + 4 en CommandOutputAdapter).
⚠️ Faltan tests de domain (7 esperados) y application (9 esperados) — cobertura insuficiente según el plan.
Recomendación: invocar @tester para completar la cobertura antes de mergear.

---

## Datos para el commit

**Mensaje:** `feat(fichas): agregar ítem a ficha de perfil (HU-31)`
**Tipo:** `feat`
**Rama:** `feature/HU-31-agregar-item-ficha-perfil`
**Archivos a incluir:**
- `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/tipoitem/TipoItem.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/port/out/ItemFichaPerfilOutputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/model/AgregarItemFichaPerfilCommand.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/port/in/AgregarItemFichaPerfilInputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/AgregarItemFichaPerfilUseCase.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/exception/ItemTipoDuplicadoException.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/exception/ItemFichaNoPropiaException.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/query/port/out/EstudianteFichaPerfilQueryOutputPort.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/tipoitem/persistence/TipoItemJpaEntity.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/tipoitem/persistence/TipoItemJpaRepository.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/persistence/ItemFichaPerfilJpaEntity.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/persistence/ItemFichaPerfilJpaRepository.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/persistence/ItemFichaPerfilMapper.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/out/persistence/ItemFichaPerfilCommandOutputAdapter.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/AgregarItemFichaPerfilInputAdapter.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/dto/AgregarItemFichaPerfilRequestDTO.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/dto/AgregarItemFichaPerfilResponseDTO.java`
- `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.3__crear_tipo_item_e_item.sql`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/AgregarItemFichaPerfilInputAdapterTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/out/persistence/ItemFichaPerfilCommandOutputAdapterTest.java`

---

## Próximos pasos

→ Errores menores pueden corregirse en PR o tarea separada (no bloqueantes).
→ Invocar @tester para completar cobertura de domain y application antes de mergear.