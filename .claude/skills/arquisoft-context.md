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
| Gradle | 9.0.0 | Multi-módulo (8 contextos + `shared` con sus submódulos) |
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
| `seguridad` | `com.arquisoft.seguridad` | **Solo configuración** (login/refresh con Keycloak, rate-limit, JWT, blacklist Redis). No tiene HUs de negocio ni BD propia | ❌ No |
| `usuarios` | `com.arquisoft.usuarios` | **Ejemplo demostrativo** del patrón asíncrono Spring Modulith + RabbitMQ. `UsuarioAggregate` + `UsuarioCreadoEvent` (topic `usuarios.usuario.creado`) sirven como referencia canónica para HUs que emiten eventos. NO implementa HUs reales en esta versión | ✅ Sí (para el ejemplo) |
| `fichas` | `com.arquisoft.fichas` | Activo | ✅ Sí |
| `proyectos` | `com.arquisoft.proyectos` | Pendiente | ✅ Sí |
| `artefactos` | `com.arquisoft.artefactos` | Pendiente | ✅ Sí |
| `repositorio_artefactos` | `com.arquisoft.repositorio_artefactos` | Pendiente | ✅ Sí |
| `entregables` | `com.arquisoft.entregables` | Pendiente | ✅ Sí |
| `evaluaciones` | `com.arquisoft.evaluaciones` | Pendiente | ✅ Sí |

> **`seguridad` no implementa HUs de negocio ni tiene aggregates propios.** Solo expone `/auth/login`, `/auth/refresh`, `/auth/validate` y la configuración global (rate-limit, JWT decoder, blacklist en Redis). Tampoco tiene BD PostgreSQL propia.
>
> **`usuarios` es contexto de ejemplo.** El `UsuarioAggregate`, `UsuarioCreadoEvent`, `CrearUsuarioUseCase` y la migración `event_publication` que ahí viven son la **referencia canónica del patrón eventos+outbox** del proyecto — se mantienen vivos como guía pero NO representan HUs de negocio activas. Las HUs reales del proyecto se planifican e implementan en los 6 contextos restantes (`fichas`, `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables`, `evaluaciones`).
>
> En documentación y ejemplos del SKILL/agentes, los aggregates publicadores de eventos se ilustran indistintamente con `fichas` (ej. `FichaPerfilAggregate.crear()` publica `FichaPerfilCreadaEvent`) o con `usuarios` (ej. `UsuarioAggregate.crear()` publica `UsuarioCreadoEvent`).

### Módulos shared

| Módulo | Paquete base | Clases/Interfaces clave |
|---|---|---|
| `shared:domain` | `com.arquisoft.shared.*` (`events`, `validation`, `inputport`, `exception`, `pagination`, `query`, `util`, `model`) | `AggregateRoot`, `DomainEvent`, `EventPublisher` (interfaz), `InputPort<I, O>`, `VoidInputPort<I>`, `PaginatedResult<T>`, `QueryCriteria`, jerarquía base de excepciones |
| `shared:amqp` | `com.arquisoft.shared.amqp` | `SpringModulithEventPublisher` (impl principal), `RabbitMQEventPublisher` (fallback), `AbstractEventConsumer`, `RabbitMQConfig`, `ModulithAmqpExternalizationConfig` |
| `shared:web` | `com.arquisoft.shared.web` | `GlobalAppExceptionHandler`, `ErrorResponseDTO`, `PageResponseDTO<T>`, `QueryCriteriaRequestDTO`, `NodoFiltroDTO` (Jackson polimórfico) |
| `shared:postgres` | `com.arquisoft.shared.postgres` | `QueryJpaSpecification<JpaEntity>`, `CampoSpec` (sealed: `Texto`, `Uuid`, `Entero`, `Decimal`, `Fecha`, `FechaHora`, `Booleano`). Utilería para traducir `Criteria` → `Specification` |
| `shared:minio` | `com.arquisoft.shared.minio` | `MinioStorageClient` (presigned URLs PUT/GET, `objectExists`, `deleteObject`), `MinioConfig`, `MinioProperties` |
| `shared:redis` | `com.arquisoft.shared.redis` | Esqueleto sin implementación |
| `shared:message` | `com.arquisoft.shared.message` | **Catálogo central de mensajes** (Message Catalog Pattern). `AppMessages` (transversal) + `{Contexto}Messages` × 7 (uno por bounded context) con nested classes por entidad. Java puro, sin Spring ni Jakarta. Expuesto transitivamente vía `shared:domain` (`api project(':shared:message')`). Ver [shared/message/README.md](../../shared/message/README.md) |

### Estructura estándar por contexto — CQRS + Vertical Slice

> **Principio organizador:** primero por **entidad/agregado**, luego dentro de cada entidad se separa `command/` (write) de `query/` (read). NO hay carpetas planas `usecase/`, `dto/`, `entity/`.

```
{contexto}/
├── domain/
│   └── {entidad}/
│       ├── aggregate/         # XxxAggregate (extiende AggregateRoot solo si emite eventos — ver sección AggregateRoot)
│       ├── port/out/          # XxxOutputPort (interfaces de persistencia del write side)
│       ├── event/             # Eventos de dominio (extiende DomainEvent)
│       └── exception/         # Excepciones del agregado (extienden DomainException)
│
├── application/
│   └── {entidad}/
│       ├── exception/         # XxxDuplicadaException, XxxNoEncontradaException (extienden ApplicationException)
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

## Mensajes y textos — Message Catalog (`shared:message`)

> **Regla dura:** ningún string literal puede vivir embebido en código de producción si entra en alguna de estas categorías: mensaje de excepción, código de error, mensaje de validación de dominio, mensaje de log, nombre de campo para reporting, o límite numérico de negocio. **Debe vivir como constante en `shared:message`.**

### Ubicación y disponibilidad

- Módulo Gradle: `shared:message` (paquete `com.arquisoft.shared.message`).
- **No tiene dependencias externas** — Java puro, sin Spring, sin Jakarta.
- Expuesto **transitivamente** desde `shared:domain` con `api project(':shared:message')`. Cualquier capa (`domain`, `application`, `infrastructure`) de cualquier contexto puede importarlo sin tocar su propio `build.gradle`.
- Documentación de convención completa: [shared/message/README.md](../../shared/message/README.md).

### Estructura

Una clase por bounded context. Dentro de cada clase, una `public static final class` por aggregate/entidad/agrupador funcional.

```
shared/message/src/main/java/com/arquisoft/shared/message/
├── AppMessages.java                       ← transversal (DomainValidator, PaginationRequest)
├── FichasMessages.java
├── SeguridadMessages.java
├── ProyectosMessages.java
├── ArtefactosMessages.java
├── RepositorioArtefactosMessages.java
├── EntregablesMessages.java
└── EvaluacionesMessages.java
```

### 5 secciones por nested class de entidad (orden obligatorio)

| # | Sección | Prefijo / Patrón | Tipo Java | Ejemplo |
|---|---|---|---|---|
| 1 | **Campos** | `CAMPO_{NOMBRE}` | `String` | `CAMPO_TITULO = "tituloProyecto"` |
| 2 | **Límites** | `{CAMPO}_{TIPO_LIMITE}` | `int`/`long` | `TITULO_MAX = 100` |
| 3 | **Códigos de error** | `{ENTIDAD}_{DESCRIPCION}` UPPER_SNAKE (valor = nombre) | `String` | `FICHA_TITULO_DUPLICADO = "FICHA_TITULO_DUPLICADO"` |
| 4 | **Mensajes de error** | descripción UPPER_SNAKE, con `%s`/`%d` | `String` | `TITULO_DUPLICADO = "El título ya existe: %s"` |
| 5 | **Logs** | `LOG_{ACCION}` con `{}` SLF4J | `String` | `LOG_REGISTRADA = "Ficha registrada — id={}"` |

Las secciones se separan con comentarios `// Campos`, `// Límites`, `// Códigos de error`, `// Mensajes de error`, `// Logs` — solo las secciones con al menos una constante.

