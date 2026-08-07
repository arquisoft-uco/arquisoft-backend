# PLAN: HU-208 — Registrar Nueva información Ficha Perfil

## Metadata
- **ID Historia:** HU-208
- **Bounded Context:** `fichas`
- **Tipo de Use Case:** Escritura
- **¿Usa AggregateRoot?:** NO — esta HU NO emite eventos (es CRUD interno sin consumidores conocidos ni auditoría). La entidad `FichaPerfilAggregate` ya existe y NO extiende `AggregateRoot` (es una clase plana con factories `crear`/`reconstruir`).
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-06-09
- **Rama sugerida:** `feature/HU-208-registrar-ficha-perfil`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md` — HU-208
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md` — comando "Registrar Nueva información Ficha Perfil"
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md` — objeto FichaPerfil y AsesorFicha
    - `mer/03_tablas_fichas_perfil.sql` — DDL completo del contexto fichas
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** COMPLETAR HU parcialmente implementada. El endpoint ya existe en `RegistrarFichaPerfilInputAdapter.java` pero el authority está incorrecto (`ficha:ficha:create` debe ser `fichas:ficha-perfil:create`). Las tablas ya existen pero hay que validarlas. No emite eventos de dominio.

---

## 1. Resumen Funcional

Esta HU permite al **Coordinador** o al **Asesor Ficha** registrar una nueva ficha de perfil de proyecto de grado. Una ficha de perfil contiene el título del proyecto y el asesor asignado. El título debe ser único en el sistema, y el asesor debe existir previamente. Esta HU NO cubre consultas, modificaciones ni flujos de evaluación — solo la creación inicial. NO emite eventos de dominio porque es CRUD interno sin consumidores conocidos ni casos de auditoría identificados.

**Estado actual:** hay código parcial implementado. El endpoint REST `POST /fichas-perfil` ya existe, pero el authority en el `@PreAuthorize` es incorrecto. El aggregate, use case, puertos, DTOs y persistencia ya están creados. Las tablas `ficha_perfil` y `asesor_ficha` ya existen en la migración Flyway `V1.0__crear_tablas_fichas_perfil.sql`. Esta HU COMPLETA la implementación corrigiendo el authority y validando que el código existente cumpla con las convenciones del proyecto.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | Coordinador o Asesor Ficha registra ficha con título y asesor válidos | Sistema retorna UUID con `201 Created` |
| 2 | Usuario intenta registrar ficha con título duplicado | Sistema rechaza con `400 Bad Request` + mensaje "El título ya existe: {titulo}" |
| 3 | Usuario intenta registrar ficha con asesor inexistente | Sistema rechaza con `400 Bad Request` + mensaje "Asesor Ficha no encontrado: {id}" |
| 4 | Usuario sin rol Coordinador ni Asesor Ficha intenta registrar | Sistema rechaza con `403 Forbidden` |
| 5 | Usuario no autenticado intenta registrar | Sistema rechaza con `401 Unauthorized` |
| 6 | Usuario envía datos inválidos (título vacío, sin asesor) | Sistema rechaza con `422 Unprocessable Content` + `fieldErrors` detallando qué campos fallan |

---

## 3. Reglas de Negocio

Extraídas del Event Storming — políticas del comando "Registrar Nueva información Ficha Perfil":

- **POL-01:** Asegurar que los datos requeridos para llevar a cabo la acción sean válidos a nivel de tipo de dato, longitud, obligatoriedad, formato y rango.
- **POL-02:** Asegurar que el nombre de una Ficha Perfil no estén repetidos.
- **POL-03:** Asegurar que exista un asesor ficha perfil para una ficha de perfil a crear.

**Traducción a código:**
- POL-01 → validaciones Jakarta (`@NotBlank`, `@Size`) en `RegistrarFichaPerfilRequestDTO` + Notification Pattern en el constructor de `FichaPerfilAggregate` con `DomainValidator`.
- POL-02 → validación de unicidad en `RegistrarFichaPerfilUseCase` consultando el `FichaPerfilOutputPort.existsByTituloProyecto(...)` antes de persistir (invariante sobre el propio aggregate).
- POL-03 → validación de existencia del asesor en `RegistrarFichaPerfilUseCase` consultando el **`AsesorFichaQueryOutputPort.existsById(...)`** antes de crear el aggregate. `AsesorFicha` es una **vista materializada** en el contexto `fichas` (origen futuro: contexto `usuarios`), por lo que el lookup va contra su lado query — no contra `FichaPerfilOutputPort`.

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `FichaPerfilAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** NO — la HU no emite eventos (CRUD interno sin consumidores conocidos ni auditoría identificada). La entidad raíz es una clase plana con factories `crear`/`reconstruir` sin maquinaria de eventos.
- **ID:** `UUID` (autogenerado en `crear(...)`)

### Atributos por objeto de dominio (extraídos del modelo enriquecido)

