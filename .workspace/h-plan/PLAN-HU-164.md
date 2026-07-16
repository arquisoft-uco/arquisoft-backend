# PLAN: HU164-Cambiar Asesor Ficha para Ficha Perfil

## Metadata
- **ID Historia:** HU-164
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura
- **¿Usa AggregateRoot?:** No — la HU no emite eventos; `FichaPerfilAggregate` es una clase plana con factories `crear`/`reconstruir`, sin extensión de `AggregateRoot`.
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-07-09
- **Rama sugerida:** `feature/HU-164-cambiar-asesor-ficha-perfil`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** Se confirmó el enum `EstadoFicha` existente con valores terminales `NO_APROBADA`, `APROBADA_CON_OBSERVACIONES`, `APROBADA`. La validación de estado terminal va en el aggregate. No se requiere auditoría.

---

## 1. Resumen Funcional

Esta HU permite que el Coordinador cambie el Asesor Ficha asignado a una Ficha Perfil existente. La acción se habilita cuando una circunstancia (renuncia, carga de trabajo, conflicto de interés) impide la participación del asesor anterior. La HU valida que: (1) la ficha existe, (2) el nuevo asesor existe, (3) el nuevo asesor es diferente al actual (POL-05), (4) el estado de la ficha no es terminal (`NO_APROBADA`, `APROBADA_CON_OBSERVACIONES`, `APROBADA` — documentación del usuario). Esta HU **NO emite eventos de dominio** según instrucción del usuario.

**Lo que NO cubre:**
- No audita el cambio histórico de asesores (sin trazabilidad).
- No valida disponibilidad de carga del nuevo asesor (regla fuera de alcance).
- No notifica a los estudiantes ni al asesor anterior.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | Coordinador invoca `PATCH /fichas-perfil/{id}/asesor-ficha` con un `asesorFichaId` válido, la ficha existe y el estado NO es terminal, y el nuevo asesor es distinto al actual | 204 No Content |
| 2 | El `asesorFichaId` es nulo o vacío | 400 Bad Request con mensaje de validación Jakarta |
| 3 | La ficha no existe | 400 Bad Request con mensaje `FichaPerfilNoEncontradaException` |
| 4 | El asesor no existe | 400 Bad Request con mensaje `AsesorFichaNoEncontradoException` |
| 5 | El nuevo `asesorFichaId` es idéntico al actual | 422 Unprocessable Content con `DomainValidationException` — `fieldErrors[{field: "asesorFichaId", errorCode: "MISMO_ASESOR"}]` |
| 6 | El estado de la ficha es terminal (`NO_APROBADA`, `APROBADA_CON_OBSERVACIONES`, `APROBADA`) | 422 Unprocessable Content con `DomainValidationException` — `fieldErrors[{field: "estadoFicha", errorCode: "ESTADO_TERMINAL"}]` |

---

## 3. Reglas de Negocio

