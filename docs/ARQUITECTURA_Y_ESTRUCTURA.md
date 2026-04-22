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

- Interfaces que definen casos de uso
- Ejemplo: `CrearFichaUseCase`, `ObtenerFichaUseCase`

#### 3. Puertos de Salida (Out)

- Interfaces que definen dependencias externas
- Ejemplo: `FichaRepositoryPort`, `ExternalServicePort`

#### 4. Aplicación (Application)

- Implementa los puertos de entrada (casos de uso)
- Orquesta la lógica de negocio
- Contiene DTOs para transformación de datos

#### 5. Infraestructura (Infrastructure)

- Implementa los puertos de salida
- Contiene adaptadores de entrada (Controllers)
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
│   │       │   └── UserRole.java         # Enum: 8 roles
│   │       ├── port/in/
│   │       │   ├── CurrentUserProvider.java
│   │       │   ├── KeycloakAuthService.java
│   │       │   └── JwtTokenProvider.java
│   │       └── exception/
│   │           ├── AuthenticationException.java
│   │           ├── InvalidCredentialsException.java
│   │           └── InvalidTokenException.java
│   ├── application/
│   │   └── src/main/java/com/arquisoft/seguridad/application/dto/
│   │       ├── LoginRequestDTO.java
│   │       ├── LoginResponseDTO.java
│   │       ├── AuthenticatedUserDTO.java
│   │       ├── RefreshTokenRequestDTO.java
│   │       ├── TokenValidationResponseDTO.java
│   │       └── ErrorResponseDTO.java
│   └── infrastructure/
│       └── src/main/java/com/arquisoft/seguridad/infrastructure/
│           ├── adapter/in/
│           │   ├── AuthController.java
│           │   └── GlobalExceptionHandler.java
│           ├── adapter/out/
│           │   ├── CurrentUserProviderImpl.java
│           │   ├── KeycloakAuthServiceImpl.java
│           │   └── JwtTokenProviderImpl.java
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

| Capa | Paquete | Ejemplo |
|------|---------|---------|
| **Domain - models** | `model/` | `Ficha.java` |
| **Domain - puertos entrada** | `port/in/` | `CrearFichaUseCase.java` |
| **Domain - puertos salida** | `port/out/` | `FichaRepositoryPort.java` |
| **Domain - excepciones** | `exception/` | `FichaNotFoundException.java` |
| **Application - DTOs** | `dto/` | `FichaDTO.java` |
| **Application - use cases** | `usecase/` | `CrearFichaUseCaseImpl.java` |
| **Infrastructure - entrada** | `adapter/in/` | `FichaController.java` |
| **Infrastructure - salida** | `adapter/out/` | `FichaRepositoryAdapter.java` |
| **Infrastructure - config** | `config/` | `FichasConfig.java` |

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
│   └── {Entidad}.java          ← extends AggregateRoot
├── event/                      ← carpeta para eventos de dominio
│   ├── {Entidad}CreadaEvent.java
│   ├── {Entidad}AprobadaEvent.java
│   └── {Entidad}FinalizadaEvent.java
├── port/
│   ├── in/
│   └── out/
└── exception/
```

### Ejemplo completo: entidad con AggregateRoot

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/Ficha.java
package com.arquisoft.fichas.domain.model;

import com.arquisoft.shared.domain.AggregateRoot;
import com.arquisoft.fichas.domain.event.FichaCreadaEvent;
import java.util.UUID;

public class Ficha extends AggregateRoot {   // ← hereda gestión de eventos

    private final UUID id;
    private final String titulo;
    private final String estado;

    private Ficha(UUID id, String titulo, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.estado = estado;
    }

    // Factory para NUEVA ficha — genera UUID y registra evento
    public static Ficha build(String titulo) {
        Ficha ficha = new Ficha(UUID.randomUUID(), titulo, "BORRADOR");
        ficha.publishEvent(new FichaCreadaEvent(ficha.id.toString(), titulo));
        return ficha;
    }

    // Factory para RECONSTRUIR desde persistencia — sin evento
    public static Ficha rebuild(UUID id, String titulo, String estado) {
        return new Ficha(id, titulo, estado);
    }

    public UUID getId()       { return id; }
    public String getTitulo() { return titulo; }
    public String getEstado() { return estado; }
}
```

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/event/FichaCreadaEvent.java
package com.arquisoft.fichas.domain.event;

