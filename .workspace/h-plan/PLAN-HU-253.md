# PLAN: Agregar información de un nuevo representante del comité

## Metadata
- **ID Historia:** HU-253
- **Bounded Context:** usuarios
- **Tipo de Use Case:** Escritura
- **¿Usa AggregateRoot?:** Sí. Emite el evento `RepresentanteComiteCurriculumAgregadoEvent` para auditoría/futuros consumidores (opción B: se anticipa, sin consumidor inmediato conocido). Coherencia: extiende `AggregateRoot` + use case inyecta `EventPublisher` + eventos declarados abajo.
- **Módulos Gradle afectados:** `usuarios:domain`, `usuarios:application`, `usuarios:infrastructure`
- **Fecha de plan:** 2026-08-23
- **Rama sugerida:** `feature/HU-253-agregar_representante_comite_curriculum`
- **Fuentes consultadas:** Contexto recopilado en corrida anterior (Event Storming, Modelo Enriquecido, MER), verificado contra el código real de `usuarios` y `fichaperfil`
- **Observaciones del usuario:**
  - **Desviación deliberada de convención de DTO:** esta HU sigue la convención "grande" de `fichaperfil` (DTO bare `record` sin anotaciones Jakarta + `RequestMapper` externo + validación en `Command.crear(...)`) en lugar de la convención "pequeña" que `usuarios` usa actualmente (DTO con `@NotBlank`/`@NotNull` + método `toCommand()` propio). Es una decisión consciente del usuario para esta HU — no es un error.
  - **Mock temporal de `UsuarioQueryOutputPort`:** el contexto `usuarios` no tiene un paquete `query/` real todavía (solo `command/`). La validación de existencia del usuario requiere un `UsuarioQueryOutputPort.existsById(UUID)`. Este puerto + su adaptador se **mockean temporalmente** en el plan: el adaptador devuelve siempre `true` hasta que la feature `usuario` desarrolle su propio read side. Debe documentarse como técnicamente incompleto y sustituirse cuando `usuario` tenga `query/`.
  - Sin atributos adicionales más allá del FK `usuario`. Sin timestamps de auditoría (fuera de alcance de esta HU).

## 1. Resumen Funcional

Permite al **Administrador** agregar un nuevo **Representante del Comité Curriculum** al sistema. El representante es un usuario existente de la tabla `usuario` que se registra como tal mediante su `usuario_id` (FK). La operación valida que el usuario exista previamente, que no esté ya registrado como representante (unicidad de la PK), y emite un evento de dominio para auditoría. **No cubre:** eliminación de representantes, modificación de representantes existentes, consulta de la lista de representantes (esos casos quedan fuera del alcance de HU-253).

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|---|---|
| 1 | El Administrador envía un `usuario` válido (UUID existente en la tabla `usuario`) | HTTP 201, body con el `id` del representante (`usuario_id`), evento emitido a RabbitMQ |
| 2 | El Administrador envía un `usuario` con formato UUID inválido | HTTP 422, `fieldErrors` con el campo `usuario` y código de error de formato |
| 3 | El Administrador envía un `usuario` que no existe en la tabla `usuario` | HTTP 422, `DomainException` indicando que el usuario no existe |
| 4 | El Administrador envía un `usuario` que ya es representante (duplicado) | HTTP 422, `DomainException` indicando que el usuario ya está registrado como representante |
| 5 | El Administrador sin rol `administrador` intenta agregar un representante | HTTP 403, sin body |
| 6 | Request sin token JWT | HTTP 401, sin body |

## 3. Reglas de Negocio

| # | Regla | Dónde se valida | Estado que el use case lee y pasa | Excepción → HTTP |
|---|---|---|---|---|
| 1 | El campo `usuario` es obligatorio y debe tener formato UUID válido | `Command.crear(...)` (application) | Ninguno (validación de formato) | `DomainValidationException` → 422 `fieldErrors` |
| 2 | El usuario debe existir previamente en la tabla `usuario` | `UsuarioExisteRule` (domain), orquestada por `AgregarRepresentanteComiteCurriculumValidator` | `boolean usuarioExiste` (obtenido vía `UsuarioExisteFinder` desde `UsuarioQueryOutputPort.existsById(...)` — **mock temporal**) | `UsuarioNoEncontradoException` → 422 |
| 3 | El usuario no debe estar ya registrado como representante | `RepresentanteComiteUnicoRule` (domain), orquestada por el `Validator` | `boolean yaEsRepresentante` (obtenido vía `RepresentanteComiteExisteFinder` desde `RepresentanteComiteCurriculumOutputPort.existePorUsuario(UUID)`) | `UsuarioYaEsRepresentanteException` → 422 |

**Coherencia dura:** Regla 1 es invariante LOCAL (formato) → acumulable en `ValidationResult` dentro de `Command.crear(...)`. Reglas 2 y 3 son restricciones de CONJUNTO (existencia, unicidad contra DB) → orquestadas por el `Validator` vía `Rule`s, que arrojan `DomainException` → 422.

**Deuda técnica registrada (post-validación, 2026-08-23) — RN-06 y RN-07 del documento fuente de la HU:**
- **RN-06** (el usuario referenciado debe existir como Usuario válido, con Identificador 4-30 alfanuméricos y/o Email con formato válido): la Regla 2 de esta tabla solo cubre la mitad — existencia — y lo hace contra `UsuarioQueryOutputPort.existsById(...)`, que es el **mock temporal documentado en sección 5/7** (siempre devuelve `true`). No hay ninguna verificación real de existencia ni de formato de Identificador/Email.
- **RN-07** (el usuario debe estar en un `EstadoUsuario` habilitado): **no implementada en absoluto**. El dominio de `usuario` no tiene ningún concepto de estado hoy (`UsuarioDomain` solo modela `UsuarioRole`) — no hay Rule, Finder ni check de ningún tipo para esto.

