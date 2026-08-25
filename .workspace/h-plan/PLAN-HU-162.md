# PLAN: HU162 - Remover información de un estudiante de una ficha perfil

## Metadata
- **ID Historia:** HU-162
- **Bounded Context:** fichas
- **Tipo de Use Case:** Escritura (eliminación)
- **¿Usa AggregateRoot?:** No — la HU NO emite eventos de dominio (CRUD simple sin consumidores conocidos ni casos de auditoría). `EstudianteFichaPerfilAggregate` es una clase plana con factories `crear`/`reconstruir`, sin extensión de `AggregateRoot`.
- **Módulos Gradle afectados:** `fichas:domain`, `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-07-03
- **Rama sugerida:** `feature/HU-162-remover-estudiante-ficha-perfil`
- **Fuentes consultadas del repo de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
- **Skill arquisoft-context cargado:** ✅
- **Observaciones del usuario:** Ninguna

---

## 1. Resumen Funcional

Esta HU permite al Coordinador remover un estudiante previamente asignado a una ficha de perfil, eliminando la relación en la tabla `estudiante_ficha_perfil`. La operación es una **eliminación física permanente** (DELETE SQL) y **NO emite eventos de dominio** — es un CRUD interno sin consumidores externos ni casos de auditoría identificados.

La HU valida que: (1) la ficha perfil existe, (2) el estudiante existe en la réplica local, y (3) la relación estudiante-ficha existe antes de eliminarla. Si alguna validación falla, devuelve 400 Bad Request. Si la eliminación es exitosa, responde con 204 No Content.

**NO cubre:** eliminación lógica (flag activo/inactivo), remoción masiva de estudiantes en una sola request, ni emisión de eventos para réplicas en otros contextos.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | El Coordinador invoca `DELETE /fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}` con un token válido | La relación se elimina físicamente de la tabla `estudiante_ficha_perfil` y responde 204 No Content |
| 2 | La ficha perfil no existe en BD | Responde 400 Bad Request con código `FICHA_PERFIL_NO_ENCONTRADA` |
| 3 | El estudiante no existe en la réplica local de `fichas` | Responde 400 Bad Request con código `ESTUDIANTE_NO_ENCONTRADO` |
| 4 | La relación estudiante-ficha no existe (el estudiante nunca estuvo asignado a esa ficha) | Responde 400 Bad Request con código `ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO` |
| 5 | El usuario autenticado no tiene el client role `fichas:estudiante-ficha-perfil:delete` | Responde 403 Forbidden |
| 6 | El usuario no está autenticado | Responde 401 Unauthorized |

---

## 3. Reglas de Negocio

- **EstudianteFichaPerfil-POL-1:** Asegurar que los datos requeridos para llevar a cabo la acción sean válidos a nivel de tipo de dato, longitud, obligatoriedad, formato y rango (aplica a los UUIDs de entrada).
- **EstudianteFichaPerfil-POL-4:** Asegurar que el estudiante vinculado a la Ficha Perfil exista por el identificador (se valida en la réplica local `estudiante` del contexto `fichas`).
- **Regla implícita:** Validar que la ficha perfil existe antes de intentar eliminar la relación (evita SQL errors y garantiza coherencia).
- **Regla implícita:** Validar que la relación estudiante-ficha existe antes de eliminar (evita DELETE exitoso con 0 filas afectadas reportado como éxito falso).

---

## 4. Modelo DDD del Contexto

### Aggregate Root
- **Entidad raíz:** `EstudianteFichaPerfilAggregate`
- **¿Extiende `AggregateRoot` de `shared:domain`?:** No — la HU NO emite eventos de dominio. Es una clase plana con factories `crear`/`reconstruir`, sin maquinaria de eventos. **Ya existe** (creado en HU-161) y ya tiene esta estructura — no se modifica en esta HU.
- **ID:** `UUID`

> **Coherencia verificada:** La entidad NO extiende `AggregateRoot` (confirmado leyendo el archivo `EstudianteFichaPerfilAggregate.java` existente), y esta HU NO emite eventos. No hay contradicción.

### Atributos por objeto de dominio

#### `EstudianteFichaPerfil`

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | Identifica el registro. Generado por `UtilUUID.generateNewUUID()` en `setId()` |
| `fichaPerfilId` | `UUID` | — | Sí | No | No | FK a `ficha_perfil`. Referencia a la ficha perfil a la que pertenece el estudiante |
| `estudianteId` | `UUID` | — | Sí | No | No | FK a `estudiante` (réplica local). Referencia al estudiante asignado |

**Combinaciones únicas (Restricciones):**
- Combinación única `(fichaPerfilId, estudianteId)` → traducción: `UNIQUE` constraint `uk_estudiante_ficha` en Flyway (ya existe), validación de unicidad en use case de asignación (HU-161).

> **Nota:** Esta HU NO crea la tabla ni el aggregate — ambos existen desde HU-161. Solo se añade el método de negocio `remover()` al aggregate (ver sección 6).

### Traducción del modelo enriquecido a código

Ya implementada en HU-161. No hay cambios en atributos ni constraints.

### Eventos de Dominio que emite

**Eventos: ninguno.**

**Razón:** CRUD interno sin consumidores conocidos ni casos de auditoría identificados. La eliminación de la relación estudiante-ficha no desencadena acciones en otros contextos ni requiere registro para observabilidad.

**Implicaciones:**
- La entidad raíz `EstudianteFichaPerfilAggregate` NO extiende `AggregateRoot` — es una clase plana con factories `crear`/`reconstruir`.
- El factory `crear(...)` NO acumula eventos (no existe `publicarEvento`).
- El use case `RemoverEstudianteFichaPerfilUseCase` NO inyecta `EventPublisher`, no hay drenado de eventos.
- No se crean archivos en `domain/estudiantefichaperfil/event/`.

---

## 5. Integraciones Externas

No aplica. La HU solo interactúa con PostgreSQL (tabla `estudiante_ficha_perfil` del schema `fichas_perfil`) y no requiere Keycloak API, SMTP, S3 ni servicios HTTP externos más allá de la autenticación estándar vía JWT.

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos NUEVOS

| Capa | Ruta completa desde raíz del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/model/RemoverEstudianteFichaPerfilCommand.java` | `record` | Intención de negocio. Campos: `fichaPerfilId` (UUID), `estudianteId` (UUID) |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/port/in/RemoverEstudianteFichaPerfilInputPort.java` | Interface (vacía) | Extiende `VoidInputPort<RemoverEstudianteFichaPerfilCommand>` de `shared:domain` (respuesta 204 No Content) |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/RemoverEstudianteFichaPerfilUseCase.java` | UseCase | `@Component` que implementa el `InputPort`. Patrón: validar ficha → validar estudiante → validar relación existe → eliminar → log |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estudiantefichaperfil/exception/EstudianteFichaPerfilNoEncontradoException.java` | Exception del use case | Extiende `ApplicationException` (400 Bad Request). Se lanza cuando la relación estudiante-ficha no existe en BD (no se puede remover lo que no está asignado) |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/RemoverEstudianteFichaPerfilInputAdapter.java` | `@RestController` | Inyecta el `InputPort`. Retorna `ResponseEntity<Void>` con `204 No Content`. ADR-011 (`@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`) |
| application | `fichas/application/src/test/java/com/arquisoft/fichas/application/estudiantefichaperfil/command/RemoverEstudianteFichaPerfilUseCaseTest.java` | Test unitario | Tests del use case con Mockito (`@ExtendWith(MockitoExtension.class)`) |
| infrastructure | `fichas/infrastructure/src/test/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/in/web/RemoverEstudianteFichaPerfilInputAdapterTest.java` | Test integración | Test del controller con `@SpringBootTest` + Spring Security Test |