- **FichaPerfil-POL-01:** Asegurar que el `asesorFichaId` sea válido (UUID no nulo, tipo de dato correcto) — validado en dos capas: (1) `@NotNull` en `CambiarAsesorFichaRequestDTO` (primera línea de defensa en la frontera HTTP) y (2) `DomainValidator.notNull(nuevoAsesorFichaId, ...)` dentro del método `cambiarAsesorFicha()` del aggregate mediante `ValidationResult` (el dominio no confía en que el dato llegue siempre desde el DTO).
- **FichaPerfil-POL-05:** Asegurar que el Asesor nuevo no sea el mismo que tenía anteriormente — validado en el método `cambiarAsesorFicha()` del aggregate con excepción de dominio si `asesorFichaId.equals(this.asesorFichaId)`.
- **Restricción por estado terminal (documentada por usuario):** No permitir cambio de asesor cuando el estado actual de la ficha es terminal. El use case consulta el estado actual vía `EstadoFichaPerfilQueryOutputPort.obtenerEstadoActual(fichaPerfilId)` y lo pasa como parámetro al método `cambiarAsesorFicha(UUID nuevoAsesorFichaId, EstadoFicha estadoActual)` del aggregate. El aggregate importa `EstadoFicha` y llama `estadoActual.esTerminal()` — si retorna `true`, lanza `EstadoFichaTerminalException` (dominio → 422). El aggregate **NO** almacena el estado como atributo propio.

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `FichaPerfilAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** No — la HU no emite eventos. Es una clase plana con factories `crear`/`reconstruir`. El archivo ya existe; esta HU lo **modifica** únicamente para agregar el método `cambiarAsesorFicha(UUID, EstadoFicha)`. No se agrega ningún atributo nuevo al aggregate.
- **ID:** `UUID`

> **Coherencia obligatoria — verificada.** El archivo `FichaPerfilAggregate.java` fue abierto y leído: no extiende `AggregateRoot`. La sección "Eventos de Dominio que emite" declara "ninguno". Coherencia confirmada.

### Atributos por objeto de dominio (extraídos del modelo enriquecido)

#### `FichaPerfil` (Aggregate Root — archivo a modificar)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí (en `crear()`) | Identifica la ficha |
| `tituloProyecto` | `String` | 1-100 | Sí | Sí (por HU-033) | No | Limpiar espacios (inicio/fin) |
| `asesorFichaId` | `UUID` | — | Sí | **Sí (por esta HU)** | No | Referencia al asesor ficha (réplica local, FK en JPA) |

**Combinaciones únicas (Restricciones):**
- El `tituloProyecto` es único (ya implementado en Flyway `uq_ficha_perfil_titulo` y validado vía use case en HU-160).

### Traducción del modelo enriquecido a código

| Característica del modelo | Traducción en código |
|---|---|
| `asesorFichaId` modificable | Setter privado `setAsesorFichaId()` (ya existe) + método público `cambiarAsesorFicha(UUID nuevoAsesorFichaId, EstadoFicha estadoActual)` (a agregar). El use case consulta el estado actual vía `EstadoFichaPerfilQueryOutputPort` y lo pasa como parámetro. El aggregate llama `estadoActual.esTerminal()` — importa el enum `EstadoFicha` sin almacenarlo como atributo. |

### Estados y tipos: planearlos como enum de dominio

`EstadoFicha` ya existe como enum en `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadoficha/EstadoFicha.java` con 6 valores: `EN_CONSTRUCCION`, `EN_REVISION`, `DISPONIBLE_PARA_EVALUACION`, `APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA`. La tabla catálogo `estado_ficha` tiene PK `VARCHAR(50)` poblada con las constantes del enum (`id` = `name()`). La tabla `ficha_perfil` referencia al catálogo con FK `VARCHAR` — no `UUID`. Esta HU **NO modifica** el enum (ya existe), solo lo consume en el aggregate para la validación de estado terminal.

### Eventos de Dominio que emite

Eventos: ninguno.
Razón: CRUD interno sin consumidores conocidos ni casos de auditoría identificados (decisión confirmada por el usuario en su mensaje inicial: "Esta HU NO emite eventos de dominio. No hay consumidores.").

Implicaciones:
  - La entidad raíz `FichaPerfilAggregate` NO extiende `AggregateRoot` — es una clase plana con factories `crear`/`reconstruir` (estado confirmado al leer el archivo).
  - El método `cambiarAsesorFicha()` NO acumula eventos (no existe `publishEvent`).
  - El use case NO inyecta `EventPublisher`, no hay drenado de eventos.
  - No se crean archivos en `domain/fichaPerfil/event/`.

---

## 5. Integraciones Externas (solo si la HU lo requiere)

No aplica — la HU solo interactúa con PostgreSQL y no requiere integraciones con sistemas externos (Keycloak, SMTP, MinIO, etc.).

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS — caso de uso write

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/model/CambiarAsesorFichaCommand.java` | `record` | Intención de negocio. Campos: `UUID fichaPerfilId`, `UUID nuevoAsesorFichaId`. |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/port/in/CambiarAsesorFichaInputPort.java` | Interface (vacía) | Extiende `VoidInputPort<CambiarAsesorFichaCommand>` de `shared:domain` (la acción no retorna nada al cliente). |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/exception/FichaPerfilNoEncontradaException.java` | Exception del use case | Extiende `ApplicationException` (ficha no encontrada → 400). Mensaje parametrizado con el `UUID` de la ficha. Ubicación directa bajo `fichaPerfil/exception/` (no anidada bajo `command/` — la excepción pertenece a la entidad, no al slice CQRS). |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/exception/AsesorFichaNoEncontradoException.java` | Exception del use case | Extiende `ApplicationException` (asesor no encontrado → 400). Mensaje parametrizado con el `UUID` del asesor. |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/CambiarAsesorFichaUseCase.java` | UseCase | `@Component` que implementa el `CambiarAsesorFichaInputPort`. Patrón: `buscar ficha → validar existencia ficha → validar existencia asesor → invocar aggregate.cambiarAsesorFicha() → guardar`. **NO inyecta `EventPublisher`** (sin eventos). |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/in/web/dto/CambiarAsesorFichaRequestDTO.java` | `record` | `record` con anotaciones Jakarta: `@NotNull UUID asesorFichaId` (con mensaje de error custom). Método `toCommand(UUID fichaPerfilId)` que produce el `CambiarAsesorFichaCommand` (el `fichaPerfilId` proviene del `@PathVariable`). |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/in/web/CambiarAsesorFichaInputAdapter.java` | `@RestController` | Inyecta el `CambiarAsesorFichaInputPort`. Endpoint `PATCH /fichas-perfil/{id}/asesor-ficha` que retorna `ResponseEntity<Void>` con `204 No Content`. ADR-011: `@Tag`, `@Operation`, `@ApiResponses` (204, 400, 401, 403, 422), `@SecurityRequirement(name="bearerAuth")`. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/asesorficha/query/port/out/AsesorFichaQueryOutputPort.java` | Interface (puerto de consulta) | Puerto de consulta para validar existencia del asesor. Paquete correcto: `asesorficha.query.port.out` (no `fichaperfil.query.port.out`). |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadofichaperfil/query/port/out/EstadoFichaPerfilQueryOutputPort.java` | Interface (puerto de consulta) | **YA EXISTE** — tiene el método `Optional<EstadoFicha> obtenerEstadoActual(UUID fichaPerfilId)`. El use case lo inyecta sin modificarlo. |
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Catálogo | **MODIFICAR** — agregar en la nested class `FichaPerfil` (que ya existe) las constantes nuevas en el orden de las 5 secciones. Ver inventario en sección de "Catálogo de mensajes". |

