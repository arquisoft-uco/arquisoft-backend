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
├── shared/                               # Módulo compartido (12 sub-módulos)
│   ├── util/                              # UtilText, UtilUUID, UtilCollection, UtilDate, UtilNumber, UtilObject
│   ├── exception/                        # BaseException/BaseError y las 5 excepciones base
│   ├── validation/                       # DomainValidator, ValidationResult (Notification Pattern)
│   ├── domain/                           # DomainEvent, AggregateRoot
│   ├── logger/                           # AppLogger
│   ├── redis/                            # RedisClient
│   ├── amqp/                             # EventPublisher
│   ├── web/                              # HttpClient, TraceIdFilter, @UuidValido
│   ├── minio/                            # Cliente MinIO
│   ├── postgres/                         # BaseRepository
│   ├── message/                          # CatalogoMensajes
│   └── notification/                     # EnvioNotificacionOutputPort (SMTP)
│
├── seguridad/                            # CONTEXTO: sin DB propia (Keycloak + Redis)
│   ├── domain/
│   │   └── src/main/java/com/arquisoft/seguridad/domain/
│   │       └── auth/
│   │           ├── SesionDomain.java       # Aggregate root: sesión activa
│   │           ├── TokenDomain.java        # Aggregate root: validación de un JWT
│   │           ├── model/                  # CredencialesSesion, IdentidadToken (value objects)
│   │           ├── secondaryport/
│   │           │   ├── AutenticacionOutputPort.java   # Contrato Keycloak
│   │           │   ├── ValidacionTokenOutputPort.java # Contrato validación JWT
│   │           │   ├── TokenInvalidadoOutputPort.java # Contrato Redis blacklist
│   │           │   └── UsuarioActualOutputPort.java   # Contrato Spring Security context
│   │           └── exception/
│   │               └── AuthenticationException.java
│   ├── application/
│   │   └── src/main/java/com/arquisoft/seguridad/application/
│   │       └── auth/command/
│   │           ├── primaryport/
│   │           │   ├── interactor/         # Autenticar/CerrarSesion/Refrescar/ValidarToken Interactor(+Impl)
│   │           │   └── model/              # AutenticarUsuarioCommand, TokenSesionCommand
│   │           ├── usecase/                # *UseCase(+Impl); colaborador interno, no bajo primaryport/
│   │           └── result/                 # AutenticacionResult, RefrescoTokenResult, ValidacionTokenResult
│   └── infrastructure/
│       └── src/main/java/com/arquisoft/seguridad/infrastructure/
│           ├── auth/command/
│           │   ├── primaryadapter/web/
│           │   │   ├── AutenticacionCommandController.java
│           │   │   └── dto/                # LoginRequestDTO (con su propio toCommand()), LoginResponseDTO, ...
│           │   └── secondaryadapter/
│           │       ├── keycloak/KeycloakAuthOutputAdapter.java
│           │       ├── redis/RedisTokenBlacklistOutputAdapter.java
│           │       ├── jwt/JwtTokenOutputAdapter.java
│           │       └── security/UsuarioActualOutputAdapter.java
│           ├── config/                   # Configuraciones de SEGURIDAD (no van en config/ raíz)
│           │   ├── security/SeguridadConfig.java    # JWT + OAuth2 Resource Server + método security
│           │   ├── cors/CorsConfig.java             # Orígenes, headers expuestos, credenciales
│           │   ├── ratelimit/LimiteSolicitudesConfig.java   # Bucket4j per-IP (100/min global, 5/min login)
│           │   └── http/RestTemplateConfig.java     # SimpleClientHttpRequestFactory (SB4 compat)
│           └── filter/
│               ├── LimitadorSolicitudesFilter.java  # OncePerRequestFilter: evalúa límite por IP
│               └── AuditFilter.java         # Registra METHOD, URI, USER, TIME, STATUS
│
├── usuarios/                              # CONTEXTO: alta de usuarios (extraído de seguridad)
│   ├── domain/                            # UsuarioDomain, secondaryport/UsuarioOutputPort
│   ├── application/                       # CrearUsuarioCommand, command/primaryport/interactor, usecase
│   └── infrastructure/                    # UsuarioCommandController, secondaryadapter/repository
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

