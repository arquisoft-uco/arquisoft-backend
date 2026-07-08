
# PLAN-HU035 — Modificar Ficha Perfil

## 1. Metadatos

| Campo | Valor |
|---|---|
| **HU** | HU035 |
| **Título** | Modificar Ficha Perfil |
| **Actor** | Estudiante |
| **Objeto de Dominio** | Ficha Perfil |
| **Comando** | Modificar Ficha Perfil |
| **Rama sugerida** | `feature/HU-35-modificar_ficha_perfil` |
| **Bounded Context** | `fichas` (base de datos PostgreSQL: `fichas_perfil`) |
| **Release** | Sprint 5 |
| **Prioridad MoSCoW** | Importante |
| **Complejidad** | Media |
| **Tallaje** | L |
| **Tipo** | Escritura — sin eventos de dominio |
| **Dependencias** | HU034 (Registrar Ficha Perfil) — la ficha ya debe existir |

### Archivos consultados

| Fuente | Archivo |
|---|---|
| HU priorizadas | `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md` |
| Event Storming | `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md` |
| SQL MER | `mer/03_tablas_fichas_perfil.sql` |

---

## 2. Descripción del Event Storming

```
Acción: Modificar Ficha Perfil
├── Actores: Estudiante
├── Descripción: Permite modificar el Título de ficha de perfil
├── Información externa / Read model: (ninguno)
├── Políticas:
│   └── FichaPerfil-POL-01: Asegurar que los datos requeridos para llevar a
│       cabo la acción sean válidos a nivel de tipo de dato, longitud,
│       obligatoriedad, formato y rango.
├── Evento(s):
│   └── Ficha Perfil Modificada
├── Eventos previos:
│   ├── Registrar Nueva información Ficha Perfil (HU034)
│   └── Cambiar Asesor Ficha para Ficha Perfil (HU futura)
└── Comandos posteriores:
    ├── Consultar información de una Ficha Perfil que genero el Coordinador
    └── Consultar información de una Ficha Perfil al que Pertenece
```

---

## 3. Funcionalidad detallada

- **Endpoint:** `PATCH /fichas-perfil/{id}`
- **Actor:** Estudiante con autoridad `fichas:ficha-perfil:update`
- **Mecanismo de autoría:** El Estudiante autenticado solo puede modificar fichas de las que es propietario (validado vía tabla `estudiante_ficha_perfil`). Se extrae el `UUID` del `jwt.getSubject()` para identificar al estudiante.
- **Request body:**
  ```json
  { "tituloProyecto": "Nuevo título del proyecto" }
  ```
- **Validaciones:**
  - `tituloProyecto` not blank, max 100 caracteres
  - El estudiante autenticado debe ser propietario de la ficha (400 si no)
  - La ficha debe existir (400 si no)
  - El título no debe estar duplicado (400 si ya existe otra ficha con ese título)
  - Validación de tipo/longitud vía `@Valid` + Jakarta + `DomainValidator` en el aggregate
- **Flujo exitoso:**
  1. Extraer `estudianteId` del JWT (`jwt.getSubject()`)
  2. Validar request (Jakarta `@Valid`)
  3. Validar que el estudiante es propietario de la ficha → `estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(fichaId, estudianteId)`. Si no, lanzar `FichaNoPropietarioException` (400)
  4. Buscar ficha por ID → si no existe, lanzar `FichaNoEncontradaException` (400)
  5. Validar unicidad del título (solo si cambió) → si ya existe, lanzar `FichaTituloDuplicadoException` (400)
  6. Delegar al aggregate: `ficha.actualizarTitulo(nuevoTitulo)` — el aggregate valida invariantes
  7. Persistir el aggregate vía `FichaPerfilOutputPort.guardar(ficha)`
  8. Retornar 204 No Content
- **Respuesta exitosa:** `204 No Content`

---

## 4. Eventos de Dominio

**Esta HU no emite eventos.**

