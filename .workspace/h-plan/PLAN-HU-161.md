# PLAN: Asignar estudiantes a una ficha de perfil existente

## Metadata
- **ID Historia:** HU-161
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura (orquestación sobre endpoint existente — extiende RegistrarFichaPerfilUseCase)
- **¿Usa AggregateRoot?:** No — `FichaPerfilAggregate` es una `final class` plana, NO extiende `AggregateRoot` (es CRUD sin eventos; verificado en el código real). Esta HU no lo cambia.
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-06-22
- **Rama sugerida:** `feature/HU-161-asignar-estudiantes-ficha-perfil`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md` (HU-161)
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/05_usuarios_modelo_enriquecido.md` (para réplica de Estudiante)
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** Esta HU es una orquestación sobre el endpoint existente `POST /api/fichas-perfil` (RegistrarFichaPerfilUseCase). El `RegistrarFichaPerfilRequestDTO` se extiende para incluir la lista de estudiantes. La HU añade la lógica de asignación de estudiantes dentro de ese flujo existente. No se crea un endpoint nuevo. El endpoint ya retorna el UUID de la ficha perfil creada, y con la relación `estudiante_ficha_perfil` eso es suficiente — no hay que cambiar la respuesta del endpoint. Los estados de ficha pertenecen a otra HU, no están en el alcance de HU-161.

---

## 1. Resumen Funcional

La HU-161 permite asignar hasta 3 estudiantes a una ficha de perfil durante su creación. El coordinador proporciona los UUIDs de los estudiantes (que ya deben existir como réplicas locales en el contexto `fichas`), y el sistema valida su existencia y disponibilidad antes de persistir las relaciones en la tabla `estudiante_ficha_perfil`. Esta funcionalidad se integra al flujo existente de registro de ficha sin crear un endpoint REST adicional — solo se extiende el `RegistrarFichaPerfilRequestDTO` con un campo `estudiantesIds` (lista de UUIDs) y se amplía la lógica del `RegistrarFichaPerfilUseCase` para persistir las relaciones tras guardar el aggregate.

Lo que NO cubre: cambio de estudiantes asignados (es otra HU), remoción de estudiantes (es otra HU), validaciones de estado de la ficha (no están en esta HU).

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | El coordinador puede asignar entre 0 y 3 estudiantes al crear una ficha | El sistema persiste las relaciones en `estudiante_ficha_perfil` |
| 2 | Todos los estudiantes deben existir previamente como réplicas locales en el contexto `fichas` | Si alguno no existe, se lanza `EstudianteNoEncontradoException` (400) |
| 3 | Un estudiante puede ser asignado una sola vez a la misma ficha | Si se incluye el mismo UUID más de una vez, se lanza `EstudianteDuplicadoException` (400) |
| 4 | El sistema rechaza listas de más de 3 estudiantes | Lanza `LimiteEstudiantesExcedidoException` (400) con el límite exacto en el mensaje |

---

## 3. Reglas de Negocio

- **Límite de estudiantes por ficha:** 3 (constante `ESTUDIANTES_MAX = 3` en catálogo `FichasMessages.FichaPerfil`).
- **Validación de existencia:** cada UUID de la lista debe corresponder a un `EstudianteAggregate` presente en la tabla `estudiante` del contexto `fichas` (réplica local materializada desde el contexto `usuarios`).
- **Unicidad dentro de la misma asignación:** la lista de UUIDs debe ser sin duplicados (validación explícita en el use case con `new HashSet<>(estudiantesIds).size() != estudiantesIds.size()`).
- **Restricción base de datos:** `UNIQUE (ficha_perfil_id, estudiante_id)` previene duplicados a nivel BD.

---

## 4. Modelo DDD del Contexto

### Aggregate Root