### Catálogo de mensajes (`shared:message`) — fila obligatoria

Esta HU introduce mensajes, códigos y logs nuevos. El plan declara una fila MODIFICAR para `shared/message/FichasMessages.java`.

Inventario de constantes a agregar al catálogo en esta HU:

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA` | Códigos de error | `String` | `"FICHA_NO_ENCONTRADA"` | `FichaPerfilNoEncontradaException` (errorCode) |
| `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA_MSG` | Mensajes de error | `String` | `"La ficha de perfil con id %s no existe"` | `FichaPerfilNoEncontradaException` (mensaje, `.formatted(id)`) |
| `FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO` | Códigos de error | `String` | `"ASESOR_NO_ENCONTRADO"` | `AsesorFichaNoEncontradoException` (errorCode) |
| `FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO_MSG` | Mensajes de error | `String` | `"El asesor ficha con id %s no existe"` | `AsesorFichaNoEncontradoException` (mensaje, `.formatted(asesorId)`) |
| `FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA_ID` | Campos | `String` | `"asesorFichaId"` | `result.addError(CAMPO_ASESOR_FICHA_ID, MISMO_ASESOR, ...)` en `cambiarAsesorFicha()` |
| `FichasMessages.FichaPerfil.CAMPO_ESTADO_FICHA` | Campos | `String` | `"estadoFicha"` | `result.addError(CAMPO_ESTADO_FICHA, ESTADO_TERMINAL, ...)` en `cambiarAsesorFicha()` |
| `FichasMessages.FichaPerfil.MISMO_ASESOR` | Códigos de error | `String` | `"MISMO_ASESOR"` | `result.addError(...)` en `cambiarAsesorFicha()` (POL-05) |
| `FichasMessages.FichaPerfil.MISMO_ASESOR_MSG` | Mensajes de error | `String` | `"El asesor nuevo no puede ser el mismo que el actual: %s"` | `result.addError(CAMPO_ASESOR_FICHA_ID, MISMO_ASESOR, MISMO_ASESOR_MSG.formatted(nuevoAsesorFichaId))` |
| `FichasMessages.FichaPerfil.ESTADO_TERMINAL` | Códigos de error | `String` | `"ESTADO_TERMINAL"` | `result.addError(...)` en `cambiarAsesorFicha()` (estado terminal) |
| `FichasMessages.FichaPerfil.ESTADO_TERMINAL_MSG` | Mensajes de error | `String` | `"No se puede cambiar el asesor porque la ficha está en estado terminal: %s"` | `result.addError(CAMPO_ESTADO_FICHA, ESTADO_TERMINAL, ESTADO_TERMINAL_MSG.formatted(estadoActual))` |
| `FichasMessages.FichaPerfil.LOG_ASESOR_CAMBIADO` | Logs | `String` | `"Asesor de ficha cambiado — fichaId={}, nuevoAsesorId={}"` | `log.info` en `CambiarAsesorFichaUseCase` |

### Archivos a MODIFICAR (si aplica)

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/src/main/java/com/arquisoft/fichas/domain/fichaperfil/aggregate/FichaPerfilAggregate.java` | **Solo agregar el método de negocio** `public void cambiarAsesorFicha(UUID nuevoAsesorFichaId, EstadoFicha estadoActual)`. No se agregan atributos ni se modifican factories. El método: (1) valida `DomainValidator.notNull(nuevoAsesorFichaId, ...)` (POL-01 en domain), (2) valida `!nuevoAsesorFichaId.equals(this.asesorFichaId)` — lanza `MismoAsesorFichaException` si son iguales (POL-05), (3) llama `estadoActual.esTerminal()` — si `true`, lanza `EstadoFichaTerminalException`, (4) invoca `setAsesorFichaId(nuevoAsesorFichaId, result)`. Todas las validaciones usan `ValidationResult` + `throwIfHasErrors()`. El aggregate importa `EstadoFicha` del mismo contexto pero **no lo almacena como campo**. |
| `fichas/src/main/java/com/arquisoft/fichas/domain/fichaperfil/port/out/FichaPerfilOutputPort.java` | **Verificar si existe método `Optional<FichaPerfilAggregate> buscarPorId(UUID id);`.** Si no existe, agregarlo (el use case lo necesita para recuperar el aggregate antes de invocarlo). Si existe, no modificar. |
| `fichas/application/src/main/java/com/arquisoft/fichas/application/asesorficha/query/port/out/AsesorFichaQueryOutputPort.java` | Puerto de consulta con método `boolean existsById(UUID id)`. Paquete correcto: `asesorficha.query.port.out` — **no** `fichaperfil.query.port.out`. `RegistrarFichaPerfilUseCase` ya lo inyectaba desde este paquete. |
| `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/out/persistence/FichaPerfilCommandOutputAdapter.java` | **Sin cambios si ya implementa `buscarPorId()`.** Verificar que exista el método `Optional<FichaPerfilAggregate> buscarPorId(UUID id)` que delega al repositorio. Si no existe, agregarlo. Patrón: `return jpaRepository.findById(id).map(mapper::toDomain);`. |
| `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/query/adapter/out/persistence/AsesorFichaQueryOutputAdapter.java` | **Verificar/crear clase** que implementa `AsesorFichaQueryOutputPort`. Si no existe, crearla (patrón: `@Component` + `@RequiredArgsConstructor` + inyecta `AsesorFichaJpaRepository` + implementa `existsById(UUID)` delegando a `jpaRepository.existsById(id)`). Si existe, verificar que tenga el método `existsById()`. |
| `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/persistence/AsesorFichaJpaRepository.java` | **Verificar/crear interface** que extiende `JpaRepository<AsesorFichaJpaEntity, UUID>`. Si no existe, crearla. Si existe, no modificar (el método `existsById()` es heredado de `JpaRepository`). |
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | **Agregar constantes a la nested class `FichaPerfil`** según inventario de la tabla anterior. Mantener el orden de las 5 secciones: (1) Campos, (2) Límites, (3) Códigos de error, (4) Mensajes de error, (5) Logs. |

