# PLAN HU-036 — Consultar todos los estados ficha

## Metadata

- **ID Historia:** HU-036
- **Bounded Context:** fichas
- **Tipo de Use Case:** Consulta
- **¿Usa AggregateRoot?:** No. `EstadoFicha` es un enum de dominio con catálogo en base de datos (ADR-012), no un Aggregate Root. La consulta lee directamente de la tabla catálogo sin reconstruir un aggregate.
- **Módulos Gradle afectados:** `fichas:application`, `fichas:infrastructure`
- **Fecha de plan:** 2026-07-01
- **Rama sugerida:** `feature/HU-036-consultar-estados-ficha`
- **Fuentes consultadas del repositorio de documentación:**
    - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
    - `artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md`
    - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md`
    - `mer/03_tablas_fichas_perfil.sql`
- **Skill arquisoft-context cargado:** Sí
- **Observaciones del usuario:** sin observaciones adicionales. Consulta pura, sin filtros ni paginación, sin eventos de dominio; reutiliza la tabla `estado_ficha` existente.

---

## 1. Resumen Funcional

Esta HU expone un endpoint REST que retorna **todos los estados ficha** disponibles en el catálogo, sin paginación ni filtros. La tabla `estado_ficha` ya existe (creada en la migración V1.2) y está poblada. El enum `EstadoFicha` del dominio también existe.

Esta HU **no crea ni modifica** recursos: solo agrega la capa de consulta (`application/query` + `infrastructure/query`) para exponer el catálogo vía REST a los actores autorizados (Estudiante, Representante del Comité de Currículum, Asesor de Ficha). **No emite eventos de dominio** porque es una consulta pura.

**Lo que cubre:** endpoint `GET /fichas-perfil/estados-ficha`, que retorna la lista completa de estados.

**Lo que NO cubre:** filtros, paginación y creación de nuevos estados. El catálogo es fijo y se puebla por migración Flyway.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|----------|--------------------|
| 1 | Un usuario autenticado con el client role `fichas:estado-ficha:view` solicita `GET /fichas-perfil/estados-ficha` | Retorna `200 OK` con una lista JSON de todos los estados ficha presentes en la tabla catálogo. Cada elemento incluye `id`, `nombre` y `descripcion`. Actualmente la tabla contiene **5 registros**: `APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA`, `EN_CONSTRUCCION` y `DISPONIBLE_PARA_EVALUACION` |
| 2 | Un usuario no autenticado solicita el endpoint | Retorna `401 Unauthorized` |
| 3 | Un usuario autenticado sin el client role requerido solicita el endpoint | Retorna `403 Forbidden` |
| 4 | El orden de los estados en la respuesta | La consulta **no** especifica `ORDER BY`, por lo que el orden lo determina el motor de base de datos. Si se necesita un orden determinístico, ver la nota de la sección 8 |

> **Discrepancia detectada y resuelta (2026-07-30).** El enum `EstadoFicha` declaraba una constante `EN_REVISION` sin fila correspondiente en la tabla. Se validó contra `mer/data/03_data_fichas_perfil.sql` del repositorio `arquisoft-uco/arquisoft-docs`: el catálogo autoritativo define exactamente 5 estados y `EN_REVISION` no existe en él. La constante se eliminó del enum. La migración V1.2 ya era correcta y no se modificó.

---

## 3. Reglas de Negocio

- El catálogo de estados ficha es **de solo lectura** vía REST: sus valores se insertan o modifican únicamente mediante migraciones Flyway.
- Se retornan todos los estados existentes, sin excepción: no hay filtro ni paginación.
- El `id` del catálogo es la constante del enum (`EstadoFicha.name()`) en SCREAMING_SNAKE_CASE (ADR-012).

---

## 4. Modelo DDD del Contexto

### Aggregate Root

**No aplica.** `EstadoFicha` es un **enum de dominio con catálogo de PK semántica (ADR-012)**, no un Aggregate Root. El enum ya existe en `fichas/domain/src/main/java/com/arquisoft/fichas/domain/estadoficha/EstadoFicha.java`. La consulta lee directamente de la tabla catálogo.

### Atributos del catálogo `estado_ficha`

| Atributo | Tipo SQL | Longitud | Obligatorio | Modificable vía API | Notas |
|---|---|---|---|---|---|
| `id` | `VARCHAR(50)` | 50 | Sí | No | PK semántica. Valor = `EstadoFicha.name()` (por ejemplo `"EN_CONSTRUCCION"`) |
| `nombre` | `VARCHAR(30)` | 30 | Sí | No | Nombre legible del estado (por ejemplo `"En Construccion"`) |
| `descripcion` | `VARCHAR(200)` | 200 | Sí | No | Descripción breve del estado. Existe solo en base de datos: el enum **no** tiene este campo |

**Restricciones de unicidad:**

- `UNIQUE (nombre)` → constraint `uk_estado_ficha_nombre`, ya creada en la migración V1.2.

### Traducción del modelo enriquecido a código

**Esta HU no modifica el modelo de dominio.** Solo agrega la capa de consulta:

| Elemento | Traducción en código | Acción |
|---|---|---|
| Enum de dominio | `EstadoFicha.java` en `domain/estadoficha/` | Ya existe, no se modifica |
| JPA Entity | `EstadoFichaJpaEntity.java` en `infrastructure/estadoficha/persistence/` | Ya existe, no se modifica |
| JPA Repository | `EstadoFichaJpaRepository.java` en `infrastructure/estadoficha/persistence/` | Ya existe, no se modifica |
| Tabla poblada | `V1.2__crear_estado_ficha_y_estado_ficha_perfil.sql` | Ya existe, no se modifica |
| ReadModel | `EstadoFichaReadModel` en `application/estadoficha/query/readmodel/` | Se crea |
| QueryOutputPort | `EstadoFichaQueryOutputPort` en `application/estadoficha/query/port/out/` | Se crea |
| InputPort | `ConsultarEstadosFichaInputPort` en `application/estadoficha/query/port/in/` | Se crea |
| UseCase | `ConsultarEstadosFichaUseCase` en `application/estadoficha/query/` | Se crea |
| QueryOutputAdapter | `EstadoFichaQueryOutputAdapter` en `infrastructure/estadoficha/query/adapter/out/persistence/` | Se crea |
| InputAdapter REST | `ConsultarEstadosFichaInputAdapter` en `infrastructure/estadoficha/query/adapter/in/web/` | Se crea |

### Estados y tipos

`EstadoFicha` ya está modelado como enum de dominio con catálogo de PK semántica (ADR-012) desde la HU-206. Esta HU **no modifica** esa estructura: únicamente la expone vía REST.

### Eventos de dominio que emite

**Ninguno.**

Razón: es una consulta pura sobre un catálogo de solo lectura, sin consumidores conocidos ni necesidad de auditoría identificada. El Event Storming menciona un evento "Todos los estados ficha consultados", pero se trata de una lectura sin efectos secundarios, por lo que **no** se publica ningún evento en RabbitMQ.

Implicaciones:

- `EstadoFicha` no extiende `AggregateRoot`: es un enum de dominio.
- El use case no inyecta `EventPublisher` ni drena eventos.
- No se crean archivos en `domain/estadoficha/event/`.

---

## 5. Integraciones Externas

**No aplica.** La HU solo lee de PostgreSQL (tabla `estado_ficha`, ya existente).

---

## 6. Árbol de Archivos a Crear o Modificar

### Archivos nuevos — caso de uso de consulta

| Capa | Ruta completa desde la raíz del monorepo | Tipo | Responsabilidad |
|------|------------------------------------------|------|-----------------|
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoficha/query/readmodel/EstadoFichaReadModel.java` | `record` | Proyección plana con `id`, `nombre` y `descripcion`. **Sin factory `fromDomain`**: el enum no tiene el campo `descripcion`, por lo que el ReadModel se construye desde la JPA Entity en el adapter de salida |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoficha/query/port/in/ConsultarEstadosFichaInputPort.java` | Interface (sin métodos propios) | Extiende `InputPort<Void, List<EstadoFichaReadModel>>` de `shared:domain` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoficha/query/port/out/EstadoFichaQueryOutputPort.java` | Interface | Puerto de salida de lectura. Método: `List<EstadoFichaReadModel> findAll()` |
| application | `fichas/application/src/main/java/com/arquisoft/fichas/application/estadoficha/query/ConsultarEstadosFichaUseCase.java` | UseCase | `@Component` + `@RequiredArgsConstructor` + `@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")`. Implementa `ConsultarEstadosFichaInputPort` y delega en `queryOutputPort.findAll()` |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoficha/query/adapter/in/web/ConsultarEstadosFichaInputAdapter.java` | `@RestController` | Expone `GET /fichas-perfil/estados-ficha`. Serializa `List<EstadoFichaReadModel>` directamente a JSON, sin DTO intermedio. Documentado según ADR-011 |
| infrastructure | `fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/estadoficha/query/adapter/out/persistence/EstadoFichaQueryOutputAdapter.java` | OutputAdapter | Implementa `EstadoFichaQueryOutputPort`. Inyecta `EstadoFichaJpaRepository` (ya existe), invoca `findAll()` y mapea cada `EstadoFichaJpaEntity` a `EstadoFichaReadModel`. **El mapeo no pasa por el enum**, porque el enum carece del campo `descripcion` |

### Catálogo de mensajes (`shared:message`)

**Sin cambios.** La HU no introduce mensajes de error, códigos, logs ni límites nuevos: es una consulta pura sin validaciones adicionales.

### Archivos a modificar

**Ninguno.** La tabla, la JPA Entity, el JPA Repository y el enum ya existen y no requieren cambios.

### Archivos de mensajería RabbitMQ

**No aplica.** La HU no publica ni consume eventos.

### Orquestación de use cases

**No aplica.** La HU ejecuta una sola acción: consultar todos los estados ficha.

---

## 7. Detalle por Archivo

### `EstadoFichaReadModel.java`

- **Paquete:** `com.arquisoft.fichas.application.estadoficha.query.readmodel`
- **Tipo:** `record`
- **Responsabilidad:** proyección plana del estado ficha con tres campos: `id`, `nombre` y `descripcion` (todos `String`).
- **Features de Java 21:** `record`, que aporta inmutabilidad y `equals`/`hashCode` automáticos.
- **Métodos:** solo el constructor canónico `EstadoFichaReadModel(String id, String nombre, String descripcion)`. **No** define factory `fromDomain(EstadoFicha)`, porque el enum no contiene `descripcion`; el adapter de salida construye el ReadModel desde la JPA Entity.
- **Dependencias:** ninguna (Java puro).

---

### `ConsultarEstadosFichaInputPort.java`

- **Paquete:** `com.arquisoft.fichas.application.estadoficha.query.port.in`
- **Tipo:** interface sin métodos propios
- **Responsabilidad:** puerto de entrada para consultar todos los estados ficha. Extiende `InputPort<Void, List<EstadoFichaReadModel>>`.
- **Métodos:** hereda `List<EstadoFichaReadModel> ejecutar(Void input)` de `InputPort`.
- **Dependencias:**
    - `com.arquisoft.shared.inputport.InputPort`
    - `EstadoFichaReadModel`
    - `java.util.List`

---

### `EstadoFichaQueryOutputPort.java`

- **Paquete:** `com.arquisoft.fichas.application.estadoficha.query.port.out`
- **Tipo:** interface
- **Responsabilidad:** puerto de salida de lectura para el catálogo de estados ficha.
- **Métodos:**
    - `List<EstadoFichaReadModel> findAll()` — retorna todos los estados del catálogo, sin filtro ni paginación.
- **Dependencias:**
    - `EstadoFichaReadModel`
    - `java.util.List`

---

### `ConsultarEstadosFichaUseCase.java`

- **Paquete:** `com.arquisoft.fichas.application.estadoficha.query`
- **Tipo:** UseCase
- **Responsabilidad:** implementa el puerto de entrada, delega en `EstadoFichaQueryOutputPort` y retorna la lista de ReadModels sin transformarla.
- **Métodos:**
    - `List<EstadoFichaReadModel> ejecutar(Void input)` — invoca `queryOutputPort.findAll()` y retorna el resultado.
- **Dependencias y anotaciones:**
    - Implementa `ConsultarEstadosFichaInputPort`
    - Inyecta `EstadoFichaQueryOutputPort`
    - `@Component`, `@RequiredArgsConstructor`, `@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")`

