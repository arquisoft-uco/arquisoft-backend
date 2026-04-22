---
name: context7-stack
description:
  IDs de librerias Context7 del stack Arquisoft (Spring Boot 4.0.5, Java 21, Gradle 9.0.0). Usar antes de generar cualquier archivo Java o de configuracion para obtener documentacion actualizada y especifica por version de cada dependencia del proyecto. Incluye tabla de IDs directos validados, IDs alternativos con mas snippets, y ejemplos de consulta por tipo de archivo.
license: MIT
compatibility: opencode
metadata:
  stack: java21-spring-boot-4.0.5
  build: gradle-9.0.0
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

| Libreria | ID Recomendado ★ | Snippets | Version en proyecto |
|----------|-----------------|----------|---------------------|
| Spring Boot | `/websites/spring_io_spring-boot` | 295 000+ | **4.0.5** |
| Spring Framework (MVC/Web/Tx) | `/websites/spring_io_spring-framework_reference_6_2` | 6 761 | 7.x (via Boot 4.0) |
| Spring Security + OAuth2 | `/websites/spring_io_spring-security_reference_6_5` | 11 697 | 6.5.x (via Boot 4.0) |
| Spring AMQP / RabbitMQ | `/websites/spring_io` | 50 638 | 3.2.x (via Boot 4.0) |
| Spring Data JPA | `/spring-projects/spring-data-jpa` | 315 | 3.4.x (via Boot 4.0) |
| Spring Data Redis | `/spring-projects/spring-data-redis` | 357 | 3.4.x (via Boot 4.0) |
| Flyway | `/flyway/flyway` | 2 434 | **10.10.0** |
| JUnit 5 | `/websites/junit_current` | 5 740 | **6.0.3** (compatible con anotaciones JUnit 5) |
| Mockito | `/mockito/mockito` | 120 | 5.x (via Boot 4.0) |
| AssertJ | `/assertj/assertj` | 81 | 3.x (via Boot 4.0) |
| Lombok | `/projectlombok/lombok` | 638 | 1.18.x |
| Gradle | `/websites/gradle_current_userguide` | 4 607 | **9.0.0** |
| JJWT | `/jwtk/jjwt` | 166 | 0.12.3 |
| Keycloak | `/keycloak/keycloak` | 2 453 | **26.6** |
| Bucket4j | `/bucket4j/bucket4j` | 301 | 7.6.0 |
| Jackson | `/fasterxml/jackson-databind` | 47 | 2.15.2 |
| Hibernate ORM | `/hibernate/hibernate-orm` | 4 278 | 6.x (via JPA Boot 3.2) |

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

# Excepcion de dominio personalizada
query-docs /websites/spring_io_spring-framework_reference_6_2 "custom RuntimeException domain exception errorCode"

# Value Object Java 21
query-docs /websites/spring_io_spring-framework_reference_6_2 "value object immutable record Java 21"
```

### Capa application — DTOs y Casos de Uso

```
# DTO con Lombok (Builder, Data, NoArgsConstructor, AllArgsConstructor)
query-docs /projectlombok/lombok "Builder Data NoArgsConstructor AllArgsConstructor toDomain fromDomain"

# DTO con validaciones Jakarta Bean Validation
query-docs /websites/spring_io_spring-boot "Valid NotBlank Email Size validation DTO"

# Implementacion de caso de uso con @Component y @RequiredArgsConstructor
query-docs /projectlombok/lombok "RequiredArgsConstructor Component service constructor injection"

# Caso de uso transaccional
query-docs /websites/spring_io_spring-framework_reference_6_2 "Transactional service component use case"

# @Builder.Default para valores por defecto en DTO
query-docs /projectlombok/lombok "Builder Default field value initialization"
```

### Capa infrastructure — Persistencia JPA + Flyway

```
# Entidad JPA con esquema especifico de PostgreSQL
query-docs /spring-projects/spring-data-jpa "Entity Table schema Column mapping PostgreSQL"

# Adaptador de repositorio (implementacion del puerto de salida)
query-docs /spring-projects/spring-data-jpa "JpaRepository save findById custom query adapter"

# Migracion Flyway con esquema propio por contexto
query-docs /flyway/flyway "SQL migration versioned V naming convention schema"

# Flyway con modulo postgresql (flyway-database-postgresql)
query-docs /flyway/flyway "PostgreSQL specific migration flyway-database-postgresql"

# Mapeo ORM con Hibernate: columnas, relaciones, tipos
query-docs /hibernate/hibernate-orm "Column ManyToOne OneToMany fetch type lazy eager"

# HikariCP pool de conexiones
query-docs /websites/spring_io_spring-boot "HikariCP connection pool maximum-pool-size datasource"
```

### Capa infrastructure — Controllers REST

```
# Controller REST con validacion y manejo de respuestas
query-docs /websites/spring_io_spring-framework_reference_6_2 "RestController RequestMapping PostMapping Valid RequestBody ResponseEntity"

# Manejo de errores centralizado con GlobalExceptionHandler
query-docs /websites/spring_io_spring-framework_reference_6_2 "RestControllerAdvice ExceptionHandler MethodArgumentNotValidException"

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

# JJWT: crear y validar tokens JWT manualmente
query-docs /jwtk/jjwt "Jwts builder signWith parseSignedClaims SecretKey 0.12"

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

# Configurar Jedis como cliente Redis
query-docs /spring-projects/spring-data-redis "JedisConnectionFactory JedisPoolConfig Spring Boot"

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

# Test de integracion con Spring Boot y H2
query-docs /websites/junit_current "SpringBootTest DataJpaTest TestPropertySource H2"

# Test de controller con Spring Security Mock
query-docs /websites/spring_io_spring-security_reference_6_5 "MockMvc WithMockUser SecurityMockMvcRequestPostProcessors"

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
# Crear migracion Flyway para esquema fichas
use library /flyway/flyway
consulta: "versioned migration V naming convention schema create table"
```

```
# Configurar JWT decoder para Keycloak en Spring Security 6
use library /websites/spring_io_spring-security_reference_6_5
consulta: "JWT decoder Keycloak issuer-uri public key NimbusJwtDecoder"
```

```
# Mapear entidad JPA con esquema especifico
use library /spring-projects/spring-data-jpa
consulta: "Entity Table schema name Column insertable updatable"
```

```
# Construir y parsear JWT con JJWT 0.12
use library /jwtk/jjwt
consulta: "Jwts builder signWith HS256 parseSignedClaims getPayload 0.12"
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
| Resultados sobre Spring Boot 2.x | ID no especifica version | Usar `/websites/spring_io_spring-boot` que cubre 3.x |
| No encuentra configuracion de Flyway 10 | Query demasiado generica | Incluir "flyway-database-postgresql" o "flyway 10" en la query |
| Timeout o sin respuesta | Rate limit o red | Reintentar con query mas corta; agregar API key en opencode.json |
| Snippets de Keycloak muy viejos | Version antigua | Indicar "Keycloak 22 23" explicitamente en la query |
| JJWT muestra API antigua (0.9.x) | Cambio de API en 0.12 | Incluir "0.12 parseSignedClaims Jwts parser" en la query |