### Archivos de MENSAJERÍA RabbitMQ (si aplica)

No aplica — esta HU no emite eventos de dominio (decisión confirmada por el usuario).

---

## 7. Detalle por Archivo

### `FichaPerfilAggregate.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.domain.fichaperfil.aggregate`
- **Tipo:** Aggregate Root
- **Responsabilidad:** Esta HU agrega únicamente el método `cambiarAsesorFicha(UUID, EstadoFicha)`. No se añaden atributos ni se tocan los factories `crear`/`reconstruir`. No se crean excepciones de dominio propias — todas las violaciones se acumulan con `ValidationResult.addError()` y se lanzan como `DomainValidationException` al final.
- **Features Java 21 aplicables:** `var` para `ValidationResult` en métodos de negocio.
- **Métodos principales:**
    - `public void cambiarAsesorFicha(UUID nuevoAsesorFichaId, EstadoFicha estadoActual)` — **agregar**. Flujo con Notification Pattern: crea `var result = new ValidationResult()`, luego: (1) `DomainValidator.notNull(nuevoAsesorFichaId, FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA_ID, FichasMessages.FichaPerfil.ASESOR_FICHA_REQUERIDO, result)` (POL-01 en domain), (2) si `nuevoAsesorFichaId != null && nuevoAsesorFichaId.equals(this.asesorFichaId)` → `result.addError(FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA_ID, FichasMessages.FichaPerfil.MISMO_ASESOR, FichasMessages.FichaPerfil.MISMO_ASESOR_MSG.formatted(nuevoAsesorFichaId))` (POL-05), (3) si `estadoActual.esTerminal()` → `result.addError(FichasMessages.FichaPerfil.CAMPO_ESTADO_FICHA, FichasMessages.FichaPerfil.ESTADO_TERMINAL, FichasMessages.FichaPerfil.ESTADO_TERMINAL_MSG.formatted(estadoActual))`, (4) `result.throwIfHasErrors()` — lanza `DomainValidationException` con todos los `fieldErrors[]` acumulados si hay errores, (5) `setAsesorFichaId(nuevoAsesorFichaId, result)` con un nuevo `ValidationResult` limpio. El parámetro `estadoActual` lo consulta el use case antes de invocar — el aggregate no lo persiste.
- **Dependencias:** `EstadoFicha`, `DomainValidator`, `ValidationResult`, `FichasMessages`, `java.util.UUID`

### `CambiarAsesorFichaCommand.java`
- **Paquete:** `com.arquisoft.fichas.application.fichaPerfil.command.model`
- **Tipo:** `record` (Command)
- **Responsabilidad:** Intención de cambiar el asesor de una ficha.
- **Features Java 21 aplicables:** `record` para inmutabilidad.
- **Campos:** `UUID fichaPerfilId`, `UUID nuevoAsesorFichaId` (ambos en español).
- **Dependencias:** `java.util.UUID`

### `CambiarAsesorFichaInputPort.java`
- **Paquete:** `com.arquisoft.fichas.application.fichaPerfil.command.port.in`
- **Tipo:** Interface (vacía)
- **Responsabilidad:** Puerto de entrada para el comando de cambiar asesor.
- **Extiende:** `VoidInputPort<CambiarAsesorFichaCommand>` (de `shared:domain.port.in`) — no retorna nada al cliente.
- **Dependencias:** `VoidInputPort`, `CambiarAsesorFichaCommand`