### Plantilla canónica de archivo por contexto

```java
package com.arquisoft.shared.message;

public final class FichasMessages {

    private FichasMessages() {}

    public static final class FichaPerfil {

        private FichaPerfil() {}

        // Campos
        public static final String CAMPO_TITULO = "tituloProyecto";

        // Límites
        public static final int TITULO_MAX = 100;

        // Códigos de error
        public static final String FICHA_TITULO_REQUERIDO = "FICHA_TITULO_REQUERIDO";

        // Mensajes de error
        public static final String TITULO_DUPLICADO = "El título ya existe: %s";

        // Logs
        public static final String LOG_REGISTRADA = "Ficha de perfil registrada — id={}";
    }
}
```

### Reglas inviolables

1. **Clase outer** `public final` con constructor privado vacío.
2. **Nested class** `public static final` con constructor privado vacío.
3. Todas las constantes `public static final`.
4. **Prohibido JavaDoc** en cualquier archivo de `shared:message` — los nombres son autoexplicativos. La convención completa vive en `shared/message/README.md`.
5. **Nunca** se crea un paquete `{entidad}/message/` dentro de un contexto. Las constantes específicas de una entidad viven SOLO en `{Contexto}Messages.{Entidad}` dentro de `shared:message`.

### Uso desde código

```java
// Validación de dominio
DomainValidator.notBlank(titulo,
        FichasMessages.FichaPerfil.CAMPO_TITULO,
        FichasMessages.FichaPerfil.FICHA_TITULO_REQUERIDO,
        result);

// Excepción con mensaje parametrizado
throw new FichaTituloDuplicadoException(
        FichasMessages.FichaPerfil.TITULO_DUPLICADO.formatted(titulo),
        FichasMessages.FichaPerfil.FICHA_TITULO_DUPLICADO);

// Log SLF4J — la plantilla del catálogo, los valores como varargs
log.info(FichasMessages.FichaPerfil.LOG_REGISTRADA, ficha.getId());
```

### Cuándo agregar al catálogo (decisión obligatoria al implementar)

| Veo este patrón en el código… | …acción obligatoria |
|---|---|
| `log.info("texto literal", ...)` | extraer a `{Contexto}Messages.{Entidad}.LOG_*` |
| `super("mensaje literal", ...)` en `*Exception` | extraer a `{Contexto}Messages.{Entidad}.{NOMBRE_DESCRIPTIVO}` o `MENSAJE_*` |
| `result.addError("campo", "CODIGO", "mensaje literal", ...)` | los 3 argumentos van al catálogo: `CAMPO_*`, código UPPER_SNAKE, y mensaje parametrizado |
| `if (valor.length() > 100)` con literal numérico de negocio | extraer límite a `{Contexto}Messages.{Entidad}.{CAMPO}_MAX` |
| `new ApplicationException("texto", "CODIGO")` | mensaje y código al catálogo |

### Cuándo NO entra al catálogo

- Strings técnicos de configuración: nombres de queues/colas/beans/headers HTTP, valores de `@RequestMapping`, claves de propiedades YAML.
- Constantes propias de módulos `shared:*` que NO representan mensajes (ej. nombres de tipos de eventos AMQP viven en `shared:amqp`).
- Literales en tests (los tests pueden importar del catálogo para evitar duplicación, pero no es obligatorio).
- JavaDocs / comentarios.

### Agregar entidad nueva

1. Identificar el bounded context (`fichas`, `seguridad`, etc.).
2. Abrir `{Contexto}Messages.java` en `shared:message`.
3. Agregar `public static final class {NombreEntidad}` con constructor privado vacío.
4. Añadir constantes en el orden de las 5 secciones, separadas por los comentarios estándar.
5. **No** crear archivos sueltos `{NombreEntidad}Messages.java` — todo va dentro de la clase del contexto.

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
- `com.arquisoft.shared.*` (subpaquetes `events/`, `validation/`, `inputport/`, `exception/`, `pagination/`, `query/`, `util/`, `model/`)
- `com.arquisoft.shared.message.*` (catálogo central de mensajes — Java puro, sin framework)

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
    void guardar(FichaPerfilAggregate aggregate);
    Optional<FichaPerfilAggregate> buscarPorId(UUID id);
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
> (factories `crear`/`reconstruir`, métodos de acción).

### Principio DDD

En el proyecto Arquisoft, **una entidad raíz extiende `AggregateRoot` de `shared:domain` SOLO si la HU emite eventos de dominio**. La extensión es función directa de la decisión de eventos, no de "consistencia futura".

**Reglas duras:**

- **`seguridad`:** nunca usa `AggregateRoot` (delega el estado en Keycloak).
- **Los otros 6 contextos:**
    - HU **emite eventos** (consumidores conocidos o auditoría justificada) → la entidad raíz **DEBE** extender `AggregateRoot`. Su ausencia es **error bloqueante**.
    - HU **NO emite eventos** (CRUD interno sin consumidores ni casos de auditoría) → la entidad raíz **NO** extiende `AggregateRoot`. Es solo un `class` con factories `crear`/`reconstruir`. Forzar la extensión "por consistencia" es un **error bloqueante** porque arrastra maquinaria muerta (lista de eventos, métodos `getUnPublishedEvents`/`drainUnPublishedEvents`) que nadie usa.

> **Migración cuando aparezca el primer evento:** si una HU futura introduce un evento sobre una entidad que hoy no extiende `AggregateRoot`, esa HU es la que añade `extends AggregateRoot` y crea la clase del evento. No se anticipa.

### ¿Qué es AggregateRoot?

Clase base en `shared:domain` que gestiona eventos de dominio acumulados en memoria hasta que el use case los drena tras persistir.

```java
// Ya existe en shared:domain — no reimplementar, solo usar
public abstract class AggregateRoot {
    private final List<DomainEvent> unPublishedEvents = new ArrayList<>();

    protected void publishEvent(DomainEvent event) { unPublishedEvents.add(event); }

    // Retorna la lista Y la limpia en una sola operación. Lo llama el use case tras
    // persistir. NO existe clearUnPublishedEvents() — código que lo invoque no compila.
    public List<DomainEvent> drainUnPublishedEvents() { /* drena + limpia */ }

    // protected — solo accesible desde tests del mismo paquete del aggregate.
    protected List<DomainEvent> getUnPublishedEvents() { /* copia de la lista */ }
}
```

### ¿Qué es DomainEvent?

Clase base en `shared:domain`. El constructor recibe `eventTopic` y `eventType`, valida que el topic cumpla el formato `{contexto}.{entidad}.{accion}`, y asigna automáticamente `eventId` (UUID) y `occurredAt` (`Instant`). **`getEventTopic()` es `final`** — la subclase NO lo sobreescribe: declara su constante `EVENT_TOPIC` y la pasa al `super(...)`.

```java
// Ya existe en shared:domain
public abstract class DomainEvent {
    protected DomainEvent(String eventTopic, String eventType) {
        validateTopic(eventTopic);                  // formato {contexto}.{entidad}.{accion}
        this.eventId    = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.eventType  = eventType;
        this.eventTopic = eventTopic;
    }
    public final String getEventTopic() { return eventTopic; }  // final — no se sobreescribe
    // getEventId(), getOccurredAt(), getEventType()
}

// Cada evento concreto declara sus constantes y las pasa al super:
public class UsuarioCreadoEvent extends DomainEvent {
    public static final String EVENT_TOPIC = "usuarios.usuario.creado";
    public static final String EVENT_TYPE  = "UsuarioCreadoEvent";
    public UsuarioCreadoEvent(UUID usuarioId, String email, String rol) {
        super(EVENT_TOPIC, EVENT_TYPE);
        // ...campos propios del evento
    }
}
```

