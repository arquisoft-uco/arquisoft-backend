---
name: arquisoft-context
description:
  Contexto autoritativo del proyecto Arquisoft Backend para subagentes. Carga SIEMPRE antes de planificar, implementar, testear o validar cualquier Historia de Usuario o Técnica. Contiene el stack exacto verificado, las convenciones de arquitectura hexagonal + DDD, la regla estricta de AggregateRoot, el mapeo contexto → base de datos PostgreSQL, la guía de uso de features de Java 21 y las plantillas de código canónicas. Este skill es la ÚNICA fuente de verdad para el estado del proyecto — no leer AGENTS.md, README.md, QUICK_START.md, ARQUITECTURA_*.md ni docs/ del repositorio.
---

# Skill: arquisoft-context — Contexto Autoritativo para Subagentes

## Propósito

Este skill es el **único documento** que los subagentes deben consultar para entender el estado del proyecto Arquisoft Backend. Reemplaza cualquier lectura de:

- `AGENTS.md` (raíz del repositorio) — es contexto del agente primario, no de subagentes.
- `README.md`, `QUICK_START.md`, `ARQUITECTURA_Y_ESTRUCTURA.md`, `ARQUITECTURA_ASINCRONICO_ARQUISOFT.md` — documentación para humanos, puede estar desactualizada.
- Cualquier archivo del directorio `docs/` — documentación para humanos.

**Regla dura:** si hay contradicción entre este skill y cualquier otro documento del repositorio, **gana este skill**.

---

## Protocolo de Carga

Los subagentes invocan este skill **al inicio de su flujo**, antes de hacer preguntas o generar código:

```
skill("arquisoft-context")
```

Tras cargarlo, el subagente tiene disponible en su contexto:
- El stack verificado (sección "Stack Verificado")
- La estructura hexagonal + DDD (sección "Arquitectura Hexagonal + DDD")
- La regla estricta de AggregateRoot (sección "AggregateRoot — Regla Estricta")
- Mapeo contexto → base de datos PostgreSQL (sección "Mapeo Contexto Gradle → Base de Datos PostgreSQL")
- Guía de features de Java 21 (sección "Java 21 — Uso Balanceado")
- Plantillas canónicas (sección "Plantillas de Código")
- Convenciones de nomenclatura (sección "Nomenclatura")
- Configuraciones clave del proyecto (Virtual Threads, Swagger, Keycloak)

---

## Stack Verificado

Valores tomados de `gradle.properties` y `build.gradle` reales. **No inventar versiones.**

| Componente | Versión | Notas |
|---|---|---|
| Java | 21 | Virtual Threads habilitados globalmente |
| Spring Boot | 4.0.5 | `@MockitoBean` reemplaza a `@MockBean`; `RestTemplateBuilder` eliminado |
| Gradle | 9.0.0 | Multi-módulo, 28 subproyectos |
| PostgreSQL | 18 | Driver gestionado por Spring Boot BOM |
| RabbitMQ | 4.2.5 | Topic Exchange `arquisoft.events` |
| Redis | 7 | Caché y sesiones distribuidas |
| Keycloak | 26.6 (server) / 25.0.3 (adapter) | OAuth2/OIDC Resource Server |
| Flyway | 11.20.3 | Migraciones SQL versionadas |
| JUnit | 6.0.3 | Jupiter API; IDs de Context7 de JUnit 5 son válidos |
| Mockito + AssertJ | últimas | Patrón AAA obligatorio |
| Lombok | 1.18.30 | Prohibido en capa domain |
| springdoc-openapi | 2.8.8 | ADR-011 — OpenAPI en todos los controllers |
| Bucket4j | 7.6.0 | Rate limiting per-IP |

### Consecuencias directas del stack

1. **Spring Boot 4.x** → usar `@MockitoBean` en tests, nunca `@MockBean`.
2. **Spring Boot 4.x** → `RestTemplateBuilder` eliminado; si se necesita, usar `SimpleClientHttpRequestFactory` directamente.
3. **Java 21** → Virtual Threads activos; **prohibido** declarar `@Bean TaskExecutor` o thread pools manuales salvo instrucción explícita del plan.
4. **JUnit 6** → anotaciones Jupiter (`@Test`, `@ExtendWith`, `@BeforeEach`) compatibles con docs JUnit 5 de Context7.

---

## Arquitectura Hexagonal + DDD

### Dirección de dependencias (no negociable)

```
Domain  ←  Application  ←  Infrastructure
```

- `domain`: Java puro. **CERO** imports de Spring, JPA, Lombok, Jackson o cualquier framework.
- `application`: solo importa `domain` + librerías permitidas (Lombok, Jakarta Validation).
- `infrastructure`: depende de ambas + frameworks completos.
- **Los bounded contexts NUNCA dependen entre sí.** Comunicación exclusivamente vía RabbitMQ.

### Bounded Contexts

| Contexto Gradle | GroupId base | Estado actual | ¿Usa AggregateRoot? |
|---|---|---|---|
| `seguridad` | `com.arquisoft.seguridad` | **Solo configuración** (login/refresh con Keycloak, rate-limit, JWT). No tiene HUs de negocio | ❌ No |
| `fichas` | `com.arquisoft.fichas` | Activo | ✅ Sí |
| `proyectos` | `com.arquisoft.proyectos` | Pendiente | ✅ Sí |
| `artefactos` | `com.arquisoft.artefactos` | Pendiente | ✅ Sí |
| `repositorio_artefactos` | `com.arquisoft.repositorio_artefactos` | Pendiente | ✅ Sí |
| `entregables` | `com.arquisoft.entregables` | Pendiente | ✅ Sí |
| `evaluaciones` | `com.arquisoft.evaluaciones` | Pendiente | ✅ Sí |

> **El contexto `usuarios` NO se implementa en esta versión.** Los usuarios y roles viven en Keycloak — los demás contextos consumen el JWT directamente.
>
> **`seguridad` no implementa HUs de negocio.** Solo expone `/auth/login`, `/auth/refresh`, `/auth/validate` y la configuración global (rate-limit, JWT decoder). No tiene aggregates, eventos ni casos de uso CQRS.
>
> En documentación y ejemplos del SKILL/agentes, los aggregates publicadores de eventos se ilustran con `fichas` (ej. `FichaPerfilAggregate.crear()` publica `FichaPerfilCreadaEvent`), **no** con `seguridad/usuarios`.

### Módulos shared

| Módulo | Paquete base | Clases/Interfaces clave |
|---|---|---|
| `shared:domain` | `com.arquisoft.shared.domain` | `AggregateRoot`, `DomainEvent`, `InputPort<I, O>`, `VoidInputPort<I>`, `PaginatedResult<T>`, jerarquía base de excepciones |
| `shared:amqp` | `com.arquisoft.shared.amqp` | `EventPublisher` (interfaz), `RabbitMQEventPublisher`, `AbstractEventConsumer`, `RabbitMQConfig` |
| `shared:web` | `com.arquisoft.shared.web` | `GlobalAppExceptionHandler`, `ErrorResponseDTO`, `PageResponseDTO<T>`, `QueryCriteriaRequestDTO`, `NodoFiltroDTO` (Jackson polimórfico) |
| `shared:postgres` | `com.arquisoft.shared.postgres` | `QueryJpaSpecification<JpaEntity>`, `CampoSpec` (sealed: `Texto`, `Uuid`, `Entero`, `Decimal`, `Fecha`, `FechaHora`, `Booleano`). Utilería para traducir `Criteria` → `Specification` |
| `shared:minio` | `com.arquisoft.shared.minio` | `MinioStorageClient` (presigned URLs PUT/GET, `objectExists`, `deleteObject`), `MinioConfig`, `MinioProperties` |
| `shared:redis` | `com.arquisoft.shared.redis` | Esqueleto sin implementación |

### Estructura estándar por contexto — CQRS + Vertical Slice

> **Principio organizador:** primero por **entidad/agregado**, luego dentro de cada entidad se separa `command/` (write) de `query/` (read). NO hay carpetas planas `usecase/`, `dto/`, `entity/`.

```
{contexto}/
├── domain/
│   └── {entidad}/
│       ├── aggregate/         # XxxAggregate (extiende AggregateRoot)
│       ├── port/out/          # XxxOutputPort (interfaces de persistencia del write side)
│       ├── event/             # Eventos de dominio (extiende DomainEvent)
│       ├── exception/         # Excepciones del agregado
│       └── message/           # Constantes de mensajes de dominio
│
├── application/
│   └── {entidad}/
│       ├── command/
│       │   ├── model/         # XxxCommand (record con la intención de negocio)
│       │   ├── port/in/       # XxxInputPort (extends InputPort<Command, Result> o VoidInputPort)
│       │   └── XxxUseCase.java   # Implementación (@Component)
│       └── query/
│           ├── criteria/      # XxxCriteria (extiende QueryCriteria, opcional)
│           ├── readmodel/     # XxxReadModel (proyección plana de solo lectura)
│           ├── port/in/       # XxxQueryInputPort
│           ├── port/out/      # XxxQueryOutputPort (lectura — vive en application, no en domain)
│           └── XxxQueryUseCase.java
│
└── infrastructure/
    └── {entidad}/
        ├── command/
        │   └── adapter/
        │       ├── in/
        │       │   ├── web/   # XxxInputAdapter (@RestController) + dto/XxxRequestDTO
        │       │   └── amqp/  # XxxConsumerInputAdapter (extiende AbstractEventConsumer, si consume eventos)
        │       └── out/
        │           └── persistence/   # XxxCommandOutputAdapter (implementa XxxOutputPort)
        ├── query/
        │   └── adapter/
        │       ├── in/web/    # XxxQueryInputAdapter (@RestController)
        │       └── out/persistence/   # XxxQueryOutputAdapter + XxxJpaSpecification (si usa Criteria)
        ├── persistence/       # XxxJpaEntity, XxxJpaRepository, XxxMapper (compartido entre command y query)
        ├── config/            # @Configuration (cablea, sin lógica)
        └── exception/         # Excepciones de infraestructura
```

### Reglas de la estructura

1. **`port/in/` vive en `application`, NO en domain.** El dominio solo expone `port/out/` (write side). Las interfaces de entrada describen casos de uso, no son contratos puros del dominio.
2. **`port/out/` se reparte entre dos sitios:** el **write side** (`XxxOutputPort`) en `domain/{entidad}/port/out/`. El **read side** (`XxxQueryOutputPort`) en `application/{entidad}/query/port/out/`. El read side no toca el agregado.
3. **JPA Entities, repositorios y mappers viven en `infrastructure/{entidad}/persistence/`** (compartido entre command y query). Los adapters específicos van en `command/adapter/out/persistence/` y `query/adapter/out/persistence/`.
4. **Vertical Slice por agregado:** todo lo de `FichaPerfil` vive bajo `domain/fichaPerfil/`, `application/fichaPerfil/`, `infrastructure/fichaPerfil/`. NO hay carpeta genérica `domain/model/` con todos los agregados.
5. **Subcarpetas obligatorias siempre**, aunque solo haya un tipo: `adapter/in/web/`, `adapter/out/persistence/`, etc. Nunca componentes directamente en `adapter/in/` o `adapter/out/`.

### Convención de sufijos de clases

| Tipo | Sufijo | Anotación |
|---|---|---|
| Adaptador REST de entrada | `XxxInputAdapter` | `@RestController` |
| Adaptador REST de consulta | `XxxQueryInputAdapter` | `@RestController` |
| Adaptador AMQP consumidor | `XxxConsumerInputAdapter` | `@Component` + `@RabbitListener` (extiende `AbstractEventConsumer`) |
| Adaptador persistencia write | `XxxCommandOutputAdapter` | `@Component` |
| Adaptador persistencia read | `XxxQueryOutputAdapter` | `@Component` |
| Caso de uso write | `XxxUseCase` | `@Component` |
| Caso de uso read | `XxxQueryUseCase` | `@Component` |
| Puerto de entrada write | `XxxInputPort` (interfaz, vacía) | — |
| Puerto de entrada read | `XxxQueryInputPort` (interfaz, vacía) | — |
| Puerto de salida write | `XxxOutputPort` (interfaz, en domain) | — |
| Puerto de salida read | `XxxQueryOutputPort` (interfaz, en application) | — |
| Comando | `XxxCommand` (`record`) | — |
| Read model | `XxxReadModel` (`record`) | — |
| Criteria | `XxxCriteria` (extiende `QueryCriteria`) | — |
| Configuración | `XxxConfig` | `@Configuration` |
| Filtro HTTP | `XxxFilter` | `@Component` + implementa `Filter` |

> **Ya NO se usan los sufijos `Controller`, `Listener`, `RepositoryAdapter`, `UseCaseImpl`.** Reemplazados por `InputAdapter`, `ConsumerInputAdapter`, `CommandOutputAdapter`/`QueryOutputAdapter`, `UseCase`.
| `OpenApiConfig`, `SecurityConfig`, `RabbitMQConfig` | `infrastructure/config/` | `@Configuration` |

**Regla:** un componente está en `adapter/in/web/` si **solo se activa durante una request REST**. Si es transversal a más cosas (filtros que también aplican a actuator, configs globales), va en `filter/` o `config/`.

---

## DDD Estricto — Separación de Responsabilidades por Capas