#### `FichaPerfil` (Entidad raíz)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | `UUID.randomUUID()` en `crear(...)` |
| `tituloProyecto` | `String` | 1-100 | Sí | No | No | Limpiar espacios (inicio + fin), validar en Notification Pattern |
| `asesorFichaId` | `UUID` | — | Sí | No | No | Referencia a `AsesorFicha` |

**Combinaciones únicas (Restricciones):**
- **Título de Ficha Perfil único:** el `tituloProyecto` nunca se repite → traducción: `UNIQUE` constraint en Flyway (`uq_ficha_perfil_titulo`), validación de unicidad previa en use case consultando `FichaPerfilOutputPort.existsByTituloProyecto(String)`.

#### `AsesorFicha` (Vista materializada del contexto `usuarios` — no implementado)

> **Nota:** `AsesorFicha` es una **vista materializada** dentro del contexto `fichas`. La entidad autoritativa vive en el contexto `usuarios` (no implementado en esta versión — se poblará manualmente en BD). En el código se modela con su propio aggregate plano (sin `AggregateRoot`), puerto write futuro (`AsesorFichaOutputPort`) cuando llegue el consumer AMQP del evento `UsuarioCreado`, y puerto query (`AsesorFichaQueryOutputPort`) que ya usamos para lookup FK desde este use case.

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | No | Recibido desde el contexto `usuarios` |
| `identificador` | `String` | 4-30 | Sí | No | No | Limpiar espacios |
| `nombre` | `String` | 2-50 | Sí | No | No | Limpiar espacios |
| `email` | `String` | 6-50 | Sí | No | No | Formato email válido, limpiar espacios |

**Combinaciones únicas:** omitidas — las garantiza el contexto `usuarios`. Esta réplica solo verifica existencia del `id`.

### Traducción del modelo enriquecido a código

| Característica del modelo | Traducción en código |
|---|---|
| Longitud 1-100 (`tituloProyecto`) | `@Size(min=1, max=100)` en DTO + `@Column(length=100)` en JPA + `VARCHAR(100)` en Flyway + `DomainValidator.maxLength` en constructor |
| Obligatorio | `@NotBlank` en DTO + `@Column(nullable=false)` en JPA + `NOT NULL` en Flyway + `DomainValidator.notBlank` o `notNull` en constructor |
| No modificable | NO se genera setter ni método `cambiar{Atributo}()` en el aggregate |
| Autogenerado (UUID `id`) | `UUID.randomUUID()` dentro de `crear(...)` |
| Limpiar espacios | `.trim()` en el factory `crear(...)` antes de validar |
| Combinación única (`tituloProyecto`) | `UNIQUE` constraint en Flyway (`uq_ficha_perfil_titulo`) + validación de unicidad en use case |

### Eventos de Dominio que emite

**Eventos: ninguno.**

**Razón:** CRUD interno sin consumidores conocidos ni casos de auditoría identificados. El Event Storming del comando "Registrar Nueva información Ficha Perfil" lista un evento "Nueva Ficha Perfil Asignada", pero según las respuestas del usuario (pregunta 5: NO emite eventos), esta HU NO lo publica. El evento puede aparecer en una HU futura si se identifica un consumidor o caso de auditoría.

**Implicaciones:**
- La entidad raíz `FichaPerfilAggregate` **NO extiende `AggregateRoot`** — es una clase plana con factories `crear`/`reconstruir`.
- El factory `crear(...)` **NO acumula eventos** (no existe `publicarEvento`).
- El use case **NO inyecta `EventPublisher`**, no hay drenado de eventos.
- **No se crean archivos en `domain/fichaperfil/event/`.**

---

## 5. Integraciones Externas

> Esta HU NO requiere integraciones externas más allá de PostgreSQL. No hay Keycloak API, SMTP, MinIO ni otros sistemas.

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

