# PLAN: HU-267 — Registrar nuevo ítem cualitativo del jurado

## Metadata

- **ID Historia:** HU-267
- **Bounded Context:** `evaluaciones`
- **Tipo de Use Case:** Escritura
- **¿Usa AggregateRoot?:** Sí. El Event Storming declara el evento `Item cualitativo del jurado registrado`; el alta lo acumula en el agregado y el use case lo drena mediante `EventPublisher` dentro de la transacción del interactor.
- **Módulos Gradle afectados:** `evaluaciones:domain`, `evaluaciones:application`, `evaluaciones:infrastructure`, `shared:message` y configuración versionada del catálogo
- **Fecha de plan:** 2026-08-23
- **Rama sugerida:** `feature/HU-267-registrar_item_cualitativo_jurado`
- **Fuentes consultadas:**
  - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md` — definición y prioridad de HU267
  - `artefactos/estrategicos/event-storming/Evaluaciones Definitivas - Event Storming.md` — comando, políticas y evento
  - `artefactos/estrategicos/modelo-dominio/anemico/documentacion/12_delimitar_contextos_evaluaciones_definitivas.md`
  - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/12_evaluaciones_definitivas_modelo_enriquecido.md`
  - `artefactos/tecnicos/diseno-arquitectonico/drivers-arquitectonicos/funcionalidades-criticas/funcionalidades_criticas.md` — FC-002 y FC-006
  - `mer/01_base_datos_y_esquemas.sql`
  - `mer/09_tablas_evaluaciones.sql`
  - `docs/architecture/flujo-evaluacion-asesor-jurado.md`
- **Código de referencia verificado:** `fichas/fichaperfil`, `shared:domain`, `shared:message`, configuración de outbox y configuraciones de DataSource por contexto.
- **Estado actual:** `evaluaciones` es scaffolding. Solo existen los cuatro módulos Gradle y `EvaluacionesDataSourceConfig`, que actualmente crea únicamente el `DataSource`; no hay dominio, aplicación, JPA, Flyway, seguridad ni tests.
- **Observaciones del usuario:** se aprobaron las propuestas del planificador: nombre único sin distinguir mayúsculas/minúsculas, evento sin consumidor conocido, respuesta UUID, ruta bajo `/evaluaciones`, client role exclusivo de administrador y alcance limitado al registro.

---

## 1. Resumen Funcional

La HU permite que un administrador registre un nuevo ítem cualitativo que los jurados podrán utilizar posteriormente en sus evaluaciones. El recurso contiene un nombre institucional único y una descripción; el sistema genera su UUID, lo persiste atómicamente y registra el evento de dominio en el outbox del contexto `evaluaciones`.

No cubre modificar, remover ni consultar ítems, tampoco registrar evaluaciones o criterios cualitativos. Esas acciones aparecen como comandos posteriores en el Event Storming y pertenecen a otras HU.

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|---|---|
| 1 | Administrador envía nombre y descripción válidos y no repetidos | `201 Created` con `RegistrarItemCualitativoJuradoResponseDTO` que contiene el UUID generado |
| 2 | Nombre o descripción están vacíos o exceden 100/300 caracteres | `400 Bad Request` con `fieldErrors`, sin consultar la BD ni publicar eventos |
| 3 | Ya existe el mismo nombre, incluso con distinta combinación de mayúsculas/minúsculas | `422 Unprocessable Content` con código `ITEM_CUALITATIVO_JURADO_NOMBRE_DUPLICADO` |
| 4 | La persistencia termina correctamente | La fila queda en `item_cualitativo_jurado` y la publicación queda registrada en `event_publication` dentro de la misma transacción |
| 5 | La persistencia falla | Se revierte tanto el ítem como el registro del evento; no hay publicación efectiva inconsistente |
| 6 | Usuario autenticado sin el client role requerido | `403 Forbidden` |
| 7 | Petición sin JWT válido | `401 Unauthorized` |
| 8 | Operación exitosa | Se escribe el log mediante `AppLogger` y una clave de `CatalogoMensajes`, sin literales embebidos |

