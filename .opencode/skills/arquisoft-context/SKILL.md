---
name: arquisoft-context
description:
   Contexto autoritativo del proyecto Arquisoft Backend para subagentes. Carga SIEMPRE antes de planificar, implementar, testear o validar cualquier Historia de Usuario o Técnica. Contiene el stack exacto verificado, las convenciones de arquitectura hexagonal + DDD, la regla estricta de AggregateRoot, el mapeo contexto → schema PostgreSQL, la guía de uso de features de Java 21 y las plantillas de código canónicas. Este skill es la ÚNICA fuente de verdad para el estado del proyecto — no leer AGENTS.md, README.md, QUICK_START.md, ARQUITECTURA_*.md ni docs/ del repositorio.
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
- Mapeo contexto → schema PostgreSQL (sección "Mapeo Contexto → Schema")
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

### Bounded Contexts (7)

| Contexto Gradle | GroupId base | ¿Usa AggregateRoot? |
|---|---|---|
| `seguridad` | `com.arquisoft.seguridad` | ❌ No (transversal, delega en Keycloak) |
| `fichas` | `com.arquisoft.fichas` | ✅ Sí |
| `proyectos` | `com.arquisoft.proyectos` | ✅ Sí |
| `artefactos` | `com.arquisoft.artefactos` | ✅ Sí |
| `repositorio_artefactos` | `com.arquisoft.repositorio_artefactos` | ✅ Sí |
| `entregables` | `com.arquisoft.entregables` | ✅ Sí |
| `evaluaciones` | `com.arquisoft.evaluaciones` | ✅ Sí |

### Módulos shared (7)

| Módulo | Paquete base | Clases/Interfaces clave |
|---|---|---|
| `shared:domain` | `com.arquisoft.shared.domain` | `AggregateRoot`, `DomainEvent` |
| `shared:exceptions` | `com.arquisoft.shared.exceptions` | `DomainException`, `GlobalExceptionHandler` |
| `shared:amqp` | `com.arquisoft.shared.amqp` | `EventPublisher` (interfaz) |
| `shared:postgres` | `com.arquisoft.shared.postgres` | `BaseRepository` (JPA) |
| `shared:redis` | `com.arquisoft.shared.redis` | `RedisClient` |
| `shared:web` | `com.arquisoft.shared.web` | `HttpClient`, filtros HTTP |
| `shared:validation` | `com.arquisoft.shared.validation` | `@ValidEmail` y validadores |

### Estructura estándar por contexto

```
{contexto}/
├── domain/
│   ├── model/         # Entidades (inmutables, build/rebuild) + Value Objects + Enums
│   ├── event/         # Eventos de dominio (extends DomainEvent)  ← CRÍTICO para DDD
│   ├── port/
│   │   ├── in/        # Casos de uso: {Accion}{Entidad}UseCase
│   │   └── out/       # Repositorios: {Entidad}RepositoryPort
│   └── exception/     # Excepciones de dominio (extends DomainException)
├── application/
│   ├── dto/           # DTOs con toDomain() / fromDomain()
│   └── usecase/       # Impl: {Accion}{Entidad}UseCaseImpl
└── infrastructure/
    ├── adapter/
    │   ├── in/
    │   │   ├── web/                 # Controllers REST + componentes web globales
    │   │   │   ├── {Entidad}Controller.java
    │   │   │   └── GlobalExceptionHandler.java   # @RestControllerAdvice (si aplica)
    │   │   └── messaging/           # EventListeners RabbitMQ (si el contexto escucha eventos)
    │   │       └── {Entidad}EventListener.java
    │   └── out/
    │       ├── persistence/         # JPA Entity, JpaRepository, RepositoryAdapter
    │       ├── messaging/           # EventPublisher RabbitMQ
    │       └── {tipo}/              # security/, storage/, notification/, etc. (si aplica)
    ├── config/                      # @Configuration (sufijo Config) — solo cablea, sin lógica
    ├── filter/                      # Filtros HTTP (sufijo Filter)
    └── resources/db/migration/      # Flyway V{n}__{descripcion}.sql
```

