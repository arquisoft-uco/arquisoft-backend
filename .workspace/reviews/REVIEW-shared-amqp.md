# Revisión Par — `shared:amqp` y contextos consumidores

**Módulos revisados:** `shared:amqp`, `seguridad:infrastructure`, `fichas:infrastructure`
**Fecha:** 2026-05-14

---

## Problemas encontrados

### 🔴 P1 — Crítico (rompe en producción)

- [x] **[AMQP-01] Anotación Jackson 2.x en contexto Jackson 3.x — deserialization silenciosamente rota**
  - `UsuarioCreadoPayload` usa `@JsonIgnoreProperties(ignoreUnknown = true)` de `com.fasterxml.jackson` (Jackson 2.x).
  - `AbstractEventConsumer.deserialize` usa `tools.jackson.databind.ObjectMapper` (Jackson 3.x), que **no reconoce** anotaciones del paquete `com.fasterxml.jackson`.
  - El evento publicado incluye campos no mapeados en el payload: `occurredAt`, `eventType`, `eventTopic`. Sin el ignore funcionando, Jackson 3.x lanza `UnrecognizedPropertyException` al recibir cualquier evento.
  - **Fix:** cambiar a `@tools.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)` (Jackson 3.x), o deshabilitar `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES` en el `rabbitObjectMapper` bean.

---

### 🟠 P2 — Alto (acoplamiento innecesario / violación de principios)

- [x] **[AMQP-02] Dependencia muerta en `seguridad:infrastructure`**
  - `seguridad/infrastructure/build.gradle` declara `implementation project(':shared:amqp')`.
  - No existe ninguna clase con uso real de AMQP en `seguridad/infrastructure/src` (la única mención es un comentario en `InMemoryUsuarioRepository.java`).
  - La publicación del evento `UsuarioCreadoEvent` ocurre en `seguridad:application` via la interfaz `EventPublisher` de `shared:domain` — no requiere dependencia directa a `shared:amqp`.
  - **Fix:** eliminar `implementation project(':shared:amqp')` de `seguridad/infrastructure/build.gradle`.

- [x] **[AMQP-03] `RabbitListenerConfig` con valores de concurrencia hardcodeados**
  - `concurrentConsumers = 5` y `maxConcurrentConsumers = 10` están fijos en código.
  - El proyecto tiene Virtual Threads habilitado (`spring.threads.virtual.enabled=true`). Con VT, el modelo de threading es diferente — estos valores controlan cuántos `SimpleMessageListenerContainer` se crean, no OS threads, pero siguen siendo valores que deben variar por ambiente (local vs prod).
  - **Fix:** mover a `application.yml` como `spring.rabbitmq.listener.simple.concurrency` y `max-concurrency`.

- [x] **[AMQP-04] `DomainEvent.occurredAt` usa `LocalDateTime` sin zona horaria**
  - `LocalDateTime.now()` es relativo a la JVM local, sin información de zona horaria.
  - En un sistema distribuido con consumers corriendo en servidores potencialmente en zonas distintas, los timestamps de eventos son inconsistentes.
  - **Fix:** cambiar `LocalDateTime` a `Instant` y asignar con `Instant.now()`.

---

### 🟡 P3 — Medio (fragilidad / deuda de diseño)

- [x] **[AMQP-05] Routing key duplicada y desincronizable**
  - `FichasUsuariosQueueConfig.USUARIO_CREADO_ROUTING_KEY = "seguridad.usuario.creado"` es una copia literal del valor que retorna `UsuarioCreadoEvent.getEventTopic()`.
  - Si el topic del evento cambia, hay que recordar actualizar ambos. No hay ningún mecanismo que lo enforce en compilación.
  - La constante es package-private sin razón — si más de una clase en el config package la necesita, está bien, pero si no, es sólo confusión.
  - **Fix:** exponer una constante pública en `UsuarioCreadoEvent` (o en una clase de constantes del contexto `seguridad:domain`) y referenciarla desde `FichasUsuariosQueueConfig`. _Nota: esto requiere que `fichas:infrastructure` dependa de `seguridad:domain`, lo cual viola el aislamiento entre bounded contexts. La alternativa correcta es que cada contexto mantenga su propia copia y se acepta el riesgo, pero debe documentarse explícitamente como decisión de diseño en lugar de dejarlo implícito._

- [x] **[AMQP-06] Referencia a infraestructura en Javadoc de dominio**
  - `EventEmittingEntity` (en `shared:domain`, Java puro) menciona en su Javadoc: `"EventPublisher (shared:amqp)"`.
  - `shared:domain` no puede conocer `shared:amqp` — la capa de dominio es agnóstica de la tecnología de mensajería.
  - **Fix:** eliminar la referencia a `shared:amqp` del Javadoc. La documentación del dominio solo debe referenciar abstracciones del mismo dominio (`EventPublisher`).

- [x] **[AMQP-07] Retry manual en `RabbitMQEventPublisher` solo captura `AmqpException`**
  - El bucle de reintentos en `RabbitMQEventPublisher.publish` solo hace retry en `catch (AmqpException)`.
  - Si `RabbitTemplate.convertAndSend` lanza una excepción de otro tipo (ej. serialización fallida con `RuntimeException`), se propaga inmediatamente sin reintentos y sin logging de error explícito.
  - Este comportamiento diferencial no está documentado en el código ni en los Javadocs del método.
  - **Fix:** documentar explícitamente que solo se reintenta `AmqpException` (errores de conectividad), o usar `RetryTemplate` de `spring-retry` (ya disponible transitivamente según el comentario en `build.gradle`) para un contrato más claro.

---

### 🔵 P4 — Bajo / Informativo

- [x] **[AMQP-08] `getEventTopic()` sin validación de formato**
  - El contrato `{contexto}.{entidad}.{accion}` existe solo en un comentario en `DomainEvent`.
  - No hay ningún mecanismo que falle en tiempo de compilación o arranque si un evento viola el formato.
  - **Fix (opcional):** agregar un método `default` o validación en la clase base `DomainEvent` que verifique el patrón al construirse, o agregar un test de contrato.

---

## Resumen

| ID | Severidad | Archivo afectado | Tipo de problema |
|----|-----------|-----------------|-----------------|
| AMQP-01 | 🔴 Crítico | `UsuarioCreadoPayload.java` | Bug — Jackson 2.x vs 3.x |
| AMQP-02 | 🟠 Alto | `seguridad/infrastructure/build.gradle` | Dependencia muerta |
| AMQP-03 | 🟠 Alto | `RabbitListenerConfig.java` | Config hardcodeada |
| AMQP-04 | 🟠 Alto | `DomainEvent.java` | `LocalDateTime` sin TZ |
| AMQP-05 | 🟡 Medio | `FichasUsuariosQueueConfig.java` | Routing key duplicada |
| AMQP-06 | 🟡 Medio | `EventEmittingEntity.java` | Infra en Javadoc de dominio |
| AMQP-07 | 🟡 Medio | `RabbitMQEventPublisher.java` | Retry parcial no documentado |
| AMQP-08 | 🔵 Bajo | `DomainEvent.java` | Contrato sin enforcement |
