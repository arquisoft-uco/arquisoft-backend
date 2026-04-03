---
name: context7-stack
description:
  IDs de librerías Context7 del stack Arquisoft (Spring Boot 3.2.4, Java 21, Gradle 8.6). Usar antes de generar cualquier archivo Java o de configuración para obtener documentación actualizada y específica por versión de cada dependencia del proyecto. Incluye tabla de IDs directos, ejemplos de consulta por tipo de archivo y solución de problemas del MCP.
license: MIT
compatibility: opencode
metadata:
  stack: java21-spring-boot-3.2.4
  build: gradle-8.6
---

# Skill: context7-stack

IDs de librerías Context7 del stack Arquisoft. Usa estos IDs directamente con
`query-docs` para saltarte el paso `resolve-library-id` y obtener documentación
más rápido.

---

## IDs de Librerías del Stack Arquisoft

| Librería | Context7 ID | Versión en proyecto |
|----------|-------------|---------------------|
| Spring Boot | `/spring-projects/spring-boot` | 3.2.4 |
| Spring AMQP (RabbitMQ) | `/spring-projects/spring-amqp` | 3.1.x |
| Spring Data JPA | `/spring-projects/spring-data-jpa` | 3.2.x |
| Spring Security | `/spring-projects/spring-security` | 6.2.x |
| Spring Web / REST | `/spring-projects/spring-framework` | 6.1.x |
| Flyway | `/flyway/flyway` | 9.x |
| JUnit 5 | `/junit-team/junit5` | 5.10.x |
| Mockito | `/mockito/mockito` | 5.x |
| AssertJ | `/assertj/assertj-core` | 3.x |
| Lombok | `/projectlombok/lombok` | 1.18.x |
| Gradle | `/gradle/gradle` | 8.6 |

---

## Consultas por Tipo de Archivo

Usa estas consultas directas según el archivo que estés generando:

### Capa domain

```
# Entidad de dominio Java 21 (records, immutability)
query-docs /spring-projects/spring-framework "domain entity immutable Java 21"

# Excepción de dominio
query-docs /spring-projects/spring-framework "RuntimeException custom domain exception"
```

### Capa application

```
# DTO con Lombok y validación Jakarta
query-docs /projectlombok/lombok "Builder Data NoArgsConstructor"

# Caso de uso con transacción
query-docs /spring-projects/spring-framework "Transactional service component"

# Validación Jakarta en DTO
query-docs /spring-projects/spring-framework "Valid NotBlank Email Size validation"
```

### Capa infrastructure — Persistencia

```
# Entidad JPA con anotaciones
query-docs /spring-projects/spring-data-jpa "Entity Table Column mapping Java 21"

# Repositorio JPA con queries
query-docs /spring-projects/spring-data-jpa "JpaRepository findBy custom query"

# Adaptador repositorio hexagonal
query-docs /spring-projects/spring-data-jpa "repository adapter port implementation"
```

### Capa infrastructure — Web

```
# Controller REST con validación y roles
query-docs /spring-projects/spring-framework "RestController RequestMapping Valid RequestBody"

# Seguridad con Keycloak OAuth2
query-docs /spring-projects/spring-security "OAuth2 resource server JWT bearer token"

# Manejo de errores centralizado
query-docs /spring-projects/spring-framework "RestControllerAdvice ExceptionHandler"
```

### Capa infrastructure — Mensajería RabbitMQ

```
# Publicar evento
query-docs /spring-projects/spring-amqp "RabbitTemplate convertAndSend exchange routing key"

# Consumir evento con acknowledge manual
query-docs /spring-projects/spring-amqp "RabbitListener acknowledgment manual ack"

# Configurar exchange y cola
query-docs /spring-projects/spring-amqp "TopicExchange Queue Binding declarables"
```

### Capa infrastructure — Migraciones

```
# Convención de nombres y scripts SQL
query-docs /flyway/flyway "SQL migration naming convention versioned"

# Migración condicional
query-docs /flyway/flyway "conditional migration script"
```

### Tests

```
# Test unitario con Mockito
query-docs /mockito/mockito "Mock InjectMocks verify when thenReturn"

# Test con AssertJ
query-docs /assertj/assertj-core "assertThat isEqualTo isNotNull throwsException"

# Test de integración Spring
query-docs /junit-team/junit5 "SpringBootTest DynamicPropertySource H2"

# Test de controller con Spring Security
query-docs /spring-projects/spring-security "MockMvc WithMockUser SecurityMockMvcRequestPostProcessors"
```

---

## Ejemplos de Uso con ID Directo en Prompts

Si ya sabes qué librería necesitas, pasa el ID directamente:

```
¿Cómo configuro @RabbitListener con acknowledge manual?
use library /spring-projects/spring-amqp
```

```
¿Cómo creo una migración condicional en Flyway?
use library /flyway/flyway
```

```
¿Cómo uso @DynamicPropertySource en tests de integración?
use library /spring-projects/spring-framework
```

```
¿Cómo mapeo una entidad JPA con esquema específico en PostgreSQL?
use library /spring-projects/spring-data-jpa
```

```
¿Cómo configuro un JWT decoder para Keycloak en Spring Security 6?
use library /spring-projects/spring-security
```

---

## Solución de Problemas de Context7 MCP

| Problema | Causa | Solución |
|----------|-------|----------|
| Context7 no aparece en `/mcp` | `opencode.json` mal formado | Verificar JSON con `cat opencode.json \| jq .` |
| `HTTP 429 Too Many Requests` | Rate limit sin API key | Obtener API key gratis en [context7.com/dashboard](https://context7.com/dashboard) |
| `npx: command not found` | Node.js no instalado | Instalar Node.js 18+ y reiniciar terminal |
| Documentación desactualizada | ID de librería incorrecto | Usar `resolve-library-id` primero para encontrar el ID correcto |
| Timeout en modo local | Descarga lenta de npx | Cambiar a opción remota en `opencode.json` (ver `.opencode/docs/mcp-context7-setup.md`) |
| Resultados irrelevantes | Query demasiado genérica | Ser más específico, incluir versión o anotación concreta en la query |