---

## 3. Reglas de Negocio

Políticas del Event Storming:

- `ItemCualJura-POL-01`: datos válidos por tipo, longitud, obligatoriedad, formato y rango.
- `ItemCualJura-POL-02`: no puede existir otro ítem cualitativo con el mismo nombre.
- `ItemCualJura-POL-03`: no puede existir otro ítem cualitativo con el mismo identificador.

| # | Regla | Dónde se valida | Estado consultado | Excepción → HTTP |
|---|---|---|---|---|
| 1 | `nombre` obligatorio, trim, longitud 1–100 | `RegistrarItemCualitativoJuradoCommand.crear(...)`; el Domain conserva la invariante con Notification Pattern | Ninguno | Errores de entrada → 400 |
| 2 | `descripcion` obligatoria, trim, longitud 1–300 | Command y Domain, usando `ValidationResult` y validadores compartidos | Ninguno | Errores de entrada → 400 |
| 3 | Nombre único sin distinguir mayúsculas/minúsculas | Use case consulta Finder; `RegistrarItemCualitativoJuradoValidator` orquesta la Rule pura | `boolean nombreYaExiste` | `NombreItemCualitativoJuradoDuplicadoException` → 422 |
| 4 | UUID único | `UtilUUID.generarNuevoUUID()` y PK de PostgreSQL | Ninguno; no se hace una consulta previa para un UUID generado | Colisión protegida por PK |

Orden obligatorio del flujo:

1. El RequestMapper llama `Command.crear(...)` y valida integridad antes de cualquier I/O.
2. El Interactor convierte Command a Domain; `crear(...)` valida invariantes locales y acumula el evento.
3. El UseCase consulta la existencia del nombre mediante Finder.
4. El Validator ejecuta la Rule de unicidad sin `if` ni I/O.
5. El UseCase persiste, drena eventos, publica y registra el log.

La unicidad debe normalizarse igual en aplicación y BD. La consulta será `existsByNombreIgnoreCase` y la protección contra carreras será un índice único funcional sobre `LOWER(nombre)`.

---

## 4. Modelo DDD del Contexto

### Entidad raíz

- **Clase:** `ItemCualitativoJuradoDomain`
- **Ubicación:** directa en `domain/itemcualitativojurado/`, sin `aggregate/` ni `model/`.
- **Extiende:** `com.arquisoft.shared.events.AggregateRoot`.
- **Construcción:** constructor privado; campos no `final`; setters privados; getters públicos; factories `crear(...)` y `reconstruir(...)`; sin Lombok ni Spring.
- **Creación:** genera UUID con `UtilUUID`, aplica trim con `UtilTexto`, valida mediante Notification Pattern y solo después llama `publicarEvento(...)`.
- **Reconstrucción:** restaura los datos persistidos sin validar, generar UUID ni acumular eventos.

### Atributos de `ItemCualitativoJurado`

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Sensible | Notas |
|---|---|---|---|---|---|---|---|
| `id` | `UUID` | — | Sí | No | Sí | No | Identifica el registro |
| `nombre` | `String` | 1–100 | Sí | No | No | No | Trim en inicio/fin; único case-insensitive por decisión del usuario |
| `descripcion` | `String` | 1–300 | Sí | Sí | No | No | Trim en inicio/fin; esta HU no implementa su modificación |

**Combinación única:** `nombre`. El MER prescribe unicidad y el usuario aclaró que debe ignorar mayúsculas/minúsculas.

### Evento de dominio

| Evento | Clase | `temaEvento` | Payload propio | Consumidor | Cuándo |
|---|---|---|---|---|---|
| Ítem cualitativo del jurado registrado | `ItemCualitativoJuradoRegistradoEvent` | `evaluaciones.item_cualitativo_jurado.registrado` | `UUID itemCualitativoJuradoId`, `String nombre`, `String descripcion` | Ninguno conocido; auditoría/futuro | Al completar `ItemCualitativoJuradoDomain.crear(...)` |