### Archivos a MODIFICAR

| Ruta completa | Cambio requerido |
|---------------|-----------------|
| `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estudiantefichaperfil/port/out/EstudianteFichaPerfilOutputPort.java` | Agregar método `void eliminar(UUID fichaPerfilId, UUID estudianteId);` para eliminar físicamente la relación. **También agregar** método `boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId);` si no existe (HU-161 debió crearlo — **verificar antes de agregar**, si ya existe no duplicar; si falta, agregarlo en esta HU) |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/command/adapter/out/persistence/EstudianteFichaPerfilCommandOutputAdapter.java` | Implementar el nuevo método `eliminar(...)` del puerto. Usa `jpaRepository.deleteByFichaPerfilIdAndEstudianteId(...)` (método derivado de Spring Data) |
| `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estudiantefichaperfil/persistence/EstudianteFichaPerfilJpaRepository.java` | Agregar método derivado `void deleteByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);` (Spring Data lo genera automáticamente por convención de nombre) |
| `shared/message/src/main/java/com/arquisoft/shared/message/FichasMessages.java` | Modificar nested class `EstudianteFichaPerfil`: agregar constantes nuevas (ver tabla de inventario abajo) |

### Catálogo de mensajes (`shared:message`) — constantes a agregar

| Constante | Sección | Tipo | Valor | Usado por |
|---|---|---|---|---|
| `FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO` | Códigos de error | `String` | `"ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO"` | `EstudianteFichaPerfilNoEncontradoException` (errorCode) |
| `FichasMessages.EstudianteFichaPerfil.RELACION_NO_ENCONTRADA_MSG` | Mensajes de error | `String` | `"La relación entre el estudiante %s y la ficha perfil %s no existe"` | `EstudianteFichaPerfilNoEncontradoException` (mensaje, `.formatted(estudianteId, fichaPerfilId)`) |
| `FichasMessages.EstudianteFichaPerfil.LOG_REMOVIDO` | Logs | `String` | `"Estudiante removido de ficha perfil — fichaPerfilId={}, estudianteId={}"` | `log.info` en `RemoverEstudianteFichaPerfilUseCase` |

> **Nota:** Las constantes `CAMPO_FICHA_PERFIL_ID`, `CAMPO_ESTUDIANTE_ID`, `FICHA_PERFIL_ID_REQUERIDO`, `ESTUDIANTE_ID_REQUERIDO` ya existen desde HU-161 (asignación). No se duplican.

### Archivos de MENSAJERÍA RabbitMQ

No aplica. Esta HU NO emite eventos de dominio — sin config de Spring Modulith ni outbox.

---

## 7. Detalle por Archivo

### `RemoverEstudianteFichaPerfilCommand.java`
- **Paquete:** `com.arquisoft.fichas.application.estudiantefichaperfil.command.model`
- **Tipo:** `record` (Command)
- **Responsabilidad:** Intención de negocio para remover un estudiante de una ficha perfil. Recibe los UUIDs de la ficha y del estudiante.
- **Features Java 21 aplicables:** `record` para Command (inmutabilidad + `equals`/`hashCode` gratis)
- **Campos:**
    - `fichaPerfilId`: `UUID` — ID de la ficha perfil de la que se removerá el estudiante
    - `estudianteId`: `UUID` — ID del estudiante a remover
- **Dependencias:** `java.util.UUID`

---

### `RemoverEstudianteFichaPerfilInputPort.java`
- **Paquete:** `com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in`
- **Tipo:** Interface (puerto de entrada)
- **Responsabilidad:** Contrato del caso de uso de remoción. Extiende `VoidInputPort<RemoverEstudianteFichaPerfilCommand>` (respuesta void = 204 No Content).
- **Métodos principales:**
    - *(heredado)* `void ejecutar(RemoverEstudianteFichaPerfilCommand command)` — ejecuta el caso de uso sin retornar valor
- **Dependencias:** `VoidInputPort` (de `shared:domain`), `RemoverEstudianteFichaPerfilCommand`

---

### `RemoverEstudianteFichaPerfilUseCase.java`
- **Paquete:** `com.arquisoft.fichas.application.estudiantefichaperfil.command`
- **Tipo:** UseCase (`@Component`)
- **Responsabilidad:** Orquesta la eliminación de la relación estudiante-ficha tras validar invariantes.
- **Features Java 21 aplicables:** `var` para variables locales evidentes
- **Métodos principales:**
    - `void ejecutar(RemoverEstudianteFichaPerfilCommand command)` — valida → elimina → log. **Sin drenado de eventos** (la HU NO emite eventos).
- **Flujo:**
    1. Validar que la ficha perfil existe (`fichaPerfilOutputPort.existsById(fichaPerfilId)`) → lanza `FichaPerfilNoEncontradaException` si no existe (400).
    2. Validar que el estudiante existe (`estudianteQueryOutputPort.existsById(estudianteId)`) → lanza `EstudianteNoEncontradoException` si no existe (400).
    3. Validar que la relación existe (`estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(...)`) → lanza `EstudianteFichaPerfilNoEncontradoException` si no existe (400).
    4. Eliminar físicamente la relación (`estudianteFichaPerfilOutputPort.eliminar(fichaPerfilId, estudianteId)`).
    5. Log (`log.info(FichasMessages.EstudianteFichaPerfil.LOG_REMOVIDO, fichaPerfilId, estudianteId)`).
- **Dependencias:**
    - `FichaPerfilOutputPort` (validación existencia ficha)
    - `EstudianteQueryOutputPort` (validación existencia estudiante)
    - `EstudianteFichaPerfilOutputPort` (validación existencia relación + eliminación)
    - `FichaPerfilNoEncontradaException`, `EstudianteNoEncontradoException`, `EstudianteFichaPerfilNoEncontradoException`
    - `FichasMessages.EstudianteFichaPerfil` (constantes de log)
- **Anotaciones:** `@Component`, `@RequiredArgsConstructor`, `@Slf4j`, `@Transactional(transactionManager = "fichasTransactionManager")`

---

### `EstudianteFichaPerfilNoEncontradoException.java`
- **Paquete:** `com.arquisoft.fichas.application.estudiantefichaperfil.exception`
- **Tipo:** Exception (`ApplicationException`)
- **Responsabilidad:** Excepción cuando la relación estudiante-ficha no existe en BD (no se puede remover lo que no está asignado). Mapea a 400 Bad Request vía `GlobalAppExceptionHandler` de `shared:web`.
- **Métodos principales:**
    - Constructor: `EstudianteFichaPerfilNoEncontradoException(UUID estudianteId, UUID fichaPerfilId)` — llama a `super(mensaje, errorCode)` con:
        - `mensaje = FichasMessages.EstudianteFichaPerfil.RELACION_NO_ENCONTRADA_MSG.formatted(estudianteId, fichaPerfilId)`
        - `errorCode = FichasMessages.EstudianteFichaPerfil.ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO`
- **Dependencias:** `ApplicationException` (de `shared:exception`), `FichasMessages.EstudianteFichaPerfil`

---

### `RemoverEstudianteFichaPerfilInputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web`
- **Tipo:** Controller (`@RestController`)
- **Responsabilidad:** Endpoint REST `DELETE /fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}`. Retorna `204 No Content` tras eliminación exitosa.
- **Features Java 21 aplicables:** —
- **Métodos principales:**
    - `ResponseEntity<Void> remover(@PathVariable UUID fichaPerfilId, @PathVariable UUID estudianteId)` — construye el `Command` y llama al `InputPort`. Retorna `ResponseEntity.noContent().build()` (204).
- **Dependencias:** `RemoverEstudianteFichaPerfilInputPort`, `RemoverEstudianteFichaPerfilCommand`
- **Anotaciones:**
    - `@RestController`, `@RequestMapping("/fichas-perfil")`, `@RequiredArgsConstructor`
    - `@Tag(name = "Fichas", description = "Gestión de fichas de perfil")` (ADR-011)
    - En el método:
        - `@DeleteMapping("/{fichaPerfilId}/estudiantes/{estudianteId}")`
        - `@PreAuthorize("hasAuthority('fichas:estudiante-ficha-perfil:delete')")`
        - `@Operation(summary = "Remover estudiante de ficha perfil")` (ADR-011)
        - `@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Estudiante removido exitosamente"), @ApiResponse(responseCode = "400", description = "Ficha, estudiante o relación no encontrada"), @ApiResponse(responseCode = "401", description = "No autenticado"), @ApiResponse(responseCode = "403", description = "Sin permiso para remover estudiantes") })` (ADR-011)
        - `@SecurityRequirement(name = "bearerAuth")` (ADR-011)

