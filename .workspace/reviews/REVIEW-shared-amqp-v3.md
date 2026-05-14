# Revisión Par — `shared:amqp` v3

**Módulos revisados:** `shared:amqp`, `shared:domain` (events), `seguridad:domain/application`, `fichas:infrastructure`
**Fecha:** 2026-05-14 — revisión post-fixes v1+v2

---

## Problemas encontrados

### 🔴 P1 — Crítico

- [x] **[AMQP3-01] `withCorrelation` — catch scope incorrecto: engloba `channel.basicAck()` junto al handler**

  `channel.basicAck(deliveryTag, false)` está **dentro** del `try` cuyo `catch (Exception ex)` dispara `basicNack`. Si el handler de negocio termina con éxito pero el ACK falla por canal roto (`IOException`), el catch invoca `basicNack(requeue=false)` → el mensaje va al DLQ **aunque ya fue procesado correctamente**. Con `requeue=false` el mensaje no se reencola, pero causa doble escritura/procesamiento en contextos con side-effects (e.g., fichas registraría el usuario y luego el evento aparecería en DLQ como si hubiera fallado).

  El `catch` debe cubrir solo `handler.handle()`, no el ACK.

  ```java
  // Fix: separar scopes
  try {
      handler.handle();
  } catch (Exception ex) {
      log.error(...);
      channel.basicNack(deliveryTag, false, false);
      return;
  }
  channel.basicAck(deliveryTag, false); // solo llega aquí si el handler no lanzó
  ```

  **Archivo:** `AbstractEventConsumer.java`

---

### 🟠 P2 — Alto

- [x] **[AMQP3-02] `EventEmittingEntity` — nombre no cumple la convención del proyecto**

  AGENTS.md define explícitamente: *"Aggregate Root: entidades raíz extienden `AggregateRoot` de `shared:domain`"*. La clase real se llama `EventEmittingEntity` — nombre técnico que describe implementación, no rol de dominio. Rompe el onboarding y la búsqueda por convención. Cualquier desarrollador que siga el doc busca `AggregateRoot` y no la encuentra.

  **Fix:** renombrar `EventEmittingEntity` → `AggregateRoot`. `Usuario` extiende la clase → actualizar allí también.

  **Archivo:** `EventEmittingEntity.java`, `Usuario.java`

- [x] **[AMQP3-03] Drain pattern no-atómico — `clearUnPublishedEvents()` es `public`**

  En `CrearUsuarioUseCaseImpl`:
  ```java
  usuario.getUnPublishedEvents().forEach(eventPublisher::publish);
  usuario.clearUnPublishedEvents(); // dos pasos separados
  ```
  Problemas:
  1. `clearUnPublishedEvents()` es `public` → cualquier código puede limpiar eventos sin publicarlos: pérdida silenciosa de eventos de negocio.
  2. El patrón de dos pasos requiere que cada use case recuerde llamar `clear` después de `forEach`. Si se omite, los eventos se re-publican en la próxima llamada.

  **Fix:** reemplazar los dos métodos con uno atómico:
  ```java
  // en EventEmittingEntity / AggregateRoot
  public List<DomainEvent> drainUnPublishedEvents() {
      List<DomainEvent> drained = new ArrayList<>(unPublishedEvents);
      unPublishedEvents.clear();
      return drained;
  }
  // getUnPublishedEvents() → protected (solo tests de dominio)
  // clearUnPublishedEvents() → eliminado o protected
  ```
  Use case: `usuario.drainUnPublishedEvents().forEach(eventPublisher::publish);`

  **Archivos:** `EventEmittingEntity.java`, `CrearUsuarioUseCaseImpl.java`

---

### 🟡 P3 — Medio

- [x] **[AMQP3-04] `fichasUsuarioCreadoBinding()` inyecta `TopicExchange` por tipo sin `@Qualifier`**

  ```java
  public Binding fichasUsuarioCreadoBinding(
          Queue fichasUsuarioCreadoQueue,
          TopicExchange arquisoftEventsExchange)  // ← por tipo, no por nombre
  ```
  Si cualquier otro `@Configuration` declara un segundo `TopicExchange` bean en el mismo contexto Spring, la inyección falla con `NoUniqueBeanDefinitionException` al arrancar. El bean correcto se llama `arquisoftEventsExchange`.

  **Fix:** `@Qualifier("arquisoftEventsExchange")` en el parámetro.

  **Archivo:** `FichasUsuariosQueueConfig.java`

---

### 🔵 P4 — Bajo

- [x] **[AMQP3-05] `@Qualifier` en `RabbitMQConfig.jsonMessageConverter()` escrito como FQCN**

  ```java
  @Bean
  public JacksonJsonMessageConverter jsonMessageConverter(
          @org.springframework.beans.factory.annotation.Qualifier("rabbitObjectMapper")
          JsonMapper rabbitObjectMapper)
  ```
  El FQCN en lugar de un `import` es inconsistente con el resto del código y reduce la legibilidad. No hay motivo técnico para evitar el import.

  **Fix:** añadir `import org.springframework.beans.factory.annotation.Qualifier;` y simplificar la anotación.

  **Archivo:** `RabbitMQConfig.java`

---

## Resumen

| ID | Severidad | Archivo afectado | Tipo |
|----|-----------|-----------------|------|
| AMQP3-01 | 🔴 Crítico | `AbstractEventConsumer.java` | Bug: catch scope incorrecto → ACK/NACK race |
| AMQP3-02 | 🟠 Alto | `EventEmittingEntity.java`, `Usuario.java` | Naming: incumple convención AGENTS.md |
| AMQP3-03 | 🟠 Alto | `EventEmittingEntity.java`, `CrearUsuarioUseCaseImpl.java` | Diseño: drain no-atómico, `clear` pública |
| AMQP3-04 | 🟡 Medio | `FichasUsuariosQueueConfig.java` | Fragility: inyección por tipo sin qualifier |
| AMQP3-05 | 🔵 Bajo | `RabbitMQConfig.java` | Style: FQCN en lugar de import |