El tema usa guiones bajos porque `DomainEvent` valida el patrón `{contexto}.{entidad}.{accion}` con segmentos `[a-z_]`; el valor inicialmente conversado con guiones sería rechazado por el código real.

El evento extiende `DomainEvent`, define `EVENT_TOPIC` y `EVENT_TYPE`, y no usa un `aggregateId` genérico.

---

## 5. Integraciones Externas

No hay integración con Keycloak Admin API, SMTP, MinIO ni HTTP externo. Los componentes estándar son:

| Componente | Uso |
|---|---|
| PostgreSQL `evaluaciones` | Persistencia del catálogo y tabla outbox distribuida |
| Spring Modulith + `EventPublisher` | Registro atómico de publicaciones y externalización posterior |
| RabbitMQ | Broker eventual del evento; no se implementa consumer en esta HU |
| Redis | Catálogo de mensajes ya provisto por `shared:message`; se agrega el archivo del contexto |

---

## 6. Árbol de Archivos a Crear / Modificar

### Archivos nuevos

| Capa | Ruta | Responsabilidad |
|---|---|---|
| domain | `evaluaciones/domain/src/main/java/com/arquisoft/evaluaciones/domain/itemcualitativojurado/ItemCualitativoJuradoDomain.java` | Aggregate root, factories, integridad y evento |
| domain | `evaluaciones/domain/src/main/java/com/arquisoft/evaluaciones/domain/itemcualitativojurado/event/ItemCualitativoJuradoRegistradoEvent.java` | Evento con campos semánticos propios |
| domain | `evaluaciones/domain/src/main/java/com/arquisoft/evaluaciones/domain/itemcualitativojurado/model/DisponibilidadNombreItemCualitativoJurado.java` | Record que entrega a la Rule el nombre y el resultado consultado |
| domain | `evaluaciones/domain/src/main/java/com/arquisoft/evaluaciones/domain/itemcualitativojurado/rules/NombreItemCualitativoJuradoUnicoRule.java` | Contrato `DomainRule<DisponibilidadNombreItemCualitativoJurado>` |
| domain | `evaluaciones/domain/src/main/java/com/arquisoft/evaluaciones/domain/itemcualitativojurado/rules/impl/NombreItemCualitativoJuradoUnicoRuleImpl.java` | Decisión pura de unicidad, sin Spring ni constructor con dependencias |
| domain | `evaluaciones/domain/src/main/java/com/arquisoft/evaluaciones/domain/itemcualitativojurado/exception/NombreItemCualitativoJuradoDuplicadoException.java` | `DomainException` con mensaje de catálogo y errorCode |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/primaryport/model/RegistrarItemCualitativoJuradoCommand.java` | Record de entrada con factory `crear(...)` |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/primaryport/mapper/RegistrarItemCualitativoJuradoMapper.java` | Command → Domain; mapper final, privado y estático |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/primaryport/interactor/RegistrarItemCualitativoJuradoInteractor.java` | Puerto primario `UUID ejecutar(Command)` |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/primaryport/interactor/impl/RegistrarItemCualitativoJuradoInteractorImpl.java` | Transacción `evaluacionesTransactionManager` y delegación |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/usecase/RegistrarItemCualitativoJuradoUseCase.java` | Colaborador interno que recibe el Domain |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/usecase/impl/RegistrarItemCualitativoJuradoUseCaseImpl.java` | Unicidad, persistencia, drenado/publicación y log |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/finder/NombreItemCualitativoJuradoExisteFinder.java` | Finder que siempre devuelve `Boolean` |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/finder/impl/NombreItemCualitativoJuradoExisteFinderImpl.java` | Delega la consulta al OutputPort |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/validator/RegistrarItemCualitativoJuradoValidator.java` | Contrato de validación de la acción |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/validator/impl/RegistrarItemCualitativoJuradoValidatorImpl.java` | Construye la Rule en constructor sin argumentos y la orquesta sin `if` |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/secondaryport/ItemCualitativoJuradoOutputPort.java` | Escritura y consulta de existencia requeridas por el comando |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/secondaryport/entity/ItemCualitativoJuradoEntity.java` | Record plano de persistencia, sin JPA/Lombok |
| application | `evaluaciones/application/src/main/java/com/arquisoft/evaluaciones/application/itemcualitativojurado/command/secondaryport/mapper/ItemCualitativoJuradoMapper.java` | Domain ↔ Entity, final/privado/estático |
| infrastructure | `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/itemcualitativojurado/command/primaryadapter/web/RegistrarItemCualitativoJuradoController.java` | Endpoint POST, seguridad y OpenAPI |
| infrastructure | `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/itemcualitativojurado/command/primaryadapter/web/dto/RegistrarItemCualitativoJuradoRequestDTO.java` | Record `nombre`, `descripcion`, sin anotaciones Jakarta |
| infrastructure | `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/itemcualitativojurado/command/primaryadapter/web/dto/RegistrarItemCualitativoJuradoResponseDTO.java` | Record con `UUID id` |
| infrastructure | `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/itemcualitativojurado/command/primaryadapter/web/mapper/RegistrarItemCualitativoJuradoRequestMapper.java` | DTO → Command mediante `Command.crear(...)` |
| infrastructure | `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/itemcualitativojurado/command/secondaryadapter/entity/ItemCualitativoJuradoJpaEntity.java` | Entidad JPA de `item_cualitativo_jurado` |
| infrastructure | `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/itemcualitativojurado/command/secondaryadapter/mapper/ItemCualitativoJuradoJpaMapper.java` | Entity ↔ JpaEntity |
| infrastructure | `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/itemcualitativojurado/command/secondaryadapter/repository/ItemCualitativoJuradoCommandRepository.java` | `JpaRepository` y `existsByNombreIgnoreCase` |
| infrastructure | `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/itemcualitativojurado/command/secondaryadapter/repository/ItemCualitativoJuradoCommandOutputAdapter.java` | Implementa OutputPort y traduce fallos de persistencia |
| infrastructure | `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/security/EvaluacionesAuthorities.java` | Client roles y expresiones `hasAuthority` |
| migration | `evaluaciones/infrastructure/src/main/resources/db/migration/evaluaciones/V1.0__crear_item_cualitativo_jurado.sql` | Tabla e índice único case-insensitive |
| migration | `evaluaciones/infrastructure/src/main/resources/db/migration/evaluaciones/V1.1__crear_event_publication.sql` | Outbox propio del contexto |
| shared:message | `shared/message/src/main/java/com/arquisoft/shared/message/constant/EvaluacionesCodes.java` | Códigos contractuales de validación/error |
| shared:message | `shared/message/src/main/java/com/arquisoft/shared/message/constant/EvaluacionesFields.java` | Nombres `nombre` y `descripcion` para `fieldErrors` |
| shared:message | `shared/message/src/main/java/com/arquisoft/shared/message/constant/EvaluacionesLimits.java` | Límites 100 y 300 |
| shared:message | `shared/message/src/main/java/com/arquisoft/shared/message/key/evaluaciones/ItemCualitativoJuradoKey.java` | Claves de error y log del feature |
| shared:message | `shared/message/src/main/java/com/arquisoft/shared/message/annotation/EvaluacionesApiMessages.java` | Textos constantes para anotaciones OpenAPI |
| catalog | `catalogo/evaluaciones.properties` | Textos versionados que se cargarán a Redis |

