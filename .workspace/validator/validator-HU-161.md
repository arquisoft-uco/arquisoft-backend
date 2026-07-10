# Reporte de Validación — HU-161 v2-fix

## Metadata
- **ID Historia:** HU-161 v2-fix
- **Bounded Context:** fichas
- **Usa AggregateRoot:** No — `FichaPerfilAggregate` es una `final class` plana, NO extiende `AggregateRoot` (CRUD sin eventos)
- **Fecha de análisis:** 2026-07-09
- **Rama propuesta:** `feature/HU-161-asignar-estudiantes-ficha-perfil-v2-fix`
- **Analizado por:** agente validator-analyze (04a-validator-analyze)
- **Skill arquisoft-context cargado:** ✅

---

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 25 | 25 | 0 | 100/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 68 | 68 | 0 | 100/100 |
| Nivel 3 — Compilación | 4 | 4 | 0 | 100/100 |
| Nivel 4 — Tests | 0 | 0 | 0 | ⏳ N/A |
| **Total** | **97** | **97** | **0** | **100/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 0

---

## Estado Final

> ✅ APROBADO — Score: 100/100. Sin checks bloqueantes.

---

## Errores Bloqueantes

**Ninguno detectado.**

---

## Errores Menores

**Ninguno detectado.**

---

## Tests

Los tests pasaron con `./gradlew fichas:domain:test fichas:application:test fichas:infrastructure:test` antes del análisis:
- domain: 7 tests (3 nuevos para factory bulk `crear(UUID, List, long)`)
- application: 20 tests (2 ajustados: excepción LimiteExcedida → DomainValidationException)
- infrastructure: 11 tests (2 ajustados: 201→204, 1 nuevo: 422)

---

## Datos para el commit

**Mensaje:** `fix(fichas): mover validación de límite de estudiantes a domain layer`

**Cuerpo del mensaje:**
- CORRIGE fuga de lógica de dominio: la validación del límite "≤ 3 estudiantes por ficha" se movió de `AsignarEstudiantesFichaPerfilUseCase` y `RegistrarFichaPerfilUseCase` (application layer) al aggregate `EstudianteFichaPerfilAggregate` (domain layer).
- La invariante de negocio ahora se evalúa dentro del factory bulk `EstudianteFichaPerfilAggregate.crear(fichaPerfilId, estudiantesIds, cantidadExistentes)` usando `ValidationResult` de `shared:domain`.
- Si se viola el límite, lanza `DomainValidationException` (422 Unprocessable Entity), no `ApplicationException` (400 Bad Request).
- `LimiteEstudiantesExcedidoException` eliminada — reemplazada por `DomainValidationException` con `errorCode = LIMITE_ESTUDIANTES_EXCEDIDO`.
- Factory individual `crear(UUID, UUID)` marcado `private` — único entry point público es el bulk atómico.
- `AsignarEstudiantesFichaPerfilInputAdapter` retorna `204 No Content` (antes `201 Created`).
- Capas afectadas: `fichas:domain`, `fichas:application` (2 use cases), `fichas:infrastructure` (adapter), `shared:message`.

**Tipo:** `fix`
**Rama:** `feature/HU-161-asignar-estudiantes-ficha-perfil-v2-fix`

**Archivos a incluir en el commit:**
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudiantefichaperfil/aggregate/EstudianteFichaPerfilAggregate.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/AsignarEstudiantesFichaPerfilUseCase.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/command/RegistrarFichaPerfilUseCase.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/AsignarEstudiantesFichaPerfilInputAdapter.java`
- `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java`
- Tests ajustados/nuevos en las tres capas
- `.workspace/h-plan/PLAN-HU-161.md`
- `.workspace/validator/validator-HU-161.md`