- **Entidad raíz (existente):** `FichaPerfilAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** No — es una `final class` plana, NO extiende `AggregateRoot` (CRUD sin eventos; confirmado leyendo el archivo real). Esta HU no lo modifica.
- **ID:** `UUID`

> **Nota:** `FichaPerfilAggregate` ya existe, NO se modifica en esta HU — no se agrega un método de negocio `asignarEstudiantes` porque la lógica de asignación es pura persistencia de relaciones, no un comportamiento invariante del aggregate. La asignación se hace tras persistir el aggregate, en el use case.

### Atributos por objeto de dominio

#### `FichaPerfil` (sin cambios)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | PK autogenerado en `crear(...)` |
| `tituloProyecto` | `String` | 5-100 | Sí | Sí | No | Limpiar espacios |
| ... | ... | ... | ... | ... | ... | (resto de atributos existentes no cambian) |

#### `Estudiante` (réplica local, nueva entidad — vista materializada del contexto `usuarios`)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | PK autogenerado cuando se replica (no es autogenerado localmente — el UUID viene del evento `UsuarioCreadoEvent` publicado por el contexto `usuarios`) |
| `nombre` | `String` | 1-100 | Sí | No | No | Nombre completo del estudiante |
| `email` | `String` | — | Sí | No | No | Email institucional (único en el contexto origen, no validado como único localmente — no aplica `UNIQUE` en la tabla `estudiante` del contexto `fichas`) |

**Combinaciones únicas (Restricciones):** NO aplica para esta réplica local — la unicidad de `email` la garantiza el contexto `usuarios` (origen). En el contexto `fichas`, `estudiante.email` NO tiene constraint `UNIQUE` porque la réplica es read-only para consultas y FKs locales.

#### `EstudianteFichaPerfil` (tabla de relación, nueva)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | PK autogenerado |
| `fichaPerfilId` | `UUID` | — | Sí | No | No | FK a `ficha_perfil.id` |
| `estudianteId` | `UUID` | — | Sí | No | No | FK a `estudiante.id` (tabla réplica local) |

**Combinaciones únicas (Restricciones):**
- `UNIQUE (ficha_perfil_id, estudiante_id)` → previene asignar el mismo estudiante dos veces a la misma ficha.

### Traducción del modelo enriquecido a código

| Característica del modelo | Traducción en código |
|---|---|
| `Estudiante.nombre` obligatorio, max 100 | `@Column(nullable=false, length=100)` en JPA + `NOT NULL VARCHAR(100)` en Flyway |
| `Estudiante.email` obligatorio, NO único local | `@Column(nullable=false)` en JPA + `NOT NULL VARCHAR(255)` en Flyway (SIN `UNIQUE`) |
| Límite 3 estudiantes por ficha | `FichasMessages.FichaPerfil.ESTUDIANTES_MAX = 3` en catálogo + validación en use case `if (estudiantesIds.size() > 3)` |
| Unicidad `(fichaPerfilId, estudianteId)` | `UNIQUE (ficha_perfil_id, estudiante_id)` en Flyway + validación de duplicados en use case antes de persistir (protección defensiva) |

### Eventos de Dominio que emite

**Eventos: ninguno.**

**Razón:** la asignación de estudiantes es una operación de relación (CRUD interno del contexto `fichas` sin consumidores conocidos ni casos de auditoría identificados en esta HU). `FichaPerfilAggregate` tampoco emite eventos hoy — es una clase plana sin `AggregateRoot`. Si un contexto futuro necesita saber que un estudiante fue asignado a una ficha (o que la ficha fue creada), esa HU introducirá el evento correspondiente y, en ese momento, `FichaPerfilAggregate` pasaría a extender `AggregateRoot`.

**Implicaciones:**
- La entidad raíz `FichaPerfilAggregate` **NO cambia** — es una `final class` plana que NO extiende `AggregateRoot` ni emite eventos.
- El factory `crear(...)` de `FichaPerfilAggregate` **NO se modifica** y **NO emite eventos**.
- El use case `RegistrarFichaPerfilUseCase` **NO inyecta `EventPublisher`** ni drena eventos — hoy inyecta `FichaPerfilOutputPort` y `AsesorFichaQueryOutputPort` (verificado en el código real). Esta HU le añade `EstudianteQueryOutputPort` y `EstudianteFichaPerfilOutputPort`.
- **No se crean archivos en `domain/estudiante/event/` ni en `domain/estudianteFichaPerfil/event/`**.
- La lógica de asignación de estudiantes se añade **tras** persistir el `FichaPerfilAggregate`, dentro de la misma transacción — sin eventos.

---

## 5. Integraciones Externas

> No aplica — la HU no requiere integración con sistemas externos más allá de PostgreSQL (tabla réplica local + tabla de relación).

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

#### Entidad `Estudiante` (réplica local)

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/src/main/java/com/arquisoft/fichas/domain/estudiante/aggregate/EstudianteAggregate.java` | Aggregate (sin extender `AggregateRoot` — réplica read-only) | Factory `reconstruir(...)` para materializar desde JPA — NO emite eventos. Esta es una vista materializada del contexto `usuarios`, solo para validaciones FK locales. |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/estudiante/persistence/EstudianteJpaEntity.java` | JPA Entity | `@Table(name = "estudiante")` sin unique en `email` |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/estudiante/persistence/EstudianteJpaRepository.java` | `JpaRepository` | `JpaRepository<EstudianteJpaEntity, UUID>` con método `existsById(UUID)` |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/estudiante/persistence/EstudianteMapper.java` | `@Component` | Mapea `EstudianteJpaEntity` ↔ `EstudianteAggregate` (solo `reconstruir`, no `crear`) |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/estudiante/query/port/out/EstudianteQueryOutputPort.java` | Interface (query side de la vista materializada) | `boolean existsById(UUID id)` — lookup FK del estudiante desde el use case write, sin acoplar `application` a JPA. Espejo de `AsesorFichaQueryOutputPort` (HU-208). |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/estudiante/query/adapter/out/persistence/EstudianteQueryOutputAdapter.java` | `@Component` (implementa el puerto) | Delega a `EstudianteJpaRepository.existsById(...)`. |

#### Entidad `EstudianteFichaPerfil` (relación)

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| domain | `fichas/src/main/java/com/arquisoft/fichas/domain/estudianteFichaPerfil/aggregate/EstudianteFichaPerfilAggregate.java` | Aggregate (sin extender `AggregateRoot` — relación CRUD) | Factory `crear(fichaPerfilId, estudianteId)` genera UUID + validaciones de nulls (Notification Pattern). Factory `reconstruir(...)` para leer desde BD. |
| domain | `fichas/src/main/java/com/arquisoft/fichas/domain/estudianteFichaPerfil/port/out/EstudianteFichaPerfilOutputPort.java` | Interface | Puerto de salida write. Métodos: `guardar(EstudianteFichaPerfilAggregate)`, `existePorFichaYEstudiante(UUID fichaId, UUID estudianteId)` (validación defensiva contra duplicados) |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/estudianteFichaPerfil/exception/EstudianteDuplicadoException.java` | Exception | Extiende `ApplicationException` (400). Código `ESTUDIANTE_DUPLICADO`. Mensaje parametrizado con ID del estudiante duplicado. |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/estudianteFichaPerfil/exception/LimiteEstudiantesExcedidoException.java` | Exception | Extiende `ApplicationException` (400). Código `LIMITE_ESTUDIANTES_EXCEDIDO`. Mensaje parametrizado con límite (3). |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/estudianteFichaPerfil/persistence/EstudianteFichaPerfilJpaEntity.java` | JPA Entity | `@Table(name = "estudiante_ficha_perfil", uniqueConstraints = @UniqueConstraint(columnNames = {"ficha_perfil_id", "estudiante_id"}))` |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/estudianteFichaPerfil/persistence/EstudianteFichaPerfilJpaRepository.java` | `JpaRepository` | `JpaRepository<EstudianteFichaPerfilJpaEntity, UUID>` con `existsByFichaPerfilIdAndEstudianteId(UUID, UUID)` |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/estudianteFichaPerfil/persistence/EstudianteFichaPerfilMapper.java` | `@Component` | Mapea `EstudianteFichaPerfilJpaEntity` ↔ `EstudianteFichaPerfilAggregate` |
| infrastructure | `fichas/src/main/java/com/arquisoft/fichas/infrastructure/estudianteFichaPerfil/command/adapter/out/persistence/EstudianteFichaPerfilCommandOutputAdapter.java` | Adapter | Implementa `EstudianteFichaPerfilOutputPort`. Usa `reconstruir(...)` en lookups. |

