# 📋 Reporte de Validación — HU-35 (Modificar Ficha Perfil)

**Estado: APROBADO ✅**

---

## 1. Compilación

| Verificación | Resultado |
|---|---|
| `./gradlew build -x test` | ✅ Exitoso (todos los módulos compilan) |
| JaCoCo `implicit_dependency` | ⚠️ Error preexistente en `fichas:application` — no relacionado con esta HU |

---

## 2. Tests

| Capa | Archivo | Tests | Resultado |
|---|---|---|---|
| Domain | `FichaPerfilAggregateTest` | 8 | ✅ Pass |
| Application | `ModificarFichaPerfilUseCaseTest` | 6 | ✅ Pass |
| Infrastructure | `ModificarFichaPerfilInputAdapterTest` | 7 | ✅ Pass |
| **Total** | | **21** | **Todos pasan** |

---

## 3. Plan vs Implementación

### Archivos creados/modificados

| Archivo (plan §5) | Estado | Observación |
|---|---|---|
| `ModificarFichaPerfilCommand.java` | ✅ | Record con campos correctos |
| `ModificarFichaPerfilInputPort.java` | ✅ | `extends VoidInputPort<ModificarFichaPerfilCommand>` |
| `ModificarFichaPerfilUseCase.java` | ✅ | Orquestación idéntica al plan |
| `FichaNoEncontradaException.java` | ✅ | `final class extends ApplicationException` |
| `FichaNoPropietarioException.java` | ✅ | `final class extends ApplicationException` |
| `ModificarFichaPerfilRequestDTO.java` | ✅ | `@Data @Builder` + `@Valid` |
| `ModificarFichaPerfilInputAdapter.java` | ✅ | `PATCH /fichas-perfil/{id}`, `204 No Content` |
| `FichasMessages.java` (modif.) | ✅ | 3 constantes nuevas en secciones correctas |

### Cumplimiento de requisitos funcionales

| Requisito (plan §3) | Verificación |
|---|---|
| `PATCH /fichas-perfil/{id}` con autoridad `fichas:ficha-perfil:update` | ✅ |
| `@AuthenticationPrincipal Jwt jwt → jwt.getSubject()` | ✅ |
| Validación autoría vía `estudianteFichaPerfilOutputPort.existePorFichaYEstudiante()` | ✅ |
| `FichaNoEncontradaException` si no existe | ✅ |
| `FichaTituloDuplicadoException` si duplicado (solo si titulo cambió) | ✅ |
| Delegación a `aggregate.actualizarTitulo()` | ✅ |
| `204 No Content` | ✅ |

---

## 4. Convenciones de Arquitectura

| Verificación | Resultado |
|---|---|
| Domain sin imports de Spring/Lombok/JPA | ✅ `FichaPerfilAggregate`: solo `java.*` y `shared.*` permitidos |
| `FichaPerfilAggregate` NO extiende `AggregateRoot` | ✅ Decisión explícita del plan: CRUD interno sin eventos |
| Sin emisión de eventos | ✅ Plan §4 lo declara explícitamente |
| `@Transactional(transactionManager = "fichasTransactionManager")` | ✅ |
| Excepciones extienden `ApplicationException` → 400 | ✅ `GlobalAppExceptionHandler` lo confirma |
| Message Catalog: constantes en `FichasMessages.FichaPerfil` | ✅ 3 constantes: `FICHA_NO_PROPIETARIO` (código), `FICHA_NO_PROPIETARIO_MSG` (mensaje), `LOG_MODIFICADA` (log) |
| `@MockitoBean` (no `@MockBean`) | ✅ Infra test usa `@MockitoBean` |
| OpenAPI: `@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement` | ✅ |
| Sin Javadoc en código de usuario | ✅ |

---

## 5. Calidad de Tests

### Domain (8 tests)
- `debeConstruirFicha_cuandoDatosValidos` ✅
- `debeLanzarExcepcion_cuandoTituloVacio` ✅
- `debeLanzarExcepcion_cuandoTituloMuyLargo` ✅
- `debeLanzarExcepcion_cuandoAsesorNull` ✅
- `debeReconstruirSinValidar_cuandoReconstruirEsInvocado` ✅
- `debeActualizarTitulo_cuandoTituloValido` ✅
- `debeRechazarActualizacion_cuandoTituloVacio` ✅
- `debeRechazarActualizacion_cuandoTituloExcedeMaximo` ✅

### Application (6 tests — plan sugería 6, implementados 6)
- `debeModificarTitulo_cuandoDatosValidos` ✅ (salida: `guardar` invocado, título actualizado)
- `debeLanzarExcepcion_cuandoEstudianteNoEsPropietario` ✅ (errorCode verificado, `guardar` nunca invocado)
- `debeLanzarExcepcion_cuandoFichaNoExiste` ✅ (errorCode verificado, `guardar` nunca invocado)
- `debeLanzarExcepcion_cuandoTituloDuplicado` ✅ (errorCode verificado, `guardar` nunca invocado)
- `debePermitirMismoTitulo_cuandoTituloNoCambia` ✅ (no llama a `existsByTituloProyecto`, procede a guardar)
- `debeInvocarGuardar_cuandoModificacionExitosa` ✅

### Infrastructure (7 tests — plan sugería 7, implementados 7)
- `debeRetornar204_cuandoFichaModificada` ✅
- `debeRetornar400_cuandoTituloVacio` ✅ (Jakarta `@Valid`)
- `debeRetornar400_cuandoNoEsPropietario` ✅
- `debeRetornar400_cuandoFichaNoExiste` ✅
- `debeRetornar400_cuandoTituloDuplicado` ✅
- `debeRetornar401_cuandoNoAutenticado` ✅
- `debeRetornar403_cuandoSinPermisos` ✅

Todos los tests de aplicación verifican `errorCode` específico y ausencia de `guardar` en escenarios de error (anti-patrón 4 aplicado correctamente). Los tests usan `catchThrowable` + `assertThat(...).isInstanceOf(...)` consolidado sin duplicar asserts.

---

## 6. Observaciones

1. **`FichaTituloDuplicadoException`** es `public class` (no `final`) mientras que `FichaNoEncontradaException` y `FichaNoPropietarioException` son `public final class`. Consistencia menor — el plan no especifica `final` para la excepción preexistente. No es bloqueante.

2. **JaCoCo**: El fallo en `:fichas:application:jacocoTestReport` con `implicit_dependency` es un problema de configuración Gradle preexistente, no introducido por esta HU.

---

## 7. Veredicto

**HU-35 APROBADA.** La implementación cumple estrictamente el plan `PLAN-HU-35.md`, todas las compilaciones y tests pasan, y se respetan las convenciones de arquitectura hexagonal + DDD del proyecto.
