# PLAN: Asignar estudiantes a una ficha de perfil existente (v2 — endpoint dedicado)

## Metadata
- **ID Historia:** HU-161
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura (asignar estudiantes a ficha YA existente)
- **¿Usa AggregateRoot?:** No — `FichaPerfilAggregate` es una `final class` plana, NO extiende `AggregateRoot` (es CRUD sin eventos; verificado en el código real). Esta HU no lo cambia.
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan (v1):** 2026-06-22
- **Fecha de revisión (v2):** 2026-07-02
- **Rama sugerida:** `feature/HU-161-asignar-estudiantes-ficha-perfil-v2`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md` (HU-161)
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/05_usuarios_modelo_enriquecido.md` (para réplica de Estudiante)
- **Skill arquisoft-context cargado:** ✅
- **Observaciones v2 del usuario:** La v1 (commit 69193a8) permitía asignar estudiantes SOLO al crear una ficha vía `RegistrarFichaPerfilUseCase` — imposibilitaba reasignar a fichas ya existentes (ej. cuando se libera un cupo). La v2 AÑADE un endpoint dedicado `POST /api/fichas-perfil/{fichaPerfilId}/estudiantes` para asignar estudiantes a una ficha existente (espejo del patrón `AgregarItemFichaPerfilUseCase`). `RegistrarFichaPerfilUseCase` se mantiene SIN cambios (su lista inicial opcional se conserva). Validación INLINE en el nuevo use case (NO se crea domain service). Se reutilizan las excepciones existentes de la v1. BUG CORREGIDO: límite validaba solo `tamaño_lista > 3`, ahora valida `(existentes_en_BD + nuevos) ≤ 3` con conteo vía puerto nuevo `contarPorFichaPerfilId(UUID)`.

---

## 1. Resumen Funcional

La HU-161 (v2) permite asignar estudiantes a una ficha de perfil **YA existente** mediante un endpoint dedicado `POST /api/fichas-perfil/{fichaPerfilId}/estudiantes` que recibe una lista de UUIDs (1 a 3 estudiantes). El sistema valida: (1) la ficha existe, (2) cada estudiante existe como réplica local, (3) ninguno ya está asignado a esa ficha, (4) el total de estudiantes (los ya asignados en BD + los nuevos) ≤ 3. Patrón canónico: espejo de `AgregarItemFichaPerfilUseCase` + `InputAdapter` con `@PathVariable fichaPerfilId`.

**Diferencia con la v1:** la v1 (commit 69193a8) extendía el endpoint de registrar ficha para asignar estudiantes SOLO al crear — bloqueaba reasignar cupos. La v2 mantiene esa funcionalidad SIN cambios y AÑADE el endpoint dedicado. `RegistrarFichaPerfilUseCase` NO se modifica.

Lo que NO cubre: remoción de estudiantes (es otra HU), cambio de estado de la ficha (otra HU).

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | El coordinador puede asignar entre 1 y 3 estudiantes a una ficha YA existente | El sistema persiste las relaciones en `estudiante_ficha_perfil` |
| 2 | La ficha destino debe existir | Si no existe, se lanza `FichaPerfilNoEncontradaException` (400) |
| 3 | Todos los estudiantes deben existir previamente como réplicas locales | Si alguno no existe, se lanza `EstudianteNoEncontradoException` (400) |
| 4 | Ningún estudiante ya asignado a la ficha puede aparecer en la lista nueva | Si alguno ya está asignado, se lanza `EstudianteDuplicadoException` (400) |
| 5 | Sin duplicados dentro de la lista entrante | Si se incluye el mismo UUID dos veces en la lista, se lanza `EstudianteDuplicadoException` (400) |
| 6 | Total de estudiantes (BD + nuevos) ≤ 3 | Si (ya_asignados + nuevos) > 3, lanza `LimiteEstudiantesExcedidoException` (400). Ej. 2 en BD + 2 nuevos ⇒ 400 |

---

## 3. Reglas de Negocio

