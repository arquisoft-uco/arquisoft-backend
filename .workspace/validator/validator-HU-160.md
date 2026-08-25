# Reporte de Validación — HU-160

## Metadata
- **ID Historia:** HU-160
- **Bounded Context:** `fichas`
- **Usa AggregateRoot:** Sí — `FichaPerfil` extiende `AggregateRoot` de `com.arquisoft.shared.domain`
- **Fecha de análisis:** 2026-05-04
- **Rama propuesta:** `feature/HU-160-consultar_fichas_perfil_coordinador`
- **Analizado por:** agente validator-analyze (04a-validator-analyze)
- **Skill arquisoft-context cargado:** ✅ (convenciones del proyecto aplicadas desde AGENTS.md)

---

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 11 | 11 | 0 | 100/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 42 | 41 | 1 | 98/100 |
| Nivel 3 — Compilación | 4 | 4 | 0 | 100/100 |
| Nivel 4 — Tests | 10 | 10 | 0 | 100/100 |
| **Total** | **67** | **66** | **1** | **98/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 1

---

## Estado Final

> ✅ APROBADO — Score: 98/100. Sin checks bloqueantes.
> Análisis listo para persistir en disco.

**Estado:** ✅ EJECUTADO
**Hash:** e8b486c
**Fecha de ejecución:** 2026-05-04

**Regla:** un solo check bloqueante fallado = estado RECHAZADO. No hay bloqueantes en esta HU.

---

## Errores Bloqueantes (deben corregirse antes del commit)

_Ninguno._

---

## Errores Menores (se pueden corregir en PR o tarea separada)

### [NIVEL 2.5] — `AsesorResumenDTO` sin `@JsonInclude(NON_NULL)`
- **Archivo:** `fichas/application/src/main/java/com/arquisoft/fichas/application/dto/AsesorResumenDTO.java`
- **Problema:** El DTO de respuesta anidado `AsesorResumenDTO` no tiene la anotación `@JsonInclude(JsonInclude.Include.NON_NULL)`. El plan la requiere en response DTOs. `FichaPerfilResumenDTO` sí la tiene, pero el DTO anidado no.
- **Referencia:** "response DTOs — `@JsonInclude(NON_NULL)`" — convenciones de DTOs en AGENTS.md / skill arquisoft-context.

---

## Observaciones Técnicas (informativas, no bloqueantes)

### Flyway — nombre de archivo con punto en lugar de doble guión bajo
- **Archivo:** `fichas/infrastructure/src/main/resources/db/migration/V1.0__crear_tablas_fichas_perfil.sql`
- **Observación:** El nombre usa `V1.0__` (con punto decimal en la versión). Flyway acepta este formato, pero la convención más común es `V1__` o `V1_0__`. No es un error funcional — Flyway lo procesa correctamente.

### `FichasDataSourceConfig` — dependencia explícita de Flyway en `fichasEntityManagerFactory`
- **Archivo:** `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/config/FichasDataSourceConfig.java` (línea 75)
- **Observación:** El bean `fichasEntityManagerFactory` recibe `@Qualifier("fichasFlyway") Flyway cichasFlyway` como parámetro para forzar el orden de inicialización (Flyway migra antes de que Hibernate valide el esquema con `hbm2ddl.auto=validate`). Es un patrón correcto y deliberado para multi-datasource. No es un error.

### `FichaPerfilJpaEntity` y `AsesorFichaJpaEntity` — método `toDomain()` en entidad JPA
- **Observación:** Ambas JPA entities tienen un método `toDomain()` que delega a `FichaPerfil.rebuild(...)` y `AsesorFicha.of(...)` respectivamente. El `FichaPerfilRepositoryAdapter` usa `FichaPerfilJpaEntity::toDomain` en lugar de llamar directamente a `FichaPerfil.rebuild(...)`. Esto es aceptable — el adapter sigue siendo el responsable de la conversión, y el método `toDomain()` en la JPA entity es solo un helper de traducción. El flujo DDD inviolable se respeta: JPA Entity → dominio vía `rebuild`, nunca JPA Entity → DTO.