**Por qué queda así:** ambas dependen de capacidades que el contexto `usuario` no tiene todavía (un `query/` real, y un concepto de `EstadoUsuario`) — la misma limitación estructural que obligó a mockear `UsuarioQueryOutputPort` en primer lugar. Decisión explícita del usuario (2026-08-23): dejarlo documentado como deuda técnica sin tocar código por ahora. Cuando `usuario` desarrolle su read side real y (si aplica) un `EstadoUsuario`, esta feature debe: (1) reemplazar el mock de `UsuarioQueryOutputPort` por una implementación real, y (2) agregar una `EstadoUsuarioHabilitadoRule` (o equivalente) orquestada por `AgregarRepresentanteComiteCurriculumValidator`.

## 4. Modelo DDD del Contexto

### Entidad raíz
- **Clase:** `RepresentanteComiteCurriculumDomain`
- **¿Extiende `AggregateRoot`?:** **Sí** — coherente con la sección "Eventos de Dominio" abajo. Emite el evento `RepresentanteComiteCurriculumAgregadoEvent` tras ser creado.

### Atributos por objeto de dominio

**RepresentanteComiteCurriculum (aggregate root):**

| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| `usuario` | `UUID` | — | Sí | No | No | FK a `usuario.id`. Es también la PK del representante. |

**Combinaciones únicas:** `usuario` es PK (`PRIMARY KEY`) + `UNIQUE` en Flyway. La validación previa ocurre en el use case vía `RepresentanteComiteUnicoRule` antes de intentar persistir.

### Eventos de Dominio

| Evento | Clase | temaEvento | Consumidor | Cuándo se emite |
|---|---|---|---|---|
| Representante del comité agregado | `RepresentanteComiteCurriculumAgregadoEvent` | `usuarios.representante-comite-curriculum.agregado` | Ninguno conocido (se anticipa para auditoría/futuros consumidores) | Después de persistir exitosamente el representante, antes de cerrar la transacción |

**Payload del evento:** `{ representanteId: UUID, usuarioId: UUID, email: String, rol: String }` — lleva el `email` y `rol` del usuario para que eventuales consumidores no tengan que consultar de vuelta a `usuarios`.

## 5. Integraciones Externas

**No aplica.** Esta HU solo interactúa con PostgreSQL (tabla `representante_comite_curriculum`) y RabbitMQ (para emitir el evento de dominio). No hay integración con Keycloak, SMTP, MinIO ni sistemas HTTP externos más allá de lo estándar del stack.

## 6. Árbol de Archivos a Crear / Modificar

