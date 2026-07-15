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
| 1 | Coordinador invoca `PATCH /fichas-perfil/{id}/asesor` con un `asesorFichaId` válido, la ficha existe y el estado NO es terminal, y el nuevo asesor es distinto al actual | 204 No Content |
| 2 | El `asesorFichaId` es nulo o vacío | 400 Bad Request con mensaje de validación Jakarta |
| 3 | La ficha no existe | 400 Bad Request con mensaje `FichaPerfilNoEncontradaException` |
| 4 | El asesor no existe | 400 Bad Request con mensaje `AsesorFichaNoEncontradoException` |
| 5 | El nuevo `asesorFichaId` es idéntico al actual | 422 Unprocessable Content con mensaje de dominio (POL-05) |
| 6 | El estado de la ficha es terminal (`NO_APROBADA`, `APROBADA_CON_OBSERVACIONES`, `APROBADA`) | 422 Unprocessable Content con mensaje `EstadoFichaTerminalException` |

---

## 3. Reglas de Negocio

- **FichaPerfil-POL-01:** Asegurar que el `asesorFichaId` sea válido (UUID no nulo, tipo de dato correcto) — validado con Jakarta en DTO.
- **FichaPerfil-POL-05:** Asegurar que el Asesor nuevo no sea el mismo que tenía anteriormente — validado en el método `cambiarAsesorFicha()` del aggregate con excepción de dominio si `asesorFichaId.equals(this.asesorFichaId)`.
- **Restricción por estado terminal (documentada por usuario):** No permitir cambio de asesor cuando el estado actual de la ficha es `NO_APROBADA`, `APROBADA_CON_OBSERVACIONES` o `APROBADA`. El aggregate debe consultar su propio `EstadoFicha` actual (atributo del aggregate a añadir en esta HU) antes de aceptar el cambio. Si el estado es terminal, lanzar `EstadoFichaTerminalException` (dominio).

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `FichaPerfilAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** No — la HU no emite eventos. Es una clase plana con factories `crear`/`reconstruir`. El archivo ya existe; esta HU lo **modifica** para agregar el método `cambiarAsesorFicha()` y el atributo `estadoFicha`.
- **ID:** `UUID`

> **Coherencia obligatoria — verificada.** El archivo `FichaPerfilAggregate.java` fue abierto y leído: no extiende `AggregateRoot`. La sección "Eventos de Dominio que emite" declara "ninguno". Coherencia confirmada.

### Atributos por objeto de dominio (extraídos del modelo enriquecido)

#### `FichaPerfil` (Aggregate Root — archivo a modificar)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí (en `crear()`) | Identifica la ficha |
| `tituloProyecto` | `String` | 1-100 | Sí | Sí (por HU-033) | No | Limpiar espacios (inicio/fin) |
| `asesorFichaId` | `UUID` | — | Sí | **Sí (por esta HU)** | No | Referencia al asesor ficha (réplica local, FK en JPA) |
| `estadoFicha` | `EstadoFicha` (enum) | — | Sí | Sí (por HU-191 de evaluación) | No | Estado actual de la ficha — **atributo nuevo a agregar en esta HU**. El aggregate ya maneja lógica de `titulo` y `asesorFichaId`, pero no tiene `estadoFicha`. Este atributo es requerido para validar la restricción de estado terminal en `cambiarAsesorFicha()`. El `setter` privado toma un `EstadoFicha` y lo asigna sin validaciones adicionales — el estado inicial se asigna al crear la ficha (típicamente `EN_CONSTRUCCION`), y las transiciones las gestionan otras HUs (ej. HU-191). Para el factory `reconstruir()`, se recibe el `estadoFicha` desde la BD. Para el factory `crear()`, se asigna el estado inicial (valor a confirmar en código existente o documentación — si no está documentado, usar `EN_CONSTRUCCION` por defecto del enum). |

**Combinaciones únicas (Restricciones):**
- El `tituloProyecto` es único (ya implementado en Flyway `uq_ficha_perfil_titulo` y validado vía use case en HU-160).

### Traducción del modelo enriquecido a código

| Característica del modelo | Traducción en código |
|---|---|
| `asesorFichaId` modificable | Setter privado `setAsesorFichaId()` (ya existe) + método público `cambiarAsesorFicha(nuevoAsesorId)` (a agregar) que valida POL-05 (no igual al actual) y restricción de estado terminal (si `estadoFicha` es `NO_APROBADA`, `APROBADA_CON_OBSERVACIONES` o `APROBADA`, lanzar excepción de dominio). |
| `estadoFicha` obligatorio | Setter privado `setEstadoFicha(EstadoFicha estado, ValidationResult result)` (a agregar) valida con `DomainValidator.notNull`. Getter `getEstadoFicha()` (a agregar). Factory `crear()` asigna el estado inicial. Factory `reconstruir()` recibe el `estadoFicha` como parámetro adicional. |

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
| domain | `fichas/src/main/java/com/arquisoft/fichas/domain/fichaperfil/exception/MismoAsesorFichaException.java` | Exception del aggregate | Extiende `DomainException` (invariante POL-05 violada → 422). Mensaje parametrizado con el `UUID` del asesor repetido. |
| domain | `fichas/src/main/java/com/arquisoft/fichas/domain/fichaperfil/exception/EstadoFichaTerminalException.java` | Exception del aggregate | Extiende `DomainException` (restricción de estado terminal → 422). Mensaje indica que no se puede cambiar asesor porque el estado de la ficha es terminal (`{estado}`). |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/model/CambiarAsesorFichaCommand.java` | `record` | Intención de negocio. Campos: `UUID fichaPerfilId`, `UUID nuevoAsesorFichaId`. |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/port/in/CambiarAsesorFichaInputPort.java` | Interface (vacía) | Extiende `VoidInputPort<CambiarAsesorFichaCommand>` de `shared:domain` (la acción no retorna nada al cliente). |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/exception/FichaPerfilNoEncontradaException.java` | Exception del use case | Extiende `ApplicationException` (ficha no encontrada → 400). Mensaje parametrizado con el `UUID` de la ficha. Ubicación directa bajo `fichaPerfil/exception/` (no anidada bajo `command/` — la excepción pertenece a la entidad, no al slice CQRS). |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/exception/AsesorFichaNoEncontradoException.java` | Exception del use case | Extiende `ApplicationException` (asesor no encontrado → 400). Mensaje parametrizado con el `UUID` del asesor. |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/CambiarAsesorFichaUseCase.java` | UseCase | `@Component` que implementa el `CambiarAsesorFichaInputPort`. Patrón: `buscar ficha → validar existencia ficha → validar existencia asesor → invocar aggregate.cambiarAsesorFicha() → guardar`. **NO inyecta `EventPublisher`** (sin eventos). |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/in/web/dto/CambiarAsesorFichaRequestDTO.java` | `record` | `record` con anotaciones Jakarta: `@NotNull UUID asesorFichaId` (con mensaje de error custom). Método `toCommand(UUID fichaPerfilId)` que produce el `CambiarAsesorFichaCommand` (el `fichaPerfilId` proviene del `@PathVariable`). |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/in/web/CambiarAsesorFichaInputAdapter.java` | `@RestController` | Inyecta el `CambiarAsesorFichaInputPort`. Endpoint `PATCH /fichas-perfil/{id}/asesor` que retorna `ResponseEntity<Void>` con `204 No Content`. ADR-011: `@Tag`, `@Operation`, `@ApiResponses` (204, 400, 401, 403, 422), `@SecurityRequirement(name="bearerAuth")`. |
| infrastructure | `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.6__agregar_indice_asesor_y_columna_estado_ficha_perfil.sql` | Flyway | **Versión:** V1.6 (siguiente número tras V1.5 existente — verificado). **Acciones:** (1) `CREATE INDEX idx_ficha_perfil_asesor ON ficha_perfil(asesor_ficha_id);` — índice faltante según el MER. (2) `ALTER TABLE ficha_perfil ADD COLUMN estado_ficha_id VARCHAR(50) NOT NULL DEFAULT 'EN_CONSTRUCCION';` — columna nueva requerida por el aggregate para validar estado terminal. FK `CONSTRAINT fk_ficha_perfil_estado FOREIGN KEY (estado_ficha_id) REFERENCES estado_ficha(id);`. **Nota sobre el default:** permite que filas existentes adquieran un valor no-nulo sin requerir migración de datos manual. Si el proyecto prefiere no usar `DEFAULT` en Flyway, la migración debe incluir un `UPDATE` explícito tras el `ADD COLUMN` con `DEFAULT NULL CONSTRAINT` temporal, pero la solución con `DEFAULT` es más simple y segura — confirmar con el usuario si el estándar del proyecto acepta defaults de columna. |
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Catálogo | **MODIFICAR** — agregar en la nested class `FichaPerfil` (que ya existe) las constantes nuevas en el orden de las 5 secciones. Ver inventario en sección de "Catálogo de mensajes". |