### Convenciones de Nomenclatura

| Capa | Paquete | Sufijo | Ejemplo |
|------|---------|--------|---------|
| **Domain - aggregate roots** | `domain/{feature}/` (directo, sin subcarpeta) | `Domain` | `FichaPerfilDomain.java` |
| **Domain - reglas de negocio** | `domain/{feature}/rules/` (+`impl/`) | `Rule`/`RuleImpl` | `FichaPerfilTituloUnicoRule.java` |
| **Domain - puertos salida** | `domain/{feature}/secondaryport/` | `OutputPort` | `FichaPerfilOutputPort.java` |
| **Domain - excepciones** | `exception/` | `Exception` | `FichaNoEncontradaException.java` |
| **Application - Commands** | `{feature}/command/primaryport/model/` | `Command` | `RegistrarFichaPerfilCommand.java` |
| **Application - ReadModels** | `{feature}/query/readmodel/` | `ReadModel` | `FichaPerfilReadModel.java` |
| **Application - interactor (comando)** | `{feature}/command/primaryport/interactor/` | `Interactor` | `RegistrarFichaPerfilInteractor.java` |
| **Application - use cases** | `{feature}/command/usecase/` o `{feature}/query/primaryport/usecase/` | `UseCase`/`UseCaseImpl` | `ConsultarFichasPerfilUseCaseImpl.java` |
| **Infrastructure - entrada web** | `{feature}/command\|query/primaryadapter/web/` | `Controller` | `RegistrarFichaPerfilController.java` |
| **Infrastructure - entrada AMQP** | `{feature}/command/primaryadapter/amqp/` | `InputAdapter` | `UsuarioCreadoInputAdapter.java` |
| **Infrastructure - salida** | `{feature}/command\|query/secondaryadapter/...` | `OutputAdapter` | `FichaPerfilCommandOutputAdapter.java` |
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

El módulo `shared` contiene **12 sub-módulos** reutilizables por cualquier contexto:

| Sub-módulo | Contenido | Uso |
|-----------|-----------|-----|
| `util` | UtilText, UtilUUID, UtilCollection, UtilDate, UtilNumber, UtilObject | Helpers estáticos sin estado |
| `exception` | BaseException/BaseError + 5 excepciones base | Jerarquía de excepciones del proyecto; sin dependencias propias (hoja del grafo) |
| `validation` | DomainValidator, ValidationResult, DomainValidationException, ApplicationValidationException | Notification Pattern: acumula errores en vez de lanzar en el primero |
| `domain` | DomainEvent, AggregateRoot | Clases base para entidades con eventos |
| `logger` | AppLogger (interface) | Logging desacoplado de SLF4J |
| `redis` | RedisClient (interface) | Operaciones de cache |
| `amqp` | EventPublisher (interface) | Publicar eventos a RabbitMQ |
| `web` | HttpClient, TraceIdFilter, GlobalAppExceptionHandler, `@UuidValido` | Llamadas HTTP entre contextos, trazabilidad, manejo global de errores |
| `minio` | Cliente MinIO | Almacenamiento de archivos |
| `postgres` | BaseRepository (JpaRepository) | Repositorios base JPA |
| `message` | CatalogoMensajes (interface + implementación ResourceBundle) | Catálogo de mensajes desacoplado de la tecnología |
| `notification` | EnvioNotificacionOutputPort | Puerto de envío de notificaciones (SMTP), usado por `notificaciones` |

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
    private final List<DomainEvent> eventosSinPublicar = new ArrayList<>();

    public void publicarEvento(DomainEvent evento) {
        eventosSinPublicar.add(evento);        // acumula el evento en memoria
    }

    public List<DomainEvent> extraerEventosSinPublicar() {
        List<DomainEvent> extraidos = new ArrayList<>(eventosSinPublicar);
        eventosSinPublicar.clear();
        return extraidos;                      // drena Y limpia en una sola operación
    }

    protected List<DomainEvent> obtenerEventosSinPublicar() {
        return new ArrayList<>(eventosSinPublicar);
    }
}
```

### ¿Cuándo extender AggregateRoot?

| Contexto | ¿Usa AggregateRoot? | Razón |
|---|---|---|
| `usuarios` | ✅ Sí | `UsuarioDomain` emite `UsuarioCreadoEvent`, drenado y publicado en `CrearUsuarioUseCaseImpl` |
| `fichas` | ✅ Sí | Los aggregate roots (`FichaPerfilDomain`, ...) extienden `AggregateRoot`; en la práctica hoy publican eventos directo desde el use case (`eventPublisher.publish(new XxxEvent(...))`) en vez de pasar por `publicarEvento`/`extraerEventosSinPublicar` — ambas formas son válidas, la de `usuarios` es la que ejercita el drenado |
| `seguridad` | ❌ No | Contexto transversal, delega a Keycloak, sin estado propio |
| `notificaciones` | ❌ No | Reacciona a eventos de otros contextos, no los emite |
| `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables`, `evaluaciones` | — | Scaffolding: sin código de dominio todavía, se espera que sigan el mismo patrón que `fichas`/`usuarios` al implementarse |

### Estructura de carpetas en domain (con eventos)

```
{contexto}/domain/src/main/java/com/arquisoft/{contexto}/domain/
└── {feature}/
    ├── {Entidad}Domain.java        ← extends AggregateRoot; vive directo aquí, sin subcarpeta
    ├── secondaryport/               ← OutputPort interfaces
    └── exception/
