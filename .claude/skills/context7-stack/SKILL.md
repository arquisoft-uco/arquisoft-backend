---
name: context7-stack
description: IDs de librerias Context7 del stack Arquisoft (Spring Boot 4.0.5, Java 21, Gradle 9.0.0). Usar antes de generar cualquier archivo Java o de configuracion para obtener documentacion actualizada y especifica por version de cada dependencia del proyecto. Incluye tabla de IDs directos validados, IDs alternativos con mas snippets, y ejemplos de consulta por tipo de archivo.
---

# Skill: context7-stack

IDs de librerias Context7 del stack Arquisoft **validados y verificados**.
Usa estos IDs directamente con `query-docs` para saltarte el paso `resolve-library-id`.

> IMPORTANTE: Siempre usar el ID de mayor score de snippets disponible para obtener
> la documentacion mas completa. Los IDs marcados con ★ son los recomendados.
>
> **Stack real:** Spring Boot 4.0.5 · Java 21 · Gradle 9.0.0 · JUnit 6.0.3 · Keycloak 26.6
> **Nota JUnit:** El ID `/websites/junit_current` cubre JUnit 5 — las anotaciones son compatibles con JUnit 6.0.3.

---

## Tabla de IDs Validados — Stack Arquisoft

> Versiones tomadas de `gradle.properties` y del BOM de Spring Boot 4.0.5 (raíz del repo). Si
> `gradle.properties` cambia, esta tabla se actualiza — no la des por buena sin mirarla.

| Libreria | ID Recomendado ★ | Snippets | Version en proyecto |
|----------|-----------------|----------|---------------------|
| Spring Boot | `/websites/spring_io_spring-boot` | 295 000+ | **4.0.5** |
| Spring Framework (MVC/Web/Tx) | `/websites/spring_io_spring-framework_reference_6_2` | 6 761 | 7.x (via Boot 4.0) |
| Spring Security + OAuth2 | `/websites/spring_io_spring-security_reference_6_5` | 11 697 | via Boot 4.0 |
| Spring AMQP / RabbitMQ | `/websites/spring_io` | 50 638 | via Boot 4.0 (broker RabbitMQ 4.2.5) |
| Spring Data JPA | `/spring-projects/spring-data-jpa` | 315 | via Boot 4.0 |
| Spring Data Redis | `/spring-projects/spring-data-redis` | 357 | via Boot 4.0 (Lettuce, Redis 7) |
| Spring Modulith | `/spring-projects/spring-modulith` | — | **2.0.0** (outbox + externalización AMQP) |
| Flyway | `/flyway/flyway` | 2 434 | **12.4.0** (via BOM; requiere `flyway-database-postgresql`) |
| JUnit 5 | `/websites/junit_current` | 5 740 | **6.0.3** (compatible con anotaciones JUnit 5) |
| Mockito | `/mockito/mockito` | 120 | via Boot 4.0 |
| AssertJ | `/assertj/assertj` | 81 | via Boot 4.0 |
| Lombok | `/projectlombok/lombok` | 638 | **1.18.36** |
| Gradle | `/websites/gradle_current_userguide` | 4 607 | **9.0.0** |
| Keycloak | `/keycloak/keycloak` | 2 453 | **26.6** (solo como IdP — sin `keycloak-admin-client`) |
| Bucket4j | `/bucket4j/bucket4j` | 301 | **8.18.0** (`com.bucket4j:bucket4j_jdk17-core`) |
| Jackson 3 | `/fasterxml/jackson-databind` | 47 | **3.1.2** vía BOM — paquete `tools.jackson.databind.*` |
| Hibernate ORM | `/hibernate/hibernate-orm` | 4 278 | via Boot 4.0 (`@Subselect`/`@Immutable`/`@Synchronize`) |
| PostgreSQL (driver) | `/websites/postgresql` | — | **42.7.2** (servidor PostgreSQL 18) |
| MinIO | `/minio/minio` | — | **8.5.12** |
| springdoc-openapi | `/springdoc/springdoc-openapi` | — | **2.8.8** |

**Trampas de versión de este stack — verifícalas antes de copiar cualquier snippet de Context7:**

- **Jackson 3:** `databind` vive en `tools.jackson.databind.*`; `com.fasterxml.jackson.databind.ObjectMapper`
  **no resuelve**. Las *anotaciones* siguen en `com.fasterxml.jackson.annotation.*`. Jackson 2
  coexiste en el classpath solo porque springdoc 2.8.8 aún depende de él.