| Ruta completa | Tipo | Responsabilidad |
|---|---|---|
| `fichas/application/src/main/java/com/arquisoft/fichas/application/asesorficha/query/port/out/AsesorFichaQueryOutputPort.java` | Interface (query side de la vista materializada) | Expone `boolean existsById(UUID id)` para lookup FK desde use cases write de otros aggregates. |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/asesorficha/query/adapter/out/persistence/AsesorFichaQueryOutputAdapter.java` | `@Component` (implementa el puerto) | Delega a `AsesorFichaJpaRepository.existsById(...)`. |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/command/adapter/in/web/RegistrarFichaPerfilInputAdapter.java` | **Corregir `@PreAuthorize`:** cambiar `hasAuthority('ficha:ficha:create')` por `hasAuthority('fichas:ficha-perfil:create')`. El authority actual está mal — debe ser `{contexto}:{recurso-kebab}:{accion}` en kebab-case. |
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/aggregate/FichaPerfilAggregate.java` | **Validar que NO extienda `AggregateRoot`** (la HU no emite eventos). Verificar que `crear(...)` use Notification Pattern con `DomainValidator` para validar `tituloProyecto` y `asesorFichaId`. Verificar que `reconstruir(...)` reconstruya sin validar. |
| `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/command/RegistrarFichaPerfilUseCase.java` | **Validar flujo completo:** (1) verificar que el asesor exista consultando `asesorFichaQueryOutputPort.existsById(asesorId)`, (2) verificar unicidad del título consultando `fichaPerfilOutputPort.existsByTituloProyecto(titulo)`, (3) crear aggregate con `FichaPerfilAggregate.crear(...)`, (4) persistir con `fichaPerfilOutputPort.guardar(...)`, (5) retornar UUID (NO hay drenado de eventos porque la HU no emite). Inyecta DOS puertos: `FichaPerfilOutputPort` y `AsesorFichaQueryOutputPort`. |
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/port/out/FichaPerfilOutputPort.java` | **Solo operaciones sobre el propio aggregate:** `guardar`, `buscarPorId`, `existsByTituloProyecto`. **NO** declarar `existsAsesorById` aquí (lookup de otro aggregate → puerto separado). |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/command/adapter/out/persistence/FichaPerfilCommandOutputAdapter.java` | **Implementar solo los métodos del puerto propio.** NO inyectar `AsesorFichaJpaRepository`. La validación FK del asesor la hace `AsesorFichaQueryOutputAdapter` (puerto separado). |
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | **Añadir nested class `FichaPerfil` con constantes** (si no existe): campos (`CAMPO_TITULO`, `CAMPO_ASESOR`), límites (`TITULO_MAX=100`), códigos de error (`FICHA_TITULO_DUPLICADO`, `ASESOR_NO_ENCONTRADO`, `FICHA_TITULO_REQUERIDO`, `ASESOR_REQUERIDO`), mensajes parametrizados (`TITULO_DUPLICADO = "El título ya existe: %s"`, `ASESOR_NO_ENCONTRADO_MSG = "Asesor Ficha no encontrado: %s"`), logs (`LOG_REGISTRADA = "Ficha de perfil registrada — id={}"`). Ver inventario completo en la sub-sección siguiente. |

**Sub-sección obligatoria: Catálogo de mensajes (`shared:message`)**

Esta HU introduce textos, códigos y límites nuevos. El plan declara modificación de `FichasMessages.java` con la nested class `FichaPerfil` y las siguientes constantes (agrupadas por las 5 secciones estándar):

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.FichaPerfil.CAMPO_TITULO` | Campos | `String` | `"tituloProyecto"` | `DomainValidator.notBlank` en `FichaPerfilAggregate` |
| `FichasMessages.FichaPerfil.CAMPO_ASESOR` | Campos | `String` | `"asesorFichaId"` | `DomainValidator.notNull` en `FichaPerfilAggregate` |
| `FichasMessages.FichaPerfil.TITULO_MAX` | Límites | `int` | `100` | `DomainValidator.maxLength` en `FichaPerfilAggregate` |
| `FichasMessages.FichaPerfil.FICHA_TITULO_DUPLICADO` | Códigos de error | `String` | `"FICHA_TITULO_DUPLICADO"` | `FichaTituloDuplicadoException` (errorCode) |
| `FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO` | Códigos de error | `String` | `"ASESOR_NO_ENCONTRADO"` | `AsesorFichaNoEncontradoException` (errorCode) |
| `FichasMessages.FichaPerfil.FICHA_TITULO_REQUERIDO` | Códigos de error | `String` | `"FICHA_TITULO_REQUERIDO"` | `DomainValidator.notBlank` (ValidationResult.addError) |
| `FichasMessages.FichaPerfil.ASESOR_REQUERIDO` | Códigos de error | `String` | `"ASESOR_REQUERIDO"` | `DomainValidator.notNull` (ValidationResult.addError) |
| `FichasMessages.FichaPerfil.TITULO_DUPLICADO` | Mensajes de error | `String` | `"El título ya existe: %s"` | `FichaTituloDuplicadoException` (mensaje, `.formatted(titulo)`) |
| `FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO_MSG` | Mensajes de error | `String` | `"Asesor Ficha no encontrado: %s"` | `AsesorFichaNoEncontradoException` (mensaje, `.formatted(id)`) |
| `FichasMessages.FichaPerfil.TITULO_REQUERIDO_MSG` | Mensajes de error | `String` | `"El título del proyecto es obligatorio"` | `DomainValidator.notBlank` (mensaje del field error) |
| `FichasMessages.FichaPerfil.ASESOR_REQUERIDO_MSG` | Mensajes de error | `String` | `"El asesor ficha es obligatorio"` | `DomainValidator.notNull` (mensaje del field error) |
| `FichasMessages.FichaPerfil.LOG_REGISTRADA` | Logs | `String` | `"Ficha de perfil registrada — id={}"` | `log.info` en `RegistrarFichaPerfilUseCase` |