| Capa | Ruta completa desde raíz del repo | Tipo | Responsabilidad |
|---|---|---|---|
| **domain** | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/RepresentanteComiteCurriculumDomain.java` | NUEVO | Aggregate root — directo, sin subcarpeta `aggregate/`. Extiende `AggregateRoot`. Constructor privado, `crear(usuario)` + `reconstruir(usuario)`, sin Lombok. |
| domain | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/event/RepresentanteComiteCurriculumAgregadoEvent.java` | NUEVO | Evento de dominio, extiende `DomainEvent`. |
| domain | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/exception/UsuarioNoEncontradoException.java` | NUEVO | `DomainException` → 422. Lanzada por `UsuarioExisteRule`. |
| domain | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/exception/UsuarioYaEsRepresentanteException.java` | NUEVO | `DomainException` → 422. Lanzada por `RepresentanteComiteUnicoRule`. |
| domain | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/model/ExistenciaUsuario.java` | NUEVO | `record` input de `UsuarioExisteRule`: `(UUID usuario, boolean existe)`. |
| domain | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/model/DisponibilidadRepresentante.java` | NUEVO | `record` input de `RepresentanteComiteUnicoRule`: `(UUID usuario, boolean yaEsRepresentante)`. |
| domain | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/rules/UsuarioExisteRule.java` | NUEVO | Interfaz `DomainRule<ExistenciaUsuario>`. |
| domain | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/rules/impl/UsuarioExisteRuleImpl.java` | NUEVO | Implementación pura (sin Spring, sin inyección). |
| domain | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/rules/RepresentanteComiteUnicoRule.java` | NUEVO | Interfaz `DomainRule<DisponibilidadRepresentante>`. |
| domain | `usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/representantecomitecurriculum/rules/impl/RepresentanteComiteUnicoRuleImpl.java` | NUEVO | Implementación pura (sin Spring, sin inyección). |
| **application** | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/primaryport/model/AgregarRepresentanteComiteCurriculumCommand.java` | NUEVO | `record` con `crear(String usuario)` factory. Valida formato UUID con `ValidatorUUID`. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/primaryport/interactor/AgregarRepresentanteComiteCurriculumInteractor.java` | NUEVO | Interfaz del entry point. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/primaryport/interactor/impl/AgregarRepresentanteComiteCurriculumInteractorImpl.java` | NUEVO | `@Component`. Dueño de `@Transactional(transactionManager = "usuariosTransactionManager")`. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/usecase/AgregarRepresentanteComiteCurriculumUseCase.java` | NUEVO | Interfaz del colaborador interno (NO bajo `primaryport/`). |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/usecase/impl/AgregarRepresentanteComiteCurriculumUseCaseImpl.java` | NUEVO | `@Component`. Orquestación de negocio, sin transacción propia. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/validator/AgregarRepresentanteComiteCurriculumValidator.java` | NUEVO | Interfaz del validador. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/validator/impl/AgregarRepresentanteComiteCurriculumValidatorImpl.java` | NUEVO | `@Component`. Puro: construye las Rules en su constructor, sin inyectar puertos. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/finder/UsuarioExisteFinder.java` | NUEVO | Interfaz `Finder<UUID, Boolean>`. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/finder/impl/UsuarioExisteFinderImpl.java` | NUEVO | `@Component`. Delega a `UsuarioQueryOutputPort.existsById(...)` (**mock temporal**). |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/finder/RepresentanteComiteExisteFinder.java` | NUEVO | Interfaz `Finder<UUID, Boolean>`. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/finder/impl/RepresentanteComiteExisteFinderImpl.java` | NUEVO | `@Component`. Delega a `RepresentanteComiteCurriculumOutputPort.existePorUsuario(UUID)`. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/secondaryport/RepresentanteComiteCurriculumOutputPort.java` | NUEVO | Output port del command side. Métodos: `agregar(Entity)`, `existePorUsuario(UUID) → Boolean`. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/secondaryport/entity/RepresentanteComiteCurriculumEntity.java` | NUEVO | `record` plain, sin JPA. Solo `UUID usuario`. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/representantecomitecurriculum/command/secondaryport/mapper/RepresentanteComiteCurriculumMapper.java` | NUEVO | `final`, constructor privado, static `toDomain(Entity)` / `toEntity(Domain)`. |
| application | `usuarios/application/src/main/java/com/arquisoft/usuarios/application/usuario/query/secondaryport/UsuarioQueryOutputPort.java` | NUEVO (**mock temporal**) | Output port read-side de `usuario`. Solo método `existsById(UUID) → Boolean`. **Nota:** este paquete `query/` de `usuario` no existe hoy — es un mock temporal a reemplazar cuando `usuario` desarrolle su read side real. |
| **infrastructure** | `usuarios/infrastructure/src/main/java/com/arquisoft/usuarios/infrastructure/representantecomitecurriculum/command/primaryadapter/web/AgregarRepresentanteComiteCurriculumController.java` | NUEVO | Un controller por acción. `@Tag`, `@Operation`, `@ApiResponses`, `@SecurityRequirement`. |
| infrastructure | `usuarios/infrastructure/src/main/java/com/arquisoft/usuarios/infrastructure/representantecomitecurriculum/command/primaryadapter/web/dto/AgregarRepresentanteComiteCurriculumRequestDTO.java` | NUEVO | `record` bare sin anotaciones Jakarta (convención fichaperfil). Solo `String usuario`. |
| infrastructure | `usuarios/infrastructure/src/main/java/com/arquisoft/usuarios/infrastructure/representantecomitecurriculum/command/primaryadapter/web/dto/AgregarRepresentanteComiteCurriculumResponseDTO.java` | NUEVO | `record` con `UUID id`. |
| infrastructure | `usuarios/infrastructure/src/main/java/com/arquisoft/usuarios/infrastructure/representantecomitecurriculum/command/primaryadapter/web/mapper/AgregarRepresentanteComiteCurriculumRequestMapper.java` | NUEVO | `final`, constructor privado, static `toCommand(dto)` que llama `Command.crear(...)`. |
| infrastructure | `usuarios/infrastructure/src/main/java/com/arquisoft/usuarios/infrastructure/representantecomitecurriculum/command/secondaryadapter/entity/RepresentanteComiteCurriculumJpaEntity.java` | NUEVO | `@Entity`, Lombok (`@Getter/@Builder/@NoArgsConstructor/@AllArgsConstructor`). Solo `UUID usuario` (PK). |
| infrastructure | `usuarios/infrastructure/src/main/java/com/arquisoft/usuarios/infrastructure/representantecomitecurriculum/command/secondaryadapter/mapper/RepresentanteComiteCurriculumJpaMapper.java` | NUEVO | `final`, constructor privado, static `toEntity(JpaEntity)` / `toJpaEntity(Entity)`. |
| infrastructure | `usuarios/infrastructure/src/main/java/com/arquisoft/usuarios/infrastructure/representantecomitecurriculum/command/secondaryadapter/repository/RepresentanteComiteCurriculumCommandOutputAdapter.java` | NUEVO | `@Component`. Implementa `RepresentanteComiteCurriculumOutputPort`. Delega a `RepresentanteComiteCurriculumCommandRepository`. |
| infrastructure | `usuarios/infrastructure/src/main/java/com/arquisoft/usuarios/infrastructure/representantecomitecurriculum/command/secondaryadapter/repository/RepresentanteComiteCurriculumCommandRepository.java` | NUEVO | Spring Data `JpaRepository<RepresentanteComiteCurriculumJpaEntity, UUID>`. Método `existsByUsuario(UUID) → Boolean` (para `existePorUsuario`). |
| infrastructure | `usuarios/infrastructure/src/main/java/com/arquisoft/usuarios/infrastructure/usuario/query/secondaryadapter/repository/UsuarioQueryOutputAdapter.java` | NUEVO (**mock temporal**) | `@Component`. Implementa `UsuarioQueryOutputPort`. Devuelve siempre `true` en `existsById(UUID)` hasta que `usuario` tenga read side real. |
| infrastructure | `usuarios/infrastructure/src/main/resources/db/migration/usuarios/V1.2__crear_tabla_representante_comite_curriculum.sql` | NUEVO | Migración Flyway. Crea tabla `representante_comite_curriculum` con PK/FK `usuario_id` apuntando a `usuario(id)`. |
| **shared** | `shared/message/src/main/java/com/arquisoft/shared/message/constant/UsuariosCodes.java` | MODIFICAR | Añadir constantes de códigos de error: `RepresentanteComiteCurriculum.USUARIO_REQUERIDO`, `USUARIO_FORMATO_INVALIDO`, `USUARIO_NO_ENCONTRADO`, `USUARIO_YA_ES_REPRESENTANTE`. |
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/constant/UsuariosFields.java` | MODIFICAR | Añadir `RepresentanteComiteCurriculum.USUARIO = "usuario"`. |
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/annotation/UsuariosApiMessages.java` | MODIFICAR | Añadir clase interna `RepresentanteComiteCurriculum` con `TAG_NAME`, `TAG_DESCRIPTION`, `AGREGAR_SUMMARY`, `AGREGAR_DESCRIPTION`, `AGREGAR_RESP_201/400/401/403/422`. |
| shared | `shared/message/src/main/java/com/arquisoft/shared/message/security/UsuariosAuthorities.java` | MODIFICAR | Añadir `String AGREGAR_REPRESENTANTE_COMITE_CURRICULUM = "usuarios:representante-comite-curriculum:create";` y `String HAS_AGREGAR_REPRESENTANTE_COMITE_CURRICULUM = "hasAuthority('" + AGREGAR_REPRESENTANTE_COMITE_CURRICULUM + "')"` en clase interna `Expresiones`. |

**Nota sobre el mock temporal:** `UsuarioQueryOutputPort` + `UsuarioQueryOutputAdapter` se crean como **stubs temporales** hasta que la feature `usuario` tenga su propio paquete `query/` real. El adaptador devuelve siempre `true` en `existsById(UUID)`, simulando que todo usuario existe. Debe sustituirse cuando `usuario` desarrolle su read side.

## 7. Detalle por Archivo

### Domain

#### `RepresentanteComiteCurriculumDomain.java`
- **Paquete:** `com.arquisoft.usuarios.domain.representantecomitecurriculum`
- **Tipo:** Aggregate root, extiende `AggregateRoot`
- **Responsabilidad:** Representar un representante del comité curriculum. Emitir el evento `RepresentanteComiteCurriculumAgregadoEvent` tras ser creado.
- **Métodos principales:**
  - `crear(UUID usuario) → RepresentanteComiteCurriculumDomain` (factory static): valida formato UUID con `ValidatorObjeto.noNulo`, acumula en `ValidationResult`, publica el evento.
  - `reconstruir(UUID usuario) → RepresentanteComiteCurriculumDomain` (factory static): sin validación, sin evento — solo para cargar desde DB.
  - `getUsuario() → UUID`
- **Dependencias:** `shared:domain` (`AggregateRoot`, `DomainEvent`), `shared:validation` (`ValidationResult`, `ValidatorObjeto`, `DomainValidationException`), `shared:message` (códigos/campos), `shared:util` (`UtilUUID`).
- **Sin Lombok, sin Spring.** Constructor privado, campos no-`final` con setters privados, solo getters públicos.

#### `RepresentanteComiteCurriculumAgregadoEvent.java`
- **Paquete:** `com.arquisoft.usuarios.domain.representantecomitecurriculum.event`
- **Tipo:** Domain event, extiende `DomainEvent`
- **Payload:** `representanteId` (UUID), `usuarioId` (UUID), `email` (String), `rol` (String).
- **Responsabilidad:** Transportar los datos del representante agregado a eventuales consumidores.

#### `UsuarioNoEncontradoException.java`
- **Paquete:** `com.arquisoft.usuarios.domain.representantecomitecurriculum.exception`
- **Tipo:** `DomainException` → 422
- **Responsabilidad:** Indicar que el usuario referenciado no existe en la tabla `usuario`.
- **Mensaje:** resuelto desde el catálogo (`UsuariosKey.RepresentanteComiteCurriculum.USUARIO_NO_ENCONTRADO`, 1 parámetro: `usuario`).

#### `UsuarioYaEsRepresentanteException.java`
- **Paquete:** `com.arquisoft.usuarios.domain.representantecomitecurriculum.exception`
- **Tipo:** `DomainException` → 422
- **Responsabilidad:** Indicar que el usuario ya está registrado como representante (unicidad violada).
- **Mensaje:** resuelto desde el catálogo (`UsuariosKey.RepresentanteComiteCurriculum.USUARIO_YA_ES_REPRESENTANTE`, 1 parámetro: `usuario`).

#### `ExistenciaUsuario.java` / `DisponibilidadRepresentante.java`
- **Paquete:** `com.arquisoft.usuarios.domain.representantecomitecurriculum.model`
- **Tipo:** `record` input de reglas de dominio.
- **Responsabilidad:** Transportar el dato ya fetched (el UUID del usuario + el boolean de existencia/disponibilidad) a las Rules.

#### `UsuarioExisteRule` / `RepresentanteComiteUnicoRule` (interfaces + impl)
- **Paquete:** `domain/representantecomitecurriculum/rules/` + `impl/`
- **Tipo:** Interfaces `DomainRule<T>` y sus implementaciones puras (sin Spring, sin inyección).
- **Responsabilidad:** Decidir y lanzar la excepción correspondiente si el boolean es `false`.
- **Sin constructor con dependencias** — construidas con `new` dentro del `ValidatorImpl`.

### Application

#### `AgregarRepresentanteComiteCurriculumCommand.java`
- **Paquete:** `com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.model`
- **Tipo:** `record(UUID usuario)`
- **Métodos:**
  - Compact constructor: `usuario = usuario;` (ya es UUID, sin normalización).
  - `crear(String usuario) → AgregarRepresentanteComiteCurriculumCommand`: valida formato con `ValidatorTexto.noEnBlanco` + `ValidatorUUID.uuidValido`, acumula en `ValidationResult`, convierte con `UtilUUID.generarUUIDDesdeTexto`, retorna el command tipado.

#### `AgregarRepresentanteComiteCurriculumInteractor` / `InteractorImpl`
- **Paquete:** `application/representantecomitecurriculum/command/primaryport/interactor/` + `impl/`
- **Responsabilidad:** Entry point del use case. Dueño de `@Transactional(transactionManager = "usuariosTransactionManager")`.
- **Métodos:**
  - `ejecutar(AgregarRepresentanteComiteCurriculumCommand) → UUID`
- **Dependencias:** inyecta `AgregarRepresentanteComiteCurriculumUseCase` (interfaz), delega.

#### `AgregarRepresentanteComiteCurriculumUseCase` / `UseCaseImpl`
- **Paquete:** `application/representantecomitecurriculum/command/usecase/` + `impl/` (NO bajo `primaryport/`)
- **Responsabilidad:** Orquestar la lógica de negocio — fetch state, validar, crear aggregate, persistir, drenar eventos, publicar.
- **Métodos:**
  - `ejecutar(AgregarRepresentanteComiteCurriculumCommand) → UUID`
- **Dependencias:** inyecta `AgregarRepresentanteComiteCurriculumValidator`, `UsuarioExisteFinder`, `RepresentanteComiteExisteFinder`, `RepresentanteComiteCurriculumOutputPort`, `EventPublisher`.
- **Flujo:**
  1. `usuarioExiste = usuarioExisteFinder.buscar(command.usuario());`
  2. `yaEsRepresentante = representanteComiteExisteFinder.buscar(command.usuario());`
  3. `validator.validar(command.usuario(), usuarioExiste, yaEsRepresentante);`
  4. `aggregate = RepresentanteComiteCurriculumDomain.crear(command.usuario());`
  5. `entity = RepresentanteComiteCurriculumMapper.toEntity(aggregate);`
  6. `outputPort.agregar(entity);`
  7. `aggregate.obtenerEventos().forEach(eventPublisher::publicar);`
  8. `return aggregate.getUsuario();` (el id = usuario)

#### `AgregarRepresentanteComiteCurriculumValidator` / `ValidatorImpl`
- **Paquete:** `application/representantecomitecurriculum/command/validator/` + `impl/`
- **Responsabilidad:** Orquestar las Rules. Puro: sin inyección, construye las Rules en su constructor con `new`.
- **Métodos:**
  - Constructor: `this.usuarioExisteRule = new UsuarioExisteRuleImpl(); this.representanteComiteUnicoRule = new RepresentanteComiteUnicoRuleImpl();`
  - `validar(UUID usuario, boolean usuarioExiste, boolean yaEsRepresentante) → void`
- **Flujo:**
  1. `usuarioExisteRule.validar(new ExistenciaUsuario(usuario, usuarioExiste));`
  2. `representanteComiteUnicoRule.validar(new DisponibilidadRepresentante(usuario, yaEsRepresentante));`
- **Sin `if`** — cada Rule decide y lanza su excepción si falla.

#### `UsuarioExisteFinder` / `RepresentanteComiteExisteFinder` (interfaces + impl)
- **Paquete:** `application/representantecomitecurriculum/command/finder/` + `impl/`
- **Tipo:** `Finder<UUID, Boolean>`
- **Responsabilidad:** Consultar el puerto y devolver el boolean — nunca lanzan excepción por "not found", siempre retornan `Boolean`.
- **Dependencias:**
  - `UsuarioExisteFinderImpl` inyecta `UsuarioQueryOutputPort` (**mock temporal**).
  - `RepresentanteComiteExisteFinderImpl` inyecta `RepresentanteComiteCurriculumOutputPort`.

#### `RepresentanteComiteCurriculumOutputPort`
- **Paquete:** `application/representantecomitecurriculum/command/secondaryport`
- **Métodos:**
  - `agregar(RepresentanteComiteCurriculumEntity) → void`
  - `existePorUsuario(UUID usuario) → Boolean`
- **Habla `Entity`, nunca `Domain`.**

#### `RepresentanteComiteCurriculumEntity`
- **Paquete:** `application/representantecomitecurriculum/command/secondaryport/entity`
- **Tipo:** `record(UUID usuario)` plain, sin JPA, sin Lombok.

#### `RepresentanteComiteCurriculumMapper`
- **Paquete:** `application/representantecomitecurriculum/command/secondaryport/mapper`
- **Tipo:** `final`, constructor privado, static methods.
- **Métodos:**
  - `toDomain(RepresentanteComiteCurriculumEntity) → RepresentanteComiteCurriculumDomain`: llama `reconstruir(entity.usuario())`.
  - `toEntity(RepresentanteComiteCurriculumDomain) → RepresentanteComiteCurriculumEntity`: llama `new RepresentanteComiteCurriculumEntity(domain.getUsuario())`.

#### `UsuarioQueryOutputPort` (**mock temporal**)
- **Paquete:** `application/usuario/query/secondaryport`
- **Métodos:**
  - `existsById(UUID id) → Boolean`
- **Nota:** este paquete `query/` de `usuario` no existe hoy. Es un stub temporal a reemplazar cuando `usuario` tenga read side real.

### Infrastructure

#### `AgregarRepresentanteComiteCurriculumController.java`
- **Paquete:** `infrastructure/representantecomitecurriculum/command/primaryadapter/web`
- **Anotaciones:**
  - `@RestController`
  - `@RequestMapping("${rutas.usuarios.representantes-comite-curriculum.base:/representantes-comite-curriculum}")`
  - `@RequiredArgsConstructor`
  - `@Tag(name = UsuariosApiMessages.RepresentanteComiteCurriculum.TAG_NAME, description = TAG_DESCRIPTION)`
- **Métodos:**
  - `agregar(@RequestBody AgregarRepresentanteComiteCurriculumRequestDTO request) → ResponseEntity<AgregarRepresentanteComiteCurriculumResponseDTO>`
    - `@PostMapping`
    - `@PreAuthorize(UsuariosAuthorities.Expresiones.HAS_AGREGAR_REPRESENTANTE_COMITE_CURRICULUM)`
    - `@Operation(summary = ..., description = ..., security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))`
    - `@ApiResponses`: 201 (created), 400 (bad request), 401 (unauthorized), 403 (forbidden), 422 (unprocessable entity)
- **Flujo:**
  1. `command = AgregarRepresentanteComiteCurriculumRequestMapper.toCommand(request);`
  2. `UUID id = interactor.ejecutar(command);`
  3. `response = new AgregarRepresentanteComiteCurriculumResponseDTO(id);`
  4. `return ResponseEntity.status(HttpStatus.CREATED).body(response);`
- **Dependencias:** inyecta `AgregarRepresentanteComiteCurriculumInteractor`.
- **Sin `@Valid`** — la validación ocurre en `Command.crear(...)`, no en el DTO.

#### `AgregarRepresentanteComiteCurriculumRequestDTO.java`
- **Paquete:** `infrastructure/representantecomitecurriculum/command/primaryadapter/web/dto`
- **Tipo:** `record(String usuario)` bare, **sin anotaciones Jakarta** (convención fichaperfil).

#### `AgregarRepresentanteComiteCurriculumResponseDTO.java`
- **Paquete:** `infrastructure/representantecomitecurriculum/command/primaryadapter/web/dto`
- **Tipo:** `record(UUID id)`

#### `AgregarRepresentanteComiteCurriculumRequestMapper.java`
- **Paquete:** `infrastructure/representantecomitecurriculum/command/primaryadapter/web/mapper`
- **Tipo:** `final`, constructor privado, static `toCommand(dto)`.
- **Método:**
  - `toCommand(AgregarRepresentanteComiteCurriculumRequestDTO dto) → AgregarRepresentanteComiteCurriculumCommand`: llama `AgregarRepresentanteComiteCurriculumCommand.crear(dto.usuario())`.

#### `RepresentanteComiteCurriculumJpaEntity.java`
- **Paquete:** `infrastructure/representantecomitecurriculum/command/secondaryadapter/entity`
- **Tipo:** `@Entity`, `@Table(name = "representante_comite_curriculum")`
- **Lombok:** `@Getter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **Campos:**
  - `@Id @Column(name = "usuario_id") UUID usuario;`

