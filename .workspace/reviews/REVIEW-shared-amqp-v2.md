# Revisión Par — `shared:amqp` v2 (post-fixes)

**Módulos revisados:** `shared:amqp`, `shared:domain`, `seguridad:domain`, `fichas:infrastructure`
**Fecha:** 2026-05-14 — revisión tras aplicar AMQP-01..08

---

## Problemas encontrados

### 🔴 P1 — Crítico

- [x] **[AMQP2-01] `DomainEvent` llama a método abstracto en el constructor — antipatrón Java**
  - `validateTopic(this.getEventTopic())` en el constructor de `DomainEvent` invoca un método abstracto antes de que la subclase esté completamente inicializada (antipatrón `MC_OVERRIDABLE_METHOD_CALL_IN_CONSTRUCTOR`, detectado por SpotBugs/IntelliJ).
  - **Riesgo concreto:** si un evento futuro construye su topic usando un campo propio (`return this.contexto + ".entity.created"`), ese campo estará `null` durante la llamada al `super()` → NPE silencioso en construcción.
  - El fix AMQP-08 introdujo el problema al agregar la validación en el constructor en lugar de separar la responsabilidad.
  - **Fix:** Template Method — hacer `getEventTopic()` `final` en `DomainEvent` (preserva la API externa) y delegarla a un método abstracto protegido `defineEventTopic()` que implementan las subclases. La validación se mueve al interior de `getEventTopic()` (ya no está en el constructor).

---

### 🟠 P2 — Alto

- [x] **[AMQP2-02] `prefetch` hardcodeado en `RabbitListenerConfig` — mismo problema que AMQP-03, incompleto**
  - `factory.setPrefetchCount(1)` está fijo en código.
  - `application.yml` ya tiene `spring.rabbitmq.listener.simple.prefetch: 1` pero no se lee — igual que ocurría con `concurrency` antes del fix AMQP-03.
  - **Fix:** añadir `@Value("${spring.rabbitmq.listener.simple.prefetch:1}") private int prefetch;` e inyectarlo en `factory.setPrefetchCount(prefetch)`.

---

### 🟡 P3 — Medio

- [x] **[AMQP2-03] `AbstractEventConsumer.deserialize` envuelve `JacksonException` en `AmqpException` — acoplamiento semántico incorrecto**
  - Un fallo de deserialización JSON (`JacksonException`) no es un fallo de protocolo/broker AMQP (`AmqpException`). El wrapping mezcla dos preocupaciones distintas.
  - No tiene impacto funcional inmediato porque `withCorrelation` captura `Exception` genérico. Pero:
    1. El tipo `AmqpException` comunica falsamente "falló la infraestructura de mensajería" cuando el problema real es "el cuerpo del mensaje es inválido".
    2. Si en el futuro `RabbitMQEventPublisher` o cualquier componente reacciona específicamente a `AmqpException`, un mensaje JSON malformado podría activar lógica de reintento de conectividad erróneamente.
  - **Fix:** eliminar el `try/catch` en `deserialize` y dejar que `JacksonException` (que extiende `RuntimeException`) se propague directamente. `withCorrelation` la captura como `Exception` y hace NACK igualmente.

---

### 🔵 P4 — Bajo

- [x] **[AMQP2-04] `eventType` resuelto con `getClass().getSimpleName()` — frágil ante refactorizaciones**
  - `this.eventType = this.getClass().getSimpleName()` deriva el tipo del evento del nombre de la clase Java.
  - Si la clase se renombra en un refactor, el `eventType` en mensajes ya encolados (o en logs históricos de Grafana) cambia sin aviso, rompiendo potencialmente dashboards, alertas o consumers que lo usen para discriminar tipos.
  - Actualmente `eventType` solo se usa en logs — el impacto es bajo. Pero establece un mal precedente para cuando se use como discriminador de tipo en routing o deserialización polimórfica.
  - **Fix (opcional):** exponer una constante `EVENT_TYPE` en cada evento (similar a `EVENT_TOPIC`) y usarla en `getEventType()`, eliminando la dependencia del nombre de clase.

---

## Resumen

| ID | Severidad | Archivo afectado | Tipo de problema |
|----|-----------|-----------------|-----------------|
| AMQP2-01 | 🔴 Crítico | `DomainEvent.java` | Antipatrón constructor — introducido en AMQP-08 |
| AMQP2-02 | 🟠 Alto | `RabbitListenerConfig.java` | Config hardcodeada — fix AMQP-03 incompleto |
| AMQP2-03 | 🟡 Medio | `AbstractEventConsumer.java` | Acoplamiento semántico de excepciones |
| AMQP2-04 | 🔵 Bajo | `DomainEvent.java` | `eventType` frágil ante rename |
