# PLAN: HU036-Consultar todos los estados ficha

## Metadata
- **ID Historia:** HU-036
- **Bounded Context:** fichas
- **Tipo de Use Case:** Consulta
- **Â¿Usa AggregateRoot?:** No â€” `EstadoFicha` es un enum de dominio con catÃ¡logo en BD (ADR-012), no un Aggregate Root. La consulta lee directamente desde la tabla catÃ¡logo sin tocar el aggregate.
- **MÃ³dulos Gradle afectados:** `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-07-01
- **Rama sugerida:** `feature/HU-036-consultar-estados-ficha`
- **Fuentes consultadas del repo de documentaciÃ³n:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
- **Skill arquisoft-context cargado:** âœ…
- **Observaciones del usuario:** Sin observaciones adicionales. Consulta pura sin filtro ni paginaciÃ³n, sin eventos de dominio, reutiliza tabla `estado_ficha` existente.

---

## 1. Resumen Funcional

Esta HU expone un endpoint REST que retorna **todos los estados ficha** disponibles en el catÃ¡logo, sin paginaciÃ³n ni filtros. La tabla `estado_ficha` ya existe (creada en migraciÃ³n V1.2) y estÃ¡ poblada con 5 registros. El enum `EstadoFicha` en el dominio ya existe. Esta HU **no crea ni modifica** recursos â€” solo agrega la capa de consulta (`application/query` + `infrastructure/query`) para exponer el catÃ¡logo vÃ­a REST a los actores autorizados (Estudiante, RepresentanteComiteCurriculum, AsesorFicha). **No emite eventos de dominio** â€” es una consulta pura.

**Lo que cubre:** endpoint `GET /fichas-perfil/estados-ficha` que retorna lista completa de estados.

**Lo que NO cubre:** filtros, paginaciÃ³n, creaciÃ³n de nuevos estados (el catÃ¡logo es fijo, poblado por migraciÃ³n).

---

## 2. Criterios de AceptaciÃ³n

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | Usuario autenticado con client role `fichas:estado-ficha:view` solicita `GET /fichas-perfil/estados-ficha` | Retorna 200 OK con lista JSON de todos los estados ficha (5 registros: `EN_CONSTRUCCION`, `EN_REVISION`, `DISPONIBLE_PARA_EVALUACION`, `APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA`), cada uno con `id`, `nombre` y `descripcion` |
| 2 | Usuario no autenticado solicita el endpoint | Retorna 401 Unauthorized |
| 3 | Usuario autenticado sin el client role requerido solicita el endpoint | Retorna 403 Forbidden |
| 4 | El orden de los estados en la respuesta es consistente | La consulta NO especifica ORDER BY â€” el orden depende del motor de BD (PostgreSQL). Si se requiere orden especÃ­fico, documentar en la secciÃ³n 8 |

---

## 3. Reglas de Negocio

- El catÃ¡logo de estados ficha es **de solo lectura** vÃ­a endpoint REST â€” los valores se insertan/modifican Ãºnicamente por migraciones Flyway.
- Todos los estados existentes se retornan sin excepciÃ³n (no hay filtro, no hay paginaciÃ³n).
- El `id` del catÃ¡logo es la constante del enum (`EstadoFicha.name()`) en SCREAMING_CASE (ADR-012).

---

## 4. Modelo DDD del Contexto

### Aggregate Root

**No aplica** â€” `EstadoFicha` es un **enum de dominio + catÃ¡logo con PK semÃ¡ntica (ADR-012)**, no un Aggregate Root. El enum ya existe en `domain/estadoficha/EstadoFicha.java`. La consulta lee directamente desde la tabla catÃ¡logo sin reconstruir un aggregate.

### Atributos por objeto de dominio

#### `EstadoFicha` (catÃ¡logo de solo lectura)

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `id` | `VARCHAR(50)` | â€” | SÃ­ | No | SÃ­ (PK semÃ¡ntica = constante del enum) | Identifica el estado. Valor = `EstadoFicha.name()` (ej. `"EN_CONSTRUCCION"`) |
| `nombre` | `String` | 1-20 | SÃ­ | No | No | Nombre legible del estado (ej. `"En Construccion"`) |
| `descripcion` | `String` | 1-200 | SÃ­ | No | No | DescripciÃ³n breve del estado |

**Combinaciones Ãºnicas (Restricciones):**
- Nombre Ãºnico: `nombre` â†’ traducciÃ³n: `UNIQUE` constraint `uk_estado_ficha_nombre` (ya existe en migraciÃ³n V1.2).

### TraducciÃ³n del modelo enriquecido a cÃ³digo

**Esta HU no modifica el modelo de dominio.** Solo agrega la capa de consulta:

| CaracterÃ­stica del modelo | TraducciÃ³n en cÃ³digo |
|---|---|
| Enum ya existente | `EstadoFicha.java` en `domain/estadoficha/` â€” **NO se modifica** |
| JPA Entity ya existente | `EstadoFichaJpaEntity.java` â€” **NO se modifica** |
| Tabla ya poblada | MigraciÃ³n V1.2 (`V1.2__crear_estado_ficha_y_estado_ficha_perfil.sql`) â€” **NO se modifica** |
| ReadModel nuevo | `EstadoFichaReadModel` en `application/estadoficha/query/readmodel/` â€” **se crea** |
| QueryOutputPort nuevo | `EstadoFichaQueryOutputPort` en `application/estadoficha/query/port/out/` â€” **se crea** |
| QueryInputPort nuevo | `ConsultarEstadosFichaInputPort` en `application/estadoficha/query/port/in/` â€” **se crea** |
| QueryUseCase nuevo | `ConsultarEstadosFichaUseCase` en `application/estadoficha/query/` â€” **se crea** |
| QueryOutputAdapter nuevo | `EstadoFichaQueryOutputAdapter` en `infrastructure/estadoficha/query/adapter/out/persistence/` â€” **se crea** |
| QueryInputAdapter nuevo | `ConsultarEstadosFichaInputAdapter` en `infrastructure/estadoficha/query/adapter/in/web/` â€” **se crea** |

### Estados y tipos: planearlos como enum de dominio

`EstadoFicha` ya estÃ¡ planificado como enum de dominio + catÃ¡logo con PK semÃ¡ntica (ADR-012) desde HU-206. Esta HU **no modifica** esa estructura â€” solo la expone vÃ­a REST.

### Eventos de Dominio que emite

```
Eventos: ninguno.
RazÃ³n: consulta pura de catÃ¡logo de solo lectura sin consumidores conocidos ni
        casos de auditorÃ­a identificados. El Event Storming menciona un evento
        "Todos los estados ficha consultados", pero esto es solo un query endpoint
        sin side effects â€” NO se publica evento RabbitMQ.
