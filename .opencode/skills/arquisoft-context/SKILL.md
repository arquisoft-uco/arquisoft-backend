---
name: arquisoft-context
description:
  Contexto autoritativo del proyecto Arquisoft Backend para subagentes. Carga SIEMPRE antes de planificar, implementar, testear o validar cualquier Historia de Usuario o Técnica. Contiene el stack exacto verificado, las convenciones de arquitectura hexagonal + DDD, la regla estricta de EventEmittingEntity, el mapeo contexto → base de datos PostgreSQL, la guía de uso de features de Java 21 y las plantillas de código canónicas. Este skill es la ÚNICA fuente de verdad para el estado del proyecto — no leer AGENTS.md, README.md, QUICK_START.md, ARQUITECTURA_*.md ni docs/ del repositorio.
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
- La regla estricta de EventEmittingEntity (sección "EventEmittingEntity — Regla Estricta")
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

### Bounded Contexts (7)

| Contexto Gradle | GroupId base | ¿Usa EventEmittingEntity? |
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
| `shared:domain` | `com.arquisoft.shared.domain` | `EventEmittingEntity`, `DomainEvent`, `Page<T>` (record para paginación interna del dominio y application) |
| `shared:exceptions` | `com.arquisoft.shared.exception` | `DomainException` (clase base de excepciones de dominio). Los handlers concretos viven en cada contexto como `{Contexto}GlobalExceptionHandler` (ver sección "{Contexto}GlobalExceptionHandler — Patrón Canónico"). |
| `shared:amqp` | `com.arquisoft.shared.amqp` | `EventPublisher` (interfaz, firma única `void publish(DomainEvent event)`), `RabbitMQConfig`, `RabbitMQEventPublisher` (implementación). |
| `shared:redis` | `com.arquisoft.shared.redis` | `RedisClient` (interfaz, **sin implementación todavía** — esqueleto de intención para uso futuro). |
| `shared:web` | `com.arquisoft.shared.web` | `GlobalAppExceptionHandler`, `ErrorResponseDTO`, `PageResponseDTO<T>`, `HttpClient`. **Hogar de DTOs técnicos genéricos** y manejo cross-cutting de excepciones. |
| `shared:validation` | `com.arquisoft.shared.validation` | Esqueleto de intención. **Sin uso activo todavía** — listo para validadores de dominio reales (cédula colombiana, código de estudiante, etc.) cuando aparezcan en una HU. NO duplicar validaciones que Jakarta Validation ya cubre (`@Email`, `@Size`, `@NotBlank`, etc.). |

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
    │   │   │   └── {Contexto}GlobalExceptionHandler.java   # @RestControllerAdvice (si aplica)
    │   │   └── messaging/           # EventListeners RabbitMQ (si el contexto escucha eventos)
    │   │       └── {Entidad}EventListener.java
    │   └── out/
    │       ├── persistence/         # JPA Entity, JpaRepository, RepositoryAdapter
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
- `adapter/out/{tipo}/` — para otras integraciones externas (ej. `security/` para Keycloak admin client, `storage/` para S3, `notification/` para SMTP). Una subcarpeta por tipo de integración.

> **Nota:** **NO** existe `adapter/out/messaging/` en los contextos. La publicación de eventos de dominio a RabbitMQ se centraliza en `shared:amqp` (con `RabbitMQEventPublisher`). Cada contexto solo inyecta `EventPublisher` y lo invoca desde sus use cases.

La consistencia (siempre subcarpeta) prevalece sobre la simplicidad (carpeta plana cuando solo hay un tipo). Esto facilita que cualquier nuevo tipo de adaptador encuentre su lugar sin reorganizaciones.

### Componentes web especiales — dónde van

| Componente | Ubicación | Tipo Spring |
|---|---|---|
| `{Entidad}Controller` | `adapter/in/web/` | `@RestController` |
| `{Contexto}GlobalExceptionHandler` | `adapter/in/web/` | `@RestControllerAdvice` |
| Interceptores REST específicos del contexto | `adapter/in/web/` | clase con `HandlerInterceptor` |
| Filtros HTTP (cross-cutting, ej. rate limit) | `infrastructure/filter/` | `@Component` con `Filter` |
| `OpenApiConfig`, `SecurityConfig`, `RabbitMQConfig` | `infrastructure/config/` | `@Configuration` |