### Archivos a modificar

| Ruta | Cambio |
|---|---|
| `evaluaciones/domain/build.gradle` | Añadir `implementation project(':shared:domain')` |
| `evaluaciones/application/build.gradle` | Mantener domain y añadir `shared:domain` (EventPublisher) y `shared:logger` |
| `evaluaciones/infrastructure/build.gradle` | Reemplazar JDBC mínimo por dependencias JPA, Flyway, PostgreSQL, seguridad, OAuth2, validation, OpenAPI, `shared:jpa`, `shared:web`, `shared:amqp`, `shared:logger` y dependencias de test equivalentes al patrón de `fichas` |
| `evaluaciones/infrastructure/src/main/java/com/arquisoft/evaluaciones/infrastructure/config/EvaluacionesDataSourceConfig.java` | Completar `@EnableJpaRepositories`, EntityManagerFactory, `evaluacionesTransactionManager` y `evaluacionesFlyway` con location `classpath:db/migration/evaluaciones` |
| `shared/message/src/main/java/com/arquisoft/shared/message/ContextosCatalogo.java` | Añadir `EVALUACIONES` y registrarlo en `TODOS` |
| `shared/message/src/main/java/com/arquisoft/shared/message/ClavesCatalogo.java` | Registrar `ItemCualitativoJuradoKey.class` |
| `catalogo/cargar.sh` | Añadir `evaluaciones` a `CONTEXTOS` en el mismo orden de `ContextosCatalogo.TODOS` |