Implicaciones:
  - `EstadoFicha` NO es un Aggregate Root que extienda `AggregateRoot` â€” es un
    enum de dominio.
  - El use case NO inyecta `EventPublisher`, no hay drenado de eventos.
  - No se crean archivos en `domain/estadoficha/event/`.
```

---

## 5. Integraciones Externas (solo si la HU lo requiere)

**No aplica** â€” la HU solo lee de PostgreSQL (tabla `estado_ficha` ya existente). No requiere integraciones externas.

---

## 6. Ãrbol de Archivos a Crear / Modificar

### Archivos NUEVOS â€” caso de uso read (Consultar)

| Capa | Ruta completa desde raÃ­z del monorepo | Tipo | Responsabilidad |
|------|---------------------------------------|------|-----------------|
| application | `fichas/src/main/java/com/arquisoft/fichas/application/estadoficha/query/readmodel/EstadoFichaReadModel.java` | `record` | ProyecciÃ³n plana del estado ficha con `id`, `nombre`, `descripcion`. Factory `fromDomain(EstadoFicha enum)` que mapea `enum.getId()` â†’ `id`, `enum.getNombre()` â†’ `nombre` + `descripcion` hardcodeada (porque el enum NO tiene campo descripciÃ³n â€” la descripciÃ³n vive solo en BD). **Alternativa:** si el enum no tiene descripciÃ³n, el ReadModel se construye desde la JPA Entity directamente en el adapter, NO desde el enum. Ver secciÃ³n 7 para decisiÃ³n. |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/estadoficha/query/port/in/ConsultarEstadosFichaInputPort.java` | Interface (vacÃ­a) | Extiende `InputPort<Void, List<EstadoFichaReadModel>>` de `shared:domain` |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/estadoficha/query/port/out/EstadoFichaQueryOutputPort.java` | Interface | Puerto de salida read. MÃ©todo: `List<EstadoFichaReadModel> findAll()` |
| application | `fichas/src/main/java/com/arquisoft/fichas/application/estadoficha/query/ConsultarEstadosFichaUseCase.java` | UseCase | `@Component` + `@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")`. Inyecta `EstadoFichaQueryOutputPort`. Implementa `ConsultarEstadosFichaInputPort`. MÃ©todo `ejecutar(Void input)` delega a `port.findAll()` y retorna la lista |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoficha/query/adapter/in/web/ConsultarEstadosFichaInputAdapter.java` | `@RestController` | Mapeo: `GET /fichas-perfil/estados-ficha`. Inyecta `ConsultarEstadosFichaInputPort`. Serializa `List<EstadoFichaReadModel>` directamente a JSON (sin DTO intermedio). ADR-011: `@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement(name="bearerAuth")` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoficha/query/adapter/out/persistence/EstadoFichaQueryOutputAdapter.java` | Adapter | Implementa `EstadoFichaQueryOutputPort`. Inyecta `EstadoFichaJpaRepository` (ya existe). MÃ©todo `findAll()` consulta todos los registros de la tabla catÃ¡logo, mapea cada `EstadoFichaJpaEntity` a `EstadoFichaReadModel` y retorna lista. **DecisiÃ³n de mapeo:** como el enum `EstadoFicha` NO tiene campo `descripcion`, el mapeo es JpaEntity â†’ ReadModel directamente (`new EstadoFichaReadModel(entity.getId(), entity.getNombre(), entity.getDescripcion())`), NO pasa por el enum. |