#### `RepresentanteComiteCurriculumJpaMapper.java`
- **Paquete:** `infrastructure/representantecomitecurriculum/command/secondaryadapter/mapper`
- **Tipo:** `final`, constructor privado, static methods.
- **Métodos:**
  - `toEntity(RepresentanteComiteCurriculumJpaEntity) → RepresentanteComiteCurriculumEntity`: llama `new RepresentanteComiteCurriculumEntity(jpa.getUsuario())`.
  - `toJpaEntity(RepresentanteComiteCurriculumEntity) → RepresentanteComiteCurriculumJpaEntity`: llama builder.

#### `RepresentanteComiteCurriculumCommandOutputAdapter.java`
- **Paquete:** `infrastructure/representantecomitecurriculum/command/secondaryadapter/repository`
- **Tipo:** `@Component`, implementa `RepresentanteComiteCurriculumOutputPort`.
- **Métodos:**
  - `agregar(RepresentanteComiteCurriculumEntity entity) → void`: convierte a `JpaEntity` con `JpaMapper.toJpaEntity(entity)`, llama `repository.save(...)`.
  - `existePorUsuario(UUID usuario) → Boolean`: llama `repository.existsByUsuario(usuario)`.
- **Dependencias:** inyecta `RepresentanteComiteCurriculumCommandRepository`.