---

### `ConsultarEstadosFichaInputAdapter.java`

- **Paquete:** `com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.in.web`
- **Tipo:** `@RestController`
- **Responsabilidad:** endpoint REST `GET /fichas-perfil/estados-ficha`. Serializa la lista de ReadModels directamente a JSON.
- **Métodos:**
    - `ResponseEntity<List<EstadoFichaReadModel>> consultarEstadosFicha()` — invoca `inputPort.ejecutar(null)` y retorna `200 OK` con la lista.
- **Dependencias y anotaciones:**
    - Inyecta `ConsultarEstadosFichaInputPort`
    - `@RestController`, `@RequiredArgsConstructor`, `@RequestMapping("/fichas-perfil")`, `@GetMapping("/estados-ficha")`
    - `@PreAuthorize("hasAuthority('fichas:estado-ficha:view')")`
    - Documentación ADR-011: `@Tag`, `@Operation` (con `security = @SecurityRequirement(name = "bearerAuth")`), `@ApiResponses`

#### Documentación Swagger (ADR-011)

- **`@Tag`:** `name = "Fichas de Perfil"`, `description = "Gestión del ciclo de vida de las fichas de perfil"`

| Método del controller | `@Operation(summary)` | Códigos `@ApiResponse` | Seguridad |
|---|---|---|---|
| `consultarEstadosFicha` | `"Consultar todos los estados ficha"` | 200, 401, 403 | `bearerAuth` |