> **Archivo a modificar:** `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` (MODIFICAR, no crear — el archivo ya existe con otras nested classes del contexto).

---

## 7. Detalle por Archivo

### `RegistrarFichaPerfilInputAdapter.java` (a MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web`
- **Tipo:** Controller REST (`@RestController`)
- **Responsabilidad:** Exponer endpoint `POST /fichas-perfil` para registrar ficha de perfil. Serializa el UUID retornado a JSON con `201 Created`.
- **Cambio principal:** corregir `@PreAuthorize("hasAuthority('ficha:ficha:create')")` por `@PreAuthorize("hasAuthority('fichas:ficha-perfil:create')")` — el authority actual está mal formado (no sigue convención kebab-case `{contexto}:{recurso}:{accion}`).
- **Métodos principales:**
    - `registrar(RegistrarFichaPerfilRequestDTO): ResponseEntity<UUID>` — delega a `registrarFichaPerfilInputPort.ejecutar(...)` y retorna `201 Created` con el UUID en el body.
- **Dependencias:** `RegistrarFichaPerfilInputPort` (puerto de entrada), `RegistrarFichaPerfilRequestDTO` (DTO request).
- **`@Tag`:** `name = "Fichas Perfil"`, `description = "Gestión de fichas de perfil de proyectos de grado"` (ya existe).
- **Endpoints documentados:**

  | Método del Controller | `@Operation(summary)` | Códigos `@ApiResponse` | `@SecurityRequirement` |
  |-----------------------|-----------------------|------------------------|------------------------|
  | `registrar` | `"Registrar ficha de perfil"` | 201 (UUID), 400 (ErrorResponseDTO), 401, 403, 422 (DomainValidationException con fieldErrors) | `bearerAuth` |

### `FichaPerfilAggregate.java` (a VALIDAR y posiblemente MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.domain.fichaperfil.aggregate`
- **Tipo:** Entidad de dominio (Aggregate Root — pero **NO extiende `AggregateRoot`** porque la HU no emite eventos)
- **Responsabilidad:** Encapsular lógica de negocio de una ficha de perfil, validar invariantes con Notification Pattern.
- **Cambio principal:** verificar que NO extienda `AggregateRoot`. Si el código actual la extiende, **quitar `extends AggregateRoot`** porque esta HU no emite eventos. Validar que `crear(...)` use `DomainValidator` con las constantes del catálogo `FichasMessages.FichaPerfil.*`.
- **Features Java 21 aplicables:** ninguno — la entidad es una clase inmutable con constructor privado, no un `record`.
- **Métodos principales:**
    - `crear(String tituloProyecto, UUID asesorFichaId): FichaPerfilAggregate` — factory que crea entidad nueva, genera UUID con `UUID.randomUUID()`, valida con Notification Pattern (lanza `DomainValidationException` si hay errores), aplica `.trim()` antes de validar. **NO acumula eventos** (no existe `publicarEvento`).
    - `reconstruir(UUID id, String tituloProyecto, UUID asesorFichaId): FichaPerfilAggregate` — factory que reconstruye desde persistencia, NO valida (confía en que la BD ya tiene datos válidos).
- **Dependencias:** `ValidationResult` (Notification Pattern de `shared:domain.validation`), `DomainValidator` (validaciones estáticas), `FichasMessages.FichaPerfil` (constantes del catálogo).

### `RegistrarFichaPerfilUseCase.java` (a VALIDAR y posiblemente MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.application.fichaperfil.command`
- **Tipo:** UseCase (`@Component`, `@Transactional`)
- **Responsabilidad:** Orquestar el flujo de registro: validar asesor existe, validar título único, crear aggregate, persistir, retornar UUID. **NO drena eventos** porque la HU no emite.
- **Cambio principal:** verificar que implemente el flujo completo: (1) `asesorFichaQueryOutputPort.existsById(asesorId)` → lanzar `AsesorFichaNoEncontradoException` si no existe, (2) `fichaPerfilOutputPort.existsByTituloProyecto(titulo)` → lanzar `FichaTituloDuplicadoException` si ya existe, (3) `FichaPerfilAggregate.crear(...)`, (4) `fichaPerfilOutputPort.guardar(...)`, (5) `log.info` con constante del catálogo, (6) retornar `ficha.getId()`. **NO** hay drenado de eventos ni inyección de `EventPublisher`.
- **Métodos principales:**
    - `ejecutar(RegistrarFichaPerfilCommand): UUID` — flujo principal descrito arriba.
- **Dependencias:** `RegistrarFichaPerfilInputPort` (implementa), `FichaPerfilOutputPort` (persistencia del propio aggregate), `AsesorFichaQueryOutputPort` (lookup FK sobre la vista materializada), `FichaPerfilAggregate`, excepciones (`AsesorFichaNoEncontradoException`, `FichaTituloDuplicadoException`), `FichasMessages.FichaPerfil` (constantes).

