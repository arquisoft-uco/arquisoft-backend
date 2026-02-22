# Arquitectura Hexagonal Modular - Documentación Completa

## Índice

1. [Visión General](#visión-general)
2. [Arquitectura Hexagonal (Puertos y Adaptadores)](#arquitectura-hexagonal-puertos-y-adaptadores)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Módulo Seguridad](#módulo-seguridad)
5. [Módulo Shared](#módulo-shared)
6. [Ejemplos Prácticos](#ejemplos-prácticos)
7. [Configuración y Build](#configuración-y-build)
8. [Flujos de Datos](#flujos-de-datos)
9. [Perfiles de Ejecución](#perfiles-de-ejecución)

---

## Visión General

Este proyecto implementa una **Arquitectura Hexagonal Modular** usando **Spring Boot 3.2.4** y **Gradle** como herramienta de construcción. La arquitectura se basa en el patrón de **Puertos y Adaptadores** con **7 contextos independientes**.

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
│   ├── validation/                       # @ValidEmail, EmailValidator
│   ├── notifications/                    # NotificationService
│   └── example/                          # Ejemplo de referencia
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
│           ├── config/
│           │   ├── SecurityConfig.java
│           │   ├── CorsConfig.java
│           │   ├── RateLimitConfig.java
│           │   └── RestTemplateConfig.java
│           └── filter/
│               ├── RateLimitingFilter.java
│               └── AuditFilter.java
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
- Gestión de roles (`ADMIN`, `COORDINADOR`, `DIRECTOR`, `ASESOR`, `JURADO`, `ESTUDIANTE`, `DOCENTE`, `INVITADO`)
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
    implementation 'org.keycloak:keycloak-admin-client:23.0.0'
    implementation 'com.bucket4j:bucket4j-core:7.6.0'
}
```

---

## Módulo Shared

El módulo `shared` contiene **8 sub-módulos** reutilizables por cualquier contexto:

| Sub-módulo | Contenido | Uso |
|-----------|-----------|-----|
| `domain` | DomainEvent, AggregateRoot | Clases base para entidades con eventos |
| `exceptions` | DomainException | Excepción base de negocio |
| `amqp` | EventPublisher (interface) | Publicar eventos a RabbitMQ |
| `postgres` | BaseRepository (JpaRepository) | Repositorios base JPA |
| `redis` | RedisClient (interface) | Operaciones de cache |
| `web` | HttpClient (interface) | Llamadas HTTP entre contextos |
| `validation` | @ValidEmail, EmailValidator | Anotaciones de validación |
| `notifications` | NotificationService (interface) | Envío de notificaciones |

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

## Ejemplos Prácticos

> Ver `shared/example/README.md` para un ejemplo completo con código.

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
javaVersion=17
springBootVersion=3.2.4
jUnitVersion=5.10.2
lombokVersion=1.18.30
h2Version=2.2.224
flywaydbVersion=10.10.0
keycloakVersion=23.0.0
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
| **Módulo compartido** | shared (8 sub-módulos) |
| **Capas** | Domain → Application → Infrastructure |
| **Framework** | Spring Boot 3.2.4 |
| **BD** | PostgreSQL 15 (1 schema por contexto) |
| **Migraciones** | Flyway 10.10.0 |
| **Build** | Gradle 7+ con Wrapper |
| **Java** | 17 |
| **Testing** | JUnit 5.10.2 + Mockito |
| **Auth** | Keycloak 22 (OAuth2/JWT) |
| **Rate Limiting** | Bucket4j 7.6.0 |

---

## Referencias

- **Ejemplo de referencia completo**: `shared/example/README.md`
- **Arquitectura Hexagonal**: https://alistair.cockburn.us/hexagonal-architecture/
- **Clean Architecture**: Robert C. Martin (2017)
- **Spring Boot Documentation**: https://spring.io/projects/spring-boot

---

**Versión**: 1.0.0