### `FichaPerfilNoEncontradaException.java`
- **Paquete:** `com.arquisoft.fichas.application.fichaPerfil.exception`
- **Tipo:** Exception del use case
- **Responsabilidad:** Se lanza cuando la ficha no existe (400 Bad Request). Extiende `ApplicationException`.
- **Constructor:** Recibe `UUID fichaId` y construye el mensaje parametrizado usando `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA_MSG.formatted(fichaId)` + código `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA`.
- **Dependencias:** `com.arquisoft.shared.exception.ApplicationException`, `FichasMessages`, `java.util.UUID`

### `AsesorFichaNoEncontradoException.java`
- **Paquete:** `com.arquisoft.fichas.application.fichaPerfil.exception`
- **Tipo:** Exception del use case
- **Responsabilidad:** Se lanza cuando el asesor no existe (400 Bad Request). Extiende `ApplicationException`.
- **Constructor:** Recibe `UUID asesorId` y construye el mensaje parametrizado usando `FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO_MSG.formatted(asesorId)` + código `FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO`.
- **Dependencias:** `com.arquisoft.shared.exception.ApplicationException`, `FichasMessages`, `java.util.UUID`

### `CambiarAsesorFichaUseCase.java`
- **Paquete:** `com.arquisoft.fichas.application.fichaPerfil.command`
- **Tipo:** UseCase
- **Responsabilidad:** Orquesta el caso de uso de cambiar asesor: valida existencias, invoca el método del aggregate, persiste. **NO inyecta `EventPublisher`** (sin eventos).
- **Anotaciones:** `@Component`, `@RequiredArgsConstructor`, `@Slf4j`, `@Transactional(transactionManager = "fichasTransactionManager")` — con qualifier explícito.
- **Features Java 21 aplicables:** `var` para variables locales evidentes.
- **Métodos principales:**
    - `@Override public void ejecutar(CambiarAsesorFichaCommand command)` — implementa el `VoidInputPort`. Patrón: (1) buscar ficha con `fichaPerfilOutputPort.buscarPorId(command.fichaPerfilId())`, lanzar `FichaPerfilNoEncontradaException` si `Optional.isEmpty()`, (2) validar existencia asesor con `asesorFichaQueryOutputPort.existsById(command.nuevoAsesorFichaId())`, lanzar `AsesorFichaNoEncontradoException` si es `false`, (3) consultar estado actual con `estadoFichaPerfilQueryOutputPort.obtenerEstadoActual(command.fichaPerfilId())` — retorna `Optional<EstadoFicha>`, usar `.orElseThrow(() -> new FichaPerfilNoEncontradaException(command.fichaPerfilId()))` (la ficha ya se verificó en paso 1, así que en la práctica siempre habrá valor; el `orElseThrow` es defensa en profundidad), (4) invocar `ficha.cambiarAsesorFicha(command.nuevoAsesorFichaId(), estadoActual)` (el aggregate valida POL-01, POL-05 y estado terminal), (5) `fichaPerfilOutputPort.guardar(ficha)`, (6) log con `log.info(FichasMessages.FichaPerfil.LOG_ASESOR_CAMBIADO, ficha.getId(), command.nuevoAsesorFichaId())`.
- **Dependencias:** `CambiarAsesorFichaInputPort`, `CambiarAsesorFichaCommand`, `FichaPerfilOutputPort`, `EstadoFichaPerfilQueryOutputPort`, `AsesorFichaQueryOutputPort`, `FichaPerfilNoEncontradaException`, `AsesorFichaNoEncontradoException`, `FichasMessages`, `@Slf4j`

### `CambiarAsesorFichaRequestDTO.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.fichaPerfil.command.adapter.in.web.dto`
- **Tipo:** `record` (RequestDTO)
- **Responsabilidad:** Input HTTP para cambiar asesor. Valida con Jakarta que `asesorFichaId` no sea nulo.
- **Features Java 21 aplicables:** `record` para inmutabilidad.
- **Campos:** `@NotNull(message = "El asesorFichaId es obligatorio") UUID asesorFichaId` (campo en español).
- **Métodos:**
    - `public CambiarAsesorFichaCommand toCommand(UUID fichaPerfilId)` — produce el `Command` combinando el path param `fichaPerfilId` con el body `asesorFichaId`.
- **Dependencias:** `jakarta.validation.constraints.NotNull`, `java.util.UUID`, `CambiarAsesorFichaCommand`

### `CambiarAsesorFichaInputAdapter.java` (Controller)
- **Paquete:** `com.arquisoft.fichas.infrastructure.fichaPerfil.command.adapter.in.web`
- **Tipo:** `@RestController`
- **Responsabilidad:** Endpoint REST `PATCH /fichas-perfil/{id}/asesor-ficha` que inyecta el `InputPort` y retorna `204 No Content`.
- **Anotaciones:** `@RestController`, `@RequestMapping("/fichas-perfil")`, `@RequiredArgsConstructor`, `@Slf4j`, ADR-011 (`@Tag`, `@SecurityRequirement`).
- **Plantilla extendida para Controllers (ADR-011):**
    - **`@Tag`:** `name = "Fichas Perfil"`, `description = "Gestión de fichas de perfil de proyectos de grado"`
    - **Endpoints documentados:**

      | Método del Controller | `@Operation(summary)` | Códigos `@ApiResponse` | `@SecurityRequirement` |
      |-----------------------|-----------------------|------------------------|------------------------|
      | `cambiarAsesor` | `"Cambiar asesor de ficha perfil"` | 204, 400, 401, 403, 422 | `bearerAuth` |