event/                                ← eventos de dominio compartidos entre features del contexto
└── {Entidad}CreadoEvent.java
```

### Ejemplo completo: aggregate con AggregateRoot (caso real, `usuarios`)

```java
// usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/usuario/UsuarioDomain.java
package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.events.AggregateRoot;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.usuarios.domain.usuario.event.UsuarioCreadoEvent;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;

import java.util.UUID;

public final class UsuarioDomain extends AggregateRoot {  // ← sufijo Domain, no Aggregate

    private final UUID id;
    private final String email;
    private final UsuarioRole rol;

    private UsuarioDomain(UUID id, String email, UsuarioRole rol) {
        this.id = id;
        this.email = email;
        this.rol = rol;
    }

    // Factory para NUEVA entidad — valida, genera UUID y registra evento
    public static UsuarioDomain crear(String email, UsuarioRole rol) {
        if (email == null || email.isBlank()) {
            throw new DomainException("El email del usuario no puede ser vacio", "USUARIO_EMAIL_REQUERIDO");
        }
        UsuarioDomain usuario = new UsuarioDomain(UUID.randomUUID(), email.trim().toLowerCase(), rol);
        usuario.publicarEvento(new UsuarioCreadoEvent(usuario.id, usuario.email, usuario.rol.getCode()));
        return usuario;
    }

    // Factory para RECONSTRUIR desde persistencia — sin evento
    public static UsuarioDomain reconstruir(UUID id, String email, UsuarioRole rol) {
        return new UsuarioDomain(id, email, rol);
    }

    public UUID getId()          { return id; }
    public String getEmail()     { return email; }
    public UsuarioRole getRol()  { return rol; }
}
```

```java
// usuarios/domain/src/main/java/com/arquisoft/usuarios/domain/usuario/event/UsuarioCreadoEvent.java
package com.arquisoft.usuarios.domain.usuario.event;

import com.arquisoft.shared.events.DomainEvent;
import java.util.UUID;

public class UsuarioCreadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "usuarios.usuario.creado";
    public static final String EVENT_TYPE  = "UsuarioCreadoEvent";

    // Cada evento declara sus propios campos con nombres semánticamente correctos.
    // DomainEvent NO tiene aggregateId genérico — el ID del objeto de dominio
    // pertenece al evento concreto, no a la clase base.
    private final UUID usuarioId;
    private final String email;
    private final String rol;

    public UsuarioCreadoEvent(UUID usuarioId, String email, String rol) {
        super(EVENT_TOPIC, EVENT_TYPE);  // idEvento, ocurridoEn se generan automáticamente
        this.usuarioId = usuarioId;
        this.email = email;
        this.rol = rol;
    }

    public UUID getUsuarioId()  { return usuarioId; }
    public String getEmail()    { return email; }
    public String getRol()      { return rol; }
}
```

### Use Case: drenar y publicar eventos tras persistir

El **use case** es responsable de drenar los eventos del aggregate y entregarlos a RabbitMQ tras persistir. Nunca lo hace el controller ni el repositorio.

```java
// usuarios/application/src/main/java/com/arquisoft/usuarios/application/usuario/command/usecase/impl/CrearUsuarioUseCaseImpl.java
@Component
@RequiredArgsConstructor
public class CrearUsuarioUseCaseImpl implements CrearUsuarioUseCase {