#### Plantilla extendida para Controllers (ADR-011)

- **`@Tag`:** `name = "Fichas"`, `description = "Gestión de fichas de perfil"`
- **Endpoints documentados:**

  | Método del Controller | `@Operation(summary)` | Códigos `@ApiResponse` | `@SecurityRequirement` |
  |-----------------------|-----------------------|------------------------|------------------------|
  | `remover` | `"Remover estudiante de ficha perfil"` | 204, 400, 401, 403 | `bearerAuth` |

---

### `EstudianteFichaPerfilOutputPort.java` (MODIFICAR)
- **Cambio:** Agregar método `void eliminar(UUID fichaPerfilId, UUID estudianteId);` para eliminar físicamente la relación.
- **Verificar antes de modificar:** si el método `boolean existePorFichaYEstudiante(UUID fichaPerfilId, UUID estudianteId);` no existe (HU-161 debió crearlo), agregarlo también.

---

### `EstudianteFichaPerfilCommandOutputAdapter.java` (MODIFICAR)
- **Cambio:** Implementar el nuevo método `eliminar(...)` del puerto. Delegación:
    ```java
    @Override
    public void eliminar(UUID fichaPerfilId, UUID estudianteId) {
        jpaRepository.deleteByFichaPerfilIdAndEstudianteId(fichaPerfilId, estudianteId);
    }
    ```