- **Nota:** el controller no documenta `404` porque la consulta siempre retorna al menos una lista vacía, ni `400` porque no recibe parámetros de entrada que puedan ser inválidos.

---

### `EstadoFichaQueryOutputAdapter.java`

- **Paquete:** `com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.out.persistence`
- **Tipo:** OutputAdapter
- **Responsabilidad:** implementa `EstadoFichaQueryOutputPort`. Consulta la tabla catálogo `estado_ficha` y mapea cada JPA Entity a ReadModel.
- **Métodos:**
    - `List<EstadoFichaReadModel> findAll()` — invoca `jpaRepository.findAll()`, mapea con un método privado `toReadModel(EstadoFichaJpaEntity)` que construye `new EstadoFichaReadModel(entity.getId(), entity.getNombre(), entity.getDescripcion())` y devuelve la lista con `.toList()`.
- **Dependencias y anotaciones:**
    - Implementa `EstadoFichaQueryOutputPort`
    - Inyecta `EstadoFichaJpaRepository` (ya existe)
    - Usa `EstadoFichaJpaEntity` (ya existe)
    - `@Component`, `@RequiredArgsConstructor`

---

## 8. Endpoints REST

### Estado del endpoint

- [x] **Endpoint nuevo** — se crea `ConsultarEstadosFichaInputAdapter.java` desde cero.