Esta sección define **qué puede y qué no puede vivir en cada capa** del proyecto. Es la regla
fundamental que subyace a todo lo demás (incluido AggregateRoot). Antes de escribir cualquier
archivo, verifica que la lógica esté en la capa correcta.

### Principio rector

> Cada capa conoce solo lo que le corresponde. Si un cambio de tecnología externa
> (Keycloak → Auth0, RabbitMQ → Kafka, PostgreSQL → MongoDB) obliga a tocar el dominio,
> hay lógica filtrada que debe moverse.

### Prueba del algodón (obligatoria antes de escribir cada archivo)

Hazte estas tres preguntas antes de poner lógica en cualquier capa:

1. **¿Es una regla que el negocio entendería y defendería?**
   (ej. "una ficha nace en estado BORRADOR", "el rol ASESOR_FICHA puede aprobar")
   → Va en `domain/`.

2. **¿Es orquestación de pasos ya definidos en el dominio?**
   (ej. "guardar → publicar eventos → limpiar eventos")
   → Va en `application/`.

3. **¿Es un detalle de cómo hablar con una tecnología externa concreta?**
   (ej. "el claim de Keycloak se llama `realm_access.roles`", "RabbitTemplate necesita `convertAndSend`")
   → Va en `infrastructure/`.

Si una misma clase responde "sí" a dos preguntas, está mezclando responsabilidades y debe dividirse.

### Responsabilidades por capa (tabla definitiva)

| Tipo de conocimiento | Capa correcta | Ejemplos en Arquisoft |
|---|---|---|
| Reglas de negocio del agregado | `domain/{entidad}/aggregate/` | `FichaPerfilAggregate` con `crear()`, `aprobar()`, invariantes |
| Definición de Value Objects | `domain/{entidad}/aggregate/` | `Calificacion`, `Email` (records con validación) |
| Eventos de dominio | `domain/{entidad}/event/` | `FichaPerfilCreadaEvent extends DomainEvent` |
| Puertos de salida write | `domain/{entidad}/port/out/` | `FichaPerfilOutputPort` (persistencia del aggregate) |
| Comandos (intención de negocio) | `application/{entidad}/command/model/` | `CrearFichaPerfilCommand` (record) |
| Puertos de entrada write | `application/{entidad}/command/port/in/` | `CrearFichaPerfilInputPort extends InputPort<Command, UUID>` |
| Casos de uso write (orquestación) | `application/{entidad}/command/` | `CrearFichaPerfilUseCase` (`@Component`) |
| Criteria de consulta (opcional) | `application/{entidad}/query/criteria/` | `FichaPerfilCriteria extends QueryCriteria` |
| Read models (proyección plana) | `application/{entidad}/query/readmodel/` | `FichaPerfilReadModel` (record) |
| Puertos de entrada read | `application/{entidad}/query/port/in/` | `ConsultarFichasPerfilInputPort` |
| Puertos de salida read | `application/{entidad}/query/port/out/` | `FichaPerfilQueryOutputPort` (vive en application, no en domain) |
| Casos de uso read | `application/{entidad}/query/` | `ConsultarFichasPerfilQueryUseCase` |
| Request DTOs HTTP | `infrastructure/{entidad}/command/adapter/in/web/dto/` | `CrearFichaPerfilRequestDTO` |
| Adaptadores REST write | `infrastructure/{entidad}/command/adapter/in/web/` | `CrearFichaPerfilInputAdapter` (`@RestController`) |
| Adaptadores REST read | `infrastructure/{entidad}/query/adapter/in/web/` | `ConsultarFichasPerfilQueryInputAdapter` (`@RestController`) |
| Consumidores AMQP | `infrastructure/{entidad}/command/adapter/in/amqp/` | `XxxConsumerInputAdapter` (extiende `AbstractEventConsumer`) |
| Adaptadores persistencia write | `infrastructure/{entidad}/command/adapter/out/persistence/` | `FichaPerfilCommandOutputAdapter` (implementa `FichaPerfilOutputPort`) |
| Adaptadores persistencia read | `infrastructure/{entidad}/query/adapter/out/persistence/` | `FichaPerfilQueryOutputAdapter` + `FichaPerfilJpaSpecification` (si usa Criteria) |
| JPA Entity / Repository / Mapper | `infrastructure/{entidad}/persistence/` | `FichaPerfilJpaEntity`, `FichaPerfilJpaRepository`, `FichaPerfilMapper` |
| Configuración técnica | `infrastructure/config/` | `RabbitMQQueueConfig`, `DataSourceConfig` (sin lógica de negocio) |
| Filtros HTTP cross-cutting | `infrastructure/filter/` | `RateLimitFilter`, `RequestLoggingFilter` |

### Lista negra de imports por capa

#### En `domain/**/*.java` — **NUNCA** debe aparecer ninguno de estos:

```
org.springframework.*              ← ningún paquete de Spring
org.hibernate.*                    ← nada de Hibernate
jakarta.persistence.*              ← nada de JPA (@Entity, @Table, @Column)
org.springframework.amqp.*         ← nada de RabbitMQ
com.fasterxml.jackson.*            ← nada de Jackson
io.swagger.*                       ← nada de OpenAPI/Swagger
org.springframework.security.*     ← nada de Spring Security
org.keycloak.*                     ← nada de Keycloak
lombok.*                           ← Lombok PROHIBIDO en dominio
```

**Permitido en `domain/`:** solo `java.*`, `java.util.*`, `java.time.*`, `java.util.UUID`,
y clases del propio proyecto:
- `com.arquisoft.{contexto}.domain.*` (mismo contexto, capa propia)
- `com.arquisoft.shared.domain.*` (incluye subpaquetes `events/`, `exception/`, `pagination/`, `validation/`, `util/`)

> **NUNCA** en `domain/`: `com.arquisoft.shared.amqp.*`, `com.arquisoft.shared.web.*`, `com.arquisoft.shared.postgres.*`, `com.arquisoft.shared.minio.*`. Esos son adaptadores técnicos.

#### En `application/**/*.java` — permitido:

```
com.arquisoft.{contexto}.domain.*          ← ok (capa inferior)
com.arquisoft.shared.*                     ← ok (compartido)
jakarta.validation.*                       ← ok (validación declarativa de DTOs)
com.fasterxml.jackson.annotation.*         ← ok (serialización de DTOs)
lombok.*                                   ← ok (solo en DTOs)
org.springframework.stereotype.Component   ← ok (para @Component en UseCases)
org.springframework.stereotype.Service     ← ok
org.springframework.transaction.annotation.Transactional ← ok
```

**Prohibido en `application/`:** JPA (`@Entity`, `@Table`), controladores web
(`@RestController`, `@RequestMapping`), clientes concretos de mensajería (`RabbitTemplate`),
configuración de Spring (`@Configuration`, `@Bean`), seguridad concreta (Keycloak APIs).

#### En `infrastructure/**/*.java` — permitido todo, pero con una regla:

**Regla dura:** ninguna clase de `infrastructure/` puede contener **reglas de negocio**.
Si tienes una decisión que el negocio entendería, esa decisión vive en `domain/`.

---

### Patrón Puerto/Adaptador para Integraciones Externas

Toda integración con un sistema externo (RabbitMQ, MinIO, Redis, SMTP, HTTP externo) sigue el patrón Puerto/Adaptador. Una regla, dos ejemplos.

**Regla:** el `domain/` declara una **interfaz** (puerto) en `port/out/`. La implementación concreta vive en `infrastructure/.../adapter/out/{tipo}/`. La capa de aplicación llama al puerto sin conocer la tecnología.

**Ejemplo 1 — Persistencia del aggregate:**

```java
// domain/fichaPerfil/port/out/FichaPerfilOutputPort.java
public interface FichaPerfilOutputPort {
    void save(FichaPerfilAggregate aggregate);
    Optional<FichaPerfilAggregate> findById(UUID id);
}

// infrastructure/fichaPerfil/command/adapter/out/persistence/FichaPerfilCommandOutputAdapter.java
@Component
public class FichaPerfilCommandOutputAdapter implements FichaPerfilOutputPort {
    private final FichaPerfilJpaRepository jpaRepository;  // tecnología: JPA
    private final FichaPerfilMapper mapper;
    // ...
}
```

**Ejemplo 2 — Storage (MinIO):**

MinIO ya tiene su cliente en `shared:minio` (`MinioStorageClient`). Los casos de uso del contexto lo inyectan directamente — no se crea un puerto adicional. Detalles en la sección "Almacenamiento — MinIO con presigned URLs".

> **Excepción a la regla general:** los clientes técnicos puros que viven en `shared:*` (como `MinioStorageClient`, `EventPublisher`) son interfaces compartidas que cualquier contexto puede inyectar directamente. **No** requieren un puerto adicional por contexto. La regla del puerto aplica cuando el contexto define una abstracción propia (persistencia del aggregate, integración HTTP específica, etc.).

---

### Señales de alarma (si ves esto en código, hay que mover lógica)


| Señal | Qué significa | Dónde debe ir |
|---|---|---|
| `import org.springframework` en `domain/` | Framework filtrado al dominio | Mover a adaptador |
| `@Configuration` con lógica de parseo o reglas | Regla de negocio en infra | Extraer a `domain/model/` |
| `@Service` de application leyendo `jwt.getClaim(...)` | Application conoce detalles de seguridad | Crear puerto + adaptador |
| Controller con `if (rol.equals("ADMIN"))` | Regla de negocio en web | Mover a entidad/VO del dominio |
| Nombre de tabla/columna hardcodeado en use case | Application conoce persistencia | Queda en `@Entity` JPA del adaptador |
| Test de dominio que requiere `@SpringBootTest` | Dominio tiene dependencias de framework | Quitar dependencias, usar Java puro |

Regla general: **si tu test de dominio necesita un `@ExtendWith(...)` de Spring, Mockito de Spring,
o un mock de `RabbitTemplate`/`Jwt`/`JpaRepository`, la lógica está en la capa equivocada.**

---

## AggregateRoot — Regla Estricta

> **Nota sobre el nombre:** `AggregateRoot` (en `shared:domain`) es la clase base
> que gestiona **únicamente la emisión de eventos de dominio** en memoria. NO define
> identidad, invariantes ni comportamiento de un Aggregate Root completo. Una entidad
> es un Aggregate Root cuando, además de extender esta clase, define su identidad,
> sus invariantes (validaciones en el constructor) y su comportamiento de negocio
> (factories `build`/`rebuild`, métodos de acción).

### Principio DDD

En el proyecto Arquisoft, **toda entidad que sea raíz de su agregado DEBE extender `AggregateRoot` de `shared:domain`**, con una única excepción:

- **Excepción:** el contexto `seguridad` no usa `AggregateRoot` porque es transversal y delega el estado en Keycloak.

En los otros 6 contextos, la ausencia de `AggregateRoot` en una entidad raíz es un **error bloqueante** que debe corregirse.

> **Importante:** una entidad puede extender `AggregateRoot` y NO emitir eventos.
> Si la HU es un CRUD simple sin consumidores conocidos del evento, el factory `build(...)`
> simplemente no llama a `publishEvent(...)` y el use case no inyecta `EventPublisher`.
> Extender `AggregateRoot` es por consistencia y para tener la maquinaria lista
> el día que aparezca la necesidad de emitir eventos. Ver sección "¿Cuándo emitir eventos
> de dominio?" más abajo.

### ¿Qué es AggregateRoot?

Clase base en `shared:domain` que gestiona eventos de dominio acumulados en memoria hasta que el use case los drena tras persistir.

```java
// Ya existe en shared:domain — no reimplementar, solo usar
public abstract class AggregateRoot {
    private final List<DomainEvent> unPublishedEvents = new ArrayList<>();

    protected void publishEvent(DomainEvent event) { unPublishedEvents.add(event); }
    public List<DomainEvent> getUnPublishedEvents() { return new ArrayList<>(unPublishedEvents); }
    public void clearUnPublishedEvents() { unPublishedEvents.clear(); }
}
```

### ¿Qué es DomainEvent?

Clase base en `shared:domain` que asigna automáticamente `eventId` (UUID), `occurredAt` (Instant) y `eventType` (simpleName de la subclase). Define el método abstracto `getEventTopic()` que cada subclase DEBE implementar.

```java
// Ya existe en shared:domain
public abstract class DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final LocalDateTime occurredAt;
    private final String eventType;

    protected DomainEvent(String aggregateId) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.occurredAt = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }

    /**
     * Routing key con la que este evento se publica al exchange arquisoft.events.
     * Formato esperado: "{contexto}.{entidad}.{accion}" (ej. "fichas.ficha.creada").
     * Cada subclase de DomainEvent DEBE implementar este método.
     */
    public abstract String getEventTopic();

    // getters...
}
```

### Ciclo completo: emisión y drenado

1. **Dominio:** la entidad raíz acumula eventos con `publishEvent(...)` en sus métodos de negocio (incluido el factory `build`).
2. **Use case:** tras persistir, drena los eventos con `getUnPublishedEvents()`, los entrega a `EventPublisher` (puerto `shared:amqp` con firma `void publish(DomainEvent event)`) y llama a `clearUnPublishedEvents()`.
3. **`EventPublisher` lee internamente `event.getEventTopic()`** y publica al exchange `arquisoft.events`. La implementación concreta `RabbitMQEventPublisher` ya vive en `shared:amqp`.
4. **El dominio NUNCA** inyecta `EventPublisher`. Solo acumula eventos en memoria.
5. **El controller NUNCA** drena eventos directamente. Solo lo hace el use case.