- **Slices de test de Spring Boot 4:** `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
  y `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`. Las rutas de Boot 3
  (`org.springframework.boot.test.autoconfigure.*`) ya no existen. `@MockitoBean`, no `@MockBean`.
- **JUnit 6.0.3** con anotaciones de JUnit 5 — la doc de `/websites/junit_current` aplica.
- **Virtual Threads** ya activos por Boot: nunca declares un `TaskExecutor` propio.
- Ya no hay `jjwt` ni `logstash-logback-encoder` en el proyecto (el perfil prod usa el
  `StructuredLogEncoder` nativo de Boot 4).

### IDs alternativos utiles (mas snippets para consultas amplias)

| Cuando necesitas | ID alternativo | Snippets |
|-----------------|----------------|----------|
| Spring Boot (referencias amplias) | `/spring-projects/spring-boot` | 1 147 |
| Spring Security (referencia oficial 6.5) | `/websites/spring_io_spring-security_reference_6_5` | 11 697 |
| Spring Framework (GitHub source) | `/spring-projects/spring-framework` | 5 965 |
| RabbitMQ (docs oficiales) | `/websites/rabbitmq` | 9 586 |
| JUnit 5 (GitHub source) | `/junit-team/junit-framework` | 801 |
| Keycloak (JavaDoc API) | `/websites/keycloak_docs-api_javadocs` | 26 535 |
| Gradle (user guide completo) | `/websites/gradle` | 79 353 |

---

## Consultas por Tipo de Archivo Java

Usa estas consultas directas segun el archivo que estes generando:

### Capa domain — Entidades y Puertos

```
# Entidad de dominio inmutable con factory methods (Java puro, sin framework)
query-docs /websites/spring_io_spring-framework_reference_6_2 "domain model immutable class factory method Java 21"

# Puerto de entrada: interfaz de caso de uso
query-docs /websites/spring_io_spring-framework_reference_6_2 "use case interface service design pattern"

# Puerto de salida: interfaz de repositorio
query-docs /spring-projects/spring-data-jpa "repository interface port out hexagonal"

# Excepcion de dominio (extiende DomainException de shared:exception — nunca RuntimeException directa)
query-docs /websites/spring_io_spring-framework_reference_6_2 "custom checked exception hierarchy error code"

# Value Object Java 21
query-docs /websites/spring_io_spring-framework_reference_6_2 "value object immutable record Java 21"
```

### Capa application — DTOs y Casos de Uso

```
# Command / ReadModel / DTO como record Java 21 (sin Lombok, sin @Builder)
query-docs /websites/spring_io_spring-boot "record DTO serialization Java 21"

# Implementacion de caso de uso con @Component y @RequiredArgsConstructor
query-docs /projectlombok/lombok "RequiredArgsConstructor Component constructor injection"

# Transaccion en el Interactor con transactionManager explicito
query-docs /websites/spring_io_spring-framework_reference_6_2 "Transactional transactionManager qualifier multiple datasources"
```

### Capa infrastructure — Persistencia JPA + Flyway

```
# Entidad JPA (el DataSource ya apunta a la base del contexto — @Table nunca lleva schema)
query-docs /spring-projects/spring-data-jpa "Entity Table Column name mapping PostgreSQL"

# Entidad de solo lectura para el lado query
query-docs /hibernate/hibernate-orm "Subselect Immutable Synchronize read-only entity"

# Adaptador de repositorio (implementacion del puerto de salida)
query-docs /spring-projects/spring-data-jpa "JpaRepository save findById custom query adapter"

# Migracion Flyway: version por timestamp VyyyyMMddHHmmss, en db/migration/{contexto}/
query-docs /flyway/flyway "SQL migration versioned V naming convention"

# Flyway con modulo postgresql (flyway-database-postgresql)
query-docs /flyway/flyway "PostgreSQL specific migration flyway-database-postgresql"

# Mapeo ORM con Hibernate: columnas, relaciones, tipos
query-docs /hibernate/hibernate-orm "Column ManyToOne OneToMany fetch type lazy eager"

# HikariCP pool de conexiones
query-docs /websites/spring_io_spring-boot "HikariCP connection pool maximum-pool-size datasource"
```

### Capa infrastructure — Controllers REST

```
# Controller REST (el RequestDTO es un record desnudo: la validacion vive en Command.crear, no en @Valid)
query-docs /websites/spring_io_spring-framework_reference_6_2 "RestController RequestMapping PostMapping RequestBody ResponseEntity"

# Manejo de errores: ya lo cubre GlobalAppExceptionHandler (shared:web/handler) — un contexto no crea el suyo
query-docs /websites/spring_io_spring-framework_reference_6_2 "RestControllerAdvice ExceptionHandler response entity"

