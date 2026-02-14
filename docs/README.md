# Arquisoft Backend - Arquitectura Hexagonal Modular

Aplicación backend para Arquisoft basada en **Arquitectura Hexagonal Modular** con **11 contextos independientes** y **comunicación asincrónica** mediante RabbitMQ.

## 📋 Índice
1. [Contextos de Negocio](#contextos-de-negocio)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Stack Tecnológico](#stack-tecnológico)
4. [Instalación y Configuración](#instalación-y-configuración)
5. [Ejecución](#ejecución)
6. [Arquitectura y Flujos](#arquitectura-y-flujos)

---

## 🏢 Contextos de Negocio

El proyecto está organizado en **11 contextos independientes**, cada uno representando un dominio de negocio específico:

| # | Contexto | Descripción |
|---|----------|-------------|
| 1 | **Usuarios** | Gestión de usuarios, roles, permisos, autenticación |
| 2 | **Fichas** | Fichas de caracterización de trabajos de grado |
| 3 | **Proyectos** | Creación y gestión de proyectos de grado |
| 4 | **Artefactos** | Gestión de documentos y artefactos de proyecto |
| 5 | **Repositorio Artefactos** | Control de versiones y almacenamiento |
| 6 | **Mapas de Ruta** | Planes de estudios y rutas de aprendizaje |
| 7 | **Biblioteca** | Catálogo centralizado de recursos educativos |
| 8 | **Entregables** | Gestión de entregables y hitos del proyecto |
| 9 | **Evaluaciones** | Evaluaciones finales y calificaciones |
| 10 | **Solicitudes** | Peticiones de cambio y aprobaciones |
| 11 | **Notificaciones** | Envío de notificaciones (email, push, SMS) |

---

## 🏗️ Estructura del Proyecto

```
arquisoft-backend/
├── shared/                              # Módulo compartido (eventos, config, etc.)
│   ├── domain/                          # Clases base de dominio
│   │   └── src/main/java/com/arquisoft/shared/
│   │       ├── event/
│   │       │   ├── DomainEvent.java      # Clase base de eventos
│   │       │   ├── AggregateRoot.java    # Raíz de agregado base
│   │       │   └── EventPublisher.java   # Interfaz publicador de eventos
│   │       ├── exception/
│   │       │   └── DomainException.java  # Excepción de negocio base
│   │       └── value/
│   │           ├── Email.java            # Value Object: Email
│   │           └── UserId.java           # Value Object: UserId
│   └── infrastructure/                  # Infraestructura compartida
│       └── src/main/java/com/arquisoft/shared/
│           ├── rabbitmq/
│           │   ├── RabbitMQConfig.java
│           │   └── RabbitMQEventPublisher.java
│           ├── postgres/
│           ├── logging/
│           └── resources/
│
├── usuarios/                            # CONTEXTO 1: Usuarios
│   ├── domain/                          # Lógica pura (modelos, puertos)
│   │   └── src/main/java/com/arquisoft/usuarios/
│   │       ├── model/                   # Entidades de negocio
│   │       └── port/
│   │           ├── in/                  # Puertos de entrada (casos de uso)
│   │           └── out/                 # Puertos de salida (dependencias)
│   ├── application/                     # Orquestación (casos de uso)
│   │   └── src/main/java/com/arquisoft/usuarios/
│   │       ├── dto/                     # Data Transfer Objects
│   │       ├── usecase/                 # Implementación de casos de uso
│   │       └── service/                 # Servicios de aplicación
│   └── infrastructure/                  # Detalles técnicos (BD, APIs, etc.)
│       └── src/main/java/com/arquisoft/usuarios/
│           ├── adapter/
│           │   ├── in/                  # Controllers REST
│           │   └── out/                 # Repositorios, clientes
│           ├── config/                  # Configuración RabbitMQ
│           └── resources/
│               └── db/migration/        # Migraciones Flyway
│
├── fichas/                              # CONTEXTO 2: Fichas
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── proyectos/                           # CONTEXTO 3: Proyectos
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── artefactos/                          # CONTEXTO 4: Artefactos
├── repositorio_artefactos/              # CONTEXTO 5: Repositorio Artefactos
├── mapas_ruta/                          # CONTEXTO 6: Mapas de Ruta
├── biblioteca/                          # CONTEXTO 7: Biblioteca
├── entregables/                         # CONTEXTO 8: Entregables
├── evaluaciones/                        # CONTEXTO 9: Evaluaciones
├── solicitudes/                         # CONTEXTO 10: Solicitudes
├── notificaciones/                      # CONTEXTO 11: Notificaciones
│
├── src/
│   ├── main/
│   │   ├── java/com/arquisoft/
│   │   │   └── ArquisoftApplication.java    # Punto de entrada
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│
├── build.gradle                         # Build principal
├── settings.gradle                      # Definición de módulos
├── gradle.properties                    # Versiones de dependencias
├── docker-compose.yml                   # Orquestación de servicios
├── Dockerfile                           # Imagen Docker multi-stage
├── init-db.sql                          # Inicialización de BD
└── README.md                            # Este archivo

```

---

## 🛠️ Stack Tecnológico

### Backend
- **Framework**: Spring Boot 3.2.4
- **Lenguaje**: Java 17
- **Build**: Gradle 7+
- **Patrón**: Arquitectura Hexagonal (Puertos y Adaptadores)

### Base de Datos
- **Motor**: PostgreSQL 15
- **Migraciones**: Flyway
- **ORM**: Spring Data JPA

### Mensajería y Eventos
- **Message Broker**: RabbitMQ 3.12
- **Modo**: Topic Exchange (desacoplamiento asincrónico)
- **Dead Letter Queue**: Para manejo de errores

### Cache y Sesiones
- **Cache**: Redis 7
- **Sesiones distribuidas**: Redis

### Autenticación y Autorización
- **Servidor OAuth2**: Keycloak 22
- **Protocolo**: OpenID Connect

### Almacenamiento de Archivos
- **Nextcloud**: 27
- **Protocolo**: WebDAV

### Monitoreo
- **Métricas**: Prometheus (Spring Boot Actuator)
- **Logs**: Spring Logging (SLF4J)
- **Health Checks**: Spring Boot Actuator

### Testing
- **Testing**: JUnit 5 + Mockito
- **Assertions**: AssertJ (opcional)

### Utilidades
- **Lombok**: Reducción de boilerplate
- **Jackson**: Serialización JSON

---

## 🚀 Instalación y Configuración

### Requisitos Previos
- Java 17+
- Docker y Docker Compose
- Git

### Paso 1: Clonar el Repositorio
```bash
git clone <repositorio-url>
cd arquisoft-backend
```

### Paso 2: Levantar Servicios con Docker Compose
```bash
docker-compose up -d
```

Esto levantará:
- PostgreSQL (puerto 5432)
- RabbitMQ (puertos 5672, 15672)
- Redis (puerto 6379)
- Keycloak (puerto 8081)
- Nextcloud (puerto 8082)

### Paso 3: Compilar y Ejecutar la Aplicación

```bash
# Construir el proyecto
./gradlew build

# Ejecutar aplicación
./gradlew bootRun
```

La aplicación estará disponible en: `http://localhost:8080/api`

---

## 📡 Acceso a Servicios

| Servicio | URL | Usuario | Contraseña |
|----------|-----|--------|-----------|
| **Backend** | http://localhost:8080/api | - | - |
| **RabbitMQ Management** | http://localhost:15672 | guest | guest |
| **Keycloak Admin** | http://localhost:8081/admin | admin | admin |
| **Nextcloud** | http://localhost:8082 | admin | admin123 |
| **PostgreSQL** | localhost:5432 | arquisoft | arquisoft123 |
| **Redis** | localhost:6379 | - | - |

---

## 🔄 Arquitectura de Eventos

### Flujo Asincrónico

1. **Request Síncrono**: El cliente envía una solicitud HTTP
2. **Respuesta Inmediata**: El contexto responde rápidamente (< 100ms)
3. **Publicación de Evento**: El dominio emite un evento de negocio
4. **RabbitMQ**: El evento se envía al exchange de eventos
5. **Consumo Asincrónico**: Otros contextos consumen el evento en background
6. **Independencia**: Cada contexto procesa sin bloquear al usuario

```
Usuario (HTTP)
    ↓ (Síncrono)
[CONTEXTO A - Controller]
    ↓
[CONTEXTO A - UseCase]
    ↓
[BD + Evento]
    ↓
HTTP 201 Created ✅ (Usuario satisfecho - ~100ms)
    
    ↓↓↓ (ASINCRÓNICO - Background)
    
[RabbitMQ - Exchange]
    ↓
[CONTEXTO B - EventListener]
[CONTEXTO C - EventListener]
[CONTEXTO D - EventListener]
    ↓
[Procesamiento independiente]
```

### Eventos Principales

Ver `ARQUITECTURA_ASINCRONICO_ARQUISOFT.md` para la tabla completa de eventos y routing keys.

Ejemplo:
```
Evento: UsuarioCreadoEvent
Routing Key: usuarios.creado
Publicador: Contexto Usuarios
Subscribers: Proyectos, Fichas, Solicitudes, Notificaciones
```

---

## 📦 Estructura de Módulos (Gradle)

### Dependencias Entre Módulos

```
shared:domain (base)
    ↓
shared:infrastructure (usa shared:domain)
    ↓
{contexto}:domain (usa shared:domain)
    ↓
{contexto}:application (usa {contexto}:domain + shared:infrastructure)
    ↓
{contexto}:infrastructure (usa {contexto}:application + shared:infrastructure)
    ↓
Aplicación Principal (usa todos los *:infrastructure)
```

---

## 🗄️ Base de Datos

### Esquemas Separados por Contexto

Cada contexto tiene su propio schema de PostgreSQL para garantizar independencia:

```sql
CREATE SCHEMA usuarios;
CREATE SCHEMA fichas;
CREATE SCHEMA proyectos;
-- ... etc para cada contexto
```

### Migraciones con Flyway

Cada módulo de infrastructure tiene migraciones en `src/main/resources/db/migration/`:

```
usuarios/infrastructure/src/main/resources/db/migration/
├── V1.0__usuarios_schema.sql
├── V1.1__usuarios_initial_data.sql
└── V2.0__usuarios_add_fields.sql

fichas/infrastructure/src/main/resources/db/migration/
├── V1.0__fichas_schema.sql
└── V1.1__fichas_initial_data.sql
```

---

## 🔌 RabbitMQ Configuration

### Exchange Principal

```properties
Exchange Name: arquisoft.events
Type: Topic
Durable: true
```

### Queues por Contexto

Cada contexto crea sus propias queues con bindings específicos:

```properties
# Ejemplo: Contexto Usuarios
Queue: usuarios.events
Binding: usuarios.*
Exchange: arquisoft.events

# Ejemplo: Contexto Proyectos (subscribed a eventos de usuarios)
Queue: proyectos.usuarios-events
Binding: usuarios.creado, usuarios.activado
Exchange: arquisoft.events
```

---

## 🧪 Testing

### Estructura de Tests

```
{contexto}/{capa}/src/test/java/com/arquisoft/{contexto}/...
```

### Ejecutar Tests

```bash
# Tests de todo el proyecto
./gradlew test

# Tests de un contexto específico
./gradlew usuarios:test

# Tests de una capa específica
./gradlew usuarios:domain:test
```

### Ejemplo: Test de UseCase

```java
@ExtendWith(MockitoExtension.class)
class CrearUsuarioUseCaseTest {
    
    @Mock
    private UsuarioRepositoryPort usuarioRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    private CrearUsuarioUseCaseImpl useCase;
    
    @BeforeEach
    void setUp() {
        useCase = new CrearUsuarioUseCaseImpl(usuarioRepository, eventPublisher);
    }
    
    @Test
    void deberiaCrearUsuarioYPublicarEvento() {
        // Arrange
        Usuario usuario = Usuario.crear("user@example.com", "Usuario Test");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        
        // Act
        Usuario resultado = useCase.crear(usuario);
        
        // Assert
        assertThat(resultado.getId()).isNotNull();
        verify(eventPublisher, times(1)).publish(any(UsuarioCreadoEvent.class));
    }
}
```

---

## 📊 Monitoreo

### Spring Boot Actuator

Endpoints disponibles en `http://localhost:8080/api/actuator/`:

```
/health              - Estado de salud
/metrics             - Métricas del sistema
/metrics/jvm.memory  - Memoria JVM
/metrics/http.server.requests - Métricas HTTP
/prometheus          - Métricas en formato Prometheus
```

### Logs

Los logs se guardan en `logs/arquisoft.log` y se muestran en consola.

```yaml
# application.yml
logging:
  level:
    com.arquisoft: DEBUG
    org.springframework: INFO
```

---

## 📝 Convenciones de Código

### Nombres de Paquetes

```
com.arquisoft.{contexto}.domain.model       - Entidades
com.arquisoft.{contexto}.domain.port.in     - Puertos entrada
com.arquisoft.{contexto}.domain.port.out    - Puertos salida
com.arquisoft.{contexto}.application.dto    - Data Transfer Objects
com.arquisoft.{contexto}.application.usecase - Casos de uso
com.arquisoft.{contexto}.infrastructure.adapter.in   - Controllers
com.arquisoft.{contexto}.infrastructure.adapter.out  - Repositorios
```

### Nombres de Clases

- **UseCase Ports**: `{AccionNegocio}UseCase` (ej: `CrearUsuarioUseCase`)
- **UseCase Impl**: `{AccionNegocio}UseCaseImpl`
- **Repository Port**: `{Entidad}RepositoryPort`
- **Repository Impl**: `{Entidad}RepositoryAdapter`
- **Controller**: `{Entidad}Controller`
- **DTO**: `{Entidad}DTO`
- **Events**: `{EventoNegocio}Event`

### Nombres de Eventos

Formato: `{NombreEvento}Event`

Ej: `UsuarioCreadoEvent`, `ProyectoFinalizadoEvent`

La clase `DomainEvent` genera automáticamente el routing key:
- `UsuarioCreadoEvent` → `usuario.creado`
- `ProyectoFinalizadoEvent` → `proyecto.finalizado`

---

## 🚢 Deploy (Producción)

### Construir imagen Docker

```bash
docker build -t arquisoft-backend:latest .
```

### Ejecutar en Kubernetes (Ejemplo)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: arquisoft-backend
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: backend
        image: arquisoft-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: url
```

---

## 📚 Recursos Adicionales

- **Arquitectura Hexagonal**: `ARQUITECTURA_Y_ESTRUCTURA.md`
- **Arquitectura Asincrónica Arquisoft**: `ARQUITECTURA_ASINCRONICO_ARQUISOFT.md`
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **RabbitMQ Docs**: https://www.rabbitmq.com/documentation.html

---

## 👥 Contribuir

Ver CONTRIBUTING.md

---

## 📄 Licencia

Este proyecto está licenciado bajo MIT License.

---

**Generado**: Enero 12, 2026  
**Versión**: 1.0.0  
**Arquitectura**: Hexagonal Modular + Eventos Asincronicos