### Factory methods obligatorios

| Método | Cuándo usar | ¿Emite evento? | ¿Genera UUID? |
|---|---|---|---|
| `build(...)` | Crear entidad nueva desde un comando/DTO | Solo si la HU emite eventos (ver "¿Cuándo emitir eventos?") | ✅ Sí — `UUID.randomUUID()` |
| `rebuild(...)` | Reconstruir entidad desde persistencia | ❌ Nunca | ❌ No — recibe el UUID de BD |

**Regla dura:** un `CommandOutputAdapter` SIEMPRE usa `rebuild(...)`, nunca `build(...)`.

### ¿Cuándo emitir eventos de dominio?

Una HU de Escritura puede emitir eventos o no. La decisión depende de si hay (o se anticipa) **al menos un consumidor** que necesite reaccionar al hecho.

**Emite eventos cuando:**
- Otro bounded context necesita reaccionar (ej. al crearse una `FichaPerfil`, el contexto `entregables` debe replicarla localmente).
- Hay un caso concreto de auditoría/observabilidad que requiere registrar el hecho.
- Se anticipa razonablemente que aparecerá un consumidor en HUs próximas.

**NO emite eventos cuando:**
- Es un CRUD interno del contexto sin consumidores conocidos ni casos de auditoría (ej. registrar un tipo de proyecto en un catálogo administrativo).
- La HU solo lee/consulta (las consultas nunca emiten eventos).
- Ningún otro contexto necesita saber del cambio y no hay caso de uso futuro identificado.

**Implicaciones de la decisión:**

| Decisión | `build(...)` | Use case | Plan declara |
|---|---|---|---|
| **Con eventos** | Llama a `publishEvent(new {Entidad}{Accion}Event(...))` | Inyecta `EventPublisher`, drena con `getUnPublishedEvents()`, publica, llama a `clearUnPublishedEvents()` | Sección 4 lista los eventos emitidos con su `eventTopic` |
| **Sin eventos (CRUD simple)** | NO llama a `publishEvent(...)` | NO inyecta `EventPublisher`, no hay drenado/limpieza | Sección 4 declara explícitamente "Esta HU no emite eventos: <razón>" |

**Ambas opciones son válidas.** La entidad sigue extendiendo `AggregateRoot` por consistencia y para tener la maquinaria lista. Cuando aparezca la necesidad futura, solo añades la clase del evento, llamas a `publishEvent(...)` en `build(...)` e inyectas `EventPublisher` en el use case.

### Firma de EventPublisher (shared:amqp)

```java
// Ya existe en shared:amqp
public interface EventPublisher {
    /**
     * Publica un evento de dominio al exchange arquisoft.events
     * usando la routing key que devuelve event.getEventTopic().
     */
    void publish(DomainEvent event);
}
```

**Una sola firma con type safety.** Recibe `DomainEvent` (no `Object`) — el compilador garantiza que solo se publican eventos de dominio.

---

## Java 21 — Uso Balanceado

Aplicar features de Java 21 **cuando aporten claridad o seguridad**, no por moda. Reglas concretas:

### Features recomendadas

| Feature | Cuándo usar | Ejemplo |
|---|---|---|
| `record` | **Value Objects** del dominio y **payloads de eventos** (inmutabilidad + equals/hashCode gratis) | `public record Rango(int minimo, int maximo) {}` |
| `sealed class` + `permits` | Jerarquías cerradas del dominio (ej. estados de una máquina de estados) | `sealed interface EstadoFicha permits Borrador, EnRevision, Aprobada {}` |
| Pattern matching para `switch` | Ramificación sobre tipos (ej. sobre un sealed type) | `return switch (estado) { case Borrador b -> ...; case EnRevision r -> ...; };` |
| Pattern matching para `instanceof` | Donde antes había `instanceof` + cast | `if (evento instanceof FichaCreadaEvent f) { use(f.getTitulo()); }` |
| Text blocks (`"""..."""`) | SQL inline, JSON de test, plantillas | `String sql = """ SELECT ... """;` |
| `var` | Variables locales con tipo evidente por RHS | `var ficha = Ficha.build("titulo");` |

### Features NO recomendadas en este proyecto

| Feature | Motivo |
|---|---|
| **`record` para entidades de dominio** | Las entidades Arquisoft requieren constructor privado + factory methods `build`/`rebuild`. Un `record` tiene constructor público y no permite el patrón. Usar `class` inmutable con campos `final`. |
| **Virtual threads manuales (`Thread.ofVirtual`, `Executors.newVirtualThreadPerTaskExecutor`)** | `spring.threads.virtual.enabled: true` ya los gestiona para Tomcat, `@Async` y RabbitMQ listeners. Crear executors manuales es innecesario y a menudo perjudicial. |
| **`record` + Lombok mezclados** | Redundante y confuso — si es `record`, no usar `@Data`/`@Builder`. |

### Regla de oro

> Si la feature no mejora la claridad del código específico que estás escribiendo, **no la uses**. Un `switch` clásico está bien; un `instanceof` con cast explícito está bien.

---

## Tipos de Use Case y sus Tests

No todos los use cases son iguales — y los tests apropiados dependen del tipo.
Antes de planificar tests, identifica qué tipo de use case estás trabajando:

### Use Case de Escritura (crear, actualizar, eliminar)

Características:
- Modifica el estado del Aggregate Root.
- Genera eventos de dominio (`build()` los acumula, `update()` los acumula).
- El use case drena los eventos tras persistir y los publica vía `EventPublisher`.

Tests apropiados:
- **domain:** ciclo completo de eventos (`publishEvent` → `getUnPublishedEvents` → `clearUnPublishedEvents`), `rebuild()` no emite eventos, invariantes del constructor.
- **application:** flujo exitoso, error de repositorio, drenado de eventos verificado con `verify(eventPublisher).publish(...)`.
- **infrastructure:** controller con códigos HTTP correctos (201, 400, 401, 403), repositorio guarda y reconstruye con `rebuild()`.

### Use Case de Consulta (listar, buscar, obtener)

Características:
- **NO** modifica estado.
- **NO** genera eventos de dominio.
- Devuelve datos (DTO o lista de DTOs).
- Puede tener filtros, paginación, ordenamiento.

Tests apropiados:
- **domain:** validaciones de los Value Objects que reciba como parámetros (ej. enum de estado, criterios de filtro).
- **application:** flujo con datos válidos, lista vacía cuando no hay resultados, excepción cuando los filtros son inválidos.
- **infrastructure:** controller con códigos HTTP correctos (200, 400, 404, 401, 403), repositorio retorna lista esperada.

**Tests que NO aplican a use cases de consulta:**
- ❌ Ciclo de eventos del Aggregate Root (no hay eventos).
- ❌ Verificación de `eventPublisher.publish(...)` (no se publica nada).
- ❌ `clearUnPublishedEvents()` / `getUnPublishedEvents()`.
- ❌ Validación de que `rebuild()` no emite eventos (irrelevante en flujo de lectura).

### Use Case Mixto (raro, requiere cuidado)

Algunos use cases hacen ambas cosas (ej. "buscar entidad y marcarla como vista" — lectura con efecto secundario). Si el plan describe esto, los tests son la suma de ambos tipos. **Si dudas si tu use case es mixto, casi siempre no lo es** — separa la consulta de la escritura en dos use cases distintos antes de tener un mixto.

---

## Anti-patrones de Testing en Arquisoft

> **Filosofía:** se testea **comportamiento observable**, no implementación interna.
> Cobertura del 75% es objetivo, no premio. Una alta cobertura con tests triviales
> es **peor** que cobertura más baja con tests significativos.

### Lo que NO se testea (con ejemplos)

#### ❌ Anti-patrón 1: Tests de getters/setters generados por Lombok

```java
// ❌ MAL — testea Lombok, no tu código
@Test
void debeRetornarTitulo_cuandoGetTituloEsLlamado() {
    Ficha ficha = Ficha.build("Mi título");
    assertThat(ficha.getTitulo()).isEqualTo("Mi título");
}
```

`@Data`, `@Getter`, `@Builder` ya están testeados por el equipo de Lombok. **Tu código no aporta lógica al getter, no hay nada que testear.** Solo testea getters cuando contengan lógica custom (ej. `public String getNombreCompleto() { return nombre + " " + apellido; }`).

#### ❌ Anti-patrón 2: Tests de cada validación Jakarta una por una

```java
// ❌ MAL — 6 tests para una sola anotación
@Test void debeRechazar_cuandoTituloEsNull() { ... }
@Test void debeRechazar_cuandoTituloEsVacio() { ... }
@Test void debeRechazar_cuandoTituloTieneEspacios() { ... }
@Test void debeRechazar_cuandoTituloEsMuyCorto() { ... }
@Test void debeRechazar_cuandoTituloEsMuyLargo() { ... }
@Test void debeRechazar_cuandoTituloTieneCaracteresEspeciales() { ... }
```

`@NotBlank`, `@Size`, `@Email` ya están testeados por Jakarta Validation. **Un solo test que verifique "el DTO falla con datos inválidos" es suficiente:**

```java
// ✅ BIEN — un test global de validación
@Test
void debeRechazarRequest_cuandoCamposObligatoriosFaltan() {
    CrearFichaRequestDTO req = new CrearFichaRequestDTO();
    Set<ConstraintViolation<CrearFichaRequestDTO>> violations = validator.validate(req);
    assertThat(violations).isNotEmpty();
}
```

Si tienes un **validator custom** (con regla de negocio propia, ej. "el título debe empezar con P-"), eso sí lo testeas — pero solo el validator custom, no las anotaciones estándar.

#### ❌ Anti-patrón 3: Tests de métodos `private`

```java
// ❌ MAL — el método es privado, no debería testearse directamente
@Test void debeConvertirEstadoActivo_cuandoStringEsActivo() { ... }
@Test void debeConvertirEstadoInactivo_cuandoStringEsInactivo() { ... }
@Test void debeRetornarNulo_cuandoEstadoStringEsNulo() { ... }
@Test void debeRetornarNulo_cuandoEstadoStringEsVacio() { ... }
@Test void debeSerCaseInsensitive_cuandoConvertirEstadoConMinusculas() { ... }
```

Métodos `private` son detalles de implementación. **Su comportamiento se valida indirectamente** desde los tests del método público que los usa. Si conviertes un método `private` en `package-private` solo para testearlo, estás rompiendo encapsulación.

✅ Si necesitas mucho un helper específico, **promuévelo a una clase aparte** con responsabilidad clara (ej. `EstadoUsuarioConverter` como Value Object) y testéalo allí.

#### ❌ Anti-patrón 4: Tests duplicados con asserts distintos

```java
// ❌ MAL — dos tests para el mismo escenario
@Test
void debeLanzarExcepcion_cuandoEstadoFiltroEsInvalido() {
    assertThatThrownBy(() -> useCase.ejecutar("BLOQUEADO"))
        .isInstanceOf(ParametroFiltroInvalidoException.class);
}

@Test
void debeLanzarExcepcionConErrorCode_cuandoEstadoFiltroEsInvalido() {
    Throwable ex = catchThrowable(() -> useCase.ejecutar("BLOQUEADO"));
    assertThat(((DomainException) ex).getErrorCode()).isEqualTo("PARAMETRO_FILTRO_INVALIDO");
}
```

Mismo "Act", asserts complementarios → **consolidar en un solo test:**

```java
// ✅ BIEN — un test con los asserts agrupados
@Test
void debeLanzarExcepcion_cuandoEstadoFiltroEsInvalido() {
    Throwable ex = catchThrowable(() -> useCase.ejecutar("BLOQUEADO"));

    assertThat(ex)
        .isInstanceOf(ParametroFiltroInvalidoException.class)
        .hasMessageContaining("BLOQUEADO");
    assertThat(((DomainException) ex).getErrorCode())
        .isEqualTo("PARAMETRO_FILTRO_INVALIDO");
}
```

#### ❌ Anti-patrón 5: Tests de delegación pura sin lógica

```java
// ❌ MAL — solo verifica que un método llama a otro
@Test
void debeDelegarBusquedaAlRepositorio_cuandoBuscarUsuariosEsInvocado() {
    useCase.ejecutar(filtros);
    verify(repository).buscar(filtros);
}
```

Si el use case **solo** delega al repositorio (sin transformar, validar, ni orquestar nada), el `verify()` ya está cubierto en el test del flujo principal. No necesita test aparte.

#### ❌ Anti-patrón 6: Tests de excepciones simples sin lógica

```java
// ❌ MAL — testear que super() funciona
@Test
void debeContenerErrorCode_cuandoExcepcionEsCreada() {
    FichaNoEncontradaException ex = new FichaNoEncontradaException("123");
    assertThat(ex.getErrorCode()).isEqualTo("FICHA_NO_ENCONTRADA");
}
```

Una excepción que solo hace `super("CODIGO", "mensaje")` no tiene lógica propia. **Su `errorCode` se verifica implícitamente** desde el test del use case que la lanza (anti-patrón 4 muestra el patrón correcto). Solo escribe `*ExceptionTest.java` si la excepción tiene lógica adicional (validaciones en el constructor, transformación de mensaje según parámetros, etc.).

#### ❌ Anti-patrón 7: Tests de equals/hashCode/toString generados