### Contrato

| Método | Ruta | Request | Response | Código HTTP | Client role |
|--------|------|---------|----------|-------------|-------------|
| `GET` | `/fichas-perfil/estados-ficha` | sin body ni parámetros | `List<EstadoFichaReadModel>`, con `id`, `nombre` y `descripcion` por elemento | 200 | `fichas:estado-ficha:view` |

**Ejemplo de respuesta:**

```json
[
  {
    "id": "EN_CONSTRUCCION",
    "nombre": "En Construccion",
    "descripcion": "Se refiere a que la ficha de perfil se encuentra en construcción o desarrollo."
  }
]
```

**Nota sobre el orden:** la consulta no especifica `ORDER BY`, por lo que el orden de los registros lo determina PostgreSQL y no está garantizado. Si se necesita un orden determinístico (alfabético por `nombre` o por secuencia semántica del ciclo de vida), debe declararse un método `findAllByOrderByNombreAsc()` en el `JpaRepository` o una `@Query` con `ORDER BY` explícito.

---

## 9. Seguridad y Autorización (Keycloak)

### Client role nuevo

| Client role | Roles de realm que lo poseen | Endpoint que lo requiere | Descripción funcional |
|---|---|---|---|
| `fichas:estado-ficha:view` | `estudiante`, `representante-comite`, `asesor-ficha` | `GET /fichas-perfil/estados-ficha` | Permite consultar el catálogo completo de estados ficha disponibles para asignar a una ficha de perfil |