### CatÃ¡logo de mensajes (`shared:message`) â€” fila obligatoria si la HU introduce texto nuevo

**Sin cambios al catÃ¡logo `shared:message`** â€” la HU no introduce ningÃºn mensaje de error, cÃ³digo, log ni lÃ­mite nuevo. Es una consulta pura que retorna el catÃ¡logo sin validaciones adicionales.

### Archivos a MODIFICAR (si aplica)

**Ninguno** â€” todos los archivos necesarios son nuevos. La tabla, JPA Entity, JPA Repository y enum ya existen y NO requieren modificaciÃ³n.

### Archivos de MENSAJERÃA RabbitMQ (si aplica)

**No aplica** â€” la HU no publica ni consume eventos RabbitMQ.

### OrquestaciÃ³n de use cases (cuando una HU coordina varias acciones)

**No aplica** â€” la HU solo coordina una acciÃ³n: consultar todos los estados ficha.

---

## 7. Detalle por Archivo

### `EstadoFichaReadModel.java`
- **Paquete:** `com.arquisoft.fichas.application.estadoficha.query.readmodel`
- **Tipo:** `record`
- **Responsabilidad:** ProyecciÃ³n plana del estado ficha con 3 campos: `id` (String), `nombre` (String), `descripcion` (String).
- **Features Java 21 aplicables:** `record` para inmutabilidad + `equals`/`hashCode` gratis.
- **MÃ©todos principales:**
    - Constructor canÃ³nico: `EstadoFichaReadModel(String id, String nombre, String descripcion)`
    - **NO tiene factory `fromDomain(EstadoFicha enum)`** porque el enum NO contiene el campo `descripcion`. El adapter construye el ReadModel directamente desde la JPA Entity.
- **Dependencias:** ninguna (Java puro).

---

### `ConsultarEstadosFichaInputPort.java`
- **Paquete:** `com.arquisoft.fichas.application.estadoficha.query.port.in`
- **Tipo:** Interface (vacÃ­a)
- **Responsabilidad:** Puerto de entrada para consultar todos los estados ficha. Extiende `InputPort<Void, List<EstadoFichaReadModel>>` de `shared:domain`.
- **Features Java 21 aplicables:** ninguna.
- **MÃ©todos principales:** hereda `List<EstadoFichaReadModel> ejecutar(Void input)` de `InputPort`.
- **Dependencias:**
    - `com.arquisoft.shared.port.in.InputPort`
    - `EstadoFichaReadModel`
    - `java.util.List`