**Regla:** un componente está en `adapter/in/web/` si **solo se activa durante una request REST**. Si es transversal a más cosas (filtros que también aplican a actuator, configs globales), va en `filter/` o `config/`.

---

## DDD Estricto — Separación de Responsabilidades por Capas

Esta sección define **qué puede y qué no puede vivir en cada capa** del proyecto. Es la regla
fundamental que subyace a todo lo demás (incluido EventEmittingEntity). Antes de escribir cualquier
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
| Advice global de errores web | `infrastructure/adapter/in/web/` | `{Contexto}GlobalExceptionHandler` (`@RestControllerAdvice`) |
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
`com.arquisoft.shared.exception.*`).

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
// seguridad/domain/model/UsuarioRole.java
package com.arquisoft.seguridad.domain.model;

public enum UsuarioRole {
    ESTUDIANTE("estudiante", "Estudiante que presenta proyecto de grado"),
    ASESOR("asesor", "Asesor asignado a un proyecto de grado"),
    ASESOR_FICHA("asesor-ficha", "Asesor que apoya la elaboración de fichas de perfil"),
    COORDINADOR("coordinador", "Coordinador del programa que gestiona proyectos"),
    JURADO("jurado", "Jurado que evalúa proyectos de grado"),
    BIBLIOTECARIO("bibliotecario", "Bibliotecario que gestiona consulta de PG"),
    REPRESENTANTE_COMITE_CURRICULUM("representante-comite", "Representante que aprueba fichas de perfil"),
    ADMINISTRADOR("administrador", "Administrador del sistema");

    private final String keycloakName;
    private final String descripcion;

    UsuarioRole(String keycloakName, String descripcion) {
        this.keycloakName = keycloakName;
        this.descripcion = descripcion;
    }

