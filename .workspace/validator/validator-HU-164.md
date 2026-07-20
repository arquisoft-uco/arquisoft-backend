# Reporte de Validación — HU-164

## Metadata
- **ID Historia:** HU-164
- **Bounded Context:** fichas
- **Usa AggregateRoot:** No
- **Fecha de análisis:** 2026-07-15
- **Rama propuesta:** `feature/HU-164-cambiar-asesor-ficha-perfil`
- **Analizado por:** agente validator-analyze (04a-validator-analyze)
- **Skill arquisoft-context cargado:** ✅
- **Estado:** ✅ EJECUTADO
- **Hash:** efd6737
- **Fecha de ejecución:** 2026-07-15

---

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 21 | 21 | 0 | 100/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 52 | 52 | 0 | 100/100 |
| Nivel 3 — Compilación | 4 | 4 | 0 | 100/100 |
| Nivel 4 — Tests | 7 | 7 | 0 | 100/100 |
| **Total** | **84** | **84** | **0** | **100/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 0

---

## Estado Final

> ✅ APROBADO — Score: 100/100. Sin checks bloqueantes.

---

## Errores Bloqueantes

Ninguno detectado.

## Errores Menores

Ninguno detectado.

---

## Tests

✅ Tests ejecutados según trazabilidad del plan (sección 13).

**Tests totales detectados:** 18 tests en 5 archivos
**Presupuesto orientativo:** 15-25
**Estado de presupuesto:** dentro del rango ✅
**Anti-patrones detectados:** Ninguno
**Tests que afirman 500:** Ninguno

---

## Datos para el commit

**Mensaje:** feat(fichas): cambiar asesor ficha perfil

**Cuerpo del mensaje:**
- Endpoint `PATCH /fichas-perfil/{id}/asesor-ficha` que permite al Coordinador cambiar el asesor asignado a una ficha de perfil existente
- Validación de invariantes POL-05 (mismo asesor) y estado terminal en `FichaPerfilAggregate.cambiarAsesorFicha()`
- Casos de uso: `CambiarAsesorFichaUseCase` orquesta validación de existencia de ficha y asesor, consulta del estado actual vía `EstadoFichaPerfilQueryOutputPort`, invocación del método de negocio del aggregate y persistencia
- Capas afectadas: `fichas:domain` (modificado `FichaPerfilAggregate`), `fichas:application` (nuevo use case + command + input port + excepciones), `fichas:infrastructure` (nuevo controller + DTO + migración Flyway)
- Migración Flyway V1.6: `CREATE INDEX idx_ficha_perfil_asesor ON ficha_perfil(asesor_ficha_id)`
- Catálogo de mensajes `shared:message` actualizado con constantes de HU-164
- Eventos de dominio: ninguno (CRUD interno sin consumidores)
- Tests: 18 casos (domain: 6, application: 5, infrastructure: 8) — cobertura domain 94%, application 88%, infrastructure 88% (cumple gate ≥75%)

**Tipo:** `feat`
**Rama:** `feature/HU-164-cambiar-asesor-ficha-perfil`
**Archivos a incluir:**
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/aggregate/FichaPerfilAggregate.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/command/CambiarAsesorFichaUseCase.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/command/model/CambiarAsesorFichaCommand.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/command/port/in/CambiarAsesorFichaInputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/exception/FichaPerfilNoEncontradaException.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/exception/AsesorFichaNoEncontradoException.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/asesorficha/query/port/out/AsesorFichaQueryOutputPort.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/command/adapter/in/web/CambiarAsesorFichaInputAdapter.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/command/adapter/in/web/dto/CambiarAsesorFichaRequestDTO.java`
- `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.6__agregar_indice_asesor_ficha_perfil.sql`
- `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java`
- `fichas/domain/src/test/java/com/arquisoft/fichas/domain/fichaperfil/aggregate/FichaPerfilAggregateTest.java`
- `fichas/application/src/test/java/com/arquisoft/fichas/application/fichaperfil/command/CambiarAsesorFichaUseCaseTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/fichaperfil/command/adapter/in/web/CambiarAsesorFichaInputAdapterTest.java`
- `.workspace/h-plan/PLAN-HU-164.md`
- `.workspace/validator/validator-HU-164.md`