### `FichaPerfilOutputPort.java` (a MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.domain.fichaperfil.port.out`
- **Tipo:** Interface (puerto de salida write — solo opera sobre el propio aggregate)
- **Responsabilidad:** Contrato para persistencia del aggregate y validaciones de invariantes sobre el propio aggregate.
- **Cambio principal:** **quitar `existsAsesorById`** del puerto (es lookup de otro aggregate — viola la regla del skill "OutputPort solo opera sobre su propio aggregate"). Dejar solo `guardar`, `buscarPorId`, `existsByTituloProyecto`.
- **Métodos principales:**
    - `guardar(FichaPerfilAggregate): void` — persiste el aggregate.
    - `Optional<FichaPerfilAggregate> buscarPorId(UUID)` — reconstruye aggregate desde BD.
    - `boolean existsByTituloProyecto(String titulo)` — valida POL-02 (invariante de unicidad sobre el propio aggregate).

### `FichaPerfilCommandOutputAdapter.java` (a MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.out.persistence`
- **Tipo:** Adapter de persistencia write (`@Component`)
- **Responsabilidad:** Implementa `FichaPerfilOutputPort` usando `FichaPerfilJpaRepository` y `FichaPerfilMapper`. Usa `reconstruir(...)` al reconstruir desde BD.
- **Cambio principal:** **quitar dependencia de `AsesorFichaJpaRepository`** y el método `existsAsesorById`. Esa lógica se traslada al nuevo `AsesorFichaQueryOutputAdapter` (puerto separado).
- **Métodos principales:**
    - `guardar(FichaPerfilAggregate): void` — persiste el aggregate.
    - `buscarPorId(UUID): Optional<FichaPerfilAggregate>` — usa `reconstruir(...)`.
    - `existsByTituloProyecto(String): boolean` — delega a `fichaPerfilJpaRepository.existsByTituloProyecto(...)`.
- **Dependencias:** `FichaPerfilJpaRepository`, `FichaPerfilMapper`, `FichaPerfilAggregate`. Mantiene `EntityManager` (`@PersistenceContext`) para `getReference(AsesorFichaJpaEntity.class, ...)` al armar la FK del `guardar(...)` — esto NO viola la regla porque solo usa la referencia JPA, no consulta el aggregate.

### `AsesorFichaQueryOutputPort.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.asesorficha.query.port.out`
- **Tipo:** Interface (query side de la vista materializada `AsesorFicha`)
- **Responsabilidad:** Exponer lookups sobre la vista materializada para uso de otros aggregates del contexto.
- **Métodos principales:** `boolean existsById(UUID id)`.

### `AsesorFichaQueryOutputAdapter.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.asesorficha.query.adapter.out.persistence`
- **Tipo:** `@Component` que implementa `AsesorFichaQueryOutputPort`.
- **Responsabilidad:** Delega a `AsesorFichaJpaRepository.existsById(...)`.

### Excepciones de aplicación (a VALIDAR y posiblemente CREAR)

Si no existen, crear en `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/exception/`:

#### `FichaTituloDuplicadoException.java`
- **Paquete:** `com.arquisoft.fichas.application.fichaperfil.exception`
- **Tipo:** Exception de duplicado (extiende `ApplicationException` → 400)
- **Responsabilidad:** Lanzada cuando el título ya existe (POL-02).
- **Constructor:** `public FichaTituloDuplicadoException(String titulo)` → llama a `super(FichasMessages.FichaPerfil.TITULO_DUPLICADO.formatted(titulo), FichasMessages.FichaPerfil.FICHA_TITULO_DUPLICADO)`.

#### `AsesorFichaNoEncontradoException.java`
- **Paquete:** `com.arquisoft.fichas.application.fichaperfil.exception`
- **Tipo:** Exception de no encontrado (extiende `ApplicationException` → 400)
- **Responsabilidad:** Lanzada cuando el asesor no existe (POL-03).
- **Constructor:** `public AsesorFichaNoEncontradoException(UUID id)` → llama a `super(FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO_MSG.formatted(id), FichasMessages.FichaPerfil.ASESOR_NO_ENCONTRADO)`.

> **Ubicación directa:** `fichaperfil/exception/`, sin anidar `command/` — la excepción pertenece al concepto entidad, no al slice CQRS.

### `FichasMessages.java` (a MODIFICAR)
- **Paquete:** `com.arquisoft.shared.message`
- **Tipo:** Catálogo de mensajes (clase `public final` con constructor privado)
- **Cambio principal:** añadir nested class `public static final class FichaPerfil` con constructor privado vacío (si no existe) y dentro las constantes listadas en la sub-sección "Catálogo de mensajes" arriba, en el orden de las 5 secciones: `// Campos`, `// Límites`, `// Códigos de error`, `// Mensajes de error`, `// Logs`.
- **Responsabilidad:** Centralizar todos los strings, códigos de error, nombres de campo, mensajes de log y límites numéricos del contexto `fichas`.
- **Restricciones:** NO JavaDoc en este archivo. Los nombres son autoexplicativos.