---

### `EstadoFichaQueryOutputPort.java`
- **Paquete:** `com.arquisoft.fichas.application.estadoficha.query.port.out`
- **Tipo:** Interface
- **Responsabilidad:** Puerto de salida read para consultar el catÃ¡logo de estados ficha.
- **Features Java 21 aplicables:** ninguna.
- **MÃ©todos principales:**
    - `List<EstadoFichaReadModel> findAll()` â€” retorna todos los estados del catÃ¡logo sin filtro ni paginaciÃ³n.
- **Dependencias:**
    - `EstadoFichaReadModel`
    - `java.util.List`

---

### `ConsultarEstadosFichaUseCase.java`
- **Paquete:** `com.arquisoft.fichas.application.estadoficha.query`
- **Tipo:** UseCase
- **Responsabilidad:** Implementa el puerto de entrada. Delega al `EstadoFichaQueryOutputPort` y retorna la lista de ReadModels.
- **Features Java 21 aplicables:** ninguna relevante.
- **MÃ©todos principales:**
    - `List<EstadoFichaReadModel> ejecutar(Void input)` â€” llama a `queryOutputPort.findAll()` y retorna la lista sin transformaciÃ³n adicional.
- **Dependencias:**
    - `ConsultarEstadosFichaInputPort` (implementa)
    - `EstadoFichaQueryOutputPort` (inyectado)
    - `EstadoFichaReadModel`
    - `@Component`, `@RequiredArgsConstructor`, `@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")`

---

### `ConsultarEstadosFichaInputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.in.web`
- **Tipo:** `@RestController`
- **Responsabilidad:** Endpoint REST `GET /fichas-perfil/estados-ficha`. Serializa la lista de ReadModels directamente a JSON.
- **Features Java 21 aplicables:** ninguna.
- **MÃ©todos principales:**
    - `ResponseEntity<List<EstadoFichaReadModel>> consultarEstadosFicha()` â€” invoca `inputPort.ejecutar(null)`, retorna `200 OK` + lista JSON.
- **Dependencias:**
    - `ConsultarEstadosFichaInputPort` (inyectado)
    - `EstadoFichaReadModel`
    - `@RestController`, `@RequiredArgsConstructor`, `@RequestMapping("/fichas-perfil")`, `@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`, `@PreAuthorize("hasAuthority('fichas:estado-ficha:view')")`, `@GetMapping("/estados-ficha")`

#### Plantilla extendida para Controllers (ADR-011)

- **`@Tag`:** `name = "Fichas de Perfil"`, `description = "GestiÃ³n del ciclo de vida de las fichas de perfil"`
- **Endpoints documentados:**

  | MÃ©todo del Controller | `@Operation(summary)` | CÃ³digos `@ApiResponse` | `@SecurityRequirement` |
  |-----------------------|-----------------------|------------------------|------------------------|
  | `consultarEstadosFicha` | `"Consultar todos los estados ficha"` | 200, 401, 403 | `bearerAuth` |

- **Nota:** Este controller NO maneja 404 â€” la consulta siempre retorna al menos la lista vacÃ­a (aunque en prÃ¡ctica retorna 5 registros porque la tabla estÃ¡ poblada por migraciÃ³n). Tampoco maneja 400 â€” no hay parÃ¡metros de entrada que puedan ser invÃ¡lidos.

---

### `EstadoFichaQueryOutputAdapter.java`
- **Paquete:** `com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.out.persistence`
- **Tipo:** Adapter
- **Responsabilidad:** Implementa `EstadoFichaQueryOutputPort`. Consulta la tabla catÃ¡logo `estado_ficha` y mapea cada JPA Entity a ReadModel.
- **Features Java 21 aplicables:** ninguna.
- **MÃ©todos principales:**
    - `List<EstadoFichaReadModel> findAll()` â€” invoca `jpaRepository.findAll()`, mapea cada `EstadoFichaJpaEntity` a `EstadoFichaReadModel` con `new EstadoFichaReadModel(entity.getId(), entity.getNombre(), entity.getDescripcion())` y retorna lista.