```java
// ❌ MAL — testea Lombok
@Test void debeSerIgual_cuandoIdsCoinciden() { ... }
@Test void debeTenerHashCodeConsistente() { ... }
@Test void debeRetornarToString_conTodosLosCampos() { ... }
```

`@Data` y `@EqualsAndHashCode` generan estos métodos correctamente. **Solo testea equality si tu código tiene un `equals()` custom** (lo cual debería ser raro y siempre justificado).

### Regla de consolidación

**Si tienes 3 o más tests con el mismo "Act" pero distintos "Assert", consolídalos en un solo test con múltiples asserts.** El patrón AAA permite varios asserts si todos verifican el mismo escenario desde ángulos complementarios.

### Presupuesto orientativo de tests por HU

| Tipo de HU | Tests esperados | Notas |
|---|---|---|
| HU pequeña (1 endpoint, 1 entidad) | 15 - 25 | CRUD básico de una entidad |
| HU mediana (2-3 endpoints) | 25 - 50 | Varios endpoints sobre la misma entidad |
| HU grande (4+ endpoints o flujo complejo) | 50 - 80 | Justificable solo en flujos complejos |
| HU > 80 tests | revisar | Casi siempre indica sobre-testeo. Buscar duplicados, helpers privados, validaciones Jakarta repetidas. |

**119 tests para una HU es señal clara de sobre-testeo** — revisar contra los 7 anti-patrones de arriba antes de aceptar el resultado.

---

## Convención de DTOs — técnicos genéricos vs DTOs de dominio

Esta convención evita duplicación, mantiene la API consistente con la industria
y respeta el modelo de dominio en el código interno.

### Dos tipos de DTOs en el proyecto

### Cuatro tipos de objetos de transferencia en el proyecto

| Tipo | Idioma de campos | Ubicación | Ejemplos |
|---|---|---|---|
| **`Command`** (intención de escritura) | **Español** (idéntico al aggregate) | `application/{entidad}/command/model/` | `CrearFichaPerfilCommand`, `AprobarFichaPerfilCommand` |
| **`ReadModel`** (proyección de lectura) | **Español** (idéntico al aggregate) | `application/{entidad}/query/readmodel/` | `FichaPerfilReadModel` |
| **`RequestDTO`** (entrada HTTP) | **Español** (idéntico al aggregate) | `infrastructure/{entidad}/command/adapter/in/web/dto/` | `CrearFichaPerfilRequestDTO` (con `@NotBlank`, `@Email`, etc.) |
| **DTO técnico genérico** | **Inglés** (convención de la industria) | `shared:web` | `ErrorResponseDTO`, `PageResponseDTO<T>`, `QueryCriteriaRequestDTO`, `NodoFiltroDTO` |

### Reglas inviolables

1. **`Command` y `ReadModel` son `record`, no clases con Lombok.** Inmutables por construcción.
2. **`RequestDTO` es `record` con anotaciones Jakarta** (`@NotBlank`, `@Email`, etc.). Tiene un método `toCommand()` que produce el `Command` correspondiente. Vive en **infrastructure**, no en application.
3. **El UseCase NO devuelve un `ResponseDTO`.** Los UseCases write devuelven `UUID` (o nada → `VoidInputPort`); los read devuelven `ReadModel` o `PaginatedResult<ReadModel>`. No existe un "ResponseDTO" intermedio entre la capa de aplicación y el adaptador REST: el `InputAdapter` serializa directamente el `ReadModel` a JSON.
4. **NUNCA crees un `PageResponseDTO` o `ErrorResponseDTO` local en un contexto.** Importa desde `com.arquisoft.shared.web`.
5. **Campos en español, idénticos al aggregate.** Si el aggregate dice `tituloProyecto`, todo lo que lo transporte (Command, ReadModel, RequestDTO, JSON HTTP) usa `tituloProyecto`. **NO** se traduce a inglés ni se renombra.
4. **No anotes campos con `@JsonProperty` para mezclar idiomas.** Si el JSON externo necesita un nombre específico, evalúa si el DTO debería ser técnico (en inglés) o de dominio (en español) — no ambos.

---

## Paginación y Filtros — `PaginatedResult` + Criteria pattern

> **Cuándo usar:** las HUs de **read** que requieren paginación, ordenamiento o filtros dinámicos. Si una HU de consulta no necesita ninguno de los tres, no se usa Criteria — la firma del puerto recibe parámetros simples y retorna `ReadModel` o `List<ReadModel>`.

### Las piezas (todas opcionales por HU)

| Pieza | Ubicación | Cuándo se crea |
|---|---|---|
| `XxxCriteria extends QueryCriteria` | `application/{entidad}/query/criteria/` | HU con paginación, ordenamiento o filtros |
| `XxxJpaSpecification extends QueryJpaSpecification<JpaEntity>` | `infrastructure/{entidad}/query/adapter/out/persistence/` | Si la HU tiene filtros (árbol `NodoFiltro`) |
| `XxxSortMapper` | `infrastructure/{entidad}/query/adapter/out/persistence/` | Si los campos de ordenamiento del cliente difieren de los paths JPA (joins implícitos) |

### Tipos en `shared:domain`

```java
// PaginatedResult — record inmutable
public record PaginatedResult<T>(
    List<T> content,
    int page, int size, long totalElements, int totalPages
) {
    public <R> PaginatedResult<R> map(Function<T, R> mapper) { /* preserva metadatos */ }
}

// QueryCriteria — clase base con builder que valida en construcción
public abstract class QueryCriteria {
    private final int pagina;
    private final int tamanio;
    private final List<SortOrder> ordenamiento;
    private final NodoFiltro raiz;
    // builder valida: campos en whitelist, profundidad árbol ≤ 10, operadores con valor
}

// NodoFiltro — sealed: Predicado | Grupo
public sealed interface NodoFiltro permits NodoFiltro.Predicado, NodoFiltro.Grupo {
    record Predicado(String campo, Operador operador, Object valor) implements NodoFiltro {}
    record Grupo(Conector conector, List<NodoFiltro> hijos) implements NodoFiltro {}
}
```

### Tipos en `shared:web`

```java
// QueryCriteriaRequestDTO — input HTTP polimórfico vía Jackson
public record QueryCriteriaRequestDTO(
    int pagina, int tamanio,
    List<String> ordenamiento,        // ["tituloProyecto:ASC", "asesorNombre:DESC"]
    NodoFiltroDTO filtros              // árbol polimórfico
) {}

// PageResponseDTO — output HTTP
public record PageResponseDTO<T>(
    List<T> content, int page, int size,
    long totalElements, int totalPages
) {
    public static <T> PageResponseDTO<T> from(PaginatedResult<T> result) { /* ... */ }
}
```

### Tipos en `shared:postgres`

```java
// QueryJpaSpecification — recorre el árbol NodoFiltro y produce Specification<JpaEntity>
public abstract class QueryJpaSpecification<E> {
    protected abstract Map<String, CampoSpec<E>> camposFiltrables();
    public Specification<E> desdeCriteria(QueryCriteria criteria) { /* recorre el árbol */ }
}

// CampoSpec — sealed con 7 variantes
public sealed interface CampoSpec<E> {
    Specification<E> construirSpec(Operador op, Object valor);
    // Variantes: Texto, Uuid, Entero, Decimal, Fecha, FechaHora, Booleano
    static <E> CampoSpec<E> texto(Function<Root<E>, Path<String>> path) { /* ... */ }
    static <E> CampoSpec<E> uuid(Function<Root<E>, Path<UUID>> path) { /* ... */ }
    // ... etc
}
```

| Tipo de campo | Operadores válidos | SQL típico |
|---|---|---|
| `Texto` | `CONTIENE`, `NO_CONTIENE`, `EMPIEZA_CON`, `TERMINA_CON`, `ES`, `NO_ES`, `ES_NULO`, `NO_ES_NULO` | `LOWER(col) LIKE '%val%'` |
| `Uuid` | `ES`, `NO_ES`, `ES_NULO`, `NO_ES_NULO` | `col = ?` |
| `Entero` / `Decimal` | `ES`, `NO_ES`, `MAYOR_QUE`, `MENOR_QUE`, `MAYOR_IGUAL_QUE`, `MENOR_IGUAL_QUE`, `ES_NULO`, `NO_ES_NULO` | `col > ?` |
| `Fecha` / `FechaHora` | Igual que `Entero` (ISO 8601 `yyyy-MM-dd` / `yyyy-MM-ddTHH:mm:ss`) | `col >= ?` |
| `Booleano` | `ES`, `NO_ES`, `ES_NULO`, `NO_ES_NULO` | `col = true` |

### Flujo completo de una HU read con Criteria

```
POST /api/fichas-perfil/query
Body: { pagina, tamanio, ordenamiento, filtros: { tipo:GRUPO, conector:AND, nodos:[...] } }
  │
  ▼ QueryInputAdapter
QueryCriteriaRequestDTO → solicitud.parsearAOrdenamiento()  → List<SortOrder>
                       → solicitud.parsearFiltros()         → NodoFiltro (sealed tree)
  │
  ▼ FichaPerfilCriteria.builder().build()  ← valida: whitelist de campos, profundidad ≤ 10
  │
  ▼ ConsultarFichasPerfilInputPort.ejecutar(criteria)
  │
  ▼ ConsultarFichasPerfilQueryUseCase
       fichaPerfilQueryOutputPort.consultarTodas(criteria)  ← delegación pura
  │
  ▼ FichaPerfilQueryOutputAdapter
       1. SortMapper:  "asesorNombre" → "asesorFicha.nombre"  (joined entity)
       2. Specification = FichaPerfilJpaSpecification.desdeCriteria(criteria)
       3. jpaRepository.findAll(spec, pageable)               ← @EntityGraph evita N+1
       4. .map(FichaPerfilMapper::toReadModel)
       5. PaginationMapper.toResult(...)                      ← PaginatedResult<ReadModel>
  │
  ▼ InputAdapter retorna PageResponseDTO.from(paginatedResult)
  │
  ▼ HTTP 200 con JSON: { content, page, size, totalElements, totalPages }
```

### Plantilla `FichaPerfilCriteria`

```java
// application/fichaPerfil/query/criteria/FichaPerfilCriteria.java
package com.arquisoft.fichas.application.fichaPerfil.query.criteria;

import com.arquisoft.shared.domain.pagination.QueryCriteria;
import com.arquisoft.shared.domain.pagination.NodoFiltro;
import com.arquisoft.shared.domain.pagination.SortOrder;
import java.util.List;
import java.util.Set;

public final class FichaPerfilCriteria extends QueryCriteria {

    /** Whitelist de campos filtrables y ordenables (validados en el builder). */
    public static final class Campo {
        public static final String TITULO_PROYECTO = "tituloProyecto";
        public static final String ASESOR_NOMBRE   = "asesorNombre";
        public static final String ASESOR_EMAIL    = "asesorEmail";
        public static final String ASESOR_ID       = "asesorId";

        public static final Set<String> FILTRABLES = Set.of(
            TITULO_PROYECTO, ASESOR_NOMBRE, ASESOR_EMAIL, ASESOR_ID);
        public static final Set<String> ORDENABLES = Set.of(
            TITULO_PROYECTO, ASESOR_NOMBRE);
    }

    private FichaPerfilCriteria(int pagina, int tamanio,
                                List<SortOrder> ordenamiento, NodoFiltro raiz) {
        super(pagina, tamanio, ordenamiento, raiz, Campo.FILTRABLES, Campo.ORDENABLES);
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        // setters fluentes... y .build() invoca al constructor (valida vía super)
    }
}
```

### Plantilla `FichaPerfilJpaSpecification`

```java
// infrastructure/fichaPerfil/query/adapter/out/persistence/FichaPerfilJpaSpecification.java
package com.arquisoft.fichas.infrastructure.fichaPerfil.query.adapter.out.persistence;

import com.arquisoft.shared.postgres.QueryJpaSpecification;
import com.arquisoft.shared.postgres.CampoSpec;
import com.arquisoft.fichas.infrastructure.fichaPerfil.persistence.FichaPerfilJpaEntity;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class FichaPerfilJpaSpecification
        extends QueryJpaSpecification<FichaPerfilJpaEntity> {

    @Override
    protected Map<String, CampoSpec<FichaPerfilJpaEntity>> camposFiltrables() {
        return Map.of(
            "tituloProyecto", CampoSpec.texto(root -> root.get("tituloProyecto")),
            "asesorNombre",   CampoSpec.texto(root -> root.get("asesorFicha").get("nombre")),
            "asesorEmail",    CampoSpec.texto(root -> root.get("asesorFicha").get("email")),
            "asesorId",       CampoSpec.uuid(root -> root.get("asesorFicha").get("id"))
        );
    }
}
```

> El mapa traduce el nombre **de dominio** (`asesorNombre`) al **path JPA** (`asesorFicha.nombre`) para joins implícitos. Lo mismo aplica al `SortMapper` cuando el cliente ordena por un campo de una entidad joined.

### Plantilla `FichaPerfilQueryOutputAdapter` con Criteria