- **Métodos principales:**
    - `@PatchMapping("/{id}/asesor") @Operation(summary = "Cambiar asesor de ficha perfil") @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Asesor cambiado"), @ApiResponse(responseCode = "400", description = "Ficha o asesor no encontrado"), @ApiResponse(responseCode = "401", description = "No autenticado"), @ApiResponse(responseCode = "403", description = "Sin permisos"), @ApiResponse(responseCode = "422", description = "Invariante violada (mismo asesor o estado terminal)") }) @SecurityRequirement(name = "bearerAuth") @PreAuthorize("hasAuthority('fichas:ficha-perfil:update-asesor')") public ResponseEntity<Void> cambiarAsesor(@PathVariable UUID id, @Valid @RequestBody CambiarAsesorFichaRequestDTO request)` — invoca `inputPort.ejecutar(request.toCommand(id))` y retorna `ResponseEntity.noContent().build()`.
- **Dependencias:** `CambiarAsesorFichaInputPort`, `CambiarAsesorFichaRequestDTO`, `org.springframework.web.bind.annotation.*`, `jakarta.validation.Valid`, ADR-011 annotations

### `V1.6__agregar_indice_asesor_ficha_perfil.sql`
- **Paquete:** `fichas/infrastructure/src/main/resources/db/migration/fichas/`
- **Tipo:** Flyway
- **Responsabilidad:** Crear el índice faltante `idx_ficha_perfil_asesor` en `ficha_perfil(asesor_ficha_id)` para optimizar lookups por asesor. **No se agrega ninguna columna nueva** — el estado de la ficha se consulta en tiempo de ejecución vía `EstadoFichaPerfilQueryOutputPort`.
- **Contenido exacto:**

```sql
CREATE INDEX idx_ficha_perfil_asesor ON ficha_perfil(asesor_ficha_id);
```

### `FichasMessages.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.shared.message`
- **Tipo:** Catálogo
- **Responsabilidad:** Agregar constantes a la nested class `FichaPerfil` según inventario de la sección 6. Mantener el orden de las 5 secciones.
- **Fragmento a agregar (dentro de `public static final class FichaPerfil { ... }`):**

```java
// Campos
public static final String CAMPO_ASESOR_FICHA_ID = "asesorFichaId";
public static final String CAMPO_ESTADO_FICHA    = "estadoFicha";

// Límites
// (sin límites nuevos en esta HU)

// Códigos de error
public static final String ASESOR_FICHA_REQUERIDO = "ASESOR_FICHA_REQUERIDO";
public static final String MISMO_ASESOR           = "MISMO_ASESOR";
public static final String ESTADO_TERMINAL        = "ESTADO_TERMINAL";
public static final String FICHA_NO_ENCONTRADA    = "FICHA_NO_ENCONTRADA";
public static final String ASESOR_NO_ENCONTRADO   = "ASESOR_NO_ENCONTRADO";

// Mensajes de error
public static final String ASESOR_FICHA_REQUERIDO_MSG = "El asesorFichaId es obligatorio";
public static final String MISMO_ASESOR_MSG           = "El asesor nuevo no puede ser el mismo que el actual: %s";
public static final String ESTADO_TERMINAL_MSG        = "No se puede cambiar el asesor porque la ficha está en estado terminal: %s";
public static final String FICHA_NO_ENCONTRADA_MSG    = "La ficha de perfil con id %s no existe";
public static final String ASESOR_NO_ENCONTRADO_MSG   = "El asesor ficha con id %s no existe";

// Logs
public static final String LOG_ASESOR_CAMBIADO = "Asesor de ficha cambiado — fichaId={}, nuevoAsesorId={}";
```

---

## 8. Endpoints REST (si aplica)

### Estado del endpoint

- [X] **Endpoint NUEVO** — crear `CambiarAsesorFichaInputAdapter.java` desde cero.

### Contrato del endpoint

| Método | Ruta | Request Body / Params | Response | Código HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|----------------------|----------|-------------|----------------------|-------------------------------|
| PATCH | `/fichas-perfil/{id}/asesor-ficha` | `CambiarAsesorFichaRequestDTO` (body: `{"asesorFichaId": "UUID"}`) + `@PathVariable UUID id` | `Void` (sin body) | 204 No Content | `fichas:ficha-perfil:update-asesor` | `@Operation(summary="Cambiar asesor de ficha perfil")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses` (204, 400, 401, 403, 422) |

---

## 9. Seguridad y Autorización (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Roles realm que lo poseen | Endpoint(s) que lo requieren | Descripción funcional |
|---|---|---|---|
| `fichas:ficha-perfil:update-asesor` | `coordinador` | `PATCH /fichas-perfil/{id}/asesor-ficha` | Permite al Coordinador cambiar el asesor asignado a una ficha de perfil existente |

