# Reporte de Validación — HU-260

## Metadata
- **ID Historia:** HU-260
- **Bounded Context:** `seguridad`
- **Usa AggregateRoot:** No (correcto — `seguridad` es excepción documentada en el skill)
- **Tipo de Use Case:** Consulta
- **Fecha de análisis:** 2026-04-28
- **Rama propuesta:** `feature/HU-260-consultar_usuarios_administrador`
- **Analizado por:** agente validator-analyze (04a-validator-analyze)
- **Skill arquisoft-context cargado:** ✅

---

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 11 | 10 | 1 | 91/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 40 | 38 | 2 | 95/100 |
| Nivel 3 — Compilación | 4 | 4 | 0 | 100/100 |
| Nivel 4 — Tests | 13 | 12 | 1 | 92/100 |
| **Total** | **68** | **64** | **4** | **94/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 4

---

## Estado Final

> ✅ APROBADO — Score: 94/100. Sin checks bloqueantes.
> Análisis listo para persistir en disco.

---

## Errores Bloqueantes

**Ninguno.** La implementación no tiene errores bloqueantes.

---

## Errores Menores (pueden corregirse en PR o tarea separada)

### [NIVEL 1] — Checklist sección 12 tiene items pendientes desactualizados

- **Archivo:** `.workspace/h-plan/PLAN-HU-260.md` — sección 12 "⏳ Pendiente"
- **Problema:** Los tres items de la sección "Pendiente" (compilar, ejecutar tests, commit) siguen marcados como `[ ]` aunque la deuda técnica fue resuelta por el implementador el 2026-04-28 y los tests pasan. Solo el commit aún es correcto.
- **Referencia:** sección 13 del plan (Trazabilidad) indica Desarrollo ✅ Completado 2026-04-28 — hay inconsistencia con el checklist.
- **Estado:** ✅ Corregido el 2026-04-28 antes de persistir este reporte.

---

### [NIVEL 2.2 — DDD] — Controller inyecta tipo concreto en lugar del puerto de entrada

- **Archivo:** `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/in/web/UsuarioController.java` — línea 44
- **Problema:** `private final ConsultarUsuariosUseCaseImpl consultarUsuariosUseCase;` inyecta la implementación concreta en lugar de la interfaz `ConsultarUsuariosUseCase`. Viola la regla "inyectar interfaces, nunca implementaciones".
- **Referencia:** skill `arquisoft-context` sección "Inyección de dependencias": *"Inyectar interfaces (puertos), nunca implementaciones"*. Y la plantilla canónica del Controller inyecta `CrearFichaUseCase` (interfaz), no `CrearFichaUseCaseImpl`.
- **Contexto de decisión:** La interfaz `ConsultarUsuariosUseCase` define `buscarUsuarios` y `contarUsuarios` con tipos de dominio como parámetros. El método `ejecutar(UsuarioFiltroDTO)` que el controller necesita no puede formar parte del puerto sin violar la dirección de dependencias (`domain` no puede importar DTOs de `application`). La violación es conocida y aceptada intencionalmente para esta HU. Se recomienda revisar el diseño del puerto en una HU técnica futura.

---

### [NIVEL 2.2 — DDD] — `ejecutar()` no está en la interfaz `ConsultarUsuariosUseCase`

- **Archivo:** `seguridad/application/src/main/java/com/arquisoft/seguridad/application/usecase/ConsultarUsuariosUseCaseImpl.java` — línea 134
- **Problema:** El método `ejecutar(UsuarioFiltroDTO)` que el controller invoca es un método público de la clase concreta, **no** está declarado en la interfaz `ConsultarUsuariosUseCase`. La interfaz solo define `buscarUsuarios` y `contarUsuarios`. Esto significa que el controller está acoplado a la implementación concreta (de ahí el punto anterior). El puerto debería exponer `ejecutar` como contrato o el controller debería invocar directamente `buscarUsuarios` + `contarUsuarios`.
- **Referencia:** skill `arquisoft-context` sección "Puertos de entrada": el puerto define el contrato que el adapter driving (controller) invoca.
- **Contexto de decisión:** misma raíz que el punto anterior. Mover `ejecutar()` a la interfaz requeriría que `domain/port/in/` importe `UsuarioFiltroDTO` de `application`, violando la dirección de dependencias. Deuda técnica de diseño aceptada.

---

### [NIVEL 4 — Tests] — Tests de controller simulan 401/403 via excepciones del use case, no via Spring Security

- **Archivo:** `seguridad/infrastructure/src/test/java/com/arquisoft/seguridad/infrastructure/adapter/in/web/UsuarioControllerTest.java` — líneas 136-160
- **Problema:** Los tests `debeRetornar403` y `debeRetornar401` hacen que el use case lance `AccessDeniedException` y `AuthenticationException` respectivamente, en lugar de que la capa de seguridad de Spring las intercepte antes de llegar al use case. En un escenario real, Spring Security rechaza la request antes de llamar al controller, por lo que estos tests validan el comportamiento del `GlobalExceptionHandler` más que la seguridad real. No es bloqueante porque los tests pasan y el comportamiento HTTP es correcto, pero la semántica es imprecisa.
- **Referencia:** skill `arquisoft-context` sección "Seguridad": `@PreAuthorize` se evalúa antes de ejecutar el método del controller.
- **Contexto de decisión:** Spring Boot 4.x eliminó `@WebMvcTest` y las test-slices de Spring MVC. Sin este mecanismo no es posible levantar el filtro de seguridad en un test de unidad del controller. Es la mejor aproximación posible con el stack actual. La verificación real de 401/403 queda como deuda para tests de integración end-to-end con `@SpringBootTest`.