```java
// infrastructure/fichaPerfil/query/adapter/out/persistence/FichaPerfilQueryOutputAdapter.java
@Component
@RequiredArgsConstructor
public class FichaPerfilQueryOutputAdapter implements FichaPerfilQueryOutputPort {

    private final FichaPerfilJpaRepository jpaRepository;
    private final FichaPerfilJpaSpecification specification;
    private final FichaPerfilSortMapper sortMapper;
    private final FichaPerfilMapper mapper;

    @Override
    public PaginatedResult<FichaPerfilReadModel> consultarTodas(FichaPerfilCriteria criteria) {
        Specification<FichaPerfilJpaEntity> spec = specification.desdeCriteria(criteria);
        PageRequest pageable = PageRequest.of(
            criteria.getPagina(), criteria.getTamanio(),
            sortMapper.aSort(criteria.getOrdenamiento())
        );

        return PaginationMapper.toResult(
            jpaRepository.findAll(spec, pageable).map(mapper::toReadModel)
        );
    }
}
```

### `@EntityGraph` para evitar N+1

Cuando el `ReadModel` incluye datos de una entidad relacionada (ej. `asesorNombre`), el `JpaRepository` declara `@EntityGraph(attributePaths = {"asesorFicha"})` en el método `findAll(Specification, Pageable)` para cargar la relación en una sola query con `JOIN FETCH`.

```java
public interface FichaPerfilJpaRepository extends JpaRepository<FichaPerfilJpaEntity, UUID>,
        JpaSpecificationExecutor<FichaPerfilJpaEntity> {

    @Override
    @EntityGraph(attributePaths = {"asesorFicha"})
    Page<FichaPerfilJpaEntity> findAll(Specification<FichaPerfilJpaEntity> spec, Pageable pageable);
}
```

### Reglas inviolables

1. **Whitelist obligatoria de campos.** Si el cliente envía un campo no permitido en `Criteria.Campo.FILTRABLES` o `ORDENABLES`, el builder lanza excepción. Nunca se confía en input del cliente para construir SQL.
2. **`QueryJpaSpecification` y `CampoSpec` viven en `shared:postgres`.** Nunca se reimplementan en el contexto. El contexto solo declara su `XxxJpaSpecification` con su mapa de campos.
3. **Validación de profundidad del árbol ≤ 10 niveles.** Previene ataques de árboles maliciosamente profundos. Validado en el constructor de `QueryCriteria`.
4. **`PaginatedResult<T>` vive en `shared:domain.pagination`** y es el retorno canónico del read side cuando hay paginación.
5. **`PageResponseDTO.from(PaginatedResult)`** es la única conversión válida hacia HTTP. Nunca serializar `PaginatedResult` directamente.
6. **NO usar `Pageable` ni `org.springframework.data.domain.Page` en `application/` ni `domain/`.** Esos tipos solo viven en `infrastructure/{entidad}/query/adapter/out/persistence/`. El read side de application maneja `XxxCriteria` y `PaginatedResult`.
7. **Cuando una HU de read NO necesita paginación, ordenamiento ni filtros**, NO se crean `Criteria`/`JpaSpecification`/`SortMapper`. El puerto recibe parámetros simples (ej. `UUID id`) y retorna `ReadModel` o `List<ReadModel>`.

---

### Value Object (usar `record`)

```java
package com.arquisoft.evaluaciones.domain.model;

public record Calificacion(double valor) {
    public Calificacion {
        if (valor < 0.0 || valor > 5.0) {
            throw new IllegalArgumentException("La calificación debe estar entre 0.0 y 5.0");
        }
    }
}
```

### Excepción de dominio

```java
package com.arquisoft.fichas.domain.fichaPerfil.exception;

import com.arquisoft.shared.domain.exception.DomainException;

public class FichaPerfilNoEncontradaException extends DomainException {
    public FichaPerfilNoEncontradaException(String id) {
        super("FICHA_PERFIL_NO_ENCONTRADA", "No se encontró la ficha con id: " + id);
    }
}
```

> Toda excepción del contexto extiende uno de los 4 tipos base: `DomainException` (422), `ApplicationException` (400), `InfrastructureException` (503), `DomainValidationException` (422). **NUNCA** extender `RuntimeException` directamente. Ver sección "Jerarquía de excepciones" más abajo.

### Plantilla completa de una HU write — InputPort + Command + UseCase + Aggregate

A continuación, una HU de creación de un agregado (write side) de principio a fin con todos los archivos involucrados. Tomamos como referencia "crear una `FichaPerfil`" del contexto `fichas`.

#### 1. Command (intención de negocio)

```java
// application/fichaPerfil/command/model/CrearFichaPerfilCommand.java
package com.arquisoft.fichas.application.fichaPerfil.command.model;

import java.util.UUID;

public record CrearFichaPerfilCommand(
    String tituloProyecto,
    UUID asesorFichaId
) {}
```

> Los nombres de los campos del `Command` **coinciden exactamente** con los del aggregate. Nada se traduce ni se renombra.

#### 2. Aggregate (con Notification Pattern + eventos)

```java
// domain/fichaPerfil/aggregate/FichaPerfilAggregate.java
package com.arquisoft.fichas.domain.fichaPerfil.aggregate;

import com.arquisoft.shared.domain.events.AggregateRoot;
import com.arquisoft.shared.domain.validation.ValidationResult;
import com.arquisoft.shared.domain.validation.DomainValidator;
import com.arquisoft.fichas.domain.fichaPerfil.event.FichaPerfilCreadaEvent;
import com.arquisoft.fichas.domain.fichaPerfil.message.FichaPerfilDomainMessages;
import java.util.UUID;

public final class FichaPerfilAggregate extends AggregateRoot {
    private UUID id;
    private String tituloProyecto;
    private UUID asesorFichaId;
    private String estado;

    private FichaPerfilAggregate() {}

    public static FichaPerfilAggregate crear(String tituloProyecto, UUID asesorFichaId) {
        FichaPerfilAggregate aggregate = new FichaPerfilAggregate();
        ValidationResult result = new ValidationResult();
        aggregate.setTituloProyecto(tituloProyecto, result);
        aggregate.setAsesorFichaId(asesorFichaId, result);
        result.throwIfHasErrors();

        aggregate.id = UUID.randomUUID();
        aggregate.estado = "BORRADOR";
        aggregate.publishEvent(new FichaPerfilCreadaEvent(
            aggregate.id, aggregate.tituloProyecto, aggregate.asesorFichaId));
        return aggregate;
    }

    public static FichaPerfilAggregate rebuild(UUID id, String titulo, UUID asesorId, String estado) {
        FichaPerfilAggregate a = new FichaPerfilAggregate();
        a.id = id; a.tituloProyecto = titulo; a.asesorFichaId = asesorId; a.estado = estado;
        return a;
    }

    private void setTituloProyecto(String titulo, ValidationResult result) {
        if (!DomainValidator.notBlank(titulo,
                FichaPerfilDomainMessages.CAMPO_TITULO,
                FichaPerfilDomainMessages.TITULO_REQUERIDO, result)) return;
        this.tituloProyecto = titulo.trim();
    }

    private void setAsesorFichaId(UUID id, ValidationResult result) {
        if (!DomainValidator.notNull(id,
                FichaPerfilDomainMessages.CAMPO_ASESOR,
                FichaPerfilDomainMessages.ASESOR_REQUERIDO, result)) return;
        this.asesorFichaId = id;
    }

    public UUID getId() { return id; }
    public String getTituloProyecto() { return tituloProyecto; }
    public UUID getAsesorFichaId() { return asesorFichaId; }
    public String getEstado() { return estado; }
}
```

#### 3. Evento de dominio

```java
// domain/fichaPerfil/event/FichaPerfilCreadaEvent.java
package com.arquisoft.fichas.domain.fichaPerfil.event;

import com.arquisoft.shared.domain.events.DomainEvent;
import java.util.UUID;

public final class FichaPerfilCreadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "fichas.fichaPerfil.creada";
    public static final String EVENT_TYPE  = "FichaPerfilCreadaEvent";

    private final String tituloProyecto;
    private final UUID asesorFichaId;

    public FichaPerfilCreadaEvent(UUID aggregateId, String titulo, UUID asesorFichaId) {
        super(aggregateId.toString(), EVENT_TOPIC, EVENT_TYPE);
        this.tituloProyecto = titulo;
        this.asesorFichaId = asesorFichaId;
    }

    public String getTituloProyecto() { return tituloProyecto; }
    public UUID getAsesorFichaId() { return asesorFichaId; }
}
```

#### 4. Puerto de salida (write — en `domain`)

```java
// domain/fichaPerfil/port/out/FichaPerfilOutputPort.java
package com.arquisoft.fichas.domain.fichaPerfil.port.out;

import com.arquisoft.fichas.domain.fichaPerfil.aggregate.FichaPerfilAggregate;
import java.util.Optional;
import java.util.UUID;

public interface FichaPerfilOutputPort {
    void save(FichaPerfilAggregate aggregate);
    Optional<FichaPerfilAggregate> findById(UUID id);
}
```

#### 5. Puerto de entrada (write — en `application`)

```java
// application/fichaPerfil/command/port/in/CrearFichaPerfilInputPort.java
package com.arquisoft.fichas.application.fichaPerfil.command.port.in;

import com.arquisoft.shared.domain.port.in.InputPort;
import com.arquisoft.fichas.application.fichaPerfil.command.model.CrearFichaPerfilCommand;
import java.util.UUID;

public interface CrearFichaPerfilInputPort
        extends InputPort<CrearFichaPerfilCommand, UUID> {}
```

> La interfaz queda **vacía**. Solo hereda `ejecutar(I) → O` de `InputPort<I, O>`. Si el comando no retorna nada, extiende `VoidInputPort<I>`.

#### 6. UseCase (write — en `application`)

```java
// application/fichaPerfil/command/CrearFichaPerfilUseCase.java
package com.arquisoft.fichas.application.fichaPerfil.command;

import com.arquisoft.fichas.application.fichaPerfil.command.model.CrearFichaPerfilCommand;
import com.arquisoft.fichas.application.fichaPerfil.command.port.in.CrearFichaPerfilInputPort;
import com.arquisoft.fichas.domain.fichaPerfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaPerfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.amqp.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrearFichaPerfilUseCase implements CrearFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public UUID ejecutar(CrearFichaPerfilCommand command) {
        FichaPerfilAggregate aggregate = FichaPerfilAggregate.crear(
            command.tituloProyecto(), command.asesorFichaId());

        fichaPerfilOutputPort.save(aggregate);

        aggregate.drainUnPublishedEvents().forEach(eventPublisher::publish);

        return aggregate.getId();
    }
}
```

> **Patrón canónico:** `crear → persistir → drainUnPublishedEvents().forEach(publish) → retornar id`. El UseCase no decide qué eventos publicar; solo drena lo que el aggregate acumuló.

#### 7. RequestDTO (en `infrastructure`)

```java
// infrastructure/fichaPerfil/command/adapter/in/web/dto/CrearFichaPerfilRequestDTO.java
package com.arquisoft.fichas.infrastructure.fichaPerfil.command.adapter.in.web.dto;

import com.arquisoft.fichas.application.fichaPerfil.command.model.CrearFichaPerfilCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CrearFichaPerfilRequestDTO(
    @NotBlank String tituloProyecto,
    @NotNull UUID asesorFichaId
) {
    public CrearFichaPerfilCommand toCommand() {
        return new CrearFichaPerfilCommand(tituloProyecto, asesorFichaId);
    }
}
```

> El `RequestDTO` vive en **infrastructure**, no en application. Validación Jakarta (`@NotBlank`, `@NotNull`) es responsabilidad del adaptador. Convención: campos del DTO **idénticos** al modelo (sin traducir a inglés).

#### 8. InputAdapter (REST controller)

```java
// infrastructure/fichaPerfil/command/adapter/in/web/CrearFichaPerfilInputAdapter.java
package com.arquisoft.fichas.infrastructure.fichaPerfil.command.adapter.in.web;

import com.arquisoft.fichas.application.fichaPerfil.command.port.in.CrearFichaPerfilInputPort;
import com.arquisoft.fichas.infrastructure.fichaPerfil.command.adapter.in.web.dto.CrearFichaPerfilRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/fichas-perfil")
@RequiredArgsConstructor
@Tag(name = "Fichas de Perfil")
public class CrearFichaPerfilInputAdapter {

    private final CrearFichaPerfilInputPort crearFichaPerfilInputPort;

    @PostMapping
    @PreAuthorize("hasAuthority('fichas:fichaPerfil:create')")
    @Operation(summary = "Crear ficha de perfil",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ficha creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public ResponseEntity<Void> crear(@Valid @RequestBody CrearFichaPerfilRequestDTO request) {
        UUID id = crearFichaPerfilInputPort.ejecutar(request.toCommand());
        return ResponseEntity.created(URI.create("/api/fichas-perfil/" + id)).build();
    }
}
```

> **Convenciones del InputAdapter de escritura:**
> - Inyecta el `InputPort` (interfaz vacía), no el `UseCase` directamente.
> - Retorna `ResponseEntity<Void>` con `201 Created` + header `Location` apuntando al recurso. **No** devuelve el recurso completo — eso es responsabilidad de un endpoint de consulta separado (CQRS estricto).
> - El `@PreAuthorize` usa client roles con formato `{contexto}:{entidad}:{accion}` con la entidad en camelCase (`fichas:fichaPerfil:create`).

#### 9. CommandOutputAdapter (persistencia write)