#### `RepresentanteComiteCurriculumCommandRepository.java`
- **Paquete:** `infrastructure/representantecomitecurriculum/command/secondaryadapter/repository`
- **Tipo:** `JpaRepository<RepresentanteComiteCurriculumJpaEntity, UUID>`
- **Métodos custom:**
  - `existsByUsuario(UUID usuario) → Boolean` (query method derivado por nombre).

#### `UsuarioQueryOutputAdapter.java` (**mock temporal**)
- **Paquete:** `infrastructure/usuario/query/secondaryadapter/repository`
- **Tipo:** `@Component`, implementa `UsuarioQueryOutputPort`.
- **Métodos:**
  - `existsById(UUID id) → Boolean`: **devuelve siempre `true`** (mock temporal).
- **Comentario en Javadoc:** `// TODO: Mock temporal. Sustituir cuando la feature usuario tenga su propio paquete query/ real con UsuarioJpaQueryEntity y repository.`

#### `V1.2__crear_tabla_representante_comite_curriculum.sql`
- **Ubicación:** `usuarios/infrastructure/src/main/resources/db/migration/usuarios/`
- **Contenido:**
```sql
-- ==============================================================================
-- Contexto: usuarios | Base de datos: usuarios
-- ==============================================================================

-- Tabla de representantes del comité curriculum.
-- Cada representante es un usuario existente (FK a usuario).
CREATE TABLE representante_comite_curriculum (
    usuario_id UUID NOT NULL,
    PRIMARY KEY (usuario_id),
    CONSTRAINT fk_representante_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);
```