---

## 8. Endpoints REST

### Estado del endpoint

- [x] **Endpoint EXISTENTE** — modificar el adapter ya presente en el proyecto.
    - **Archivo a modificar:** `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/fichaperfil/command/adapter/in/web/RegistrarFichaPerfilInputAdapter.java`
    - **Qué cambia:** corregir `@PreAuthorize("hasAuthority('ficha:ficha:create')")` por `@PreAuthorize("hasAuthority('fichas:ficha-perfil:create')")` — el authority actual no sigue convención kebab-case del proyecto.

### Contrato del endpoint

| Método | Ruta | Request Body | Response | Código HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|--------------|----------|-------------|----------------------|-------------------------------|
| POST | `/api/fichas-perfil` | `RegistrarFichaPerfilRequestDTO` (`tituloProyecto`: String 1-100, `asesorFichaId`: UUID) | `UUID` (body: id generado) | 201 | `fichas:ficha-perfil:create` | `@Operation(summary="Registrar ficha de perfil")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses` con 201, 400, 401, 403, 422 |

> **Convención de respuesta (write opción A — default):** `ResponseEntity<UUID>` con `201 Created` y el UUID en el body. El use case retorna `UUID` y el `InputPort` extiende `InputPort<RegistrarFichaPerfilCommand, UUID>`.

---

## 9. Seguridad y Autorización (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Roles realm que lo poseen | Endpoint(s) que lo requieren | Descripción funcional |
|---|---|---|---|
| `fichas:ficha-perfil:create` | `coordinador`, `asesor-ficha` | `POST /api/fichas-perfil` | Permite registrar nuevas fichas de perfil de proyectos de grado asignando título y asesor |

### Reglas de uso

1. **Formato del client role:** `fichas:ficha-perfil:create` — en kebab-case (todo minúscula, palabras del recurso separadas por guiones). El authority actual en el código (`ficha:ficha:create`) está **mal** — debe corregirse.
2. **Conversión de nombres:** la entidad `FichaPerfilAggregate` → recurso `ficha-perfil` (kebab-case, no camelCase `fichaPerfil`).
3. **Roles realm en kebab-case:** `coordinador`, `asesor-ficha`.
4. **Un client role puede pertenecer a varios roles realm:** `coordinador` y `asesor-ficha` comparten el mismo client role `fichas:ficha-perfil:create`.
5. **Un `@PreAuthorize` por endpoint** con un único client role.
6. **NO se usa `hasRole(...)`** ni roles realm directamente — siempre `hasAuthority(...)` con client role.

### Configuración requerida en Keycloak (instrucciones para equipo de seguridad)

1. En el cliente `arquisoft-api`: crear el client role `fichas:ficha-perfil:create`.
2. Asignar el client role a los roles realm `coordinador` y `asesor-ficha`.
3. Verificar que los usuarios de prueba con esos roles realm reciban el client role en su JWT (`resource_access.arquisoft-api.roles`).

---

## 10. Eventos RabbitMQ

**Eventos: ninguno.**

Esta HU NO emite eventos de dominio (CRUD interno sin consumidores conocidos ni auditoría identificada). El use case NO inyecta `EventPublisher`, no hay drenado de eventos ni archivos de mensajería RabbitMQ.

---

## 11. Migración de Base de Datos

**Las tablas ya existen.** La migración `V1.0__crear_tablas_fichas_perfil.sql` ya creó las tablas `ficha_perfil` y `asesor_ficha` con las columnas correctas y el constraint `UNIQUE (titulo_proyecto)`.

**Validación requerida:** verificar que el archivo `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.0__crear_tablas_fichas_perfil.sql` contiene:

```sql
CREATE TABLE asesor_ficha (
    id             UUID         NOT NULL,
    identificador  VARCHAR(30)  NOT NULL,
    nombre         VARCHAR(50)  NOT NULL,
    email          VARCHAR(50)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE ficha_perfil (
    id               UUID         NOT NULL,
    titulo_proyecto  VARCHAR(100) NOT NULL,
    asesor_ficha_id  UUID         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_ficha_perfil_titulo UNIQUE (titulo_proyecto),
    CONSTRAINT fk_ficha_perfil_asesor FOREIGN KEY (asesor_ficha_id)
        REFERENCES asesor_ficha(id)
);
```

**Sin cambios necesarios en Flyway** si el archivo ya contiene el DDL correcto.

---

## 12. Casos de Prueba Sugeridos

> **Tipo de Use Case declarado en Metadata:** Escritura. Aplica la sección "Caso A — Use Case de ESCRITURA" del protocolo de tests.