### Catálogo de mensajes (`shared:message`) — fila obligatoria

Esta HU introduce mensajes, códigos y logs nuevos. El plan declara una fila MODIFICAR para `shared/message/FichasMessages.java`.

Inventario de constantes a agregar al catálogo en esta HU:

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.FichaPerfil.CAMPO_ESTADO_FICHA` | Campos | `String` | `"estadoFicha"` | `DomainValidator.notNull` en `FichaPerfilAggregate.setEstadoFicha()` |
| `FichasMessages.FichaPerfil.ESTADO_FICHA_REQUERIDO` | Códigos de error | `String` | `"ESTADO_FICHA_REQUERIDO"` | `setEstadoFicha()` validation (errorCode) |
| `FichasMessages.FichaPerfil.ESTADO_REQUERIDO_MSG` | Mensajes de error | `String` | `"El estado de la ficha es obligatorio"` | `setEstadoFicha()` validation (mensaje) |
| `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA` | Códigos de error | `String` | `"FICHA_NO_ENCONTRADA"` | `FichaPerfilNoEncontradaException` (errorCode) |
| `FichasMessages.FichaPerfil.FICHA_NO_ENCONTRADA_MSG` | Mensajes de error | `String` | `"La ficha de perfil con id %s no existe"` | `FichaPerfilNoEncontradaException` (mensaje, `.formatted(id)`) |
| `FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO` | Códigos de error | `String` | `"ASESOR_NO_ENCONTRADO"` | `AsesorFichaNoEncontradoException` (errorCode) |
| `FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO_MSG` | Mensajes de error | `String` | `"El asesor ficha con id %s no existe"` | `AsesorFichaNoEncontradoException` (mensaje, `.formatted(asesorId)`) |
| `FichasMessages.FichaPerfil.MISMO_ASESOR` | Códigos de error | `String` | `"MISMO_ASESOR"` | `MismoAsesorFichaException` (errorCode) |
| `FichasMessages.FichaPerfil.MISMO_ASESOR_MSG` | Mensajes de error | `String` | `"El asesor nuevo no puede ser el mismo que el actual: %s"` | `MismoAsesorFichaException` (mensaje, `.formatted(asesorId)`) |
| `FichasMessages.FichaPerfil.ESTADO_TERMINAL` | Códigos de error | `String` | `"ESTADO_TERMINAL"` | `EstadoFichaTerminalException` (errorCode) |
| `FichasMessages.FichaPerfil.ESTADO_TERMINAL_MSG` | Mensajes de error | `String` | `"No se puede cambiar el asesor porque la ficha está en estado terminal: %s"` | `EstadoFichaTerminalException` (mensaje, `.formatted(estado.getNombre())`) |
| `FichasMessages.FichaPerfil.LOG_ASESOR_CAMBIADO` | Logs | `String` | `"Asesor de ficha cambiado — fichaId={}, nuevoAsesorId={}"` | `log.info` en `CambiarAsesorFichaUseCase` |

### Archivos a MODIFICAR (si aplica)

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/src/main/java/com/arquisoft/fichas/domain/fichaperfil/aggregate/FichaPerfilAggregate.java` | **Agregar atributo `estadoFicha`:** `private EstadoFicha estadoFicha;` + setter privado `setEstadoFicha(EstadoFicha estado, ValidationResult result)` + getter `getEstadoFicha()`. **Modificar factories:** (1) `crear()` debe asignar el estado inicial (típicamente `EstadoFicha.EN_CONSTRUCCION`) invocando `setEstadoFicha()` con `ValidationResult`. (2) `reconstruir()` debe recibir un parámetro adicional `EstadoFicha estadoFicha` y asignarlo al atributo. **Agregar método de negocio:** `public void cambiarAsesorFicha(UUID nuevoAsesorFichaId)` que: (1) valida con `DomainValidator.notNull` el `nuevoAsesorFichaId`, (2) valida que `!nuevoAsesorFichaId.equals(this.asesorFichaId)` (POL-05), lanzando `MismoAsesorFichaException` si son iguales, (3) valida que `estadoFicha` NO sea `APROBADA`, `APROBADA_CON_OBSERVACIONES` ni `NO_APROBADA` (restricción de estado terminal), lanzando `EstadoFichaTerminalException` si lo es, (4) invoca el setter privado `setAsesorFichaId()` con `ValidationResult` para asignar el nuevo asesor. Todas las validaciones usan `ValidationResult` + `throwIfHasErrors()` al final del método. |
| `fichas/src/main/java/com/arquisoft/fichas/domain/fichaperfil/port/out/FichaPerfilOutputPort.java` | **Verificar si existe método `Optional<FichaPerfilAggregate> buscarPorId(UUID id);`.** Si no existe, agregarlo (el use case lo necesita para recuperar el aggregate antes de invocarlo). Si existe, no modificar. |
| `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/query/port/out/AsesorFichaQueryOutputPort.java` | **Verificar si existe método `boolean existsById(UUID id);`.** Si el archivo `AsesorFichaQueryOutputPort.java` no existe, **crearlo** (sigue la estructura en sección 6 — tabla "Archivos NUEVOS — caso de uso read"). Si existe pero no tiene `existsById()`, agregarlo (el use case lo necesita para validar que el nuevo asesor existe — lookup cross-aggregate, ubicación correcta según skill `arquisoft-context` §Caso 3). |
| `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/persistence/FichaPerfilMapper.java` | **Modificar métodos `toDomain()` y `toJpaEntity()` para mapear el campo `estadoFicha`:** (1) `toDomain()` (aka `reconstruir()`) debe leer `entity.getEstadoFicha().getId()` y convertirlo a `EstadoFicha.valueOf(...)` para pasarlo al factory `reconstruir()` del aggregate (que ahora recibe `estadoFicha` como parámetro adicional). (2) `toJpaEntity()` debe obtener `aggregate.getEstadoFicha().getId()` e inyectar el `estadoFichaJpaRepository.getReferenceById(id)` para asignar la referencia JPA (sin viaje a BD — patrón del proyecto para catálogos con PK semántica). El mapper inyecta `EstadoFichaJpaRepository` por constructor. |
| `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/persistence/FichaPerfilJpaEntity.java` | **Agregar campo `estadoFicha`:** `@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "estado_ficha_id", nullable = false) private EstadoFichaJpaEntity estadoFicha;` + getter + setter. **NO agregar `@Column(name = "...")` en el getter** — es `@JoinColumn` en la anotación del campo, no `@Column`. |
| `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/out/persistence/FichaPerfilCommandOutputAdapter.java` | **Sin cambios si ya implementa `buscarPorId()`.** Verificar que exista el método `Optional<FichaPerfilAggregate> buscarPorId(UUID id)` que delega al repositorio. Si no existe, agregarlo. Patrón: `return jpaRepository.findById(id).map(mapper::toDomain);`. |
| `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/query/adapter/out/persistence/AsesorFichaQueryOutputAdapter.java` | **Verificar/crear clase** que implementa `AsesorFichaQueryOutputPort`. Si no existe, crearla (patrón: `@Component` + `@RequiredArgsConstructor` + inyecta `AsesorFichaJpaRepository` + implementa `existsById(UUID)` delegando a `jpaRepository.existsById(id)`). Si existe, verificar que tenga el método `existsById()`. |
| `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/persistence/AsesorFichaJpaRepository.java` | **Verificar/crear interface** que extiende `JpaRepository<AsesorFichaJpaEntity, UUID>`. Si no existe, crearla. Si existe, no modificar (el método `existsById()` es heredado de `JpaRepository`). |
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | **Agregar constantes a la nested class `FichaPerfil`** según inventario de la tabla anterior. Mantener el orden de las 5 secciones: (1) Campos, (2) Límites, (3) Códigos de error, (4) Mensajes de error, (5) Logs. |