- **Límite de estudiantes por ficha:** 3 (constante `ESTUDIANTES_MAX = 3` en catálogo `FichasMessages.FichaPerfil`).
- **Validación agregada del límite (v2 — BUG CORREGIDO):** el límite se aplica a **(relaciones existentes en BD) + (nuevas a crear) ≤ 3**. Por eso el use case DEBE contar las relaciones existentes con `estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfilId)` antes de persistir las nuevas. Ejemplo: si 2 estudiantes ya están asignados, solo se puede asignar 1 adicional.
- **Validación de existencia:** cada UUID de la lista debe corresponder a un `EstudianteAggregate` presente en la tabla `estudiante` del contexto `fichas` (réplica local).
- **Unicidad dentro de la lista entrante:** la lista de UUIDs debe ser sin duplicados (validación explícita con `new HashSet<>(estudiantesIds).size() != estudiantesIds.size()`).
- **Unicidad contra BD:** ningún estudiante de la lista puede estar ya asignado a la ficha (validación previa con `existePorFichaYEstudiante` para cada uno).
- **Restricción base de datos:** `UNIQUE (ficha_perfil_id, estudiante_id)` previene duplicados a nivel BD (última línea de defensa).

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
| `identificador` | `String` | 1-30 | Sí | No | No | Código/documento de identidad del estudiante (ej. código estudiantil) |
| `nombre` | `String` | 1-50 | Sí | No | No | Nombre completo del estudiante |
| `email` | `String` | 1-50 | Sí | No | No | Email institucional (único en el contexto origen, no validado como único localmente — no aplica `UNIQUE` en la tabla `estudiante` del contexto `fichas`) |

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
| `Estudiante.identificador` obligatorio, max 30 | `@Column(nullable=false, length=30)` en JPA + `NOT NULL VARCHAR(30)` en Flyway |
| `Estudiante.nombre` obligatorio, max 50 | `@Column(nullable=false, length=50)` en JPA + `NOT NULL VARCHAR(50)` en Flyway |
| `Estudiante.email` obligatorio, max 50, NO único local | `@Column(nullable=false, length=50)` en JPA + `NOT NULL VARCHAR(50)` en Flyway (SIN `UNIQUE`) |
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

> **Contexto de la v2:** los archivos de la entidad `Estudiante` (réplica local), `EstudianteFichaPerfil` (relación), JPA Entities, repositorios, mappers, excepciones, adaptadores de persistencia, catálogo de mensajes y migración Flyway YA EXISTEN — fueron creados en la v1 (commit 69193a8). **NO se vuelven a planificar como "a crear".**

### Archivos NUEVOS (solo el delta de la v2)

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/model/AsignarEstudiantesFichaPerfilCommand.java` | `record` | Comando con `UUID fichaPerfilId` + `List<UUID> estudiantesIds` (1 a 3) |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/port/in/AsignarEstudiantesFichaPerfilInputPort.java` | Interface (vacía) | Extiende `VoidInputPort<AsignarEstudiantesFichaPerfilCommand>` — retorna void, el cliente ya conoce fichaPerfilId y los ids asignados. Si el POST retorna 201, la asignación tuvo éxito. |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/AsignarEstudiantesFichaPerfilUseCase.java` | UseCase | `@Component`, `@Transactional(transactionManager = "fichasTransactionManager")`. Validaciones inline: ficha existe, sin duplicados en lista, por cada estudiante (existe + no ya vinculado), límite: `contarPorFichaPerfilId(fichaId) + estudiantesIds.size() > ESTUDIANTES_MAX` ⇒ `LimiteEstudiantesExcedidoException`. Crear cada `EstudianteFichaPerfilAggregate.crear(...)` y guardar. Sin eventos. |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/AsignarEstudiantesFichaPerfilInputAdapter.java` | `@RestController` | `POST /api/fichas-perfil/{fichaPerfilId}/estudiantes`, `@PathVariable fichaPerfilId`, `@Valid @RequestBody`, ADR-011 Swagger, `@PreAuthorize("hasAuthority('fichas:estudiante-ficha-perfil:create')")`. Patrón canónico: espejo de `AgregarItemFichaPerfilInputAdapter`. Retorna `201 Created` sin body (void). |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/dto/AsignarEstudiantesFichaPerfilRequestDTO.java` | `record` | `List<UUID> estudiantesIds` con `@NotEmpty` + `@Size(max = 3)`. Método `toCommand(UUID fichaPerfilId)` que combina el path param con el body. |

### Archivos a MODIFICAR (delta de la v2)

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudiantefichaperfil/port/out/EstudianteFichaPerfilOutputPort.java` | Añadir método `long contarPorFichaPerfilId(UUID fichaPerfilId)` (retorna la cantidad de relaciones existentes para esa ficha). |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/persistence/EstudianteFichaPerfilJpaRepository.java` | Añadir método `long countByFichaPerfilId(UUID fichaPerfilId)`. |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/out/persistence/EstudianteFichaPerfilCommandOutputAdapter.java` | Implementar `contarPorFichaPerfilId(...)` delegando al `jpaRepository.countByFichaPerfilId(...)`. |
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` (solo si hace falta) | Añadir constante de log `EstudianteFichaPerfil.LOG_ASIGNADO = "Estudiantes asignados a ficha — fichaId={}, cantidad={}"` (opcional). Reutiliza todos los códigos y mensajes de error existentes (definidos en v1). |
| `fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/command/RegistrarFichaPerfilUseCase.java` | **Ajuste de convención, sin cambio de comportamiento:** `command.estudiantesIds() != null` → `!UtilObject.isNull(command.estudiantesIds())` (regla del skill: no `== null` crudo). La lógica de registro NO cambia. |
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudiantefichaperfil/aggregate/EstudianteFichaPerfilAggregate.java` | **Ajuste de convención, sin cambio de comportamiento:** `setId()` genera el UUID con `UtilUUID.generateNewUUID()` (antes `UUID.randomUUID()` en `crear`), alineado a la regla de valores autogenerados del skill. |