La acción es un CRUD interno del contexto `fichas` sin consumidores conocidos ni casos de auditoría que justifiquen eventos. La entidad raíz `FichaPerfilAggregate` **no extiende `AggregateRoot`** y mantiene su estado actual: `class` plano con factories `crear`/`reconstruir`.

El Event Storming documenta el evento `Ficha Perfil Modificada` como parte del modelo conceptual, pero la decisión de implementación para esta iteración es no emitirlo. Si una HU futura requiere consumidores que reaccionen a este cambio, será esa HU la que añada `extends AggregateRoot` y cree la clase del evento.

---

## 5. Árbol de Archivos

### Archivos a modificar

| Ruta absoluta | Acción |
|---|---|
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Agregar constantes: `LOG_MODIFICADA`, `FICHA_NO_PROPIETARIO`, `FICHA_NO_PROPIETARIO_MSG` |

### Archivos a crear

| Ruta absoluta | Tipo | Sufijo |
|---|---|---|
| `fichas/application/.../fichaperfil/command/model/ModificarFichaPerfilCommand.java` | record | Command |
| `fichas/application/.../fichaperfil/command/port/in/ModificarFichaPerfilInputPort.java` | interface | InputPort |
| `fichas/application/.../fichaperfil/command/ModificarFichaPerfilUseCase.java` | @Service | UseCase |
| `fichas/application/.../fichaperfil/exception/FichaNoEncontradaException.java` | exception | Exception |
| `fichas/application/.../fichaperfil/exception/FichaNoPropietarioException.java` | exception | Exception |
| `fichas/infrastructure/.../fichaperfil/command/adapter/in/web/ModificarFichaPerfilInputAdapter.java` | @RestController | InputAdapter |
| `fichas/infrastructure/.../fichaperfil/command/adapter/in/web/dto/ModificarFichaPerfilRequestDTO.java` | DTO | RequestDTO |

> **Nota:** `FichaPerfilAggregate` **no se modifica**. El método `actualizarTitulo` ya existe y funciona correctamente. No se añade `extends AggregateRoot` ni emisión de eventos.

---

## 6. Detalle por capa

### 6.1 Capa Domain

**Sin cambios.** `FichaPerfilAggregate` mantiene su implementación actual. El método `actualizarTitulo(String nuevoTitulo)` ya existe con validaciones completas y se usa tal cual desde el nuevo use case.

### 6.2 Capa Application

#### 6.2.1 `ModificarFichaPerfilCommand` (NUEVO)

```
fichas/application/.../fichaperfil/command/model/ModificarFichaPerfilCommand.java
```

```java
public record ModificarFichaPerfilCommand(
    UUID fichaId,
    UUID estudianteId,
    String tituloProyecto
) {}
```

#### 6.2.2 `ModificarFichaPerfilInputPort` (NUEVO)

```
fichas/application/.../fichaperfil/command/port/in/ModificarFichaPerfilInputPort.java
```

```java
public interface ModificarFichaPerfilInputPort
        extends VoidInputPort<ModificarFichaPerfilCommand> {}
```

#### 6.2.3 `FichaNoEncontradaException` (NUEVO)

```
fichas/application/.../fichaperfil/exception/FichaNoEncontradaException.java
```

- Extiende `ApplicationException` de `shared:domain`
- Código de error: `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA`
- Mensaje: `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA_MSG`

#### 6.2.4 `FichaNoPropietarioException` (NUEVO)

```
fichas/application/.../fichaperfil/exception/FichaNoPropietarioException.java
```

- Extiende `ApplicationException` de `shared:domain`
- Código de error: `FichasMessages.FichaPerfil.FICHA_NO_PROPIETARIO`
- Mensaje: `FichasMessages.FichaPerfil.FICHA_NO_PROPIETARIO_MSG`
- Mapeo HTTP: `400 Bad Request` (manejado por `GlobalAppExceptionHandler` según la capa: application → 400)