### Shared (modificaciones)

#### `UsuariosCodes.java`
- Añadir clase interna `RepresentanteComiteCurriculum`:
  - `USUARIO_REQUERIDO = "usuarios.representante_comite_curriculum.usuario.requerido"`
  - `USUARIO_FORMATO_INVALIDO = "usuarios.representante_comite_curriculum.usuario.formato_invalido"`
  - `USUARIO_NO_ENCONTRADO = "usuarios.representante_comite_curriculum.usuario.no_encontrado"`
  - `USUARIO_YA_ES_REPRESENTANTE = "usuarios.representante_comite_curriculum.usuario.ya_es_representante"`

#### `UsuariosFields.java`
- Añadir clase interna `RepresentanteComiteCurriculum`:
  - `USUARIO = "usuario"`

#### `UsuariosApiMessages.java`
- Añadir clase interna `RepresentanteComiteCurriculum`:
  - `TAG_NAME = "Representantes del Comité Curriculum"`
  - `TAG_DESCRIPTION = "Operaciones para gestionar representantes del comité curriculum"`
  - `AGREGAR_SUMMARY = "Agregar un nuevo representante del comité curriculum"`
  - `AGREGAR_DESCRIPTION = "Registra un usuario existente como representante del comité curriculum"`
  - `AGREGAR_RESP_201 = "Representante agregado exitosamente"`
  - `AGREGAR_RESP_400 = "Solicitud inválida"`
  - `AGREGAR_RESP_401 = "No autenticado"`
  - `AGREGAR_RESP_403 = "Acceso denegado"`
  - `AGREGAR_RESP_422 = "Usuario no encontrado o ya es representante"`

#### `UsuariosAuthorities.java`
- Añadir en la raíz:
  - `String AGREGAR_REPRESENTANTE_COMITE_CURRICULUM = "usuarios:representante-comite-curriculum:create";`
- Añadir en clase interna `Expresiones`:
  - `String HAS_AGREGAR_REPRESENTANTE_COMITE_CURRICULUM = "hasAuthority('" + UsuariosAuthorities.AGREGAR_REPRESENTANTE_COMITE_CURRICULUM + "')";`

## 8. Endpoints REST

| Método | Ruta (sin /api) | Request | Response | HTTP | Client role | Swagger |
|---|---|---|---|---|---|---|
| POST | `/representantes-comite-curriculum` | `AgregarRepresentanteComiteCurriculumRequestDTO { usuario: String }` | `AgregarRepresentanteComiteCurriculumResponseDTO { id: UUID }` | 201 Created | `usuarios:representante-comite-curriculum:create` | `@Tag(RepresentanteComiteCurriculum)` + `@Operation(summary = AGREGAR_SUMMARY)` + `@ApiResponses(201/400/401/403/422)` + `@SecurityRequirement(BEARER_AUTH)` |

**Nota:** el endpoint devuelve `201` con el `id` del representante (que es el mismo `usuario_id`, ya que es la PK). No se usa `Location` header porque el recurso es un singleton por usuario (no hay colección `/representantes/{id}` separada del usuario).

## 9. Seguridad y Autorización (Keycloak)

