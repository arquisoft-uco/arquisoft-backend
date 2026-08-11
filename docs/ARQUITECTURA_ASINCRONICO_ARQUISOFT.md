> [!WARNING]
> **SOLO LECTURA — NO USAR COMO CONTEXTO DE AGENTES O IA**
>
> Este archivo es documentación de referencia para desarrolladores humanos.
> **No debe ser leído ni indexado por agentes, asistentes de IA ni herramientas de generación de código.**
> El contexto autoritativo del proyecto para agentes reside exclusivamente en `AGENTS.md` (raíz del repositorio)
> y en los skills de `.opencode/skills/`. Usar este archivo como contexto puede producir código incorrecto,
> versiones desactualizadas o convenciones que no reflejan el estado real del proyecto.

# Arquitectura Hexagonal Asincrónica para Arquisoft

## Índice

1. [Visión General](#visión-general)
2. [Los 7 Contextos de Arquisoft](#los-7-contextos-de-arquisoft)
3. [Desacoplamiento Asincrónico via Message Queue](#desacoplamiento-asincrónico-via-message-queue)
4. [Estructura Modular Multi-Contexto](#estructura-modular-multi-contexto)
5. [Stack Tecnológico Completo](#stack-tecnológico-completo)
6. [Ejemplo: Flujo de Eventos End-to-End](#ejemplo-flujo-de-eventos-end-to-end)
7. [Configuración RabbitMQ por Contexto](#configuración-rabbitmq-por-contexto)
8. [Monitoreo y Trazabilidad](#monitoreo-y-trazabilidad)
9. [Despliegue](#despliegue)

---

## Visión General

Arquisoft implementa una **Arquitectura Hexagonal Modular con Desacoplamiento Asincrónico**:

- **7 Contextos Independientes** en un único backend monolítico
- **Comunicación Asincrónica** via RabbitMQ para desacoplamiento entre contextos
- **Baja Latencia** en respuestas al usuario (procesar en background)
- **Logs Centralizados** para auditoría y trazabilidad
- **Stack Completo** (Keycloak, PostgreSQL, RabbitMQ, Redis, Nextcloud)

### Arquitectura: Hexagonal + Async

```
ARQUITECTURA ARQUISOFT (HEXAGONAL + ASYNC):
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│ Controller   │       │ EventListener│       │ ScheduledTask│
│   (IN)       │       │   (IN)       │       │   (IN)       │
└──────┬───────┘       └──────┬───────┘       └──────┬───────┘
       │ (SÍNCRONO)            │ (ASYNC)            │ (ASYNC)
       ▼                       ▼                     ▼
┌─────────────────────────────────────────────────────────┐
│                     RabbitMQ                            │
│            (Topic Exchange: arquisoft.events)           │
└──────────────────────┬────────────────────────────────┘
                       │
       ┌───────────────┼───────────────┐
       │               │               │
       ▼               ▼               ▼
   CONTEXT A      CONTEXT B       CONTEXT C
   (Fichas)      (Proyectos)    (Evaluaciones)
   
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

## Los 7 Contextos de Arquisoft

Cada contexto es un **módulo hexagonal independiente** con su propio:
- Domain (modelos + puertos)
- Application (casos de uso + DTOs)
- Infrastructure (controladores + repositorios + event listeners)

### Contextos

| # | Contexto | Responsabilidad | Entidades Principales | Eventos que Emite |
|---|----------|----------------|----------------------|-------------------|
| 1 | **Seguridad** | Autenticación, autorización, roles, rate limiting | UserRole, Token, Session | `seguridad.usuario.creado` |
| 2 | **Fichas** | Fichas de caracterización de trabajos de grado | Ficha, TemaProyecto, AreaConocimiento | `FichaCreada`, `FichaAprobada`, `FichaRechazada` |
| 3 | **Proyectos** | Creación y gestión de proyectos de grado | Proyecto, EstadoProyecto, Línea | `ProyectoCreado`, `ProyectoAsignado`, `ProyectoFinalizado` |
| 4 | **Artefactos** | Gestión de documentos y artefactos | Artefacto, VersionArtefacto, Observacion | `ArtefactoCreado`, `ArtefactoCatalogado`, `ArtefactoEvaluado` |
| 5 | **Repositorio Artefactos** | Control de versiones y almacenamiento | RepositorioArtefacto, Version | `VersionPublicada`, `VersionArchivada` |
| 6 | **Entregables** | Gestión de entregables e hitos | EntregableProyectoGrado, Hito | `EntregableCreado`, `EntregableSubido`, `EntregableEvaluado` |
| 7 | **Evaluaciones** | Evaluaciones finales y calificaciones | EvaluacionFinal, Criterio, Calificacion | `EvaluacionCreada`, `EvaluacionCalificada`, `EvaluacionFinalizada` |

---

## Desacoplamiento Asincrónico via Message Queue

### Arquitectura de Eventos

```
┌─────────────────────────────────────────────────────────────────┐
│                    CONTEXTO FICHAS                               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ Controller: POST /api/fichas → Crear Ficha               │   │
│  │ • Responde inmediatamente al cliente (~100ms)            │   │
│  │ • Retorna: {"id": 1, "titulo": "Mi Ficha"}              │   │
│  └──────────────────┬───────────────────────────────────────┘   │
│                     │                                             │
│  ┌──────────────────▼───────────────────────────────────────┐   │
│  │ UseCase: CrearFichaUseCase                               │   │
│  │ • Validar datos                                          │   │
│  │ • Guardar en BD                                          │   │
│  │ • Emitir evento: FichaCreada { id, titulo, area }       │   │
│  └──────────────────┬───────────────────────────────────────┘   │
│                     │                                             │
│  ┌──────────────────▼───────────────────────────────────────┐   │
│  │ EventPublisher: Publish a RabbitMQ                       │   │
│  │ • Routing Key: fichas.creada                             │   │
│  │ • Exchange: arquisoft.events                             │   │
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
        ┌─────────────┴───────────────┐
        │                             │
        ▼                             ▼
   Queue:                        Queue:
   proyectos.events          artefactos.events
        │                         │
        ▼                         ▼
   ┌──────────────┐        ┌──────────────┐
   │ CONTEXTO     │        │ CONTEXTO     │
   │ PROYECTOS    │        │ ARTEFACTOS   │
   │ EventListener│        │ EventListener│
   └──────────────┘        └──────────────┘
```

### Beneficios

- **Latencia Baja**: Respuesta inmediata al usuario (~100ms)
- **Desacoplamiento**: Contextos independientes en tiempo de ejecución
- **Escalabilidad**: Más eventos sin degradar respuestas
- **Confiabilidad**: Si falla un consumer, el evento sigue en la queue
- **Auditoría**: Todos los eventos quedan registrados

---

## Estructura Modular Multi-Contexto

### Estructura Base

```
arquisoft-backend/
│
├── build.gradle
├── settings.gradle
├── gradle.properties
├── docker-compose.yml
├── init-db.sql
│
├── shared/                           # Utilidades compartidas (7 sub-módulos)
│   ├── domain/                       # DomainEvent, AggregateRoot
│   ├── exceptions/                   # DomainException
│   ├── amqp/                         # EventPublisher interface
│   ├── postgres/                     # BaseRepository
│   ├── redis/                        # RedisClient
│   ├── web/                          # HttpClient
│   └── validation/                   # @ValidEmail
│
├── seguridad/                        # CONTEXTO 1: Auth/Seguridad
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── fichas/                           # CONTEXTO 2
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── proyectos/                        # CONTEXTO 3
├── artefactos/                       # CONTEXTO 4
├── repositorio_artefactos/           # CONTEXTO 5
├── entregables/                      # CONTEXTO 6
└── evaluaciones/                     # CONTEXTO 7
```

### Dependencias Gradle (por contexto)

```gradle
// {contexto}/domain/build.gradle
dependencies {
    implementation project(':shared:domain')
}

// {contexto}/application/build.gradle
dependencies {
    implementation project(':{contexto}:domain')
}

// {contexto}/infrastructure/build.gradle
dependencies {
    implementation project(':{contexto}:domain')
    implementation project(':{contexto}:application')
    implementation project(':shared:amqp')       // Para publicar eventos
    implementation project(':shared:postgres')   // Para repositorios JPA
    implementation "org.springframework.boot:spring-boot-starter-web"
    implementation "org.springframework.boot:spring-boot-starter-data-jpa"
    implementation "org.springframework.amqp:spring-rabbit"
    runtimeOnly 'org.postgresql:postgresql'
}
```

---

## Stack Tecnológico Completo

### Backend

```
Framework: Spring Boot 4.0.5
├─ Spring Web (REST APIs)
├─ Spring Data JPA (ORM)
├─ Spring AMQP (RabbitMQ)
├─ Spring Data Redis (Cache)
├─ Spring Security + OAuth2 (Keycloak)
├─ Lombok (reduce boilerplate)
└─ JUnit 6.0.3 + Mockito + AssertJ (testing)
```

### Infraestructura

```
Message Queue: RabbitMQ 4.2.5
├─ Exchange: Topic (arquisoft.events)
├─ Queues: Una por contexto
├─ DLQ: Dead Letter Queue para errores
└─ Persistence: Durable queues

Database: PostgreSQL 18
├─ 7 Schemas (1 por contexto)
├─ Migrations: Flyway 11.20.3
├─ Connection Pool: HikariCP
└─ Testing: H2

Cache: Redis 7+
├─ Session store
├─ Cache distribuido
└─ Rate limiting data

Auth: Keycloak 26.6
├─ OpenID Connect
├─ OAuth2 / JWT
└─ 8 roles predefinidos

Storage: Nextcloud 27+
├─ WebDAV API
├─ Versioning
└─ Activity Log
```

---

## Ejemplo: Flujo de Eventos End-to-End

### Caso de Uso: Estudiante Sube Entregable

#### Paso 1: Request Síncrono

```http
POST /api/entregables HTTP/1.1
Authorization: Bearer <token>
Content-Type: multipart/form-data

proyectoId=123
descripcion=Entrega Fase 1
archivo=documento.pdf
```

#### Paso 2: Controller responde inmediatamente

```java
// entregables/infrastructure/entregable/command/primaryadapter/web/EntregablesController.java
@RestController
@RequestMapping("/api/entregables")
@RequiredArgsConstructor
public class EntregablesController {
    private final CrearEntregableUseCase crearEntregableUseCase;

    @PostMapping
    public ResponseEntity<EntregableResponse> crearEntregable(
            @Valid @RequestBody CrearEntregableRequest request) {
        
        Entregable entregable = crearEntregableUseCase.ejecutar(
            new CrearEntregableCommand(
                request.getProyectoId(),
                request.getDescripcion(),
                request.getArchivo()
            )
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(EntregableResponse.from(entregable));
    }
}
```

#### Paso 3: Domain emite evento

```java
// entregables/domain/model/Entregable.java
public class Entregable extends AggregateRoot {
    private Long id;
    private Long proyectoId;
    private String descripcion;
    private String archivoUrl;
    private LocalDateTime fechaCreacion;

    public static Entregable crear(Long proyectoId, String descripcion, String archivoUrl) {
        Entregable e = new Entregable();
        e.proyectoId = proyectoId;
        e.descripcion = descripcion;
        e.archivoUrl = archivoUrl;
        e.fechaCreacion = LocalDateTime.now();
        
        e.publicarEvento(new EntregableSubidoEvent(
            e.id, proyectoId, descripcion, archivoUrl, LocalDateTime.now()
        ));
        return e;
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
    private final EventPublisher eventPublisher;

    @Override
    public Entregable ejecutar(CrearEntregableCommand command) {
        Entregable entregable = Entregable.crear(
            command.getProyectoId(),
            command.getDescripcion(),
            command.getArchivoUrl()
        );

        Entregable saved = entregableRepository.save(entregable);
        saved.extraerEventosSinPublicar().forEach(eventPublisher::publish);
        return saved;
    }
}
```

#### Paso 5: Otros contextos escuchan (Background)

```java
// evaluaciones/infrastructure/evaluacion/command/primaryadapter/amqp/EvaluacionesEventListener.java
@Component
@RequiredArgsConstructor
public class EvaluacionesEventListener {
    private final CrearEvaluacionUseCase crearEvaluacionUseCase;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "evaluaciones.queue", durable = true),
        exchange = @Exchange(name = "arquisoft.events", type = ExchangeTypes.TOPIC),
        key = "entregables.subido"
    ))
    public void onEntregableSubido(String message) {
        EntregableSubidoEvent event = objectMapper.readValue(message, EntregableSubidoEvent.class);
        crearEvaluacionUseCase.ejecutar(
            new CrearEvaluacionCommand(event.entregableId, event.proyectoId)
        );
    }
}
```

#### Timeline

```
T=0ms     → Cliente: POST /api/entregables
T=10ms    → Controller recibe request
T=30ms    → BD: INSERT INTO entregables
T=50ms    → RabbitMQ: Evento publicado
T=100ms   → Cliente RECIBE: 201 Created ✅

[BACKGROUND]
T=110ms   → EVALUACIONES: Crea evaluación pendiente
T=150ms   → REPOSITORIO_ARTEFACTOS: Crea versión inicial
T=200ms   → Todos los contextos han procesado ✅
```

---

## Configuración RabbitMQ por Contexto

### Exchange Principal

```java
// shared/amqp — EventPublisher interface
public interface EventPublisher {
    void publish(DomainEvent event);
}
```

```properties
Exchange Name: arquisoft.events
Type: Topic
Durable: true
```

### Ejemplo: Config de Evaluaciones

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

    @Bean
    public Binding bindEntregableSubido(Queue evaluacionesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(evaluacionesQueue)
            .to(exchange).with("entregables.subido");
    }

    @Bean
    public Binding bindProyectoFinalizado(Queue evaluacionesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(evaluacionesQueue)
            .to(exchange).with("proyectos.finalizado");
    }
}
```

### Tabla de Routing Keys

| Evento | Routing Key | Publisher | Subscribers |
|--------|-------------|----------|-------------|
| Ficha creada | `fichas.creada` | FICHAS | PROYECTOS |
| Ficha aprobada | `fichas.aprobada` | FICHAS | PROYECTOS |
| Proyecto creado | `proyectos.creado` | PROYECTOS | ARTEFACTOS, ENTREGABLES, EVALUACIONES |
| Proyecto asignado | `proyectos.asignado` | PROYECTOS | ENTREGABLES, EVALUACIONES |
| Proyecto finalizado | `proyectos.finalizado` | PROYECTOS | EVALUACIONES |
| Artefacto creado | `artefactos.creado` | ARTEFACTOS | REPOSITORIO_ARTEFACTOS |
| Artefacto evaluado | `artefactos.evaluado` | ARTEFACTOS | ENTREGABLES |
| Version publicada | `repositorio.version-publicada` | REPOSITORIO_ARTEFACTOS | ARTEFACTOS |
| Entregable creado | `entregables.creado` | ENTREGABLES | EVALUACIONES, ARTEFACTOS |
| Entregable subido | `entregables.subido` | ENTREGABLES | EVALUACIONES, REPOSITORIO_ARTEFACTOS |
| Entregable evaluado | `entregables.evaluado` | ENTREGABLES | EVALUACIONES |
| Evaluación creada | `evaluaciones.creada` | EVALUACIONES | ENTREGABLES |
| Evaluación calificada | `evaluaciones.calificada` | EVALUACIONES | PROYECTOS |
| Evaluación finalizada | `evaluaciones.finalizada` | EVALUACIONES | PROYECTOS |

---

## Monitoreo y Trazabilidad

### Spring Boot Actuator

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
```

### Endpoints de Monitoreo

```
GET /api/actuator/health         → Estado de salud
GET /api/actuator/metrics        → Métricas del sistema
GET /api/actuator/prometheus     → Formato Prometheus
```

### AuditFilter (incluido en seguridad/)

El filtro `AuditFilter` registra todas las peticiones HTTP:
```
[AUDIT] METHOD=POST URI=/api/fichas USER=juan@uco.edu.co TIME=45ms STATUS=201
```

---

## Despliegue

### Docker Compose (Desarrollo)

```yaml
services:
  arquisoft-backend:
    build: .
    ports: ["8080:8080"]
    environment:
      SPRING_PROFILES_ACTIVE: dev
    depends_on: [postgres, rabbitmq, redis, keycloak]

  postgres:
    image: postgres:18-alpine
    environment:
      POSTGRES_DB: arquisoft
      POSTGRES_USER: arquisoft
      POSTGRES_PASSWORD: arquisoft123
    volumes:
      - ./init-db.sql:/docker-entrypoint-initdb.d/init.sql
    ports: ["5432:5432"]

  rabbitmq:
    image: rabbitmq:4.2.5-management-alpine
    ports: ["5672:5672", "15672:15672"]

  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]

  keycloak:
    image: quay.io/keycloak/keycloak:26.6
    command: start-dev
    ports: ["8081:8080"]
    depends_on: [postgres]

  nextcloud:
    image: nextcloud:28-apache
    ports: ["8082:80"]
```

### Producción

```bash
# Build
docker build -t arquisoft-backend:latest .

# Run con perfil prod
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://db:5432/arquisoft \
  -e DATABASE_USERNAME=arquisoft \
  -e DATABASE_PASSWORD=secret \
  -e RABBITMQ_HOST=mq \
  -e KEYCLOAK_ISSUER_URI=https://auth.uco.edu.co/realms/arquisoft \
  arquisoft-backend:latest
```

---

## Resumen Arquitectónico

| Aspecto | Especificación |
|---------|----------------|
| **Patrón** | Hexagonal Modular + Async Event-Driven |
| **Contextos** | 7 independientes (Seguridad, Fichas, Proyectos, Artefactos, Repositorio Artefactos, Entregables, Evaluaciones) |
| **Comunicación** | RabbitMQ Topic Exchange + Durable Queues |
| **Latencia** | ~100ms (respuesta síncrona al usuario) |
| **Base de Datos** | PostgreSQL 18 (1 schema por contexto) |
| **Cache** | Redis 7 (sesiones + cache distribuido) |
| **Almacenamiento** | Nextcloud (WebDAV) |
| **Autenticación** | Keycloak 26.6 (OAuth2/JWT) |
| **Rate Limiting** | Bucket4j 7.6.0 |
| **Framework** | Spring Boot 4.0.5 (Java 21) |
| **Build** | Gradle 9.0.0 |
| **Testing** | JUnit 6.0.3 + Mockito + AssertJ |

---

## Outbox Pattern — Atomicidad entre BD y Broker

Sin el Outbox Pattern, `save(aggregate)` y `eventPublisher.publish(event)` son dos operaciones independientes. Si el broker cae después del `save`, el evento se pierde y el sistema queda inconsistente.

### Solución: Spring Modulith 2.0.0 Event Publication Registry

```
[UseCase] @Transactional
    │
    ├── BEGIN TX
    │     ├── INSERT aggregate en BD del contexto
    │     └── INSERT event_publication en arquisoft_events  ← misma TX
    └── COMMIT
            │
    [Spring Modulith — post-commit]
            ├── Publica a RabbitMQ (routing key = temaEvento)
            │     ├── OK   → borra fila de event_publication
            │     └── FAIL → status = FAILED, fila permanece
            │
    [FailedEventRetryConfig — cada 5 min]
            └── Reintenta todos los eventos con status FAILED
```

### BD centralizada `arquisoft_events`

Todos los contextos comparten la tabla `event_publication` en la base de datos `arquisoft_events`. Cada evento persistido incluye el JSON completo del objeto, lo que permite republicarlo sin intervención manual.

### Configuración de retries

| Mecanismo | Cuándo actúa | Qué hace |
|---|---|---|
| `republish-outstanding-events-on-restart` | Al arrancar la app | Republica TODO lo que no tenga `completion_date` |
| Staleness checker (`processing: 2m`) | Cada 1 minuto | Marca como `FAILED` eventos atascados en `PROCESSING` |
| Staleness checker (`resubmission: 10m`) | Cada 1 minuto | Marca como `FAILED` eventos atascados en `RESUBMITTED` |
| `FailedEventRetryConfig` | Cada 5 minutos | Reintenta eventos con status `FAILED` |

> El staleness checker **no reintenta** — solo marca. El reintento real lo hace `FailedEventRetryConfig`.

---

## Referencias

- **Arquitectura y Estructura**: `ARQUITECTURA_Y_ESTRUCTURA.md`
- **Deuda Técnica Outbox (resuelta)**: `DEUDA_TECNICA_OUTBOX_PATTERN.md`
- **Arquitectura Hexagonal**: Alistair Cockburn
- **Event-Driven DDD**: Chris Richardson, "Microservices Patterns"
- **Spring Modulith**: https://docs.spring.io/spring-modulith/docs/current/reference/html/#events

---

**Versión**: 2.0.0