---

### `EstudianteFichaPerfilJpaRepository.java` (MODIFICAR)
- **Cambio:** Agregar método derivado de Spring Data:
    ```java
    void deleteByFichaPerfilIdAndEstudianteId(UUID fichaPerfilId, UUID estudianteId);
    ```
- **Nota:** Spring Data JPA genera la implementación automáticamente por convención de nombre. El método ejecuta `DELETE FROM estudiante_ficha_perfil WHERE ficha_perfil_id = ? AND estudiante_id = ?`. **Debe anotarse con `@Transactional` en el repositorio** para que el DELETE sea efectivo (Spring Data repositories son `@Transactional` por defecto en métodos modificadores).

---

### `FichasMessages.java` (MODIFICAR)
- **Cambio:** En la nested class `EstudianteFichaPerfil`, agregar las 3 constantes nuevas listadas en la tabla de inventario (sección 6).
- **Ubicación exacta:** agregar en las secciones correspondientes:
    - Sección `// Códigos de error`: `ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO`
    - Sección `// Mensajes de error`: `RELACION_NO_ENCONTRADA`
    - Sección `// Logs`: `LOG_REMOVIDO`

---

## 8. Endpoints REST

### Estado del endpoint

- [x] **Endpoint NUEVO** — crear `RemoverEstudianteFichaPerfilInputAdapter.java` desde cero.