| Client role | Roles realm que lo poseen | Endpoint(s) | Descripción |
|---|---|---|---|
| `usuarios:representante-comite-curriculum:create` | `administrador` | `POST /representantes-comite-curriculum` | Permite al Administrador agregar un nuevo representante del comité curriculum |

**Configuración en Keycloak (manual, fuera del alcance del código):**
1. Crear el client role `usuarios:representante-comite-curriculum:create` en el client de backend.
2. Asignar ese client role al role realm `administrador`.

## 10. Eventos RabbitMQ

| Dirección | Exchange | Routing Key | Payload | Contexto receptor |
|---|---|---|---|---|
| Salida (produce) | `arquisoft.eventos` | `usuarios.representante-comite-curriculum.agregado` | `{ representanteId: UUID, usuarioId: UUID, email: String, rol: String }` | Ninguno conocido (se anticipa para auditoría/futuros consumidores) |

**Configuración:** el evento se publica a través de `EventPublisher` (`shared:amqp`) con el `temaEvento` configurado en `RepresentanteComiteCurriculumAgregadoEvent`. El exchange y las políticas de RabbitMQ ya están configuradas globalmente en el contexto `shared`.

## 11. Migración de Base de Datos

**Archivo:** `V1.2__crear_tabla_representante_comite_curriculum.sql` en `usuarios/infrastructure/src/main/resources/db/migration/usuarios/`

**Siguiente número verificado:** las migraciones actuales terminan en `V1.1`, por tanto la siguiente es `V1.2`.

**Sin prefijo de schema** (el contexto de Flyway ya apunta a `usuarios`).

**Sin FKs cruzadas entre contextos** — la FK apunta a `usuario(id)` que vive en el mismo schema `usuarios`.

**Contenido:**
```sql
-- ==============================================================================
-- Contexto: usuarios | Base de datos: usuarios
-- ==============================================================================

-- Tabla de representantes del comité curriculum.
-- Cada representante es un usuario existente (FK a usuario).
CREATE TABLE representante_comite_curriculum (
    usuario_id UUID NOT NULL,
    PRIMARY KEY (usuario_id),
    CONSTRAINT fk_representante_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);
```

## 12. Casos de Prueba Sugeridos

**Presupuesto:** HU pequeña (1 endpoint, 1 entidad raíz nueva) → **15-25 tests** esperados.

### Domain (7-9 tests)

**`RepresentanteComiteCurriculumDomainTest`:**
1. `debeCrearRepresentante_cuandoUsuarioValido()` — campo válido, `crear(...)` retorna aggregate, evento emitido.
2. `debeFallar_cuandoUsuarioNulo()` — `crear(null)` lanza `DomainValidationException` con `fieldErrors`.
3. `debeReconstruirRepresentante_sinValidacion()` — `reconstruir(...)` no valida, no emite evento.
4. `debeEmitirEvento_cuandoSeCreaNuevoRepresentante()` — `obtenerEventos()` contiene `RepresentanteComiteCurriculumAgregadoEvent`.

**`UsuarioExisteRuleImplTest`:**
1. `debePasar_cuandoUsuarioExiste()` — `validar(new ExistenciaUsuario(uuid, true))` no lanza.
2. `debeFallar_cuandoUsuarioNoExiste()` — `validar(new ExistenciaUsuario(uuid, false))` lanza `UsuarioNoEncontradoException`.

**`RepresentanteComiteUnicoRuleImplTest`:**
1. `debePasar_cuandoUsuarioNoEsRepresentante()` — `validar(new DisponibilidadRepresentante(uuid, false))` no lanza.
2. `debeFallar_cuandoUsuarioYaEsRepresentante()` — `validar(new DisponibilidadRepresentante(uuid, true))` lanza `UsuarioYaEsRepresentanteException`.

### Application (5-7 tests)

**`AgregarRepresentanteComiteCurriculumCommandTest`:**
1. `debeCrearCommand_cuandoUsuarioFormatoValido()` — `crear("uuid-valido")` retorna command tipado.
2. `debeFallar_cuandoUsuarioVacio()` — `crear("")` lanza `DomainValidationException` con `fieldErrors`.
3. `debeFallar_cuandoUsuarioFormatoInvalido()` — `crear("no-es-uuid")` lanza `DomainValidationException` con `fieldErrors`.

**`AgregarRepresentanteComiteCurriculumUseCaseImplTest`:**
1. `debeAgregarRepresentante_cuandoDatosValidos()` — mock finders retornan `true`/`false`, validator no lanza, outputPort mockeado, retorna UUID.
2. `debeFallar_cuandoUsuarioNoExiste()` — finder retorna `false`, validator lanza `UsuarioNoEncontradoException`.
3. `debeFallar_cuandoUsuarioYaEsRepresentante()` — finder retorna `true`, validator lanza `UsuarioYaEsRepresentanteException`.
4. `debePublicarEvento_cuandoRepresentanteAgregado()` — verifica que `eventPublisher.publicar(...)` fue llamado con el evento correcto.

**`AgregarRepresentanteComiteCurriculumValidatorImplTest`:**
1. `debeOrquestarReglas_enOrdenCorrecto()` — llama las Rules en orden: existencia primero, unicidad después.
2. `debeFallar_cuandoUsuarioNoExiste()` — `validar(uuid, false, false)` lanza `UsuarioNoEncontradoException`.
3. `debeFallar_cuandoUsuarioYaEsRepresentante()` — `validar(uuid, true, true)` lanza `UsuarioYaEsRepresentanteException`.

### Infrastructure (3-5 tests)

**`RepresentanteComiteCurriculumCommandOutputAdapterTest` (unit):**
1. `debeAgregar_cuandoEntityValida()` — mock repository, verifica `save(...)` llamado con JpaEntity correcto.
2. `debeRetornarTrue_cuandoExistePorUsuario()` — mock repository retorna `true`, verifica delegación.