---

## 7. Detalle por Archivo (delta v2 — solo archivos nuevos/modificados)

> **Archivos YA EXISTENTES de la v1 (commit 69193a8) que NO se modifican en v2:**
> - `EstudianteAggregate.java`, `EstudianteFichaPerfilAggregate.java`, `EstudianteJpaEntity.java`, `EstudianteFichaPerfilJpaEntity.java`, `EstudianteJpaRepository.java`, `EstudianteFichaPerfilMapper.java`, `EstudianteMapper.java`, `EstudianteNoEncontradoException.java`, `EstudianteDuplicadoException.java`, `LimiteEstudiantesExcedidoException.java`, `EstudianteQueryOutputPort.java`, `EstudianteQueryOutputAdapter.java`, migración `V1.1__*`.

### `AsignarEstudiantesFichaPerfilCommand.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.estudiantefichaperfil.command.model`
- **Tipo:** `record`
- **Responsabilidad:** Comando con la intención de negocio "asignar estudiantes a una ficha existente".
- **Campos:**
    ```java
    UUID fichaPerfilId,
    List<UUID> estudiantesIds
    ```
- **Dependencias:** `java.util.UUID`, `java.util.List`

### `AsignarEstudiantesFichaPerfilInputPort.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in`
- **Tipo:** Interface (vacía)
- **Responsabilidad:** Extiende `VoidInputPort<AsignarEstudiantesFichaPerfilCommand>` (de `shared:domain.inputport`).
- **Firma:**
    ```java
    public interface AsignarEstudiantesFichaPerfilInputPort extends VoidInputPort<AsignarEstudiantesFichaPerfilCommand> {
    }
    ```
- **Dependencias:** `VoidInputPort`, el `Command`

### `AsignarEstudiantesFichaPerfilUseCase.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.application.estudiantefichaperfil.command`
- **Tipo:** UseCase
- **Responsabilidad:** Implementa `AsignarEstudiantesFichaPerfilInputPort`. Valida inline: ficha existe, sin duplicados en lista, por cada estudiante (existe + no ya vinculado), límite: `(contarPorFichaPerfilId(fichaId) + estudiantesIds.size()) > ESTUDIANTES_MAX` ⇒ `LimiteEstudiantesExcedidoException`. Crea cada `EstudianteFichaPerfilAggregate.crear(...)` y persiste. Sin eventos.
- **Anotaciones:** `@Component`, `@RequiredArgsConstructor`, `@Slf4j`, `@Transactional(transactionManager = "fichasTransactionManager")`
- **Métodos principales:**
    - `void ejecutar(AsignarEstudiantesFichaPerfilCommand command)` — valida y persiste
- **Orden de validación (crítico para tests):**
    > La lista nula/vacía la rechaza `@NotEmpty` del `RequestDTO` (400) — el use case NO revalida forma de input, solo reglas de negocio.
    1. Ficha existe (`fichaPerfilOutputPort.existsById(fichaPerfilId)`) → si no, lanza `FichaPerfilNoEncontradaException`
    2. Sin duplicados dentro de la lista (`new HashSet<>(estudiantesIds).size() != estudiantesIds.size()`) → si hay, identificar el primero y lanzar `EstudianteDuplicadoException(duplicado)`
    3. Por cada estudiante en la lista:
        - Existe (`estudianteQueryOutputPort.existsById(estudianteId)`) → si no, lanza `EstudianteNoEncontradoException(estudianteId)`
        - No ya vinculado (`existePorFichaYEstudiante(fichaId, estudianteId)` → false) → si ya vinculado, lanza `EstudianteDuplicadoException(estudianteId)`
    4. Límite: `long existentes = estudianteFichaPerfilOutputPort.contarPorFichaPerfilId(fichaPerfilId)` → `if (existentes + estudiantesIds.size() > FichasMessages.FichaPerfil.ESTUDIANTES_MAX)` → lanza `LimiteEstudiantesExcedidoException()`
    5. Crear y guardar cada relación: `for (UUID estudianteId : estudiantesIds) { var relacion = EstudianteFichaPerfilAggregate.crear(fichaId, estudianteId); estudianteFichaPerfilOutputPort.guardar(relacion); }`
    6. Log: `log.info(FichasMessages.EstudianteFichaPerfil.LOG_ASIGNADO, fichaId, estudiantesIds.size());`