### Ciclo de emisión y drenado

El aggregate acumula eventos con `publishEvent(...)` en sus factories/métodos de negocio. El use case, tras persistir, drena y publica en una línea: `aggregate.drainUnPublishedEvents().forEach(eventPublisher::publish)`. El dominio NUNCA inyecta `EventPublisher`; el controller NUNCA drena. Mecánica completa (Outbox por contexto, Spring Modulith) en **"Eventos asíncronos — RabbitMQ"**.

### Factory methods obligatorios

| Método | Cuándo usar | ¿Emite evento? | ¿Genera UUID? |
|---|---|---|---|
| `crear(...)` | Crear entidad nueva desde un comando/DTO | Solo si la entidad extiende `AggregateRoot` Y la HU emite eventos | ✅ Sí — `UUID.randomUUID()` |
| `reconstruir(...)` | Reconstruir entidad desde persistencia | ❌ Nunca | ❌ No — recibe el UUID de BD |

> **Valores autogenerados en `crear(...)`:** `UUID` → `UUID.randomUUID()` · `Instant` → `UtilDate.generateNewInstantNow()` (de `com.arquisoft.shared.util.UtilDate`, en `shared:domain`). **Nunca** `Instant.now()` directamente en código de dominio.

**Regla dura:** un `CommandOutputAdapter` SIEMPRE usa `reconstruir(...)`, nunca `crear(...)`. Aplica tanto si la entidad extiende `AggregateRoot` como si no — el factory `crear` queda reservado para la creación inicial desde el use case.

**Convención de nombres bilingüe:** los factories del aggregate son **conceptos de negocio** (crear una ficha, reconstruir una ficha desde BD), no sufijos técnicos como el `Builder.build()` de Lombok. Por eso van en español junto con los demás métodos de negocio del aggregate (`aprobar`, `rechazar`, `actualizarTitulo`, etc.). No usar `build`/`rebuild` en inglés — código antiguo con esa convención debe migrarse.

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

| Decisión | Entidad raíz | `crear(...)` | Use case | Plan declara |
|---|---|---|---|---|
| **Con eventos** | `extends AggregateRoot` | Llama a `publishEvent(new {Entidad}{Accion}Event(...))` | Inyecta `EventPublisher` y drena tras persistir con `aggregate.drainUnPublishedEvents().forEach(eventPublisher::publish)` | Sección 4 lista los eventos emitidos con su `eventTopic` |
| **Sin eventos (CRUD simple)** | **NO extiende `AggregateRoot`** — es un `class` plano con factories `crear`/`reconstruir` | NO existe `publishEvent(...)` ni se llama | NO inyecta `EventPublisher`, no hay drenado | Sección 4 declara explícitamente "Esta HU no emite eventos: <razón>" + "Entidad raíz NO extiende `AggregateRoot`" |

**Cuando aparezca la primera necesidad de evento futuro:** la HU que introduzca el evento añade `extends AggregateRoot` a la entidad, crea la clase del evento (`{Entidad}{Accion}Event extends DomainEvent` con su constante `EVENT_TOPIC` pasada al `super(...)`), llama a `publishEvent(...)` en el factory correspondiente e inyecta `EventPublisher` en el use case. No se anticipa.

### Firma de EventPublisher

`void publish(DomainEvent event)` (interfaz en `shared:domain.events`). Type-safe: recibe `DomainEvent`, no `Object`. El use case la inyecta como interfaz; Spring resuelve la implementación (`SpringModulithEventPublisher` por defecto, `RabbitMQEventPublisher` como fallback). Detalle en **"Eventos asíncronos — RabbitMQ"**.

---

## Java 21 — Uso Balanceado

Aplicar features de Java 21 **cuando aporten claridad o seguridad**, no por moda. Reglas concretas:

### Features recomendadas

| Feature | Cuándo usar | Ejemplo |
|---|---|---|
| `record` | `Command`, `ReadModel`, `RequestDTO`, payloads de eventos (inmutabilidad + `equals`/`hashCode` gratis) | `public record CrearFichaPerfilCommand(String tituloProyecto, UUID asesorFichaId) {}` |
| `sealed interface` + `permits` | Jerarquías cerradas técnicas — uso actual en el proyecto: `NodoFiltro`, `CampoSpec` de `shared:domain`/`shared:postgres`. No para modelar estados del dominio en este proyecto. | `sealed interface NodoFiltro permits NodoFiltro.Predicado, NodoFiltro.Grupo {}` |
| Pattern matching para `switch` | Ramificación sobre `sealed interface` (típicamente sobre `NodoFiltro` o `CampoSpec`) | `return switch (campo) { case CampoSpec.Texto t -> ...; case CampoSpec.Uuid u -> ...; };` |
| Pattern matching para `instanceof` | Donde antes había `instanceof` + cast | `if (evento instanceof FichaCreadaEvent f) { use(f.getTitulo()); }` |
| Text blocks (`"""..."""`) | SQL inline, JSON de test, plantillas | `String sql = """ SELECT ... """;` |
| `var` | Variables locales con tipo evidente por RHS | `var ficha = Ficha.crear("titulo");` |

### Features NO recomendadas en este proyecto

| Feature | Motivo |
|---|---|
| **`record` para entidades de dominio** | Las entidades Arquisoft requieren constructor privado + factory methods `crear`/`reconstruir`. Un `record` tiene constructor público y no permite el patrón. Usar `class` inmutable con campos `final`. |
| **Virtual threads manuales (`Thread.ofVirtual`, `Executors.newVirtualThreadPerTaskExecutor`)** | `spring.threads.virtual.enabled: true` ya los gestiona para Tomcat, `@Async` y RabbitMQ listeners. Crear executors manuales es innecesario y a menudo perjudicial. |
| **`record` + Lombok mezclados** | Redundante y confuso — si es `record`, no usar `@Data`/`@Builder`. |

### Regla de oro

> Si la feature no mejora la claridad del código específico que estás escribiendo, **no la uses**. Un `switch` clásico está bien; un `instanceof` con cast explícito está bien.

---

## Javadoc — Política del Proyecto

> **Regla del proyecto:** NO se incluye Javadoc descriptivo (`/** ... */` con `@param`/`@return`/descripción) en clases, interfaces ni métodos del código fuente. **El código debe ser autodescriptivo.** Los nombres de clases, métodos y variables (en español para negocio, inglés para sufijos técnicos) ya comunican la intención.

### Qué NO se escribe

```java
// ❌ MAL — Javadoc redundante que solo repite la firma del método
/**
 * Ejecuta el caso de uso con el input dado y retorna el resultado.
 *
 * @param input comando o criterio que dispara el caso de uso
 * @return resultado producido por el caso de uso
 */
O ejecutar(I input);

// ❌ MAL — comentario que explica lo evidente
/**
 * Guarda la ficha de perfil en la base de datos.
 *
 * @param ficha la ficha a guardar
 */
void guardar(FichaPerfilAggregate ficha);

// ❌ MAL — Javadoc en clases triviales (records, DTOs, excepciones simples)
/**
 * DTO de request para crear una ficha de perfil.
 */
public record CrearFichaPerfilRequestDTO(...) {}
```

### Qué SÍ se escribe

**Solo se permite un comentario** (NO Javadoc completo con `@param`/`@return`) cuando el "por qué" del código no es evidente:

```java
// ✅ BIEN — comentario breve que aclara una decisión no obvia
// Se usa reconstruir() en lugar de crear() porque el UUID viene de BD; crear() generaría uno nuevo.
return jpaRepository.findById(id).map(FichaPerfilMapper::toDomain);

// ✅ BIEN — comentario que justifica una excepción a una regla del proyecto
// @SuppressWarnings necesario: Spring genera un cast inseguro al inyectar el bean
// desde el parent context. Se documentó en ADR-014.
@SuppressWarnings("unchecked")
private Map<String, Object> claims;
```

### Excepciones a la regla (los únicos lugares donde Javadoc completo SÍ aplica)

| Caso | Justificación |
|---|---|
| Clases **base del módulo `shared`** que serán heredadas por múltiples contextos (ej. `AggregateRoot`, `DomainEvent`, `QueryCriteria`, `EventPublisher`) | Son el contrato del framework interno del proyecto; sus usuarios son otros desarrolladores que no verán su implementación. |
| Métodos abstractos en clases del `shared` que cada subclase DEBE implementar (ej. `DomainEvent.getEventTopic()`) | El contrato debe documentarse claramente porque diferentes contextos lo implementarán. |
| Anotaciones públicas custom del proyecto (si las hubiera) | Documentación del API para quien las consume. |

**En los 7 contextos de negocio, NO hay Javadoc descriptivo.** Ni en aggregates, ni en use cases, ni en adapters, ni en controllers, ni en tests, ni en DTOs.

### Por qué esta regla

1. **Reduce ruido visual.** Los archivos del proyecto son más cortos y se leen más rápido.
2. **Evita doc obsoleto.** El Javadoc desactualizado miente; el código no.
3. **Obliga a buenos nombres.** Si necesitas un párrafo para explicar qué hace un método, su nombre o su firma están mal.
4. **Consume menos tokens en LLMs.** Los agentes generan y analizan menos texto innecesario.

### Regla para los agentes (implementador, tester, planificador)

> Cuando generes código en el proyecto, **NO incluyas bloques `/** ... */`** en clases, métodos, constructores ni campos del dominio, application o infrastructure de los 7 contextos. Si una decisión no es obvia, usa un **comentario de una sola línea con `//`** justo encima de la línea relevante.

---

## Tipos de Use Case y sus Tests

No todos los use cases son iguales — y los tests apropiados dependen del tipo.
Antes de planificar tests, identifica qué tipo de use case estás trabajando:

### Use Case de Escritura (crear, actualizar, eliminar)

Características:
- Modifica el estado del Aggregate Root.
- Genera eventos de dominio (`crear()` los acumula, métodos de negocio como `aprobar()` también).
- El use case drena los eventos tras persistir y los publica vía `EventPublisher`.

Tests apropiados:
- **domain:** ciclo completo de eventos (`publishEvent` interno del factory → `drainUnPublishedEvents()` retorna y limpia), `reconstruir()` no emite eventos, invariantes del constructor.
- **application:** flujo exitoso, error de repositorio, drenado de eventos verificado con `verify(eventPublisher).publish(...)`.
- **infrastructure:** controller con códigos HTTP correctos (201, 400, 401, 403), repositorio guarda y reconstruye con `reconstruir()`.

### Use Case de Consulta (listar, buscar, obtener)

Características:
- **NO** modifica estado.
- **NO** genera eventos de dominio.
- Devuelve datos (DTO o lista de DTOs).
- Puede tener filtros, paginación, ordenamiento.

Tests apropiados:
- **domain:** validaciones de parámetros del Criteria (whitelist de campos filtrables/ordenables, profundidad del árbol) si la HU usa Criteria.
- **application:** flujo con datos válidos, lista vacía cuando no hay resultados, excepción cuando los filtros son inválidos.
- **infrastructure:** controller con códigos HTTP correctos (200, 400, 404, 401, 403), repositorio retorna lista esperada.

**Tests que NO aplican a use cases de consulta:**
- ❌ Ciclo de eventos del Aggregate Root (no hay eventos).
- ❌ Verificación de `eventPublisher.publish(...)` (no se publica nada).
- ❌ `drainUnPublishedEvents()` / `getUnPublishedEvents()`.
- ❌ Validación de que `reconstruir()` no emite eventos (irrelevante en flujo de lectura).

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
    Ficha ficha = Ficha.crear("Mi título");
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

✅ Si necesitas mucho un helper específico, **promuévelo a una clase aparte** con responsabilidad clara (ej. un converter o utilitario en `domain/util/`) y testéalo allí.

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
3. **El UseCase NO devuelve un `ResponseDTO`.** Los UseCases write devuelven una de tres opciones declarada por el plan: **(A) `UUID`** del recurso creado (caso por defecto, el más común), **(B) `void`** vía `VoidInputPort<Command>` (no hay nada útil que devolver al cliente), o **(C) un objeto específico** del dominio o `application` (típicamente un `ReadModel` cuando el cliente necesita el recurso completo tras crearlo — patrón REST común). Los UseCases read devuelven `ReadModel` o `PaginatedResult<ReadModel>`. **No existe un "ResponseDTO" intermedio** entre la capa de aplicación y el adaptador REST: el `InputAdapter` serializa directamente el valor retornado a JSON. La opción C requiere justificación explícita en el plan (sección 8) porque rompe la simetría con la convención por defecto (A).
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

import com.arquisoft.shared.query.QueryCriteria;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
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

### Excepción de dominio

```java
package com.arquisoft.fichas.domain.fichaPerfil.exception;

import com.arquisoft.shared.exception.DomainException;

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

import com.arquisoft.shared.events.AggregateRoot;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.fichas.domain.fichaPerfil.event.FichaPerfilCreadaEvent;
import com.arquisoft.shared.message.FichasMessages;
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

   public static FichaPerfilAggregate reconstruir(UUID id, String titulo, UUID asesorId, String estado) {
      FichaPerfilAggregate a = new FichaPerfilAggregate();
      a.id = id; a.tituloProyecto = titulo; a.asesorFichaId = asesorId; a.estado = estado;
      return a;
   }

   private void setTituloProyecto(String titulo, ValidationResult result) {
      if (!DomainValidator.notBlank(titulo,
              FichasMessages.FichaPerfil.CAMPO_TITULO,
              FichasMessages.FichaPerfil.TITULO_REQUERIDO, result)) return;
      this.tituloProyecto = titulo.trim();
   }

   private void setAsesorFichaId(UUID id, ValidationResult result) {
      if (!DomainValidator.notNull(id,
              FichasMessages.FichaPerfil.CAMPO_ASESOR_FICHA_ID,
              FichasMessages.FichaPerfil.ASESOR_REQUERIDO, result)) return;
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

import com.arquisoft.shared.events.DomainEvent;
import java.util.UUID;

public final class FichaPerfilCreadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "fichas.ficha_perfil.creada";
    public static final String EVENT_TYPE  = "FichaPerfilCreadaEvent";

    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final UUID asesorFichaId;

    public FichaPerfilCreadaEvent(UUID fichaPerfilId, String titulo, UUID asesorFichaId) {
        super(EVENT_TOPIC, EVENT_TYPE);          // base solo recibe (eventTopic, eventType)
        this.fichaPerfilId  = fichaPerfilId;
        this.tituloProyecto = titulo;
        this.asesorFichaId  = asesorFichaId;
    }

    public UUID getFichaPerfilId() { return fichaPerfilId; }
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
    void guardar(FichaPerfilAggregate aggregate);
    Optional<FichaPerfilAggregate> buscarPorId(UUID id);
}
```

#### 5. Puerto de entrada (write — en `application`)