#### Excepción nueva (estudiante no encontrado)

| Capa | Ruta completa | Tipo | Responsabilidad |
|------|---------------|------|-----------------|
| application | `fichas/src/main/java/com/arquisoft/fichas/application/estudiante/exception/EstudianteNoEncontradoException.java` | Exception | Extiende `ApplicationException` (400). Código `ESTUDIANTE_NO_ENCONTRADO`. Mensaje parametrizado con UUID del estudiante no encontrado. |

### Catálogo de mensajes (`shared:message`) — MODIFICAR

| Capa | Ruta completa | Tipo | Acción | Detalles |
|------|---------------|------|--------|----------|
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Catálogo | MODIFICAR | Agregar nested class `public static final class Estudiante` (si no existe) con las constantes de Estudiante. Dentro de la nested class existente `FichaPerfil`, agregar constante `ESTUDIANTES_MAX = 3`. Dentro de `EstudianteFichaPerfil` (nueva nested class), agregar códigos y mensajes de error de las excepciones de esta HU. |

**Inventario de constantes a agregar al catálogo en esta HU:**

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.FichaPerfil.ESTUDIANTES_MAX` | Límites | `int` | `3` | `LimiteEstudiantesExcedidoException` (mensaje) + validación en use case |
| `FichasMessages.Estudiante.ESTUDIANTE_NO_ENCONTRADO` | Códigos de error | `String` | `"ESTUDIANTE_NO_ENCONTRADO"` | `EstudianteNoEncontradoException` (errorCode) |
| `FichasMessages.Estudiante.NO_ENCONTRADO` | Mensajes de error | `String` | `"No se encontró el estudiante con id: %s"` | `EstudianteNoEncontradoException` (mensaje, `.formatted(estudianteId)`) |
| `FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO` | Códigos de error | `String` | `"ESTUDIANTE_DUPLICADO"` | `EstudianteDuplicadoException` (errorCode) |
| `FichasMessages.EstudianteFichaPerfil.DUPLICADO` | Mensajes de error | `String` | `"El estudiante ya está asignado a esta ficha: %s"` | `EstudianteDuplicadoException` (mensaje, `.formatted(estudianteId)`) |
| `FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO` | Códigos de error | `String` | `"LIMITE_ESTUDIANTES_EXCEDIDO"` | `LimiteEstudiantesExcedidoException` (errorCode) |
| `FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO` | Mensajes de error | `String` | `"No se pueden asignar más de %d estudiantes a una ficha"` | `LimiteEstudiantesExcedidoException` (mensaje, `.formatted(FichasMessages.FichaPerfil.ESTUDIANTES_MAX)`) |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/RegistrarFichaPerfilUseCase.java` | Añadir inyección de `EstudianteQueryOutputPort` (para validar existencia vía query side de la vista materializada — **NO** inyectar `EstudianteJpaRepository`, eso rompería `application ← infrastructure`) y `EstudianteFichaPerfilOutputPort` (para persistir relaciones). Tras persistir `FichaPerfilAggregate`, iterar sobre la lista `command.estudiantesIds()` (si no es null ni vacía): validar existencia de cada estudiante, validar que no se repita dentro de la lista, validar límite ≤ 3, crear cada `EstudianteFichaPerfilAggregate` con `crear(fichaId, estudianteId)` y persistir vía puerto. **La lógica de asignación va DESPUÉS de `fichaPerfil.save()` y ANTES de `drainUnPublishedEvents()`**. |
| `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/in/web/dto/RegistrarFichaPerfilRequestDTO.java` | Añadir campo `List<UUID> estudiantesIds` (nullable — puede ser `null` o vacío si no se asignan estudiantes) con anotación `@Size(max = 3, message = "No se pueden asignar más de 3 estudiantes")`. En el método `toCommand()`, pasar este campo al `RegistrarFichaPerfilCommand`. |
| `fichas/src/main/java/com/arquisoft/fichas/application/fichaPerfil/command/model/RegistrarFichaPerfilCommand.java` | Añadir campo `List<UUID> estudiantesIds` (nullable). |
| `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.1__crear_estudiante_y_estudiante_ficha_perfil.sql` (siguiente versión tras `V1.0`; `1.1 > 1` baseline → se ejecuta) | Crear tabla `estudiante` (réplica local sin `UNIQUE` en `email`). Crear tabla `estudiante_ficha_perfil` con FKs a `ficha_perfil` y `estudiante`, y constraint `UNIQUE (ficha_perfil_id, estudiante_id)`. **La migración va en el subdirectorio `db/migration/fichas/`** — el Flyway del contexto carga `classpath:db/migration/fichas`. |

---

## 7. Detalle por Archivo