- **Dependencias:**
    - `EstadoFichaQueryOutputPort` (implementa)
    - `EstadoFichaJpaRepository` (inyectado, ya existe)
    - `EstadoFichaJpaEntity` (ya existe)
    - `EstadoFichaReadModel`
    - `@Component`, `@RequiredArgsConstructor`

---

## 8. Endpoints REST (si aplica)

### Estado del endpoint

- [x] **Endpoint NUEVO** â€” crear `ConsultarEstadosFichaInputAdapter.java` desde cero.

### Contrato del endpoint

| MÃ©todo | Ruta | Request Body / Params | Response | CÃ³digo HTTP | Client role requerido | Anotaciones Swagger (ADR-011) |
|--------|------|----------------------|----------|-------------|----------------------|-------------------------------|
| GET | `/fichas-perfil/estados-ficha` | â€” | `List<EstadoFichaReadModel>` con 3 campos por estado: `id` (String), `nombre` (String), `descripcion` (String). Ejemplo: `[{"id":"EN_CONSTRUCCION","nombre":"En Construccion","descripcion":"Se refiere a que la ficha de perfil se encuentra en construcciÃ³n o desarrollo."},...]` | 200 | `fichas:estado-ficha:view` | `@Operation(summary="Consultar todos los estados ficha")` + `@SecurityRequirement(name="bearerAuth")` + `@ApiResponses`: 200 OK, 401 Unauthorized, 403 Forbidden |

**Observaciones sobre orden:**
- La consulta **NO especifica `ORDER BY`** â€” el orden de los registros depende del motor de BD (PostgreSQL). Si se requiere orden especÃ­fico (ej. alfabÃ©tico por `nombre`, o por severidad semÃ¡ntica), se debe agregar `.orderBy(...)` en el `JpaRepository` o especificar `@Query` con `ORDER BY`.

---

## 9. Seguridad y AutorizaciÃ³n (Keycloak)

### Client roles nuevos a crear en Keycloak

| Client role | Roles realm que lo poseen | Endpoint(s) que lo requieren | DescripciÃ³n funcional |
|---|---|---|---|
| `fichas:estado-ficha:view` | `estudiante`, `representante-comite`, `asesor-ficha` | `GET /fichas-perfil/estados-ficha` | Permite consultar el catÃ¡logo completo de estados ficha disponibles para asignar a una ficha de perfil |

### Reglas de uso

1. **Formato del client role:** `fichas:estado-ficha:view` â€” todo en minÃºsculas, palabras separadas por guiones.
2. **ConversiÃ³n de nombres:** el paquete Java es `estadoficha` (sin guiÃ³n), pero el client role usa guiÃ³n (`estado-ficha`) por convenciÃ³n REST/kebab-case.
3. **Roles realm en kebab-case:** `estudiante`, `representante-comite`, `asesor-ficha`.
4. Un client role pertenece a varios roles realm (los tres actores pueden ejecutar esta consulta).
5. El endpoint tiene exactamente un `@PreAuthorize("hasAuthority('fichas:estado-ficha:view')")`.
6. NO se usa `hasRole(...)` â€” siempre `hasAuthority(...)` con client role.

### ConfiguraciÃ³n requerida en Keycloak (instrucciones para equipo de seguridad)

Para el client role `fichas:estado-ficha:view`:

1. En el cliente `arquisoft-api`: crear el client role con el nombre exacto `fichas:estado-ficha:view`.
2. Asignar el client role a cada uno de los roles realm: `estudiante`, `representante-comite`, `asesor-ficha`.
3. Verificar que los usuarios de prueba con esos roles realm reciban el client role en su JWT (`resource_access.arquisoft-api.roles`).

---

## 10. Eventos RabbitMQ (si aplica)

**Eventos: ninguno.**

RazÃ³n: consulta pura de catÃ¡logo de solo lectura. El Event Storming menciona un evento "Todos los estados ficha consultados", pero esto NO se traduce en un evento RabbitMQ â€” es solo una acciÃ³n de lectura sin side effects.

---

## 11. MigraciÃ³n de Base de Datos (si aplica)

**No se requiere migraciÃ³n nueva.**

