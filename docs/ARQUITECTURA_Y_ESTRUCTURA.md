> [!NOTE]
> Referencia profunda de arquitectura, con más detalle del que necesitan las skills de agentes.
> Los agentes cargan primero `.claude/skills/arquisoft-arquitectura/SKILL.md` (resumen conciso con
> referencias al código real de `fichas/fichaperfil`) y vienen aquí solo si necesitan el detalle
> extendido. Para estándares de código/testing/git, ver `.claude/skills/arquisoft-estandares/SKILL.md`.

# Arquitectura Hexagonal Modular - Documentación Completa

## Índice

1. [Visión General](#visión-general)
2. [Arquitectura Hexagonal (Puertos y Adaptadores)](#arquitectura-hexagonal-puertos-y-adaptadores)
3. [Estructura del Proyecto](#estructura-del-proyecto)
   - [Estructura de una feature dentro de un contexto](#estructura-de-una-feature-dentro-de-un-contexto)
   - [Convenciones de Nomenclatura](#convenciones-de-nomenclatura)
4. [Desviaciones conocidas respecto a la convención](#desviaciones-conocidas-respecto-a-la-convención)
5. [Módulo Seguridad](#módulo-seguridad)
6. [Módulo Shared](#módulo-shared)
7. [Eventos de Dominio](#eventos-de-dominio)
8. [Virtual Threads (ADR-008)](#virtual-threads-adr-008)
9. [Ejemplos Prácticos](#ejemplos-prácticos)
10. [Configuración y Build](#configuración-y-build)
11. [Flujos de Datos](#flujos-de-datos)
12. [Perfiles de Ejecución](#perfiles-de-ejecución)

---

## Visión General

Este proyecto implementa una **Arquitectura Hexagonal Modular** usando **Spring Boot 4.0.5** y **Gradle 9.0.0** como herramienta de construcción. La arquitectura se basa en el patrón de **Puertos y Adaptadores** con **9 contextos independientes** (4 con implementación real: `seguridad`, `usuarios`, `fichas`, `notificaciones`; 5 en scaffolding), Java 21 y Virtual Threads habilitados.

### Ventajas Principales

- **Independencia de frameworks**: La lógica de negocio es agnóstica a tecnologías externas
- **Modularidad**: Cada contexto es completamente independiente
- **Testabilidad**: Las dependencias se invierten, facilitando pruebas unitarias
- **Escalabilidad**: Los módulos pueden crecer sin afectar otros
- **Mantenibilidad**: El código está organizado por responsabilidades claras

---

## Arquitectura Hexagonal (Puertos y Adaptadores)

### Concepto Fundamental

La arquitectura hexagonal divide cada contexto en tres capas principales:

```
┌─────────────────────────────────────────┐
│      ADAPTADORES DE ENTRADA (IN)        │
│  Controllers, REST APIs, Event Listeners│
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│        PUERTOS DE ENTRADA (IN)          │
│   Use Cases, Interfaces de Negocio      │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      DOMINIO - NÚCLEO DEL NEGOCIO       │
│    Entidades, Modelos, Lógica Pura      │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│        PUERTOS DE SALIDA (OUT)          │
│   Repository Ports, Service Ports       │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│     ADAPTADORES DE SALIDA (OUT)         │
│  Repositorios, Servicios Externos       │
└─────────────────────────────────────────┘
```

### Componentes Clave

#### 1. Dominio (Domain)

- Contiene la lógica de negocio pura
- Modelos (entidades) y value objects
- Puertos (interfaces) que definen contratos
- **Sin dependencias** a frameworks o bases de datos

#### 2. Puertos de Entrada (In)

- Interfaces que definen casos de uso, sufijo `InputPort`
- Ejemplo: `CrearFichaInputPort`, `ConsultarFichasPerfilInputPort`

#### 3. Puertos de Salida (Out)

- Interfaces que definen dependencias externas, sufijo `OutputPort`
- Ejemplo: `FichaPerfilOutputPort`, `UsuarioOutputPort`

#### 4. Aplicación (Application)

- Implementa los puertos de entrada (casos de uso)
- Orquesta la lógica de negocio
- Contiene DTOs para transformación de datos

#### 5. Infraestructura (Infrastructure)

- Implementa los puertos de salida mediante `OutputAdapter` (e.g., `FichaPerfilCommandOutputAdapter`)
- Contiene adaptadores de entrada: REST controllers con sufijo `Controller` (e.g., `RegistrarFichaPerfilController`) y consumers AMQP con sufijo `Consumer` (e.g., `UsuarioCreadoConsumer`) — `InputAdapter` no se usa en el proyecto
- Configuración de Spring Boot, Flyway, etc.

---

## Estructura del Proyecto

```
arquisoft-backend/
│
├── build.gradle                          # Config principal Gradle
├── settings.gradle                       # Definición de módulos
├── gradle.properties                     # Versiones de dependencias
├── docker-compose.yml                    # Orquestación de contenedores
├── Dockerfile                            # Imagen Docker multi-stage
├── init-db.sql                           # Creación de 8 bases de datos (una por contexto con implementación o scaffolding, sin contar `seguridad`) + `keycloak`
│
├── config/                               # Configuraciones GENERALES del proyecto
│   └── checkstyle/
│       └── checkstyle.xml                # Reglas de estilo de código
│   # config/ contiene solo tooling del build (checkstyle, etc.)
│   # OpenApiConfig.java GLOBAL está en src/main/java/com/arquisoft/config/ — NO en config/ ni en contextos individuales.
│
├── src/
│   └── main/
│       ├── java/com/arquisoft/
│       │   └── ArquisoftApplication.java # Punto de entrada Spring Boot
│       └── resources/
│           ├── application.yml           # Config base
│           ├── application-dev.yml       # Perfil desarrollo
│           └── application-prod.yml      # Perfil producción
│
├── shared/                               # Módulo compartido (14 sub-módulos + el agregador `shared`)
│   ├── util/                             # UtilTexto, UtilUUID, UtilColeccion, UtilFecha, UtilNumero, UtilObjeto, UtilEnum
│   ├── exception/                        # BaseException/BaseError y las 5 excepciones base (módulo hoja, sin dependencias)
│   ├── validation/                       # ValidatorObjeto/Texto/Longitud/Numero/UUID/Coleccion + ValidationResult (Notification Pattern)
│   ├── domain/                           # DomainEvent (com.arquisoft.shared.events), DomainRule (com.arquisoft.shared.rules) — lo unico que la capa de dominio puede ver
│   ├── application/                      # UseCase/Interactor/Finder + EventPublisher (puerto) — contratos de la capa de aplicacion
│   ├── logger/                           # AppLogger + Slf4jAppLogger (bean prototype de AppLoggerConfig)
│   ├── redis/                            # RedisClient
│   ├── amqp/                             # SpringModulithEventPublisher, RabbitMQEventPublisher, AbstractEventConsumer, RabbitMQConfig
│   ├── web/                              # filter/TrazabilidadFilter, handler/GlobalAppExceptionHandler, dto/ErrorResponseDTO+PageResponseDTO, client/HttpClient
│   ├── tracing/                          # Contexto de traza sobre MDC — único shared con capas hexagonales internas
│   ├── minio/                            # Cliente MinIO
│   ├── jpa/                              # QueryRepository/SpecificationQueryRepository, CampoSpec/QueryJpaSpecification, PageableMapper/PaginationMapper
│   ├── query/                            # Vocabulario de consulta sin Spring: QueryCriteria, NodoFiltro, PaginatedResult, DTOs de filtro
│   ├── message/                          # CatalogoMensajes + los .properties del catálogo
│   └── notification/                     # EnvioNotificacionOutputPort (SMTP)
│
├── seguridad/                            # CONTEXTO: sin DB propia (Keycloak + Redis)
│   ├── domain/
│   │   └── src/main/java/com/arquisoft/seguridad/domain/
│   │       └── auth/
│   │           ├── SesionDomain.java       # Aggregate root: sesión activa
│   │           ├── TokenDomain.java        # Aggregate root: validación de un JWT
│   │           ├── model/                  # CredencialesSesion, IdentidadToken (value objects)
│   │           └── exception/
│   │               └── AuthenticationException.java
│   ├── application/
│   │   └── src/main/java/com/arquisoft/seguridad/application/
│   │       └── auth/command/
│   │           ├── primaryport/
│   │           │   ├── interactor/         # Autenticar/CerrarSesion/Refrescar/ValidarToken Interactor(+Impl) — sin @Transactional, no hay DataSource
│   │           │   └── model/              # AutenticarUsuarioCommand, TokenSesionCommand
│   │           ├── usecase/                # *UseCase(+Impl); colaborador interno, no bajo primaryport/
│   │           ├── secondaryport/          # Puertos de salida — application, NO domain (el dominio no hace I/O)
│   │           │   ├── AutenticacionOutputPort.java   # Contrato Keycloak
│   │           │   ├── ValidacionTokenOutputPort.java # Contrato validación JWT
│   │           │   ├── TokenInvalidadoOutputPort.java # Contrato Redis blacklist
│   │           │   └── UsuarioActualOutputPort.java   # Contrato Spring Security context
│   │           └── result/                 # AutenticacionResult, RefrescoTokenResult, ValidacionTokenResult
│   └── infrastructure/
│       └── src/main/java/com/arquisoft/seguridad/infrastructure/
│           ├── auth/command/
│           │   ├── primaryadapter/web/     # Un controller por acción
│           │   │   ├── IniciarSesionController.java, CerrarSesionController.java,
│           │   │   ├── RefrescarTokenController.java, ValidarTokenController.java
│           │   │   ├── dto/                # IniciarSesionRequestDTO (record desnudo), ...ResponseDTO
│           │   │   └── mapper/             # IniciarSesionRequestMapper (→ Command), IniciarSesionResponseMapper (Result → DTO)
│           │   └── secondaryadapter/
│           │       ├── keycloak/KeycloakAuthOutputAdapter.java
│           │       ├── redis/RedisTokenBlacklistOutputAdapter.java
│           │       ├── jwt/JwtTokenOutputAdapter.java
│           │       └── security/UsuarioActualOutputAdapter.java
│           ├── config/                   # Configuraciones de SEGURIDAD (no van en config/ raíz)
│           │   ├── security/SeguridadConfig.java    # JWT + OAuth2 Resource Server + método security
│           │   ├── cors/CorsConfig.java             # Orígenes, headers expuestos, credenciales
│           │   ├── ratelimit/LimiteSolicitudesConfig.java   # Bucket4j per-IP via Redis — 100/min global dev, 5/min login dev (60/3 prod)
│           │   └── http/RestTemplateConfig.java     # SimpleClientHttpRequestFactory (SB4 compat)
│           └── filter/
│               ├── LimitadorSolicitudesFilter.java  # OncePerRequestFilter: evalúa límite por IP
│               └── IdentidadTrazaFilter.java # Añade el sub del JWT a la traza abierta
│
├── usuarios/                              # CONTEXTO: alta de usuarios (extraído de seguridad)
│   ├── domain/                            # UsuarioDomain, secondaryport/UsuarioOutputPort
│   ├── application/                       # CrearUsuarioCommand, command/primaryport/interactor, usecase
│   └── infrastructure/                    # CrearUsuarioController, secondaryadapter/repository
│
├── fichas/                                # CONTEXTO: implementación real más completa
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── notificaciones/                        # CONTEXTO: envío de notificaciones (SMTP)
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── proyectos/                             # CONTEXTO: scaffolding (solo DataSourceConfig)
├── artefactos/                            # CONTEXTO: scaffolding
├── repositorio_artefactos/                # CONTEXTO: scaffolding
├── entregables/                           # CONTEXTO: scaffolding
└── evaluaciones/                          # CONTEXTO: scaffolding
```

### Estructura de una feature dentro de un contexto

La unidad de organización dentro de un contexto **no es la capa, es la feature**. Las tres capas
(`domain`, `application`, `infrastructure`) son módulos Gradle separados, y cada una abre el mismo
paquete `{feature}/` en su interior. El segmento de paquete de la feature va todo en minúscula y sin
separadores: `fichaperfil/`, no `fichaPerfil/` ni `ficha_perfil/`.

Dentro de `application` e `infrastructure`, la feature se parte además en `command/` y `query/`.
Ese corte es **CQRS y es total**: son dos árboles independientes que no comparten ninguna clase de
persistencia (ver *Aislamiento CQRS* más abajo).

```
{contexto}/domain/src/main/java/com/arquisoft/{contexto}/domain/
└── {feature}/
    ├── {Entidad}Domain.java              # Aggregate root. Sustantivo, sin subcarpeta propia
    ├── {Accion}{Entidad}Domain.java      # Objeto de acción (ver abajo). Mismo nivel que el agregado
    ├── model/                            # Value objects + el record de entrada de cada Rule
    ├── rules/
    │   ├── {Regla}Rule.java              # Interfaz, extiende shared.rules.DomainRule<T>
    │   └── impl/{Regla}RuleImpl.java     # POJO puro: sin Spring, sin Lombok, sin dependencias
    ├── event/{Concepto}Event.java        # Eventos de dominio (extienden DomainEvent)
    └── exception/{X}Exception.java       # Excepciones de dominio (extienden DomainException → 422)

{contexto}/application/src/main/java/com/arquisoft/{contexto}/application/
└── {feature}/
    ├── command/
    │   ├── primaryport/                          # Contrato primario del comando
    │   │   ├── interactor/{Accion}{Entidad}Interactor.java
    │   │   │   └── impl/{Accion}{Entidad}InteractorImpl.java   # Dueño de @Transactional
    │   │   ├── model/{Accion}{Entidad}Command.java             # Entrada (record + crear(...))
    │   │   └── mapper/{Accion}{Entidad}Mapper.java             # Command → dominio (estático)
    │   ├── usecase/{Accion}{Entidad}UseCase.java               # NO va bajo primaryport/
    │   │   └── impl/{Accion}{Entidad}UseCaseImpl.java          # @Component, orquesta, sin transacción
    │   ├── validator/{Accion}{Entidad}Validator.java           # Interfaz
    │   │   └── impl/{Accion}{Entidad}ValidatorImpl.java        # @Component, solo inyecta Rules
    │   ├── finder/{Concepto}Finder.java                        # Extiende shared.finder.Finder<T,R>
    │   │   └── impl/{Concepto}FinderImpl.java                  # @Component, delega en el OutputPort
    │   ├── secondaryport/{Entidad}OutputPort.java              # Puerto de salida (escritura)
    │   │   ├── entity/{Entidad}Entity.java                     # record plano: sin JPA, sin Lombok
    │   │   └── mapper/{Entidad}Mapper.java                     # Entity ↔ Domain (estático)
    │   └── result/{Concepto}Result.java                        # Solo si el comando no devuelve UUID/void
    └── query/
        ├── primaryport/usecase/{Consultar}{Entidad}UseCase.java   # Aquí el UseCase SÍ es el contrato
        │   └── impl/{Consultar}{Entidad}UseCaseImpl.java          # @Transactional(readOnly = true)
        ├── secondaryport/{Entidad}QueryOutputPort.java
        ├── criteria/{Entidad}Criteria.java                        # Entrada de la consulta
        └── readmodel/{Entidad}ReadModel.java                      # Salida de la consulta

{contexto}/infrastructure/src/main/java/com/arquisoft/{contexto}/infrastructure/
├── {feature}/
│   ├── command/
│   │   ├── primaryadapter/
│   │   │   ├── web/{Accion}{Entidad}Controller.java
│   │   │   │   ├── dto/{Accion}{Entidad}RequestDTO.java
│   │   │   │   ├── dto/{Accion}{Entidad}ResponseDTO.java   # Cuando el comando devuelve cuerpo
│   │   │   │   └── mapper/{Accion}{Entidad}RequestMapper.java
│   │   │   └── amqp/{Evento}Consumer.java                  # + {Evento}Payload.java (record)
│   │   └── secondaryadapter/
│   │       ├── entity/{Entidad}JpaEntity.java              # La JPA real: @Entity/@Table/Lombok
│   │       ├── mapper/{Entidad}JpaMapper.java              # Entity ↔ JpaEntity (estático)
│   │       └── repository/{Entidad}CommandOutputAdapter.java
│   │           └──          {Entidad}CommandRepository.java   # Spring Data: solo escritura
│   └── query/
│       ├── primaryadapter/web/{Consultar}{Entidad}Controller.java
│       │   ├── dto/{Entidad}ResponseDTO.java
│       │   └── mapper/{Consultar}{Entidad}RequestMapper.java   # DTO genérico → Criteria
│       │       └──   {Entidad}ResponseMapper.java              # ReadModel → ResponseDTO
│       └── secondaryadapter/repository/
│           ├── {Entidad}JpaQueryEntity.java     # @Subselect + @Immutable + @Synchronize
│           ├── {Entidad}JpaSpecification.java   # NodoFiltro → Specification
│           ├── {Entidad}SortMapper.java         # clave de ordenamiento → columna
│           ├── {Entidad}QueryOutputAdapter.java
│           ├── {Entidad}QueryRepository.java    # Extiende QueryRepository, NO JpaRepository
│           └── mapper/{Entidad}QueryMapper.java # JpaQueryEntity → ReadModel
├── config/                  # {Contexto}DataSourceConfig, colas AMQP
├── security/                # {Contexto}Authorities
├── web/                     # {Contexto}Routes
├── exception/               # Excepciones de infraestructura (extienden InfrastructureException → 503)
├── filter/                  # Filtros HTTP propios del contexto
└── db/migration/{contexto}/ # Flyway
```

Ninguna feature usa todos los paquetes. La regla es **no crear un paquete vacío ni un puerto sin
consumidor**: `representantecomite` y `revisionitem` solo tienen `finder/`, `secondaryport/` y su
adaptador, porque su único papel es responder consultas de existencia que necesita otra feature.

### Convenciones de Nomenclatura

| Capa | Paquete | Sufijo | Ejemplo |
|------|---------|--------|---------|
| **Domain - aggregate roots** | `domain/{feature}/` (directo, sin subcarpeta) | `Domain` | `FichaPerfilDomain.java` |
| **Domain - reglas de negocio** | `domain/{feature}/rules/` (+`impl/`) | `Rule`/`RuleImpl` | `FichaPerfilTituloUnicoRule.java` |
| **Application - puertos salida (escritura)** | `application/{feature}/command/secondaryport/` | `OutputPort` | `FichaPerfilOutputPort.java` |
| **Application - puertos salida (lectura)** | `application/{feature}/query/secondaryport/` | `QueryOutputPort` | `FichaPerfilQueryOutputPort.java` |
| **Application - Entity (record plano)** | `{feature}/command/secondaryport/entity/` | `Entity` | `FichaPerfilEntity.java` |
| **Infrastructure - JpaEntity** | `{feature}/command/secondaryadapter/entity/` | `JpaEntity` | `FichaPerfilJpaEntity.java` |
| **Domain - excepciones** | `exception/` | `Exception` | `FichaNoEncontradaException.java` |
| **Application - Commands** | `{feature}/command/primaryport/model/` | `Command` | `RegistrarFichaPerfilCommand.java` |
| **Application - ReadModels** | `{feature}/query/readmodel/` | `ReadModel` | `FichaPerfilReadModel.java` |
| **Application - interactor (comando)** | `{feature}/command/primaryport/interactor/` | `Interactor` | `RegistrarFichaPerfilInteractor.java` |
| **Application - use cases** | `{feature}/command/usecase/` o `{feature}/query/usecase/` (con su interactor en `{feature}/query/primaryport/interactor/`) | `UseCase`/`UseCaseImpl` | `ConsultarFichasPerfilUseCaseImpl.java` |
| **Infrastructure - entrada web** | `{feature}/command\|query/primaryadapter/web/` | `Controller` | `RegistrarFichaPerfilController.java` |
| **Infrastructure - entrada AMQP** | `{feature}/command/primaryadapter/amqp/` | `Consumer` | `UsuarioCreadoConsumer.java` |
| **Infrastructure - salida** | `{feature}/command\|query/secondaryadapter/...` | `OutputAdapter` | `FichaPerfilCommandOutputAdapter.java` |
| **Infrastructure - config** | `config/` | `Config` | `FichasConfig.java` |

**Regla bilingüe:** español para el concepto de negocio, inglés para el sufijo técnico
(`RegistrarFichaPerfilInteractor`). No queda ningún sufijo en español: los controladores REST se
llaman `Controller`, no `Controlador`.

#### Domain

| Elemento | Paquete | Sufijo | Ejemplo real |
|---|---|---|---|
| Aggregate root | `domain/{feature}/` (directo) | `Domain` | `FichaPerfilDomain` |
| Objeto de acción | `domain/{feature}/` (directo) | `Domain` | `RegistroFichaPerfilDomain` |
| Value object / entrada de Rule | `domain/{feature}/model/` | — | `ExistenciaAsesorFicha` |
| Regla de negocio | `domain/{feature}/rules/` (+ `impl/`) | `Rule` / `RuleImpl` | `AsesorFichaExisteRule` |
| Evento de dominio | `domain/{feature}/event/` | `Event` | `AsesorFichaCambiadoEvent` |
| Excepción de dominio | `domain/{feature}/exception/` | `Exception` | `FichaPerfilNoEncontradaException` |

El **aggregate root** es un sustantivo, nunca el infinitivo de la acción que lo crea: `FichaPerfilDomain`,
no `RegistrarFichaPerfilDomain`. Vive directo en `domain/{feature}/`, sin subcarpeta `aggregate/`.

El **objeto de acción** es un patrón aparte y conviene no confundirlo con el agregado. Cuando una acción
no mapea al agregado raíz sino a un conjunto de cosas que esa acción arrastra, se declara un objeto de
dominio propio nombrado por **nominalización del verbo** — `Registro`, `Modificacion`, `Cambio`,
`Agregacion`, `Remocion` — que agrupa el agregado con lo demás:

```
RegistroFichaPerfilDomain          ← agrupa FichaPerfilDomain + estado inicial + estudiantes
CambioAsesorFichaDomain            AgregacionItemFichaPerfilDomain
ModificacionFichaPerfilDomain      RemocionEstudianteFichaPerfilDomain
AgregacionEstadoEvaluacionFichaDomain
```

Es lo que construye el `{Accion}{Entidad}Mapper` de `command/primaryport/mapper/` y lo que recibe el
`UseCase`. Sigue siendo un sustantivo, así que no rompe la regla del agregado.

`domain/` **nunca** declara puertos ni entidades de persistencia y no hace I/O de ningún tipo: solo
depende de `shared:domain`, `shared:validation` y `shared:util`. Un puerto bajo `domain/` no
compilaría — invertiría la dirección `domain ← application ← infrastructure`.

#### Application

| Elemento | Paquete | Sufijo | Ejemplo real |
|---|---|---|---|
| Interactor (comando) | `{feature}/command/primaryport/interactor/` (+ `impl/`) | `Interactor` / `InteractorImpl` | `RegistrarFichaPerfilInteractorImpl` |
| Command (entrada) | `{feature}/command/primaryport/model/` | `Command` | `RegistrarFichaPerfilCommand` |
| Mapper Command → dominio | `{feature}/command/primaryport/mapper/` | `Mapper` | `RegistrarFichaPerfilMapper` |
| Use case (comando) | `{feature}/command/usecase/` (+ `impl/`) | `UseCase` / `UseCaseImpl` | `RegistrarFichaPerfilUseCaseImpl` |
| Validator | `{feature}/command/validator/` (+ `impl/`) | `Validator` / `ValidatorImpl` | `RegistrarFichaPerfilValidatorImpl` |
| Finder | `{feature}/command/finder/` (+ `impl/`) | `Finder` / `FinderImpl` | `AsesorFichaExisteFinderImpl` |
| Puerto de salida (escritura) | `{feature}/command/secondaryport/` | `OutputPort` | `FichaPerfilOutputPort` |
| Entity (record plano) | `{feature}/command/secondaryport/entity/` | `Entity` | `FichaPerfilEntity` |
| Mapper Entity ↔ Domain | `{feature}/command/secondaryport/mapper/` | `Mapper` | `FichaPerfilMapper` |
| Resultado de comando | `{feature}/command/result/` | `Result` | `AutenticacionResult` |
| Use case (consulta) | `{feature}/query/primaryport/usecase/` (+ `impl/`) | `UseCase` / `UseCaseImpl` | `ConsultarFichasPerfilUseCaseImpl` |
| Puerto de salida (lectura) | `{feature}/query/secondaryport/` | `QueryOutputPort` | `FichaPerfilQueryOutputPort` |
| Criteria (entrada) | `{feature}/query/criteria/` | `Criteria` | `FichaPerfilCriteria` |
| ReadModel (salida) | `{feature}/query/readmodel/` | `ReadModel` | `FichaPerfilReadModel` |

Tres asimetrías deliberadas entre los dos lados:

- **El `Interactor` va bajo `primaryport/`; el `UseCase` de comando no.** El interactor es el contrato
  primario — es lo que inyecta el adaptador — mientras que el use case es un colaborador interno.
- **En el lado consulta no hay interactor**, así que ahí el `UseCase` **sí** es el contrato primario y
  por eso vive en `query/primaryport/usecase/`.
- **`Validator` y `Finder` son interfaz + `impl/`**, igual que `Rule`, `Interactor` y `UseCase`. El
  `@Component` va siempre en el `Impl`; lo que se inyecta es la interfaz.

`{Entidad}Entity` y `{Entidad}JpaEntity` viven bajo `command/` **aunque el lado consulta también las
necesite**: una entidad no es de comando ni de consulta. Lo que el lado consulta no hace es importarlas
(ver *Aislamiento CQRS*).

#### Infrastructure

| Elemento | Paquete | Sufijo | Ejemplo real |
|---|---|---|---|
| Controller de comando | `{feature}/command/primaryadapter/web/` | `Controller` | `RegistrarFichaPerfilController` |
| Controller de consulta | `{feature}/query/primaryadapter/web/` | `Controller` | `ConsultarFichasPerfilController` |
| RequestDTO / ResponseDTO | `.../primaryadapter/web/dto/` | `RequestDTO` / `ResponseDTO` | `RegistrarFichaPerfilRequestDTO` |
| Mapper DTO → Command | `.../command/primaryadapter/web/mapper/` | `RequestMapper` | `RegistrarFichaPerfilRequestMapper` |
| Mapper DTO → Criteria | `.../query/primaryadapter/web/mapper/` | `RequestMapper` | `ConsultarFichasPerfilRequestMapper` |
| Mapper ReadModel → DTO | `.../query/primaryadapter/web/mapper/` | `ResponseMapper` | `FichaPerfilResponseMapper` |
| Consumer AMQP | `{feature}/command/primaryadapter/amqp/` | `Consumer` | `AsesorFichaCambiadoConsumer` |
| Payload AMQP | `{feature}/command/primaryadapter/amqp/` | `Payload` | `AsesorFichaCambiadoPayload` |
| JpaEntity (escritura) | `{feature}/command/secondaryadapter/entity/` | `JpaEntity` | `FichaPerfilJpaEntity` |
| Mapper Entity ↔ JpaEntity | `{feature}/command/secondaryadapter/mapper/` | `JpaMapper` | `FichaPerfilJpaMapper` |
| OutputAdapter (escritura) | `{feature}/command/secondaryadapter/repository/` | `CommandOutputAdapter` | `FichaPerfilCommandOutputAdapter` |
| Repositorio (escritura) | `{feature}/command/secondaryadapter/repository/` | `CommandRepository` | `FichaPerfilCommandRepository` |
| JpaEntity (lectura) | `{feature}/query/secondaryadapter/repository/` | `JpaQueryEntity` | `FichaPerfilJpaQueryEntity` |
| Specification | `{feature}/query/secondaryadapter/repository/` | `JpaSpecification` | `FichaPerfilJpaSpecification` |
| Traductor de ordenamiento | `{feature}/query/secondaryadapter/repository/` | `SortMapper` | `FichaPerfilSortMapper` |
| OutputAdapter (lectura) | `{feature}/query/secondaryadapter/repository/` | `QueryOutputAdapter` | `FichaPerfilQueryOutputAdapter` |
| Repositorio (lectura) | `{feature}/query/secondaryadapter/repository/` | `QueryRepository` | `FichaPerfilQueryRepository` |
| Mapper JpaQueryEntity → ReadModel | `{feature}/query/secondaryadapter/repository/mapper/` | `QueryMapper` | `FichaPerfilQueryMapper` |
| Adaptador no-JPA | `{feature}/command/secondaryadapter/{tecnologia}/` | `OutputAdapter` | `KeycloakAuthOutputAdapter` |
| Configuración | `config/` | `Config` | `FichasDataSourceConfig` |

Un adaptador de salida que no habla JPA no usa `repository/`, usa un subpaquete con el nombre de su
tecnología: `secondaryadapter/keycloak/`, `secondaryadapter/redis/`, `secondaryadapter/jwt/`,
`secondaryadapter/security/`.

`Controller` queda reservado para entradas HTTP. Un consumidor AMQP se llama `Consumer`, que es
además el nombre que corresponde a la clase base que extiende (`AbstractEventConsumer`).

#### Mappers: uno por frontera, todos estáticos

Todos los mappers son clases `final`, con constructor privado y métodos `static`. **Ninguno es un
bean**, por eso jamás aparecen como dependencia inyectada. Hay exactamente uno por frontera y el
nombre dice cuál cruza:

| Mapper | Convierte | Capa | Lo invoca |
|---|---|---|---|
| `{Accion}{Entidad}RequestMapper` | `RequestDTO` → `Command` | infrastructure | el Controller |
| `{Accion}{Entidad}Mapper` | `Command` → dominio | application (primaryport) | el Interactor |
| `{Entidad}Mapper` | `Entity` ↔ `Domain` | application (secondaryport) | `toEntity` el UseCase, `toDomain` el Finder |
| `{Entidad}JpaMapper` | `Entity` ↔ `JpaEntity` | infrastructure | el OutputAdapter |
| `{Entidad}QueryMapper` | `JpaQueryEntity` → `ReadModel` | infrastructure | el QueryOutputAdapter |
| `{Consultar}{Entidad}RequestMapper` | `QueryCriteriaRequestDTO` → `Criteria` | infrastructure | el Controller de consulta |
| `{Entidad}ResponseMapper` | `ReadModel` → `ResponseDTO` | infrastructure | el Controller de consulta |

Los mappers de comando se nombran **por la acción** (`RegistrarFichaPerfilRequestMapper`); los de
persistencia, **por la entidad** (`FichaPerfilJpaMapper`), porque son los mismos para todas las
acciones de la feature.

#### Aislamiento CQRS

El corte `command/` ↔ `query/` es total en infraestructura: **`query/secondaryadapter` no importa
nada de `command/secondaryadapter`, ni siquiera el `{Entidad}JpaEntity`**. Cada feature con lectura
real declara su propio `{Entidad}JpaQueryEntity` con `@Subselect` + `@Immutable` + `@Synchronize`.
Reutilizar la `JpaEntity` de escritura ataría el camino de lectura a su mapeo Hibernate —sus
`@ManyToOne`, su configuración de cascada— que es justo lo que la separación existe para evitar.
Como el subselect ya resuelve los joins, la entidad de lectura es **plana**: `asesorNombre`, no
`asesorFicha.nombre`.

Por la misma razón el repositorio está partido en dos: `{Entidad}CommandRepository` extiende
`JpaRepository`, y `{Entidad}QueryRepository` extiende `QueryRepository` /
`SpecificationQueryRepository` de `shared:jpa` — interfaces `@NoRepositoryBean` que exponen solo
`findById`/`existsById`/`findAll`/`count`. **El repositorio de lectura no hereda `save` ni `delete`:
es el compilador, no la convención, el que mantiene honesto el CQRS.**

#### Cuándo existe un paquete `query/`

Solo cuando la feature tiene una lectura real que se alcanza por un `primaryport` — un `UseCase` con
su `Controller`, algo que un cliente efectivamente llama. Una comprobación de existencia o de estado
que necesita un `Validator`/`Rule` del lado comando (*¿existe este estudiante?*, *¿es este
representante el dueño de la evaluación?*) es un asunto **de comando** aunque solo lea: va en el
`{Entidad}OutputPort` de `command/`, consumida por un `Finder`. Por eso `estudiante`,
`representantecomite`, `evaluacionfichaperfil` y `estadofichaperfil` no tienen paquete `query/`.

La única excepción es la **proyección anidada**: `asesorficha` tiene `query/readmodel/AsesorFichaReadModel`
y un `AsesorFichaResponseDTO` sin `UseCase` ni `Controller` propios, porque ambos son componentes
anidados dentro de `FichaPerfilReadModel` y `FichaPerfilResponseDTO`. El ReadModel anidado vive en la
feature que describe, no en la que lo compone.

---

## Desviaciones conocidas respecto a la convención

Estos casos existen hoy en el código y **no** son ejemplos a seguir. Se listan para que nadie los
copie creyendo que son la regla, y para que se resuelvan cuando se toque esa parte del código.

| Qué | Dónde | Convención | Estado |
|---|---|---|---|
| `CrearUsuarioRequestDTO` con anotaciones Jakarta + `toCommand()` propio | `usuarios/infrastructure/usuario/command/primaryadapter/web/dto/` | Único DTO que queda en la convención "contexto pequeño", ya retirada: debería ser un `record` desnudo + `CrearUsuarioRequestMapper` que llame a `CrearUsuarioCommand.crear(...)`. Además anida un enum `RolUsuarioDTO` que duplica `UsuarioRole`, y construye el `Command` con `new` en vez de `crear(...)`, así que nada valida el formato | Pendiente de migrar |
| `*ResponseDTO` como clase Lombok `@Data`/`@Builder` | `seguridad/infrastructure/auth/command/primaryadapter/web/dto/` (los cuatro) | Los DTO de respuesta son `record`, como en `fichas` | Pendiente de migrar |
| `EstadoEvaluacionCommandRepository` | `fichas/infrastructure/estadoevaluacion/command/secondaryadapter/repository/` | Código muerto: no hay `OutputPort` ni `OutputAdapter` que lo consuma | Pendiente de eliminar |
| `UsuarioCommandOutputAdapter` no persiste | `usuarios/infrastructure/usuario/command/secondaryadapter/repository/` | **Deliberado.** `usuarios` es un contexto de ejemplo: el adaptador solo deja el log y no escribe, para no generar registros si se invoca el flujo. Falta a propósito el `UsuarioJpaEntity` + `UsuarioJpaMapper` + `UsuarioCommandRepository`. `UsuarioEmailUnicoRule` no se dispara nunca mientras siga así | No es un pendiente — se ajustará cuando el contexto se desarrolle |
| `fichas/application/usuario` | `command/usecase/RegistrarUsuarioUseCase` | Stub con `// TODO: persistir en tabla espejo`. Por eso no tiene `Interactor`, ni `@Transactional`, ni `Validator`, y el `UsuarioCreadoConsumer` inyecta el `UseCase` directo — cuando persista de verdad debe pasar por un `Interactor` | Pendiente de implementar |

### Decisión abierta: dónde vive un enum de catálogo

Hoy conviven dos ubicaciones y **todavía no hay regla acordada**:

| Enum | Contexto | Ubicación | ¿Tabla propia? |
|---|---|---|---|
| `EstadoFicha`, `TipoItem`, `EstadoEvaluacion` | fichas | `domain/{catalogo}/` — feature propia, con su `exception/`, su `Entity`/`JpaEntity` y, en el caso de `EstadoFicha`, un `query/` completo | Sí (`estado_ficha`, `tipo_item`, `estado_evaluacion`) |
| `EstadoNotificacion`, `TipoNotificacion` | notificaciones | `domain/notificacion/model/` — value object de la feature | No (columnas `VARCHAR` de `notificacion`) |
| `UsuarioRole` | usuarios | `domain/usuario/model/` — value object de la feature | No (columna `usuario.rol`; la verdad vive en Keycloak) |

La correlación con "¿el catálogo tiene tabla propia en Postgres?" es exacta en los 6 casos, pero
**está pendiente de discutir con el asesor del proyecto** si se adopta ese criterio, si se unifica
todo en `domain/{catalogo}/`, o si se unifica todo en `domain/{feature}/model/`. Hasta entonces,
un enum de catálogo nuevo debe seguir la ubicación que ya use su contexto.

Lo que sí es regla, y no está en discusión: `valueOf` **nunca** se llama fuera del enum. Cada enum de
catálogo expone `desde(String)` —que devuelve la constante o lanza su propia
`{Enum}NoEncontradoException` (`DomainException` → 422)— y `esValido(String)` cuando el valor también
llega por el `crear(...)` de un agregado, para que el error se acumule en el `ValidationResult` en vez
de abortar en el primero. Ambos delegan en `UtilEnum`. Y todo enum de catálogo expone `getId()`: los
mappers persisten `getId()`, nunca un `.name()` pelado.

---

## Módulo Seguridad

El contexto **seguridad** maneja toda la autenticación y autorización de la aplicación.

### Responsabilidades

- Autenticación OAuth2/JWT via Keycloak
- Gestión de roles (`ADMINISTRADOR`, `COORDINADOR`, `ASESOR_FICHA`, `ASESOR`, `JURADO`, `ESTUDIANTE`, `BIBLIOTECARIO`, `REPRESENTANTE_COMITE_CURRICULUM`)
- Rate limiting con Bucket4j
- Auditoría de requests
- CORS configuration
- Global exception handling

### Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/login` | Login con Keycloak |
| POST | `/api/auth/refresh` | Renovar token |
| POST | `/api/auth/logout` | Cerrar sesión |
| POST | `/api/auth/validate` | Validar token |

### Dependencias

```gradle
// seguridad/infrastructure/build.gradle
dependencies {
    implementation project(':seguridad:domain')
    implementation project(':seguridad:application')
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
    implementation 'org.springframework.security:spring-security-oauth2-jose'
    implementation 'org.keycloak:keycloak-admin-client:25.0.3'
    implementation "com.bucket4j:bucket4j_jdk17-core"
    implementation "com.bucket4j:bucket4j_jdk17-redis-common:${bucket4jVersion}"
    implementation "com.bucket4j:bucket4j_jdk17-lettuce:${bucket4jVersion}"
}
```

---

## Módulo Shared

El módulo `shared` contiene **13 sub-módulos** reutilizables por cualquier contexto:

| Sub-módulo | Contenido | Uso |
|-----------|-----------|-----|
| `util` | UtilTexto, UtilUUID, UtilColeccion, UtilFecha, UtilNumero, UtilObjeto | Helpers estáticos sin estado |
| `exception` | BaseException/BaseError + 5 excepciones base | Jerarquía de excepciones del proyecto; sin dependencias propias (hoja del grafo) |
| `validation` | DomainValidator, ValidationResult, DomainValidationException, ApplicationValidationException | Notification Pattern: acumula errores en vez de lanzar en el primero |
| `domain` | DomainEvent, DomainRule | Lo unico que la capa de dominio puede ver |
| `application` | UseCase, VoidUseCase, Interactor, VoidInteractor, Finder, EventPublisher | Contratos de la capa de aplicacion. Separado de `domain` para que el compilador impida que un agregado implemente un `UseCase` o inyecte un puerto |
| `logger` | AppLogger (interface) | Logging desacoplado de SLF4J |
| `redis` | RedisClient (interface) | Operaciones de cache |
| `amqp` | EventPublisher (interface) | Publicar eventos a RabbitMQ |
| `web` | `filter/TrazabilidadFilter`, `handler/GlobalAppExceptionHandler`, DTOs comunes | Adaptadores HTTP transversales y manejo global de errores |
| `tracing` | GestorTraza, AlcanceTraza, TrazaKeys, TrazaHeaders | Contexto de traza sobre MDC: correlación, transacción, propagación HTTP/AMQP |
| `minio` | Cliente MinIO | Almacenamiento de archivos |
| `jpa` | `QueryRepository`/`SpecificationQueryRepository`, `CampoSpec`, `PageableMapper`/`PaginationMapper` | Todo lo que necesita Spring Data JPA para el lado de consulta — nunca se importa desde `domain` ni `application` |
| `query` | `QueryCriteria`/`NodoFiltro`/`FiltroOperador`/`SortOrder`, `PaginatedResult`, DTOs de filtro | Vocabulario de consulta **sin ninguna dependencia de Spring** — usable en cualquier capa |
| `message` | `CatalogoMensajes` (puerto), `Mensajes` (fachada estática), `ClavesCatalogo`, `*Codes`/`*Fields`/`*Limits` | Puerto y registro del catálogo; el texto vive en `catalogo/*.properties` y lo sirve `shared:redis` |
| `notification` | EnvioNotificacionOutputPort | Puerto de envío de notificaciones (SMTP), usado por `notificaciones` |

`shared:jpa` fue `shared:postgres` hasta hace poco — se renombró porque no tiene nada
específico de PostgreSQL (ni driver, ni SQL nativo, ni dialecto), y `shared:query` se extrajo
de él (más `shared:domain` y `shared:web`) para que un módulo que solo necesita declarar un
criterio de consulta no arrastre Spring Data JPA como dependencia transitiva.

### Ejemplo de Uso

```gradle
// fichas/domain/build.gradle
dependencies {
    implementation project(':shared:domain')  // Para usar DomainEvent
}

// fichas/infrastructure/build.gradle
dependencies {
    implementation project(':shared:amqp')    // Para publicar eventos
    implementation project(':shared:jpa')     // Para repositorios de consulta JPA
}

// fichas/application/build.gradle
dependencies {
    implementation project(':shared:query')   // Para declarar un Criteria — sin JPA
}
```

---

## Eventos de Dominio

### ¿Cómo se emite un evento?

No hay una clase base que acumule eventos en memoria, y el agregado no participa en la publicación:
no la hereda de nadie, no guarda eventos y no expone ningún método para emitirlos ni drenarlos. El
use case, tras persistir, construye el evento y lo publica directo:
`eventPublisher.publish(new XxxEvent(...))`. Es la única forma que existe en el proyecto.

Hubo una alternativa —el agregado acumulaba y el use case drenaba— con un solo consumidor real, y se
retiró: mantener dos formas válidas para la misma cosa no aportaba nada. Se resolvió por eliminación
y no por migración, quitando la de un solo uso en vez de la que ya usaba el contexto de referencia.
Además el grafo de módulos hoy lo impide: `{contexto}/domain` solo declara `shared:domain`, y el
puerto `EventPublisher` vive en `shared:application`, así que el dominio ni siquiera lo ve.

```java
// UseCaseImpl — construye el evento con lo que ya tiene y publica, sin pasar por el aggregate
eventPublisher.publish(new AsesorFichaCambiadoEvent(fichaPerfil, ficha.getTituloProyecto(),
        asesorFicha.getId(), asesorFicha.getNombre(), asesorFicha.getEmail()));
```

### ¿Qué contextos emiten eventos?

| Contexto | ¿Emite eventos? | Cómo |
|---|---|---|
| `fichas` | ✅ Sí | El use case publica directo tras persistir (`CambiarAsesorFichaUseCaseImpl` → `AsesorFichaCambiadoEvent`) |
| `usuarios` | ✅ Sí | Mismo patrón (`CrearUsuarioUseCaseImpl` → `UsuarioCreadoEvent`) |
| `seguridad` | ❌ No | Contexto transversal, delega a Keycloak, sin estado propio |
| `notificaciones` | ❌ No | Reacciona a eventos de otros contextos, no los emite |
| `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables`, `evaluaciones` | — | Scaffolding: sin código de dominio todavía, se espera que sigan el mismo patrón al implementarse |

### Estructura de carpetas en domain (con eventos)

```
{contexto}/domain/src/main/java/com/arquisoft/{contexto}/domain/
└── {feature}/
    ├── {Entidad}Domain.java        ← vive directo aquí, sin subcarpeta
    ├── event/                       ← eventos de dominio de ESTA feature
    │   └── {Entidad}CreadoEvent.java
    ├── secondaryport/               ← OutputPort interfaces
    └── exception/
```

### Regla importante

- `crear(...)` → para **crear** una entidad nueva: valida invariantes, genera UUID — **no** `build(...)`.
- `reconstruir(...)` → para **reconstruir** desde BD: dato ya confiable, sin UUID nuevo, sin
  re-validar — **no** `rebuild(...)`.
- El dominio **nunca** inyecta `EventPublisher` — el aggregate no sabe que se va a publicar nada;
  quien construye y publica el evento es el use case, que ya tiene todo lo que necesita porque
  acaba de crear o consultar el aggregate.

## Virtual Threads (ADR-008)

### Configuración

> Habilitados explícitamente en `application.yml`:
> ```yaml
> spring:
>   threads:
>     virtual:
>       enabled: true
> ```
> Java 21 + Spring Boot 4.x: la propiedad hace que Tomcat, `@Async` y los listeners de
> RabbitMQ usen virtual threads automáticamente — no requiere configuración adicional en
> código más allá de esa línea.

Esta configuración hace que Spring Boot reemplace automáticamente los executors de OS threads en **Tomcat**, **`@Async`** y **RabbitMQ listeners**.

### ¿Qué cubre automáticamente?

| Componente del proyecto | Efecto |
|---|---|
| Todos los **Controllers** REST (requests HTTP) | Cada request corre en un virtual thread |
| **`KeycloakAuthOutputAdapter`** (HTTP a Keycloak) | Bloqueo I/O sin consumir OS thread |
| **`JwtTokenOutputAdapter`** (decodificación JWT) | Igual |
| **`FichaPerfilCommandOutputAdapter`** y todos los OutputAdapters JPA/JDBC | Queries a BD sin bloquear OS thread |
| **`@RabbitListener`** en los `Consumer` AMQP | Mensajes procesados en virtual threads |
| **`TrazabilidadFilter`**, **`LimitadorSolicitudesFilter`** | Mismo virtual thread del request |

No hay que modificar ningún método ni clase — el beneficio es completamente transparente para el código de negocio.

### La única excepción: `@Async` con `TaskExecutor` manual

Si en algún use case se declara un `TaskExecutor` propio en un `@Configuration`, ese bean **no hereda** la configuración global y debe ajustarse explícitamente:

```java
// ❌ NO hereda virtual threads — usa OS threads del pool
@Bean
public TaskExecutor miExecutor() {
    return new ThreadPoolTaskExecutor();
}

// ✅ SÍ usa virtual threads
@Bean
public TaskExecutor miExecutor() {
    return new SimpleAsyncTaskExecutor(Thread.ofVirtual().factory());
}
```

En este proyecto no hay ningún `TaskExecutor` declarado manualmente, por lo que toda la concurrencia queda cubierta con la propiedad ya configurada.

> **`@Async` sí necesita un `TaskDecorator` para el MDC.** Virtual threads resuelve qué *tipo*
> de hilo ejecuta la tarea async, pero no resuelve que el `MDC` (`ThreadLocal`) del hilo del
> request no viaja solo al hilo nuevo. El listener interno de `spring-modulith-events-amqp` que
> publica eventos a RabbitMQ es `@Async`, y sin ayuda pierde `correlacionId`/`transaccionId` al
> cruzar ese hilo. `MdcTaskDecorator` (`shared:tracing/infrastructure/traza/config/`, `@Bean
> TaskDecorator` en `TrazabilidadConfig`) es el único ajuste manual que este proyecto sí declara
> para `@Async` — Spring Boot lo recoge automáticamente para el `applicationTaskExecutor` sin
> tocar la configuración de virtual threads. Ver [ARQUITECTURA_ASINCRONICO_ARQUISOFT.md → "El
> salto de hilo que casi rompe la trazabilidad"](ARQUITECTURA_ASINCRONICO_ARQUISOFT.md#el-salto-de-hilo-que-casi-rompe-la-trazabilidad).

### Separación entre `config/` raíz y `seguridad/infrastructure/config/`

| Carpeta | Qué va aquí |
|---|---|
| `config/` *(raíz del proyecto)* | Configuraciones **transversales** del build/tooling: `checkstyle.xml`, reglas de análisis estático. |
| `src/main/java/com/arquisoft/` | Punto de entrada (`ArquisoftApplication`) y configuraciones **globales de la API ensamblada**: `config/OpenApiConfig` con `@OpenAPIDefinition` y `@SecurityScheme`. Reside aquí porque es el único módulo con visibilidad de todos los contextos. |
| `seguridad/infrastructure/config/` | Configuraciones **de runtime de Spring Security**: `SeguridadConfig`, `CorsConfig`, `LimiteSolicitudesConfig`, `RestTemplateConfig`. Solo pertenecen al contexto de seguridad. |

---

## Ejemplos Prácticos

### Ejemplo: Registrar una Ficha (flujo de comando real, `fichas`)

#### Flujo Completo

```
HTTP POST /fichas-perfil
    ↓
RegistrarFichaPerfilController.registrar(RequestDTO)         [primaryadapter/web — REST Controller]
    ↓
RegistrarFichaPerfilRequestMapper.toCommand(dto)               [primaryadapter/web/mapper — DTO → Command]
    ↓
RegistrarFichaPerfilCommand.crear(...)                        [primaryport/model — valida integridad de datos]
    ↓
RegistrarFichaPerfilInteractor.ejecutar(command)               [primaryport/interactor — @Transactional, entry point]
    ↓
FichaPerfilDomain.crear(...)                                  [aggregate root — valida invariantes de dominio]
    ↓
RegistrarFichaPerfilUseCase.ejecutar(ficha)                    [usecase — valida existencia/unicidad, mapea Domain → Entity]
    ↓
FichaPerfilOutputPort.registrarFicha(entity)                   [secondaryport — interfaz, habla Entity]
    ↓
FichaPerfilCommandOutputAdapter.registrarFicha(entity)          [secondaryadapter/repository — traduce Entity → JpaEntity]
    ↓
FichaPerfilCommandRepository.save(FichaPerfilJpaMapper.toJpaEntity(entity))   [Spring Data JPA — INSERT]
    ↓
HTTP 201 Created
```

El `Interactor` es el único que orquesta varios casos de uso y posee la transacción (registra la ficha, le asigna su estado inicial y vincula a los estudiantes en una sola unidad de trabajo); ninguno de esos pasos intermedios se muestra aquí por brevedad — ver `RegistrarFichaPerfilInteractorImpl` en el código fuente.

#### Código

Cada paso del flujo tiene su archivo real bajo `fichas/{capa}/.../fichaperfil/` — ábrelos con
`Read` en vez de buscar el código aquí:

| # | Pieza | Archivo real |
|---|---|---|
| 1 | DTO + `RequestMapper` (DTO sin anotaciones, el mapper delega el formato a `Command.crear(...)`) | `fichas/infrastructure/.../fichaperfil/command/primaryadapter/web/dto/RegistrarFichaPerfilRequestDTO.java` + `.../mapper/RegistrarFichaPerfilRequestMapper.java` |
| 2 | Command (`crear(...)` valida integridad con `ValidationResult`) | `fichas/application/.../fichaperfil/command/primaryport/model/RegistrarFichaPerfilCommand.java` |
| 3 | Aggregate root | `fichas/domain/.../fichaperfil/FichaPerfilDomain.java` |
| 4 | Puerto de salida (habla `Entity`, nunca `Domain`) + su `Entity` (record plano) | `fichas/application/.../fichaperfil/command/secondaryport/FichaPerfilOutputPort.java` + `secondaryport/entity/FichaPerfilEntity.java` |
| 5 | UseCase (consulta Finders, valida, mapea `Domain → Entity`, persiste) | `fichas/application/.../fichaperfil/command/usecase/impl/RegistrarFichaPerfilUseCaseImpl.java` |
| 6 | Interactor (dueño de la transacción, orquesta varios UseCases) | `fichas/application/.../fichaperfil/command/primaryport/interactor/impl/RegistrarFichaPerfilInteractorImpl.java` |
| 7 | Controller (inyecta el Interactor, nunca el UseCase) | `fichas/infrastructure/.../fichaperfil/command/primaryadapter/web/RegistrarFichaPerfilController.java` |
| 8 | OutputAdapter (traduce `Entity ↔ JpaEntity`) + `JpaEntity` real | `fichas/infrastructure/.../fichaperfil/command/secondaryadapter/repository/FichaPerfilCommandOutputAdapter.java` + `secondaryadapter/entity/FichaPerfilJpaEntity.java` |

Todo el I/O de un comando vive en el use case; el validator y las rules son funciones puras de lo
que ya se consultó (ver skill `arquisoft-estandares`).

### Test Unitario

Ejemplo real con Mockito + AAA:
`fichas/application/src/test/java/com/arquisoft/fichas/application/fichaperfil/command/usecase/impl/RegistrarFichaPerfilUseCaseTest.java`.

---

## Configuración y Build

### Versiones de Dependencias (gradle.properties)

```properties
javaVersion=21
springBootVersion=4.0.5
jUnitVersion=6.0.3
lombokVersion=1.18.36
h2Version=2.3.232
flywaydbVersion=11.20.3
keycloakVersion=25.0.3
# postgresVersion, rabbitmqVersion, redisVersion están comentadas
# Spring Boot BOM gestiona estas versiones automáticamente
```

### Dependencias entre Capas (build.gradle)

```gradle
// {contexto}/domain/build.gradle
dependencies {
    implementation project(':shared:domain')    // Solo si necesita DomainEvent
}

// {contexto}/application/build.gradle
dependencies {
    implementation project(':{contexto}:domain')
}

// {contexto}/infrastructure/build.gradle
dependencies {
    implementation project(':{contexto}:domain')
    implementation project(':{contexto}:application')
    implementation "org.springframework.boot:spring-boot-starter-jdbc:${springBootVersion}"
    implementation "org.flywaydb:flyway-core:${flywaydbVersion}"
    runtimeOnly 'org.postgresql:postgresql'
    testImplementation "com.h2database:h2:${h2Version}"
}
```

### Construir el Proyecto

```bash
# Build completo
./gradlew build

# Build sin tests
./gradlew build -x test

# Ejecutar tests
./gradlew test

# Run la aplicación
./gradlew bootRun --args='--spring.profiles.active=dev'
```

---

## Flujos de Datos

### Flujo de Lectura (POST /fichas-perfil/consultar)

```
ConsultarFichasPerfilController.consultar(criteria)   [primaryadapter/web — REST Controller]
    ↓
ConsultarFichasPerfilInteractor.ejecutar(FichaPerfilCriteria)  [query/primaryport/interactor — contrato primario, @Transactional(readOnly)]
    ↓
ConsultarFichasPerfilUseCase.ejecutar(FichaPerfilCriteria)  [query/usecase — colaborador interno]
    ↓
FichaPerfilQueryOutputPort.consultarTodas(criteria)   [query/secondaryport — interfaz]
    ↓
FichaPerfilQueryOutputAdapter.consultarTodas(criteria)  [secondaryadapter/repository — pura delegación]
    ↓
PageableMapper.toPageable(criteria, sortMapper) + FichaPerfilJpaSpecification.desdeCriteria(criteria)
    ↓
FichaPerfilQueryRepository.findAll(spec, pageable)     [contra FichaPerfilJpaQueryEntity — entidad de lectura
    ↓                                                    dedicada, @Subselect, plana, no la del lado command]
FichaPerfilQueryMapper.toReadModel(...)  →  FichaPerfilReadModel
    ↓
PaginationMapper.toResult(...)  →  PaginatedResult<FichaPerfilReadModel>
    ↓
HTTP Response 200 OK
```

Ver `docs/PATRON_QUERY_OBJECT_FILTROS_DINAMICOS.md` para el detalle completo del patrón de
filtrado dinámico (Query Object + Spring Data Specification).

### Flujo de Escritura (POST /fichas-perfil)

```
RegistrarFichaPerfilController.registrar(RequestDTO)   [primaryadapter/web — REST Controller]
    ↓
RegistrarFichaPerfilRequestMapper.toCommand(dto)        [primaryadapter/web/mapper]
    ↓
RegistrarFichaPerfilCommand.crear(...)                 [command/primaryport/model — integridad de datos]
    ↓
RegistrarFichaPerfilInteractor.ejecutar(command)        [command/primaryport/interactor — @Transactional]
    ↓
FichaPerfilDomain.crear(...)                           [aggregate root — invariantes de dominio]
    ↓ [existencia/unicidad validadas en el UseCase antes de persistir]
FichaPerfilOutputPort.registrarFicha(entity)            [secondaryport — interfaz, habla Entity]
    ↓
FichaPerfilCommandOutputAdapter.registrarFicha(entity)  [secondaryadapter/repository — traduce Entity → JpaEntity]
    ↓
INSERT en BD (schema fichas_perfil)
    ↓
HTTP Response 201 CREATED
```

---

## Perfiles de Ejecución

### Perfil `dev` (application-dev.yml)

- Logging en nivel DEBUG para `com.arquisoft`
- Servicios en localhost
- Rate limiting deshabilitado
- CORS permisivo (localhost:3000, 4200, 5173)
- Keycloak en localhost:8081

### Perfil `prod` (application-prod.yml)

- Todas las credenciales por variables de entorno
- Rate limiting activo (60 req/min global, 3 login/min) — buckets en Redis, no en memoria
- CORS configurado para dominio de producción
- Logging a archivo (`/var/log/arquisoft/`)
- Pool de conexiones mayores

---

## Principios de Diseño

### 1. Separación de Responsabilidades

- **Domain**: Lógica pura de negocio
- **Application**: Orquestación y DTOs
- **Infrastructure**: Detalles técnicos

### 2. Inversión de Dependencias

```
sin hexagonal:  Controller → Repository → Database
con hexagonal:  Controller → Port (interface) ← Repository
```

### 3. Independencia de Frameworks

- El dominio NO importa Spring
- Los puertos son interfaces puras
- Facilita cambio de BD, framework, etc.

### 4. Comunicación entre Contextos

- Los contextos **nunca** dependen directamente entre sí
- Comunicación exclusivamente via **eventos RabbitMQ**
- Cada contexto tiene su propia **base de datos** (no un schema): `init-db.sql` hace un `CREATE DATABASE` por contexto, y cada uno lleva su propio `flyway_schema_history` y sus migraciones en `db/migration/{contexto}/`. Una FK entre contextos es imposible, no solo desaconsejada

---

## Checklist: Agregar un Nuevo Contexto

1. Crear estructura `{contexto}/domain/`, `application/`, `infrastructure/`
2. Agregar `build.gradle` en cada sub-módulo
3. Registrar en `settings.gradle`
4. Agregar schema en `init-db.sql`
5. Definir entidades en domain
6. Definir puertos (in/out)
7. Implementar use cases en application
8. Crear adaptadores en infrastructure
9. Agregar migraciones Flyway
10. Crear tests unitarios

---

## Resumen Arquitectónico

| Aspecto | Descripción |
|--------|-------------|
| **Patrón** | Hexagonal (Puertos y Adaptadores) |
| **Contextos** | 9 independientes (4 con implementación real, 5 scaffolding) |
| **Módulo compartido** | shared (13 sub-módulos) |
| **Capas** | Domain → Application → Infrastructure |
| **Framework** | Spring Boot 4.0.5 |
| **BD** | PostgreSQL 18 (1 schema por contexto) |
| **Migraciones** | Flyway 12.4.0 |
| **Build** | Gradle 9.0.0 con Wrapper |
| **Java** | 21 (Virtual Threads habilitados) |
| **Testing** | JUnit 6.0.3 + Mockito + AssertJ |
| **Auth** | Keycloak 26.6 (OAuth2/OIDC Resource Server) |
| **Rate Limiting** | Bucket4j 8.18.0 (`com.bucket4j:bucket4j_jdk17-core`) |
| **Concurrencia** | Virtual Threads (`spring.threads.virtual.enabled=true`) |

---

## Referencias

- **Arquitectura Hexagonal**: https://alistair.cockburn.us/hexagonal-architecture/
- **Clean Architecture**: Robert C. Martin (2017)
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot

---

**Versión**: 1.0.0