**`AgregarRepresentanteComiteCurriculumControllerTest` (`@WebMvcTest`):**
1. `debeRetornar201_cuandoRepresentanteAgregadoExitosamente()` — mock interactor retorna UUID, verifica response 201 con body `{ id: UUID }`.
2. `debeRetornar422_cuandoUsuarioNoEncontrado()` — mock interactor lanza `UsuarioNoEncontradoException`, verifica 422.
3. `debeRetornar422_cuandoUsuarioYaEsRepresentante()` — mock interactor lanza `UsuarioYaEsRepresentanteException`, verifica 422.
4. `debeRetornar403_cuandoRolIncorrecto()` — autenticado con rol no-administrador, verifica 403.
5. `debeRetornar401_cuandoSinToken()` — request sin JWT, verifica 401.

**`RepresentanteComiteCurriculumCommandRepositoryTest` (`@DataJpaTest`):**
1. `debeGuardarRepresentante_cuandoUsuarioExiste()` — seed `usuario` con `TestEntityManager`, save `RepresentanteComiteCurriculumJpaEntity`, verifica persistido.
2. `debeRetornarTrue_cuandoExistePorUsuario()` — save entity, llama `existsByUsuario(...)`, verifica `true`.
3. `debeRetornarFalse_cuandoNoExistePorUsuario()` — llama `existsByUsuario(uuid-inexistente)`, verifica `false`.
4. `debeFallar_cuandoUsuarioNoExiste()` — intenta save con FK inválido, verifica `DataIntegrityViolationException` (validación de FK).
5. `debeFallar_cuandoUsuarioDuplicado()` — save dos veces con el mismo `usuario_id`, verifica `DataIntegrityViolationException` (PK duplicada).

**Total aproximado:** 7-9 (domain) + 5-7 (application) + 3-5 (infrastructure) = **15-21 tests**, dentro del presupuesto para HU pequeña.

**Consolidación de asserts:** un test por escenario — no duplicar tests por cada campo de un DTO ni testear getters/setters ni métodos privados. No testear `ApplicationReadyEvent` listeners ni logs de arranque (fuera de alcance de tests unitarios).

## 13. Checklist de Implementación

- [ ] Entidad raíz `RepresentanteComiteCurriculumDomain`: constructor privado, campos no-`final` con setters privados, solo getters, `crear`/`reconstruir` (nunca `build`/`rebuild`), sin Lombok, sin Spring, sin subcarpeta `aggregate/`
- [ ] **Extiende `AggregateRoot`** — coherente con la sección "Eventos que emite" (sí, emite evento)
- [ ] Cada regla de la sección 3 se valida donde corresponde: invariante (formato UUID) → `Command.crear(...)`/422; conjunto (existencia, unicidad) → use case vía Rules/422 — nunca `if/throw` fuera de lugar
- [ ] IDs siempre `UUID` (el `usuario` es UUID, que es también la PK del representante)
- [ ] `Interactor` dueño de `@Transactional(transactionManager = "usuariosTransactionManager")` con qualifier explícito; `UseCase` sin transacción propia
- [ ] `OutputPort` habla `Entity` (plain record), nunca `Domain`
- [ ] Excepciones nuevas extienden la base correcta: `UsuarioNoEncontradoException`, `UsuarioYaEsRepresentanteException` → `DomainException` → 422
- [ ] Identificador `usuario` en el RequestDTO: `String`, validado en `Command.crear(...)` vía `ValidatorUUID.uuidValido`, nunca con anotación Jakarta
- [ ] Controller documentado con `@Tag`/`@Operation`/`@ApiResponses`/`@SecurityRequirement` (ADR-011), un controller por acción
- [ ] `@PreAuthorize(UsuariosAuthorities.Expresiones.HAS_AGREGAR_REPRESENTANTE_COMITE_CURRICULUM)` — un solo client role por endpoint
- [ ] Migración Flyway `V1.2__crear_tabla_representante_comite_curriculum.sql` con el siguiente número real del contexto (verificado: V1.1 es el último), sin prefijo de schema
- [ ] Tests con patrón AAA (Arrange/Act/Assert), cobertura ≥75%
- [ ] Sin `@Bean TaskExecutor` manual (Virtual Threads ya gestionados por Spring Boot)
- [ ] **DTO sin anotaciones Jakarta** (convención fichaperfil), `RequestMapper` externo, `Command.crear(...)` hace la validación — desviación deliberada documentada
- [ ] **Mock temporal de `UsuarioQueryOutputPort`** documentado con `// TODO` y comentario en el plan
- [ ] Commit sugerido: `feat(usuarios): agregar representante comité curriculum (HU-253)`
- [ ] Códigos/campos/mensajes API añadidos a `UsuariosCodes`, `UsuariosFields`, `UsuariosApiMessages`, `UsuariosAuthorities` (`shared:message`)
- [ ] Evento `RepresentanteComiteCurriculumAgregadoEvent` con tema `usuarios.representante-comite-curriculum.agregado`, payload `{representanteId, usuarioId, email, rol}`

## 14. Trazabilidad del Flujo

| Etapa | Agente | Estado | Fecha | Notas |
|---|---|---|---|---|
| Planificación | @planificador | ✅ Completado | 2026-08-23 | PLAN-HU-253.md generado con desviación deliberada de convención DTO documentada |
| Desarrollo | @implementador | ✅ Completado | 2026-08-23 | Build -x test: sin errores. Auto-corrección: agregado `final` a `RepresentanteComiteCurriculumDomain` (Checkstyle). Mock temporal de `UsuarioQueryOutputPort` implementado |
| Tests | @tester | ✅ Completado | 2026-08-23 | 30 tests (8 domain + 11 application + 11 infrastructure) — PASAN. Cobertura infrastructure: 94% (CUMPLE ≥75%). Bloqueos documentados: (1) Repository reducido de 5→1 test por desviación conocida `UsuarioJpaEntity` (tabla usuario no existe en H2); (2) `check` de domain/application bloqueado por tests preexistentes de feature `usuario` (gap testFixtures — fuera de alcance HU-253) |
| Validación | @validator-analyze | ✅ Completado | 2026-08-23 | Score: 100/100 · Bloqueantes: 0 · APROBADO |
| Reporte | @validator-report | ✅ Completado | 2026-08-23 | Reporte persistido en .workspace/validator/validator-HU-253.md |
| Commit | @commit | ⏳ Pendiente | | |

---

**Fin del PLAN-HU-253.md**