### Archivos de MENSAJERÍA RabbitMQ (si aplica)

No aplica — esta HU no emite eventos de dominio (decisión confirmada por el usuario).

---

## 7. Detalle por Archivo

### `MismoAsesorFichaException.java`
- **Paquete:** `com.arquisoft.fichas.domain.fichaperfil.exception`
- **Tipo:** Exception del aggregate
- **Responsabilidad:** Se lanza cuando el nuevo `asesorFichaId` es idéntico al actual (POL-05 violada). Extiende `DomainException` (422 Unprocessable Content).
- **Constructor:** Recibe `UUID asesorId` y construye el mensaje parametrizado usando `FichasMessages.FichaPerfil.MISMO_ASESOR_MSG.formatted(asesorId)` + código `FichasMessages.FichaPerfil.MISMO_ASESOR`.
- **Dependencias:** `com.arquisoft.shared.exception.DomainException`, `FichasMessages`, `java.util.UUID`

### `EstadoFichaTerminalException.java`
- **Paquete:** `com.arquisoft.fichas.domain.fichaperfil.exception`
- **Tipo:** Exception del aggregate
- **Responsabilidad:** Se lanza cuando se intenta cambiar asesor en una ficha con estado terminal (`NO_APROBADA`, `APROBADA_CON_OBSERVACIONES`, `APROBADA`).
- **Constructor:** Recibe `EstadoFicha estado` y construye el mensaje parametrizado usando `FichasMessages.FichaPerfil.ESTADO_TERMINAL_MSG.formatted(estado.getNombre())` + código `FichasMessages.FichaPerfil.ESTADO_TERMINAL`.
- **Dependencias:** `com.arquisoft.shared.exception.DomainException`, `FichasMessages`, `com.arquisoft.fichas.domain.estadoficha.EstadoFicha`