### `FichaPerfilTest` — 3 tests en lugar de 2 sugeridos por el plan
- **Observación:** El plan sugería consolidar null/blank/empty en un solo test (`debeLanzarExcepcion_cuandoTituloProyectoEsNuloOVacioEnBuild`). El código de test tiene ese test consolidado **más** un test adicional `debeLanzarExcepcion_cuandoTituloProyectoSuperaLongitudMaxima`. Esto no es un anti-patrón — el test de longitud máxima verifica una invariante de negocio diferente (100 chars). El total de 10 tests sigue dentro del presupuesto orientativo.

---

## Tests

✅ Tests ejecutados según trazabilidad del plan (sección 13).

**Tests totales detectados:** 10 tests en 4 archivos
- `FichaPerfilTest.java`: 3 tests
- `ConsultarFichasPerfilUseCaseImplTest.java`: 2 tests
- `FichaPerfilRepositoryAdapterTest.java`: 2 tests
- `FichaPerfilControllerTest.java`: 4 tests (nota: el plan estimaba 4 en controller, el código tiene 4 — correcto)

**Presupuesto orientativo:** 10–15 tests (HU pequeña de consulta con arquitectura nueva)
**Estado de presupuesto:** ✅ dentro del rango (10 tests)

**Anti-patrones detectados (sección 2.11):**
- Ninguno detectado.
  - Anti-patrón 1 (getter/setter): no hay tests de getters aislados.
  - Anti-patrón 2 (validación Jakarta uno por uno): las validaciones se consolidan en un solo test con múltiples asserts.
  - Anti-patrón 3 (método privado): no hay tests de métodos privados.
  - Anti-patrón 4 (duplicados con asserts complementarios): no hay pares duplicados.
  - Anti-patrón 5 (delegación pura): `ConsultarFichasPerfilUseCaseImplTest` usa `verify()` pero también tiene asserts sobre el resultado — no es delegación pura.
  - Anti-patrón 6 (test de excepción simple): no existe `FichaPerfilCreadaEventTest` — correcto, el plan lo excluye explícitamente.
  - Anti-patrón 7 (equals/hashCode/toString de Lombok): no hay tests de este tipo.

**Tests que afirman 500 (sección 2.12):**
- Ninguno detectado. El test `debe400_cuandoParametrosPaginacionInvalidos` afirma correctamente `status().isBadRequest()` (400). No hay ningún test que afirme `isInternalServerError()`.

**Tests apropiados para Tipo de UC (sección 2.13):**
- Tipo de UC declarado: **Consulta**
- ✅ Tests apropiados. No hay verificaciones de `publicarEvento`, `obtenerEventosSinPublicar`, `clearUnPublishedEvents` ni `verify(eventPublisher)` en los tests de use case (`ConsultarFichasPerfilUseCaseImplTest`).
- Nota: `FichaPerfilTest.debeReconstruirSinEventos_cuandoRebuildEsInvocado` verifica que `getUnPublishedEvents()` está vacío tras `rebuild()`. Esto es correcto para un test de dominio que verifica la invariante de `rebuild` — no es un test de ciclo de eventos del use case de consulta.

---

## Datos para el commit

**Mensaje:** `feat(fichas): implementar consulta paginada de fichas de perfil para coordinador`
**Tipo:** `feat`
**Rama:** `feature/HU-160-consultar_fichas_perfil_coordinador`
**Archivos a incluir:**
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/FichaPerfil.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/AsesorFicha.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/event/FichaPerfilCreadaEvent.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/in/ConsultarFichasPerfilUseCase.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/FichaPerfilRepositoryPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/dto/AsesorResumenDTO.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/dto/FichaPerfilResumenDTO.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/usecase/ConsultarFichasPerfilUseCaseImpl.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichaPerfilController.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichasGlobalExceptionHandler.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/AsesorFichaJpaEntity.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilJpaEntity.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilJpaRepository.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilRepositoryAdapter.java`
- `fichas/infrastructure/src/main/resources/db/migration/V1.0__crear_tablas_fichas_perfil.sql`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/config/FichasDataSourceConfig.java`
- `fichas/domain/src/test/java/com/arquisoft/fichas/domain/model/FichaPerfilTest.java`
- `fichas/application/src/test/java/com/arquisoft/fichas/application/usecase/ConsultarFichasPerfilUseCaseImplTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/adapter/out/persistence/FichaPerfilRepositoryAdapterTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/adapter/in/web/FichaPerfilControllerTest.java`

---

## Próximos pasos

→ Para ejecutar el commit, invoca en una sesión nueva:
`"@commit ejecuta el commit de HU-160"`