    private final UsuarioOutputPort usuarioOutputPort;
    private final CrearUsuarioValidator crearUsuarioValidator;
    private final EventPublisher eventPublisher;      // shared:amqp

    @Override
    public UUID ejecutar(CrearUsuarioCommand entrada) {
        UsuarioDomain usuario = UsuarioDomain.crear(entrada.email(), entrada.rol());  // 1. crear + acumular evento

        crearUsuarioValidator.validar(usuario);

        usuarioOutputPort.save(usuario);                                              // 2. persistir

        usuario.extraerEventosSinPublicar().forEach(eventPublisher::publish);          // 3. drenar y publicar

        return usuario.getId();
    }
}
```

### Regla importante

- `crear(...)` → para **crear** una entidad nueva: valida invariantes, genera UUID y registra eventos — **no** `build(...)`.
- `reconstruir(...)` → para **reconstruir** desde BD: dato ya confiable, sin UUID nuevo, sin eventos, sin re-validar — **no** `rebuild(...)`.
- El dominio **nunca** inyecta `EventPublisher` — solo acumula eventos en memoria vía `publicarEvento(...)`.

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
| **`AuditFilter`**, **`LimitadorSolicitudesFilter`** | Mismo virtual thread del request |

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
RegistrarFichaPerfilUseCase.ejecutar(ficha)                    [usecase — valida existencia/unicidad, orquesta]
    ↓
FichaPerfilOutputPort.registrarFicha(ficha)                    [secondaryport — interfaz]
    ↓
FichaPerfilCommandOutputAdapter.registrarFicha(ficha)           [secondaryadapter/repository — implementación]
    ↓
FichaPerfilRepository.save(FichaPerfilMapper.toEntity(ficha))   [Spring Data JPA — INSERT]
    ↓
HTTP 201 Created
```

El `Interactor` es el único que orquesta varios casos de uso y posee la transacción (registra la ficha, le asigna su estado inicial y vincula a los estudiantes en una sola unidad de trabajo); ninguno de esos pasos intermedios se muestra aquí por brevedad — ver `RegistrarFichaPerfilInteractorImpl` en el código fuente.

#### Código

**1. DTO + Mapper (infrastructure)**

```java
// fichas/infrastructure/.../fichaperfil/command/primaryadapter/web/dto/RegistrarFichaPerfilRequestDTO.java
public record RegistrarFichaPerfilRequestDTO(
        String tituloProyecto, String asesorFicha, List<String> estudiantes) {}
```

```java
// fichas/infrastructure/.../fichaperfil/command/primaryadapter/web/mapper/RegistrarFichaPerfilRequestMapper.java
public final class RegistrarFichaPerfilRequestMapper {

    private RegistrarFichaPerfilRequestMapper() {}

    public static RegistrarFichaPerfilCommand toCommand(RegistrarFichaPerfilRequestDTO dto) {
        return RegistrarFichaPerfilCommand.crear(dto.tituloProyecto(), dto.asesorFicha(), dto.estudiantes());
    }
}
```

El DTO es un record "tonto" sin anotaciones; el `Mapper` externo delega toda la validación de formato al `Command.crear(...)` (ver "DTOs" en `CLAUDE.md` — `seguridad`/`usuarios` usan en cambio un `toCommand()` propio del DTO, por ser contextos más pequeños).

**2. Command (application, `primaryport/model`)**

```java
// fichas/application/.../fichaperfil/command/primaryport/model/RegistrarFichaPerfilCommand.java
public record RegistrarFichaPerfilCommand(String tituloProyecto, UUID asesorFicha, List<UUID> estudiantes) {

    public static RegistrarFichaPerfilCommand crear(
            String tituloProyecto, String asesorFicha, List<String> estudiantes) {
        var result = new ValidationResult();
        // ... DomainValidator.noEnBlanco / uuidValido / tamanioMaximo, acumulando en result
        result.lanzarSiTieneErroresDeEntrada();
        return new RegistrarFichaPerfilCommand(
                tituloProyecto,
                UtilUUID.generateUUIDFromString(asesorFicha),
                estudiantes.stream().map(UtilUUID::generateUUIDFromString).toList());
    }
}
```