```java
// infrastructure/fichaPerfil/command/adapter/out/persistence/FichaPerfilCommandOutputAdapter.java
package com.arquisoft.fichas.infrastructure.fichaPerfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.fichaPerfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaPerfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.fichaPerfil.persistence.FichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.fichaPerfil.persistence.FichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FichaPerfilCommandOutputAdapter implements FichaPerfilOutputPort {

    private final FichaPerfilJpaRepository jpaRepository;
    private final FichaPerfilMapper mapper;

    @Override
    public void save(FichaPerfilAggregate aggregate) {
        jpaRepository.save(mapper.toJpaEntity(aggregate));
    }

    @Override
    public Optional<FichaPerfilAggregate> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toAggregate);
    }
}
```

> **`JpaEntity`, `JpaRepository` y `Mapper`** viven en `infrastructure/{entidad}/persistence/` (compartido entre `command/` y `query/`).

### Plantilla resumida de una HU read — QueryInputPort + ReadModel + QueryUseCase

Cuando la HU es de consulta y NO necesita Criteria (consulta simple por id o por un campo):

#### 1. ReadModel

```java
// application/fichaPerfil/query/readmodel/FichaPerfilReadModel.java
public record FichaPerfilReadModel(
    UUID id,
    String tituloProyecto,
    String estado,
    String asesorNombre
) {}
```

#### 2. QueryInputPort (vacío)

```java
// application/fichaPerfil/query/port/in/ConsultarFichaPerfilInputPort.java
public interface ConsultarFichaPerfilInputPort
        extends InputPort<UUID, FichaPerfilReadModel> {}
```

#### 3. QueryOutputPort (vive en `application`, NO en domain)

```java
// application/fichaPerfil/query/port/out/FichaPerfilQueryOutputPort.java
public interface FichaPerfilQueryOutputPort {
    Optional<FichaPerfilReadModel> findById(UUID id);
}
```

#### 4. QueryUseCase

```java
// application/fichaPerfil/query/ConsultarFichaPerfilQueryUseCase.java
@Component
@RequiredArgsConstructor
public class ConsultarFichaPerfilQueryUseCase implements ConsultarFichaPerfilInputPort {

    private final FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Override
    @Transactional(readOnly = true)
    public FichaPerfilReadModel ejecutar(UUID id) {
        return fichaPerfilQueryOutputPort.findById(id)
            .orElseThrow(() -> new FichaPerfilNoEncontradaException(id.toString()));
    }
}
```

#### 5. QueryInputAdapter

```java
// infrastructure/fichaPerfil/query/adapter/in/web/ConsultarFichaPerfilQueryInputAdapter.java
@RestController
@RequestMapping("/api/fichas-perfil")
@RequiredArgsConstructor
public class ConsultarFichaPerfilQueryInputAdapter {

    private final ConsultarFichaPerfilInputPort consultarFichaPerfilInputPort;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('fichas:fichaPerfil:view')")
    public FichaPerfilReadModel consultar(@PathVariable UUID id) {
        return consultarFichaPerfilInputPort.ejecutar(id);
    }
}
```

#### 6. QueryOutputAdapter

```java
// infrastructure/fichaPerfil/query/adapter/out/persistence/FichaPerfilQueryOutputAdapter.java
@Component
@RequiredArgsConstructor
public class FichaPerfilQueryOutputAdapter implements FichaPerfilQueryOutputPort {

    private final FichaPerfilJpaRepository jpaRepository;
    private final FichaPerfilMapper mapper;

    @Override
    public Optional<FichaPerfilReadModel> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toReadModel);
    }
}
```

> **El read side NO usa el aggregate.** Consulta directa de la JPA Entity al `ReadModel`. No hay invariantes que validar — solo proyección de datos.

---

### {Contexto}GlobalExceptionHandler — Patrón Canónico (uno por contexto, nombre prefijado)

Cada bounded context que **defina excepciones de dominio propias** debe tener su propio handler nombrado con el prefijo del contexto en PascalCase: `{Contexto}GlobalExceptionHandler`. El archivo se ubica en `infrastructure/adapter/in/web/{Contexto}GlobalExceptionHandler.java`. Ahí se mapean las excepciones del dominio a códigos HTTP. **Ninguna excepción de dominio debe caer en `handleGeneral(Exception ex)` (que retorna 500)** — eso indica que falta su `@ExceptionHandler` específico.

> **Por qué el prefijo del contexto:** Spring Boot detecta múltiples `@RestControllerAdvice` con el mismo nombre simple de clase (`GlobalExceptionHandler`) en distintos paquetes y genera conflicto al registrar los beans en runtime, aunque los paquetes sean distintos. Prefijar con el nombre del contexto resuelve el conflicto y mejora la legibilidad en stack traces.

#### Tabla de nombres por contexto

| Contexto Gradle | Nombre del handler | Archivo |
|---|---|---|
| `seguridad` | `SeguridadGlobalExceptionHandler` | `seguridad/.../adapter/in/web/SeguridadGlobalExceptionHandler.java` |
| `fichas` | `FichasGlobalExceptionHandler` | `fichas/.../adapter/in/web/FichasGlobalExceptionHandler.java` |
| `proyectos` | `ProyectosGlobalExceptionHandler` | `proyectos/.../adapter/in/web/ProyectosGlobalExceptionHandler.java` |
| `artefactos` | `ArtefactosGlobalExceptionHandler` | `artefactos/.../adapter/in/web/ArtefactosGlobalExceptionHandler.java` |
| `repositorio_artefactos` | `RepositorioArtefactosGlobalExceptionHandler` | `repositorio_artefactos/.../adapter/in/web/RepositorioArtefactosGlobalExceptionHandler.java` |
| `entregables` | `EntregablesGlobalExceptionHandler` | `entregables/.../adapter/in/web/EntregablesGlobalExceptionHandler.java` |
| `evaluaciones` | `EvaluacionesGlobalExceptionHandler` | `evaluaciones/.../adapter/in/web/EvaluacionesGlobalExceptionHandler.java` |

**Regla de PascalCase:** los contextos con underscore en su nombre Gradle (`repositorio_artefactos`) se convierten a PascalCase eliminando el underscore (`RepositorioArtefactos`).

**Estado actual del proyecto:** solo `seguridad` tiene su handler implementado (como `SeguridadGlobalExceptionHandler`). Los demás contextos **no lo tienen aún** — se crearán cuando aparezca su primera excepción de dominio, ya con el nombre prefijado.

#### Mapeo estándar Excepción → Código HTTP

| Patrón de la excepción / errorCode | Código HTTP |
|---|---|
| `*NoEncontrad*Exception` / errorCode `*_NO_ENCONTRADO` / `*_NOT_FOUND` | 404 |
| `*Invalid*Exception` / errorCode `*_INVALIDO` / `PARAMETRO_*_INVALIDO` | 400 |
| `*NoAutorizad*Exception` / `AccesoDenegadoException` | 403 |
| `*Conflict*Exception` / `*Duplicad*Exception` / errorCode `*_CONFLICTO` / `*_DUPLICADO` | 409 |
| `EstadoInvalidoException` (transición de estado prohibida por regla de negocio) | 409 |
| Resto de `DomainException` (regla de negocio violada que no encaja arriba) | 422 |
| `Exception` genérica (fallback) | 500 |

> **Si una excepción nueva no encaja claramente en ningún patrón**, el implementador debe pausar y preguntar al usuario qué código HTTP corresponde — nunca asumir 500.

#### Plantilla canónica del handler (reducida — solo excepciones de dominio)

> **Importante:** este handler **solo** maneja excepciones de dominio del contexto.
> Las excepciones cross-cutting (Spring Security, validaciones de Jakarta, JSON
> mal formado, fallback `Exception.class`) las maneja `GlobalAppExceptionHandler`
> en `shared:web` con `@Order(LOWEST_PRECEDENCE)`.
>
> **NO incluyas** en este handler:
> - `@ExceptionHandler(Exception.class)` (lo tiene `shared:web`).
> - `@ExceptionHandler(MethodArgumentNotValidException.class)` (lo tiene `shared:web`).
> - `@ExceptionHandler(AccessDeniedException.class)` o `AuthorizationDeniedException` (lo tiene `shared:web`).
> - `@ExceptionHandler(ConstraintViolationException.class)`, `MissingServletRequestParameterException`, `HttpMessageNotReadableException` (lo tiene `shared:web`).
>
> Excepción a la regla: el handler de `seguridad` puede manejar excepciones de su propio dominio relacionadas con autenticación (`InvalidCredentialsException`, `InvalidTokenException`, `AuthenticationException`) → 401. Esas son excepciones de su dominio, no cross-cutting.

```java
package com.arquisoft.

{contexto}.infrastructure.adapter.in.web;

import com.arquisoft.shared.web.ErrorResponseDTO;
import com.arquisoft.{contexto}.domain.exception.{Entidad}NoEncontradaException;
import com.arquisoft.shared.domain.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.arquisoft.{contexto}")
public class {Contexto}

GlobalExceptionHandler {

    @ExceptionHandler({Entidad} NoEncontradaException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoEncontrada (
            {Entidad} NoEncontradaException ex,
    HttpServletRequest request){
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.builder()
                        .error("Not Found")
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .status(HttpStatus.NOT_FOUND.value())
                        .path(request.getRequestURI())
                        .build());
    }

    // Más @ExceptionHandler — uno por cada DomainException específica del contexto, mapeada al código HTTP correcto según la tabla de mapeo.

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponseDTO> handleDomainGeneric (
            DomainException ex,
            HttpServletRequest request){
        log.warn("Regla de negocio violada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponseDTO.builder()
                        .error("Unprocessable Entity")
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                        .path(request.getRequestURI())
                        .build());
    }
}
```

> **Nota:** `@RestControllerAdvice(basePackages = "com.arquisoft.{contexto}")` limita el handler a su contexto. `GlobalAppExceptionHandler` de `shared:web` tiene `@Order(LOWEST_PRECEDENCE)` así que SIEMPRE pierde frente a este si ambos pueden manejar la excepción — los handlers de contexto siempre ganan para excepciones de su dominio.

#### Reglas inviolables

1. **Toda excepción de dominio nueva debe registrarse** en el handler antes de cerrar la capa infrastructure. Si no, cae en el `DomainException` genérico → 422, lo cual puede no ser el código correcto.
2. **Los tests de controller NO deben afirmar 500** para inputs inválidos. Si un test espera 500, indica handler faltante o uso indebido del fallback de `shared:web` — corrige el handler, no el test.
3. **NUNCA incluyas `@ExceptionHandler(Exception.class)` en un handler de contexto.** Eso es responsabilidad exclusiva de `GlobalAppExceptionHandler` en `shared:web`. Si lo añades, capturarás excepciones cross-cutting por accidente y romperás el sistema (ej. `AuthorizationDeniedException` caería como 500 en lugar de 403).

---

## Autorización — Roles realm + Client Roles de Keycloak

> **Convención del proyecto:** los endpoints REST autorizan contra **client roles** (formato `contexto:recurso:accion`) usando `@PreAuthorize("hasAuthority('...')")`. Los **roles realm** (`coordinador`, `asesor-ficha`, etc.) NO se evalúan directamente en endpoints — se mantienen como agrupación lógica de permisos en Keycloak, y cada rol realm tiene asignados sus client roles correspondientes.

### Estructura del JWT

El JWT que llega del frontend contiene:

```json
{
  "realm_access": {
    "roles": ["coordinador"]
  },
  "resource_access": {
    "arquisoft-api": {
      "roles": ["fichas:ficha:view", "fichas:ficha:create"]
    }
  }
}
```

- `realm_access.roles` → roles realm (kebab-case): `coordinador`, `asesor`, `asesor-ficha`, etc.
- `resource_access.arquisoft-api.roles` → **client roles** del cliente `arquisoft-api`. Estos son los que `hasAuthority(...)` evalúa.

### Convención de client roles: `contexto:recurso:accion`

Cada client role declara qué acción se puede realizar sobre qué recurso de qué contexto:

| Componente | Significado | Ejemplos |
|---|---|---|
| `contexto` | Bounded context Gradle | `fichas`, `proyectos`, `entregables`, `evaluaciones`, `repositorio_artefactos`, `artefactos`, `seguridad` |
| `recurso` | Entidad o recurso del contexto | `ficha`, `proyecto`, `entregable`, `evaluacion`, `usuario` |
| `accion` | Verbo CRUD o acción de negocio | `view`, `create`, `update`, `delete`, `approve`, `submit`, `evaluate` |

**Ejemplos válidos:**
- `fichas:ficha:view` → ver fichas de perfil
- `fichas:ficha:create` → crear fichas de perfil
- `proyectos:proyecto:approve` → aprobar proyecto de grado
- `evaluaciones:evaluacion:submit` → enviar evaluación

### Mapeo realm role → client roles

Cada **rol realm** tiene asignados varios **client roles** según sus responsabilidades. Un mismo client role puede pertenecer a varios roles realm:

| Client role | Roles realm que lo poseen | Razón |
|---|---|---|
| `fichas:ficha:view` | `coordinador`, `asesor-ficha`, `representante-comite` | Todos pueden consultar fichas |
| `fichas:ficha:create` | `asesor-ficha` | Solo asesor-ficha crea fichas |
| `fichas:ficha:approve` | `representante-comite` | Solo el comité aprueba |

> **El planificador es responsable de declarar este mapeo** en cada plan: por cada endpoint/acción, lista el client role nuevo y a qué roles realm debe asignarse en Keycloak. Esto permite al equipo de seguridad configurar Keycloak en paralelo con el desarrollo.

### Uso en controllers

