# Arquitectura Hexagonal Asincrónica para Arquisoft

## Índice
1. [Visión General](#visión-general)
2. [Los 11 Contextos de Arquisoft](#los-11-contextos-de-arquisoft)
3. [Desacoplamiento Asincrónico via Message Queue](#desacoplamiento-asincrónico-via-message-queue)
4. [Estructura Modular Multi-Contexto](#estructura-modular-multi-contexto)
5. [Stack Tecnológico Completo](#stack-tecnológico-completo)
6. [Ejemplo: Flujo de Eventos End-to-End](#ejemplo-flujo-de-eventos-end-to-end)
7. [Configuración RabbitMQ por Contexto](#configuración-rabbitmq-por-contexto)
8. [Monitoreo y Trazabilidad Centralizada](#monitoreo-y-trazabilidad-centralizada)
9. [Despliegue: Docker Compose Completo](#despliegue-docker-compose-completo)

---

## Visión General

Arquisoft requiere una **Arquitectura Hexagonal Modular con Desacoplamiento Asincrónico**:

- **11 Contextos Independientes** en un único backend monolítico
- **Comunicación Asincrónica** via Message Queue (RabbitMQ) para desacoplamiento
- **Baja Latencia** en respuestas al usuario (procesar en background)
- **Logs Centralizados** para auditoría y trazabilidad
- **Stack Tecnológico Completo** (Nextcloud, Keycloak, PostgreSQL, RabbitMQ, Redis)
- **Escalabilidad Gradual** sin cambios en la arquitectura

### Comparativa: Tradicional vs Arquisoft

```
ARQUITECTURA HEXAGONAL TRADICIONAL:
┌──────────────┐
│ Controller   │
│   (IN)       │
└──────┬───────┘
       │ (SÍNCRONO)
       ▼
┌──────────────┐
│ UseCase      │
│ (domain)     │
└──────┬───────┘
       │ (SÍNCRONO)
       ▼
┌──────────────┐
│ Repository   │
│   (OUT)      │
└──────┬───────┘
       │
       ▼
    [DATABASE]


ARQUITECTURA ARQUISOFT (HEXAGONAL + ASYNC):
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│ Controller   │       │ EventListener│       │ ScheduledTask│
│   (IN)       │       │   (IN)       │       │   (IN)       │
└──────┬───────┘       └──────┬───────┘       └──────┬───────┘
       │ (SÍNCRONO)            │ (ASYNC)            │ (ASYNC)
       ▼                       ▼                     ▼
┌─────────────────────────────────────────────────────────┐
│                     Message Queue                       │
│              (RabbitMQ / Redis Streams)                 │
└──────────────────────┬────────────────────────────────┘
                       │
       ┌───────────────┼───────────────┐
       │               │               │
       ▼               ▼               ▼
   CONTEXT A      CONTEXT B       CONTEXT C
   (Usuarios)    (Proyectos)    (Evaluaciones)
   
   ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
   │ Domain      │ │ Domain      │ │ Domain      │
   │ UseCase     │ │ UseCase     │ │ UseCase     │
   │ Repository  │ │ Repository  │ │ Repository  │
   └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
          │               │               │
          └───────────────┼───────────────┘
                          │
                          ▼
                  [PostgreSQL + Redis]
```

---

## Los 11 Contextos de Arquisoft

Cada contexto es un **módulo hexagonal independiente** con su propio:
- Domain (modelos + puertos)
- Application (casos de uso + DTOs)
- Infrastructure (controladores + repositorios + event listeners)

### Contextos Identificados

| # | Contexto | Responsabilidad Principal | Entidades Principales | Eventos que Emite |
|---|----------|--------------------------|----------------------|-------------------|
| 1 | **Usuarios** | Gestión de usuarios, roles, permisos, autenticación, administradores, asesores, jurados | Usuario, Rol, Permiso, EstadoUsuario, Administrador, Asesor, Jurado | `UsuarioCreado`, `UsuarioActivado`, `RolAsignado` |
| 2 | **Fichas (Trabajos de Grado)** | Fichas de caracterización de trabajos de grado | Ficha, TemaProyecto, AreaConocimiento, EstadoFicha | `FichaCreada`, `FichaAprobada`, `FichaModificada`, `FichaRechazada` |
| 3 | **Proyectos de Grado** | Creación y gestión de proyectos de grado | Proyecto, EstadoProyecto, Línea, LineaProyecto | `ProyectoCreado`, `ProyectoAsignado`, `ProyectoFinalizado` |
| 4 | **Artefactos** | Gestión de documentos y artefactos de proyecto | Artefacto, VersionArtefacto, Observacion, EstadoObservacion | `ArtefactoCreado`, `ArtefactoCatalogado`, `ArtefactoEvaluado` |
| 5 | **Repositorio Artefactos** | Control de versiones y almacenamiento de artefactos | RepositorioArtefacto, VersionRepositorioArtefacto | `RepositorioCreado`, `VersionPublicada`, `VersionArchivada` |
| 6 | **Mapas de Ruta** | Planes de estudios y rutas de aprendizaje | Ruta, Asignatura, Prerequisito, EstadoRuta | `RutaCreada`, `RutaAprobada`, `RutaModificada` |
| 7 | **Biblioteca** | Catálogo centralizado de recursos educativos | Recurso, Clasificacion, Etiqueta, EntregableProyectoGrado | `RecursoAñadido`, `RecursoClasificado`, `RecursoUtilizado` |
| 8 | **Entregables Proyectos de Grado** | Gestión de entregables y hitos del proyecto | EntregableProyectoGrado, EstadoEntregable, Hito | `EntregableCreado`, `EntregableSubido`, `EntregableEvaluado` |
| 9 | **Evaluaciones Definitivas** | Evaluaciones finales y calificaciones de proyectos | EvaluacionFinal, Jurado, Criterio, Calificacion | `EvaluacionCreada`, `EvaluacionCalificada`, `EvaluacionFinalizada` |
| 10 | **Solicitudes** | Peticiones de cambio, aprobaciones, trámites | Solicitud, Remitente, Destinatario, Respuesta, EstadoRespuesta, TipoSolicitud | `SolicitudCreada`, `SolicitudAprobada`, `SolicitudRechazada`, `RespuestaEnviada` |
| 11 | **Notificaciones** | Envío de notificaciones por email, push, SMS, eventos en tiempo real | Notificacion, Template, Destinatario, Preferencia, Entrega | `NotificacionEnviada`, `NotificacionEntregada`, `NotificacionFallida` |

---

## Desacoplamiento Asincrónico via Message Queue

### Arquitectura de Eventos

```
┌─────────────────────────────────────────────────────────────────┐
│                    CONTEXTO USUARIOS                            │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ Controller: POST /usuarios → Crear Usuario               │   │
│  │ • Responde inmediatamente al cliente (latencia: 100ms)   │   │
│  │ • Retorna: {"id": 1, "email": "user@example.com"}       │   │
│  └──────────────────┬───────────────────────────────────────┘   │
│                     │                                             │
│  ┌──────────────────▼───────────────────────────────────────┐   │
│  │ UseCase: CrearUsuarioUseCase                             │   │
│  │ • Validar datos                                          │   │
│  │ • Guardar en BD                                          │   │
│  │ • Emitir evento: UsuarioCreado { id, email, rol }       │   │
│  └──────────────────┬───────────────────────────────────────┘   │
│                     │                                             │
│  ┌──────────────────▼───────────────────────────────────────┐   │
│  │ EventPublisher: Publish a RabbitMQ                       │   │
│  │ • Routing Key: usuarios.creado                           │   │
│  │ • Message: {"userId": 1, "email": "user@example.com"}   │   │
│  └──────────────────┬───────────────────────────────────────┘   │
└─────────────────────┼─────────────────────────────────────────────┘
                      │
                      │ [ASYNC - No bloquea al usuario]
                      │
        ┌─────────────▼──────────────┐
        │    RabbitMQ Exchange        │
        │ (tipo: Topic)               │
        │ (nombre: arquisoft.events)  │
        └─────────────┬──────────────┘
                      │
        ┌─────────────┴─────────────┬──────────────────┐
        │                           │                  │
        ▼                           ▼                  ▼
   Queue:                      Queue:              Queue:
   proyectos.events       solicitudes.events  repositorio-artefactos.events
        │                       │                      │
        │ [BACKGROUND]          │ [BACKGROUND]         │ [BACKGROUND]
        │                       │                      │
        ▼                       ▼                      ▼
   ┌──────────────┐        ┌──────────────┐   ┌──────────────────────┐
   │ CONTEXTO     │        │ CONTEXTO     │   │ CONTEXTO             │
   │ PROYECTOS    │        │ SOLICITUDES  │   │ REPOSITORIO-ARTEFACTOS
   │              │        │              │   │                      │
   │ EventListener│        │ EventListener│   │ EventListener        │
   │ procesaEvt() │        │ procesaEvt() │   │ procesaEvt()         │
   └──────────────┘        └──────────────┘   └──────────────────────┘
        │                       │                      │
        ▼                       ▼                      ▼
   [DB Update]             [DB Update]        [DB Update +
                                              Version Control]
```

### Beneficios

✅ **Latencia Baja:** Usuario recibe respuesta inmediata (no espera a otros contextos)  
✅ **Desacoplamiento:** Contextos no dependen unos de otros en tiempo de ejecución  
✅ **Escalabilidad:** Puede procesar más eventos sin degradar respuestas  
✅ **Confiabilidad:** Si falla un consumer, el evento sigue en la queue  
✅ **Auditoría:** Todos los eventos quedan registrados para trazabilidad  

---

## Estructura Modular Multi-Contexto

### Estructura Base del Proyecto

```
arquisoft-backend/
│
├── pom.xml (versiones padre)
├── docker-compose.yml
│
├── shared/                           # Utilidades compartidas
│   ├── domain/
│   │   ├── src/main/java/com/arquisoft/shared/
│   │   │   └── domain/
│   │   │       ├── event/
│   │   │       │   ├── DomainEvent.java (clase base)
│   │   │       │   └── EventPublisher.java (interfaz)
│   │   │       ├── exception/
│   │   │       │   └── DomainException.java
│   │   │       └── value/
│   │   │           ├── Email.java
│   │   │           └── UserId.java
│   │
│   └── infrastructure/
│       ├── src/main/java/com/arquisoft/shared/
│       │   ├── rabbitmq/
│       │   │   ├── RabbitMQEventPublisher.java
│       │   │   └── RabbitMQConfig.java
│       │   ├── postgres/
│       │   │   └── PostgresConfig.java
│       │   └── logging/
│       │       └── CentralizedLogger.java
│       └── src/main/resources/
│           └── application-shared.yml
│
├── usuarios/                         # CONTEXTO 1
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── fichas/                           # CONTEXTO 2
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── proyectos/                        # CONTEXTO 3
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── artefactos/                       # CONTEXTO 4
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── repositorio-artefactos/           # CONTEXTO 5
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── mapas-ruta/                       # CONTEXTO 6
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── biblioteca/                       # CONTEXTO 7
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── entregables/                      # CONTEXTO 8
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── evaluaciones/                     # CONTEXTO 9
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── solicitudes/                      # CONTEXTO 10
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
├── notificaciones/                   # CONTEXTO 11
│   ├── pom.xml
│   ├── domain/ ... (misma estructura)
│   ├── application/ ... (misma estructura)
│   └── infrastructure/ ... (misma estructura)
│
└── api-gateway/                      # API Gateway única
    ├── pom.xml
    ├── src/main/java/com/arquisoft/
    │   ├── ApiGatewayApplication.java
    │   ├── config/
    │   │   ├── SecurityConfig.java (Keycloak OAuth2)
    │   │   └── CorsConfig.java
    │   └── health/
    │       └── HealthController.java
    └── src/main/resources/
        └── application.yml
```

### Depuración de Maven

```xml
<!-- pom.xml PADRE -->
<project>
    <groupId>com.arquisoft</groupId>
    <artifactId>arquisoft</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <modules>
        <module>shared</module>
        <module>usuarios</module>
        <module>fichas</module>
        <module>proyectos</module>
        <module>artefactos</module>
        <module>repositorio-artefactos</module>
        <module>mapas-ruta</module>
        <module>biblioteca</module>
        <module>entregables</module>
        <module>evaluaciones</module>
        <module>solicitudes</module>
        <module>notificaciones</module>
        <module>api-gateway</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <spring-boot.version>3.1.5</spring-boot.version>
        <rabbitmq.version>6.0.0</rabbitmq.version>
        <postgres.version>15</postgres.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

```xml
<!-- pom.xml CONTEXTO (ej: usuarios) -->
<project>
    <parent>
        <groupId>com.arquisoft</groupId>
        <artifactId>arquisoft</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>usuarios</artifactId>
    <packaging>pom</packaging>

    <modules>
        <module>domain</module>
        <module>application</module>
        <module>infrastructure</module>
    </modules>
</project>
```

```xml
<!-- pom.xml DOMAIN -->
<project>
    <parent>
        <groupId>com.arquisoft</groupId>
        <artifactId>usuarios</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>usuarios-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.arquisoft</groupId>
            <artifactId>shared-domain</artifactId>
            <version>1.0.0</version>
        </dependency>

        <!-- NO Spring Boot aquí - solo lógica pura -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

```xml
<!-- pom.xml APPLICATION -->
<project>
    <parent>
        <groupId>com.arquisoft</groupId>
        <artifactId>usuarios</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>usuarios-application</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.arquisoft</groupId>
            <artifactId>usuarios-domain</artifactId>
            <version>1.0.0</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
        </dependency>
    </dependencies>
</project>
```

```xml
<!-- pom.xml INFRASTRUCTURE -->
<project>
    <parent>
        <groupId>com.arquisoft</groupId>
        <artifactId>usuarios</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>usuarios-infrastructure</artifactId>

    <dependencies>
        <dependency>
            <groupId>com.arquisoft</groupId>
            <artifactId>usuarios-application</artifactId>
            <version>1.0.0</version>
        </dependency>

        <dependency>
            <groupId>com.arquisoft</groupId>
            <artifactId>shared-infrastructure</artifactId>
            <version>1.0.0</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit</artifactId>
        </dependency>

        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

---

## Stack Tecnológico Completo

### Backend

```yaml
Framework: Spring Boot 3.1.5
├─ Spring Web (REST APIs)
├─ Spring Data JPA (ORM)
├─ Spring AMQP (RabbitMQ)
├─ Spring Data Redis (Cache)
├─ Spring Security + OAuth2 (Keycloak)
├─ Lombok (reduce boilerplate)
├─ MapStruct (DTO mapping)
└─ JUnit 5 + Mockito (testing)

Message Queue: RabbitMQ 3.12+
├─ Exchanges: Topic (arquisoft.events)
├─ Queues: Una por contexto
├─ DLQ (Dead Letter Queue): Para mensajes fallidos
└─ Queue persistence: Durable queues

Database: PostgreSQL 15+
├─ Shared schema: Un schema por contexto
├─ Migrations: Flyway v10+
├─ Connection Pool: HikariCP (10-20 conexiones)
└─ Replication: Backup diario

Cache: Redis 7+
├─ Session store
├─ Cache distribuido
└─ Rate limiting

File Storage: Nextcloud 28+
├─ WebDAV API
├─ Activity Log
├─ Versions
└─ Sharing policies

Authentication: Keycloak 22+
├─ OpenID Connect
├─ SAML (opcional)
└─ Institution SSO
```

### Frontend

```yaml
Framework: React 18+ o Vue 3+
├─ API Client: Axios/Fetch
├─ State Management: Redux/Pinia
├─ Routing: React Router/Vue Router
└─ UI Components: Material-UI / TailwindCSS
```

### DevOps & Monitoring

```yaml
Container: Docker
├─ Base image: eclipse-temurin:17-jdk-alpine
├─ Multi-stage: Maven compile + runtime
└─ Security: Non-root user

Orchestration: Docker Compose (desarrollo)
├─ Services: Backend, PostgreSQL, RabbitMQ, Redis, Nextcloud, Keycloak
└─ Networks: Backend network (interno)

CI/CD: GitHub Actions
├─ Build: Maven compile + test
├─ Push: Docker image push
└─ Deploy: SSH to UCO server

Monitoring: Prometheus + Grafana
├─ Metrics: Spring Boot Actuator
├─ Dashboards: JVM, HTTP, RabbitMQ
└─ Alerts: Disk, Memory, Queue depth

Logging: ELK Stack (opcional)
├─ Elasticsearch
├─ Logstash
└─ Kibana

Reverse Proxy: Nginx
├─ SSL termination
├─ Load balancing
└─ Gzip compression
```

---

## Ejemplo: Flujo de Eventos End-to-End

### Caso de Uso: Estudiante Sube Entregable

#### Paso 1: Request Síncrono (Baja Latencia)

```http
POST /api/entregables HTTP/1.1
Host: arquisoft.uco.edu.co
Authorization: Bearer <token>
Content-Type: multipart/form-data

proyectoId=123
descripcion=Entrega Fase 1
archivo=documento.pdf
```

#### Paso 2: Contexto ENTREGABLES responde inmediatamente

```java
// entregables/infrastructure/adapter/in/EntregablesController.java
@RestController
@RequestMapping("/api/entregables")
@RequiredArgsConstructor
public class EntregablesController {
    private final CrearEntregableUseCase crearEntregableUseCase;
    
    @PostMapping
    public ResponseEntity<EntregableResponse> crearEntregable(
            @Valid @RequestBody CrearEntregableRequest request,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        
        // 1. Guardar en BD (rápido: ~50ms)
        Entregable entregable = crearEntregableUseCase.ejecutar(
            new CrearEntregableCommand(
                request.getProyectoId(),
                request.getDescripcion(),
                request.getArchivo()
            )
        );
        
        // 2. Retornar inmediatamente al cliente (~100ms total)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(EntregableResponse.from(entregable));
    }
}
```

#### Paso 3: Domain emite evento

```java
// entregables/domain/model/Entregable.java
@Entity
@Getter
public class Entregable extends AggregateRoot {
    private Long id;
    private Long proyectoId;
    private String descripcion;
    private String archivoUrl; // Nextcloud
    private LocalDateTime fechaCreacion;
    
    public static Entregable crear(Long proyectoId, String descripcion, String archivoUrl) {
        Entregable entregable = new Entregable();
        entregable.proyectoId = proyectoId;
        entregable.descripcion = descripcion;
        entregable.archivoUrl = archivoUrl;
        entregable.fechaCreacion = LocalDateTime.now();
        
        // Emitir evento
        entregable.recordEvent(
            new EntregableSubidoEvent(
                entregable.id,
                proyectoId,
                descripcion,
                archivoUrl,
                LocalDateTime.now()
            )
        );
        
        return entregable;
    }
}

// entregables/domain/event/EntregableSubidoEvent.java
public class EntregableSubidoEvent extends DomainEvent {
    public final Long entregableId;
    public final Long proyectoId;
    public final String descripcion;
    public final String archivoUrl;
    
    public EntregableSubidoEvent(Long entregableId, Long proyectoId, 
                                  String descripcion, String archivoUrl, 
                                  LocalDateTime occurredOn) {
        super(occurredOn);
        this.entregableId = entregableId;
        this.proyectoId = proyectoId;
        this.descripcion = descripcion;
        this.archivoUrl = archivoUrl;
    }
}
```

#### Paso 4: UseCase publica evento

```java
// entregables/application/usecase/CrearEntregableUseCaseImpl.java
@Service
@RequiredArgsConstructor
public class CrearEntregableUseCaseImpl implements CrearEntregableUseCase {
    private final EntregableRepositoryPort entregableRepository;
    private final EventPublisherPort eventPublisher;
    private final NextcloudClient nextcloudClient;
    
    @Override
    public Entregable ejecutar(CrearEntregableCommand command) {
        // 1. Validar
        if (command.getArchivo() == null) {
            throw new InvalidEntregableException("Archivo requerido");
        }
        
        // 2. Guardar archivo en Nextcloud
        String archivoUrl = nextcloudClient.uploadFile(
            command.getArchivo(),
            "/proyectos/" + command.getProyectoId()
        );
        
        // 3. Crear entidad
        Entregable entregable = Entregable.crear(
            command.getProyectoId(),
            command.getDescripcion(),
            archivoUrl
        );
        
        // 4. Persistir
        Entregable entregableSaved = entregableRepository.save(entregable);
        
        // 5. Publicar eventos (NO BLOQUEA - async)
        entregableSaved.getDomainEvents()
            .forEach(eventPublisher::publish);
        
        return entregableSaved;
    }
}
```

#### Paso 5: EventPublisher envía a RabbitMQ (Background)

```java
// shared/infrastructure/rabbitmq/RabbitMQEventPublisher.java
@Service
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisherPort {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    @Override
    public void publish(DomainEvent event) {
        // Convertir a JSON
        String messageJson = objectMapper.writeValueAsString(event);
        
        // Determinar routing key basado en tipo de evento
        String routingKey = extractRoutingKey(event.getClass().getSimpleName());
        
        // Enviar a exchange (non-blocking)
        rabbitTemplate.convertAndSend(
            "arquisoft.events",      // Exchange
            routingKey,              // Routing key: "entregables.subido"
            messageJson
        );
        
        log.info("Evento publicado: {} -> {}", 
            event.getClass().getSimpleName(), routingKey);
    }
    
    private String extractRoutingKey(String eventClassName) {
        // EntregableSubidoEvent -> entregables.subido
        String[] parts = eventClassName.split("(?=[A-Z])");
        return parts[0].toLowerCase() + "." + parts[1].toLowerCase();
    }
}
```

#### Paso 6: Otros contextos escuchan eventos (Background)

```java
// evaluaciones/infrastructure/adapter/in/EvaluacionesEventListener.java
@Component
@RequiredArgsConstructor
public class EvaluacionesEventListener {
    private final CrearEvaluacionUseCase crearEvaluacionUseCase;
    
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = "evaluaciones.queue", durable = true),
            exchange = @Exchange(name = "arquisoft.events", type = ExchangeTypes.TOPIC),
            key = "entregables.subido"
        )
    )
    public void onEntregableSubido(String message) {
        try {
            EntregableSubidoEvent event = 
                objectMapper.readValue(message, EntregableSubidoEvent.class);
            
            log.info("Contexto EVALUACIONES: Entregable subido, creando evaluación...");
            
            // Crear evaluación de forma asincrónica
            crearEvaluacionUseCase.ejecutar(
                new CrearEvaluacionCommand(
                    event.entregableId,
                    event.proyectoId,
                    EstadoEvaluacion.PENDIENTE
                )
            );
            
            log.info("Evaluación creada para entregable {}", event.entregableId);
            
        } catch (Exception e) {
            log.error("Error procesando evento EntregableSubido", e);
            // Message irá a DLQ automáticamente si falla
        }
    }
}

// biblioteca/infrastructure/adapter/in/BibliotecaEventListener.java
@Component
@RequiredArgsConstructor
public class BibliotecaEventListener {
    private final CatalogarArtefactoUseCase catalogarArtefactoUseCase;
    
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = "biblioteca.queue", durable = true),
            exchange = @Exchange(name = "arquisoft.events", type = ExchangeTypes.TOPIC),
            key = "entregables.subido"
        )
    )
    public void onEntregableSubido(String message) {
        EntregableSubidoEvent event = 
            objectMapper.readValue(message, EntregableSubidoEvent.class);
        
        log.info("Contexto BIBLIOTECA: Catalogando artefacto...");
        
        catalogarArtefactoUseCase.ejecutar(
            new CatalogarArtefactoCommand(
                event.archivoUrl,
                event.descripcion,
                TipoArtefacto.ENTREGABLE
            )
        );
    }
}

// repositorio-artefactos/infrastructure/adapter/in/RepositorioEventListener.java
@Component
@RequiredArgsConstructor
public class RepositorioEventListener {
    private final PublicarVersionUseCase publicarVersionUseCase;
    private final ArtefactosClient artefactosClient;
    
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = "repositorio-artefactos.queue", durable = true),
            exchange = @Exchange(name = "arquisoft.events", type = ExchangeTypes.TOPIC),
            key = "artefactos.creado"
        )
    )
    public void onArtefactoCreado(String message) {
        ArtefactoCreadoEvent event = 
            objectMapper.readValue(message, ArtefactoCreadoEvent.class);
        
        log.info("Contexto REPOSITORIO-ARTEFACTOS: Creando versión inicial en repositorio...");
        
        // Obtener metadata del artefacto
        Artefacto artefacto = artefactosClient.obtenerArtefacto(event.artefactoId);
        
        // Crear versión en repositorio de control
        publicarVersionUseCase.ejecutar(
            new PublicarVersionCommand(
                event.artefactoId,
                event.proyectoId,
                "Version 1.0 - " + artefacto.getNombre(),
                artefacto.getContenido()
            )
        );
        
        // Inicializar historial de commits (opcional)
        repositorioService.crearHistorial(event.artefactoId);
    }
}
```

#### EventListener: Notificaciones

```java
// notificaciones/infrastructure/adapter/in/NotificacionesEventListener.java
@Component
@RequiredArgsConstructor
public class NotificacionesEventListener {
    private final EnviarNotificacionUseCase enviarNotificacionUseCase;
    private final UsuariosClient usuariosClient;
    private final ObjectMapper objectMapper;
    
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = "notificaciones.queue", durable = true),
            exchange = @Exchange(name = "arquisoft.events", type = ExchangeTypes.TOPIC),
            key = "entregables.subido"
        )
    )
    public void onEntregableSubido(String message) throws JsonProcessingException {
        EntregableSubidoEvent event = 
            objectMapper.readValue(message, EntregableSubidoEvent.class);
        
        log.info("Contexto NOTIFICACIONES: Procesando evento EntregableSubido");
        
        // Obtener información del usuario
        Usuario asesor = usuariosClient.obtenerAsesorDelProyecto(event.proyectoId);
        Usuario estudiante = usuariosClient.obtenerUsuario(event.estudianteId);
        
        // Enviar notificación
        enviarNotificacionUseCase.ejecutar(
            new EnviarNotificacionCommand(
                asesor.getId(),
                "Nuevo entregable subido",
                "El estudiante " + estudiante.getNombre() + 
                    " ha subido un nuevo entregable: " + event.titulo,
                TipoNotificacion.ENTREGABLE_SUBIDO,
                List.of(
                    new DestinoNotificacion("EMAIL", asesor.getEmail()),
                    new DestinoNotificacion("PUSH", asesor.getDeviceToken())
                )
            )
        );
    }
    
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(name = "notificaciones.queue", durable = true),
            exchange = @Exchange(name = "arquisoft.events", type = ExchangeTypes.TOPIC),
            key = "evaluaciones.calificada"
        )
    )
    public void onEvaluacionCalificada(String message) throws JsonProcessingException {
        EvaluacionCalificadaEvent event = 
            objectMapper.readValue(message, EvaluacionCalificadaEvent.class);
        
        log.info("Contexto NOTIFICACIONES: Procesando evento EvaluacionCalificada");
        
        Usuario estudiante = usuariosClient.obtenerUsuario(event.estudianteId);
        
        // Notificar calificación
        enviarNotificacionUseCase.ejecutar(
            new EnviarNotificacionCommand(
                estudiante.getId(),
                "Proyecto evaluado",
                "Tu proyecto ha sido evaluado. Calificación: " + event.calificacion,
                TipoNotificacion.EVALUACION_RESULTADO,
                List.of(
                    new DestinoNotificacion("EMAIL", estudiante.getEmail()),
                    new DestinoNotificacion("PUSH", estudiante.getDeviceToken()),
                    new DestinoNotificacion("SMS", estudiante.getTelefono())
                )
            )
        );
    }
}
```

#### Paso 7: Timeline Completa

```
T=0ms     → Cliente: POST /api/entregables
           │