### `FichaPerfilAggregate.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.domain.fichaperfil.aggregate`
- **Tipo:** Aggregate Root
- **Responsabilidad:** Encapsula las invariantes de `FichaPerfil`. Esta HU añade el atributo `estadoFicha` (tipo `EstadoFicha`) y el método de negocio `cambiarAsesorFicha(UUID)`.
- **Features Java 21 aplicables:** `var` para `ValidationResult` en métodos de negocio.
- **Métodos principales:**
    - `public static FichaPerfilAggregate crear(String titulo, UUID asesorFichaId)` — **modificar** para asignar el estado inicial `EstadoFicha.EN_CONSTRUCCION` invocando `setEstadoFicha(EstadoFicha.EN_CONSTRUCCION, result)` tras los otros `set*`. El estado inicial se elige por convención del enum; si hay documentación que especifique otro, ajustar.
    - `public static FichaPerfilAggregate reconstruir(UUID id, String titulo, UUID asesorFichaId, EstadoFicha estadoFicha)` — **modificar** firma para recibir `estadoFicha` como cuarto parámetro y asignarlo al atributo `this.estadoFicha = estadoFicha` en el constructor privado.
    - `public void cambiarAsesorFicha(UUID nuevoAsesorFichaId)` — **agregar**. Valida: (1) `DomainValidator.notNull(nuevoAsesorFichaId, ...)`, (2) `!nuevoAsesorFichaId.equals(this.asesorFichaId)` (lanza `MismoAsesorFichaException` si son iguales), (3) estado no terminal (si `estadoFicha == EstadoFicha.NO_APROBADA || estadoFicha == EstadoFicha.APROBADA_CON_OBSERVACIONES || estadoFicha == EstadoFicha.APROBADA`, lanza `EstadoFichaTerminalException`), (4) invoca `setAsesorFichaId(nuevoAsesorFichaId, result)`. Al final: `result.throwIfHasErrors()`.
    - `private void setEstadoFicha(EstadoFicha estado, ValidationResult result)` — **agregar**. Valida con `DomainValidator.notNull(estado, FichasMessages.FichaPerfil.CAMPO_ESTADO_FICHA, FichasMessages.FichaPerfil.ESTADO_FICHA_REQUERIDO, result)`. Si válido, asigna `this.estadoFicha = estado`.
    - `public EstadoFicha getEstadoFicha()` — **agregar**. Retorna `estadoFicha`.