#### 6.2.5 `ModificarFichaPerfilUseCase` (NUEVO)

```
fichas/application/.../fichaperfil/command/ModificarFichaPerfilUseCase.java
```

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ModificarFichaPerfilUseCase implements ModificarFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final EstudianteFichaPerfilOutputPort estudianteFichaPerfilOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ModificarFichaPerfilCommand command) {
        if (!estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(
                command.fichaId(), command.estudianteId())) {
            throw new FichaNoPropietarioException(command.fichaId(), command.estudianteId());
        }

        var ficha = fichaPerfilOutputPort.buscarPorId(command.fichaId())
            .orElseThrow(() -> new FichaNoEncontradaException(command.fichaId()));

        if (!ficha.getTituloProyecto().equals(command.tituloProyecto())
                && fichaPerfilOutputPort.existsByTituloProyecto(command.tituloProyecto())) {
            throw new FichaTituloDuplicadoException(command.tituloProyecto());
        }

        ficha.actualizarTitulo(command.tituloProyecto());
        fichaPerfilOutputPort.guardar(ficha);

        log.info(FichasMessages.FichaPerfil.LOG_MODIFICADA, ficha.getId());
    }
}
```

### 6.3 Capa Infrastructure

#### 6.3.1 `ModificarFichaPerfilRequestDTO` (NUEVO)

```
fichas/infrastructure/.../fichaperfil/command/adapter/in/web/dto/ModificarFichaPerfilRequestDTO.java
```

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModificarFichaPerfilRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String tituloProyecto;

    public ModificarFichaPerfilCommand toCommand(UUID fichaId, UUID estudianteId) {
        return new ModificarFichaPerfilCommand(fichaId, estudianteId, tituloProyecto);
    }
}
```

#### 6.3.2 `ModificarFichaPerfilInputAdapter` (NUEVO)

```
fichas/infrastructure/.../fichaperfil/command/adapter/in/web/ModificarFichaPerfilInputAdapter.java
```

- `@RestController` + `@RequestMapping("/fichas-perfil")`
- `@Tag(name = "Fichas Perfil", ...)`
- Endpoint: `PATCH /{id}`
- Extrae `estudianteId` del JWT: `@AuthenticationPrincipal Jwt jwt` → `UUID.fromString(jwt.getSubject())`
- `@PreAuthorize("hasAuthority('fichas:ficha-perfil:update')")`
- `@Valid @RequestBody ModificarFichaPerfilRequestDTO`
- Llama a `modificarFichaPerfilInputPort.ejecutar(request.toCommand(id, estudianteId))`
- Retorna `ResponseEntity<Void>` con 204 No Content
- OpenAPI: `@Operation`, `@ApiResponses` con 204, 400, 401, 403

### 6.4 Capa Shared:message

#### `FichasMessages.java` (MODIFICADO)

Agregar a `FichaPerfil` nested class:

```java
// Códigos de error
public static final String FICHA_NO_PROPIETARIO = "FICHA_NO_PROPIETARIO";

// Mensajes de error
public static final String FICHA_NO_PROPIETARIO_MSG =
        "El estudiante %s no es propietario de la ficha %s";

// Logs
public static final String LOG_MODIFICADA = "Ficha de perfil modificada — id={}";
```

Ya existen: `FICHA_NO_ENCONTRADA`, `FICHA_NO_ENCONTRADA_MSG`, `FICHA_TITULO_DUPLICADO`, `TITULO_DUPLICADO`.

---

## 7. Endpoint REST

| Método | Path | Autoridad | Request Body | Response |
|---|---|---|---|---|
| `PATCH` | `/fichas-perfil/{id}` | `fichas:ficha-perfil:update` | `ModificarFichaPerfilRequestDTO` | `204 No Content` |

**Path:** `PATCH /fichas-perfil/{id}` — genérico y extensible a futuros campos modificables. El título se envía en el body.

### Códigos de respuesta