T=10ms    → ENTREGABLES Controller recibe request
           │
T=30ms    → BD: INSERT INTO entregables
           │     Evento: EntregableSubidoEvent creado
           │
T=50ms    → RabbitMQ: Mensaje publicado
           │
T=100ms   → Cliente RECIBE: 201 Created ✅ (Usuario satisfecho)
           │
[BACKGROUND - No bloquea usuario]
           │
T=110ms   → EVALUACIONES: Escucha evento, crea evaluación
           │
T=130ms   → BIBLIOTECA: Escucha evento, cataloga artefacto
           │
T=150ms   → REPOSITORIO-ARTEFACTOS: Escucha evento, crea version inicial
           │
T=200ms   → Todos los contextos han procesado el evento ✅

```

---

## Configuración RabbitMQ por Contexto

### RabbitMQ Config (Shared Infrastructure)

```java
// shared/infrastructure/rabbitmq/RabbitMQConfig.java
@Configuration
public class RabbitMQConfig {
    
    // Exchange único para toda la aplicación
    public static final String EXCHANGE_NAME = "arquisoft.events";
    
    @Bean
    public TopicExchange arquisoftEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }
    
    // Queues y bindings por contexto (creados dinámicamente)
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
```

### Contexto Usuarios - RabbitMQ Bindings

```java
// usuarios/infrastructure/config/UsuariosRabbitMQConfig.java
@Configuration
public class UsuariosRabbitMQConfig {
    