### `EstudianteAggregate.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.domain.estudiante.aggregate`
- **Tipo:** Aggregate (sin extender `AggregateRoot` — réplica read-only)
- **Responsabilidad:** Modelo de dominio de la vista materializada de `Estudiante` del contexto `usuarios`. Solo se usa para validaciones FK locales en el contexto `fichas`. NO emite eventos ni tiene comportamiento de negocio — es una proyección read-only replicada desde eventos del contexto `usuarios`.
- **Features Java 21 aplicables:** `var` para variables locales evidentes, campos `final` inmutables
- **Métodos principales:**
    - `reconstruir(UUID id, String nombre, String email): EstudianteAggregate` — factory para materializar desde JPA. NO valida (asume datos ya válidos de BD). NO genera UUID — recibe el UUID original del contexto `usuarios`.
- **Dependencias:** `com.arquisoft.shared.validation.ValidationResult`, `com.arquisoft.shared.message.FichasMessages.Estudiante`, `java.util.UUID`

### `EstudianteFichaPerfilAggregate.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.domain.estudianteFichaPerfil.aggregate`
- **Tipo:** Aggregate (sin extender `AggregateRoot` — relación CRUD)
- **Responsabilidad:** Modelo de dominio de la relación entre `FichaPerfil` y `Estudiante`. Representa la asignación de un estudiante a una ficha con su fecha de asignación.
- **Features Java 21 aplicables:** `var` para variables locales
- **Métodos principales:**
    - `crear(UUID fichaPerfilId, UUID estudianteId): EstudianteFichaPerfilAggregate` — factory para nueva relación. Genera UUID. Valida no-nulls con Notification Pattern (lanza `DomainValidationException` si hay errores).
    - `reconstruir(UUID id, UUID fichaPerfilId, UUID estudianteId): EstudianteFichaPerfilAggregate` — factory para reconstruir desde BD. NO valida, NO genera UUID.
- **Dependencias:** `com.arquisoft.shared.validation.ValidationResult`, `com.arquisoft.shared.validation.DomainValidator`, `com.arquisoft.shared.message.FichasMessages.EstudianteFichaPerfil`, `java.util.UUID`

### `EstudianteFichaPerfilOutputPort.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.domain.estudianteFichaPerfil.port.out`
- **Tipo:** Interface (puerto de salida write)
- **Responsabilidad:** Define el contrato para persistir y consultar relaciones `EstudianteFichaPerfil` desde el dominio.
- **Métodos principales:**
    - `void guardar(EstudianteFichaPerfilAggregate relacion)` — persiste la relación en BD
    - `boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId)` — validación defensiva contra duplicados (aunque ya hay constraint `UNIQUE` en BD, la validación previa mejora el error retornado al cliente)
- **Dependencias:** `com.arquisoft.fichas.domain.estudianteFichaPerfil.aggregate.EstudianteFichaPerfilAggregate`, `java.util.UUID`

### `EstudianteNoEncontradoException.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.estudiante.exception`
- **Tipo:** Exception
- **Responsabilidad:** Lanzada cuando se intenta asignar un estudiante cuyo UUID no existe en la tabla réplica local `estudiante` del contexto `fichas`.
- **Constructor:**
    ```java
    public EstudianteNoEncontradoException(UUID estudianteId) {
        super(
            FichasMessages.Estudiante.NO_ENCONTRADO.formatted(estudianteId),
            FichasMessages.Estudiante.ESTUDIANTE_NO_ENCONTRADO
        );
    }
    ```
- **Dependencias:** `com.arquisoft.shared.exception.ApplicationException` (clase base → 400), `com.arquisoft.shared.message.FichasMessages.Estudiante`

### `EstudianteDuplicadoException.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.estudianteFichaPerfil.exception`
- **Tipo:** Exception
- **Responsabilidad:** Lanzada cuando se intenta asignar el mismo estudiante más de una vez a la misma ficha (duplicado en la lista del request).
- **Constructor:**
    ```java
    public EstudianteDuplicadoException(UUID estudianteId) {
        super(
            FichasMessages.EstudianteFichaPerfil.DUPLICADO.formatted(estudianteId),
            FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_DUPLICADO
        );
    }
    ```
- **Dependencias:** `com.arquisoft.shared.exception.ApplicationException`, `com.arquisoft.shared.message.FichasMessages.EstudianteFichaPerfil`

### `LimiteEstudiantesExcedidoException.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.estudianteFichaPerfil.exception`
- **Tipo:** Exception
- **Responsabilidad:** Lanzada cuando la lista de estudiantes en el request excede el límite de 3.
- **Constructor:**
    ```java
    public LimiteEstudiantesExcedidoException() {
        super(
            FichasMessages.EstudianteFichaPerfil.LIMITE_EXCEDIDO.formatted(FichasMessages.FichaPerfil.ESTUDIANTES_MAX),
            FichasMessages.EstudianteFichaPerfil.LIMITE_ESTUDIANTES_EXCEDIDO
        );
    }
    ```
- **Dependencias:** `com.arquisoft.shared.exception.ApplicationException`, `com.arquisoft.shared.message.FichasMessages`