```java
// ✅ CORRECTO — usa hasAuthority con client role
@PostMapping
@PreAuthorize("hasAuthority('fichas:ficha:create')")
public ResponseEntity<FichaResponseDTO> crear(@Valid @RequestBody CrearFichaRequestDTO req) {
    // ...
}

// ❌ MAL — hasRole con realm role (convención antigua)
@PreAuthorize("hasRole('ASESOR_FICHA')")  // ❌
@PreAuthorize("hasRole('asesor-ficha')")  // ❌

// ❌ MAL — múltiples authorities con OR (preferir uno solo y asignarlo a varios roles realm en Keycloak)
@PreAuthorize("hasAuthority('fichas:ficha:view') or hasAuthority('fichas:ficha:admin')")  // ❌
```

### Reglas de uso

1. **Cada endpoint tiene exactamente un `@PreAuthorize("hasAuthority('...')")`** con un único client role.
2. **El nombre del client role refleja el contexto, recurso y acción.** No usar nombres genéricos como `admin`, `read`, `write`.
3. **Si dos roles realm distintos pueden ejecutar la misma acción**, ambos tendrán asignado el mismo client role en Keycloak — NO se hace OR en el endpoint.
4. **El planificador documenta los client roles nuevos** en cada plan (sección de seguridad) con su mapeo a roles realm.
5. **Tests de controller** mockean los authorities con `with(jwt().authorities(new SimpleGrantedAuthority("fichas:ficha:create")))` — no roles realm.

---

## Almacenamiento — MinIO con presigned URLs

> **Patrón:** el cliente habla **directo** con MinIO. El backend solo genera URLs firmadas temporales. Nunca proxy de bytes — eso ahorra ancho de banda y CPU.

### Flujo (upload)

```
1. Cliente → backend:  GET /api/{entidad}/upload-url?key=...     ← solicita URL firmada
2. Backend → MinIO:    generatePresignedUrl(PUT, key, 15 min)     ← SDK
3. Backend → cliente:  { url: "http://minio/.../?X-Amz-Signature=..." }
4. Cliente → MinIO:    PUT con el archivo                         ← directo, sin pasar por backend
```

Mismo patrón para descarga (`GET` firmado).

### Interfaz `MinioStorageClient` (en `shared:minio`)

```java
public interface MinioStorageClient {
    String generateUploadPresignedUrl(String bucket, String objectKey);    // PUT, 15 min
    String generateDownloadPresignedUrl(String bucket, String objectKey);  // GET, 15 min
    boolean objectExists(String bucket, String objectKey);
    void deleteObject(String bucket, String objectKey);
}
```

La implementación `MinioStorageClientImpl` (en `shared:minio`) usa el SDK `io.minio:minio` y:
- Crea el bucket automáticamente al primer `generateUploadPresignedUrl` si no existe.
- Envuelve toda excepción del SDK en `MinioOperationException` (extiende `InfrastructureException` → HTTP 503).

### Estructura de almacenamiento

| Concepto | Convención | Ejemplo |
|---|---|---|
| Bucket | Uno por contexto: `arquisoft-{contexto}` | `arquisoft-fichas`, `arquisoft-entregables` |
| Object key | Ruta lógica con `/` simulando jerarquía | `documentos/2025/proyecto-123/informe.pdf` |
| Expiración URL | 15 minutos (configurable) | upload + download |

> MinIO es **flat storage** — no hay carpetas reales. Los `/` en el key simulan jerarquía para herramientas de exploración. El **backend** define la estructura de keys; el cliente no elige rutas arbitrariamente.

### Configuración (`application.yml`)

```yaml
minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
  access-key: ${MINIO_ACCESS_KEY:minioadmin}
  secret-key: ${MINIO_SECRET_KEY:minioadmin}
  presigned-url-expiry:
    upload-minutes: 15
    download-minutes: 15
  trust-self-signed-certificates: false   # true solo en dev
```

### Uso desde un contexto

`MinioStorageClient` es una **interfaz técnica de `shared:minio`**. Los casos de uso del contexto la inyectan **directamente** — no se crea un puerto adicional por contexto (excepción a la regla puerto/adaptador, ya documentada en la sección Puerto/Adaptador).

```java
// application/{entidad}/command/SubirDocumentoUseCase.java
@Component
@RequiredArgsConstructor
public class SubirDocumentoUseCase implements SubirDocumentoInputPort {

    private final MinioStorageClient minioStorageClient;  // inyección directa desde shared:minio

    @Override
    public String ejecutar(SubirDocumentoCommand command) {
        String objectKey = "documentos/" + command.proyectoId() + "/" + command.nombreArchivo();
        return minioStorageClient.generateUploadPresignedUrl(
            "arquisoft-" + command.contexto(), objectKey);
    }
}
```

### Reglas inviolables

1. **El backend NUNCA recibe bytes del archivo.** Solo genera URL firmada y la devuelve al cliente.
2. **El backend define el `objectKey`**, nunca lo acepta del cliente sin sanitizar — un cliente malicioso podría usar `../../` para sobrescribir archivos de otros recursos.
3. **Un bucket por contexto** (`arquisoft-fichas`, `arquisoft-entregables`, etc.). No mezclar archivos de bounded contexts distintos.
4. **`MinioStorageClient` solo se inyecta en use cases** que necesitan storage. Nunca en domain ni en controllers directamente.
5. **`trust-self-signed-certificates: true` solo en dev.** En producción siempre `false` (validación SSL normal con CA reconocida).

---

## Eventos asíncronos — RabbitMQ (publicación y consumo)

### Topología

```
Exchange "arquisoft.events" (TopicExchange, durable)
  │
  ├── routing key "fichas.fichaPerfil.creada"
  │     ├── Queue "proyectos.fichas.fichaPerfil.creada" (durable + DLX)
  │     └── Queue "evaluaciones.fichas.fichaPerfil.creada" (durable + DLX)
  │
  └── ... otras routing keys
```

| Recurso | Convención | Ejemplo |
|---|---|---|
| Exchange único | `arquisoft.events` (Topic, durable) | — |
| Routing key | `{contexto}.{entidad}.{accion}` (la constante `EVENT_TOPIC` del evento) | `fichas.fichaPerfil.creada` |
| Cola por consumidor | `{contextoConsumidor}.{eventTopic}` durable | `proyectos.fichas.fichaPerfil.creada` |
| DLX | `arquisoft.dlx` + routing key `{queue}.dead` | — |

### Publicación — `RabbitMQEventPublisher` (en `shared:amqp`)

Ya existe. Implementa `EventPublisher` de `shared:domain.events`. Tras `usuario.drainUnPublishedEvents().forEach(eventPublisher::publish)`, el publisher:

1. Usa `event.getEventTopic()` como routing key sobre `arquisoft.events`.
2. **Publisher Confirms** habilitados — si el broker rechaza, loguea el `correlationId`.
3. **Reintento con backoff exponencial:** 3 intentos (500ms → 1s → 2s) **solo** ante `AmqpException` (error de conectividad). Errores de negocio NO se reintentan.
4. **Propaga headers de trazabilidad** desde el MDC del request HTTP:
   - `X-Trace-Id` (correlación de request → eventos → side-effects)
   - `X-User-Id` (auditoría de quién originó el evento)

### Consumo — `AbstractEventConsumer` (en `shared:amqp`)

Clase base que encapsula 3 responsabilidades transversales del consumo:

```java
@Component
public class FichaPerfilCreadaConsumerInputAdapter extends AbstractEventConsumer {

    private final RegistrarFichaPerfilLocalInputPort registrarFichaPerfilLocalInputPort;

    @RabbitListener(queues = ProyectosFichaPerfilQueueConfig.QUEUE_NAME)
    public void onFichaPerfilCreada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, () -> {
            // 1. Deserializar payload (record local, NO importa el evento del publicador)
            FichaPerfilCreadaPayload payload = deserialize(message, FichaPerfilCreadaPayload.class);
            // 2. Mapear a Command y ejecutar el caso de uso
            registrarFichaPerfilLocalInputPort.ejecutar(new RegistrarFichaPerfilLocalCommand(
                UUID.fromString(payload.aggregateId()), payload.tituloProyecto()));
        });
    }
}
```

`withCorrelation(message, channel, runnable)` hace:

1. **Lee headers** `X-Trace-Id` y `X-User-Id` del mensaje y los pone en el MDC.
2. **Ejecuta el `runnable`** (lógica del consumer).
3. **ACK manual** (`basicAck`) si la lógica termina sin excepción.
4. **NACK manual** (`basicNack(requeue=false)`) si lanza excepción → mensaje al DLX (no se re-encola en loop infinito).
5. **Limpia MDC** en `finally` (compatible con Virtual Threads).

### Payload local — cero acoplamiento entre contextos

> **Regla crítica:** el contexto consumidor **NO importa la clase del evento del contexto publicador**. Declara un `record` propio con los campos que necesita.

```java
// proyectos/infrastructure/fichaPerfil/command/adapter/in/amqp/FichaPerfilCreadaPayload.java
public record FichaPerfilCreadaPayload(
    String aggregateId,
    String tituloProyecto,
    String asesorFichaId
) {}
```

Esto significa que `proyectos` **no depende** de `fichas:domain`. Si `fichas` cambia internamente su evento añadiendo campos nuevos, `proyectos` ignora los que no necesita. Aislamiento estricto entre bounded contexts.

### Configuración de cola + binding + DLX

Cada contexto consumidor declara su `@Configuration` en `infrastructure/config/`:

```java
@Configuration
public class ProyectosFichaPerfilQueueConfig {

    public static final String QUEUE_NAME = "proyectos.fichas.fichaPerfil.creada";

    @Bean
    public Queue proyectosFichaPerfilCreadaQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
            .withArgument("x-dead-letter-exchange", "arquisoft.dlx")
            .withArgument("x-dead-letter-routing-key", QUEUE_NAME + ".dead")
            .build();
    }

    @Bean
    public Binding binding(Queue proyectosFichaPerfilCreadaQueue,
                            TopicExchange arquisoftEventsExchange) {
        return BindingBuilder.bind(proyectosFichaPerfilCreadaQueue)
            .to(arquisoftEventsExchange)
            .with(FichaPerfilCreadaEvent.EVENT_TOPIC);
    }
}
```

> El routing key del binding usa la **constante `EVENT_TOPIC`** del evento, no un string literal. Si el contexto consumidor no quiere depender del módulo `fichas:domain`, duplica la constante localmente o usa el string literal documentado.

### Resumen de responsabilidades

| Pieza | Capa | Responsabilidad |
|---|---|---|
| Decide qué evento emitir | `domain/{entidad}/aggregate/` | Solo el aggregate decide qué pasó |
| Acumula evento en memoria | `domain/{entidad}/aggregate/` | `publishEvent(...)` desde el factory |
| Drena y publica | `application/{entidad}/command/` | `drainUnPublishedEvents().forEach(publish)` tras persistir |
| Publica a RabbitMQ | `shared:amqp` (`RabbitMQEventPublisher`) | Routing key, reintentos, headers |
| Configura cola + DLX | `{contextoConsumidor}/infrastructure/config/` | `@Configuration` con Queue + Binding |
| Consume el mensaje | `{contextoConsumidor}/infrastructure/{entidad}/command/adapter/in/amqp/` | Extiende `AbstractEventConsumer` |
| Payload del consumidor | mismo paquete del consumer | `record` local, **NO** importa el evento del publicador |

### Reglas inviolables

1. **`AggregateRoot` solo acumula eventos en memoria.** Nunca conoce `EventPublisher`.
2. **El UseCase write drena con `drainUnPublishedEvents()`** (un solo método que retorna + limpia atómico). Nunca itera el aggregate manualmente.
3. **El consumidor declara su propio `record` payload.** Nunca importa la clase del evento del publicador (cero acoplamiento entre contextos).
4. **Toda cola tiene DLX configurado** (`x-dead-letter-exchange`). Sin DLX, mensajes en error se re-encolan eternamente.
5. **El consumer extiende `AbstractEventConsumer`** y usa `withCorrelation(message, channel, runnable)`. No implementa ACK/NACK manualmente.
6. **Deuda técnica conocida (a resolver con Outbox Pattern):** `save()` y `publish()` no son atómicos. Si el broker falla tras persistir, el evento se pierde. Es responsabilidad del implementador documentarlo si la HU tiene tolerancia cero a eventos perdidos.

---



## Manejo de errores externos en `seguridad` (Keycloak)

Una lección aprendida: el `SeguridadGlobalExceptionHandler` debe **distinguir entre fallos de credenciales del usuario y fallos de infraestructura externa** (Keycloak). Mezclarlos es un bug de seguridad.

### Tabla de mapeo correcto

| Situación | Código HTTP | Razón |
|---|---|---|
| Credenciales incorrectas (login) | `401 Unauthorized` | El usuario falló la autenticación |
| Refresh token inválido / expirado | `401 Unauthorized` | El token del usuario no sirve |
| Keycloak caído / timeout | `503 Service Unavailable` | Problema del servicio externo, no del usuario |
| Refresh con Keycloak caído | `503 Service Unavailable` | Idem |
| Error inesperado en `/auth/*` (NPE, casts, bugs) | `500 Internal Server Error` | Bug del backend, NO del usuario |

### Anti-patrón resuelto