    // Queue privada para USUARIOS
    @Bean
    public Queue usuariosQueue() {
        return QueueBuilder
            .durable("usuarios.queue")
            .withArgument("x-dead-letter-exchange", "usuarios.dlx")
            .withArgument("x-dead-letter-routing-key", "usuarios.dlq")
            .build();
    }
    
    // DLQ (Dead Letter Queue)
    @Bean
    public Queue usuariosDLQ() {
        return QueueBuilder.durable("usuarios.dlq").build();
    }
    
    @Bean
    public DirectExchange usuariosDLXExchange() {
        return new DirectExchange("usuarios.dlx", true, false);
    }
    
    @Bean
    public Binding usuariosDLQBinding(Queue usuariosDLQ, DirectExchange usuariosDLXExchange) {
        return BindingBuilder.bind(usuariosDLQ)
            .to(usuariosDLXExchange)
            .with("usuarios.dlq");
    }
    
    // Bindings: qué eventos escucha USUARIOS
    @Bean
    public Binding usuariosBindingSolicitudAprobada(
            Queue usuariosQueue,
            TopicExchange arquisoftEventsExchange) {
        return BindingBuilder.bind(usuariosQueue)
            .to(arquisoftEventsExchange)
            .with("solicitudes.aprobada");  // USUARIOS escucha: solicitudes.aprobada
    }
    
