---
name: context7-stack
description: IDs de librerias Context7 del stack Arquisoft (Spring Boot 4.0.5, Java 21, Gradle 9.8.0). Usar antes de generar cualquier archivo Java o de configuracion para obtener documentacion actualizada y especifica por version de cada dependencia del proyecto. Incluye tabla de IDs directos validados, IDs alternativos con mas snippets, y ejemplos de consulta por tipo de archivo.
---

# Skill: context7-stack

IDs de Context7 validados para el stack. Usalos directo con `query-docs` y saltate
`resolve-library-id`.

## Tabla de IDs Validados — Stack Arquisoft

Versiones de `gradle.properties` y del BOM de Spring Boot 4.0.5. Si `gradle.properties` cambia, esta
tabla se actualiza: no la des por buena sin mirarla.

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
| Gradle | `/websites/gradle_current_userguide` | 4 607 | **9.8.0** |
| Keycloak | `/keycloak/keycloak` | 2 453 | **26.6** (solo como IdP — sin `keycloak-admin-client`) |
| Bucket4j | `/bucket4j/bucket4j` | 301 | **8.18.0** (`com.bucket4j:bucket4j_jdk17-core`) |
| Jackson 3 | `/fasterxml/jackson-databind` | 47 | **3.1.2** vía BOM — paquete `tools.jackson.databind.*` |
| Hibernate ORM | `/hibernate/hibernate-orm` | 4 278 | via Boot 4.0 (`@Subselect`/`@Immutable`/`@Synchronize`) |
| PostgreSQL (driver) | `/websites/postgresql` | — | **42.7.2** (servidor PostgreSQL 18) |
| MinIO | `/minio/minio` | — | **8.5.12** |
| springdoc-openapi | `/springdoc/springdoc-openapi` | — | **2.8.8** |

**IDs alternativos** (más snippets, para consultas amplias): Spring Boot `/spring-projects/spring-boot`
· Spring Framework `/spring-projects/spring-framework` · RabbitMQ `/websites/rabbitmq` · JUnit
`/junit-team/junit-framework` · Keycloak JavaDoc `/websites/keycloak_docs-api_javadocs` · Gradle
completo `/websites/gradle`.

## Trampas de versión — verifícalas antes de copiar un snippet

- **Jackson 3:** `databind` vive en `tools.jackson.databind.*`; `com.fasterxml.jackson.databind.ObjectMapper`
  **no resuelve**. Las *anotaciones* siguen en `com.fasterxml.jackson.annotation.*`. Jackson 2
  coexiste en el classpath solo porque springdoc 2.8.8 aún depende de él.
- **Slices de test de Spring Boot 4:** `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
  y `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`. Las rutas de Boot 3
  (`org.springframework.boot.test.autoconfigure.*`) ya no existen. `@MockitoBean`, no `@MockBean`.
- **JUnit 6.0.3** con anotaciones de JUnit 5: la doc de `/websites/junit_current` aplica.
- **Virtual Threads** ya activos por Boot: nunca declares un `TaskExecutor` propio.
- Snippets de Spring Boot 2.x/3.x o Keycloak viejo: incluye "Spring Boot 4" / "Keycloak 26" en la
  query y contrasta con el código real de `fichas`. Para Flyway 12, incluye "flyway-database-postgresql".
- Ya no hay `jjwt` ni `logstash-logback-encoder` en el proyecto (prod usa el `StructuredLogEncoder`
  nativo de Boot 4).

## Consultas por tipo de archivo

La query va en inglés y con los nombres de las APIs que buscas. Ejemplos que funcionan:

| Archivo | ID | Query |
|---|---|---|
| Interactor con `@Transactional` | `/websites/spring_io_spring-framework_reference_6_2` | `"Transactional transactionManager qualifier multiple datasources"` |
| `JpaEntity` (`@Table` nunca lleva schema) | `/spring-projects/spring-data-jpa` | `"Entity Table Column name mapping PostgreSQL"` |
| `JpaQueryEntity` | `/hibernate/hibernate-orm` | `"Subselect Immutable Synchronize read-only entity"` |
| Migración Flyway | `/flyway/flyway` | `"SQL migration versioned V naming convention"` |
| Controller | `/websites/spring_io_spring-framework_reference_6_2` | `"RestController PostMapping RequestBody ResponseEntity"` |
| Filtro HTTP | `/websites/spring_io_spring-framework_reference_6_2` | `"OncePerRequestFilter doFilterInternal HttpServletRequest"` |
| Resource Server / JWT | `/websites/spring_io_spring-security_reference_6_5` | `"OAuth2 resource server JWT decoder issuer-uri"` |
| `SecurityFilterChain` | `/websites/spring_io_spring-security_reference_6_5` | `"SecurityFilterChain requestMatchers hasAuthority"` |
| Rate limiting | `/bucket4j/bucket4j` | `"Bucket tryConsume refill greedy bandwidth filter"` |
| Consumer AMQP | `/websites/spring_io` | `"RabbitListener manual ack Channel basicAck basicNack"` |
| Colas / bindings | `/websites/spring_io` | `"TopicExchange Queue Binding Declarables"` |
| Outbox / externalización | `/spring-projects/spring-modulith` | `"event externalization AMQP event publication registry"` |
| Redis | `/spring-projects/spring-data-redis` | `"RedisTemplate opsForValue LettuceConnectionFactory"` |
| Correo | `/websites/spring_io_spring-boot` | `"JavaMailSender MimeMessage spring.mail properties"` |
| Configuración | `/websites/spring_io_spring-boot` | `"ConfigurationProperties Profile ConditionalOnProperty"` |
| Test unitario | `/mockito/mockito` | `"ExtendWith MockitoExtension ArgumentCaptor verify InOrder"` |
| Asserts | `/assertj/assertj` | `"assertThatThrownBy isInstanceOfSatisfying extracting"` |
| `@DataJpaTest` | `/websites/spring_io_spring-boot` | `"DataJpaTest TestEntityManager H2 slice test"` |
| `@WebMvcTest` con JWT | `/websites/spring_io_spring-security_reference_6_5` | `"MockMvc SecurityMockMvcRequestPostProcessors jwt authorities"` |
| `build.gradle` | `/websites/gradle_current_userguide` | `"multi-project dependencies JaCoCo coverage threshold"` |

Si `resolve-library-id` devuelve un ID equivocado, usa el de la tabla; si hay timeout, reintenta con
una query más corta.

> **Regla final:** Context7 da la API de la libreria, no la convención del proyecto. Ante cualquier
> choque entre un snippet y `arquisoft-arquitectura`/`arquisoft-estandares`, **gana la skill** — el
> snippet se adapta, no al revés.