No se crea paquete `query/`: `existsByNombreIgnoreCase` solo alimenta una Rule del comando y por eso pertenece al OutputPort de `command` consumido por un Finder.

`src/main/java/com/arquisoft/config/OpenApiConfig.java` ya contiene el grupo `08-evaluaciones` con `/evaluaciones/**`; no requiere cambios.

---

## 7. Detalle por Archivo y Flujo

### Dominio

- `ItemCualitativoJuradoDomain.crear(nombre, descripcion)` valida con `ValidationResult`, genera el UUID, asigna los valores normalizados y publica `ItemCualitativoJuradoRegistradoEvent`. La publicación se añade únicamente después de `lanzarSiTieneErrores()`.
- `ItemCualitativoJuradoDomain.reconstruir(id, nombre, descripcion)` no valida ni publica.
- `NombreItemCualitativoJuradoUnicoRuleImpl.validar(disponibilidad)` lanza `NombreItemCualitativoJuradoDuplicadoException` cuando `nombreYaExiste` sea verdadero. Es POJO puro sin anotaciones.
- La excepción resuelve su texto mediante `Mensajes.obtener(ItemCualitativoJuradoKey.ERROR_NOMBRE_DUPLICADO, nombre)` y usa `EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_DUPLICADO`.
- El evento declara `EVENT_TOPIC = "evaluaciones.item_cualitativo_jurado.registrado"` y `EVENT_TYPE = "ItemCualitativoJuradoRegistradoEvent"`.

### Aplicación

- `RegistrarItemCualitativoJuradoCommand.crear(nombre, descripcion)` aplica trim, valida obligatoriedad y máximos con constantes `EvaluacionesFields/Codes/Limits`, acumula todos los errores y llama `lanzarSiTieneErroresDeEntrada()`.
- `RegistrarItemCualitativoJuradoMapper.toDomain(command)` llama exclusivamente a `ItemCualitativoJuradoDomain.crear(...)`.
- El Interactor implementa `UUID ejecutar(Command)`, lleva `@Component`, `@RequiredArgsConstructor` y `@Transactional(transactionManager = "evaluacionesTransactionManager")`; solo mapea y delega.
- El UseCase recibe `ItemCualitativoJuradoDomain`, consulta `NombreItemCualitativoJuradoExisteFinder.obtener(nombre)`, invoca el Validator, convierte a `ItemCualitativoJuradoEntity`, persiste, drena `extraerEventosSinPublicar().forEach(eventPublisher::publish)`, registra el log y retorna el UUID. No lleva `@Transactional`.
- El Validator posee un único constructor sin argumentos que instancia `NombreItemCualitativoJuradoUnicoRuleImpl`; no inyecta Finder ni OutputPort y no contiene `if`.
- El Finder es `@Component`, inyecta únicamente `ItemCualitativoJuradoOutputPort` y devuelve `Boolean`, nunca lanza por ausencia.
- El OutputPort define `void registrar(ItemCualitativoJuradoEntity entity)` y `Boolean existePorNombreIgnorandoMayusculas(String nombre)`.

### Infraestructura