### Reglas de uso

1. **Formato del client role:** `fichas:estado-ficha:view`, todo en minúsculas y con palabras separadas por guiones.
2. **Conversión de nombres:** el paquete Java es `estadoficha` (sin guion), pero el client role usa `estado-ficha` por convención kebab-case de REST.
3. **Roles de realm en kebab-case:** `estudiante`, `representante-comite`, `asesor-ficha`.
4. El mismo client role se asigna a varios roles de realm: los tres actores pueden ejecutar esta consulta.
5. El endpoint declara exactamente un `@PreAuthorize("hasAuthority('fichas:estado-ficha:view')")`.
6. Nunca se usa `hasRole(...)`: siempre `hasAuthority(...)` con el client role.

### Configuración requerida en Keycloak

Para el client role `fichas:estado-ficha:view`:

1. En el cliente `arquisoft-api`, crear el client role con el nombre exacto `fichas:estado-ficha:view`.
2. Asignarlo a los roles de realm `estudiante`, `representante-comite` y `asesor-ficha`.
3. Verificar que los usuarios de prueba con esos roles reciban el client role en su JWT, bajo `resource_access.arquisoft-api.roles`.

---

## 10. Eventos RabbitMQ

**Ninguno.**

Es una consulta pura sobre un catálogo de solo lectura. El Event Storming menciona "Todos los estados ficha consultados", pero eso no se traduce en un evento de RabbitMQ: es una lectura sin efectos secundarios.

---

## 11. Migración de Base de Datos

**No se requiere migración nueva.**

- La tabla `estado_ficha` ya existe: se crea en `V1.2__crear_estado_ficha_y_estado_ficha_perfil.sql`.
- Esa misma migración la puebla con 5 registros: `APROBADA`, `APROBADA_CON_OBSERVACIONES`, `NO_APROBADA`, `EN_CONSTRUCCION` y `DISPONIBLE_PARA_EVALUACION`.
- Esta HU solo agrega la capa de consulta: no modifica el esquema.

> **Resuelto (2026-07-30).** La constante `EN_REVISION` del enum no existía en el catálogo autoritativo del MER y fue eliminada del código. La tabla y su migración no requirieron cambios: ya coincidían con `mer/data/03_data_fichas_perfil.sql`. Ver la nota de la sección 2.

---

## 12. Casos de Prueba

### Presupuesto orientativo

**Tamaño de la HU:** pequeña (un endpoint de lectura sobre una entidad de catálogo).

**Tests esperados:** entre 10 y 15.

### Tests de la capa `domain`

**No aplica.** `EstadoFicha` es un enum, no un Aggregate Root, y no se modifica en esta HU.