| Código | Descripción |
|---|---|
| `204` | Ficha modificada exitosamente |
| `400` | Error de aplicación (capa application): título duplicado, ficha no encontrada, no es propietario, o datos inválidos. Cada caso retorna un `errorCode` y mensaje específico. |
| `401` | No autenticado |
| `403` | Sin permisos — no posee la autoridad `fichas:ficha-perfil:update` (rechazado por `@PreAuthorize`) |

---

## 8. Migraciones Flyway

**No se requieren migraciones** — esta HU modifica solo el campo existente `titulo_proyecto` de la tabla `ficha_perfil`, sin cambios de esquema.

---

## 9. Casos de Prueba Sugeridos

### 9.1 Tests de Domain

| Test | Clase | Escenario |
|---|---|---|
| `debeActualizarTitulo_cuandoTituloValido` | `FichaPerfilAggregateTest` | Título válido → se actualiza |
| `debeRechazar_cuandoTituloVacio` | `FichaPerfilAggregateTest` | `""` → `DomainException` |
| `debeRechazar_cuandoTituloExcedeMaximo` | `FichaPerfilAggregateTest` | 101 caracteres → `DomainException` |

### 9.2 Tests de Application

| Test | Clase | Escenario |
|---|---|---|
| `debeModificarFicha_cuandoDatosValidos` | `ModificarFichaPerfilUseCaseTest` | Estudiante propietario, ficha existe, título único → éxito, sin excepción |
| `debeLanzarExcepcion_cuandoEstudianteNoEsPropietario` | `ModificarFichaPerfilUseCaseTest` | `existePorFichaYEstudiante` retorna `false` → `FichaNoPropietarioException` |
| `debeLanzarExcepcion_cuandoFichaNoExiste` | `ModificarFichaPerfilUseCaseTest` | `buscarPorId` retorna `empty` → `FichaNoEncontradaException` |
| `debeLanzarExcepcion_cuandoTituloDuplicado` | `ModificarFichaPerfilUseCaseTest` | `existsByTituloProyecto` retorna `true` → `FichaTituloDuplicadoException` |
| `debePermitirMismoTitulo_cuandoTituloNoCambia` | `ModificarFichaPerfilUseCaseTest` | Mismo título que ya tiene → no valida duplicado, procede |
| `debeGuardarFicha_cuandoTituloModificado` | `ModificarFichaPerfilUseCaseTest` | Verificar que `fichaPerfilOutputPort.guardar(ficha)` es invocado |

### 9.3 Tests de Infrastructure

| Test | Clase | Escenario |
|---|---|---|
| `debeRetornar204_cuandoFichaModificada` | `ModificarFichaPerfilInputAdapterTest` | Request válido → 204 No Content |
| `debeRetornar400_cuandoTituloVacio` | `ModificarFichaPerfilInputAdapterTest` | `@Valid` rechaza → 400 |
| `debeRetornar400_cuandoNoEsPropietario` | `ModificarFichaPerfilInputAdapterTest` | `FichaNoPropietarioException` → 400 |
| `debeRetornar400_cuandoFichaNoExiste` | `ModificarFichaPerfilInputAdapterTest` | `FichaNoEncontradaException` → 400 |
| `debeRetornar400_cuandoTituloDuplicado` | `ModificarFichaPerfilInputAdapterTest` | `FichaTituloDuplicadoException` → 400 |
| `debeRetornar401_cuandoNoAutenticado` | `ModificarFichaPerfilInputAdapterTest` | Sin JWT → 401 |
| `debeRetornar403_cuandoSinPermisos` | `ModificarFichaPerfilInputAdapterTest` | Sin autoridad → 403 |

---

## 10. Decisiones Tomadas