```java
// application/fichaPerfil/command/port/in/CrearFichaPerfilInputPort.java
package com.arquisoft.fichas.application.fichaPerfil.command.port.in;

import com.arquisoft.shared.inputport.InputPort;
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
import com.arquisoft.shared.events.EventPublisher;
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
   @Transactional(transactionManager = "fichasTransactionManager")  // qualifier obligatorio si emite eventos
   public UUID ejecutar(CrearFichaPerfilCommand command) {
      FichaPerfilAggregate aggregate = FichaPerfilAggregate.crear(
              command.tituloProyecto(), command.asesorFichaId());

      fichaPerfilOutputPort.guardar(aggregate);

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
   @PreAuthorize("hasAuthority('fichas:ficha-perfil:create')")
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
> - El `@PreAuthorize` usa client roles con formato `{contexto}:{recurso}:{accion}` **en kebab-case** (todo minúsculas, guiones entre palabras del recurso, ej. `fichas:ficha-perfil:create`). **Nunca** camelCase ni MAYÚSCULAS.

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
   public void guardar(FichaPerfilAggregate aggregate) {
      jpaRepository.save(mapper.toJpaEntity(aggregate));
   }

   @Override
   public Optional<FichaPerfilAggregate> buscarPorId(UUID id) {
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
   @PreAuthorize("hasAuthority('fichas:ficha-perfil:view')")
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

### Manejo de Excepciones — `GlobalAppExceptionHandler` centralizado (regla por defecto)

> **Regla del proyecto:** las excepciones del dominio se manejan **centralizadamente** en `GlobalAppExceptionHandler` (`shared:web`) por jerarquía de su clase base. **Los contextos de negocio (`fichas`, `proyectos`, `artefactos`, `repositorio_artefactos`, `entregables`, `evaluaciones`) NO crean handlers propios** — reutilizan el del shared. La única excepción es `seguridad`, que mantiene `SeguridadGlobalExceptionHandler` por colisión de nombres con `org.springframework.security.core.AuthenticationException`.

#### Cómo funciona el handler centralizado

`GlobalAppExceptionHandler` (`com.arquisoft.shared.web.exception.GlobalAppExceptionHandler`) resuelve el código HTTP recorriendo la jerarquía de superclases de la excepción hasta encontrar un mapeo en este mapa fijo:

| Clase base | HTTP | Mensaje genérico |
|---|---|---|
| `DomainException` | **422** Unprocessable Content | "Error de dominio" |
| `ApplicationException` | **400** Bad Request | "Error de aplicación" |
| `InfrastructureException` | **503** Service Unavailable | "Servicio no disponible" |
| `DomainValidationException` | **422** + `fieldErrors[]` | Notification Pattern |
| fallback (`Exception`) | **500** Internal Server Error | "Error interno" |

El handler construye el body con `ErrorResponseDTO.fromBaseException(...)` — el `message` y el `errorCode` vienen **directamente del constructor de la excepción**, así que el cliente recibe el mensaje exacto que el desarrollador puso en la excepción.

#### Cómo extender una excepción para que dispare el HTTP correcto

El implementador elige la clase base según la semántica del error:

| Semántica | Extiende | HTTP resultante |
|---|---|---|
| Recurso duplicado / ya existe en BD | `ApplicationException` | 400 |
| Recurso no encontrado | `ApplicationException` | 400 |
| Parámetro inválido / filtro inválido | `ApplicationException` | 400 |
| Invariante del aggregate violada (regla de negocio) | `DomainException` | 422 |
| Estado inválido / transición de estado prohibida | `DomainException` | 422 |
| Validación multi-campo con Notification Pattern | `DomainValidationException` | 422 + `fieldErrors[]` |
| Fallo de infraestructura (BD, RabbitMQ, Keycloak caído) | `InfrastructureException` | 503 |

**Ejemplo de excepción de dominio bien definida:**

```java
// fichas/application/.../fichaPerfil/exception/FichaPerfilDuplicadaException.java
package com.arquisoft.fichas.application.fichaPerfil.exception;

import com.arquisoft.shared.exception.ApplicationException;

public class FichaPerfilDuplicadaException extends ApplicationException {
    public FichaPerfilDuplicadaException(String titulo) {
        super("Ya existe una ficha de perfil con el título: " + titulo, "FICHA_PERFIL_DUPLICADA");
    }
}
```

Al lanzarse, el cliente recibe:

```json
{
  "status": 400,
  "error": "Error de aplicación",
  "errorCode": "FICHA_PERFIL_DUPLICADA",
  "message": "Ya existe una ficha de perfil con el título: Sistema de inventarios",
  "path": "/fichas-perfil"
}
```

Sin crear handler propio. Sin tocar `shared:web`.

#### Ubicación de excepciones en el contexto

> **Regla dura — La ubicación de la excepción la determina la clase base que extiende, NO el archivo donde se lanza.** Si una excepción extiende `ApplicationException`, va en `application/`, aunque también la consuma un controller. Si extiende `DomainException`, va en `domain/`, aunque la lance el use case. Mezclar esto (ej. `ApplicationException` ubicada en `domain/`) es **violación bloqueante**: rompe la dirección de dependencias del proyecto — la capa `domain` no puede conocer clases base de `application`/`infrastructure`.

| Clase base que extiende | Ubicación obligatoria | Por qué |
|---|---|---|
| `DomainException` | `domain/{entidad}/exception/` | Invariantes del aggregate o estado inválido — viven con la entidad raíz. La clase base `DomainException` vive en `shared:domain.exception` (capa `domain`). |
| `DomainValidationException` | `domain/{entidad}/exception/` | Notification Pattern multi-campo — también es del dominio. |
| `ApplicationException` | `application/{entidad}/exception/` | Recurso no encontrado / duplicado / regla de orquestación que **no es invariante del aggregate** — la decisión la toma el use case. La clase base vive en `shared:exception` (consumida desde `application`). **Ubicación directa bajo `{entidad}/`, sin anidar `command/` o `query/`**: la excepción pertenece al concepto entidad, no al slice CQRS. |
| `InfrastructureException` | `infrastructure/exception/` | Fallos técnicos (JPA, RabbitMQ caído, Keycloak inaccesible) — solo conocidos en `infrastructure`. |

**Casos típicos por tipo y dónde duele si se ubica mal:**

| Excepción ejemplo | Extiende | Ubicación correcta | Si se ubica en `domain/` por error |
|---|---|---|---|
| `FichaPerfilDuplicadaException` | `ApplicationException` | `application/fichaPerfil/exception/` | La capa `domain` no debe conocer la regla "ya existe en BD" (eso es decisión del use case tras consultar el repositorio). Además, importar `ApplicationException` desde `domain/` viola la dirección de dependencias. |
| `AsesorFichaNoEncontradoException` | `ApplicationException` | `application/asesorFicha/exception/` | Igual: "no encontrado en BD" es decisión del use case, no del aggregate. Vive bajo la entidad `asesorFicha` aunque la lance el use case de `fichaPerfil`. |
| `FichaPerfilEstadoInvalidoException` | `DomainException` | `domain/fichaPerfil/exception/` | Esta sí va en `domain/` porque "no se puede aprobar una ficha en estado BORRADOR" es invariante del aggregate. |
| `FichaPerfilTituloInvalidoException` | `DomainValidationException` | `domain/fichaPerfil/exception/` | Notification Pattern aplicado en el constructor del aggregate — invariante de dominio. |

**Cómo decidir rápido:** pregúntate "¿esta excepción la lanza el aggregate en su propio constructor / método de negocio, sin ayuda de un repositorio?"
- **Sí** → invariante del dominio → extiende `DomainException` → vive en `domain/{entidad}/exception/`.
- **No, el use case la lanza tras consultar un puerto** (repositorio, servicio externo) → es decisión de orquestación → extiende `ApplicationException` → vive en `application/{entidad}/exception/` (directamente bajo la entidad, sin anidar `command/` o `query/`).

#### Cuándo SÍ crear un handler propio del contexto

Solo en **dos casos concretos**:

1. **Colisión de nombre con clases del framework.** Caso real: `seguridad` define su propio `AuthenticationException` que colisiona con `org.springframework.security.core.AuthenticationException`. Sin handler propio con `@Order(HIGHEST_PRECEDENCE)`, Spring puede enrutar mal la excepción. Por eso `SeguridadGlobalExceptionHandler` existe.

2. **HTTP status fuera del mapeo de la jerarquía base.** Si una excepción de dominio necesita un código que no corresponda al de su clase base (ej. 409 Conflict en lugar de 400 para duplicados, o 404 explícito para "no encontrado" en lugar de 400), se requiere un handler propio del contexto con `@Order(Ordered.HIGHEST_PRECEDENCE)` que sobrescriba al global.

**En la mayoría de HUs nuevas, NINGUNO de esos dos casos aplica.** El planificador y el implementador asumen por defecto el handler centralizado.

#### Tabla de nombres por contexto (solo si se requiere handler propio por uno de los dos casos anteriores)

| Contexto Gradle | Nombre del handler | Archivo |
|---|---|---|
| `seguridad` | `SeguridadGlobalExceptionHandler` | `seguridad/.../infrastructure/adapter/in/web/SeguridadGlobalExceptionHandler.java` |
| `fichas` | `FichasGlobalExceptionHandler` | `fichas/.../infrastructure/exception/FichasGlobalExceptionHandler.java` |
| `proyectos` | `ProyectosGlobalExceptionHandler` | `proyectos/.../infrastructure/exception/ProyectosGlobalExceptionHandler.java` |
| `artefactos` | `ArtefactosGlobalExceptionHandler` | `artefactos/.../infrastructure/exception/ArtefactosGlobalExceptionHandler.java` |
| `repositorio_artefactos` | `RepositorioArtefactosGlobalExceptionHandler` | `repositorio_artefactos/.../infrastructure/exception/RepositorioArtefactosGlobalExceptionHandler.java` |
| `entregables` | `EntregablesGlobalExceptionHandler` | `entregables/.../infrastructure/exception/EntregablesGlobalExceptionHandler.java` |
| `evaluaciones` | `EvaluacionesGlobalExceptionHandler` | `evaluaciones/.../infrastructure/exception/EvaluacionesGlobalExceptionHandler.java` |

**Regla de PascalCase:** los contextos con underscore en su nombre Gradle (`repositorio_artefactos`) se convierten a PascalCase eliminando el underscore (`RepositorioArtefactos`).

**Estado actual del proyecto:** solo `seguridad` tiene handler propio. Los demás contextos NO lo tienen ni deben tenerlo por defecto.

#### Plantilla canónica del handler de contexto (solo si aplica uno de los dos casos)

```java
package com.arquisoft.{contexto}.infrastructure.exception;