### Tests de la capa `application`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `ConsultarEstadosFichaUseCaseTest` | `debeRetornarListaCompleta_cuandoExistenEstados` | El mock de `EstadoFichaQueryOutputPort.findAll()` retorna una lista con varios elementos; se verifica que el use case devuelve la misma lista sin transformarla |
| `ConsultarEstadosFichaUseCaseTest` | `debeRetornarListaVacia_cuandoNoHayEstados` | El mock retorna una lista vacía; se verifica que el use case devuelve una lista vacía. Es un caso improbable en producción (la tabla está poblada por migración), pero el código debe soportarlo |

### Tests de la capa `infrastructure`

| Clase de test | Método | Escenario |
|---------------|--------|-----------|
| `EstadoFichaQueryOutputAdapterTest` | `debeRetornarListaDeReadModels_cuandoFindAllEsInvocado` | El mock de `EstadoFichaJpaRepository.findAll()` retorna varias `EstadoFichaJpaEntity`; se verifica que el adapter las mapea a `EstadoFichaReadModel` con `id`, `nombre` y `descripcion` correctos |
| `EstadoFichaQueryOutputAdapterTest` | `debeRetornarListaVacia_cuandoRepositorioRetornaVacio` | El mock retorna una lista vacía; se verifica que el adapter devuelve una lista vacía |
| `EstadoFichaQueryOutputAdapterTest` | `debeMapearCorrectamente_cuandoConvierteJpaEntityAReadModel` | Se verifica campo por campo el mapeo de una entidad individual |
| `ConsultarEstadosFichaInputAdapterTest` | `debe200_cuandoConsultaExitosa` | El mock de `inputPort.ejecutar(null)` retorna varios ReadModels; se verifica `200 OK` y el body JSON |
| `ConsultarEstadosFichaInputAdapterTest` | `debe200ConListaVacia_cuandoNoHayEstados` | El mock retorna una lista vacía; se verifica `200 OK` con array JSON vacío |
| `ConsultarEstadosFichaInputAdapterTest` | `debeRetornarJsonCorrecto_cuandoListaTieneElementos` | Se verifica la forma exacta del JSON (nombres de campos y valores) |
| `ConsultarEstadosFichaInputAdapterTest` | `debe401_cuandoNoAutenticado` | Sin token JWT; se verifica `401 Unauthorized` |
| `ConsultarEstadosFichaInputAdapterTest` | `debe403_cuandoRolInsuficiente` | Token JWT válido pero sin el client role `fichas:estado-ficha:view`; se verifica `403 Forbidden` |
| `ConsultarEstadosFichaInputAdapterTest` | `debeInvocarInputPort_cuandoEndpointEsLlamado` | Se verifica que el controller delega efectivamente en el input port |
| `ConsultarEstadosFichaInputAdapterTest` | `debeUsarPreAuthorizeConClientRole_cuandoEndpointRequiereAutorizacion` | Se verifica que la autorización se resuelve por client role y no por rol de realm |

**Convenciones aplicables:**

- Tests de use case y de adapter de salida: JUnit 6 + Mockito + AssertJ con `@ExtendWith(MockitoExtension.class)`, sin contexto de Spring.
- Test de controller: `@WebMvcTest` (`org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`) + `@Import(GlobalAppExceptionHandler.class)`, mock del input port con `@MockitoBean` y autenticación con `SecurityMockMvcRequestPostProcessors.jwt().authorities(...)`.
- Test de slice de repositorio (si se agrega): `@DataJpaTest` de `org.springframework.boot.data.jpa.test.autoconfigure`. **No se usa `@SpringBootTest`** en este repositorio.

### Reglas de consolidación

- No se testean getters ni setters generados por Lombok: el ReadModel es un `record` sin lógica propia.
- No se testean validaciones Jakarta: esta HU no tiene request DTO.
- No se testean métodos privados de forma aislada: el mapeo del adapter se cubre a través de `findAll()`.
- No se define ni se testea ninguna excepción nueva.
- No se testea el ciclo de publicación de eventos: la HU no emite eventos.