| # | Pregunta | Decisión |
|---|---|---|
| 1 | Ruta del endpoint | `PATCH /fichas-perfil/{id}` — genérico, extensible a más campos en el futuro |
| 2 | Formato de respuesta | `204 No Content` — el frontend ya conoce el nuevo título, no necesita datos en la respuesta. |
| 3 | Validación de autoría | **Sí.** El estudiante autenticado debe ser propietario de la ficha (validado vía `estudiante_ficha_perfil`). Si no lo es → `400 FichaNoPropietarioException` (`ApplicationException` → 400 según convención de capas). |
| 4 | ¿Emitir eventos? | **No.** Esta implementación no emite `FichaPerfilModificadaEvent`. `FichaPerfilAggregate` no extiende `AggregateRoot`. El Event Storming documenta el evento a nivel conceptual, pero no hay consumidores que justifiquen emitirlo en esta iteración. |

---

## 11. Trazabilidad

| Elemento del plan | Origen |
|---|---|
| `FichaPerfil-POL-01` (validación de datos) | Event Storming § Modificar Ficha Perfil → Políticas |
| `FichaPerfil-POL-02` (título único) | Event Storming § Registrar Nueva información → Políticas (aplica también a modificación) |
| Tabla `ficha_perfil` columnas | MER `03_tablas_fichas_perfil.sql` |
| `@PreAuthorize("hasAuthority('fichas:ficha-perfil:update')")` | Consistente con `fichas:ficha-perfil:create` y `fichas:ficha-perfil:view` existentes |
| Autoría (estudiante propietario vía `estudiante_ficha_perfil`) | Patrón existente en `AgregarItemFichaPerfilInputAdapter` — `jwt.getSubject()` + `EstudianteFichaPerfilOutputPort.existePorFichaYEstudiante()` |
| Sin eventos — `FichaPerfilAggregate` no extiende `AggregateRoot` | Decisión explícita: CRUD interno sin consumidores conocidos |

---

## 12. Checklist de implementación

- [x] `ModificarFichaPerfilCommand` record (fichaId, estudianteId, tituloProyecto)
- [x] `ModificarFichaPerfilInputPort` interface
- [x] `FichaNoEncontradaException`
- [x] `FichaNoPropietarioException`
- [x] `ModificarFichaPerfilUseCase` (orquestación: validar autoría → buscar ficha → validar unicidad → actualizar → persistir)
- [x] `ModificarFichaPerfilRequestDTO` con `@Valid`
- [x] `ModificarFichaPerfilInputAdapter` (`@RestController` + OpenAPI + `@AuthenticationPrincipal Jwt jwt`)
- [x] Constantes en `FichasMessages.FichaPerfil` (LOG_MODIFICADA, FICHA_NO_PROPIETARIO, FICHA_NO_PROPIETARIO_MSG)
- [ ] Tests domain (`FichaPerfilAggregateTest` — actualizarTitulo)
- [ ] Tests application (`ModificarFichaPerfilUseCaseTest`)
- [ ] Tests infrastructure (`ModificarFichaPerfilInputAdapterTest`)
- [x] `./gradlew build -x test` exitoso
- [ ] `./gradlew jacocoTestReport` ≥ 75%

---

## 13. Historial de versiones

| Versión | Fecha | Cambio |
|---|---|---|
| 1.0 | 2026-06-30 | Plan inicial |
| 2.0 | 2026-06-30 | Corrección: sin eventos, sin AggregateRoot. Tablas corregidas. |
| 2.1 | 2026-06-30 | Status codes ajustados a convencion de capas (app→400). Rama sugerida y BD corregidas. |

---

## 14. Estados de Agentes

| Agente | Estado     | Fecha |
|---|------------|---|
| Planificador | Completado | 2026-06-30 |
| Implementador | Completado | 2026-06-30 |
| Tester | Completado | 2026-06-30 | 21/21 tests OK — Cobertura: N/D (JaCoCo no configurado en build.gradle) |
| Validación | @validator-analyze | ✅ Completado | 2026-06-30 | Score: 100/100 — APROBADO |
| Commit | Completado | 2026-06-30 |