    public String getKeycloakName() { return keycloakName; }
    public String getDescripcion() { return descripcion; }
}
```

> **Convención de nombres:** los roles realm en Keycloak usan **kebab-case** (`coordinador`, `asesor-ficha`, `representante-comite`). El JWT trae estos nombres tal cual en `realm_access.roles`. **NO** se usa el prefijo `ROLE_` ni MAYÚSCULAS — esos son convenciones de Spring Security antiguas que aquí no aplican.

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

## EventEmittingEntity — Regla Estricta

> **Nota sobre el nombre:** `EventEmittingEntity` (en `shared:domain`) es la clase base
> que gestiona **únicamente la emisión de eventos de dominio** en memoria. NO define
> identidad, invariantes ni comportamiento de un Aggregate Root completo. Una entidad
> es un Aggregate Root cuando, además de extender esta clase, define su identidad,
> sus invariantes (validaciones en el constructor) y su comportamiento de negocio
> (factories `build`/`rebuild`, métodos de acción).

### Principio DDD

En el proyecto Arquisoft, **toda entidad que sea raíz de su agregado DEBE extender `EventEmittingEntity` de `shared:domain`**, con una única excepción:

- **Excepción:** el contexto `seguridad` no usa `EventEmittingEntity` porque es transversal y delega el estado en Keycloak.

En los otros 6 contextos, la ausencia de `EventEmittingEntity` en una entidad raíz es un **error bloqueante** que debe corregirse.

> **Importante:** una entidad puede extender `EventEmittingEntity` y NO emitir eventos.
> Si la HU es un CRUD simple sin consumidores conocidos del evento, el factory `build(...)`
> simplemente no llama a `publishEvent(...)` y el use case no inyecta `EventPublisher`.
> Extender `EventEmittingEntity` es por consistencia y para tener la maquinaria lista
> el día que aparezca la necesidad de emitir eventos. Ver sección "¿Cuándo emitir eventos
> de dominio?" más abajo.

### ¿Qué es EventEmittingEntity?

Clase base en `shared:domain` que gestiona eventos de dominio acumulados en memoria hasta que el use case los drena tras persistir.

```java
// Ya existe en shared:domain — no reimplementar, solo usar
public abstract class EventEmittingEntity {
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

**Regla dura:** un `RepositoryAdapter` SIEMPRE usa `rebuild(...)`, nunca `build(...)`.

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

**Ambas opciones son válidas.** La entidad sigue extendiendo `EventEmittingEntity` por consistencia y para tener la maquinaria lista. Cuando aparezca la necesidad futura, solo añades la clase del evento, llamas a `publishEvent(...)` en `build(...)` e inyectas `EventPublisher` en el use case.

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

| Tipo | Idioma de campos | Ubicación | Ejemplos |
|---|---|---|---|
| **DTO técnico genérico** | **Inglés** (sigue convención de la industria) | `shared:web` | `ErrorResponseDTO`, `PageResponseDTO<T>`, futuros `HealthResponseDTO`, etc. |
| **DTO de dominio** | **Español** (refleja el modelo enriquecido) | `{contexto}/application/dto/` | `FichaPerfilResumenDTO`, `CrearFichaPerfilRequestDTO`, `UsuarioResponseDTO` |

### Razón de la separación

- **DTOs técnicos** son envoltorios que no contienen conceptos de negocio. Pagina cualquier cosa, formatea cualquier error. Sus campos siguen convenciones universales (`content`, `totalElements`, `error`, `errorCode`) que los clientes esperan. Viven en `shared:web` para no duplicarlos.
- **DTOs de dominio** modelan tu dominio académico. Sus campos (`tituloProyecto`, `asesorFichaId`, `nombreEstudiante`) reflejan el modelo enriquecido y se mantienen en español para consistencia con entidades, repositorios y eventos.

### Reglas inviolables

1. **NUNCA crees un `PageResponseDTO` o `ErrorResponseDTO` local en un contexto.** Importa desde `com.arquisoft.shared.web`.
2. **Si necesitas un DTO técnico nuevo que vaya a usarse en más de un contexto** (ej. `FilterRequestDTO`, `ValidationErrorDTO` específico, etc.) → ponlo en `shared:web` con campos en inglés.
3. **DTOs de dominio NO pueden tener campos en inglés** que renombren conceptos del modelo enriquecido. Si el modelo dice `tituloProyecto`, el DTO usa `tituloProyecto`, no `title`.
4. **No anotes campos con `@JsonProperty` para mezclar idiomas.** Si el JSON externo necesita un nombre específico, evalúa si el DTO debería ser técnico (en inglés) o de dominio (en español) — no ambos.

---

## Paginación — Spring Data `Page<T>` en domain/application + `PageResponseDTO<T>` en HTTP

> **Decisión arquitectónica:** Spring Data `spring-data-commons` (que provee `Page<T>` y `Pageable`) se trata como tipo estándar de la industria, al nivel de `java.util.List`. Es una librería ligera de tipos genéricos que NO arrastra Spring Boot, ORM ni autoconfiguración. Usarla en domain reduce drásticamente la complejidad sin romper la separación de responsabilidades real (lo que SÍ rompe DDD es importar `@Entity`, `JpaRepository`, `EntityManager` — eso NO ocurre con `Page<T>`/`Pageable`).
>
> **No existe `Page<T>` propio en `shared:domain`.** Si ves un import desde `com.arquisoft.shared.domain.Page`, está obsoleto: debe ser `org.springframework.data.domain.Page`.

### Flujo canónico de paginación

```
┌─────────────────────────────────────────────────────────────────┐
│ HTTP Client                                                     │
│   ↓ JSON request con params (?page=0&size=20&sort=...)          │
├─────────────────────────────────────────────────────────────────┤
│ INFRASTRUCTURE ({contexto}/infrastructure)                      │
│   • Controller recibe Pageable (Spring lo construye desde       │
│     query params automáticamente)                               │
│   • Llama al UseCase: Page<{Entidad}DTO> ejecutar(Pageable)     │
│   • Convierte Page<{Entidad}DTO> → PageResponseDTO<{Entidad}DTO>│
│     con PageResponseDTO.from(...)                               │
│   • Adapter JPA: jpaRepository.findAll(pageable)                │
│     .map(JpaEntity::toDomain) → Page<{Entidad}>                 │
├─────────────────────────────────────────────────────────────────┤
│ APPLICATION ({contexto}/application)                            │
│   • UseCaseImpl orquesta lógica                                 │
│   • Habla con Page<T>, Pageable de Spring Data — son tipos      │
│     estándar de la industria, igual que List<T>                 │
│   • Llama a RepositoryPort (interfaz del dominio)               │
├─────────────────────────────────────────────────────────────────┤
│ DOMAIN ({contexto}/domain + shared/domain)                      │
│   • Define puertos (interfaces): UseCase, RepositoryPort        │
│     que pueden retornar Page<{Entidad}> (Spring Data)           │
│   • Define modelos de dominio                                   │
│   • CERO conocimiento de @Entity, JpaRepository, ni             │
│     PageResponseDTO                                             │
└─────────────────────────────────────────────────────────────────┘
```

### Tipos involucrados — quién conoce qué

| Tipo | Vive en | Usado por | Notas |
|---|---|---|---|
| `org.springframework.data.domain.Page<T>` | `spring-data-commons` | Puerto, use case, adapter | Tipo estándar — válido en domain, application e infrastructure |
| `org.springframework.data.domain.Pageable` | `spring-data-commons` | Puerto, use case, controller | Tipo estándar — Spring lo construye desde query params automáticamente |
| `PageResponseDTO<T>` | `shared:web` | Controller (response) | **SOLO** infrastructure. NUNCA aparece en domain ni application |

### Reglas inviolables de paginación

1. **`spring-data-commons` se declara como `api` en `shared:domain/build.gradle`** para exponer `Page<T>` y `Pageable` transitivamente a quien consuma el módulo.
2. **El puerto retorna `Page<{Entidad}>` (Spring Data) y recibe `Pageable`.** El nombre del método NUNCA incluye "Paginadas" — la paginación se infiere del `Pageable`.
3. **El use case usa `.map(...)` directo de Spring `Page`** para convertir entidades a DTOs (Spring `Page<T>` ya tiene método `map(Function<T, R>)` built-in).
4. **El adapter del repositorio queda en una sola línea:** `jpaRepository.findAll(pageable).map(JpaEntity::toDomain)`. Cero conversiones manuales.
5. **El controller convierte `Page<...>` → `PageResponseDTO<...>` con `PageResponseDTO.from(...)`** justo antes del `ResponseEntity.ok(...)`.
6. **`PageResponseDTO` NUNCA aparece en `domain/` ni en `application/`.** Si aparece, es violación bloqueante.
7. **Nombres de métodos:** usar `consultarTodas`, `listarTodas`, `buscarPor...` — la paginación es un parámetro, no parte del nombre. **`consultarPaginadas` es un anti-patrón.**

### Configuración de `shared:domain/build.gradle`

```gradle
dependencies {
    api 'org.springframework.data:spring-data-commons'
    // ... resto de dependencias
}
```

`api` (no `implementation`) expone los tipos transitivamente. Así `application` e `infrastructure` ven `Page<T>` y `Pageable` automáticamente sin redeclarar la dependencia.

### Ejemplo completo — firmas correctas

```java
// ✅ Puerto de salida (domain)
public interface FichaPerfilRepositoryPort {
    Page<FichaPerfil> consultarTodas(Pageable pageable);
}

// ✅ Use case (domain port in)
public interface ConsultarFichasPerfilUseCase {
    Page<FichaPerfilResumenDTO> ejecutar(Pageable pageable);
}

// ✅ Use case impl (application) — usa .map() de Spring Page
@Service
@RequiredArgsConstructor
public class ConsultarFichasPerfilUseCaseImpl implements ConsultarFichasPerfilUseCase {
    private final FichaPerfilRepositoryPort repository;

    @Override
    public Page<FichaPerfilResumenDTO> ejecutar(Pageable pageable) {
        return repository.consultarTodas(pageable)
                .map(FichaPerfilResumenDTO::from);
    }
}

// ✅ Adapter (infrastructure) — una sola línea
@Component
@RequiredArgsConstructor
public class FichaPerfilRepositoryAdapter implements FichaPerfilRepositoryPort {
    private final FichaPerfilJpaRepository jpaRepository;

    @Override
    public Page<FichaPerfil> consultarTodas(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(FichaPerfilJpaEntity::toDomain);
    }
}

// ✅ Controller (infrastructure) — Spring inyecta Pageable desde query params
@GetMapping
public ResponseEntity<PageResponseDTO<FichaPerfilResumenDTO>> consultar(Pageable pageable) {
    return ResponseEntity.ok(PageResponseDTO.from(useCase.ejecutar(pageable)));
}
```

### Anti-patrones bloqueantes

```java
// ❌ MAL — Page propio del dominio (eliminado del proyecto)
import com.arquisoft.shared.domain.Page;  // ya no existe
Page<FichaPerfil> consultarTodas(...);

// ❌ MAL — nombre con "Paginadas"
Page<FichaPerfil> consultarPaginadas(int page, int size);
Page<FichaPerfil> consultarTodasPaginadas(int page, int size);

// ❌ MAL — use case retornando PageResponseDTO (DTO técnico filtra a application)
PageResponseDTO<FichaPerfilResumenDTO> ejecutar(Pageable pageable);

// ❌ MAL — puerto retornando Page<JpaEntity>
Page<FichaPerfilJpaEntity> consultarTodas(Pageable pageable);

// ❌ MAL — controller con @RequestParam manuales en lugar de Pageable
public ResponseEntity<...> consultar(@RequestParam int page, @RequestParam int size) {
    // Spring ya construye Pageable solo. Hacerlo manual es redundante.
}
```

---



```java
package com.arquisoft.fichas.domain.model;

import com.arquisoft.shared.domain.EventEmittingEntity;
import com.arquisoft.fichas.domain.event.FichaCreadaEvent;
import java.util.UUID;

public class Ficha extends EventEmittingEntity {
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

    @Override
    public String getEventTopic() {
        return "fichas.ficha.creada";
    }

    public String getTitulo() { return titulo; }
}
```

> **Obligatorio:** toda subclase de `DomainEvent` DEBE implementar `getEventTopic()` retornando un string con el formato `{contexto}.{entidad}.{accion}`. La firma del método es `public abstract String getEventTopic()` en la clase base. Sin esta implementación el código no compila.

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

import com.arquisoft.shared.exception.DomainException;

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
    @PreAuthorize("hasAuthority('fichas:ficha:create')")
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
import com.arquisoft.shared.exception.DomainException;
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
6. Toda entidad raíz en los 6 contextos de negocio **DEBE** extender `EventEmittingEntity`. La única excepción documentada es `seguridad`.
7. Los IDs son **siempre `UUID`**. Cualquier uso de `Long`/`Integer` como ID es un error.
8. La dirección de dependencias `domain ← application ← infrastructure` **no se negocia**.
9. Los bounded contexts **no se importan entre sí** — solo eventos RabbitMQ.
10. Versiones del stack: **no inventar**. Usar exactamente las de este skill.
11. Features de Java 21: **balanceado**. Aplicar cuando aporte claridad (records para VO y payloads de eventos, sealed para estados, text blocks para SQL, var con tipo evidente). No forzar en entidades de dominio (requieren constructor privado + factories).
12. Virtual Threads están gestionados por Spring Boot — **nunca** crear `TaskExecutor` manual.
13. **Prueba del algodón:** antes de escribir cualquier archivo, pregúntate "¿si mañana cambio esta tecnología externa, qué archivos tengo que tocar?". Si la respuesta incluye `domain/`, la lógica está mal ubicada.