- **Dependencias:** puertos (`FichaPerfilOutputPort.existsById`, `EstudianteQueryOutputPort.existsById`, `EstudianteFichaPerfilOutputPort.{existePorFichaYEstudiante, contarPorFichaPerfilId, guardar}`), `EstudianteFichaPerfilAggregate`, excepciones, catálogo `FichasMessages`

### `AsignarEstudiantesFichaPerfilRequestDTO.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web.dto`
- **Tipo:** `record`
- **Responsabilidad:** DTO de request para el endpoint `POST /api/fichas-perfil/{fichaPerfilId}/estudiantes`.
- **Campos:**
    ```java
    @NotEmpty(message = "La lista de estudiantes es obligatoria y no puede estar vacía")
    @Size(max = 3, message = "No se pueden asignar más de 3 estudiantes")
    List<UUID> estudiantesIds
    ```
- **Métodos:**
    ```java
    public AsignarEstudiantesFichaPerfilCommand toCommand(UUID fichaPerfilId) {
        return new AsignarEstudiantesFichaPerfilCommand(fichaPerfilId, estudiantesIds);
    }
    ```
- **Dependencias:** `jakarta.validation.constraints.*`, `java.util.UUID`, `java.util.List`, el `Command`

### `AsignarEstudiantesFichaPerfilInputAdapter.java` (NUEVO)
- **Paquete:** `com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web`
- **Tipo:** `@RestController`
- **Responsabilidad:** Controller REST para asignar estudiantes a ficha existente. Patrón canónico: espejo de `AgregarItemFichaPerfilInputAdapter` (ruta con `@PathVariable` del ID padre).
- **Anotaciones:**
    - Clase: `@RestController`, `@RequestMapping("/fichas-perfil")`, `@RequiredArgsConstructor`, `@Slf4j`, `@Tag(name = "Fichas Perfil", description = "...")`
    - Método: `@PostMapping("/{fichaPerfilId}/estudiantes")`, `@PreAuthorize("hasAuthority('fichas:estudiante-ficha-perfil:create')")`, ADR-011 (`@Operation`, `@SecurityRequirement`, `@ApiResponses`)
- **Método principal:**
    ```java
    public ResponseEntity<Void> asignarEstudiantes(
            @PathVariable UUID fichaPerfilId,
            @Valid @RequestBody AsignarEstudiantesFichaPerfilRequestDTO dto) {
        
        asignarEstudiantesFichaPerfilInputPort.ejecutar(dto.toCommand(fichaPerfilId));
        
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    ```
- **Response:** `201 Created` sin body (void). El cliente ya conoce fichaPerfilId y estudiantesIds — si llega 201, las asignaciones tuvieron éxito.
- **Dependencias:** `AsignarEstudiantesFichaPerfilInputPort`, DTO, `org.springframework.http.*`, `io.swagger.v3.oas.annotations.*`, `jakarta.validation.Valid`, `org.springframework.security.access.prepost.PreAuthorize`

### Modificaciones a archivos existentes

#### `EstudianteFichaPerfilOutputPort.java` (MODIFICAR)
- **Cambio:** añadir método `long contarPorFichaPerfilId(UUID fichaPerfilId);`
- **Razón:** el use case necesita contar las relaciones existentes antes de validar el límite. Signature completa tras modificar:
    ```java
    void guardar(EstudianteFichaPerfilAggregate relacion);
    boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId);
    long contarPorFichaPerfilId(UUID fichaPerfilId);  // NUEVO
    ```

#### `EstudianteFichaPerfilJpaRepository.java` (MODIFICAR)
- **Cambio:** añadir método `long countByFichaPerfilId(UUID fichaPerfilId);`
- **Razón:** persistencia del conteo para el adapter. Signature completa tras modificar:
    ```java
    public interface EstudianteFichaPerfilJpaRepository extends JpaRepository<EstudianteFichaPerfilJpaEntity, UUID> {
        boolean existsByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);
        long countByFichaPerfilId(UUID fichaPerfilId);  // NUEVO
    }
    ```

#### `EstudianteFichaPerfilCommandOutputAdapter.java` (MODIFICAR)
- **Cambio:** implementar `contarPorFichaPerfilId(...)` delegando al repo:
    ```java
    @Override
    public long contarPorFichaPerfilId(UUID fichaPerfilId) {
        return jpaRepository.countByFichaPerfilId(fichaPerfilId);
    }
    ```
- **Métodos completos tras modificar:** `guardar`, `existePorFichaYEstudiante`, `contarPorFichaPerfilId`

#### `FichasMessages.java` (MODIFICAR — opcional)
- **Cambio:** añadir constante de log en la nested class `EstudianteFichaPerfil`:
    ```java
    // Logs
    public static final String LOG_ASIGNADO = "Estudiantes asignados a ficha — fichaId={}, cantidad={}";
    ```