### Contrato del endpoint

| Método | Ruta | Request Body / Params | Response | Código HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|----------------------|----------|-------------|----------------------|-------------------------------|
| DELETE | `/fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}` | `@PathVariable UUID fichaPerfilId`, `@PathVariable UUID estudianteId` | `Void` (sin body) | 204 No Content | `fichas:estudiante-ficha-perfil:delete` | `@Operation(summary="Remover estudiante de ficha perfil")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses` (204, 400, 401, 403) |

> **Convención de respuesta (write opción B):** `ResponseEntity<Void>` con `204 No Content`, sin body ni header `Location` (estándar REST para DELETE exitoso). El use case implementa `VoidInputPort<Command>` de `shared:domain.port.in`.

---

## 9. Seguridad y Autorización (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Roles realm que lo poseen | Endpoint(s) que lo requieren | Descripción funcional |
|---|---|---|---|
| `fichas:estudiante-ficha-perfil:delete` | `coordinador` | `DELETE /fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}` | Permite remover un estudiante de una ficha de perfil existente |

### Reglas de uso

1. **Formato del client role:** `fichas:estudiante-ficha-perfil:delete` — todo en minúscula, palabras del recurso separadas por guiones (`estudiante-ficha-perfil`), verbo `delete`.
2. **Roles realm:** solo `coordinador`.
3. **Un client role, un endpoint:** el client role `fichas:estudiante-ficha-perfil:delete` se valida únicamente en este endpoint.
4. **NO se usa `hasRole(...)`** ni roles realm directamente en el endpoint — siempre `hasAuthority(...)` con client role.