    // Ejemplo: Usuarios también escucha cuando se rechaza una solicitud
    @Bean
    public Binding usuariosBindingSolicitudRechazada(
            Queue usuariosQueue,
            TopicExchange arquisoftEventsExchange) {
        return BindingBuilder.bind(usuariosQueue)
            .to(arquisoftEventsExchange)
            .with("solicitudes.rechazada");
    }
}
```

### Contexto Evaluaciones - RabbitMQ Bindings

```java
// evaluaciones/infrastructure/config/EvaluacionesRabbitMQConfig.java
@Configuration
public class EvaluacionesRabbitMQConfig {
    
    @Bean
    public Queue evaluacionesQueue() {
        return QueueBuilder
            .durable("evaluaciones.queue")
            .withArgument("x-dead-letter-exchange", "evaluaciones.dlx")
            .build();
    }
    
    // Evaluaciones escucha: entregables.subido
    @Bean
    public Binding evaluacionesBindingEntregableSubido(
            Queue evaluacionesQueue,
            TopicExchange arquisoftEventsExchange) {
        return BindingBuilder.bind(evaluacionesQueue)
            .to(arquisoftEventsExchange)
            .with("entregables.subido");
    }
    
    // Evaluaciones escucha: proyectos.finalizado
    @Bean
    public Binding evaluacionesBindingProyectoFinalizado(
            Queue evaluacionesQueue,
            TopicExchange arquisoftEventsExchange) {
        return BindingBuilder.bind(evaluacionesQueue)
            .to(arquisoftEventsExchange)
            .with("proyectos.finalizado");
    }
}
```

### Tabla de Routing Keys

| Evento | Routing Key | Context | Publishers | Subscribers |
|--------|-------------|---------|-----------|-------------|
| Usuario creado | `usuarios.creado` | Usuarios | USUARIOS | PROYECTOS, FICHAS, SOLICITUDES |
| Usuario activado | `usuarios.activado` | Usuarios | USUARIOS | PROYECTOS, SOLICITUDES |
| Rol asignado | `usuarios.rol-asignado` | Usuarios | USUARIOS | SOLICITUDES |
| Ficha creada | `fichas.creada` | Fichas | FICHAS | PROYECTOS, SOLICITUDES |
| Ficha aprobada | `fichas.aprobada` | Fichas | FICHAS | PROYECTOS, SOLICITUDES |
| Ficha modificada | `fichas.modificada` | Fichas | FICHAS | PROYECTOS, SOLICITUDES |
| Proyecto creado | `proyectos.creado` | Proyectos | PROYECTOS | ARTEFACTOS, ENTREGABLES, EVALUACIONES, SOLICITUDES |
| Proyecto asignado | `proyectos.asignado` | Proyectos | PROYECTOS | ENTREGABLES, EVALUACIONES |
| Proyecto finalizado | `proyectos.finalizado` | Proyectos | PROYECTOS | EVALUACIONES, SOLICITUDES |
| Artefacto creado | `artefactos.creado` | Artefactos | ARTEFACTOS | REPOSITORIO-ARTEFACTOS, BIBLIOTECA |
| Artefacto catalogado | `artefactos.catalogado` | Artefactos | ARTEFACTOS | BIBLIOTECA |
| Artefacto evaluado | `artefactos.evaluado` | Artefactos | ARTEFACTOS | SOLICITUDES |
| Version publicada | `repositorio.version-publicada` | Repositorio Artefactos | REPOSITORIO-ARTEFACTOS | BIBLIOTECA, ARTEFACTOS |
| Version archivada | `repositorio.version-archivada` | Repositorio Artefactos | REPOSITORIO-ARTEFACTOS | ARTEFACTOS |
| Ruta creada | `mapas-ruta.creada` | Mapas de Ruta | MAPAS-RUTA | PROYECTOS, SOLICITUDES |
| Ruta aprobada | `mapas-ruta.aprobada` | Mapas de Ruta | MAPAS-RUTA | PROYECTOS, SOLICITUDES |
| Ruta modificada | `mapas-ruta.modificada` | Mapas de Ruta | MAPAS-RUTA | PROYECTOS |
| Recurso añadido | `biblioteca.recurso-añadido` | Biblioteca | BIBLIOTECA | MAPAS-RUTA, PROYECTOS |
| Recurso clasificado | `biblioteca.recurso-clasificado` | Biblioteca | BIBLIOTECA | MAPAS-RUTA |
| Recurso utilizado | `biblioteca.recurso-utilizado` | Biblioteca | BIBLIOTECA | (Analytics) |
| Entregable creado | `entregables.creado` | Entregables | ENTREGABLES | EVALUACIONES, ARTEFACTOS, SOLICITUDES |
| Entregable subido | `entregables.subido` | Entregables | ENTREGABLES | EVALUACIONES, ARTEFACTOS, REPOSITORIO-ARTEFACTOS |
| Entregable evaluado | `entregables.evaluado` | Entregables | ENTREGABLES | EVALUACIONES, SOLICITUDES |
| Evaluacion creada | `evaluaciones.creada` | Evaluaciones Definitivas | EVALUACIONES | SOLICITUDES |
| Evaluacion calificada | `evaluaciones.calificada` | Evaluaciones Definitivas | EVALUACIONES | PROYECTOS, SOLICITUDES |
| Evaluacion finalizada | `evaluaciones.finalizada` | Evaluaciones Definitivas | EVALUACIONES | PROYECTOS, SOLICITUDES |
| Solicitud creada | `solicitudes.creada` | Solicitudes | SOLICITUDES | USUARIOS, PROYECTOS |
| Solicitud aprobada | `solicitudes.aprobada` | Solicitudes | SOLICITUDES | USUARIOS, PROYECTOS, ARTEFACTOS, EVALUACIONES |
| Solicitud rechazada | `solicitudes.rechazada` | Solicitudes | SOLICITUDES | USUARIOS, PROYECTOS |
| Respuesta enviada | `solicitudes.respuesta-enviada` | Solicitudes | SOLICITUDES | USUARIOS |
| Usuario creado | `usuarios.creado` | Usuarios | USUARIOS | NOTIFICACIONES |
| Rol asignado | `usuarios.rol-asignado` | Usuarios | USUARIOS | NOTIFICACIONES |
| Proyecto finalizado | `proyectos.finalizado` | Proyectos | PROYECTOS | NOTIFICACIONES |
| Ficha aprobada | `fichas.aprobada` | Fichas | FICHAS | NOTIFICACIONES |
| Entregable subido | `entregables.subido` | Entregables | ENTREGABLES | NOTIFICACIONES |
| Evaluacion calificada | `evaluaciones.calificada` | Evaluaciones Definitivas | EVALUACIONES | NOTIFICACIONES |
| Solicitud aprobada | `solicitudes.aprobada` | Solicitudes | SOLICITUDES | NOTIFICACIONES |
| Solicitud rechazada | `solicitudes.rechazada` | Solicitudes | SOLICITUDES | NOTIFICACIONES |
| Notificación enviada | `notificaciones.enviada` | Notificaciones | NOTIFICACIONES | (Audit Log) |
| Notificación entregada | `notificaciones.entregada` | Notificaciones | NOTIFICACIONES | (Audit Log) |
| Notificación fallida | `notificaciones.fallida` | Notificaciones | NOTIFICACIONES | (Retry Queue)

---

## Monitoreo y Trazabilidad Centralizada

### Logs Centralizados (ELK Stack)

```java
// shared/infrastructure/logging/CentralizedLogger.java
@Component
@RequiredArgsConstructor
public class CentralizedLogger {
    private static final Logger log = LoggerFactory.getLogger(CentralizedLogger.class);
    private final ObjectMapper objectMapper;
    