**3. Aggregate root (domain)**

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/FichaPerfilDomain.java
public final class FichaPerfilDomain extends AggregateRoot {   // ← sufijo Domain, no Aggregate

    private UUID id;
    private String tituloProyecto;
    private UUID asesorFicha;

    private FichaPerfilDomain() {}

    // Factory para NUEVA ficha — valida invariantes de dominio
    public static FichaPerfilDomain crear(String titulo, UUID asesorFicha) {
        var ficha = new FichaPerfilDomain();
        var result = new ValidationResult();
        ficha.setId();
        ficha.setTituloProyecto(titulo, result);
        ficha.setAsesorFicha(asesorFicha, result);
        result.lanzarSiTieneErrores();
        return ficha;
    }

    // Factory para RECONSTRUIR desde persistencia — dato confiable, sin re-validar
    public static FichaPerfilDomain reconstruir(UUID id, String titulo, UUID asesorFicha) {
        return new FichaPerfilDomain(id, titulo, asesorFicha);
    }

    public UUID getId() { return id; }
    public String getTituloProyecto() { return tituloProyecto; }
    public UUID getAsesorFicha() { return asesorFicha; }
}
```

**4. Puerto de Salida (domain, `secondaryport`)**

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/fichaperfil/secondaryport/FichaPerfilOutputPort.java
public interface FichaPerfilOutputPort {
    void registrarFicha(FichaPerfilDomain ficha);
    Optional<FichaPerfilDomain> buscarPorId(UUID id);
    boolean existePorId(UUID id);
    boolean existePorTituloProyecto(String titulo);
    // ...
}
```

**5. UseCase (application, colaborador interno — no bajo `primaryport`)**

```java
// fichas/application/.../fichaperfil/command/usecase/impl/RegistrarFichaPerfilUseCaseImpl.java
@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilUseCaseImpl implements RegistrarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final RegistrarFichaPerfilValidator registrarFichaPerfilValidator;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public UUID ejecutar(FichaPerfilDomain ficha) {
        registrarFichaPerfilValidator.validar(ficha);          // existencia/unicidad contra BD
        fichaPerfilOutputPort.registrarFicha(ficha);
        logger.info(catalogo.obtener(FichaPerfilKey.LOG_REGISTRADA), ficha.getId());
        return ficha.getId();
    }
}
```

**6. Interactor (application, entry point — dueño de la transacción)**

```java
// fichas/application/.../fichaperfil/command/primaryport/interactor/impl/RegistrarFichaPerfilInteractorImpl.java
@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilInteractorImpl implements RegistrarFichaPerfilInteractor {

    private final RegistrarFichaPerfilUseCase registrarFichaPerfilUseCase;
    private final AsignarEstadoInicialFichaPerfilUseCase asignarEstadoInicialFichaPerfilUseCase;
    private final AsignarEstudiantesFichaPerfilUseCase asignarEstudiantesFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        FichaPerfilDomain ficha = FichaPerfilDomain.crear(command.tituloProyecto(), command.asesorFicha());
        UUID fichaPerfil = registrarFichaPerfilUseCase.ejecutar(ficha);
        asignarEstadoInicialFichaPerfilUseCase.ejecutar(EstadoFichaPerfilDomain.crear(fichaPerfil));
        asignarEstudiantesFichaPerfilUseCase.ejecutar(AsignarEstudiantesFichaPerfilMapper.toDomain(
                new AsignarEstudiantesFichaPerfilCommand(fichaPerfil, command.estudiantes())));
        return fichaPerfil;
    }
}
```

**7. Controller (infrastructure, `primaryadapter/web`) — inyecta el Interactor, nunca el UseCase**