### Configuración requerida en Keycloak (instrucciones para equipo de seguridad)

1. En el cliente `arquisoft-api`: crear el client role `fichas:estudiante-ficha-perfil:delete`.
2. Asignar el client role al rol realm `coordinador`.
3. Verificar que los usuarios de prueba con rol realm `coordinador` reciban el client role en su JWT (`resource_access.arquisoft-api.roles`).

---

## 10. Eventos RabbitMQ

**Eventos: ninguno.**

Esta HU NO emite eventos de dominio (CRUD interno sin consumidores conocidos ni casos de auditoría). No hay publicación a RabbitMQ. El use case NO inyecta `EventPublisher`.

---

## 11. Migración de Base de Datos

No aplica. La tabla `estudiante_ficha_perfil` ya existe (creada en HU-161). No se crean columnas ni tablas nuevas. La operación de esta HU es un `DELETE` SQL estándar sobre la tabla existente.

---

## 12. Casos de Prueba Sugeridos

> **Tipo de Use Case:** Escritura (eliminación). Incluir tests de dominio, application e infrastructure para operaciones de escritura, pero **sin tests de ciclo de eventos** (la HU NO emite eventos).

### Presupuesto orientativo

| Tipo de HU | Tests esperados |
|---|---|
| Pequeña (1 endpoint, 1 entidad) | 15 - 25 |

Estimación para esta HU: **~18 tests** (6 domain/application + 12 infrastructure).

---

### Tests capa `application`

> **Nota:** No hay tests de dominio nuevos porque `EstudianteFichaPerfilAggregate` NO se modifica en esta HU (ya existe desde HU-161 y NO tiene método de negocio `remover()` — la eliminación es puramente orquestal en el use case).

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `RemoverEstudianteFichaPerfilUseCaseTest` | `debeRemover_cuandoRelacionExiste` | Flujo exitoso: ficha existe, estudiante existe, relación existe → se elimina la relación y se loguea |
| `RemoverEstudianteFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoFichaNoExiste` | Lanza `FichaPerfilNoEncontradaException` (400) cuando `fichaPerfilOutputPort.existsById(...)` retorna `false` |
| `RemoverEstudianteFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoEstudianteNoExiste` | Lanza `EstudianteNoEncontradoException` (400) cuando `estudianteQueryOutputPort.existsById(...)` retorna `false` |
| `RemoverEstudianteFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoRelacionNoExiste` | Lanza `EstudianteFichaPerfilNoEncontradoException` (400) cuando `estudianteFichaPerfilOutputPort.existePorFichaYEstudiante(...)` retorna `false` |
| `RemoverEstudianteFichaPerfilUseCaseTest` | `debeLanzarExcepcion_cuandoRepositorioFalla` | Propaga error de repositorio (ej. `DataAccessException`) sin envolverla |