- **Razón:** consistencia con el patrón de logs del proyecto. Si se omite, el use case puede loguear con un literal directo (no preferible, pero no bloqueante).

---

> **Archivos YA EXISTENTES de la v1 que NO se tocan en v2 (solo para referencia):**

### Referencias a archivos YA EXISTENTES de la v1 (commit 69193a8, NO se modifican en v2)

| Archivo | Ubicación | Tipo | Razón por la que NO se modifica |
|---|---|---|---|
| `EstudianteAggregate.java` | `fichas/domain/src/.../estudiante/aggregate/` | Aggregate (réplica read-only) | La entidad de dominio del estudiante ya existe y cubre todas las necesidades del contexto — el nuevo use case la inyecta vía `EstudianteQueryOutputPort.existsById` sin tocar el aggregate |
| `EstudianteFichaPerfilAggregate.java` | `fichas/domain/src/.../estudiantefichaperfil/aggregate/` | Aggregate (relación CRUD) | El factory `crear(fichaId, estudianteId)` y `reconstruir(...)` ya existen — el nuevo use case los reutiliza tal cual |
| `EstudianteNoEncontradoException.java` | `fichas/application/src/.../estudiante/exception/` | Exception (400) | Reutilizada por el nuevo use case cuando un estudiante no existe — misma semántica, mismo mensaje |
| `EstudianteDuplicadoException.java` | `fichas/application/src/.../estudiantefichaperfil/exception/` | Exception (400) | Reutilizada por el nuevo use case para duplicados en lista Y para estudiantes ya asignados — dos escenarios, misma excepción |
| `LimiteEstudiantesExcedidoException.java` | `fichas/application/src/.../estudiantefichaperfil/exception/` | Exception (400) | Reutilizada por el nuevo use case — ahora con validación agregada (existentes + nuevos) en vez de solo `nuevos.size()` |
| `EstudianteJpaEntity.java`, `EstudianteJpaRepository.java`, `EstudianteMapper.java` | `fichas/infrastructure/src/.../estudiante/persistence/` | JPA Entity, Repository, Mapper | Persistencia de la réplica local — ya existe, se reutiliza vía `EstudianteQueryOutputPort` |
| `EstudianteFichaPerfilJpaEntity.java`, `EstudianteFichaPerfilJpaRepository.java`, `EstudianteFichaPerfilMapper.java` | `fichas/infrastructure/src/.../estudiantefichaperfil/persistence/` | JPA Entity, Repository, Mapper | Persistencia de la relación — ya existe, solo se añade el método de conteo al `JpaRepository` |
| `EstudianteQueryOutputPort.java`, `EstudianteQueryOutputAdapter.java` | `fichas/application/.../query/port/out/` y `fichas/infrastructure/.../query/adapter/out/` | Puerto query + Adapter | Puerto de lectura del estudiante (lookup FK cross-aggregate) — ya existe con `existsById` que el nuevo use case inyecta |
| `V1.1__crear_estudiante_y_estudiante_ficha_perfil.sql` | `fichas/infrastructure/src/main/resources/db/migration/fichas/` | Flyway | Migración que creó las tablas `estudiante` y `estudiante_ficha_perfil` — YA ejecutada en v1, no requiere cambios de esquema |

---

## 8. Endpoints REST

### Estado del endpoint (v2)

- [x] **Endpoint NUEVO** — crear `AsignarEstudiantesFichaPerfilInputAdapter.java`.
    - **Patrón canónico:** espejo de `AgregarItemFichaPerfilInputAdapter` (POST a subruta con `@PathVariable` del ID padre).
    - **Razón:** permite asignar estudiantes a fichas YA existentes (la v1 solo permitía asignación inicial al crear la ficha).

### Contrato del endpoint

| Método | Ruta | Request Body / Params | Response | Código HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|----------------------|----------|-------------|----------------------|-------------------------------|
| POST | `/api/fichas-perfil/{fichaPerfilId}/estudiantes` | `@PathVariable UUID fichaPerfilId` + `@Valid @RequestBody AsignarEstudiantesFichaPerfilRequestDTO` (con `List<UUID> estudiantesIds`: 1 a 3 elementos) | Void (sin body) | 201 Created | `fichas:estudiante-ficha-perfil:create` | `@Operation(summary="Asignar estudiantes a ficha de perfil existente")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses`: 201, 400 (ficha no encontrada / estudiante no encontrado / duplicado en lista / ya asignado / límite excedido / validación Jakarta), 401, 403 |

> **Decisión de retorno (VoidInputPort):** el endpoint retorna `201 Created` SIN body. El cliente ya conoce `fichaPerfilId` (path param) y los UUIDs de estudiantes (envió la lista). Si el 201 llega, las relaciones se crearon. No se devuelve la lista de UUIDs de relaciones porque el cliente solo necesita saber "éxito" — es el patrón canónico del proyecto para asignaciones sin necesidad de consulta subsiguiente.

