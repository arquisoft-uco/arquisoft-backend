# Reporte de Validación — HU-161 (v2)

## Metadata
- **ID Historia:** HU-161
- **Bounded Context:** fichas
- **Usa AggregateRoot:** No
- **Fecha de análisis:** 2026-07-02
- **Rama propuesta:** `feature/HU-161-asignar-estudiantes-ficha-perfil-v2`
- **Analizado por:** agente validator-analyze (04a-validator-analyze)
- **Skill arquisoft-context cargado:** ✅

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 17 | 17 | 0 | 100/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 45 | 45 | 0 | 100/100 |
| Nivel 3 — Compilación | 4 | 4 | 0 | 100/100 |
| Nivel 4 — Tests | 16 | 16 | 0 | 100/100 |
| **Total** | **82** | **82** | **0** | **100/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 0

## Estado Final

> ✅ APROBADO — Score: 100/100. Sin checks bloqueantes.

## Errores Bloqueantes
Ninguno detectado.

## Errores Menores
Ninguno detectado.

## Verificación adicional (agente primario)
- Build completo `./gradlew :fichas:domain:check :fichas:application:check :fichas:infrastructure:check` → BUILD SUCCESSFUL (test + checkstyle + cobertura ≥75%).
- La query derivada `countByFichaPerfilId` quedó cubierta con test H2 real en `EstudianteFichaPerfilJpaRepositoryTest`.
- Deuda preexistente v1 (helpers muertos en el repo test) eliminada.

## Tests
16 tests del delta: `AsignarEstudiantesFichaPerfilUseCaseTest` (6), `AsignarEstudiantesFichaPerfilInputAdapterTest` (9), + conteo en `EstudianteFichaPerfilCommandOutputAdapterTest` (1) y `EstudianteFichaPerfilJpaRepositoryTest` (1). Sin anti-patrones. Tipo UC: Escritura, sin eventos (coherente).

## Datos para el commit

**Mensaje:** feat(fichas): endpoint dedicado para asignar estudiantes a ficha existente (HU-161 v2)

**Cuerpo del mensaje:**
- Añade `POST /api/fichas-perfil/{fichaPerfilId}/estudiantes` para asignar estudiantes a fichas YA existentes (1 a 3 estudiantes)
- Patrón canónico: espejo de `AgregarItemFichaPerfilUseCase` (ruta con `@PathVariable` del ID padre)
- Corrige BUG de la v1: el límite valida `(existentes_en_BD + nuevos) ≤ 3` con conteo vía `contarPorFichaPerfilId(UUID)`
- `RegistrarFichaPerfilUseCase`: solo ajuste de convención (`!UtilObject.isNull(...)`), sin cambio de comportamiento
- `EstudianteFichaPerfilAggregate`: `setId()` con `UtilUUID.generateNewUUID()` (convención de valores autogenerados)
- Reutiliza todas las excepciones existentes de la v1
- Añade constante de log `EstudianteFichaPerfil.LOG_ASIGNADO`
- Client role: `fichas:estudiante-ficha-perfil:create`
- Tests: 16 nuevos — cobertura ≥75%, build en verde (test + checkstyle)

**Tipo:** `feat`
**Rama:** `feature/HU-161-asignar-estudiantes-ficha-perfil-v2`
**Archivos a incluir:**
- `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/model/AsignarEstudiantesFichaPerfilCommand.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/port/in/AsignarEstudiantesFichaPerfilInputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/AsignarEstudiantesFichaPerfilUseCase.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/dto/AsignarEstudiantesFichaPerfilRequestDTO.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/AsignarEstudiantesFichaPerfilInputAdapter.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudiantefichaperfil/port/out/EstudianteFichaPerfilOutputPort.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/persistence/EstudianteFichaPerfilJpaRepository.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/out/persistence/EstudianteFichaPerfilCommandOutputAdapter.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/command/RegistrarFichaPerfilUseCase.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudiantefichaperfil/aggregate/EstudianteFichaPerfilAggregate.java`
- `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java`
- `fichas/application/src/test/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/AsignarEstudiantesFichaPerfilUseCaseTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/AsignarEstudiantesFichaPerfilInputAdapterTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/out/persistence/EstudianteFichaPerfilCommandOutputAdapterTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/persistence/EstudianteFichaPerfilJpaRepositoryTest.java`
- `.workspace/h-plan/PLAN-HU-161.md`
- `.workspace/validator/validator-HU-161.md`

## Próximos pasos
→ Invocar @commit para HU-161.