- **Dependencias:** `com.arquisoft.shared.validation.DomainValidator`, `ValidationResult`, `UtilUUID`, `UtilText`, `FichasMessages`, `EstadoFicha`, `MismoAsesorFichaException`, `EstadoFichaTerminalException`, `java.util.UUID`

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
    - `@Override public void ejecutar(CambiarAsesorFichaCommand command)` — implementa el `VoidInputPort`. Patrón: (1) buscar ficha con `fichaPerfilOutputPort.buscarPorId(command.fichaPerfilId())`, lanzar `FichaPerfilNoEncontradaException` si `Optional.isEmpty()`, (2) validar existencia asesor con `asesorFichaQueryOutputPort.existsById(command.nuevoAsesorFichaId())`, lanzar `AsesorFichaNoEncontradoException` si es `false`, (3) invocar `ficha.cambiarAsesorFicha(command.nuevoAsesorFichaId())` (el aggregate valida POL-05 y estado terminal), (4) `fichaPerfilOutputPort.guardar(ficha)`, (5) log con `log.info(FichasMessages.FichaPerfil.LOG_ASESOR_CAMBIADO, ficha.getId(), command.nuevoAsesorFichaId())`.
- **Dependencias:** `CambiarAsesorFichaInputPort`, `CambiarAsesorFichaCommand`, `FichaPerfilOutputPort`, `AsesorFichaQueryOutputPort`, `FichaPerfilNoEncontradaException`, `AsesorFichaNoEncontradoException`, `FichasMessages`, `@Slf4j`

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
- **Responsabilidad:** Endpoint REST `PATCH /fichas-perfil/{id}/asesor` que inyecta el `InputPort` y retorna `204 No Content`.
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

### `V1.6__agregar_indice_asesor_y_columna_estado_ficha_perfil.sql`
- **Paquete:** `fichas/infrastructure/src/main/resources/db/migration/fichas/`
- **Tipo:** Flyway
- **Responsabilidad:** (1) Crear índice `idx_ficha_perfil_asesor` en `ficha_perfil(asesor_ficha_id)` para optimizar lookups por asesor. (2) Agregar columna `estado_ficha_id VARCHAR(50) NOT NULL DEFAULT 'EN_CONSTRUCCION'` a `ficha_perfil` con FK a `estado_ficha(id)`.
- **Contenido exacto:**

```sql
-- Índice faltante para asesor_ficha_id (según MER)
CREATE INDEX idx_ficha_perfil_asesor ON ficha_perfil(asesor_ficha_id);

-- Columna nueva para el estado actual de la ficha
ALTER TABLE ficha_perfil
ADD COLUMN estado_ficha_id VARCHAR(50) NOT NULL DEFAULT 'EN_CONSTRUCCION';

-- FK al catálogo estado_ficha
ALTER TABLE ficha_perfil
ADD CONSTRAINT fk_ficha_perfil_estado
FOREIGN KEY (estado_ficha_id) REFERENCES estado_ficha(id);
```

- **Nota sobre el `DEFAULT 'EN_CONSTRUCCION'`:** Permite que las filas existentes adquieran automáticamente un valor no-nulo. Si el proyecto prefiere evitar defaults de columna, reemplazar con:

