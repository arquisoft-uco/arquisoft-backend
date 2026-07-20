# Reporte de Validación — HU-033

## Metadata
- **ID Historia:** HU-033
- **Bounded Context:** fichas
- **Usa AggregateRoot:** No
- **Fecha de análisis:** 2026-07-04
- **Rama propuesta:** `feature/HU-033-modificar-item-ficha-perfil`
- **Analizado por:** agente validator-analyze (04a-validator-analyze)
- **Skill arquisoft-context cargado:** ✅

---

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|-------|--------|---------|----------|-------|
| Nivel 1 — Completitud del Plan | 14 | 14 | 0 | 100/100 |
| Nivel 2 — Convenciones DDD + Arquisoft | 87 | 87 | 0 | 100/100 |
| Nivel 3 — Compilación | 4 | 4 | 0 | 100/100 |
| Nivel 4 — Tests | 7 | 7 | 0 | 100/100 |
| **Total** | **112** | **112** | **0** | **100/100** |

**Checks bloqueantes fallados:** 0
**Checks menores fallados:** 0

---

## Estado Final

> ✅ APROBADO — Score: 100/100. Sin checks bloqueantes.
> Análisis listo para persistir en disco.

**Estado:** ✅ EJECUTADO  
**Hash:** 5afa926  
**Fecha de ejecución:** 2026-07-06

---

## Errores Bloqueantes (deben corregirse antes del commit)

Ninguno detectado.

---

## Errores Menores (se pueden corregir en PR o tarea separada)

Ninguno detectado.

---

## Tests

✅ Tests ejecutados según trazabilidad del plan (sección 14).

**Tests totales detectados:** 19 tests en 5 archivos
- `ItemFichaPerfilAggregateTest`: 11 tests (4 nuevos de `modificarContenido`, 7 existentes de `crear`)
- `ModificarItemFichaPerfilUseCaseTest`: 6 tests
- `FichaPerfilQueryOutputAdapterTest`: 4 tests (2 nuevos de `esEstudiantePropietario`, 2 existentes)
- `ItemFichaPerfilCommandOutputAdapterTest`: 9 tests (3 nuevos: `existsById` x2 + `debeGuardarCambios_cuandoModificarContenido`, 6 existentes)
- `ModificarItemFichaPerfilInputAdapterTest`: 6 tests

**Presupuesto orientativo:** 18-22 tests (HU pequeña)
**Estado de presupuesto:** ✅ dentro del rango (19 tests)

**Anti-patrones detectados (sección 2.11):**
- Ninguno detectado

**Tests que afirman 500 (sección 2.12):**
- Ninguno detectado

**Tests apropiados para Tipo de UC (sección 2.13):**
- Tipo de UC declarado: Escritura
- ✅ Tests apropiados — los tests de aggregate NO incluyen ciclo de eventos del Aggregate Root (correcto porque la entidad NO extiende `AggregateRoot`), los tests de use case NO verifican `eventPublisher.publish(...)` (correcto porque la HU no emite eventos), los tests validan solo el método de negocio `modificarContenido(...)` y el flujo de orquestación del use case.

---

## Datos para el commit

**Mensaje:** feat(fichas): modificar contenido de ítem de ficha de perfil

**Cuerpo del mensaje:**
- Implementado caso de uso `ModificarItemFichaPerfilUseCase` que permite a un estudiante modificar el contenido de un ítem de su propia ficha de perfil
- Endpoint REST `PUT /fichas-perfil/{itemId}/items` con autorización `fichas:item-ficha-perfil:update` (kebab-case)
- Método de negocio `modificarContenido(String)` agregado al aggregate `ItemFichaPerfilAggregate` con validación Notification Pattern (sin extensión de `AggregateRoot` — CRUD sin eventos)
- Puerto de salida write `ItemFichaPerfilOutputPort` extendido con `existsById(UUID)` y `buscarPorId(UUID)`
- Puerto de salida read `FichaPerfilQueryOutputPort` extendido con `esEstudiantePropietario(UUID, UUID)` para validar propiedad cross-aggregate
- Excepción `ItemNoEncontradoException` (extiende `ApplicationException` → 400) agregada en `application/itemfichaperfil/exception/`
- Catálogo de mensajes `FichasMessages.ItemFichaPerfil` extendido con constantes `ITEM_NO_ENCONTRADO`, `ITEM_NO_ENCONTRADO_MSG`, `LOG_MODIFICADO`
- Tests unitarios (19 totales: domain 4, application 6, infrastructure 9) con cobertura dentro del presupuesto orientativo (18-22)
- Validaciones: ítem existe, estudiante es propietario, contenido válido (obligatorio, máximo 7000 caracteres, trim aplicado)
- Campos `tipoItem` y `fichaPerfilId` inmutables tras creación (solo se modifica `contenido`)
- Sin migración Flyway (tabla `item` ya existe en V1.3)
- Sin eventos RabbitMQ (CRUD interno sin consumidores)