- El Controller usa `@RequestMapping("${rutas.evaluaciones.items-cualitativos-jurado.base:/evaluaciones/items-cualitativos-jurado}")`, `@PostMapping`, `@PreAuthorize(EvaluacionesAuthorities.Expresiones.HAS_ITEM_CUALITATIVO_JURADO_CREATE)` y delega en el Interactor.
- La respuesta se construye como `ResponseEntity.status(HttpStatus.CREATED).body(new RegistrarItemCualitativoJuradoResponseDTO(id))`.
- OpenAPI: `@Tag` en clase; `@Operation` con resumen inferior a diez palabras y `ApiSecurity.BEARER_AUTH`; respuestas 201, 400, 401, 403, 422 y 503 usando `ApiCodes`, `EvaluacionesApiMessages` y `ErrorResponseDTO`.
- `ItemCualitativoJuradoJpaEntity` usa `@Entity`, `@Table(name = "item_cualitativo_jurado")`, UUID y columnas `nombre`/`descripcion` con longitudes 100/300 y `nullable = false`.
- El repositorio declara `boolean existsByNombreIgnoreCase(String nombre)`.
- El adapter usa el JpaMapper, guarda mediante el repositorio y traduce una violación concurrente del índice de nombre a la misma excepción de dominio; fallos genuinos de BD se traducen a `InfrastructureException` (503), sin lógica de negocio adicional.
- `EvaluacionesDataSourceConfig` sigue el patrón real de `FichasDataSourceConfig`, con nombres de beans `evaluacionesDataSource`, `evaluacionesEntityManagerFactory`, `evaluacionesTransactionManager` y `evaluacionesFlyway`.

### Catálogo de mensajes

- `EvaluacionesFields.ItemCualitativoJurado`: `NOMBRE = "nombre"`, `DESCRIPCION = "descripcion"`.
- `EvaluacionesLimits.ItemCualitativoJurado`: `NOMBRE_MAX = 100`, `DESCRIPCION_MAX = 300`.
- `EvaluacionesCodes.ItemCualitativoJurado`: códigos requeridos, demasiado largo y nombre duplicado.
- `ItemCualitativoJuradoKey`: `ERROR_NOMBRE_DUPLICADO` declara un parámetro de formato y `LOG_REGISTRADO` declara aridad cero, porque su marcador `{}` lo resuelve `AppLogger`/SLF4J.
- `catalogo/evaluaciones.properties`: texto del duplicado y log; las validaciones genéricas continúan resolviéndose con `ValidadorKey`.
- `EvaluacionesApiMessages`: grupo `Comun` para 401/403 y grupo `ItemCualitativoJurado` para tag, operación y respuestas.
- El cambio debe conservar sincronizados `ContextosCatalogo.TODOS` y `catalogo/cargar.sh`; `CatalogoCargaTest` verificará que el enum nuevo esté registrado y todas las claves tengan texto.

---

## 8. Endpoint REST

| Método | Ruta sin `/api` | Request | Response | HTTP | Client role | Swagger |
|---|---|---|---|---|---|---|
| POST | `/evaluaciones/items-cualitativos-jurado` | `{ "nombre": String, "descripcion": String }` | `{ "id": UUID }` | 201 | `evaluaciones:item-cualitativo-jurado:create` | 201/400/401/403/422/503 + bearerAuth |

El endpoint es nuevo. El prefijo `/api` proviene del context path global y no se escribe en las anotaciones.

---

## 9. Seguridad y Autorización (Keycloak)

| Client role | Realm role que lo posee | Endpoint | Descripción |
|---|---|---|---|
| `evaluaciones:item-cualitativo-jurado:create` | `administrador` | `POST /evaluaciones/items-cualitativos-jurado` | Gestionar el alta institucional del catálogo cualitativo del jurado |

- Crear la constante y expresión en `EvaluacionesAuthorities`; no escribir el authority inline.
- Usar un único `hasAuthority` en el endpoint; no `hasRole` ni el realm role directamente.
- Registrar el client role en el cliente de Keycloak configurado para el backend y componerlo en `administrador`.
- `realm-arquisoft.json` actualmente contiene roles realm en mayúsculas y no versiona ningún client role. No renombrar globalmente esos roles dentro de esta HU: coordinar la carga del client role conforme a HT-011 y documentar la discrepancia hasta que el realm versionado sea normalizado.
- No hay validación de propietario: el ítem es un catálogo institucional sin dueño individual.