> **NO incluir:** tests de `publicarEvento`, `obtenerEventosSinPublicar`, `extraerEventosSinPublicar`, ni `verify(eventPublisher).publish(...)` — esta HU NO emite eventos.

---

### Tests capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EstudianteFichaPerfilCommandOutputAdapterTest` (MODIFICAR el test existente) | `debeEliminar_cuandoRelacionExiste` | Verifica que el adapter llama a `jpaRepository.deleteByFichaPerfilIdAndEstudianteId(...)` con los UUIDs correctos |
| `EstudianteFichaPerfilJpaRepositoryTest` (MODIFICAR el test existente) | `debeEliminar_cuandoRelacionExisteEnBD` | Test de integración con H2: inserta la relación, llama a `deleteByFichaPerfilIdAndEstudianteId(...)`, verifica que ya no existe con `findById(...)` |
| `RemoverEstudianteFichaPerfilInputAdapterTest` | `debe204_cuandoPeticionValida` | DELETE exitoso retorna 204 No Content sin body |
| `RemoverEstudianteFichaPerfilInputAdapterTest` | `debe400_cuandoFichaNoExiste` | Mock del use case lanza `FichaPerfilNoEncontradaException` → responde 400 con código `FICHA_PERFIL_NO_ENCONTRADA` |
| `RemoverEstudianteFichaPerfilInputAdapterTest` | `debe400_cuandoEstudianteNoExiste` | Mock del use case lanza `EstudianteNoEncontradoException` → responde 400 con código `ESTUDIANTE_NO_ENCONTRADO` |
| `RemoverEstudianteFichaPerfilInputAdapterTest` | `debe400_cuandoRelacionNoExiste` | Mock del use case lanza `EstudianteFichaPerfilNoEncontradoException` → responde 400 con código `ESTUDIANTE_FICHA_PERFIL_NO_ENCONTRADO` |
| `RemoverEstudianteFichaPerfilInputAdapterTest` | `debe401_cuandoNoAutenticado` | Sin token JWT → 401 Unauthorized |
| `RemoverEstudianteFichaPerfilInputAdapterTest` | `debe403_cuandoRolInsuficiente` | Token válido pero sin client role `fichas:estudiante-ficha-perfil:delete` → 403 Forbidden |
| `RemoverEstudianteFichaPerfilInputAdapterTest` | `debe400_cuandoFichaPerfilIdInvalido` | `@PathVariable` con UUID mal formado → Spring responde 400 automáticamente (test verifica que el framework maneja el error, no el controller) |
| `RemoverEstudianteFichaPerfilInputAdapterTest` | `debe400_cuandoEstudianteIdInvalido` | `@PathVariable` con UUID mal formado → 400 |

> **Total estimado:** 5 tests application + 10 tests infrastructure (2 modificaciones de tests existentes + 8 nuevos) = **~15 tests nuevos/modificados**.

### Reglas de consolidación

- **NO incluyas tests de getters/setters** generados por Lombok.
- **NO incluyas tests de validaciones Jakarta** una por una (no aplican — los UUIDs vienen por `@PathVariable`, no por request body).
- **NO incluyas tests de métodos `private`**.
- **NO incluyas test propio de excepción** si la excepción solo hace `super("CODE", "msg")`.
- **Consolidar asserts:** si dos tests tienen el mismo "Act" pero distintos asserts, consolidarlos en un solo test con múltiples asserts.

---

## 13. Checklist de Implementación