    // Estructura de log: contexto | evento | usuario | timestamp | resultado
    public void logEventPublished(DomainEvent event, String context, String result) {
        LogEntry entry = LogEntry.builder()
            .timestamp(LocalDateTime.now())
            .context(context)
            .eventType(event.getClass().getSimpleName())
            .eventData(objectMapper.writeValueAsString(event))
            .result(result)
            .build();
        
        log.info("{}", entry.toJson());
    }
    
    public void logEventProcessed(DomainEvent event, String context, String result) {
        LogEntry entry = LogEntry.builder()
            .timestamp(LocalDateTime.now())
            .context(context)
            .eventType(event.getClass().getSimpleName())
            .result(result)
            .build();
        
        log.info("{}", entry.toJson());
    }
}
```

### Métricas Prometheus

```yaml
# prometheus/prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'arquisoft'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
```

### Dashboards Grafana

```json
{
  "dashboard": {
    "title": "Arquisoft - RabbitMQ & Eventos",
    "panels": [
      {
        "title": "Eventos Publicados por Contexto",
        "targets": [
          {
            "expr": "increase(rabbitmq_messages_published_total[5m])"
          }
        ]
      },
      {
        "title": "Tamaño de Queues (alertar si > 1000)",
        "targets": [
          {
            "expr": "rabbitmq_queue_messages_ready"
          }
        ]
      },
      {
        "title": "Latencia de Procesamiento (ms)",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, event_processing_duration_ms)"
          }
        ]
      },
      {
        "title": "Mensajes en DLQ (alertar si > 0)",
        "targets": [
          {
            "expr": "rabbitmq_queue_messages_ready{queue=~\".*dlq\"}"
          }
        ]
      }
    ]
  }
}
```

### Application Insights (Spring Boot Actuator)

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,info,loggers
  metrics:
    export:
      prometheus:
        enabled: true
  health:
    probes:
      enabled: true
```