---

## 10. Eventos RabbitMQ y Outbox

| Dirección | Exchange | Routing key | Payload | Receptor |
|---|---|---|---|---|
| Salida | Exchange configurado por `shared:amqp`/Spring Modulith | `evaluaciones.item_cualitativo_jurado.registrado` | `itemCualitativoJuradoId`, `nombre`, `descripcion`, más metadatos heredados de `DomainEvent` | Ninguno conocido actualmente |

Flujo transaccional obligatorio:

1. `RegistrarItemCualitativoJuradoInteractorImpl` abre la transacción de `evaluaciones`.
2. El UseCase persiste el Entity.
3. Drena el evento del AggregateRoot y llama `EventPublisher.publish` antes del retorno.
4. Spring Modulith inserta la publicación en `event_publication` usando la transacción activa del contexto.
5. Tras commit intenta externalizar a RabbitMQ; si falla, la publicación queda recuperable y `FailedEventRetryConfig` la reintenta.

No crear producer AMQP manual, listener ni scheduler adicional.

---

## 11. Migraciones de Base de Datos

No existe hoy `db/migration/evaluaciones`; por tanto los siguientes números reales son `V1` y `V2`.

### `V1.0__crear_item_cualitativo_jurado.sql`

- Crear `item_cualitativo_jurado` sin prefijo de schema: `id UUID PRIMARY KEY`, `nombre VARCHAR(100) NOT NULL`, `descripcion VARCHAR(300) NOT NULL`.
- Crear `CREATE UNIQUE INDEX uk_item_cual_jurado_nombre_ci ON item_cualitativo_jurado (LOWER(nombre));`.
- El índice funcional sustituye el constraint case-sensitive `uk_item_cual_jurado_nombre` del MER por la decisión explícita del usuario. No crear ambos, pues el funcional ya garantiza la regla más fuerte acordada.
- No hay FK ni dependencia de `usuarios`/`entregables` para esta tabla.

### `V1.1__crear_event_publication.sql`

Crear la tabla estándar distribuida del outbox con columnas `id`, `listener_id`, `event_type`, `serialized_event`, `publication_date`, `completion_date`, `status`, `completion_attempts`, `last_resubmission_date` y PK UUID, exactamente con los tipos establecidos en `AGENTS.md`.

`evaluacionesFlyway` debe ejecutar ambas migraciones antes de construir el EntityManagerFactory; la presencia de `event_publication` permite que `ContextAwareEventPublicationRepository` detecte automáticamente este DataSource.

---

## 12. Casos de Prueba Sugeridos

HU pequeña de escritura: objetivo de 20–25 tests, sin probar getters, setters privados ni métodos privados.

### Domain (7–8)

- `debeCrearItemYAplicarTrim_cuandoDatosValidos`.
- `debeAcumularErrores_cuandoNombreYDescripcionInvalidos`.
- Casos de máximos 100/300 aceptados y exceso rechazado.
- `debePublicarEventoConPayloadSemantico_cuandoCrearEsExitoso`.
- `debeDrenarYLimpiarEventos_cuandoExtraerEventos`.
- `debeReconstruirSinPublicarEvento_cuandoProvieneDePersistencia`.
- Rule acepta nombre disponible y lanza excepción/código correcto cuando existe.

### Application (7–8)

- Command normaliza y acepta datos válidos; acumula errores de entrada inválida.
- Mapper crea el Domain con su evento.
- Finder devuelve exactamente el booleano del OutputPort y nunca lanza por `false`.
- Validator delega en la Rule.
- UseCase exitoso: consulta unicidad, persiste Entity, publica un evento, registra log y retorna UUID.
- Nombre duplicado: no persiste, no publica y conserva el evento solo en el Domain efímero.
- Fallo de persistencia: no publica evento.
- Interactor delega una vez y posee test que verifica el contrato de retorno; la transacción se valida por configuración/slice, no simulando Spring en unitario.