### `RegistrarFichaPerfilUseCase.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.application.fichaPerfil.command`
- **Tipo:** UseCase (ya existe, se extiende)
- **Cambio requerido:**
    1. Añadir inyección de `EstudianteQueryOutputPort` y `EstudianteFichaPerfilOutputPort` en el constructor (via `@RequiredArgsConstructor`).
    2. Tras persistir `FichaPerfilAggregate` con `fichaPerfil.guardar(...)`, añadir bloque de asignación de estudiantes:
        ```java
        // Asignar estudiantes (si aplica)
        if (command.estudiantesIds() != null && !command.estudiantesIds().isEmpty()) {
            // Validación de límite
            if (command.estudiantesIds().size() > FichasMessages.FichaPerfil.ESTUDIANTES_MAX) {
                throw new LimiteEstudiantesExcedidoException();
            }
            // Validación de duplicados en la lista
            var idsUnicos = new HashSet<>(command.estudiantesIds());
            if (idsUnicos.size() != command.estudiantesIds().size()) {
                // Identificar el primer duplicado para el mensaje de error
                var visitados = new HashSet<UUID>();
                var duplicado = command.estudiantesIds().stream()
                    .filter(id -> !visitados.add(id))
                    .findFirst()
                    .orElseThrow();
                throw new EstudianteDuplicadoException(duplicado);
            }
            // Validación de existencia y asignación
            for (UUID estudianteId : command.estudiantesIds()) {
                if (!estudianteQueryOutputPort.existsById(estudianteId)) {
                    throw new EstudianteNoEncontradoException(estudianteId);
                }
                var relacion = EstudianteFichaPerfilAggregate.crear(fichaId, estudianteId);
                estudianteFichaPerfilOutputPort.guardar(relacion);
            }
        }
        // Continuar con el drenado de eventos del FichaPerfilAggregate (ya existe)
        ```
    3. El resto del flujo (retorno del UUID de la ficha) NO cambia. **NO hay drenado de eventos** — `RegistrarFichaPerfilUseCase` no inyecta `EventPublisher` (la ficha no emite eventos).
- **Dependencias nuevas:** `EstudianteQueryOutputPort`, `EstudianteFichaPerfilOutputPort`, `EstudianteFichaPerfilAggregate`, excepciones nuevas, `java.util.HashSet`

### `RegistrarFichaPerfilRequestDTO.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.infrastructure.fichaPerfil.command.adapter.in.web.dto`
- **Tipo:** `record` (ya existe, se extiende)
- **Cambio requerido:**
    - Añadir campo:
        ```java
        @Size(max = 3, message = "No se pueden asignar más de 3 estudiantes")
        List<UUID> estudiantesIds
        ```
    - Modificar `toCommand()` para pasar el nuevo campo al `RegistrarFichaPerfilCommand`.
- **Dependencias nuevas:** `jakarta.validation.constraints.Size`, `java.util.List`, `java.util.UUID`

### `RegistrarFichaPerfilCommand.java` (MODIFICAR)
- **Paquete:** `com.arquisoft.fichas.application.fichaPerfil.command.model`
- **Tipo:** `record` (ya existe, se extiende)
- **Cambio requerido:**
    - Añadir campo:
        ```java
        List<UUID> estudiantesIds
        ```
- **Dependencias nuevas:** `java.util.List`, `java.util.UUID`

### `EstudianteFichaPerfilJpaEntity.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.estudianteFichaPerfil.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Entidad JPA para la tabla `estudiante_ficha_perfil`.
- **Anotaciones:**
    ```java
    @Entity
    @Table(
        name = "estudiante_ficha_perfil",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ficha_perfil_id", "estudiante_id"})
    )
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    ```
- **Columnas:**
    - `@Id @Column(columnDefinition = "UUID") UUID id`
    - `@Column(name = "ficha_perfil_id", nullable = false, columnDefinition = "UUID") UUID fichaPerfilId`
    - `@Column(name = "estudiante_id", nullable = false, columnDefinition = "UUID") UUID estudianteId`
- **Dependencias:** `jakarta.persistence.*`, `lombok.*`, `java.util.UUID`

### `EstudianteFichaPerfilJpaRepository.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.estudianteFichaPerfil.persistence`
- **Tipo:** `JpaRepository`
- **Responsabilidad:** Repositorio JPA para `EstudianteFichaPerfilJpaEntity`.
- **Métodos:**
    ```java
    public interface EstudianteFichaPerfilJpaRepository extends JpaRepository<EstudianteFichaPerfilJpaEntity, UUID> {
        boolean existsByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);
    }
    ```
- **Dependencias:** `org.springframework.data.jpa.repository.JpaRepository`, entidad JPA, `java.util.UUID`

### `EstudianteFichaPerfilCommandOutputAdapter.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.estudianteFichaPerfil.command.adapter.out.persistence`
- **Tipo:** Adapter
- **Responsabilidad:** Implementa `EstudianteFichaPerfilOutputPort`. Persiste y consulta relaciones estudiante-ficha.
- **Métodos principales:**
    - `void guardar(EstudianteFichaPerfilAggregate relacion)` — mapea a JpaEntity y persiste con `jpaRepository.save(...)`
    - `boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId)` — delega a `jpaRepository.existsByFichaPerfilIdAndEstudianteId(...)`
- **Dependencias:** puerto, mapper, JPA repository, aggregate, `@Component`, `@RequiredArgsConstructor`

### `EstudianteJpaEntity.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.estudiante.persistence`
- **Tipo:** JPA Entity
- **Responsabilidad:** Entidad JPA para la tabla réplica `estudiante`.
- **Anotaciones:**
    ```java
    @Entity
    @Table(name = "estudiante")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    ```
- **Columnas:**
    - `@Id @Column(columnDefinition = "UUID") UUID id`
    - `@Column(nullable = false, length = 100) String nombre`
    - `@Column(nullable = false) String email` (SIN `unique = true` — la unicidad la garantiza el contexto origen `usuarios`)
- **Dependencias:** `jakarta.persistence.*`, `lombok.*`, `java.util.UUID`

### `EstudianteJpaRepository.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.estudiante.persistence`
- **Tipo:** `JpaRepository`
- **Responsabilidad:** Repositorio JPA para `EstudianteJpaEntity`.
- **Métodos:** hereda `existsById(UUID)` de `JpaRepository` — no requiere métodos custom adicionales.
- **Dependencias:** `org.springframework.data.jpa.repository.JpaRepository`, entidad JPA, `java.util.UUID`