> **Notas sobre validación:**
> - La lista `estudiantesIds` es **obligatoria** y **no vacía** (`@NotEmpty`).
> - Máximo 3 elementos (`@Size(max = 3)`).
> - Si la ficha no existe, se lanza `FichaPerfilNoEncontradaException` (400).
> - Si algún estudiante no existe, se lanza `EstudianteNoEncontradoException` (400).
> - Si algún estudiante YA está asignado a la ficha, se lanza `EstudianteDuplicadoException` (400).
> - Si hay duplicados dentro de la lista entrante, se lanza `EstudianteDuplicadoException` (400).
> - Si `(estudiantes_ya_asignados_en_BD + estudiantesIds.size()) > 3`, se lanza `LimiteEstudiantesExcedidoException` (400).

---

## 9. Seguridad y Autorización (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Descripción | Roles realm asignados | Justificación |
|---|---|---|---|
| `fichas:estudiante-ficha-perfil:create` | Crear la relación estudiante-ficha (asignar estudiantes a una ficha existente) | `coordinador` | El coordinador es quien gestiona asignaciones de estudiantes a fichas — separado del permiso `fichas:ficha-perfil:create` (crear la ficha) porque asignar estudiantes puede hacerse después de la creación. |

**Convención aplicada:** formato `{contexto}:{recurso}:{accion}` en kebab-case. El recurso es `estudiante-ficha-perfil` (no `estudianteFichaPerfil` en camelCase). La acción es `create` porque se crea la relación (tabla pivot `estudiante_ficha_perfil`).

---

## 10. Eventos RabbitMQ

**Eventos: ninguno.**

> Esta HU NO emite eventos. Ni la asignación de estudiantes ni `FichaPerfilAggregate` emiten eventos de dominio (la ficha es CRUD sin `AggregateRoot`; `RegistrarFichaPerfilUseCase` no inyecta `EventPublisher`). Si un contexto futuro necesita saber que un estudiante fue asignado a una ficha, esa HU introducirá el evento correspondiente (ej. `EstudianteAsignadoAFichaEvent`).

---

## 11. Migración de Base de Datos

> **Sin cambios respecto a la v1.** La migración `V1.1__crear_estudiante_y_estudiante_ficha_perfil.sql` YA EXISTE (creada en commit 69193a8). Las tablas `estudiante` y `estudiante_ficha_perfil` ya están en la base de datos. La v2 NO requiere cambios de esquema — solo añade lógica de negocio (use case + endpoint nuevo).

---

## 12. Casos de Prueba Sugeridos (delta v2)

> **Tipo de Use Case (v2):** Escritura (nuevo endpoint dedicado para asignar estudiantes a ficha existente).

### Presupuesto orientativo

| Tipo de HU | Tests esperados |
|---|---|
| Pequeña (nuevo endpoint + use case simple + tests de validación del conteo) | 12 - 18 |

**Estimación para HU-161 v2 (solo el delta):** ~15 tests (domain: 0, application: 6, infrastructure: 9).

> **Los tests de la v1 YA EXISTEN** (aggregates, excepciones, repositorios, mappers — cubiertos en commit 69193a8). **NO se vuelven a planificar.** La v2 añade tests SOLO del nuevo use case, adapter/controller nuevo, DTO y el conteo.

---

### Tests capa `domain` (delta v2)

**Ninguno** — no se modifican aggregates ni se crean nuevos. El método `contarPorFichaPerfilId` es un puerto (interfaz), no comportamiento de aggregate.

---

### Tests capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `AsignarEstudiantesFichaPerfilUseCaseTest` | `debeAsignarEstudiantes_cuandoListaValidaYLimiteNoExcedido` | flujo exitoso: ficha con 1 estudiante ya asignado + agregar 2 nuevos → total 3 → OK. Verifica `guardar(...)` llamado 2 veces. |
| `AsignarEstudiantesFichaPerfilUseCaseTest` | `debeLanzarFichaNoEncontrada_cuandoFichaIdNoExiste` | mock `fichaPerfilOutputPort.existsById(fichaId)` → `false` → lanza `FichaPerfilNoEncontradaException` |
| `AsignarEstudiantesFichaPerfilUseCaseTest` | `debeLanzarEstudianteNoEncontrado_cuandoUUIDNoExiste` | mock `estudianteQueryOutputPort.existsById(...)` → `false` para uno → lanza `EstudianteNoEncontradoException` |
| `AsignarEstudiantesFichaPerfilUseCaseTest` | `debeLanzarEstudianteDuplicado_cuandoUUIDRepetidoEnLista` | lista con el mismo UUID dos veces → lanza `EstudianteDuplicadoException` |
| `AsignarEstudiantesFichaPerfilUseCaseTest` | `debeLanzarEstudianteDuplicado_cuandoYaAsignadoEnBD` | mock `existePorFichaYEstudiante(fichaId, estudianteId)` → `true` → lanza `EstudianteDuplicadoException` |
| `AsignarEstudiantesFichaPerfilUseCaseTest` | `debeLanzarLimiteExcedido_cuandoExistentes2MasNuevos2` | **Test del bug corregido.** Mock `contarPorFichaPerfilId(fichaId)` → `2L`, lista con 2 UUIDs → total 4 → lanza `LimiteEstudiantesExcedidoException`. Verifica que el límite valida (existentes_en_BD + nuevos) y NO solo `nuevos.size()`. |