import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import com.arquisoft.{contexto}.domain.{entidad}.exception.{Entidad}DuplicadaException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class {Contexto}GlobalExceptionHandler {

    @ExceptionHandler({Entidad}DuplicadaException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicada(
            {Entidad}DuplicadaException ex, HttpServletRequest request) {
        log.warn("Recurso duplicado en {}: [{}] {}", request.getRequestURI(), ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponseDTO.fromBaseException(ex, "Conflict", HttpStatus.CONFLICT, request.getRequestURI()));
    }
}
```

> **Notas:**
> - `@Order(HIGHEST_PRECEDENCE)` garantiza que este handler gane sobre `GlobalAppExceptionHandler` (que tiene `LOWEST_PRECEDENCE`) cuando ambos pueden manejar la excepción.
> - El handler de contexto **solo** maneja excepciones específicas que requieren HTTP especial. No incluye fallback de `DomainException` ni `Exception` — esos los maneja siempre el global.
> - **NUNCA** incluyas `@ExceptionHandler(Exception.class)`, `@ExceptionHandler(MethodArgumentNotValidException.class)`, `@ExceptionHandler(AccessDeniedException.class)` ni `@ExceptionHandler(AuthorizationDeniedException.class)` en un handler de contexto. Esas son cross-cutting y viven exclusivamente en `GlobalAppExceptionHandler` de `shared:web`.

#### Reglas inviolables

1. **Por defecto, NO se crean handlers de contexto.** Las excepciones extienden la clase base correcta (`DomainException` / `ApplicationException` / `InfrastructureException`) y `GlobalAppExceptionHandler` resuelve el HTTP por jerarquía.
2. **El mensaje al cliente viene de `getMessage()` de la excepción.** Por tanto el constructor de la excepción debe construir un mensaje claro y útil para el frontend (sin filtrar PII).
3. **El `errorCode` es obligatorio.** Todas las excepciones llaman a `super(message, errorCode)` con un código trazable (formato `ENTIDAD_ACCION` en SCREAMING_SNAKE_CASE, ej. `FICHA_PERFIL_DUPLICADA`).
4. **Si la jerarquía base no aplica al HTTP que se necesita**, se crea handler de contexto con `@Order(HIGHEST_PRECEDENCE)` (caso excepcional documentado en el plan).
5. **Los tests de controller NO deben afirmar 500** para inputs inválidos. Si un test espera 500, indica una excepción mal categorizada (extiende la clase base equivocada o `RuntimeException` directo) — corrige la excepción, no el test.

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

### Convención de client roles: `contexto:recurso:accion` (todo en kebab-case)

Cada client role declara qué acción se puede realizar sobre qué recurso de qué contexto. **Todo el client role va en kebab-case** (minúsculas, palabras separadas por guiones). **Nunca** camelCase, snake_case ni MAYÚSCULAS.

| Componente | Significado | Formato | Ejemplos |
|---|---|---|---|
| `contexto` | Bounded context Gradle | kebab-case (palabras del nombre Gradle separadas por guiones) | `fichas`, `proyectos`, `entregables`, `evaluaciones`, `repositorio-artefactos`, `artefactos`, `seguridad` |
| `recurso` | Entidad o recurso del contexto | kebab-case (palabras separadas por guiones, NO camelCase) | `ficha-perfil`, `proyecto-grado`, `entregable`, `evaluacion-definitiva`, `usuario` |
| `accion` | Verbo CRUD o acción de negocio | minúscula, una sola palabra | `view`, `create`, `update`, `delete`, `approve`, `submit`, `evaluate` |

**Ejemplos válidos (kebab-case):**
- `fichas:ficha-perfil:view` → ver fichas de perfil
- `fichas:ficha-perfil:create` → crear fichas de perfil
- `proyectos:proyecto-grado:approve` → aprobar proyecto de grado
- `evaluaciones:evaluacion-definitiva:submit` → enviar evaluación definitiva
- `repositorio-artefactos:artefacto:upload` → subir artefacto al repositorio

**Ejemplos inválidos (NO usar):**
- `fichas:fichaPerfil:create` ❌ camelCase en el recurso
- `fichas:Ficha-Perfil:create` ❌ MAYÚSCULAS iniciales
- `Fichas:ficha-perfil:CREATE` ❌ mayúsculas en cualquier parte
- `repositorio_artefactos:artefacto:upload` ❌ underscore en el contexto

> **Regla de conversión:** si el nombre Gradle del contexto tiene underscore (`repositorio_artefactos`), en el client role se convierte a guión (`repositorio-artefactos`). Si la entidad tiene varias palabras (`FichaPerfilAggregate`), el recurso del client role la representa con guiones (`ficha-perfil`).

### Mapeo realm role → client roles

Cada **rol realm** tiene asignados varios **client roles** según sus responsabilidades. Un mismo client role puede pertenecer a varios roles realm:

| Client role | Roles realm que lo poseen | Razón |
|---|---|---|
| `fichas:ficha-perfil:view` | `coordinador`, `asesor-ficha`, `representante-comite` | Todos pueden consultar fichas |
| `fichas:ficha-perfil:create` | `coordinador`, `asesor-ficha` | Coordinador y asesor crean fichas |
| `fichas:ficha-perfil:approve` | `representante-comite` | Solo el comité aprueba |

> **El planificador es responsable de declarar este mapeo** en cada plan: por cada endpoint/acción, lista el client role nuevo y a qué roles realm debe asignarse en Keycloak. Esto permite al equipo de seguridad configurar Keycloak en paralelo con el desarrollo.

### Uso en controllers

```java
// ✅ CORRECTO — usa hasAuthority con client role en kebab-case
@PostMapping
@PreAuthorize("hasAuthority('fichas:ficha-perfil:create')")
public ResponseEntity<UUID> crear(@Valid @RequestBody CrearFichaPerfilRequestDTO req) {
    // ...
}

// ❌ MAL — camelCase en el recurso
@PreAuthorize("hasAuthority('fichas:fichaPerfil:create')")  // ❌

// ❌ MAL — MAYÚSCULAS en cualquier parte
@PreAuthorize("hasAuthority('Fichas:Ficha-Perfil:CREATE')")  // ❌

// ❌ MAL — hasRole con realm role (convención antigua)
@PreAuthorize("hasRole('ASESOR_FICHA')")  // ❌
@PreAuthorize("hasRole('asesor-ficha')")  // ❌

// ❌ MAL — múltiples authorities con OR (preferir uno solo y asignarlo a varios roles realm en Keycloak)
@PreAuthorize("hasAuthority('fichas:ficha-perfil:view') or hasAuthority('fichas:ficha-perfil:admin')")  // ❌
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
  ├── routing key "fichas.ficha_perfil.creada"
  │     ├── Queue "proyectos.fichas.ficha_perfil.creada" (durable + DLX)
  │     └── Queue "evaluaciones.fichas.ficha_perfil.creada" (durable + DLX)
  │
  └── ... otras routing keys
```

> **Formato del topic:** `{contexto}.{entidad}.{accion}`, **todo en minúsculas y `snake_case`** (tres segmentos `[a-z][a-z_]*`). `DomainEvent` valida este formato en el constructor — un topic con camelCase (ej. `fichas.fichaPerfil.creada`) lanza `IllegalArgumentException`.

| Recurso | Convención | Ejemplo |
|---|---|---|
| Exchange único | `arquisoft.events` (Topic, durable) | — |
| Routing key | `{contexto}.{entidad}.{accion}` (la constante `EVENT_TOPIC` del evento) | `fichas.ficha_perfil.creada` |
| Cola por consumidor | `{contextoConsumidor}.{eventTopic}` durable | `proyectos.fichas.ficha_perfil.creada` |
| DLX | `arquisoft.dlx` + routing key `{queue}.dead` | — |

### Publicación — `SpringModulithEventPublisher` (Spring Modulith + Outbox Pattern)

El publicador principal del proyecto es **`SpringModulithEventPublisher`** (en `shared:amqp`), que implementa el puerto `EventPublisher` de `shared:domain.events`. Internamente delega a `ApplicationEventPublisher.publishEvent(...)`, y Spring Modulith intercepta esa publicación para aplicar **Outbox Pattern con tabla `event_publication` por contexto**:

1. **Dentro de la transacción del use case:** persiste el evento en la tabla `event_publication` **de la BD del contexto** (mismo `DataSource` del aggregate) con `completion_date = NULL`. El INSERT está en la misma transacción que el `save()` del aggregate — atomicidad real, no práctica.
2. **Tras el commit:** publica al exchange `arquisoft.events` usando `event.getEventTopic()` como routing key, vía la configuración `ModulithAmqpExternalizationConfig`.
3. **Si el broker rechaza o está caído:** el evento queda en `event_publication` con `completion_date = NULL`. El bean `FailedEventRetryConfig` reintenta cada 5 min mediante `FailedEventPublications.resubmit(...)` con `withMinAge(2m)`.

**Cómo Modulith sabe a qué BD escribir:** el proyecto reemplaza el `JdbcEventPublicationRepository` por un componente custom — **`ContextAwareEventPublicationRepository`** (en `src/main/java/com/arquisoft/config/outbox/`). En el arranque detecta automáticamente qué `DataSource`s tienen tabla `event_publication`; al publicar un evento, busca la transacción activa (vía `TransactionSynchronizationManager`) y enruta el INSERT al `JdbcTemplate` correspondiente. Las queries de estado (incompletos, fallidos, conteos) hacen fan-out a todas las tablas detectadas. La autoconfig de Modulith para JDBC está **explícitamente excluida** en `application.yml` (`JdbcEventPublicationAutoConfiguration` en la lista de `spring.autoconfigure.exclude`).

**Implicación para el use case:** la anotación `@Transactional` debe llevar el **qualifier explícito** del transaction manager del contexto, para que Modulith escriba en la BD correcta. Ejemplo: `@Transactional(transactionManager = "usuariosTransactionManager")` en `usuarios`, `@Transactional(transactionManager = "fichasTransactionManager")` en `fichas`. Sin el qualifier puede fallar el routing si hay otro `@Primary` en el contexto.

**Implicación para el plan/implementación:** todo contexto que emita eventos necesita la migración Flyway `crear_event_publication.sql` (con el siguiente número de versión del contexto) en `db/migration/{contexto}/`. Los contextos que NO emiten eventos no necesitan la tabla — el `ContextAwareEventPublicationRepository` los ignora.

Existe además un **fallback `RabbitMQEventPublisher`** anotado con `@ConditionalOnMissingBean(EventPublisher.class)` — solo se activa si Spring Modulith no está en el classpath. Maneja publish directo con backoff exponencial 3× (500ms → 1s → 2s) ante `AmqpException`. En la operación normal del proyecto NO se usa este fallback.

**Headers de trazabilidad** (inyectados por `traceHeadersPostProcessor` en `RabbitTemplate`, válidos para ambos publicadores):

- `X-Trace-Id` (correlación de request → eventos → side-effects). Si el MDC está vacío (caso retry de Modulith), se genera un UUID.
- `X-User-Id` (auditoría de quién originó el evento). Fallback: `"SYSTEM"`.

**Implicación para el use case:** inyecta la interfaz `EventPublisher` de `com.arquisoft.shared.events` (vive en `shared:domain`). NO conoce que hay Outbox por debajo — eso es responsabilidad de `shared:amqp`. El patrón es:

```java
aggregate.drainUnPublishedEvents().forEach(eventPublisher::publish);
```

`drainUnPublishedEvents()` retorna y limpia la lista en una sola operación atómica. **NO existe `clearUnPublishedEvents()`** — no llamar a un método con ese nombre.

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

   public static final String QUEUE_NAME = "proyectos.fichas.ficha_perfil.creada";

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
| Publica a RabbitMQ | `shared:amqp` (`SpringModulithEventPublisher`) | Delega a `ApplicationEventPublisher`; Modulith persiste en `event_publication` y publica tras commit |
| Outbox por contexto | `ContextAwareEventPublicationRepository` (`src/main/java/com/arquisoft/config/outbox/`) | Detecta DataSources con tabla `event_publication` en el arranque; enruta INSERT a la transacción activa; fan-out en queries de estado |
| Reintentos de FAILED | `FailedEventRetryConfig` | Scheduler `@Scheduled` cada 5 min: `FailedEventPublications.resubmit(...)` con `withMinAge(2m)` |
| Externalización a exchange | `shared:amqp` (`ModulithAmqpExternalizationConfig`) | `EventExternalizationConfiguration` con routing por `getEventTopic()` |
| Configura cola + DLX | `{contextoConsumidor}/infrastructure/config/` | `@Configuration` con Queue + Binding |
| Consume el mensaje | `{contextoConsumidor}/infrastructure/{entidad}/command/adapter/in/amqp/` | Extiende `AbstractEventConsumer` |
| Payload del consumidor | mismo paquete del consumer | `record` local, **NO** importa el evento del publicador |

### Reglas inviolables

1. **`AggregateRoot` solo acumula eventos en memoria.** Nunca conoce `EventPublisher`.
2. **El UseCase write drena con `drainUnPublishedEvents()`** (un solo método que retorna + limpia atómico). Nunca itera el aggregate manualmente. **NO existe `clearUnPublishedEvents()`** en `AggregateRoot` — código que lo use NO compila.
3. **El use case inyecta `EventPublisher`** (interfaz de `shared:domain.events`). Quién la provee (`SpringModulithEventPublisher` en operación normal, `RabbitMQEventPublisher` como fallback) lo decide Spring — el use case no conoce la implementación.
   **Si el use case emite eventos**, el `@Transactional` lleva qualifier explícito: `@Transactional(transactionManager = "{contexto}TransactionManager")`. Sin qualifier, el outbox puede romperse silenciosamente al escribir en una BD equivocada.
4. **El consumidor declara su propio `record` payload.** Nunca importa la clase del evento del publicador (cero acoplamiento entre contextos).
5. **Toda cola tiene DLX configurado** (`x-dead-letter-exchange`). Sin DLX, mensajes en error se re-encolan eternamente.
6. **El consumer extiende `AbstractEventConsumer`** y usa `withCorrelation(message, channel, runnable)`. No implementa ACK/NACK manualmente.
7. **Atomicidad práctica garantizada vía Outbox Pattern de Spring Modulith.** El INSERT en `event_publication` viaja en la misma transacción que el `save()` del aggregate (mismo `JdbcTemplate` de `arquisoftEventsDataSource`). Si el broker está caído al commit, el evento queda persistido y `FailedEventRetryConfig` lo reintenta cada 5 min.

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
| `seguridad` | *(sin BD propia — usa Redis para blacklist y Keycloak para identidad)* |
| `usuarios` | `usuarios` |
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
- **Migraciones Flyway** en `{contexto}/infrastructure/src/main/resources/db/migration/{contexto}/` — el `{Contexto}DataSourceConfig` carga `classpath:db/migration/{contexto}` (ej. `classpath:db/migration/fichas`), así que el SQL DEBE ir en ese subdirectorio `{contexto}/`. Una migración fuera de ese subdir NO se ejecuta.
- **Nomenclatura Flyway (versionado secuencial por contexto):** `V{major}.{minor}__{descripcion_snake_case}.sql` (ej. `V1.0__crear_tablas_fichas_perfil.sql`, luego `V1.1__crear_estudiante.sql`). Cada contexto tiene su **propia secuencia** (su propio Flyway). La migración nueva usa el **siguiente número** tras la versión más alta existente en `db/migration/{contexto}/`: **LEE el directorio antes de elegir el número — no lo adivines (eso causa huecos como un `V3` sin `V2`) ni reutilices uno existente.**
- **Minor por defecto, major reservado:** el proyecto está en el esquema **v1**, así que toda migración nueva **incrementa el minor** (`V1.0` → `V1.1` → `V1.2`…). El salto a un **major** (`V2.0`) se reserva para un **cambio grande de esquema / nueva versión del proyecto** y es una **decisión humana explícita** — el agente **nunca** lo decide: siempre incrementa el minor.
- **Inmutabilidad de migraciones aplicadas (regla dura):** una migración que YA se aplicó a cualquier entorno (local, servidor de pruebas, prod) es **inmutable**. **NUNCA** la renombres (cambia su versión → Flyway la trata como nueva y la re-ejecuta → `relation already exists`) ni edites su contenido (cambia el checksum → Flyway falla la validación). Para modificar una tabla existente se agrega una migración NUEVA (`ALTER`) con la siguiente versión — nunca se edita la anterior.
- **Baseline (`baselineOnMigrate=true`):** todos los `{Contexto}DataSourceConfig` usan baseline. Si Flyway corre por primera vez contra una BD que **ya tenía tablas**, crea una baseline (ej. versión `1`) y **omite** toda migración con versión ≤ baseline (`V1.0` == `1` → omitida en esa BD; las tablas ya están). Una migración nueva debe tener versión **mayor** que la baseline para ejecutarse (ej. `V1.1` corre porque `1.1 > 1`).
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
- `crear(...)` genera el UUID con `UUID.randomUUID()`.
- `reconstruir(...)` recibe el UUID desde persistencia.
- Para campos `Instant` autogenerados en `crear(...)` (ej. `fechaActualizacion`), usar `UtilDate.generateNewInstantNow()` de `com.arquisoft.shared.util.UtilDate` (`shared:domain`). **Nunca** `Instant.now()` directamente en código de dominio.

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

Exchange único `arquisoft.events` (Topic); routing key `{contexto}.{entidad}.{accion}`. Topología completa, publicación (Spring Modulith + Outbox por contexto), consumo y reglas en **"Eventos asíncronos — RabbitMQ"**.

### Swagger / OpenAPI (ADR-011)

- `OpenApiConfig.java` global en `src/main/java/com/arquisoft/config/` (raíz de la app). **No duplicar** en módulos.
- Todo `@RestController` debe tener: `@Tag` (clase), `@Operation` + `@ApiResponses` (cada método).
- Endpoints protegidos: `@SecurityRequirement(name = "bearerAuth")`.
- Endpoints públicos (login, refresh, validate): omitir `@SecurityRequirement`.
- URL dev: `http://localhost:8080/api/swagger-ui/index.html`.

### Seguridad

- JWT decodificado vía JWK Set URI de Keycloak. `@EnableMethodSecurity(prePostEnabled = true)`.
- Autorización con `@PreAuthorize("hasAuthority('contexto:recurso:accion')")` — convención de client roles, mapeo y reglas en **"Autorización — Roles realm + Client Roles"**.
- Endpoints públicos permit-all: `/auth/login`, `/auth/refresh`, `/auth/validate`, `/actuator/health/**`, `/swagger-ui/**`, `/v3/api-docs/**`.
- CSRF deshabilitado, sesiones stateless. Rate limiting (Bucket4j) en prod: 60/min global, 3/min login.

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

# Listar todos los subproyectos
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
6. **`AggregateRoot` es condicional a eventos:** una entidad raíz extiende `AggregateRoot` SOLO si su HU emite eventos de dominio (ver "AggregateRoot — Regla Estricta"). Sin eventos = clase plana con factories `crear`/`reconstruir`. `seguridad` nunca usa `AggregateRoot`.
7. Los IDs son **siempre `UUID`**. Cualquier uso de `Long`/`Integer` como ID es un error.
8. La dirección de dependencias `domain ← application ← infrastructure` **no se negocia**.
9. Los bounded contexts **no se importan entre sí** — solo eventos RabbitMQ.
10. Versiones del stack: **no inventar**. Usar exactamente las de este skill.
11. Features de Java 21: **balanceado**. Aplicar cuando aporte claridad (records para VO y payloads de eventos, sealed para estados, text blocks para SQL, var con tipo evidente). No forzar en entidades de dominio (requieren constructor privado + factories).
12. Virtual Threads están gestionados por Spring Boot — **nunca** crear `TaskExecutor` manual.
13. **Prueba del algodón:** antes de escribir cualquier archivo, pregúntate "¿si mañana cambio esta tecnología externa, qué archivos tengo que tocar?". Si la respuesta incluye `domain/`, la lógica está mal ubicada.