### `EstudianteMapper.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.estudiante.persistence`
- **Tipo:** `@Component`
- **Responsabilidad:** Mapea `EstudianteJpaEntity` ↔ `EstudianteAggregate`.
- **Métodos principales:**
    - `EstudianteAggregate toDomain(EstudianteJpaEntity entity)` — usa `EstudianteAggregate.reconstruir(...)`
    - `EstudianteJpaEntity toJpaEntity(EstudianteAggregate aggregate)` — NO se usa en esta HU (la réplica es read-only para el contexto `fichas`)
- **Dependencias:** aggregate, JPA entity, `@Component`

---

## 8. Endpoints REST

### Estado del endpoint

- [x] **Endpoint EXISTENTE** — modificar el adapter ya presente en el proyecto.
    - **Archivo a modificar:** `fichas/src/main/java/com/arquisoft/fichas/infrastructure/fichaPerfil/command/adapter/in/web/RegistrarFichaPerfilInputAdapter.java`
    - **Qué cambia:** el `RegistrarFichaPerfilRequestDTO` (input del endpoint) se extiende con el campo `List<UUID> estudiantesIds` (nullable, máx. 3 elementos). El endpoint ya existente NO cambia en firma HTTP (sigue siendo `POST /api/fichas-perfil` con `201 Created` + `UUID` en body + header `Location`), pero ahora acepta la lista de UUIDs de estudiantes en el request body. La lógica de validación y persistencia de las relaciones se maneja en el use case (ya documentada en sección 7), no en el controller.

### Contrato del endpoint

| Método | Ruta | Request Body / Params | Response | Código HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|----------------------|----------|-------------|----------------------|-------------------------------|
| POST (write A) | `/api/fichas-perfil` | `RegistrarFichaPerfilRequestDTO` (extendido con `estudiantesIds`) | `UUID` (body: id generado) + header `Location: /api/fichas-perfil/{uuid}` | 201 | `fichas:ficha-perfil:create` | `@Operation(summary="Registrar nueva ficha de perfil y asignar estudiantes")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses`: 201, 400 (estudiante no encontrado / duplicado / límite excedido / validación Jakarta), 401, 403 |

> **Notas:**
> - La lista `estudiantesIds` es **opcional** (puede ser `null` o vacía si no se asignan estudiantes en esta creación).
> - Si `estudiantesIds` tiene más de 3 elementos, la validación Jakarta con `@Size(max = 3)` lanza 400 antes de llegar al use case.
> - Si algún UUID no existe en la tabla `estudiante`, se lanza `EstudianteNoEncontradoException` (400).
> - Si hay duplicados en la lista, se lanza `EstudianteDuplicadoException` (400).
> - La respuesta NO cambia: retorna el UUID de la ficha creada (el cliente puede asumir que las relaciones se persistieron correctamente si el status es 201).

---

## 9. Seguridad y Autorización (Keycloak)

> **Sin cambios** — el client role `fichas:ficha-perfil:create` ya existe (creado en HU previa). La extensión de la funcionalidad (asignar estudiantes) usa el mismo permiso que crear la ficha.

### Client roles nuevos a crear en Keycloak

**Ninguno** — se reutiliza el client role existente.

---

## 10. Eventos RabbitMQ

**Eventos: ninguno.**

> Esta HU NO emite eventos. Ni la asignación de estudiantes ni `FichaPerfilAggregate` emiten eventos de dominio (la ficha es CRUD sin `AggregateRoot`; `RegistrarFichaPerfilUseCase` no inyecta `EventPublisher`). Si un contexto futuro necesita saber que un estudiante fue asignado a una ficha, esa HU introducirá el evento correspondiente (ej. `EstudianteAsignadoAFichaEvent`).

---

## 11. Migración de Base de Datos

- **Archivo:** `fichas/infrastructure/src/main/resources/db/migration/fichas/V1.1__crear_estudiante_y_estudiante_ficha_perfil.sql` (siguiente número secuencial tras la `V1.0` existente; `1.1 > 1` baseline → se ejecuta. En el subdirectorio `fichas/` — el `FichasDataSourceConfig` carga `classpath:db/migration/fichas`; una migración fuera de ese subdir NO se ejecuta)
- **Base de datos:** `fichas_perfil` (BD del contexto `fichas` según tabla de mapeo del skill)
- **Sin schemas:** las tablas se crean sin prefijo
- **Sin FKs cruzadas entre BDs:** las tablas `estudiante` y `estudiante_ficha_perfil` viven en la misma BD que `ficha_perfil` — todas las FKs son intra-BD (no hay dependencia de otro contexto en Flyway).
- **Cambios:**
    1. Crear tabla `estudiante` (réplica local sin `UNIQUE` en `email`):
        ```sql
        CREATE TABLE estudiante (
            id UUID PRIMARY KEY,
            nombre VARCHAR(100) NOT NULL,
            email VARCHAR(255) NOT NULL
        );
        CREATE INDEX idx_estudiante_id ON estudiante(id);
        ```
    2. Crear tabla `estudiante_ficha_perfil` (relación con constraint `UNIQUE`):
        ```sql
        CREATE TABLE estudiante_ficha_perfil (
            id UUID PRIMARY KEY,
            ficha_perfil_id UUID NOT NULL,
            estudiante_id UUID NOT NULL,
            CONSTRAINT fk_estudiante_ficha_perfil_ficha
                FOREIGN KEY (ficha_perfil_id) REFERENCES ficha_perfil(id) ON DELETE CASCADE,
            CONSTRAINT fk_estudiante_ficha_perfil_estudiante
                FOREIGN KEY (estudiante_id) REFERENCES estudiante(id) ON DELETE CASCADE,
            CONSTRAINT uq_ficha_estudiante UNIQUE (ficha_perfil_id, estudiante_id)
        );
        CREATE INDEX idx_estudiante_ficha_perfil_ficha ON estudiante_ficha_perfil(ficha_perfil_id);
        CREATE INDEX idx_estudiante_ficha_perfil_estudiante ON estudiante_ficha_perfil(estudiante_id);
        ```