---

### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `AsignarEstudiantesFichaPerfilInputAdapterTest` | `debe201_cuandoPeticionValidaConListaDeUno` | request con 1 estudiante nuevo, ficha tiene 0 → retorna `201 Created` sin body |
| `AsignarEstudiantesFichaPerfilInputAdapterTest` | `debe201_cuandoPeticionValidaConListaDeTres` | request con 3 estudiantes nuevos, ficha tiene 0 → retorna `201 Created` sin body |
| `AsignarEstudiantesFichaPerfilInputAdapterTest` | `debe400_cuandoListaVacia` | request con lista vacía → validación Jakarta (`@NotEmpty`) → `400 Bad Request` |
| `AsignarEstudiantesFichaPerfilInputAdapterTest` | `debe400_cuandoListaTieneMasDeTres` | request con 4 UUIDs → validación Jakarta (`@Size(max = 3)`) → `400 Bad Request` |
| `AsignarEstudiantesFichaPerfilInputAdapterTest` | `debe400_cuandoFichaNoExiste` | ficha no existe → lanza `FichaPerfilNoEncontradaException` → `400` con errorCode correspondiente |
| `AsignarEstudiantesFichaPerfilInputAdapterTest` | `debe400_cuandoEstudianteNoExiste` | uno de los UUIDs no existe → lanza `EstudianteNoEncontradoException` → `400` con errorCode `ESTUDIANTE_NO_ENCONTRADO` |
| `AsignarEstudiantesFichaPerfilInputAdapterTest` | `debe400_cuandoEstudianteDuplicadoEnLista` | UUID repetido en lista → lanza `EstudianteDuplicadoException` → `400` con errorCode `ESTUDIANTE_DUPLICADO` |
| `AsignarEstudiantesFichaPerfilInputAdapterTest` | `debe401_cuandoNoAutenticado` | sin token JWT → retorna `401 Unauthorized` |
| `AsignarEstudiantesFichaPerfilInputAdapterTest` | `debe403_cuandoRolInsuficiente` | token válido pero sin client role `fichas:estudiante-ficha-perfil:create` → retorna `403 Forbidden` |

**Adapter de persistencia y repositorio:**
- `EstudianteFichaPerfilCommandOutputAdapterTest` **YA existe** (v1). Añadir un test único:
    - `debeRetornarConteo_cuandoContarPorFichaPerfilId` | mock `jpaRepository.countByFichaPerfilId(fichaId)` → `2L` → adapter retorna `2L`

- `EstudianteFichaPerfilJpaRepositoryTest` **YA existe** (v1). Añadir un test único:
    - `debeRetornarConteo_cuandoCountByFichaPerfilId` | guardar 2 relaciones con la misma ficha, 1 con otra → `countByFichaPerfilId(fichaId)` → `2`

---

### Reglas de consolidación

- **NO re-testear** lo ya cubierto en la v1 (aggregates, mappers, excepciones existentes).
- **Consolidar** asserts del error (tipo + errorCode + mensaje) en un solo test por excepción.
- **NO tests de getters/setters** generados por Lombok.
- **NO tests de validaciones Jakarta** una por una — un test global "rechaza lista vacía" y otro "rechaza más de 3" basta.

---

## 13. Checklist de Implementación (v2)