### Presupuesto orientativo

| Tipo de HU | Tests esperados |
|---|---|
| Pequeña (1 endpoint, 1 entidad) | 15 - 25 |

**Estimación para esta HU:** ~20 tests (domain 5, application 6, infrastructure 9).

---

### Caso A — Use Case de ESCRITURA (crea ficha)

#### Tests capa `domain` (Aggregate Root — sin eventos)

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `FichaPerfilAggregateTest` | `debeConstruirFicha_cuandoDatosValidos` | `crear(titulo, asesorId)` crea entidad con UUID no nulo, título trimmed, asesor asignado |
| `FichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoTituloVacio` | `crear("", asesorId)` lanza `DomainValidationException` con `fieldErrors` conteniendo error para `tituloProyecto` |
| `FichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoTituloMuyLargo` | `crear(titulo_101_caracteres, asesorId)` lanza `DomainValidationException` |
| `FichaPerfilAggregateTest` | `debeLanzarExcepcion_cuandoAsesorNull` | `crear(titulo, null)` lanza `DomainValidationException` con error para `asesorFichaId` |
| `FichaPerfilAggregateTest` | `debeReconstruirSinValidar_cuandoReconstruirEsInvocado` | `reconstruir(id, titulo, asesorId)` retorna entidad sin lanzar excepción, incluso con datos inválidos |

> **NO se testea ciclo de eventos** (`publicarEvento`, `extraerEventosSinPublicar`) porque la HU no emite eventos. El aggregate NO extiende `AggregateRoot` — es una clase plana.

#### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `RegistrarFichaPerfilUseCaseTest` | `debeRegistrar_cuandoDatosValidos` | flujo exitoso completo: asesor existe, título único, retorna UUID no nulo |
| `RegistrarFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoAsesorNoExiste` | `asesorFichaQueryOutputPort.existsById(asesorId)` retorna `false` → lanza `AsesorFichaNoEncontradoException` con errorCode `ASESOR_NO_ENCONTRADO` |
| `RegistrarFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoTituloDuplicado` | `existsByTituloProyecto(titulo)` retorna `true` → lanza `FichaTituloDuplicadoException` con errorCode `FICHA_TITULO_DUPLICADO` |
| `RegistrarFichaPerfilUseCaseTest` | `debeGuardarFicha_cuandoValidacionesExitosas` | verify `fichaPerfilOutputPort.save(any())` se llamó exactamente 1 vez |
| `RegistrarFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoRepositorioFalla` | `save(...)` lanza `InfrastructureException` → se propaga |
| `RegistrarFichaPerfilUseCaseTest` | `debeLoguearRegistro_cuandoExitoso` | verify `log.info` con constante `FichasMessages.FichaPerfil.LOG_REGISTRADA` |

> **NO se verifica publicación de eventos** (`verify(eventPublisher).publish(...)`) porque la HU no emite eventos. El use case NO inyecta `EventPublisher`.

#### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `FichaPerfilCommandOutputAdapterTest` | `debeGuardar_cuandoFichaEsValida` | persistencia OK — JPA Entity guardado |
| `FichaPerfilCommandOutputAdapterTest` | `debeReconstruirConReconstruir_cuandoFindByIdExiste` | `findById(id)` retorna `Optional` con aggregate usando `reconstruir(...)` |
| `AsesorFichaQueryOutputAdapterTest` | `debeRetornarTrue_cuandoAsesorExiste` | `existsById(id)` retorna `true` si `asesorFichaJpaRepository.existsById(id)` es `true` |
| `AsesorFichaQueryOutputAdapterTest` | `debeRetornarFalse_cuandoAsesorNoExiste` | `existsById(id)` retorna `false` si no existe |
| `FichaPerfilCommandOutputAdapterTest` | `debeRetornarTrue_cuandoTituloExiste` | `existsByTituloProyecto(titulo)` retorna `true` si ya existe ficha con ese título |
| `FichaPerfilCommandOutputAdapterTest` | `debeRetornarFalse_cuandoTituloNoExiste` | `existsByTituloProyecto(titulo)` retorna `false` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe201_cuandoPeticionValida` | POST con DTO válido → `201 Created` + UUID en body |
| `RegistrarFichaPerfilInputAdapterTest` | `debe422_cuandoRequestInvalido` | POST con DTO con datos inválidos (titulo vacío) → `422 Unprocessable Content` + `fieldErrors[]` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe401_cuandoNoAutenticado` | sin token → `401 Unauthorized` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe403_cuandoRolInsuficiente` | autenticado con rol `estudiante` (sin client role `fichas:ficha-perfil:create`) → `403 Forbidden` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe400_cuandoTituloDuplicado` | `FichaTituloDuplicadoException` lanzada → `400 Bad Request` + errorCode `FICHA_TITULO_DUPLICADO` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe400_cuandoAsesorNoEncontrado` | `AsesorFichaNoEncontradoException` lanzada → `400 Bad Request` + errorCode `ASESOR_NO_ENCONTRADO` |

---

### Reglas de consolidación

- **No tests de getters/setters** generados por Lombok (no existen en el aggregate — es inmutable sin Lombok).
- **No tests de validaciones Jakarta una por una** — un solo test `debe422_cuandoRequestInvalido` basta.
- **No tests de métodos `private`** — se validan implícitamente desde los factories `crear`/`reconstruir`.
- **Consolidar asserts complementarios** en un solo test (ej. tipo de excepción + errorCode + mensaje en un solo test `debeLanzarExcepcion_cuandoTituloDuplicado`).

Ver sección "Anti-patrones de Testing en Arquisoft" del skill `arquisoft-context` para ejemplos detallados.

---

## 13. Checklist de Implementación

- [ ] **DDD:** Entidad de dominio `FichaPerfilAggregate` **NO** extiende `AggregateRoot` (la HU no emite eventos)
- [ ] Entidad inmutable: constructor privado, campos `final`, factory methods `crear` / `reconstruir`, sin Lombok
- [ ] **NO hay eventos de dominio** (no se crean archivos en `domain/fichaperfil/event/`)
- [ ] Factory `crear(...)` usa Notification Pattern con `DomainValidator` y lanza `DomainValidationException` si hay errores. **NO acumula eventos** (no existe `publicarEvento`).
- [ ] IDs siempre `UUID` (autogenerado en `crear(...)` con `UUID.randomUUID()`)
- [ ] Puerto de entrada (`RegistrarFichaPerfilInputPort`) definido (ya existe)
- [ ] Puerto de salida `FichaPerfilOutputPort` define `guardar`, `buscarPorId`, `existsByTituloProyecto` (solo sobre el propio aggregate)
- [ ] Puerto de salida `AsesorFichaQueryOutputPort` define `existsById` (lookup FK sobre la vista materializada)
- [ ] El use case inyecta **ambos** puertos (`FichaPerfilOutputPort` + `AsesorFichaQueryOutputPort`)
- [ ] Excepciones de aplicación (`FichaTituloDuplicadoException`, `AsesorFichaNoEncontradoException`) extienden `ApplicationException` (400) y usan constantes del catálogo
- [ ] `RegistrarFichaPerfilCommand` (`record` en `application/fichaperfil/command/model/`) y `RegistrarFichaPerfilRequestDTO` (`record` en `infrastructure/.../dto/`) con anotaciones Jakarta + método `toCommand()`. Campos en español idénticos al aggregate.
- [ ] Caso de uso (`RegistrarFichaPerfilUseCase`) con `@RequiredArgsConstructor`, `@Transactional` y flujo completo (validar asesor, validar título, crear, persistir, loguear, retornar UUID). **NO drena eventos** (no inyecta `EventPublisher`).
- [ ] Controller REST con `@Valid @RequestBody` y autorización `@PreAuthorize("hasAuthority('fichas:ficha-perfil:create')")` **corregido** (era `'ficha:ficha:create'` — incorrecto)
- [ ] Controller documentado con `@Tag`, `@Operation`, `@ApiResponses` y `@SecurityRequirement` (ADR-011) — ya existe, validar que esté completo
- [ ] Entidad JPA `FichaPerfilJpaEntity` con `@Table(name = "ficha_perfil")` (sin atributo `schema`) y adaptador de repositorio `FichaPerfilCommandOutputAdapter` implementa puerto
- [ ] Migración Flyway `V1.0__crear_tablas_fichas_perfil.sql` ya existe — validar que contenga `UNIQUE (titulo_proyecto)` y FK correcta
- [ ] **NO hay eventos RabbitMQ** (esta HU no emite eventos)
- [ ] Tests unitarios con patrón AAA (cobertura ≥ 75%), **sin tests de ciclo de eventos** (no aplica porque el aggregate no extiende `AggregateRoot`)
- [ ] Tests de repositorio con H2
- [ ] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4.x)
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot)
- [ ] **Catálogo `shared:message`:** nested class `FichasMessages.FichaPerfil` con las 11 constantes listadas (5 secciones: Campos, Límites, Códigos de error, Mensajes de error, Logs)
- [ ] Commit: `feat(fichas): completar HU-208 registrar ficha perfil`

---

## 14. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-06-11 | Build -x test: sin errores |
| Tests      | @tester             | ✅ Completado | 2026-06-20 | 20 tests en verde — domain (5), application (6), infrastructure (9). Fix: añadida dep oauth2-resource-server para @WebMvcTest con JWT |
| Validación | @validator-analyze  | ✅ Completado | 2026-06-20 | Score: 100/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-06-20 | /.workspace/validator/validator-HU-208.md |
| Commit     | @commit             | ✅ Completado | 2026-06-20 | Hash: cbeb352 |