**Tipo:** `feat`
**Rama:** `feature/HU-033-modificar-item-ficha-perfil`
**Archivos a incluir:**
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregate.java`
- `fichas/domain/src/main/java/com/arquisoft/fichas/domain/itemfichaperfil/port/out/ItemFichaPerfilOutputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/query/port/out/FichaPerfilQueryOutputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/exception/ItemNoEncontradoException.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/model/ModificarItemFichaPerfilCommand.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/port/in/ModificarItemFichaPerfilInputPort.java`
- `fichas/application/src/main/java/com/arquisoft/fichas/application/itemfichaperfil/command/ModificarItemFichaPerfilUseCase.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/query/adapter/out/persistence/FichaPerfilQueryOutputAdapter.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/out/persistence/ItemFichaPerfilCommandOutputAdapter.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/dto/ModificarItemFichaPerfilRequestDTO.java`
- `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/ModificarItemFichaPerfilInputAdapter.java`
- `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java`
- `fichas/domain/src/test/java/com/arquisoft/fichas/domain/itemfichaperfil/aggregate/ItemFichaPerfilAggregateTest.java`
- `fichas/application/src/test/java/com/arquisoft/fichas/application/itemfichaperfil/command/ModificarItemFichaPerfilUseCaseTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/fichaperfil/query/adapter/out/persistence/FichaPerfilQueryOutputAdapterTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/out/persistence/ItemFichaPerfilCommandOutputAdapterTest.java`
- `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/itemfichaperfil/command/adapter/in/web/ModificarItemFichaPerfilInputAdapterTest.java`
- `.workspace/h-plan/PLAN-HU-033.md`
- `.workspace/validator/validator-HU-033.md`

---

# Revalidación post-auditoría — 2026-07-09

## Motivo

La auditoría batch de arquitectura (DDD / Clean / CQRS) sobre los 13 planes detectó que el
**criterio de aceptación #6 del PLAN-HU-033 nunca se implementó**. La validación original
(score 100/100, 2026-07-06) no lo detectó porque contrastó el código contra la *sección 7 del
plan* (flujo del use case), que ya omitía la regla — no contra la *sección 2* (criterios de
aceptación). Es un falso negativo del check de completitud.

## Hallazgos corregidos

| # | Hallazgo | Severidad | Estado |
|---|----------|-----------|--------|
| 1 | Criterio #6 (ficha en estado modificable) sin implementar en `ModificarItemFichaPerfilUseCase` | 🔴 Bloqueante | ✅ Corregido |
| 2 | Criterio #3 documentaba 400 para no-propietario; tras introducir `AuthorizationException` el comportamiento correcto es 403 | 🟡 Menor | ✅ Corregido |
| 3 | El plan proponía la regla de estado con excepción 400 (semántica de aplicación) siendo una invariante de negocio | 🟡 Menor | ✅ Corregido → 422 |

## Verificación de Tell, Don't Ask

Check explícito sobre la regla de negocio validada en dominio:

- ✅ `EstadoFicha.esTerminal()` / `permiteModificacion()` — el enum responde por sí mismo; el
  llamador no compara constantes. Espejo de `EstadoEvaluacion.esTerminal()` (HU-191).
- ✅ `ItemFichaPerfilAggregate.modificarContenido(contenido, estadoFichaActual)` — el aggregate
  **decide**; acumula `ESTADO_FICHA_NO_MODIFICABLE` en el `ValidationResult` y **no** aplica el
  cambio si el estado es terminal.
- ✅ `ModificarItemFichaPerfilUseCase` — recupera el estado vía
  `EstadoFichaPerfilOutputPort.obtenerEstadoActual(...)` y **lo entrega** al aggregate. No hay
  ningún `if` sobre el estado en la capa de aplicación. **Sin fuga de lógica.**
- ✅ Separación de excepciones: invariante de estado → `DomainValidationException` (422);
  ficha sin trazabilidad de estado → `FichaPerfilNoEncontradaException` (400); propiedad del
  recurso → `ItemFichaNoPropiaException` (403).

## Cobertura de tests añadida

| Capa | Test | Escenario |
|---|---|---|
| domain | `debeLanzarExcepcion_cuandoEstadoFichaEsTerminal` | `@ParameterizedTest` × 3 estados terminales → 422, contenido intacto |
| domain | `debeModificarContenido_cuandoEstadoFichaNoEsTerminal` | `@ParameterizedTest` × 3 estados no terminales → OK |
| domain | `debeLanzarExcepcion_cuandoEstadoFichaEsNulo` | `ESTADO_FICHA_REQUERIDO` |
| application | `debePropagarDomainValidation_cuandoFichaEnEstadoTerminal` | propaga sin capturar; nunca llama `guardar` |
| application | `debeLanzarFichaPerfilNoEncontrada_cuandoFichaSinEstadoRegistrado` | `Optional.empty()` → 400 |
| infrastructure | `debeRetornarEstadoMasReciente_cuandoFichaTieneVariosEstados` | orden por `fechaActualizacion DESC` |
| infrastructure | `debeRetornarVacio_cuandoFichaNoTieneEstados` | `Optional.empty()` |
| infrastructure | `debe422_cuandoFichaEnEstadoTerminal` | controller → 422 + `fieldErrors` |
| infrastructure | `debe403_cuandoEstudianteNoEsPropietario` | renombrado desde `debe400_...` |

## Resultado

| Verificación | Resultado |
|---|---|
| `:fichas:domain:test` | ✅ BUILD SUCCESSFUL |
| `:fichas:application:test` | ✅ BUILD SUCCESSFUL |
| `:fichas:infrastructure:test` | ✅ BUILD SUCCESSFUL |
| `checkstyleMain` / `checkstyleTest` (domain, application, infrastructure, shared:message) | ✅ BUILD SUCCESSFUL |

> ✅ **APROBADO** — criterio #6 implementado sin fuga de lógica, con la regla de negocio
> resuelta en el dominio conforme a Tell, Don't Ask.