- [ ] **DDD:** `FichaPerfilAggregate` es una clase plana que **NO** extiende `AggregateRoot` (CRUD sin eventos; **NO se modifica en v2**)
- [ ] `EstudianteFichaPerfilAggregate` es clase plana con factories `crear` / `reconstruir`, sin Lombok, sin extender `AggregateRoot` (relación CRUD sin eventos — **YA existe v1, NO se modifica**)
- [ ] `EstudianteAggregate` es clase plana con factory `reconstruir` (réplica read-only, sin eventos — **YA existe v1, NO se modifica**)
- [ ] IDs siempre `UUID` en todas las entidades (OK desde v1)
- [ ] **Puerto nuevo:** añadir `long contarPorFichaPerfilId(UUID)` a `EstudianteFichaPerfilOutputPort` (write side, en `domain/`)
- [ ] **JpaRepository:** añadir `long countByFichaPerfilId(UUID)` a `EstudianteFichaPerfilJpaRepository`
- [ ] **Adapter:** implementar `contarPorFichaPerfilId` en `EstudianteFichaPerfilCommandOutputAdapter` delegando al repo
- [ ] **Comando nuevo:** `AsignarEstudiantesFichaPerfilCommand` (`record` con `fichaPerfilId` + `estudiantesIds`)
- [ ] **InputPort nuevo:** `AsignarEstudiantesFichaPerfilInputPort` extiende `VoidInputPort<Command>` (retorna void)
- [ ] **Use case nuevo:** `AsignarEstudiantesFichaPerfilUseCase` con validación inline: ficha existe, sin duplicados en lista, por cada estudiante (existe + no ya vinculado), límite: `(contarPorFichaPerfilId + estudiantesIds.size()) ≤ 3`
- [ ] **DTO nuevo:** `AsignarEstudiantesFichaPerfilRequestDTO` (`record` con `List<UUID> estudiantesIds`, `@NotEmpty`, `@Size(max = 3)`, método `toCommand(UUID fichaPerfilId)`)
- [ ] **Controller nuevo:** `AsignarEstudiantesFichaPerfilInputAdapter` (`POST /api/fichas-perfil/{fichaPerfilId}/estudiantes`, `@PathVariable`, `@Valid @RequestBody`, ADR-011 Swagger, `@PreAuthorize("hasAuthority('fichas:estudiante-ficha-perfil:create')")`, retorna `201 Created` sin body)
- [ ] **Client role nuevo en Keycloak:** `fichas:estudiante-ficha-perfil:create` asignado a `coordinador`
- [ ] `RegistrarFichaPerfilUseCase` **NO se modifica** (decisión v2 — mantiene su lista inicial opcional tal cual)
- [ ] Catálogo `shared:message`: añadir `EstudianteFichaPerfil.LOG_ASIGNADO` (opcional) — reutilizar todos los códigos/mensajes de error existentes de v1
- [ ] Sin eventos RabbitMQ (la HU no emite eventos — coherente con v1)
- [ ] Migración Flyway **sin cambios** (tablas `estudiante` y `estudiante_ficha_perfil` ya existen desde v1)
- [ ] Tests unitarios con patrón AAA: domain (0 nuevos), application (6), infrastructure (9 + 2 en existentes) = ~17 tests nuevos (cobertura ≥ 75% sobre código nuevo)
- [ ] Tests de repositorio con H2
- [ ] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4.x)
- [ ] Commit: `feat(fichas): endpoint dedicado para asignar estudiantes a ficha existente (HU-161 v2)`

---

## 14. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

### v1 (commit 69193a8 — enfoque de orquestación)

> **Decisión arquitectónica superada:** la v1 extendía `RegistrarFichaPerfilUseCase` para asignar estudiantes SOLO al crear la ficha. Bloqueaba reasignar cupos a fichas existentes. **BUG de validación:** el límite solo miraba `estudiantesIds.size() > 3`, ignorando las relaciones existentes en BD.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-06-22 | Build -x test: sin errores |
| Tests      | @tester             | ✅ Completado | 2026-06-23 | domain (6), application (8 ext), infrastructure (5) — todos pasan |
| Validación | @validator-analyze  | ✅ Completado | 2026-06-23 | Score: 99/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-06-23 | /.workspace/validator/validator-HU-161.md |
| Commit     | @commit             | ✅ Completado | 2026-06-23 | Hash: 69193a8 |

### v2 (revisión — endpoint dedicado + bug corregido)

> **Decisión arquitectónica:** v2 añade `POST /api/fichas-perfil/{fichaPerfilId}/estudiantes` (patrón canónico espejo de `AgregarItemFichaPerfilUseCase`) para asignar a fichas existentes. `RegistrarFichaPerfilUseCase` se conserva sin cambios (lista inicial opcional). **BUG corregido:** el límite ahora valida `(ya_asignados_en_BD + nuevos) ≤ 3` con conteo vía `contarPorFichaPerfilId(UUID)`. Requiere método nuevo en puerto/repo.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| Planificación | @planificador    | ✅ Completado | 2026-07-02 | Plan v2 generado — resetea etapas subsiguientes |
| Desarrollo | @implementador      | ✅ Completado | 2026-07-02 | Build -x test: sin errores. 5 nuevos + 3 modificados. |
| Tests      | @tester             | ✅ Completado | 2026-07-02 | application (6), infrastructure (10) — todos pasan. Gate cobertura: ✅ CUMPLE |
| Validación | @validator-analyze  | ✅ Completado | 2026-07-02 | Score: 100/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-07-02 | /.workspace/validator/validator-HU-161.md |
| Commit     | @commit             | ⏸️ Pendiente | — | — |