### Convenciones de subcarpetas en adaptadores

**Subcarpetas SIEMPRE obligatorias** (aunque solo haya un tipo de adaptador):

- `adapter/in/web/` — para todo lo que sirve a la capa REST: controllers (`@RestController`), advice global (`@RestControllerAdvice`), interceptores de petición que solo aplican a endpoints REST.
- `adapter/in/messaging/` — para listeners de RabbitMQ (`@RabbitListener`). Solo se crea si el contexto consume eventos de otros bounded contexts.
- `adapter/out/persistence/` — para JPA Entity, JpaRepository, RepositoryAdapter.
- `adapter/out/messaging/` — para publishers de RabbitMQ (`EventPublisher` impl).
- `adapter/out/{tipo}/` — para otras integraciones externas (ej. `security/` para Keycloak admin client, `storage/` para S3, `notification/` para SMTP). Una subcarpeta por tipo de integración.

La consistencia (siempre subcarpeta) prevalece sobre la simplicidad (carpeta plana cuando solo hay un tipo). Esto facilita que cualquier nuevo tipo de adaptador encuentre su lugar sin reorganizaciones.

### Componentes web especiales — dónde van

| Componente | Ubicación | Tipo Spring |
|---|---|---|
| `{Entidad}Controller` | `adapter/in/web/` | `@RestController` |
| `GlobalExceptionHandler` | `adapter/in/web/` | `@RestControllerAdvice` |
| Interceptores REST específicos del contexto | `adapter/in/web/` | clase con `HandlerInterceptor` |
| Filtros HTTP (cross-cutting, ej. rate limit) | `infrastructure/filter/` | `@Component` con `Filter` |
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
| Reglas de negocio | `domain/model/` | "una `Ficha` nace en BORRADOR", "solo ASESOR_FICHA puede aprobar" |
| Invariantes de entidad | `domain/model/` | validaciones en constructor, transiciones de estado |
| Definición de Value Objects | `domain/model/` | `Calificacion`, `Rol`, `Email` (records con validación) |
| Definición de eventos de dominio | `domain/event/` | `FichaCreadaEvent extends DomainEvent` |
| Contratos con el exterior (abstractos) | `domain/port/out/` | `FichaRepositoryPort`, `TokenAuthoritiesPort` |
| Casos de uso (qué se invoca y en qué orden) | `application/usecase/` | "persistir → drenar eventos → publicar → limpiar" |
| DTOs de entrada/salida | `application/dto/` | `CrearFichaRequestDTO`, `FichaResponseDTO` |
| Controllers REST | `infrastructure/adapter/in/web/` | `FichaController` (`@RestController`) |
| Advice global de errores web | `infrastructure/adapter/in/web/` | `GlobalExceptionHandler` (`@RestControllerAdvice`) |
| Listeners RabbitMQ | `infrastructure/adapter/in/messaging/` | `FichaEventListener` (`@RabbitListener`) |
| Implementaciones concretas de puertos out | `infrastructure/adapter/out/{tipo}/` | `FichaRepositoryAdapter` (en `persistence/`), `KeycloakAuthoritiesAdapter` (en `security/`) |
| Detalles de frameworks | `infrastructure/` | JPA `@Entity`, Spring `@Configuration`, RabbitMQ `@RabbitListener` |
| Configuración técnica (cableado) | `infrastructure/config/` | `SecurityConfig`, `RabbitMQConfig`, `OpenApiConfig` (sin lógica de negocio) |
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
y clases del propio proyecto (`com.arquisoft.{contexto}.domain.*`, `com.arquisoft.shared.domain.*`,
`com.arquisoft.shared.exceptions.*`).

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

Toda integración con un sistema externo (Keycloak, RabbitMQ, Redis, servicios HTTP, SMTP, S3…)
sigue este patrón sin excepción:

```
┌────────────────────────────────────────────────────────────┐
│ domain/model/                                              │
│   Rol.java, Usuario.java (entidades y VOs puros)           │
└────────────────────────────────────────────────────────────┘
                        ▲
                        │ usa
┌────────────────────────────────────────────────────────────┐
│ domain/port/out/                                           │
│   TokenAuthoritiesPort.java                                │
│   interface TokenAuthoritiesPort {                         │
│       List<Rol> extraerRoles(Object token);                │
│   }                                                        │
└────────────────────────────────────────────────────────────┘
                        ▲
                        │ implementa
┌────────────────────────────────────────────────────────────┐
│ infrastructure/adapter/out/security/                       │
│   KeycloakAuthoritiesAdapter.java                          │
│   @Component                                               │
│   class KeycloakAuthoritiesAdapter                         │
│           implements TokenAuthoritiesPort {                │
│       // aquí sí se lee "realm_access.roles"               │
│       // aquí sí se usan clases org.springframework.*      │
│   }                                                        │
└────────────────────────────────────────────────────────────┘
```

- El **dominio** no menciona Keycloak jamás.
- El **puerto** define qué necesita el dominio en términos del dominio (`List<Rol>`).
- El **adaptador** traduce entre el mundo externo y el dominio.

Consecuencias:
- Cambiar Keycloak por Auth0 = escribir un `Auth0AuthoritiesAdapter` nuevo. Cero cambios en dominio o aplicación.
- Tests del dominio = Java puro, sin mocks de Spring.
- Tests del adaptador = mock de la librería externa.

---

### Ejemplo Corregido — Roles de Keycloak (el caso `KeycloakJwtConverter`)

#### ❌ Anti-patrón (lo que había antes, mezclado)

```java
// infrastructure/config/KeycloakJwtConverterConfig.java
@Configuration
public class KeycloakJwtConverterConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // ❌ Regla de negocio: qué claims representan roles
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            List<String> roles = (List<String>) realmAccess.get("roles");

            // ❌ Regla de negocio: prefijo "ROLE_" y uppercase
            return roles.stream()
                .map(r -> "ROLE_" + r.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .toList();
        });
        return converter;
    }
}
```

Problemas:
- La decisión de **qué es un rol válido** y **cómo se nombra** está en infraestructura.
- Cambiar a Auth0 obliga a reescribir lógica de negocio.
- El test necesita un `Jwt` real o un mock de Spring Security.

#### ✅ Versión corregida (DDD estricto)

**Capa domain — el concepto de Rol es del negocio:**

```java
// seguridad/domain/model/Rol.java
package com.arquisoft.seguridad.domain.model;

public enum Rol {
    COORDINADOR,
    ASESOR_FICHA,
    ASESOR_PROYECTO,
    JURADO,
    ESTUDIANTE;

    /** Regla de negocio: así se representa un rol como authority de seguridad. */
    public String asAuthority() {
        return "ROLE_" + this.name();
    }

    /** Regla de negocio: parsear un string externo a un rol válido. */
    public static Rol fromExternalName(String nombre) {
        return Rol.valueOf(nombre.toUpperCase());
    }
}
```

**Capa domain — el puerto abstracto:**

```java
// seguridad/domain/port/out/TokenAuthoritiesPort.java
package com.arquisoft.seguridad.domain.port.out;

import com.arquisoft.seguridad.domain.model.Rol;
import java.util.List;

public interface TokenAuthoritiesPort {
    /** Extrae los roles de un token ya verificado. */
    List<Rol> extraerRoles(Object token);
}
```

**Capa infrastructure — el adaptador conoce Keycloak:**