# Filtro HTTP personalizado (audit, rate limiting)
query-docs /websites/spring_io_spring-framework_reference_6_2 "OncePerRequestFilter doFilterInternal HttpServletRequest"

# Paginacion y parametros de consulta
query-docs /websites/spring_io_spring-framework_reference_6_2 "RequestParam PathVariable Pageable PageRequest"
```

### Capa infrastructure — Seguridad (Keycloak + Spring Security)

```
# Configuracion OAuth2 Resource Server con JWT para Keycloak
query-docs /websites/spring_io_spring-security_reference_6_5 "OAuth2 resource server JWT bearer token decoder"

# SecurityFilterChain con reglas por endpoint
query-docs /websites/spring_io_spring-security_reference_6_5 "SecurityFilterChain requestMatchers permitAll authenticated roles"

# Extraer usuario del SecurityContext
query-docs /websites/spring_io_spring-security_reference_6_5 "SecurityContextHolder Authentication principal JWT claims"

# Keycloak: configurar realm, client y roles
query-docs /keycloak/keycloak "realm client configuration roles Spring Boot adapter"

# Rate limiting con Bucket4j
query-docs /bucket4j/bucket4j "Bucket tryConsume refill bandwidth filter Spring"

# CORS configuration
query-docs /websites/spring_io_spring-security_reference_6_5 "CorsConfiguration CorsConfigurationSource allowedOrigins"
```

### Capa infrastructure — Mensajeria RabbitMQ

```
# Publicar evento con RabbitTemplate
query-docs /websites/spring_io "RabbitTemplate convertAndSend exchange routing key message"

# Consumir evento con RabbitListener y acknowledge manual
query-docs /websites/spring_io "RabbitListener acknowledgment manual ack Channel basicAck"

# Configurar exchange, cola y binding (declarables)
query-docs /websites/spring_io "TopicExchange Queue Binding declarables RabbitAdmin"

# Configurar Jackson como serializador de mensajes
query-docs /websites/spring_io "Jackson2JsonMessageConverter MessageConverter RabbitMQ serialization"

# Publicar evento de dominio (patron EventPublisher)
query-docs /websites/spring_io "DomainEvent publish ApplicationEventPublisher async"
```

### Capa infrastructure — Redis / Cache

```
# RedisTemplate: operaciones clave-valor
query-docs /spring-projects/spring-data-redis "RedisTemplate opsForValue set get expire TTL"

# Configurar Lettuce como cliente Redis (el que usa el proyecto)
query-docs /spring-projects/spring-data-redis "LettuceConnectionFactory client configuration Spring Boot"

# Cache con @Cacheable y @CacheEvict
query-docs /websites/spring_io_spring-boot "Cacheable CacheEvict CachePut Redis Spring Boot"
```

### Capa infrastructure — Cliente HTTP (WebClient / RestTemplate)

```
# WebClient reactivo para llamadas a APIs externas (Keycloak, Nextcloud)
query-docs /websites/spring_io_spring-framework_reference_6_2 "WebClient retrieve bodyToMono exchange HTTP client"

# RestTemplate (en uso actual para Keycloak)
query-docs /websites/spring_io_spring-framework_reference_6_2 "RestTemplate exchange postForObject HttpEntity headers"

# Manejo de errores HTTP en cliente (traducir a excepcion de dominio)
query-docs /websites/spring_io_spring-framework_reference_6_2 "HttpClientErrorException ResourceAccessException RestClientException"
```

### Notificaciones (Email + Thymeleaf)

```
# Enviar email con JavaMailSender y plantilla Thymeleaf
query-docs /websites/spring_io_spring-boot "JavaMailSender MimeMessage Thymeleaf template email"

# Configurar propiedades de correo
query-docs /websites/spring_io_spring-boot "spring.mail host port username password SSL TLS"
```

### Tests

```
# Test unitario con Mockito (patron AAA, sin contexto Spring)
query-docs /mockito/mockito "Mock InjectMocks ExtendWith MockitoExtension verify when thenReturn"

# Capturar argumentos con ArgumentCaptor
query-docs /mockito/mockito "ArgumentCaptor capture verify getValue"

# Verificar excepciones con AssertJ
query-docs /assertj/assertj "assertThatThrownBy assertThatExceptionOfType isInstanceOf hasMessage"

# Assertions fluidas con AssertJ
query-docs /assertj/assertj "assertThat isEqualTo isNotNull extracting containsExactly"

# Slice de repositorio con H2 (@SpringBootTest no se usa en este repo)
query-docs /websites/spring_io_spring-boot "DataJpaTest TestEntityManager H2 slice test"