> **Nota sobre `ON DELETE CASCADE`:** si se elimina una ficha o un estudiante, las relaciones se eliminan automáticamente (previene huérfanos en BD). Si la HU de eliminación tiene reglas de negocio específicas, puede desactivarse CASCADE y validar manualmente en el use case.

---

## 12. Casos de Prueba Sugeridos

> **Tipo de Use Case:** Escritura (orquestación sobre endpoint existente).

### Presupuesto orientativo

| Tipo de HU | Tests esperados |
|---|---|
| Mediana (extiende endpoint existente + 2 entidades nuevas) | 25 - 50 |

**Estimación para HU-161:** ~35 tests (domain: 10, application: 8, infrastructure: 17).

---

### Tests capa `domain` (Aggregates nuevos)

> `FichaPerfilAggregate` ya tiene tests completos (HU previa) — NO se modifica, NO se testea de nuevo en esta HU.

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EstudianteFichaPerfilTest` | `debeConstruirRelacion_cuandoDatosValidos` | `crear(fichaId, estudianteId)` crea relación con UUID no nulo |
| `EstudianteFichaPerfilTest` | `debeReconstruirSinValidar_cuandoReconstruirEsInvocado` | `reconstruir(...)` no valida, no genera UUID |
| `EstudianteFichaPerfilTest` | `debeLanzarDomainValidationException_cuandoFichaIdEsNull` | constructor lanza si `fichaPerfilId` es null |
| `EstudianteFichaPerfilTest` | `debeLanzarDomainValidationException_cuandoEstudianteIdEsNull` | constructor lanza si `estudianteId` es null |
| `EstudianteTest` | `debeReconstruir_cuandoDatosValidos` | `reconstruir(...)` crea `EstudianteAggregate` con campos no nulos |
| `EstudianteTest` | `debeReconstruirSinValidar_cuandoReconstruirEsInvocado` | `reconstruir(...)` no valida (asume datos ya válidos de BD) |

> **NO se testea** `getUnPublishedEvents()` ni `drainUnPublishedEvents()` en `EstudianteFichaPerfilTest` porque `EstudianteFichaPerfilAggregate` NO extiende `AggregateRoot` — es una relación CRUD sin eventos. Tampoco hay tests de ciclo de eventos para `FichaPerfilAggregate`: es una clase plana que no extiende `AggregateRoot` ni emite eventos.

---

### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `RegistrarFichaPerfilUseCaseTest` | `debeAsignarEstudiantes_cuandoListaValida` | flujo exitoso con 1, 2 o 3 estudiantes (consolidado en un test parametrizado o 3 tests separados) — verifica que se llama `estudianteFichaPerfilOutputPort.guardar(...)` N veces |
| `RegistrarFichaPerfilUseCaseTest` | `debeCrearFichaSinEstudiantes_cuandoListaEsNull` | lista de estudiantes es `null` — ficha se crea sin asignaciones |
| `RegistrarFichaPerfilUseCaseTest` | `debeCrearFichaSinEstudiantes_cuandoListaEsVacia` | lista de estudiantes es vacía — ficha se crea sin asignaciones |
| `RegistrarFichaPerfilUseCaseTest` | `debeLanzarLimiteExcedido_cuandoMasDeTresEstudiantes` | lista con 4 UUIDs → lanza `LimiteEstudiantesExcedidoException` |
| `RegistrarFichaPerfilUseCaseTest` | `debeLanzarEstudianteNoEncontrado_cuandoUUIDNoExiste` | mock de `EstudianteQueryOutputPort.existsById(...)` → `false` → lanza `EstudianteNoEncontradoException` |
| `RegistrarFichaPerfilUseCaseTest` | `debeLanzarEstudianteDuplicado_cuandoUUIDRepetidoEnLista` | lista con el mismo UUID dos veces → lanza `EstudianteDuplicadoException` con el UUID del duplicado |
| `RegistrarFichaPerfilUseCaseTest` | `debePersistirRelaciones_despuesDePersistirFicha` | verifica orden de ejecución: `fichaPerfilOutputPort.guardar(...)` ANTES de `estudianteFichaPerfilOutputPort.guardar(...)` (usar `InOrder` de Mockito) |

> **NO crear tests separados** para "errorCode correcto cuando límite excedido" y "mensaje correcto cuando límite excedido" — consolidar en el test `debeLanzarLimiteExcedido_cuandoMasDeTresEstudiantes` con múltiples asserts (tipo + errorCode + mensaje). Aplica igual a las otras excepciones.

---

### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EstudianteFichaPerfilCommandOutputAdapterTest` | `debeGuardar_cuandoRelacionValida` | persistencia OK — verifica que `jpaRepository.save(...)` se llamó con entidad correcta |
| `EstudianteFichaPerfilCommandOutputAdapterTest` | `debeRetornarTrue_cuandoRelacionExiste` | `existePorFichaYEstudiante(...)` retorna `true` si la relación ya existe en BD |
| `EstudianteFichaPerfilCommandOutputAdapterTest` | `debeRetornarFalse_cuandoRelacionNoExiste` | `existePorFichaYEstudiante(...)` retorna `false` si la relación no existe |
| `RegistrarFichaPerfilInputAdapterTest` | `debe201_cuandoPeticionValidaConEstudiantes` | request válido con lista de 1-3 estudiantes → retorna `201 Created` + UUID + header `Location` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe201_cuandoPeticionValidaSinEstudiantes` | request válido con lista `null` o vacía → retorna `201 Created` (sin asignaciones) |
| `RegistrarFichaPerfilInputAdapterTest` | `debe400_cuandoMasDeTresEstudiantes` | request con 4 UUIDs en la lista → validación Jakarta falla (`@Size(max = 3)`) → retorna `400 Bad Request` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe400_cuandoEstudianteNoExiste` | request válido pero uno de los UUIDs no existe → lanza `EstudianteNoEncontradoException` → retorna `400` con errorCode `ESTUDIANTE_NO_ENCONTRADO` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe400_cuandoEstudianteDuplicadoEnLista` | request válido pero UUID repetido en la lista → lanza `EstudianteDuplicadoException` → retorna `400` con errorCode `ESTUDIANTE_DUPLICADO` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe401_cuandoNoAutenticado` | sin token JWT → retorna `401 Unauthorized` |
| `RegistrarFichaPerfilInputAdapterTest` | `debe403_cuandoRolInsuficiente` | token válido pero sin client role `fichas:ficha-perfil:create` → retorna `403 Forbidden` |
| `EstudianteFichaPerfilJpaRepositoryTest` | `debeRetornarTrue_cuandoRelacionExiste` | `existsByFichaPerfilIdAndEstudianteId(...)` con datos existentes → `true` |
| `EstudianteFichaPerfilJpaRepositoryTest` | `debeRetornarFalse_cuandoRelacionNoExiste` | `existsByFichaPerfilIdAndEstudianteId(...)` con datos no existentes → `false` |
| `EstudianteFichaPerfilJpaRepositoryTest` | `debeGuardarConUniqueConstraint_cuandoParNuevo` | guardar relación nueva → OK |
| `EstudianteFichaPerfilJpaRepositoryTest` | `debeLanzarDataIntegrityViolation_cuandoDuplicadoEnBD` | intentar guardar par `(fichaId, estudianteId)` duplicado → lanza `DataIntegrityViolationException` (protección defensiva — el use case previene esto, pero el constraint BD es última línea de defensa) |
| `EstudianteJpaRepositoryTest` | `debeRetornarTrue_cuandoEstudianteExiste` | `existsById(UUID)` con UUID existente → `true` |
| `EstudianteJpaRepositoryTest` | `debeRetornarFalse_cuandoEstudianteNoExiste` | `existsById(UUID)` con UUID no existente → `false` |