---

## Despliegue: Docker Compose Completo

### docker-compose.yml

```yaml
version: '3.9'

networks:
  arquisoft:
    driver: bridge

volumes:
  postgres_data:
  redis_data:
  nextcloud_data:
  keycloak_data:

services:
  # ==================== BACKEND ====================
  
  arquisoft-backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: arquisoft-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/arquisoft
      SPRING_DATASOURCE_USERNAME: arquisoft
      SPRING_DATASOURCE_PASSWORD: arquisoft123
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_PORT: 5672
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      KEYCLOAK_ISSUER_URI: http://keycloak:8081/realms/arquisoft
      NEXTCLOUD_HOST: http://nextcloud
      NEXTCLOUD_PORT: 80
    depends_on:
      - postgres
      - rabbitmq
      - redis
      - keycloak
      - nextcloud
    networks:
      - arquisoft
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # ==================== DATABASE ====================
  
  postgres:
    image: postgres:15-alpine
    container_name: arquisoft-postgres
    environment:
      POSTGRES_DB: arquisoft
      POSTGRES_USER: arquisoft
      POSTGRES_PASSWORD: arquisoft123
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./scripts/init-db.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    networks:
      - arquisoft
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U arquisoft"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # ==================== MESSAGE QUEUE ====================
  
  rabbitmq:
    image: rabbitmq:3.12-management-alpine
    container_name: arquisoft-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: arquisoft
      RABBITMQ_DEFAULT_PASS: arquisoft123
      RABBITMQ_DEFAULT_VHOST: /
    ports:
      - "5672:5672"   # AMQP
      - "15672:15672" # Management UI
    networks:
      - arquisoft
    healthcheck:
      test: rabbitmq-diagnostics -q ping
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # ==================== CACHE ====================
  
  redis:
    image: redis:7-alpine
    container_name: arquisoft-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - arquisoft
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

  # ==================== FILE STORAGE ====================
  
  nextcloud:
    image: nextcloud:28-apache
    container_name: arquisoft-nextcloud
    environment:
      NEXTCLOUD_ADMIN_USER: admin
      NEXTCLOUD_ADMIN_PASSWORD: admin123
      MYSQL_HOST: mysql
      MYSQL_USER: nextcloud
      MYSQL_PASSWORD: nextcloud123
      MYSQL_DATABASE: nextcloud
    ports:
      - "8081:80"
    volumes:
      - nextcloud_data:/var/www/html
    depends_on:
      - mysql
    networks:
      - arquisoft
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:80"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    container_name: arquisoft-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_USER: nextcloud
      MYSQL_PASSWORD: nextcloud123
      MYSQL_DATABASE: nextcloud
    ports:
      - "3306:3306"
    networks:
      - arquisoft
    restart: unless-stopped

  # ==================== AUTHENTICATION ====================
  
  keycloak:
    image: quay.io/keycloak/keycloak:22.0.0
    container_name: arquisoft-keycloak
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin123
      KC_DB: postgres
      KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: keycloak123
      KC_PROXY: rewrite
    command: start-dev
    ports:
      - "8082:8080"
    depends_on:
      - postgres
    networks:
      - arquisoft
    restart: unless-stopped

  # ==================== MONITORING ====================
  
  prometheus:
    image: prom/prometheus:latest
    container_name: arquisoft-prometheus
    volumes:
      - ./monitoring/prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"
    networks:
      - arquisoft
    restart: unless-stopped

  grafana:
    image: grafana/grafana:latest
    container_name: arquisoft-grafana
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin123
    ports:
      - "3000:3000"
    depends_on:
      - prometheus
    networks:
      - arquisoft
    restart: unless-stopped

  # ==================== REVERSE PROXY ====================
  
  nginx:
    image: nginx:alpine
    container_name: arquisoft-nginx
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - arquisoft-backend
      - nextcloud
      - keycloak
    networks:
      - arquisoft
    restart: unless-stopped
```

