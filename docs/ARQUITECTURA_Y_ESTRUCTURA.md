> [!WARNING]
> **SOLO LECTURA — NO USAR COMO CONTEXTO DE AGENTES O IA**
>
> Este archivo es documentación de referencia para desarrolladores humanos.
> **No debe ser leído ni indexado por agentes, asistentes de IA ni herramientas de generación de código.**
> El contexto autoritativo del proyecto para agentes reside exclusivamente en `AGENTS.md` (raíz del repositorio)
> y en los skills de `.opencode/skills/`. Usar este archivo como contexto puede producir código incorrecto,
> versiones desactualizadas o convenciones que no reflejan el estado real del proyecto.

# Arquitectura Hexagonal Modular - Documentación Completa

## Índice

1. [Visión General](#visión-general)
2. [Arquitectura Hexagonal (Puertos y Adaptadores)](#arquitectura-hexagonal-puertos-y-adaptadores)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Módulo Seguridad](#módulo-seguridad)
5. [Módulo Shared](#módulo-shared)
6. [AggregateRoot y Eventos de Dominio](#aggregateroot-y-eventos-de-dominio)
7. [Virtual Threads (ADR-008)](#virtual-threads-adr-008)
8. [Ejemplos Prácticos](#ejemplos-prácticos)
9. [Configuración y Build](#configuración-y-build)
10. [Flujos de Datos](#flujos-de-datos)
11. [Perfiles de Ejecución](#perfiles-de-ejecución)

---

## Visión General

Este proyecto implementa una **Arquitectura Hexagonal Modular** usando **Spring Boot 4.0.5** y **Gradle 9.0.0** como herramienta de construcción. La arquitectura se basa en el patrón de **Puertos y Adaptadores** con **7 contextos independientes**, Java 21 y Virtual Threads habilitados.

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

- Implementa los puertos de salida mediante `OutputAdapter` (e.g., `FichaPerfilOutputAdapter`)
- Contiene adaptadores de entrada (`InputAdapter`): REST controllers y AMQP consumers (e.g., `FichaPerfilInputAdapter`, `UsuarioCreadoInputAdapter`)
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
├── init-db.sql                           # Creación de 7 schemas
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
├── shared/                               # Módulo compartido
│   ├── domain/                           # DomainEvent, AggregateRoot
│   ├── exceptions/                       # DomainException
│   ├── amqp/                             # EventPublisher
│   ├── postgres/                         # BaseRepository
│   ├── redis/                            # RedisClient
│   ├── web/                              # HttpClient
│   └── validation/                       # @ValidEmail, EmailValidator
│
├── seguridad/                            # CONTEXTO 1
│   ├── domain/
│   │   └── src/main/java/com/arquisoft/seguridad/domain/
│   │       ├── model/
│   │       │   ├── UsuarioAggregate.java  # Aggregate root con factory build()/rebuild()
│   │       │   └── UserRole.java          # Enum: 8 roles
│   │       ├── port/out/
│   │       │   └── UsuarioOutputPort.java # OutputPort para persistencia de usuarios
│   │       └── exception/
│   │           ├── AuthenticationException.java
│   │           ├── InvalidCredentialsException.java
│   │           └── InvalidTokenException.java
│   ├── application/
│   │   └── src/main/java/com/arquisoft/seguridad/application/
│   │       ├── auth/
│   │       │   ├── AuthenticateUserInputPort.java   # InputPort (contiene inner record AuthResult)
│   │       │   ├── AuthenticateUserUseCaseImpl.java
│   │       │   ├── LogoutInputPort.java
│   │       │   ├── LogoutUseCaseImpl.java
│   │       │   ├── RefreshTokenInputPort.java       # InputPort (contiene inner record RefreshResult)
│   │       │   ├── RefreshTokenUseCaseImpl.java
│   │       │   ├── ValidateTokenInputPort.java      # InputPort (contiene inner record ValidationResult)
│   │       │   └── ValidateTokenUseCaseImpl.java
│   │       ├── usuario/
│   │       │   ├── CrearUsuarioInputPort.java
│   │       │   ├── CrearUsuarioUseCaseImpl.java
│   │       │   ├── RegistrarUsuarioInputPort.java
│   │       │   └── RegistrarUsuarioUseCaseImpl.java
│   │       ├── port/out/
│   │       │   ├── AuthenticationOutputPort.java   # Contrato Keycloak
│   │       │   ├── TokenOutputPort.java             # Contrato validación JWT
│   │       │   ├── TokenBlacklistOutputPort.java    # Contrato Redis blacklist
│   │       │   └── CurrentUserOutputPort.java       # Contrato Spring Security context
│   │       └── dto/
│   │           ├── LoginRequestDTO.java
│   │           ├── LoginResponseDTO.java
│   │           ├── AuthenticatedUserDTO.java
│   │           ├── RefreshTokenRequestDTO.java
│   │           ├── TokenValidationReadModel.java    # ReadModel (sufijo ReadModel, no ResponseDTO)
│   │           └── ErrorResponseDTO.java
│   └── infrastructure/
│       └── src/main/java/com/arquisoft/seguridad/infrastructure/
│           ├── adapter/in/
│           │   ├── AuthInputAdapter.java            # InputAdapter (ex AuthController)
│           │   ├── UsuarioInputAdapter.java          # InputAdapter (ex UsuarioController)
│           │   └── UsuarioCreadoInputAdapter.java    # InputAdapter AMQP (ex UsuarioCreadoConsumer)
│           ├── adapter/out/
│           │   ├── UsuarioOutputAdapter.java         # OutputAdapter (ex InMemoryUsuarioRepository)
│           │   ├── JwtTokenOutputAdapter.java        # OutputAdapter (ex JwtTokenAdapter)
│           │   ├── KeycloakAuthOutputAdapter.java    # OutputAdapter (ex KeycloakAuthAdapter)
│           │   ├── CurrentUserOutputAdapter.java     # OutputAdapter (ex CurrentUserAdapter)
│           │   └── RedisTokenBlacklistOutputAdapter.java  # OutputAdapter (ex RedisTokenBlacklistAdapter)
│           ├── config/                   # Configuraciones de SEGURIDAD (no van en config/ raíz)
│           │   ├── SecurityConfig.java    # JWT + OAuth2 Resource Server + método security
│           │   ├── CorsConfig.java        # Orígenes, headers expuestos, credenciales
│           │   ├── RateLimitConfig.java   # Bucket4j per-IP (100/min global, 5/min login)
│           │   └── RestTemplateConfig.java # SimpleClientHttpRequestFactory (SB4 compat)
│           └── filter/
│               ├── RateLimitingFilter.java  # OncePerRequestFilter: evalúa límite por IP
│               └── AuditFilter.java         # Registra METHOD, URI, USER, TIME, STATUS
│
├── fichas/                               # CONTEXTO 2
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── proyectos/                            # CONTEXTO 3
├── artefactos/                           # CONTEXTO 4
├── repositorio_artefactos/               # CONTEXTO 5
├── entregables/                          # CONTEXTO 6
└── evaluaciones/                         # CONTEXTO 7
```

### Convenciones de Nomenclatura

| Capa | Paquete | Sufijo | Ejemplo |
|------|---------|--------|---------|
| **Domain - aggregate roots** | `model/` | `Aggregate` | `FichaPerfilAggregate.java` |
| **Domain - puertos entrada** | `port/in/` | `InputPort` | `CrearFichaInputPort.java` |
| **Domain - puertos salida** | `port/out/` | `OutputPort` | `FichaPerfilOutputPort.java` |
| **Domain - excepciones** | `exception/` | `Exception` | `FichaNotFoundException.java` |
| **Application - DTOs** | `{feature}/dto/` | `DTO` | `LoginRequestDTO.java` |
| **Application - ReadModels** | `{feature}/dto/` | `ReadModel` | `FichaPerfilReadModel.java` |
| **Application - input ports** | `{feature}/command/` o `{feature}/query/` | `InputPort` | `ConsultarFichasPerfilInputPort.java` |
| **Application - use cases** | `{feature}/command/` o `{feature}/query/` | `UseCaseImpl` | `ConsultarFichasPerfilUseCaseImpl.java` |
| **Infrastructure - entrada** | `adapter/in/` | `InputAdapter` | `FichaPerfilInputAdapter.java` |
| **Infrastructure - salida** | `adapter/out/` | `OutputAdapter` | `FichaPerfilOutputAdapter.java` |
| **Infrastructure - config** | `config/` | `Config` | `FichasConfig.java` |

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
    implementation 'com.bucket4j:bucket4j-core:7.6.0'
}
```

---

## Módulo Shared

El módulo `shared` contiene **7 sub-módulos** reutilizables por cualquier contexto:

| Sub-módulo | Contenido | Uso |
|-----------|-----------|-----|
| `domain` | DomainEvent, AggregateRoot | Clases base para entidades con eventos |
| `exceptions` | DomainException | Excepción base de negocio |
| `amqp` | EventPublisher (interface) | Publicar eventos a RabbitMQ |
| `postgres` | BaseRepository (JpaRepository) | Repositorios base JPA |
| `redis` | RedisClient (interface) | Operaciones de cache |
| `web` | HttpClient (interface) | Llamadas HTTP entre contextos |
| `validation` | @ValidEmail, EmailValidator | Anotaciones de validación |

### Ejemplo de Uso

```gradle
// fichas/domain/build.gradle
dependencies {
    implementation project(':shared:domain')  // Para usar DomainEvent, AggregateRoot
}

// fichas/infrastructure/build.gradle
dependencies {
    implementation project(':shared:amqp')    // Para publicar eventos
    implementation project(':shared:postgres') // Para repositorios JPA
}
```

---

## AggregateRoot y Eventos de Dominio

### ¿Qué es AggregateRoot?

`AggregateRoot` (en `shared/domain`) es la clase base para entidades de dominio que necesitan **emitir eventos de negocio**. Gestiona una lista interna de eventos no publicados que el use case drena después de persistir.

```java
// shared/domain — clase existente
public abstract class AggregateRoot {
    private final List<DomainEvent> unPublishedEvents = new ArrayList<>();

    protected void publishEvent(DomainEvent event) {
        unPublishedEvents.add(event);         // acumula el evento en memoria
    }

    public List<DomainEvent> getUnPublishedEvents() {
        return new ArrayList<>(unPublishedEvents);
    }

    public void clearUnPublishedEvents() {
        unPublishedEvents.clear();
    }
}
```

### ¿Cuándo extender AggregateRoot?

| Contexto | ¿Usa AggregateRoot? | Razón |
|---|---|---|
| `fichas` | ✅ Sí | Emite `FichaCreadaEvent`, `FichaAprobadaEvent` |
| `proyectos` | ✅ Sí | Emite `ProyectoCreadoEvent`, `ProyectoFinalizadoEvent` |
| `artefactos` | ✅ Sí | Emite `ArtefactoCreadoEvent`, `ArtefactoEvaluadoEvent` |
| `entregables` | ✅ Sí | Emite `EntregableCreadoEvent`, `EntregableSubidoEvent` |
| `evaluaciones` | ✅ Sí | Emite `EvaluacionCalificadaEvent` |
| `repositorio_artefactos` | ✅ Sí | Emite `VersionPublicadaEvent` |
| `seguridad` | ❌ No | Contexto transversal, delega a Keycloak, sin estado propio |

### Estructura de carpetas en domain (con eventos)

```
{contexto}/domain/src/main/java/com/arquisoft/{contexto}/domain/
├── model/
│   └── {Entidad}Aggregate.java    ← extends AggregateRoot
├── event/                         ← carpeta para eventos de dominio
│   ├── {Entidad}CreadaEvent.java
│   ├── {Entidad}AprobadaEvent.java
│   └── {Entidad}FinalizadaEvent.java
├── port/
│   ├── in/                        ← InputPort interfaces (o en application layer)
│   └── out/                       ← OutputPort interfaces
└── exception/
```

### Ejemplo completo: aggregate con AggregateRoot

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/FichaPerfilAggregate.java
package com.arquisoft.fichas.domain.model;

import com.arquisoft.shared.domain.AggregateRoot;
import com.arquisoft.fichas.domain.event.FichaPerfilCreadaEvent;
import java.util.UUID;

public class FichaPerfilAggregate extends AggregateRoot {  // ← sufijo Aggregate

    private final UUID id;
    private final String tituloProyecto;
    private final AsesorFicha asesorFicha;

    private FichaPerfilAggregate(UUID id, String tituloProyecto, AsesorFicha asesorFicha) {
        this.id = id;
        this.tituloProyecto = tituloProyecto;
        this.asesorFicha = asesorFicha;
    }

    // Factory para NUEVA ficha — genera UUID y registra evento
    public static FichaPerfilAggregate build(String tituloProyecto, AsesorFicha asesorFicha) {
        FichaPerfilAggregate ficha = new FichaPerfilAggregate(UUID.randomUUID(), tituloProyecto, asesorFicha);
        ficha.publishEvent(new FichaPerfilCreadaEvent(ficha.id.toString(), tituloProyecto));
        return ficha;
    }

    // Factory para RECONSTRUIR desde persistencia — sin evento
    public static FichaPerfilAggregate rebuild(UUID id, String tituloProyecto, AsesorFicha asesorFicha) {
        return new FichaPerfilAggregate(id, tituloProyecto, asesorFicha);
    }

    public UUID getId()                   { return id; }
    public String getTituloProyecto()     { return tituloProyecto; }
    public AsesorFicha getAsesorFicha()   { return asesorFicha; }
}
```

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/event/FichaCreadaEvent.java
package com.arquisoft.fichas.domain.event;

import com.arquisoft.shared.domain.DomainEvent;
import java.util.UUID;

public class FichaCreadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "fichas.ficha.creada";
    public static final String EVENT_TYPE  = "FichaCreadaEvent";

    // Cada evento declara sus propios campos con nombres semánticamente correctos.
    // DomainEvent NO tiene aggregateId genérico — el ID del objeto de dominio
    // pertenece al evento concreto, no a la clase base.
    private final UUID fichaId;
    private final String titulo;

    public FichaCreadaEvent(UUID fichaId, String titulo) {
        super(EVENT_TOPIC, EVENT_TYPE);  // eventId, occurredAt se generan automáticamente
        this.fichaId = fichaId;
        this.titulo  = titulo;
    }

    public UUID getFichaId()    { return fichaId; }
    public String getTitulo()   { return titulo;  }
}
```

### Use Case: drenar y publicar eventos tras persistir

El **use case** es el único responsable de drenar los eventos del aggregate y entregarlos a RabbitMQ. Nunca lo hace el controller ni el repositorio.

```java
// fichas/application/.../fichaperfil/command/CrearFichaPerfilUseCaseImpl.java
@Component
@RequiredArgsConstructor
public class CrearFichaPerfilUseCaseImpl implements CrearFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final EventPublisher eventPublisher;      // shared:amqp

    @Override
    public FichaPerfilAggregate crear(FichaPerfilAggregate ficha) {
        FichaPerfilAggregate guardada = fichaPerfilOutputPort.save(ficha);  // 1. persistir

        guardada.getUnPublishedEvents()                // 2. drenar eventos acumulados
                .forEach(eventPublisher::publish);     // 3. publicar a RabbitMQ
        guardada.clearUnPublishedEvents();             // 4. limpiar lista

        return guardada;
    }
}
```

### Regla importante

- `build(...)` → para **crear** una entidad nueva: genera UUID + registra eventos.
- `rebuild(...)` → para **reconstruir** desde BD: sin UUID nuevo, sin eventos.
- El dominio **nunca** inyecta `EventPublisher` — solo acumula eventos en memoria.

---

## Virtual Threads (ADR-008)

### Configuración

> **Spring Boot 4.0.x (versión actual del proyecto):** Virtual Threads se activan **automáticamente** cuando la JVM es Java 21+. La propiedad `spring.threads.virtual.enabled=true` ya **no es necesaria** y fue eliminada de `application.yml` con la migración a Boot 4.0.5 (ADR-008).
>
> Si el proyecto estuviera en Spring Boot 3.x, la propiedad requerida sería:
> ```yaml
> spring:
>   threads:
>     virtual:
>       enabled: true
> ```
> En Boot 4.x esto ocurre sin configuración adicional.

Esta configuración hace que Spring Boot reemplace automáticamente los executors de OS threads en **Tomcat**, **`@Async`** y **RabbitMQ listeners**.

### ¿Qué cubre automáticamente?

| Componente del proyecto | Efecto |
|---|---|
| Todos los **InputAdapters** REST (requests HTTP) | Cada request corre en un virtual thread |
| **`KeycloakAuthOutputAdapter`** (HTTP a Keycloak) | Bloqueo I/O sin consumir OS thread |
| **`JwtTokenOutputAdapter`** (decodificación JWT) | Igual |
| **`FichaPerfilOutputAdapter`** y todos los OutputAdapters JPA/JDBC | Queries a BD sin bloquear OS thread |
| **`@RabbitListener`** en InputAdapters AMQP | Mensajes procesados en virtual threads |
| **`AuditFilter`**, **`RateLimitingFilter`** | Mismo virtual thread del request |

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

### Separación entre `config/` raíz y `seguridad/infrastructure/config/`

| Carpeta | Qué va aquí |
|---|---|
| `config/` *(raíz del proyecto)* | Configuraciones **transversales** del build/tooling: `checkstyle.xml`, reglas de análisis estático. |
| `src/main/java/com/arquisoft/` | Punto de entrada (`ArquisoftApplication`) y configuraciones **globales de la API ensamblada**: `config/OpenApiConfig` con `@OpenAPIDefinition` y `@SecurityScheme`. Reside aquí porque es el único módulo con visibilidad de todos los contextos. |
| `seguridad/infrastructure/config/` | Configuraciones **de runtime de Spring Security**: `SecurityConfig`, `CorsConfig`, `RateLimitConfig`, `RestTemplateConfig`. Solo pertenecen al contexto de seguridad. |

---

## Ejemplos Prácticos

### Ejemplo: Crear una Ficha

#### Flujo Completo

```
HTTP POST /api/fichas
    ↓
FichaPerfilInputAdapter.crear(FichaDTO)          [InputAdapter IN]
    ↓
FichaDTO.toDomain() → FichaPerfilAggregate
    ↓
CrearFichaUseCaseImpl.crear(FichaPerfilAggregate) [Use Case]
    ↓
FichaPerfilOutputPort.save(FichaPerfilAggregate)  [OutputPort - Interface]
    ↓
FichaPerfilOutputAdapter.save(FichaPerfilAggregate) [OutputAdapter - Implementation]
    ↓
JpaRepository.save()                              [Database INSERT]
    ↓
HTTP 201 Created
```

#### Código

**1. Aggregate de Dominio**

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/FichaPerfilAggregate.java
package com.arquisoft.fichas.domain.model;

import com.arquisoft.shared.domain.AggregateRoot;
import java.util.UUID;

public class FichaPerfilAggregate extends AggregateRoot {
    private final UUID id;
    private final String tituloProyecto;
    private final AsesorFicha asesorFicha;

    private FichaPerfilAggregate(UUID id, String tituloProyecto, AsesorFicha asesorFicha) {
        this.id = id;
        this.tituloProyecto = tituloProyecto;
        this.asesorFicha = asesorFicha;
    }

    // Factory para NUEVA ficha — genera UUID y puede registrar evento
    public static FichaPerfilAggregate build(String tituloProyecto, AsesorFicha asesorFicha) {
        return new FichaPerfilAggregate(UUID.randomUUID(), tituloProyecto, asesorFicha);
    }

    // Factory para RECONSTRUIR desde persistencia — sin evento
    public static FichaPerfilAggregate rebuild(UUID id, String tituloProyecto, AsesorFicha asesorFicha) {
        return new FichaPerfilAggregate(id, tituloProyecto, asesorFicha);
    }

    public UUID getId() { return id; }
    public String getTituloProyecto() { return tituloProyecto; }
    public AsesorFicha getAsesorFicha() { return asesorFicha; }
}
```

**2. Puerto de Entrada (InputPort)**

```java
// fichas/application/src/main/java/com/arquisoft/fichas/application/fichaperfil/query/ConsultarFichasPerfilInputPort.java
package com.arquisoft.fichas.application.fichaperfil.query;

import com.arquisoft.fichas.application.fichaperfil.dto.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;

public interface ConsultarFichasPerfilInputPort {
    PaginatedResult<FichaPerfilReadModel> ejecutar(PaginationRequest request);
}
```

**3. Puerto de Salida (OutputPort)**

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/FichaPerfilOutputPort.java
package com.arquisoft.fichas.domain.port.out;

import com.arquisoft.fichas.domain.model.FichaPerfilAggregate;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;

public interface FichaPerfilOutputPort {
    PaginatedResult<FichaPerfilAggregate> consultarTodas(PaginationRequest request);
}
```

**4. Implementación UseCaseImpl**

```java
// fichas/application/.../fichaperfil/query/ConsultarFichasPerfilUseCaseImpl.java
package com.arquisoft.fichas.application.fichaperfil.query;

import com.arquisoft.fichas.application.fichaperfil.dto.FichaPerfilReadModel;
import com.arquisoft.fichas.domain.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.pagination.PaginationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilUseCaseImpl implements ConsultarFichasPerfilInputPort {
    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    @Override
    public PaginatedResult<FichaPerfilReadModel> ejecutar(PaginationRequest request) {
        return fichaPerfilOutputPort.consultarTodas(request)
                .map(FichaPerfilReadModel::fromDomain);
    }
}
```

**5. InputAdapter (REST Controller)**

```java
// fichas/infrastructure/.../adapter/in/web/FichaPerfilInputAdapter.java
package com.arquisoft.fichas.infrastructure.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.query.ConsultarFichasPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.dto.FichaPerfilReadModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fichas-perfil")
@RequiredArgsConstructor
public class FichaPerfilInputAdapter {
    private final ConsultarFichasPerfilInputPort consultarFichasPerfilInputPort;

    @GetMapping("/coordinador")
    @PreAuthorize("hasAuthority('ficha:ficha:view')")
    public ResponseEntity<?> consultarTodas(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(consultarFichasPerfilInputPort.ejecutar(
                PaginationRequest.of(page, size)));
    }
}
```

**6. OutputAdapter (JPA)**

```java
// fichas/infrastructure/.../adapter/out/persistence/fichaperfil/FichaPerfilOutputAdapter.java
package com.arquisoft.fichas.infrastructure.adapter.out.persistence.fichaperfil;

import com.arquisoft.fichas.domain.model.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.port.out.FichaPerfilOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FichaPerfilOutputAdapter implements FichaPerfilOutputPort {
    private final FichaPerfilRepository fichaPerfilRepository;

    @Override
    public PaginatedResult<FichaPerfilAggregate> consultarTodas(PaginationRequest request) {
        Page<FichaPerfilEntity> page = fichaPerfilRepository.findAll(
                PageRequest.of(request.getPage(), request.getSize()));
        return PaginatedResult.of(
                page.getContent().stream().map(FichaPerfilMapper::toDomain).toList(),
                request.getPage(), request.getSize(), page.getTotalElements());
    }
}
```

### Test Unitario

```java
// fichas/application/src/test/java/com/arquisoft/fichas/application/usecase/ConsultarFichasPerfilUseCaseImplTest.java
package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.application.fichaperfil.query.ConsultarFichasPerfilUseCaseImpl;
import com.arquisoft.fichas.application.fichaperfil.dto.FichaPerfilReadModel;
import com.arquisoft.fichas.domain.model.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.port.out.FichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultarFichasPerfilUseCaseImplTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @InjectMocks
    private ConsultarFichasPerfilUseCaseImpl consultarFichasPerfilUseCase;

    @Test
    void debeRetornarVacio_cuandoNoHayFichas() {
        PaginationRequest request = PaginationRequest.of(0, 10);
        when(fichaPerfilOutputPort.consultarTodas(request))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        PaginatedResult<FichaPerfilReadModel> resultado = consultarFichasPerfilUseCase.ejecutar(request);

        assertThat(resultado.getContent()).isEmpty();
        verify(fichaPerfilOutputPort, times(1)).consultarTodas(request);
    }
}
```

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
    implementation project(':shared:domain')    // Solo si necesita DomainEvent/AggregateRoot
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

### Flujo de Lectura (GET /fichas-perfil/coordinador)

```
FichaPerfilInputAdapter.consultarTodas(page, size)   [InputAdapter IN]
    ↓
ConsultarFichasPerfilUseCaseImpl.ejecutar(request)   [UseCaseImpl]
    ↓
FichaPerfilOutputPort.consultarTodas(request)        [OutputPort - interface]
    ↓
FichaPerfilOutputAdapter.consultarTodas(request)     [OutputAdapter - implementation]
    ↓
FichaPerfilRepository.findAll(PageRequest)
    ↓
FichaPerfilMapper.toDomain() → FichaPerfilAggregate
    ↓
FichaPerfilReadModel.fromDomain(aggregate)           [ReadModel projection]
    ↓
HTTP Response 200 OK [PaginatedResult<FichaPerfilReadModel>]
```

### Flujo de Escritura (POST /fichas-perfil)

```
HTTP Request [JSON]
    ↓
FichaPerfilInputAdapter.crear(FichaDTO)              [InputAdapter IN]
    ↓
FichaDTO.toDomain() → FichaPerfilAggregate
    ↓
CrearFichaPerfilUseCaseImpl.crear(aggregate)         [UseCaseImpl]
    ↓ [Validaciones de negocio en el aggregate]
FichaPerfilOutputPort.save(aggregate)                [OutputPort - interface]
    ↓
FichaPerfilOutputAdapter.save(aggregate)             [OutputAdapter - implementation]
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
- Rate limiting activo (60 req/min, 3 login/min)
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
- Cada contexto tiene su propio schema de BD

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
| **Contextos** | 7 independientes |
| **Módulo compartido** | shared (7 sub-módulos) |
| **Capas** | Domain → Application → Infrastructure |
| **Framework** | Spring Boot 4.0.5 |
| **BD** | PostgreSQL 18 (1 schema por contexto) |
| **Migraciones** | Flyway 11.20.3 |
| **Build** | Gradle 9.0.0 con Wrapper |
| **Java** | 21 (Virtual Threads habilitados) |
| **Testing** | JUnit 6.0.3 + Mockito + AssertJ |
| **Auth** | Keycloak 26.6 (OAuth2/OIDC Resource Server) |
| **Rate Limiting** | Bucket4j 7.6.0 |
| **Concurrencia** | Virtual Threads (`spring.threads.virtual.enabled=true`) |

---

## Referencias

- **Arquitectura Hexagonal**: https://alistair.cockburn.us/hexagonal-architecture/
- **Clean Architecture**: Robert C. Martin (2017)
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot

---

**Versión**: 1.0.0