# Test de controller con Spring Security Mock (jwt().authorities — nunca @WithMockUser)
query-docs /websites/spring_io_spring-security_reference_6_5 "MockMvc SecurityMockMvcRequestPostProcessors jwt authorities"

# Test con DynamicPropertySource (Testcontainers o H2)
query-docs /websites/junit_current "DynamicPropertySource DynamicPropertyRegistry test configuration"

# Test parametrizado JUnit 5
query-docs /websites/junit_current "ParameterizedTest ValueSource CsvSource MethodSource"
```

### Configuracion (application.yml / @Configuration)

```
# Externalizar configuracion con @Value y @ConfigurationProperties
query-docs /websites/spring_io_spring-boot "Value ConfigurationProperties prefix binding validation"

# Perfiles Spring (dev, prod)
query-docs /websites/spring_io_spring-boot "Profile ConditionalOnProperty active profiles application-dev"

# Configurar Actuator (health, metrics, prometheus)
query-docs /websites/spring_io_spring-boot "Actuator health info metrics prometheus endpoint exposure"

# Configurar virtual threads Java 21
query-docs /websites/spring_io_spring-boot "virtual threads spring.threads.virtual.enabled Java 21"
```

### Gradle (build.gradle, settings.gradle)

```
# Proyecto multi-modulo Gradle
query-docs /websites/gradle_current_userguide "multi-project subproject include settings dependencies"

# Dependencias entre subproyectos
query-docs /websites/gradle_current_userguide "implementation project path dependency subproject"

# Configurar JaCoCo para cobertura minima 75%
query-docs /websites/gradle_current_userguide "JaCoCo coverage minimum threshold jacocoTestReport"

# Centralizar versiones en gradle.properties
query-docs /websites/gradle_current_userguide "gradle.properties version catalog centralized dependency"
```

---

## Ejemplos de Uso Rapido con ID Directo

```
# Configurar RabbitListener con acknowledge manual
use library /websites/spring_io
consulta: "RabbitListener manual ack acknowledgment Channel basicAck basicNack"
```

```
# Crear migracion Flyway para la base de fichas (db/migration/fichas/, version timestamp)
use library /flyway/flyway
consulta: "versioned migration V naming convention create table"
```

```
# Configurar JWT decoder para Keycloak en Spring Security 6
use library /websites/spring_io_spring-security_reference_6_5
consulta: "JWT decoder Keycloak issuer-uri public key NimbusJwtDecoder"
```

```
# Mapear entidad JPA (@Table NUNCA lleva schema: el DataSource ya apunta a la base del contexto)
use library /spring-projects/spring-data-jpa
consulta: "Entity Table name Column insertable updatable"
```

```
# Aplicar rate limiting por IP con Bucket4j en un filtro
use library /bucket4j/bucket4j
consulta: "Bucket tryConsume refill bandwidth local rate limiting filter"
```

```
# Crear mock con Mockito y verificar interaccion
use library /mockito/mockito
consulta: "Mock InjectMocks when thenReturn verify times never"
```

---

## Problemas Comunes del MCP Context7

| Problema | Causa | Solucion |
|----------|-------|----------|
| `resolve-library-id` devuelve ID equivocado | Nombre demasiado generico | Usar directamente el ID de esta tabla |
| Documentacion desactualizada o irrelevante | ID con pocos snippets | Preferir IDs con mayor numero de snippets de la tabla de alternativas |
| Resultados sobre Spring Boot 2.x/3.x | El ID no fija version | Incluir "Spring Boot 4" en la query y contrastar con el codigo real de `fichas` antes de copiar |
| Snippets con `com.fasterxml.jackson.databind` | Doc de Jackson 2 | El proyecto usa Jackson 3 (`tools.jackson.databind`) — traducir el import |
| Snippets con `@MockBean` o `org.springframework.boot.test.autoconfigure.*` | Doc de Boot 3 | Usar `@MockitoBean` y las rutas de slice de Boot 4 |
| No encuentra configuracion de Flyway 12 | Query demasiado generica | Incluir "flyway-database-postgresql" en la query |
| Timeout o sin respuesta | Rate limit o red | Reintentar con una query mas corta |
| Snippets de Keycloak muy viejos | Version antigua | Indicar "Keycloak 26" explicitamente en la query |

> **Regla final:** Context7 da la API de la libreria, no la convención del proyecto. Ante cualquier
> choque entre un snippet y `arquisoft-arquitectura`/`arquisoft-estandares`, **gana la skill** — el
> snippet se adapta, no al revés.