### Reglas de uso

1. **Formato del client role:** `fichas:ficha-perfil:update-asesor` (kebab-case, todo en minúsculas, palabras del recurso separadas por guiones).
2. **Roles realm en kebab-case:** `coordinador` (único autorizado para esta acción según Event Storming).
3. **Un client role, un rol realm** en este caso — solo `coordinador` puede cambiar asesor.
4. **Cada endpoint tiene exactamente un `@PreAuthorize("hasAuthority('...')")`** con un único client role.
5. **NO se usa `hasRole(...)`** — siempre `hasAuthority(...)`.

### Configuración requerida en Keycloak (instrucciones para equipo de seguridad)

1. En el cliente `arquisoft-api`: crear el client role `fichas:ficha-perfil:update-asesor`.
2. Asignar el client role al rol realm `coordinador`.
3. Verificar que los usuarios de prueba con rol `coordinador` reciban el client role en su JWT (`resource_access.arquisoft-api.roles`).

---

## 10. Eventos RabbitMQ (si aplica)

Eventos: ninguno.

Razón: CRUD interno sin consumidores conocidos ni casos de auditoría identificados (decisión confirmada por el usuario: "Esta HU NO emite eventos de dominio. No hay consumidores. No se agrega ningún evento.").

---

## 11. Migración de Base de Datos (si aplica)

- **Archivo:** `V1.6__agregar_indice_asesor_ficha_perfil.sql` (en `db/migration/fichas/`. Versión = V1.6, siguiente número tras V1.5 — verificado.)
- **Base de datos:** `fichas_perfil` (BD propia del contexto `fichas`).
- **DDL exacto:**

```sql
CREATE INDEX idx_ficha_perfil_asesor ON ficha_perfil(asesor_ficha_id);
```

- **Solo un índice:** el estado de la ficha se obtiene vía `EstadoFichaPerfilQueryOutputPort` en tiempo de ejecución — no requiere nueva columna en `ficha_perfil`.

---

## 12. Casos de Prueba Sugeridos

### Tests de Dominio (`FichaPerfilAggregate`)

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 1 | `debeCambiarAsesor_cuandoNuevoAsesorEsDiferenteYEstadoNoTerminal` | Aggregate con `asesorFichaId = UUID_A`, invocar `cambiarAsesorFicha(UUID_B, EstadoFicha.EN_CONSTRUCCION)` | No lanza excepción, `getAsesorFichaId()` retorna `UUID_B` |
| 2 | `debeLanzarDomainValidationException_cuandoNuevoAsesorEsIgualAlActual` | Aggregate con `asesorFichaId = UUID_A`, invocar `cambiarAsesorFicha(UUID_A, EstadoFicha.EN_REVISION)` | Lanza `DomainValidationException` con `fieldErrors[{field: "asesorFichaId", errorCode: "MISMO_ASESOR"}]` |
| 3 | `debeLanzarDomainValidationException_cuandoEstadoEsAprobada` | Invocar `cambiarAsesorFicha(UUID_B, EstadoFicha.APROBADA)` | Lanza `DomainValidationException` con `fieldErrors[{field: "estadoFicha", errorCode: "ESTADO_TERMINAL"}]` |
| 4 | `debeLanzarDomainValidationException_cuandoEstadoEsAprobadaConObservaciones` | Invocar `cambiarAsesorFicha(UUID_B, EstadoFicha.APROBADA_CON_OBSERVACIONES)` | Lanza `DomainValidationException` con `fieldErrors[{field: "estadoFicha", errorCode: "ESTADO_TERMINAL"}]` |
| 5 | `debeLanzarDomainValidationException_cuandoEstadoEsNoAprobada` | Invocar `cambiarAsesorFicha(UUID_B, EstadoFicha.NO_APROBADA)` | Lanza `DomainValidationException` con `fieldErrors[{field: "estadoFicha", errorCode: "ESTADO_TERMINAL"}]` |
| 6 | `debeLanzarDomainValidationException_cuandoNuevoAsesorIdEsNulo` | Invocar `cambiarAsesorFicha(null, EstadoFicha.EN_CONSTRUCCION)` | Lanza `DomainValidationException` con `fieldErrors[{field: "asesorFichaId", errorCode: "ASESOR_FICHA_REQUERIDO"}]` |

> **Tipo de use case:** Escritura — tests de ciclo de eventos **NO aplican** porque la HU no extiende `AggregateRoot` (confirmado). Los tests de dominio validan invariantes (mismo asesor, estado terminal, null checks).