```java
// seguridad/infrastructure/adapter/out/security/KeycloakAuthoritiesAdapter.java
package com.arquisoft.seguridad.infrastructure.adapter.out.security;

import com.arquisoft.seguridad.domain.model.Rol;
import com.arquisoft.seguridad.domain.port.out.TokenAuthoritiesPort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakAuthoritiesAdapter implements TokenAuthoritiesPort {

    @Override
    @SuppressWarnings("unchecked")
    public List<Rol> extraerRoles(Object token) {
        if (!(token instanceof Jwt jwt)) {
            return List.of();
        }
        // Detalle de infraestructura: Keycloak pone los roles aquí.
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) return List.of();

        List<String> nombres = (List<String>) realmAccess.getOrDefault("roles", List.of());
        return nombres.stream().map(Rol::fromExternalName).toList();
    }
}
```

**Capa infrastructure — el config de Spring solo cablea:**

```java
// seguridad/infrastructure/config/KeycloakJwtConverterConfig.java
@Configuration
@RequiredArgsConstructor
public class KeycloakJwtConverterConfig {

    private final TokenAuthoritiesPort tokenAuthoritiesPort;  // puerto inyectado

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt ->
            tokenAuthoritiesPort.extraerRoles(jwt).stream()
                .map(Rol::asAuthority)                           // regla de negocio
                .map(SimpleGrantedAuthority::new)
                .toList()
        );
        return converter;
    }
}
```

Lo que se consiguió:
- El `Config` **no tiene lógica de negocio**, solo cablea el puerto con Spring Security.
- El **dominio decide** qué rol existe, cómo se parsea y cómo se representa.
- El **adaptador** es la única clase que conoce la estructura de un JWT de Keycloak.
- Test del dominio (`Rol.asAuthority()`) = Java puro, sin Spring.
- Migrar a Auth0 = escribir `Auth0AuthoritiesAdapter`, cero cambios en dominio.

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

### Principio DDD

En el proyecto Arquisoft, **toda entidad que sea raíz de su agregado DEBE extender `AggregateRoot` de `shared:domain`**, con una única excepción:

- **Excepción:** el contexto `seguridad` no usa `AggregateRoot` porque es transversal y delega el estado en Keycloak.

En los otros 6 contextos, la ausencia de `AggregateRoot` en una entidad raíz es un **error bloqueante** que debe corregirse.

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

Clase base en `shared:domain` que asigna automáticamente `eventId` (UUID), `occurredAt` (Instant) y `eventType` (simpleName de la subclase).

```java
// Ya existe en shared:domain
public abstract class DomainEvent {
    private final UUID eventId;
    private final Instant occurredAt;
    private final String eventType;
    private final String aggregateId;

    protected DomainEvent(String aggregateId) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.eventType = this.getClass().getSimpleName();
        this.aggregateId = aggregateId;
    }
    // getters...
}
```

### Ciclo completo: emisión y drenado

1. **Dominio:** la entidad raíz acumula eventos con `publishEvent(...)` en sus métodos de negocio (incluido el factory `build`).
2. **Use case:** tras persistir, drena los eventos con `getUnPublishedEvents()`, los entrega a `EventPublisher` (puerto `shared:amqp`) y llama a `clearUnPublishedEvents()`.
3. **El dominio NUNCA** inyecta `EventPublisher`. Solo acumula eventos en memoria.
4. **El controller NUNCA** drena eventos directamente. Solo lo hace el use case.

### Factory methods obligatorios

| Método | Cuándo usar | ¿Emite evento? | ¿Genera UUID? |
|---|---|---|---|
| `build(...)` | Crear entidad nueva desde un comando/DTO | ✅ Sí (típicamente `{Entidad}CreadaEvent`) | ✅ Sí — `UUID.randomUUID()` |
| `rebuild(...)` | Reconstruir entidad desde persistencia | ❌ No | ❌ No — recibe el UUID de BD |

**Regla dura:** un `RepositoryAdapter` SIEMPRE usa `rebuild(...)`, nunca `build(...)`.

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

## Plantillas de Código Canónicas

### Entidad (Aggregate Root)

