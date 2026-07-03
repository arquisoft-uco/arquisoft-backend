# Reporte de Validación — HU-162

## Metadata
- **ID Historia:** HU-162
- **Bounded Context:** fichas
- **Usa AggregateRoot:** No
- **Fecha de análisis:** 2026-07-03
- **Rama propuesta:** `feature/HU-162-remover-estudiante-ficha-perfil`

## Score final (tras correcciones)
| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 18 | 18 | 0 | 100/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 47 | 47 | 0 | 100/100 |
| Nivel 3 — Compilación | 4 | 4 | 0 | 100/100 |
| Nivel 4 — Tests | 10 | 10 | 0 | 100/100 |
| **Total** | **79** | **79** | **0** | **100/100** |

## Estado Final
> ✅ APROBADO — Score: 100/100. Sin checks bloqueantes ni menores pendientes.

## Correcciones aplicadas tras el análisis inicial (score original 95/100)
1. **Tests duplicados consolidados** (anti-patrón 4): en `RemoverEstudianteFichaPerfilInputAdapterTest.java` se eliminaron `debeInvocarInputPort_cuandoPeticionValida`, `debeConstruirCommand_cuandoPeticionValida` y `debeRetornarNoContent_cuandoEliminacionExitosa` — su cobertura quedó consolidada en `debe204_cuandoPeticionValida`.
2. **Test de delegación pura eliminado** (anti-patrón 5): se eliminó `debeEliminar_cuandoRelacionExiste` de `EstudianteFichaPerfilCommandOutputAdapterTest.java` (ya cubierto indirectamente por el test de integración del repositorio JPA).
3. **Verificación débil corregida**: `debe204_cuandoPeticionValida` ahora usa `ArgumentCaptor<RemoverEstudianteFichaPerfilCommand>` para capturar y afirmar que `fichaPerfilId` y `estudianteId` del comando coinciden exactamente con los `@PathVariable` de la request, cerrando el gap de un posible bug de UUIDs invertidos.

## Tests (estado final)
**Total tests HU-162:** 14 (bajó de 18 tras consolidar/eliminar duplicados y triviales)
- Application: 5 tests (`RemoverEstudianteFichaPerfilUseCaseTest`)
- Infrastructure: 9 tests
  - `RemoverEstudianteFichaPerfilInputAdapterTest`: 8 tests
  - `EstudianteFichaPerfilJpaRepositoryTest`: 1 test nuevo (`debeEliminar_cuandoRelacionExisteEnBD`)
  - `EstudianteFichaPerfilCommandOutputAdapterTest`: 0 tests nuevos (el de delegación pura fue eliminado)

**Verificación post-corrección:** `./gradlew :fichas:application:check :fichas:infrastructure:check` → BUILD SUCCESSFUL. Cobertura JaCoCo: application 85%, infrastructure 87% (ambos ≥75% requerido). Checkstyle: sin violaciones.

**Presupuesto de tests:** 14 tests, dentro del rango orientativo 15-25 para HU pequeña (ligeramente por debajo, justificado por la eliminación de tests redundantes).

**Anti-patrones:** Ninguno pendiente — los 3 detectados en el análisis inicial fueron corregidos.

## Datos para el commit
**Mensaje:** feat(fichas): remover estudiante de ficha perfil

**Cuerpo del mensaje:**
- Endpoint DELETE /fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId} con autorización vía client role fichas:estudiante-ficha-perfil:delete
- Caso de uso RemoverEstudianteFichaPerfilUseCase con validaciones (ficha existe, estudiante existe, relación existe) antes de eliminar
- Eliminación física permanente (DELETE SQL) sin emisión de eventos de dominio (CRUD interno sin consumidores)
- Capas afectadas: application (Command, InputPort, UseCase, Exception), domain (OutputPort modificado con método eliminar), infrastructure (InputAdapter REST, CommandOutputAdapter, JpaRepository modificado con método derivado deleteByFichaPerfilIdAndEstudianteId)
- Catálogo de mensajes modificado (FichasMessages.EstudianteFichaPerfil): constantes nuevas para código de error, mensaje y log
- Tests: 14 totales (5 application + 9 infrastructure). Cobertura: application 85%, infrastructure 87% — cumple gate del 75%. Checkstyle: verde

**Tipo:** `feat`
**Rama:** `feature/HU-162-remover-estudiante-ficha-perfil`

**Archivos a incluir:**
- fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/model/RemoverEstudianteFichaPerfilCommand.java
- fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/port/in/RemoverEstudianteFichaPerfilInputPort.java
- fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/RemoverEstudianteFichaPerfilUseCase.java
- fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/exception/EstudianteFichaPerfilNoEncontradoException.java
- fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudiantefichaperfil/port/out/EstudianteFichaPerfilOutputPort.java
- fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/RemoverEstudianteFichaPerfilInputAdapter.java
- fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/out/persistence/EstudianteFichaPerfilCommandOutputAdapter.java
- fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/persistence/EstudianteFichaPerfilJpaRepository.java
- shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java
- fichas/application/src/test/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/RemoverEstudianteFichaPerfilUseCaseTest.java
- fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/RemoverEstudianteFichaPerfilInputAdapterTest.java
- fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/out/persistence/EstudianteFichaPerfilCommandOutputAdapterTest.java
- fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/persistence/EstudianteFichaPerfilJpaRepositoryTest.java
- .workspace/h-plan/PLAN-HU-162.md
- .workspace/validator/validator-HU-162.md