- La tabla `estado_ficha` ya existe (creada en migraciÃ³n V1.2: `V1.2__crear_estado_ficha_y_estado_ficha_perfil.sql`).
- La tabla estÃ¡ poblada con 5 registros: `APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA`, `EN_CONSTRUCCION`, `DISPONIBLE_PARA_EVALUACION`.
- Esta HU solo agrega la capa de consulta â€” NO modifica el schema.

---

## 12. Casos de Prueba Sugeridos (condicional segÃºn tipo de Use Case)

### Presupuesto orientativo

**Tipo de HU:** PequeÃ±a (1 endpoint read, 1 entidad de catÃ¡logo).

**Tests esperados:** 10 - 15.

---

### Caso B â€” Use Case de CONSULTA (listar, buscar, obtener)

#### Tests capa `domain`

**No aplica** â€” `EstadoFicha` es un enum, no un Aggregate Root. No hay lÃ³gica de dominio que testear en esta HU (el enum ya existe y NO se modifica).

#### Tests capa `application`

| Clase de test | MÃ©todo | Escenario |
|---------------|--------|-----------|
| `ConsultarEstadosFichaUseCaseTest` | `debeRetornarListaCompleta_cuandoExistenEstados` | El use case delega al `queryOutputPort.findAll()` y retorna la lista de ReadModels sin transformaciÃ³n. Mock del puerto retorna lista con 5 elementos, verificar que el use case retorna la misma lista |
| `ConsultarEstadosFichaUseCaseTest` | `debeRetornarListaVacia_cuandoNoHayEstados` | Mock del puerto retorna lista vacÃ­a, verificar que el use case retorna lista vacÃ­a (caso improbable porque la tabla estÃ¡ poblada por migraciÃ³n, pero el cÃ³digo debe manejarlo) |

#### Tests capa `infrastructure`

| Clase de test | MÃ©todo | Escenario |
|---------------|--------|-----------|
| `EstadoFichaQueryOutputAdapterTest` | `debeRetornarListaDeReadModels_cuandoFindAllEsInvocado` | Mock de `EstadoFichaJpaRepository.findAll()` retorna lista de 3 `EstadoFichaJpaEntity`, verificar que el adapter mapea correctamente a 3 `EstadoFichaReadModel` con `id`, `nombre`, `descripcion` correctos |
| `EstadoFichaQueryOutputAdapterTest` | `debeRetornarListaVacia_cuandoRepositorioRetornaVacio` | Mock retorna lista vacÃ­a, verificar que adapter retorna lista vacÃ­a |
| `ConsultarEstadosFichaInputAdapterTest` | `debe200_cuandoConsultaExitosa` | Mock del `inputPort.ejecutar(null)` retorna lista con 2 ReadModels, verificar que controller retorna `200 OK` + body JSON con 2 elementos |
| `ConsultarEstadosFichaInputAdapterTest` | `debe401_cuandoNoAutenticado` | Sin token JWT, verificar que controller retorna `401 Unauthorized` |
| `ConsultarEstadosFichaInputAdapterTest` | `debe403_cuandoRolInsuficiente` | Token JWT vÃ¡lido pero sin el client role `fichas:estado-ficha:view`, verificar que controller retorna `403 Forbidden` |

**Test de integraciÃ³n adicional (opcional, recomendado):**

| Clase de test | MÃ©todo | Escenario |
|---------------|--------|-----------|
| `EstadoFichaJpaRepositoryIntegrationTest` | `debeBuscarTodosLosEstados_cuandoTablaEstaPoblada` | Test con `@SpringBootTest` + H2. Poblar tabla `estado_ficha` con 5 registros (replicando datos de migraciÃ³n V1.2), invocar `jpaRepository.findAll()`, verificar que retorna 5 elementos con IDs, nombres y descripciones correctas |

---

### Reglas de consolidaciÃ³n

- **No incluir tests de getters/setters** generados por Lombok â€” el ReadModel es un `record`, no tiene lÃ³gica custom.
- **No incluir tests de validaciones Jakarta** â€” no hay validaciones en esta HU (no hay request DTO).
- **No incluir tests de mÃ©todos `private`** â€” el adapter no tiene mÃ©todos privados relevantes.
- **No incluir test propio de excepciÃ³n** â€” esta HU no define excepciones nuevas.
- **No incluir tests de ciclo de eventos** â€” esta HU es consulta pura, NO emite eventos.