```java
package com.arquisoft.fichas.domain.model;

import com.arquisoft.shared.domain.AggregateRoot;
import com.arquisoft.fichas.domain.event.FichaCreadaEvent;
import java.util.UUID;

public class Ficha extends AggregateRoot {
    private final UUID id;
    private final String titulo;
    private final String estado;

    private Ficha(UUID id, String titulo, String estado) {
        this.id = id;
        this.titulo = titulo;
        this.estado = estado;
    }

    /** Crear una nueva ficha — genera UUID y publica evento. */
    public static Ficha build(String titulo) {
        Ficha ficha = new Ficha(UUID.randomUUID(), titulo, "BORRADOR");
        ficha.publishEvent(new FichaCreadaEvent(ficha.id.toString(), titulo));
        return ficha;
    }

    /** Reconstruir desde persistencia — sin UUID nuevo, sin eventos. */
    public static Ficha rebuild(UUID id, String titulo, String estado) {
        return new Ficha(id, titulo, estado);
    }

    public UUID getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getEstado() { return estado; }
}
```

### Evento de dominio

```java
package com.arquisoft.fichas.domain.event;

import com.arquisoft.shared.domain.DomainEvent;

public final class FichaCreadaEvent extends DomainEvent {
    private final String titulo;

    public FichaCreadaEvent(String aggregateId, String titulo) {
        super(aggregateId);
        this.titulo = titulo;
    }

    public String getTitulo() { return titulo; }
}
```

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
package com.arquisoft.fichas.domain.exception;

import com.arquisoft.shared.exceptions.DomainException;

public class FichaNoEncontradaException extends DomainException {
    public FichaNoEncontradaException(String id) {
        super("FICHA_NO_ENCONTRADA", "No se encontró la ficha con id: " + id);
    }
}
```

### Puerto de entrada (UseCase)

```java
package com.arquisoft.fichas.domain.port.in;

import com.arquisoft.fichas.domain.model.Ficha;

public interface CrearFichaUseCase {
    Ficha ejecutar(Ficha ficha);
}
```

### Puerto de salida (Repository)

```java
package com.arquisoft.fichas.domain.port.out;

import com.arquisoft.fichas.domain.model.Ficha;
import java.util.Optional;
import java.util.UUID;

public interface FichaRepositoryPort {
    Ficha guardar(Ficha ficha);
    Optional<Ficha> buscarPorId(UUID id);
}
```

### DTO Request

```java
package com.arquisoft.fichas.application.dto;

import com.arquisoft.fichas.domain.model.Ficha;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CrearFichaRequestDTO {
    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    public Ficha toDomain() {
        return Ficha.build(this.titulo);
    }
}
```

### DTO Response

```java
package com.arquisoft.fichas.application.dto;

import com.arquisoft.fichas.domain.model.Ficha;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FichaResponseDTO {
    private UUID id;
    private String titulo;
    private String estado;

    public static FichaResponseDTO fromDomain(Ficha ficha) {
        return FichaResponseDTO.builder()
            .id(ficha.getId())
            .titulo(ficha.getTitulo())
            .estado(ficha.getEstado())
            .build();
    }
}
```

### UseCase Impl (drena eventos)

```java
package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.domain.model.Ficha;
import com.arquisoft.fichas.domain.port.in.CrearFichaUseCase;
import com.arquisoft.fichas.domain.port.out.FichaRepositoryPort;
import com.arquisoft.shared.amqp.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrearFichaUseCaseImpl implements CrearFichaUseCase {

    private final FichaRepositoryPort fichaRepository;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public Ficha ejecutar(Ficha ficha) {
        log.info("Creando ficha: {}", ficha.getTitulo());
        Ficha guardada = fichaRepository.guardar(ficha);

        guardada.getUnPublishedEvents().forEach(eventPublisher::publish);
        guardada.clearUnPublishedEvents();

        return guardada;
    }
}
```

### Controller REST (ADR-011)

```java
package com.arquisoft.fichas.infrastructure.adapter.in.web;