```sql
ALTER TABLE ficha_perfil ADD COLUMN estado_ficha_id VARCHAR(50);
UPDATE ficha_perfil SET estado_ficha_id = 'EN_CONSTRUCCION' WHERE estado_ficha_id IS NULL;
ALTER TABLE ficha_perfil ALTER COLUMN estado_ficha_id SET NOT NULL;
ALTER TABLE ficha_perfil ADD CONSTRAINT fk_ficha_perfil_estado FOREIGN KEY (estado_ficha_id) REFERENCES estado_ficha(id);
```

Confirmar con el usuario si el estándar del proyecto acepta `DEFAULT` en `ADD COLUMN` — la primera versión es más simple.

### `FichasMessages.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.shared.message`
- **Tipo:** Catálogo
- **Responsabilidad:** Agregar constantes a la nested class `FichaPerfil` según inventario de la sección 6. Mantener el orden de las 5 secciones.
- **Fragmento a agregar (dentro de `public static final class FichaPerfil { ... }`):**

```java
// Campos
public static final String CAMPO_ESTADO_FICHA = "estadoFicha";

// Límites
// (sin límites nuevos en esta HU)

// Códigos de error
public static final String ESTADO_FICHA_REQUERIDO = "ESTADO_FICHA_REQUERIDO";
public static final String FICHA_NO_ENCONTRADA = "FICHA_NO_ENCONTRADA";
public static final String ASESOR_NO_ENCONTRADO = "ASESOR_NO_ENCONTRADO";
public static final String MISMO_ASESOR = "MISMO_ASESOR";
public static final String ESTADO_TERMINAL = "ESTADO_TERMINAL";

// Mensajes de error
public static final String ESTADO_REQUERIDO_MSG = "El estado de la ficha es obligatorio";
public static final String FICHA_NO_ENCONTRADA_MSG = "La ficha de perfil con id %s no existe";
public static final String ASESOR_NO_ENCONTRADO_MSG = "El asesor ficha con id %s no existe";
public static final String MISMO_ASESOR_MSG = "El asesor nuevo no puede ser el mismo que el actual: %s";
public static final String ESTADO_TERMINAL_MSG = "No se puede cambiar el asesor porque la ficha está en estado terminal: %s";

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
| PATCH | `/fichas-perfil/{id}/asesor` | `CambiarAsesorFichaRequestDTO` (body: `{"asesorFichaId": "UUID"}`) + `@PathVariable UUID id` | `Void` (sin body) | 204 No Content | `fichas:ficha-perfil:update-asesor` | `@Operation(summary="Cambiar asesor de ficha perfil")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses` (204, 400, 401, 403, 422) |

---

## 9. Seguridad y Autorización (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Roles realm que lo poseen | Endpoint(s) que lo requieren | Descripción funcional |
|---|---|---|---|
| `fichas:ficha-perfil:update-asesor` | `coordinador` | `PATCH /fichas-perfil/{id}/asesor` | Permite al Coordinador cambiar el asesor asignado a una ficha de perfil existente |

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

- **Archivo:** `V1.6__agregar_indice_asesor_y_columna_estado_ficha_perfil.sql` (en `db/migration/fichas/`. Versión = V1.6, siguiente número tras V1.5 — verificado mediante `ls` del directorio. Migraciones ya aplicadas: V1.0 a V1.5.)
- **Base de datos:** `fichas_perfil` (BD propia del contexto `fichas` según skill `arquisoft-context`).
- **Sin schemas:** las tablas se crean sin prefijo (ej. `ALTER TABLE ficha_perfil` sin `fichas_perfil.ficha_perfil`).
- **DDL exacto:**
  - **Índice:** `CREATE INDEX idx_ficha_perfil_asesor ON ficha_perfil(asesor_ficha_id);` — índice faltante según el MER (confirmado con `gh api` del SQL del MER: el índice aparece en el DDL canónico pero no en V1.0 del proyecto).
  - **Columna nueva:** `ALTER TABLE ficha_perfil ADD COLUMN estado_ficha_id VARCHAR(50) NOT NULL DEFAULT 'EN_CONSTRUCCION';` — columna requerida por el aggregate para validar estado terminal. El default `'EN_CONSTRUCCION'` permite que filas existentes adquieran un valor no-nulo automáticamente. Si el estándar del proyecto no acepta defaults en `ADD COLUMN`, reemplazar con secuencia `ADD COLUMN` (nullable) → `UPDATE` → `ALTER COLUMN SET NOT NULL` (ver detalle en sección 7).
  - **FK:** `ALTER TABLE ficha_perfil ADD CONSTRAINT fk_ficha_perfil_estado FOREIGN KEY (estado_ficha_id) REFERENCES estado_ficha(id);` — FK al catálogo `estado_ficha` (PK `VARCHAR(50)`, poblado con las constantes del enum `EstadoFicha`).
- **Tipos de dato:** `VARCHAR(50)` para `estado_ficha_id` (PK semántica según ADR-012 — el catálogo `estado_ficha` usa PK `VARCHAR`, no `UUID`).
- **Restricciones:** FK `fk_ficha_perfil_estado` referencia al catálogo. Índice `idx_ficha_perfil_asesor` acelera lookups por asesor (queries futuras como "listar fichas de un asesor").

**Nota sobre dependencias entre schemas:** el contexto `fichas` depende del contexto `usuarios` (según `mer/01_base_datos_y_esquemas.sql`). La tabla `asesor_ficha` referencia a `usuarios` — esa FK ya existe desde V1.0. Esta migración NO añade FKs cruzadas nuevas — solo índice y columna intra-contexto.

---

## 12. Casos de Prueba Sugeridos

### Tests de Dominio (`FichaPerfilAggregate`)

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 1 | `debeCrearFichaConEstadoInicial_cuandoDatosValidos` | Invocar `crear()` con datos válidos | Retorna aggregate con `estadoFicha == EstadoFicha.EN_CONSTRUCCION` (estado inicial), sin lanzar excepciones |
| 2 | `debeReconstruirFichaConEstado_cuandoDatosValidos` | Invocar `reconstruir()` con `estadoFicha = EstadoFicha.APROBADA` | Retorna aggregate con `getEstadoFicha() == EstadoFicha.APROBADA`, sin validaciones (dato confiable desde BD) |
| 3 | `debeLanzarDomainValidationException_cuandoEstadoFichaNuloEnCrear` | Invocar `crear()` alterando el código para pasar `estadoFicha = null` en `setEstadoFicha()` (test interno del setter, no del factory público — el factory pasa `EN_CONSTRUCCION` siempre) | Lanza `DomainValidationException` con campo `estadoFicha` + código `ESTADO_FICHA_REQUERIDO` |
| 4 | `debeCambiarAsesor_cuandoNuevoAsesorEsDiferenteYEstadoNoTerminal` | Crear aggregate con `asesorFichaId = UUID.randomUUID()`, `estadoFicha = EstadoFicha.EN_CONSTRUCCION`, invocar `cambiarAsesorFicha(otroUUID)` | No lanza excepción, `getAsesorFichaId()` retorna el nuevo UUID |
| 5 | `debeLanzarMismoAsesorFichaException_cuandoNuevoAsesorEsIgualAlActual` | Aggregate con `asesorFichaId = UUID_A`, invocar `cambiarAsesorFicha(UUID_A)` | Lanza `MismoAsesorFichaException` con mensaje parametrizado con `UUID_A` |
| 6 | `debeLanzarEstadoFichaTerminalException_cuandoEstadoEsAprobada` | Aggregate con `estadoFicha = EstadoFicha.APROBADA`, invocar `cambiarAsesorFicha(UUID)` | Lanza `EstadoFichaTerminalException` con mensaje que incluye "APROBADA" |
| 7 | `debeLanzarEstadoFichaTerminalException_cuandoEstadoEsAprobadaConObservaciones` | Aggregate con `estadoFicha = EstadoFicha.APROBADA_CON_OBSERVACIONES`, invocar `cambiarAsesorFicha(UUID)` | Lanza `EstadoFichaTerminalException` |
| 8 | `debeLanzarEstadoFichaTerminalException_cuandoEstadoEsNoAprobada` | Aggregate con `estadoFicha = EstadoFicha.NO_APROBADA`, invocar `cambiarAsesorFicha(UUID)` | Lanza `EstadoFichaTerminalException` |
| 9 | `debeLanzarDomainValidationException_cuandoNuevoAsesorIdEsNulo` | Invocar `cambiarAsesorFicha(null)` | Lanza `DomainValidationException` con campo `asesorFichaId` + código correspondiente del catálogo |

> **Tipo de use case:** Escritura — tests de ciclo de eventos **NO aplican** porque la HU no extiende `AggregateRoot` (confirmado). Los tests de dominio validan invariantes (mismo asesor, estado terminal, null checks).

### Tests de Application (`CambiarAsesorFichaUseCase`)

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 10 | `debeCambiarAsesor_cuandoDatosValidos` | Ficha existe, asesor nuevo existe, distinto del actual, estado no terminal | Invoca `outputPort.guardar(ficha)` exactamente una vez con aggregate modificado (asesor nuevo). Log con `LOG_ASESOR_CAMBIADO` |
| 11 | `debeLanzarFichaPerfilNoEncontradaException_cuandoFichaNoExiste` | `fichaPerfilOutputPort.buscarPorId()` retorna `Optional.empty()` | Lanza `FichaPerfilNoEncontradaException` con mensaje parametrizado con el `fichaPerfilId` |
| 12 | `debeLanzarAsesorFichaNoEncontradoException_cuandoAsesorNoExiste` | Ficha existe, pero `asesorFichaQueryOutputPort.existsById()` retorna `false` | Lanza `AsesorFichaNoEncontradoException` con mensaje parametrizado con el `asesorFichaId` |
| 13 | `debePropagagrarMismoAsesorFichaException_cuandoAggregateValidaIgualdad` | Ficha existe, asesor existe, pero aggregate lanza `MismoAsesorFichaException` | Propaga la excepción sin capturarla (el `GlobalAppExceptionHandler` la convierte a 422) |
| 14 | `debePropagagrarEstadoFichaTerminalException_cuandoAggregateValidaEstado` | Ficha existe con estado terminal, asesor existe y es distinto | Propaga `EstadoFichaTerminalException` sin capturarla |

> **Nota sobre `EventPublisher`:** NO se testea `verify(eventPublisher).publish(...)` porque esta HU no inyecta `EventPublisher` (sin eventos según decisión del usuario).

### Tests de Infrastructure (`CambiarAsesorFichaInputAdapter`)

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 15 | `debeRetornar204_cuandoCambioExitoso` | Invocar `PATCH /fichas-perfil/{id}/asesor` con body válido, use case ejecuta sin excepción | HTTP 204 No Content, sin body |
| 16 | `debeRetornar400_cuandoAsesorFichaIdNuloEnBody` | Body con `{"asesorFichaId": null}` | HTTP 400 con mensaje de validación Jakarta |
| 17 | `debeRetornar400_cuandoFichaNoEncontrada` | Use case lanza `FichaPerfilNoEncontradaException` | HTTP 400 con `ErrorResponseDTO` que incluye el código `FICHA_NO_ENCONTRADA` |
| 18 | `debeRetornar400_cuandoAsesorNoEncontrado` | Use case lanza `AsesorFichaNoEncontradoException` | HTTP 400 con `ErrorResponseDTO` |
| 19 | `debeRetornar422_cuandoMismoAsesor` | Use case propaga `MismoAsesorFichaException` | HTTP 422 con `ErrorResponseDTO` que incluye el código `MISMO_ASESOR` |
| 20 | `debeRetornar422_cuandoEstadoTerminal` | Use case propaga `EstadoFichaTerminalException` | HTTP 422 con `ErrorResponseDTO` |
| 21 | `debeRetornar401_cuandoNoAutenticado` | Request sin header `Authorization` | HTTP 401 Unauthorized |
| 22 | `debeRetornar403_cuandoSinPermisos` | JWT válido sin el client role `fichas:ficha-perfil:update-asesor` | HTTP 403 Forbidden |

### Tests de Infrastructure — Persistencia (`FichaPerfilCommandOutputAdapter` / `FichaPerfilMapper`)

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 23 | `debeGuardarFichaConNuevoAsesor_cuandoAggregateModificado` | Aggregate con `asesorFichaId` modificado | `jpaRepository.save()` guarda la JpaEntity con `asesorFichaId` nuevo |
| 24 | `debeMapearEstadoFichaAAggregate_cuandoReconstruir` | JpaEntity con `estadoFicha.id = "APROBADA"` | `mapper.toDomain()` retorna aggregate con `getEstadoFicha() == EstadoFicha.APROBADA` |
| 25 | `debeMapearEstadoFichaAJpaEntity_cuandoGuardar` | Aggregate con `estadoFicha = EstadoFicha.EN_CONSTRUCCION` | `mapper.toJpaEntity()` retorna JpaEntity con `estadoFicha.id == "EN_CONSTRUCCION"` (referencia cargada vía `getReferenceById()`, sin viaje a BD) |

### Tests de Migración Flyway

| # | Nombre del test | Escenario | Resultado esperado |
|---|-----------------|-----------|-------------------|
| 26 | `debeCrearIndiceAsesor_cuandoMigracionV16Ejecutada` | Ejecutar migración V1.6 | Índice `idx_ficha_perfil_asesor` existe en `ficha_perfil(asesor_ficha_id)` |
| 27 | `debeAgregarColumnaEstadoFicha_cuandoMigracionV16Ejecutada` | Ejecutar migración V1.6 | Columna `estado_ficha_id` existe en `ficha_perfil`, tipo `VARCHAR(50)`, NOT NULL, FK a `estado_ficha(id)` |
| 28 | `debeAsignarEstadoInicialAFilasExistentes_cuandoMigracionV16Ejecutada` | Filas en `ficha_perfil` previas a la migración | Todas tienen `estado_ficha_id = 'EN_CONSTRUCCION'` tras la migración (asignado por el `DEFAULT`) |

> **Nota final sobre cobertura:** 28 tests sugeridos. Objetivo: 75% líneas + 80% branches. Los tests de dominio (1-9) cubren las validaciones nuevas del aggregate. Los tests de application (10-14) cubren el flujo orquestal del use case. Los tests de infrastructure (15-25) cubren controller + persistencia. Los tests de Flyway (26-28) validan DDL. No hay tests de ciclo de eventos (la HU no extiende `AggregateRoot` ni emite eventos).

---

## Fin del Plan