---

## 13. Checklist de ImplementaciÃ³n

- [x] **DDD:** `EstadoFicha` NO es un Aggregate Root â€” es un enum de dominio + catÃ¡logo con PK semÃ¡ntica (ADR-012). Ya existe, NO se modifica.
- [x] IDs siempre `VARCHAR(50)` en catÃ¡logo (PK semÃ¡ntica = constante del enum), ya implementado en migraciÃ³n V1.2.
- [x] Puerto de entrada (`ConsultarEstadosFichaInputPort`) definido â€” extiende `InputPort<Void, List<EstadoFichaReadModel>>`.
- [x] Puerto de salida (`EstadoFichaQueryOutputPort`) definido â€” vive en `application/`, no en `domain/` (read side no toca aggregate).
- [x] `ReadModel` (`record` en `application/estadoficha/query/readmodel/`) creado con 3 campos: `id`, `nombre`, `descripcion`.
- [x] Caso de uso (`ConsultarEstadosFichaUseCase`) con `@RequiredArgsConstructor`, `@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")`.
- [x] Controller REST documentado con `@Tag`, `@Operation`, `@ApiResponses` y `@SecurityRequirement` (ADR-011).
- [x] Controller con autorizaciÃ³n vÃ­a `@PreAuthorize("hasAuthority('fichas:estado-ficha:view')")` en kebab-case â€” client role declarado en secciÃ³n 9.
- [x] JPA Entity (`EstadoFichaJpaEntity`) y JPA Repository (`EstadoFichaJpaRepository`) ya existen â€” NO se modifican.
- [x] MigraciÃ³n Flyway NO requerida â€” tabla `estado_ficha` ya creada y poblada en V1.2.
- [x] Eventos RabbitMQ: ninguno â€” esta HU no publica ni consume eventos.
- [x] Tests unitarios con patrÃ³n AAA (cobertura â‰¥ 75%), **sin** tests de ciclo de eventos (no aplica a consultas).
- [x] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4.x).
- [x] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot).
- [x] Commit: `feat(fichas): consultar todos los estados ficha sin filtro`

---

## 14. Trazabilidad del Flujo

> Esta secciÃ³n es actualizada automÃ¡ticamente por cada agente al completar su etapa.
> No modificar manualmente.

| Etapa      | Agente              | Estado       | Fecha | Notas |
|------------|---------------------|--------------|-------|-------|
| PlanificaciÃ³n | @plan-agent     | âœ… Completado | 2026-07-01 | Plan generado |
| Desarrollo | @implementador      | âœ… Completado | 2026-07-01 | 6 archivos nuevos (application: ReadModel, InputPort, OutputPort, UseCase; infrastructure: InputAdapter REST, OutputAdapter persistencia). Reutiliza EstadoFicha enum, EstadoFichaJpaEntity/Repository existentes. Sin migraciÃ³n ni eventos. Compila y pasa checkstyle. |
| Tests      | @tester             | âœ… Completado | 2026-07-01 | 3 archivos de test generados: ConsultarEstadosFichaUseCaseTest (application, 2 tests), EstadoFichaQueryOutputAdapterTest (infrastructure, 3 tests), ConsultarEstadosFichaInputAdapterTest (infrastructure, 7 tests). Total: 12 tests unitarios. Suite completa pasa: `./gradlew fichas:application:test fichas:infrastructure:test` â€” BUILD SUCCESSFUL. Sin tests de domain (no aplica: EstadoFicha es enum, no AggregateRoot). PatrÃ³n AAA, nomenclatura `debeHacerAlgo_cuandoCondicion`, cobertura de casos exitosos + errores de autorizaciÃ³n (401/403) + lista vacÃ­a. |
| Validación | @validator-analyze  | ✅ Completado | 2026-07-01 | Score: 98/100 — APROBADO |
| Reporte    | @validator-report   | ✅ Completado | 2026-07-01 | /.workspace/validator/validator-HU-036.md |
| Commit     | @commit             | ✅ Completado | 2026-07-01 | Hash: 02b3759 |