import com.arquisoft.fichas.application.dto.CrearFichaRequestDTO;
import com.arquisoft.fichas.application.dto.FichaResponseDTO;
import com.arquisoft.fichas.domain.model.Ficha;
import com.arquisoft.fichas.domain.port.in.CrearFichaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/fichas")
@RequiredArgsConstructor
@Tag(name = "Fichas", description = "Gestion de fichas de perfil de proyectos de grado")
public class FichaController {

    private final CrearFichaUseCase crearFichaUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ASESOR_FICHA')")
    @Operation(
        summary = "Crear ficha de perfil",
        description = "Crea una nueva ficha de perfil para un proyecto de grado",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Ficha creada exitosamente",
            content = @Content(schema = @Schema(implementation = FichaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    public FichaResponseDTO crear(@Valid @RequestBody CrearFichaRequestDTO request) {
        Ficha ficha = crearFichaUseCase.ejecutar(request.toDomain());
        return FichaResponseDTO.fromDomain(ficha);
    }
}
```

---

## Mapeo Contexto → Schema PostgreSQL

El nombre del schema **no coincide** con el nombre del módulo Gradle en 3 casos. Usar **siempre** esta tabla:

| Contexto Gradle | Schema PostgreSQL |
|---|---|
| `seguridad` | `usuarios` |
| `fichas` | `fichas_perfil` |
| `proyectos` | `proyectos_grado` |
| `artefactos` | `artefactos` |
| `repositorio_artefactos` | `repositorio_artefactos` |
| `entregables` | `entregables` |
| `evaluaciones` | `evaluaciones` |

### Reglas Flyway

- Ubicación: `{contexto}/infrastructure/src/main/resources/db/migration/`
- Nomenclatura: `V{n}__{descripcion}.sql` (ej. `V1.0__fichas_schema.sql`)
- El SQL debe referenciar el schema correcto (ej. `fichas_perfil.ficha`, no `fichas.ficha`).
- En `@Entity` de JPA: `@Table(schema = "fichas_perfil", name = "ficha")`.

### Dependencias entre schemas (orden de creación)

Todos los schemas conviven en la base de datos `arquisoft`. Cuando una tabla de un contexto
referencia con FK una tabla de otro schema, la migración del contexto dependiente debe
ejecutarse **después** de que el schema del que depende ya tenga sus tablas creadas.

```
usuarios              → (ninguna — schema raíz)
fichas_perfil         → usuarios
repositorio_artefactos→ usuarios
proyectos_grado       → usuarios, fichas_perfil
mapas_ruta            → usuarios, proyectos_grado
artefactos            → usuarios, proyectos_grado, repositorio_artefactos
entregables           → usuarios, proyectos_grado, artefactos
evaluaciones          → usuarios, entregables
biblioteca            → usuarios
solicitudes           → usuarios
```

**Regla para FKs cruzadas entre schemas:** si la tabla del contexto A tiene una FK que apunta
a un schema B, escribir la constraint con schema calificado:
`REFERENCES {schema_b}.{tabla}(id)`. Verificar que la migración de B se ejecute antes que la de A.

---

## Nomenclatura (Regla Bilingüe)

| Elemento | Convención | Ejemplo |
|---|---|---|
| Paquete de contexto | español, minúsculas | `fichas`, `proyectos` |
| Paquete estructural | inglés | `domain`, `application`, `infrastructure` |
| Término de negocio | español | `ProyectoGrado`, `crearFicha`, `EstadoFicha` |
| Sufijo técnico | inglés | `UseCase`, `Port`, `DTO`, `Adapter`, `Config` |
| Clase | PascalCase | `CrearFichaUseCaseImpl` |
| Interfaz (puerto) | PascalCase, sin prefijo `I` | `FichaRepositoryPort` |
| Implementación | sufijo `Impl` | `FichaRepositoryAdapter` (o `...Impl`) |
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
- `@EnableMethodSecurity(prePostEnabled = true)` → usar `@PreAuthorize("hasRole('...')")` en controllers.
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