### Dockerfile Multi-Stage

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn dependency:download-sources -q

COPY . .
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Security: non-root user
RUN addgroup -g 1000 arquisoft && \
    adduser -D -u 1000 -G arquisoft arquisoft

COPY --from=builder /build/api-gateway/infrastructure/target/*.jar app.jar
RUN chown -R arquisoft:arquisoft /app

USER arquisoft

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### init-db.sql

```sql
-- Crear schemas para cada contexto
CREATE SCHEMA IF NOT EXISTS usuarios;
CREATE SCHEMA IF NOT EXISTS proyectos;
CREATE SCHEMA IF NOT EXISTS fichas;
CREATE SCHEMA IF NOT EXISTS evaluaciones;
CREATE SCHEMA IF NOT EXISTS entregables;
CREATE SCHEMA IF NOT EXISTS artefactos;
CREATE SCHEMA IF NOT EXISTS biblioteca;
CREATE SCHEMA IF NOT EXISTS mapas_ruta;
CREATE SCHEMA IF NOT EXISTS solicitudes;
CREATE SCHEMA IF NOT EXISTS repositorio_artefactos;
CREATE SCHEMA IF NOT EXISTS notificaciones;

-- Crear usuario para Keycloak
CREATE USER keycloak WITH PASSWORD 'keycloak123';
CREATE DATABASE keycloak OWNER keycloak;

-- Grants
GRANT ALL PRIVILEGES ON DATABASE arquisoft TO arquisoft;
GRANT ALL PRIVILEGES ON ALL SCHEMAS IN DATABASE arquisoft TO arquisoft;
GRANT ALL PRIVILEGES ON ALL TABLES IN DATABASE arquisoft TO arquisoft;
```

---

## Resumen Arquitectónico

| Aspecto | Especificación |
|---------|----------------|
| **Patrón** | Hexagonal Modular + Async Event-Driven |
| **Contextos** | 11 independientes (Usuarios, Fichas, Proyectos, Artefactos, Repositorio Artefactos, Mapas Ruta, Biblioteca, Entregables, Evaluaciones, Solicitudes, Notificaciones) |
| **Comunicación** | RabbitMQ Topic Exchange + Durable Queues |
| **Latencia Usuario** | ~100ms (respuesta síncrona) |
| **Procesamiento Background** | Asincrónico sin latencia inter-contextos |
| **Base de Datos** | PostgreSQL (1 schema por contexto) |
| **Cache** | Redis (sesiones + cache distribuido) |
| **Almacenamiento** | Nextcloud (WebDAV + Activity Log) |
| **Autenticación** | Keycloak (OpenID Connect + SAML) |
| **Framework** | Spring Boot 3.1.5 (Java 17) |
| **Contenedor** | Docker (1 backend + servicios) |
| **Orquestación** | Docker Compose (local) / Docker Swarm o K8s (producción) |
| **Monitoreo** | Prometheus + Grafana + ELK Stack |
| **CI/CD** | GitHub Actions + SSH deploy |
| **Escalabilidad** | 200-500 usuarios sin cambios arquitectónicos |

---

## Referencias

- **Arquitectura Recomendada:** `alternativas_solucion/arquitectura.md`
- **Arquitectura Hexagonal:** Alistair Cockburn (https://alistair.cockburn.us/)
- **Spring Boot & RabbitMQ:** Spring Official Documentation
- **Event-Driven DDD:** Chris Richardson, "Microservices Patterns"