```java
// fichas/infrastructure/.../fichaperfil/command/primaryadapter/web/RegistrarFichaPerfilController.java
@RestController
@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")
@RequiredArgsConstructor
public class RegistrarFichaPerfilController {

    private final RegistrarFichaPerfilInteractor registrarFichaPerfilInteractor;

    @PostMapping
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_CREATE)
    public ResponseEntity<RegistrarFichaPerfilResponseDTO> registrar(
            @RequestBody RegistrarFichaPerfilRequestDTO request) {
        UUID id = registrarFichaPerfilInteractor.ejecutar(RegistrarFichaPerfilRequestMapper.toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrarFichaPerfilResponseDTO(id));
    }
}
```

**8. Output Adapter (infrastructure, `secondaryadapter/repository`)**

```java
// fichas/infrastructure/.../fichaperfil/command/secondaryadapter/repository/FichaPerfilCommandOutputAdapter.java
@Component
@RequiredArgsConstructor
public class FichaPerfilCommandOutputAdapter implements FichaPerfilOutputPort {

    private final FichaPerfilRepository fichaPerfilRepository;
    private final AsesorFichaRepository asesorFichaRepository;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public void registrarFicha(FichaPerfilDomain ficha) {
        AsesorFichaEntity asesorRef = asesorFichaRepository.getReferenceById(ficha.getAsesorFicha());
        fichaPerfilRepository.save(FichaPerfilMapper.toEntity(ficha, asesorRef));
        logger.debug(catalogo.obtener(FichaPerfilKey.LOG_GUARDADA), ficha.getId());
    }
    // ... resto de métodos del OutputPort
}
```

### Test Unitario

```java
// fichas/application/src/test/java/com/arquisoft/fichas/application/fichaperfil/command/usecase/impl/RegistrarFichaPerfilUseCaseImplTest.java
@ExtendWith(MockitoExtension.class)
class RegistrarFichaPerfilUseCaseImplTest {

    @Mock private FichaPerfilOutputPort fichaPerfilOutputPort;
    @Mock private RegistrarFichaPerfilValidator registrarFichaPerfilValidator;
    @Mock private AppLogger logger;
    @Mock private CatalogoMensajes catalogo;

    @InjectMocks
    private RegistrarFichaPerfilUseCaseImpl registrarFichaPerfilUseCase;

    @Test
    void debeRegistrarFicha_cuandoDatosValidos() {
        FichaPerfilDomain ficha = FichaPerfilDomain.crear("Titulo", UUID.randomUUID());

        UUID id = registrarFichaPerfilUseCase.ejecutar(ficha);

        assertThat(id).isEqualTo(ficha.getId());
        verify(registrarFichaPerfilValidator).validar(ficha);
        verify(fichaPerfilOutputPort).registrarFicha(ficha);
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

### Flujo de Lectura (POST /fichas-perfil/consultar)

```
ConsultarFichasPerfilController.consultar(criteria)   [primaryadapter/web — REST Controller]
    ↓
ConsultarFichasPerfilUseCase.ejecutar(FichaPerfilCriteria)  [query/primaryport/usecase — contrato primario, sin interactor]
    ↓
FichaPerfilQueryOutputPort.consultarTodas(criteria)   [query/secondaryport — interfaz]
    ↓
FichaPerfilQueryOutputAdapter.consultarTodas(criteria)  [secondaryadapter/repository — implementación]
    ↓
FichaPerfilJpaSpecification + FichaPerfilRepository.findAll(spec, pageable)
    ↓
FichaPerfilReadModel                                  [proyección directa desde la entidad JPA, vía mapper]
    ↓
HTTP Response 200 OK [PaginatedResult<FichaPerfilReadModel>]
```

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
FichaPerfilOutputPort.registrarFicha(ficha)             [secondaryport — interfaz]
    ↓
FichaPerfilCommandOutputAdapter.registrarFicha(ficha)   [secondaryadapter/repository — implementación]
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
| **Contextos** | 9 independientes (4 con implementación real, 5 scaffolding) |
| **Módulo compartido** | shared (12 sub-módulos) |
| **Capas** | Domain → Application → Infrastructure |
| **Framework** | Spring Boot 4.0.5 |
| **BD** | PostgreSQL 18 (1 schema por contexto) |
| **Migraciones** | Flyway 12.4.0 |
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
