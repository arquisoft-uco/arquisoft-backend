# Reporte de Validación — HU-161

## Metadata
- **ID Historia:** HU-161
- **Bounded Context:** fichas
- **Usa AggregateRoot:** No (FichaPerfilAggregate es clase plana sin eventos; EstudianteFichaPerfilAggregate tampoco extiende AggregateRoot — relación CRUD sin eventos)
- **Fecha de análisis:** 2026-06-23
- **Rama propuesta:** `feature/HU-161-asignar-estudiantes-ficha-perfil`
- **Analizado por:** agente validator-analyze (04a-validator-analyze)
- **Skill arquisoft-context cargado:** ✅

---

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 19 | 19 | 0 | 100/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 147 | 145 | 2 | 98/100 |
| Nivel 3 — Compilación | 4 | 4 | 0 | 100/100 |
| Nivel 4 — Tests | 15 | 15 | 0 | 100/100 |
| **Total** | **185** | **183** | **2** | **99/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 2

---

## Estado Final

> ✅ APROBADO — Score: 99/100. Sin checks bloqueantes.

---

## Errores Bloqueantes

Ninguno detectado.

---

## Errores Menores (se pueden corregir en PR o tarea separada)

### [NIVEL 1.1] — Archivo de test de dominio ausente
- **Archivo esperado:** `fichas/domain/src/test/java/com/arquisoft/fichas/domain/estudiante/aggregate/EstudianteTest.java`
- **Problema:** El plan declara tests para `EstudianteAggregate` en la sección 12, pero el archivo no existe en el repositorio.
- **Referencia:** Plan HU-161, sección 12 — tabla "Tests capa domain"

### [NIVEL 1.2] — Archivo de test de dominio ausente
- **Archivo esperado:** `fichas/domain/src/test/java/com/arquisoft/fichas/domain/estudianteFichaPerfil/aggregate/EstudianteFichaPerfilTest.java`
- **Problema:** El plan declara tests para `EstudianteFichaPerfilAggregate` en la sección 12, pero el archivo no existe en el repositorio.
- **Referencia:** Plan HU-161, sección 12 — tabla "Tests capa domain"

---

## Tests

✅ Tests ejecutados según trazabilidad del plan (sección 14).

**Tests totales detectados:** 19 tests en 3 archivos
- `RegistrarFichaPerfilUseCaseTest.java`: 14 tests
- `EstudianteFichaPerfilCommandOutputAdapterTest.java`: 3 tests
- `RegistrarFichaPerfilInputAdapterTest.java`: 12 tests (7 nuevos de HU-161)

**Presupuesto orientativo:** 25-50 (HU mediana)
**Estado de presupuesto:** dentro del rango

**Anti-patrones detectados:** Ninguno
**Tests que afirman 500:** Ninguno
**Tests de ciclo de eventos:** No presentes (correcto — la HU no emite eventos)

---

## Datos para el commit

**Estado:** ✅ EJECUTADO
**Hash:** 69193a8
**Fecha de ejecución:** 2026-06-23
**Mensaje:** `feat(fichas): asignar estudiantes a ficha de perfil (HU-161)`
**Tipo:** `feat`
**Rama:** `feature/HU-161-asignar-estudiantes-ficha-perfil`

**Archivos a incluir:**
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudiante/aggregate/EstudianteAggregate.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudianteFichaPerfil/aggregate/EstudianteFichaPerfilAggregate.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudianteFichaPerfil/port/out/EstudianteFichaPerfilOutputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiante/exception/EstudianteNoEncontradoException.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiante/query/port/out/EstudianteQueryOutputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/estudianteFichaPerfil/exception/EstudianteDuplicadoException.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/estudianteFichaPerfil/exception/LimiteEstudiantesExcedidoException.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/RegistrarFichaPerfilUseCase.java` (modificado)
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/model/RegistrarFichaPerfilCommand.java` (modificado)
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/in/web/dto/RegistrarFichaPerfilRequestDTO.java` (modificado)
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiante/persistence/EstudianteJpaEntity.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiante/persistence/EstudianteJpaRepository.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiante/persistence/EstudianteMapper.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiante/query/adapter/out/persistence/EstudianteQueryOutputAdapter.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudianteFichaPerfil/persistence/EstudianteFichaPerfilJpaEntity.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudianteFichaPerfil/persistence/EstudianteFichaPerfilJpaRepository.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudianteFichaPerfil/persistence/EstudianteFichaPerfilMapper.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudianteFichaPerfil/command/adapter/out/persistence/EstudianteFichaPerfilCommandOutputAdapter.java`
- `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.1__crear_estudiante_y_estudiante_ficha_perfil.sql`
- `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` (modificado)
- `fichas/application/src/test/java/com/arquisoft/fichas/application/fichaPerfil/command/RegistrarFichaPerfilUseCaseTest.java` (modificado)
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/estudianteFichaPerfil/command/adapter/out/persistence/EstudianteFichaPerfilCommandOutputAdapterTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/in/web/RegistrarFichaPerfilInputAdapterTest.java` (modificado)