### Tests de Application (`CambiarAsesorFichaUseCase`)

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 7  | `debeCambiarAsesor_cuandoDatosValidos` | Ficha existe, asesor nuevo existe, distinto del actual, estado no terminal | Invoca `fichaPerfilOutputPort.guardar(ficha)` exactamente una vez con el `asesorFichaId` actualizado. Log con `LOG_ASESOR_CAMBIADO` |
| 8  | `debeLanzarFichaPerfilNoEncontradaException_cuandoFichaNoExiste` | `fichaPerfilOutputPort.buscarPorId()` retorna `Optional.empty()` | Lanza `FichaPerfilNoEncontradaException` con mensaje parametrizado con el `fichaPerfilId` |
| 9  | `debeLanzarAsesorFichaNoEncontradoException_cuandoAsesorNoExiste` | Ficha existe, pero `asesorFichaQueryOutputPort.existsById()` retorna `false` | Lanza `AsesorFichaNoEncontradoException` con mensaje parametrizado con el `asesorFichaId` |
| 10 | `debePropagar DomainValidationException_cuandoMismoAsesor` | Ficha existe, asesor existe, estado no terminal, nuevo asesor igual al actual | Propaga `DomainValidationException` con `fieldErrors[{field: "asesorFichaId", errorCode: "MISMO_ASESOR"}]` sin capturarla |
| 11 | `debePropagar DomainValidationException_cuandoEstadoEsTerminal` | Ficha existe, asesor existe, distinto del actual, `estadoFichaPerfilQueryOutputPort.obtenerEstadoActual()` retorna `APROBADA` | Propaga `DomainValidationException` con `fieldErrors[{field: "estadoFicha", errorCode: "ESTADO_TERMINAL"}]` sin capturarla |

> **Nota sobre `EventPublisher`:** NO se testea `verify(eventPublisher).publish(...)` porque esta HU no inyecta `EventPublisher` (sin eventos según decisión del usuario).

### Tests de Infrastructure (`CambiarAsesorFichaInputAdapter`)

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 12 | `debeRetornar204_cuandoCambioExitoso` | Invocar `PATCH /fichas-perfil/{id}/asesor-ficha` con body válido, use case ejecuta sin excepción | HTTP 204 No Content, sin body |
| 13 | `debeRetornar400_cuandoAsesorFichaIdNuloEnBody` | Body con `{"asesorFichaId": null}` | HTTP 400 con mensaje de validación Jakarta |
| 14 | `debeRetornar400_cuandoFichaNoEncontrada` | Use case lanza `FichaPerfilNoEncontradaException` | HTTP 400 con `ErrorResponseDTO` que incluye el código `FICHA_NO_ENCONTRADA` |
| 15 | `debeRetornar400_cuandoAsesorNoEncontrado` | Use case lanza `AsesorFichaNoEncontradoException` | HTTP 400 con `ErrorResponseDTO` |
| 16 | `debeRetornar422_cuandoMismoAsesor` | Aggregate lanza `DomainValidationException` por POL-05 | HTTP 422 con `fieldErrors[{field: "asesorFichaId", errorCode: "MISMO_ASESOR"}]` |
| 17 | `debeRetornar422_cuandoEstadoTerminal` | Aggregate lanza `DomainValidationException` por estado terminal | HTTP 422 con `fieldErrors[{field: "estadoFicha", errorCode: "ESTADO_TERMINAL"}]` |
| 18 | `debeRetornar401_cuandoNoAutenticado` | Request sin header `Authorization` | HTTP 401 Unauthorized |
| 19 | `debeRetornar403_cuandoSinPermisos` | JWT válido sin el client role `fichas:ficha-perfil:update-asesor` | HTTP 403 Forbidden |

### Tests de Infrastructure — Persistencia (`FichaPerfilCommandOutputAdapter`)

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 20 | `debeGuardarFichaConNuevoAsesor_cuandoAggregateModificado` | Aggregate con `asesorFichaId` modificado tras llamar `cambiarAsesorFicha()` | `jpaRepository.save()` guarda la JpaEntity con el nuevo `asesorFichaId` |

### Tests de Migración Flyway

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 21 | `debeCrearIndiceAsesor_cuandoMigracionV16Ejecutada` | Ejecutar migración V1.6 | Índice `idx_ficha_perfil_asesor` existe en `ficha_perfil(asesor_ficha_id)` |

> **Nota final sobre cobertura:** 21 tests sugeridos. Objetivo: 75% líneas + 80% branches. Dominio (1-6): invariantes de `cambiarAsesorFicha()`. Application (7-11): flujo orquestal del use case. Infrastructure/controller (12-19): códigos HTTP y validaciones Jakarta. Infrastructure/persistencia (20): guarda el nuevo asesor. Flyway (21): índice. No hay tests de ciclo de eventos.

---

## 13. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente             | Estado        | Fecha | Notas |
|------------|--------------------|---------------|-------|-------|
| Desarrollo | @implementador     | ✅ Completado | 2026-07-15 | Build -x test: sin errores |
| Tests      | @tester            | ✅ Completado | 2026-07-15 | 18 tests generados (domain: 6, application: 5, infrastructure: 8). Cobertura: domain 94%, application 88%, infrastructure 88% — CUMPLE gate ≥75%. Build check: PASÓ. |
| Validación | @validator-analyze | ✅ Completado | 2026-07-15 | Score: 100/100 — APROBADO |
| Reporte    | @validator-report  | ✅ Completado | 2026-07-15 | ./.workspace/validator/validator-HU-164.md |
| Commit     | @commit            | ✅ Completado | 2026-07-15 | Hash: efd6737 |

---

## Fin del Plan