---

## Tests

✅ Tests ejecutados según trazabilidad del plan (sección 13).

**Tests totales detectados:** 16 tests en 5 archivos
- `UsuarioTest.java` — 1 test
- `EstadoUsuarioTest.java` — 1 test
- `ConsultarUsuariosUseCaseImplTest.java` — 4 tests
- `UsuarioRepositoryAdapterTest.java` — 5 tests
- `UsuarioControllerTest.java` — 5 tests

**Presupuesto orientativo:** 15-25 tests (HU mediana, 1 endpoint con filtros y paginación)
**Estado de presupuesto:** ✅ Dentro del rango (16 tests)

**Anti-patrones detectados (sección 2.11):**
- Ninguno detectado. Los 7 anti-patrones fueron evitados correctamente:
  - Sin tests de getters Lombok
  - Sin tests de validaciones Jakarta uno por uno
  - Sin tests de métodos privados (`convertirEstado`, `convertirRol` son métodos públicos del use case, no privados)
  - Sin tests duplicados con asserts complementarios (todos consolidados)
  - Sin tests de delegación pura
  - Sin tests propios de `ParametroFiltroInvalidoException` (excepción simple)
  - Sin tests de equals/hashCode/toString de Lombok

**Tests que afirman 500 (sección 2.12):**
- Ninguno detectado. El test `debeRetornar400_cuandoFiltroInvalido` correctamente afirma `status().isBadRequest()` gracias al `@ExceptionHandler` registrado.

**Tests apropiados para Tipo de UC (sección 2.13):**
- Tipo de UC declarado: **Consulta**
- ✅ Tests apropiados: ningún test incluye `publishEvent`, `getUnPublishedEvents`, `clearUnPublishedEvents` ni `verify(eventPublisher)`. Correcto para HU de solo lectura.

---

## Datos para el commit

**Mensaje:** `feat(seguridad): consultar usuarios con filtros y paginacion para administrador`
**Tipo:** `feat`
**Rama:** `feature/HU-260-consultar_usuarios_administrador`
**Archivos a incluir:**
- `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/Usuario.java`
- `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/model/EstadoUsuario.java`
- `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/port/in/ConsultarUsuariosUseCase.java`
- `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/port/out/UsuarioRepositoryPort.java`
- `seguridad/domain/src/main/java/com/arquisoft/seguridad/domain/exception/ParametroFiltroInvalidoException.java`
- `seguridad/application/src/main/java/com/arquisoft/seguridad/application/dto/UsuarioFiltroDTO.java`
- `seguridad/application/src/main/java/com/arquisoft/seguridad/application/dto/UsuarioResponseDTO.java`
- `seguridad/application/src/main/java/com/arquisoft/seguridad/application/dto/PaginaResponseDTO.java`
- `seguridad/application/src/main/java/com/arquisoft/seguridad/application/usecase/ConsultarUsuariosUseCaseImpl.java`
- `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/in/web/UsuarioController.java`
- `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/in/web/GlobalExceptionHandler.java`
- `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioJpaEntity.java`
- `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/EstadoUsuarioJpaEntity.java`
- `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/RolJpaEntity.java`
- `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioJpaRepository.java`
- `seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioRepositoryAdapter.java`
- `seguridad/infrastructure/src/main/resources/db/migration/V1__crear_schema_usuarios.sql`
- `seguridad/infrastructure/src/main/resources/db/migration/V2__normalizar_estado_usuario.sql`
- `seguridad/infrastructure/build.gradle`
- `seguridad/domain/src/test/java/com/arquisoft/seguridad/domain/model/UsuarioTest.java`
- `seguridad/domain/src/test/java/com/arquisoft/seguridad/domain/model/EstadoUsuarioTest.java`
- `seguridad/application/src/test/java/com/arquisoft/seguridad/application/usecase/ConsultarUsuariosUseCaseImplTest.java`
- `seguridad/infrastructure/src/test/java/com/arquisoft/seguridad/infrastructure/adapter/out/persistence/UsuarioRepositoryAdapterTest.java`
- `seguridad/infrastructure/src/test/java/com/arquisoft/seguridad/infrastructure/adapter/in/web/UsuarioControllerTest.java`

---

## Próximos pasos

**Estado:** ✅ EJECUTADO
**Hash:** 12b8924
**Fecha de ejecución:** 2026-04-28

→ Abrir Pull Request hacia `develop` usando `.github/PULL_REQUEST_TEMPLATE.md`
→ Requiere 1 aprobación según CONTRIBUTING.md