import com.arquisoft.shared.domain.DomainEvent;

public class FichaCreadaEvent extends DomainEvent {
    private final String titulo;

    public FichaCreadaEvent(String aggregateId, String titulo) {
        super(aggregateId);   // eventId, occurredAt y eventType se generan automáticamente
        this.titulo = titulo;
    }

    public String getTitulo() { return titulo; }
}
```

### Use Case: drenar y publicar eventos tras persistir

El **use case** es el único responsable de drenar los eventos del aggregate y entregarlos a RabbitMQ. Nunca lo hace el controller ni el repositorio.

```java
// fichas/application/src/main/java/com/arquisoft/fichas/application/usecase/CrearFichaUseCaseImpl.java
@Component
@RequiredArgsConstructor
public class CrearFichaUseCaseImpl implements CrearFichaUseCase {

    private final FichaRepositoryPort fichaRepository;
    private final EventPublisher eventPublisher;      // shared:amqp

    @Override
    public Ficha crearFicha(Ficha ficha) {
        Ficha guardada = fichaRepository.save(ficha);  // 1. persistir

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
| Todos los **Controllers** (requests HTTP) | Cada request corre en un virtual thread |
| **`KeycloakAuthAdapter`** (HTTP a Keycloak) | Bloqueo I/O sin consumir OS thread |
| **`JwtTokenAdapter`** (decodificación JWT) | Igual |
| **`FichaRepositoryAdapter`** y todos los adapters JPA/JDBC | Queries a BD sin bloquear OS thread |
| **`@RabbitListener`** en event listeners | Mensajes procesados en virtual threads |
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
FichaController.crear(FichaDTO)          [Adapter IN]
    ↓
FichaDTO.toDomain() → Ficha
    ↓
CrearFichaUseCaseImpl.crearFicha(Ficha)  [Use Case]
    ↓
FichaRepositoryPort.save(Ficha)          [Port OUT - Interface]
    ↓
FichaRepositoryAdapter.save(Ficha)       [Adapter OUT - Implementation]
    ↓
JdbcTemplate.update()                    [Database INSERT]
    ↓
HTTP 201 Created
```

#### Código

**1. Entidad de Dominio**

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/Ficha.java
package com.arquisoft.fichas.domain.model;

import java.time.LocalDateTime;

public class Ficha {
    private final Long id;
    private final String titulo;
    private final String descripcion;
    private final String areaConocimiento;
    private final String estado;
    private final LocalDateTime fechaCreacion;

    private Ficha(Long id, String titulo, String descripcion,
                  String areaConocimiento, String estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.areaConocimiento = areaConocimiento;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public static Ficha build(String titulo, String descripcion, String areaConocimiento) {
        return new Ficha(null, titulo, descripcion, areaConocimiento, "BORRADOR", LocalDateTime.now());
    }

    public static Ficha rebuild(Long id, String titulo, String descripcion,
                                String areaConocimiento, String estado, LocalDateTime fechaCreacion) {
        return new Ficha(id, titulo, descripcion, areaConocimiento, estado, fechaCreacion);
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getAreaConocimiento() { return areaConocimiento; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
```

**2. Puerto de Entrada**

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/in/CrearFichaUseCase.java
package com.arquisoft.fichas.domain.port.in;

import com.arquisoft.fichas.domain.model.Ficha;

public interface CrearFichaUseCase {
    Ficha crearFicha(Ficha ficha);
}
```

**3. Puerto de Salida**

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/FichaRepositoryPort.java
package com.arquisoft.fichas.domain.port.out;

import com.arquisoft.fichas.domain.model.Ficha;
import java.util.List;
import java.util.Optional;

public interface FichaRepositoryPort {
    Ficha save(Ficha ficha);
    Optional<Ficha> findById(Long id);
    List<Ficha> findAll();
    boolean deleteById(Long id);
}
```

**4. Implementación UseCase**

```java
// fichas/application/src/main/java/com/arquisoft/fichas/application/usecase/CrearFichaUseCaseImpl.java
package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.domain.model.Ficha;
import com.arquisoft.fichas.domain.port.in.CrearFichaUseCase;
import com.arquisoft.fichas.domain.port.out.FichaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CrearFichaUseCaseImpl implements CrearFichaUseCase {
    private final FichaRepositoryPort fichaRepositoryPort;

    @Override
    public Ficha crearFicha(Ficha ficha) {
        return fichaRepositoryPort.save(ficha);
    }
}
```

**5. Controller**

```java
// fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/FichaController.java
package com.arquisoft.fichas.infrastructure.adapter.in;

import com.arquisoft.fichas.application.dto.FichaDTO;
import com.arquisoft.fichas.domain.port.in.CrearFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fichas")
@RequiredArgsConstructor
public class FichaController {
    private final CrearFichaUseCase crearFichaUseCase;

    @PostMapping
    public ResponseEntity<FichaDTO> crear(@RequestBody FichaDTO dto) {
        var ficha = crearFichaUseCase.crearFicha(dto.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(FichaDTO.fromDomain(ficha));
    }
}
```

**6. Repository Adapter**

```java
// fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/FichaRepositoryAdapter.java
package com.arquisoft.fichas.infrastructure.adapter.out;

import com.arquisoft.fichas.domain.model.Ficha;
import com.arquisoft.fichas.domain.port.out.FichaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FichaRepositoryAdapter implements FichaRepositoryPort {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Ficha save(Ficha ficha) {
        String sql = "INSERT INTO fichas.ficha (titulo, descripcion, area_conocimiento, estado, fecha_creacion) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            ficha.getTitulo(), ficha.getDescripcion(),
            ficha.getAreaConocimiento(), ficha.getEstado(),
            ficha.getFechaCreacion()
        );
        return ficha;
    }

    @Override
    public Optional<Ficha> findById(Long id) {
        // Implementar SELECT por ID en schema fichas
        return Optional.empty();
    }

    @Override
    public List<Ficha> findAll() {
        // Implementar SELECT ALL en schema fichas
        return List.of();
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM fichas.ficha WHERE id = ?", id) > 0;
    }
}
```

### Test Unitario

```java
// fichas/application/src/test/java/com/arquisoft/fichas/application/usecase/CrearFichaUseCaseImplTest.java
package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.domain.model.Ficha;
import com.arquisoft.fichas.domain.port.out.FichaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearFichaUseCaseImplTest {

    @Mock
    private FichaRepositoryPort fichaRepositoryPort;

    @InjectMocks
    private CrearFichaUseCaseImpl crearFichaUseCase;

    @Test
    void shouldCreateFichaSuccessfully() {
        Ficha ficha = Ficha.build("Mi Ficha", "Descripción", "Ingeniería");
        when(fichaRepositoryPort.save(any(Ficha.class))).thenReturn(ficha);

        Ficha result = crearFichaUseCase.crearFicha(ficha);

        assertNotNull(result);
        assertEquals("Mi Ficha", result.getTitulo());
        verify(fichaRepositoryPort, times(1)).save(ficha);
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

### Flujo de Lectura (GET /api/fichas/1)

```
FichaController.obtenerPorId(1)
    ↓
ObtenerFichaUseCaseImpl.obtener(1)
    ↓
FichaRepositoryPort.findById(1)  [interface]
    ↓
FichaRepositoryAdapter.findById(1)  [implementation]
    ↓
JdbcTemplate.queryForObject()
    ↓
Mapeo SQL → Ficha (entidad)
    ↓
FichaDTO.fromDomain(ficha)
    ↓
HTTP Response 200 OK
```

### Flujo de Escritura (POST /api/fichas)

```
HTTP Request [JSON]
    ↓
FichaController.crear(FichaDTO)
    ↓
FichaDTO.toDomain() → Ficha
    ↓
CrearFichaUseCaseImpl.crearFicha(Ficha)
    ↓ [Validaciones de negocio]
FichaRepositoryPort.save(Ficha) [interface]
    ↓
FichaRepositoryAdapter.save(Ficha) [implementation]
    ↓
INSERT en BD (schema fichas)
    ↓
FichaDTO.fromDomain(ficha)
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