### Infrastructure (8–9)

- JpaMapper conserva los tres campos en ambos sentidos.
- OutputAdapter guarda y consulta ignorando mayúsculas/minúsculas.
- Repositorio `@DataJpaTest` con H2: persiste; detecta `"Claridad"` al consultar `"claridad"`; valida columnas obligatorias/longitudes relevantes. Si H2 no soporta el índice funcional de PostgreSQL en el slice, comprobar el DDL por test de migración separado o inspección SQL, sin degradar la regla productiva.
- Controller `@WebMvcTest` + `GlobalAppExceptionHandler` + `AppLoggerConfig`: 201 y body, 400 de entrada, 422 duplicado, 401, 403 y acceso con authority correcto.
- Verificar que el Controller usa `EvaluacionesApiMessages`, `ApiCodes` y `ApiSecurity`.
- `CatalogoCargaTest` y tests del catálogo deben incluir el nuevo contexto, enum y archivo de propiedades.

Todos los tests siguen AAA, JUnit 6, Mockito, AssertJ, `@MockitoBean` en slices y nombres `debeHacerAlgo_cuandoCondicion`. Cobertura mínima 75%.

---

## 13. Checklist de Implementación

- [ ] `ItemCualitativoJuradoDomain` directo en la feature, sin `aggregate/`, constructor privado, campos no final, `crear/reconstruir`, sin Lombok/Spring.
- [ ] Extiende `AggregateRoot`; `crear` acumula el evento después de validar y `reconstruir` no publica.
- [ ] Evento con campos propios y topic válido con guiones bajos.
- [ ] Command valida integridad antes de I/O; DTO sin Jakarta y RequestMapper externo.
- [ ] Finder devuelve `Boolean`; Validator puro, sin Finder/OutputPort/`if`; Rule POJO sin dependencias.
- [ ] Interactor dueño de `@Transactional(transactionManager = "evaluacionesTransactionManager")`; UseCase sin anotación transaccional.
- [ ] UseCase persiste antes de drenar/publicar; no publica si falla validación o persistencia.
- [ ] OutputPort habla `ItemCualitativoJuradoEntity`, nunca Domain; JPA queda solo en infrastructure.
- [ ] No crear lado `query/` para una comprobación interna del comando.
- [ ] Un Controller por acción con OpenAPI completo y `@PreAuthorize` mediante `EvaluacionesAuthorities`.
- [ ] Endpoint retorna ResponseDTO con UUID y 201.
- [ ] Nombre único case-insensitive en consulta previa e índice PostgreSQL.
- [ ] DataSourceConfig completo con JPA, transaction manager y Flyway cualificados.
- [ ] Migraciones `V1` y `V2`, incluida `event_publication` del contexto.
- [ ] Catálogo de mensajes completo: constants, key enum, properties, registros y loader sincronizados.
- [ ] Registrar `evaluaciones:item-cualitativo-jurado:create` para `administrador` en Keycloak.
- [ ] AppLogger por constructor; sin `@Slf4j`, `@Autowired`, `@Service` ni strings de producción inline.
- [ ] Checkstyle main/test, pruebas y cobertura ≥75% en verde.
- [ ] Sin TaskExecutor, scheduler, producer o consumer AMQP manual.
- [ ] Commit sugerido: `feat(evaluaciones): registrar ítem cualitativo del jurado`.

---

## 14. Trazabilidad del Flujo

| Etapa | Agente | Estado | Fecha | Notas |
|---|---|---|---|---|
| Desarrollo | @implementador | ✅ Completado | 2026-08-23 | Capas domain/application/infrastructure aprobadas; `:evaluaciones:build -x test` y `build -x test` exitosos. |
| Tests | @tester | ✅ Completado | 2026-08-23 | Cobertura: 92,60% — CUMPLE el 75% (domain 100%, application 89,47%, infrastructure 81,48%). |
| Validación | @validator-analyze | ⏳ Pendiente | | |
| Reporte | @validator-report | ⏳ Pendiente | | |
| Commit | @commit | ⏳ Pendiente | | |