**ANTES** (vulnerabilidad): el handler de `seguridad` capturaba todo error de Keycloak (timeout, conexión rechazada, errores HTTP del Keycloak) y lo retornaba como `401 Unauthorized` con un mensaje que **incluía la URL de Keycloak** en el cuerpo. Esto exponía infraestructura interna al cliente.

**DESPUÉS** (correcto): el handler distingue:

```java
// En SeguridadGlobalExceptionHandler

@ExceptionHandler(InvalidCredentialsException.class)
public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(...) {
    // 401 — fallo legítimo del usuario
}

@ExceptionHandler(InvalidTokenException.class)
public ResponseEntity<ErrorResponseDTO> handleInvalidToken(...) {
    // 401 — el token del usuario no sirve
}

// ResourceAccessException (Keycloak caído / timeout) ya está cubierto por
// GlobalAppExceptionHandler en shared:web → 503. NO duplicar aquí.

// NO hay @ExceptionHandler(Exception.class) en SeguridadGlobalExceptionHandler.
// El fallback Exception → 500 vive en GlobalAppExceptionHandler de shared:web.
```

### Reglas inviolables

1. **El handler de `seguridad` NO captura `Exception.class`** — el fallback genérico vive en `shared:web`.
2. **El handler de `seguridad` NO captura `ResourceAccessException` ni `HttpClientErrorException` propios de RestTemplate/WebClient hacia Keycloak** — eso lo hace `GlobalAppExceptionHandler` de `shared:web` con código 503.
3. **El mensaje de error NUNCA expone la URL del Keycloak**, ni stack traces, ni nombres de hosts internos. Mensajes genéricos de cara al cliente.
4. **El log interno** sí registra el error completo con `log.error("...", ex)` para diagnóstico, pero el `ErrorResponseDTO` que se retorna al cliente es genérico.
5. **Un error 500 NUNCA se disfraza de 401.** Si la causa es interna del backend, retorna 500. Si es Keycloak externo caído, retorna 503. Solo retorna 401 si el usuario falló credenciales o token.

---

## Mapeo Contexto Gradle → Base de Datos PostgreSQL

Cada bounded context tiene **su propia base de datos PostgreSQL independiente**.
No hay schemas dentro de una BD compartida — son BDs distintas, una por contexto.
Todas las BDs viven en el mismo servidor PostgreSQL pero son aisladas entre sí.

| Contexto Gradle | Base de Datos PostgreSQL |
|---|---|
| `seguridad` | `usuarios` |
| `fichas` | `fichas_perfil` |
| `proyectos` | `proyectos_grado` |
| `artefactos` | `artefactos` |
| `repositorio_artefactos` | `repositorio_artefactos` |
| `entregables` | `entregables` |
| `evaluaciones` | `evaluaciones` |

### Configuración del DataSource (ya existe, NO se crea por HU)

Cada contexto tiene una clase `{Contexto}DataSourceConfig` ya creada y funcional en
`infrastructure/config/`. Esta clase configura el `DataSource`, `EntityManagerFactory`
y `TransactionManager` apuntando a la BD de ese contexto. **No se crea ni modifica
en HUs nuevas** — es infraestructura ya establecida.

La URL, usuario y password de cada BD están parametrizados en `application.yml`
por contexto. El implementador **no toca configuración de DataSource**.

### Reglas para JPA y Flyway

- **`@Table` SIN atributo `schema`.** Todas las tablas viven en el `public` de su BD propia.
  Usa solo `@Table(name = "ficha_perfil")`. **Nunca** `@Table(schema = "...", name = "...")`.
- **Migraciones Flyway** en `{contexto}/infrastructure/src/main/resources/db/migration/`.
- **Nomenclatura Flyway:** `V{n}__{descripcion}.sql` (ej. `V1.0__crear_tabla_ficha_perfil.sql`).
- **El SQL referencia tablas sin prefijo de schema** (ej. `CREATE TABLE ficha_perfil (...)`,
  no `CREATE TABLE fichas_perfil.ficha (...)`).
- **NO hay FKs cruzadas entre BDs.** Cada contexto es totalmente autónomo a nivel de datos.

### Réplicas locales de entidades de otros contextos

Cuando un contexto necesita información de otro (ej. `fichas_perfil` necesita conocer
`nombre`, `identificador` y `email` de un estudiante que vive en `usuarios`), **modela
una entidad propia con esos atributos denormalizados** dentro de su BD.

Ejemplo: en la BD `fichas_perfil` existe una tabla `estudiante` con columnas
`id`, `identificador`, `nombre`, `email` — **réplica local**, no FK a `usuarios.estudiante`.
La consistencia entre la copia local y la fuente real **no es preocupación del implementador
de la HU** — se gestiona fuera del contexto. El implementador trata la tabla local como
parte natural de su contexto.

### Convención de nombres de tablas

- `snake_case` para tablas y columnas (ej. `ficha_perfil`, `tipo_item`, `fecha_actualizacion`).
- Nombre de tabla = nombre de entidad de dominio en `snake_case`. La entidad `FichaPerfil`
  mapea a la tabla `ficha_perfil`.

---

## Nomenclatura (Regla Bilingüe)

| Elemento | Convención | Ejemplo |
|---|---|---|
| Paquete de contexto | español, minúsculas | `fichas`, `proyectos` |
| Paquete estructural | inglés | `domain`, `application`, `infrastructure` |
| Término de negocio | español | `ProyectoGrado`, `crearFicha`, `EstadoFicha` |
| Sufijo técnico | inglés | `UseCase`, `Port`, `DTO`, `Adapter`, `Config` |
| Clase | PascalCase | `CrearFichaUseCase` |
| Interfaz (puerto) | PascalCase, sin prefijo `I` | `FichaRepositoryPort` |
| Implementación / Adaptador | sufijo `InputAdapter`, `OutputAdapter`, `UseCase` | `FichaPerfilCommandOutputAdapter`, `CrearFichaPerfilUseCase` |
| DTO | sufijo `DTO` | `CrearFichaRequestDTO`, `FichaResponseDTO` |
| Excepción | sufijo `Exception` | `FichaNoEncontradaException` |
| Evento de dominio | sufijo `Event` | `FichaCreadaEvent`, `ProyectoFinalizadoEvent` |
| Enum | PascalCase, valores `SCREAMING_SNAKE_CASE` | `EstadoFicha.EN_REVISION` |
| Método de negocio | camelCase, verbo primero | `crearFicha`, `obtenerPorId` |
| Método de test | `debeAlgo_cuandoCondicion` | `debeCrearFicha_cuandoDatosValidos` |
| Commit | `feat({contexto}): ...` | `feat(fichas): crear ficha perfil` |

### IDs

- **Siempre `UUID`** (`java.util.UUID`). **Nunca** `Long`, `Integer` o autoincrementales de BD.
- `build(...)` genera el UUID con `UUID.randomUUID()`.
- `rebuild(...)` recibe el UUID desde persistencia.

### Imports

- **Siempre explícitos**, nunca wildcard `*`.
- Orden: proyecto → Jakarta → Lombok → Spring → Java stdlib.

### Inyección de dependencias

- **Siempre** por constructor con `@RequiredArgsConstructor` (Lombok).
- **Nunca** `@Autowired` en campos.
- Inyectar interfaces (puertos), nunca implementaciones concretas.

---

## Configuraciones Clave

### Virtual Threads (ADR-008)

- Habilitados globalmente en `application.yml`: `spring.threads.virtual.enabled: true`.
- Aplica automáticamente a Tomcat, `@Async` y listeners de RabbitMQ.
- **Prohibido** declarar `@Bean TaskExecutor` o thread pools manuales salvo que el plan lo indique.

### Eventos RabbitMQ

- Exchange único: `arquisoft.events` (Topic Exchange).
- Routing keys: `{contexto}.{entidad}.{accion}` (ej. `fichas.ficha.creada`).
- Cada contexto declara sus propias queues y bindings en `{contexto}/infrastructure/config/RabbitMQ{Entidad}Config.java`.
- Puerto: `EventPublisher` de `shared:amqp`.

### Swagger / OpenAPI (ADR-011)

- `OpenApiConfig.java` global en `src/main/java/com/arquisoft/config/` (raíz de la app). **No duplicar** en módulos.
- Todo `@RestController` debe tener: `@Tag` (clase), `@Operation` + `@ApiResponses` (cada método).
- Endpoints protegidos: `@SecurityRequirement(name = "bearerAuth")`.
- Endpoints públicos (login, refresh, validate): omitir `@SecurityRequirement`.
- URL dev: `http://localhost:8080/api/swagger-ui/index.html`.

### Seguridad

- JWT decodificado vía JWK Set URI de Keycloak.
- `@EnableMethodSecurity(prePostEnabled = true)` → usar `@PreAuthorize("hasAuthority('contexto:recurso:accion')")` en controllers (ver sección "Autorización con client roles" más abajo).
- **NO** se usa `hasRole('NOMBRE_ROL')` — los roles realm de Keycloak (`coordinador`, `asesor-ficha`, etc.) **no se evalúan directamente en endpoints**; en su lugar, cada rol tiene asignados unos *client roles* (formato `contexto:recurso:accion`) y la autorización del endpoint se hace contra esos client roles vía `hasAuthority(...)`.
- Endpoints públicos permit-all: `/auth/login`, `/auth/refresh`, `/auth/validate`, `/actuator/health/**`, `/swagger-ui/**`, `/v3/api-docs/**`.
- CSRF deshabilitado, sesiones stateless.
- Rate limiting (Bucket4j) habilitado en prod: 60/min global, 3/min login.

### Logging

- `@Slf4j` en clases que loguean.
- `log.warn()` para respuestas 4xx, `log.error()` para 5xx.

---

## Comandos Gradle Útiles

```bash
# Compilar todo (sin tests)
./gradlew build -x test

# Compilar un contexto
./gradlew :{contexto}:build -x test

# Compilar una capa específica
./gradlew :{contexto}:domain:compileJava
./gradlew :{contexto}:application:compileJava
./gradlew :{contexto}:infrastructure:compileJava

# Tests
./gradlew test                               # todos
./gradlew :{contexto}:test                   # de un contexto
./gradlew :{contexto}:{capa}:test            # de una capa
./gradlew test --tests "*.MiClaseTest.miMetodo"

# Cobertura
./gradlew jacocoTestReport
./gradlew :{contexto}:jacocoTestReport

# Listar los 28 subproyectos
./gradlew projects
```

**Siempre `./gradlew`**, nunca `mvn` ni `javac` directo.

---

## Interacción con otros skills

Este skill **no reemplaza** a los otros dos skills del proyecto, los complementa:

| Skill | Uso | Invocado por |
|---|---|---|
| `arquisoft-context` (este) | Contexto autoritativo del proyecto | Los 4 subagentes, al inicio |
| `gh-docs-reader` | Consulta HUs, Event Storming y ADRs en el repo `arquisoft-docs` | `@planificador` |
| `context7-stack` | Verifica APIs actualizadas del stack (Spring, JPA, Mockito, etc.) | `@implementador`, `@tester` |

Orden típico de consulta por subagente:

- **`@planificador`:** `arquisoft-context` → `gh-docs-reader` → preguntas → plan.
- **`@implementador`:** `arquisoft-context` → `context7-stack` (antes de cada archivo) → código.
- **`@tester`:** `arquisoft-context` → `context7-stack` (antes de cada capa de tests) → tests.
- **`@validator`:** `arquisoft-context` → lectura de plan y código → reporte.

---

## Reglas Invariantes del Skill

1. Este skill es la **única** fuente de verdad del estado del proyecto para los subagentes.
2. Si un subagente detecta una contradicción entre este skill y otro archivo del repositorio, **gana este skill** y reporta la discrepancia al usuario.
3. **DDD estricto por capas:** el dominio es Java puro (sin Spring, JPA, Lombok, Jackson, RabbitMQ, Keycloak, Swagger). La aplicación solo conoce dominio + librerías permitidas. La infraestructura es la única que habla con tecnologías externas.
4. **Cero reglas de negocio en `infrastructure/`:** si una decisión tiene sentido de negocio, vive en `domain/`. Los `@Configuration` solo cablean; los adaptadores solo traducen.
5. **Patrón puerto/adaptador obligatorio** para toda integración externa (Keycloak, RabbitMQ, Redis, servicios HTTP, SMTP, S3). Puerto en `domain/port/out/`, adaptador en `infrastructure/adapter/out/{tipo}/`.
6. Toda entidad raíz en los 6 contextos de negocio **DEBE** extender `AggregateRoot`. La única excepción documentada es `seguridad`.
7. Los IDs son **siempre `UUID`**. Cualquier uso de `Long`/`Integer` como ID es un error.
8. La dirección de dependencias `domain ← application ← infrastructure` **no se negocia**.
9. Los bounded contexts **no se importan entre sí** — solo eventos RabbitMQ.
10. Versiones del stack: **no inventar**. Usar exactamente las de este skill.
11. Features de Java 21: **balanceado**. Aplicar cuando aporte claridad (records para VO y payloads de eventos, sealed para estados, text blocks para SQL, var con tipo evidente). No forzar en entidades de dominio (requieren constructor privado + factories).
12. Virtual Threads están gestionados por Spring Boot — **nunca** crear `TaskExecutor` manual.
13. **Prueba del algodón:** antes de escribir cualquier archivo, pregúntate "¿si mañana cambio esta tecnología externa, qué archivos tengo que tocar?". Si la respuesta incluye `domain/`, la lógica está mal ubicada.