---

## 13. Checklist de Implementación

- [x] **DDD:** `EstadoFicha` no es un Aggregate Root, sino un enum de dominio con catálogo de PK semántica (ADR-012). Ya existe y no se modifica.
- [x] La PK del catálogo es `VARCHAR(50)` con valor igual a la constante del enum, ya implementada en la migración V1.2.
- [x] Puerto de entrada `ConsultarEstadosFichaInputPort` definido, extendiendo `InputPort<Void, List<EstadoFichaReadModel>>`.
- [x] Puerto de salida `EstadoFichaQueryOutputPort` definido en `application/`, no en `domain/` (el lado de lectura no toca el aggregate).
- [x] `EstadoFichaReadModel` creado como `record` en `application/estadoficha/query/readmodel/` con los campos `id`, `nombre` y `descripcion`.
- [x] `ConsultarEstadosFichaUseCase` anotado con `@Component`, `@RequiredArgsConstructor` y `@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")`.
- [x] Controller REST documentado con `@Tag`, `@Operation`, `@ApiResponses` y `@SecurityRequirement` (ADR-011).
- [x] Autorización mediante `@PreAuthorize("hasAuthority('fichas:estado-ficha:view')")`, con el client role declarado en la sección 9.
- [x] `EstadoFichaJpaEntity` y `EstadoFichaJpaRepository` ya existen y no se modifican.
- [x] Sin migración Flyway nueva: la tabla `estado_ficha` ya está creada y poblada por V1.2.
- [x] Sin eventos RabbitMQ: la HU no publica ni consume.
- [x] Tests unitarios con patrón Arrange/Act/Assert y cobertura mínima del 75 %, sin tests de ciclo de eventos.
- [x] Tests de controller con Spring Security Test y `@MockitoBean` (Spring Boot 4).
- [x] Sin `@Bean TaskExecutor` manual: los Virtual Threads los gestiona Spring Boot.
- [x] Commit: `feat(fichas): consultar todos los estados ficha sin filtro`

---

## 14. Trazabilidad del Flujo

> Esta sección la actualiza automáticamente cada agente al completar su etapa. No modificar manualmente.

| Etapa | Agente | Estado | Fecha | Notas |
|-------|--------|--------|-------|-------|
| Planificación | @plan-agent | Completado | 2026-07-01 | Plan generado |
| Desarrollo | @implementador | Completado | 2026-07-01 | 6 archivos nuevos: 4 en `application` (ReadModel, InputPort, OutputPort, UseCase) y 2 en `infrastructure` (InputAdapter REST, OutputAdapter de persistencia). Reutiliza el enum `EstadoFicha` y `EstadoFichaJpaEntity`/`EstadoFichaJpaRepository` existentes. Sin migración ni eventos. Compila y pasa Checkstyle |
| Tests | @tester | Completado | 2026-07-01 | 3 archivos de test: `ConsultarEstadosFichaUseCaseTest` (2 tests), `EstadoFichaQueryOutputAdapterTest` (3 tests) y `ConsultarEstadosFichaInputAdapterTest` (7 tests). Total: 12 tests unitarios. `./gradlew fichas:application:test fichas:infrastructure:test` termina en BUILD SUCCESSFUL. Sin tests de `domain` (no aplica: `EstadoFicha` es un enum). Patrón AAA y nomenclatura `debeHacerAlgo_cuandoCondicion` |
| Validación | @validator-analyze | Completado | 2026-07-01 | Score: 98/100 — aprobado |
| Reporte | @validator-report | Completado | 2026-07-01 | `.workspace/validator/validator-HU-036.md` |
| Commit | @commit | Completado | 2026-07-01 | Hash: `02b3759` |
| Revisión editorial | — | Completado | 2026-07-29 | Reescritura completa: corregida la codificación (mojibake UTF-8 doble + BOM), la ortografía y las rutas y datos técnicos contrastados contra el código implementado |