---

### Reglas de consolidación

- **Mismo "Act", distintos "Assert"** → consolidar en un solo test con múltiples asserts (no 2 tests).
- **NO incluir tests de getters/setters** generados por Lombok en JPA Entities.
- **NO incluir tests de validaciones Jakarta** una por una — un solo test "rechaza request inválido con más de 3 estudiantes" basta.
- **NO incluir test propio de excepción** si la excepción solo hace `super("CODE", "msg")`.

---

## 13. Checklist de Implementación

- [ ] **DDD:** `FichaPerfilAggregate` es una clase plana que **NO** extiende `AggregateRoot` (CRUD sin eventos; ya existe, sin cambios en esta HU)
- [ ] `EstudianteFichaPerfilAggregate` es clase plana con factories `crear` / `reconstruir`, sin Lombok, sin extender `AggregateRoot` (relación CRUD sin eventos)
- [ ] `EstudianteAggregate` es clase plana con factory `reconstruir` (réplica read-only, sin eventos)
- [ ] IDs siempre `UUID` en todas las entidades nuevas
- [ ] Puertos de entrada y salida definidos (`EstudianteFichaPerfilOutputPort`)
- [ ] Excepciones nuevas (`EstudianteNoEncontradoException`, `EstudianteDuplicadoException`, `LimiteEstudiantesExcedidoException`) extienden `ApplicationException` (400) y tienen `errorCode` del catálogo `shared:message`
- [ ] `RegistrarFichaPerfilRequestDTO` extendido con `List<UUID> estudiantesIds` + `@Size(max = 3)`
- [ ] `RegistrarFichaPerfilCommand` extendido con `List<UUID> estudiantesIds`
- [ ] Caso de uso modificado con validación de límite, existencia y duplicados antes de persistir relaciones
- [ ] Lógica de asignación de estudiantes va DESPUÉS de `fichaPerfil.guardar(...)`, dentro de la misma transacción (no hay drenado de eventos — la ficha no emite)
- [ ] Controller REST sin cambios de firma HTTP (sigue siendo `POST /api/fichas-perfil` con `201 Created` + UUID + header `Location`)
- [ ] Entidades JPA (`EstudianteFichaPerfilJpaEntity`, `EstudianteJpaEntity`) con constraints correctos (`UNIQUE` en par ficha-estudiante, NO unique en `estudiante.email`)
- [ ] Migración Flyway (`V1.1__crear_estudiante_y_estudiante_ficha_perfil.sql`) en `db/migration/fichas/`, crea tablas sin prefijo de schema, con FKs y constraint `UNIQUE`
- [ ] Catálogo `shared:message` modificado con nested classes `Estudiante` y `EstudianteFichaPerfil` + constantes de límites, códigos y mensajes
- [ ] Sin eventos RabbitMQ (la HU no emite eventos propios y `FichaPerfilAggregate` tampoco — es una clase plana sin `AggregateRoot`)
- [ ] Tests unitarios con patrón AAA: domain (7), application (8), infrastructure (17) = ~32 tests (cobertura ≥ 75%)
- [ ] Tests de repositorio con H2
- [ ] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4.x)
- [ ] Commit: `feat(fichas): asignar estudiantes a ficha de perfil (HU-161)`

---

## 14. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-06-22 | Build -x test: sin errores |
| Tests      | @tester             | ⏳ Pendiente |       |       |
| Validación | @validator-analyze  | ⏳ Pendiente |       |       |
| Reporte    | @validator-report   | ⏳ Pendiente |       |       |
| Commit     | @commit             | ⏳ Pendiente |       |       |