- [ ] **DDD:** Entidad de dominio `EstudianteFichaPerfilAggregate` **NO extiende `AggregateRoot`** (la HU NO emite eventos — CRUD simple)
- [ ] Entidad inmutable: constructor privado, campos `final`, factory methods `crear` / `reconstruir`, sin Lombok (ya existe desde HU-161 — no se modifica)
- [ ] **NO se crean** archivos en `domain/estudiantefichaperfil/event/` — la HU NO emite eventos
- [ ] **NO se llama** `publishEvent(...)` en ningún método del aggregate ni use case
- [ ] IDs siempre `UUID` (ya cumplido desde HU-161)
- [ ] Puerto de entrada (`RemoverEstudianteFichaPerfilInputPort`) extiende `VoidInputPort<Command>` (respuesta void = 204 No Content)
- [ ] Puerto de salida (`EstudianteFichaPerfilOutputPort`) MODIFICADO con método `eliminar(UUID, UUID)` y `existePorFichaYEstudiante(UUID, UUID)` (verificar si este último ya existe)
- [ ] Excepciones de aplicación definidas, extienden `ApplicationException` (400 Bad Request) y tienen `errorCode`
- [ ] **Cada excepción nueva extiende `ApplicationException`** (la clase base correcta para duplicado/no encontrado/relación no encontrada) para que `GlobalAppExceptionHandler` de `shared:web` resuelva su HTTP 400 automáticamente. **NO se crea handler de contexto** — el manejo centralizado es suficiente.
- [ ] `Command` (`record` en `application/estudiantefichaperfil/command/model/`) creado. Campos: `fichaPerfilId` (UUID), `estudianteId` (UUID), en español.
- [ ] Caso de uso (`RemoverEstudianteFichaPerfilUseCase`) con `@RequiredArgsConstructor`, `@Transactional(transactionManager = "fichasTransactionManager")` **SIN drenado de eventos** (la HU NO emite eventos)
- [ ] Controller REST con autorización vía `@PreAuthorize("hasAuthority('fichas:estudiante-ficha-perfil:delete')")` **en kebab-case** — client role declarado en sección 9 del plan
- [ ] Controller documentado con `@Tag`, `@Operation`, `@ApiResponses` y `@SecurityRequirement` (ADR-011)
- [ ] Adaptador de repositorio MODIFICADO (`EstudianteFichaPerfilCommandOutputAdapter`) con método `eliminar(...)` que delega a `jpaRepository.deleteByFichaPerfilIdAndEstudianteId(...)`
- [ ] JPA Repository MODIFICADO (`EstudianteFichaPerfilJpaRepository`) con método derivado `void deleteByFichaPerfilIdAndEstudianteId(UUID, UUID);`
- [ ] **NO se crea migración Flyway** — la tabla `estudiante_ficha_perfil` ya existe desde HU-161
- [ ] **NO se emiten eventos RabbitMQ** — sin config de Spring Modulith ni outbox
- [ ] Catálogo de mensajes (`FichasMessages.EstudianteFichaPerfil`) MODIFICADO con 3 constantes nuevas (ver sección 6)
- [ ] Tests unitarios con patrón AAA (cobertura ≥ 75%), **sin tests de ciclo de eventos** (la HU NO emite eventos)
- [ ] Tests de repositorio con H2 (método `deleteByFichaPerfilIdAndEstudianteId(...)`)
- [ ] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4.x)
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot)
- [ ] Commit: `feat(fichas): remover estudiante de ficha perfil (HU-162)`

---

## 14. Trazabilidad del Flujo

> Esta sección es actualizada automáticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha      | Notas |
|------------|---------------------|--------------|------------|-------|
| Desarrollo | @implementador      | ✅ Completado | 2026-07-03 | Build -x test: sin errores. Código de producción completo (application + infrastructure). |
| Tests      | @tester             | ✅ Completado | 2026-07-03 | 18 tests generados (5 application + 13 infrastructure). Cobertura: application 85%, infrastructure 87% — CUMPLE gate del 75%. Checkstyle: verde. |
| Validación | @validator-analyze  | ✅ Completado | 2026-07-03 | Score: 100/100 — APROBADO (tras correcciones: 3 anti-patrones en tests resueltos) |
| Reporte    | @validator-report   | ✅ Completado | 2026-07-03 | /.workspace/validator/validator-HU-162.md |
| Commit     | @commit             | ⏳ Pendiente |            |       |
