# Diseño — Módulo de Eventos AMQP (`shared/amqp`)

**Fecha:** 2026-05-13  
**Estado:** Borrador — pendiente aprobación  
**Scope:** Ampliación de `shared/amqp` para publicación y consumo fiable de eventos con propagación de traza  

---

## 1. Análisis del broker: RabbitMQ vs Apache Kafka

### Decisión: **RabbitMQ** ✅

| Criterio | RabbitMQ | Apache Kafka |
|---|---|---|
| Ya en el stack | ✅ `rabbitmq:4.2.5` en docker-compose y Spring AMQP configurado | ❌ requiere infraestructura nueva |
| Patrón de uso | Colas de trabajo, fan-out, topic routing entre 7 bounded contexts | Streaming de alto volumen y replay histórico |
| Durabilidad si el broker cae | Colas durable + Publisher Confirms + retry automático | Offset commit en Zookeeper/KRaft |
| Durabilidad si el consumer cae | Manual ACK — mensajes se reencolan mientras el consumer esté caído | Consumer group offset — mensajes disponibles al reiniciar |
| Complejidad operacional | Baja — ya gestionado en docker-compose e infra/coolify | Alta — ZooKeeper/KRaft, replicación, particiones |
| Sobreingeniería | Ninguna — el volumen de eventos entre 7 contextos académicos no justifica Kafka | Sí — Kafka está optimizado para millones de mensajes/s |

**Conclusión:** RabbitMQ cubre todos los requisitos de fiabilidad con la configuración correcta
(Publisher Confirms + colas durable + Manual ACK + Dead Letter Exchange). Kafka resolvería el
mismo problema con 10× más complejidad operacional y sin beneficio tangible en este dominio.

---

## 2. Diagnóstico del estado actual de `shared/amqp`

El módulo existe y tiene la estructura base. Sin embargo tiene cuatro **brechas críticas**:

| # | Problema | Consecuencia |
|---|---|---|
| 1 | `acknowledge-mode: auto` en `application.yml` | Si el consumer procesa el mensaje y falla antes de terminar, el mensaje se pierde (ya fue ACK-eado automáticamente al entregarse) |
| 2 | Sin Publisher Confirms | Si RabbitMQ rechaza o pierde el mensaje en tránsito, el publicador no se entera |
| 3 | Sin headers de correlación (`X-Trace-Id`, `X-User-Id`) | Logs del consumer no tienen traza del request HTTP original — rompe observabilidad |
| 4 | Sin Dead Letter Exchange (DLX) | Mensajes que fallan repetidamente se pierden o bloquean la cola indefinidamente |
| 5 | Sin abstracción para consumers | Cada contexto implementaría MDC propagation por su cuenta — duplicación y errores |
| 6 | Solo el exchange está declarado — sin colas ni bindings | Los contextos que quieran consumir no tienen dónde conectarse |

---

## 3. Diseño de la solución

### 3.1 Estrategia: ampliar `shared/amqp`, no crear módulo nuevo

Crear un submódulo nuevo (`shared/events`, etc.) sería sobreingeniería dado que:
- `shared/amqp` ya tiene la dependencia correcta a `shared/domain`
- Ya tiene `RabbitTemplate`, `JacksonJsonMessageConverter` y el exchange configurado  
- La extensión es cohesiva — todo pertenece a la misma preocupación (mensajería)

**Sí se reorganiza internamente** con subpaquetes para mantener claridad:

```
shared/amqp/src/main/java/com/arquisoft/shared/amqp/
├── EventPublisher.java                   (existente — sin cambios: es el puerto de salida)
├── RabbitMQConfig.java                   (MODIFICAR: Publisher Confirms, DLX, propiedades)
├── RabbitMQEventPublisher.java           (MODIFICAR: añadir headers X-Trace-Id / X-User-Id)
└── consumer/
    └── AbstractEventConsumer.java        (NUEVO: base class con MDC propagation + manual ACK)
```

### 3.2 Contrato del publicador (sin cambio de interfaz)

`EventPublisher` permanece igual — es el **puerto de salida** que los use cases conocen:

```java
// shared/amqp — ya existente, no se modifica
public interface EventPublisher {
    void publish(DomainEvent event);
}
```

La implementación `RabbitMQEventPublisher` se enriquece internamente para:
1. Añadir headers `X-Trace-Id` y `X-User-Id` tomados del MDC
2. Generar un `X-Trace-Id` aleatorio si el MDC está vacío (eventos disparados fuera de un request HTTP)

### 3.3 Fiabilidad: Publisher Confirms + colas durable + Manual ACK + DLX

#### 3.3.1 Publisher Confirms

```yaml
# application.yml (adición)
spring:
  rabbitmq:
    publisher-confirm-type: CORRELATED   # ACK/NACK del broker al publicador
    publisher-returns: true              # Si el broker no puede enrutar el mensaje, lo devuelve
```

Con esto `RabbitTemplate` lanza un callback cuando el broker confirma o rechaza. En caso de
NACK o return, `RabbitMQEventPublisher` registra un `log.error` con el `eventId` y relanza la
excepción para que el use case pueda decidir (reintento, compensación, etc.).

#### 3.3.2 Acknowledge Mode: MANUAL

```yaml
# application.yml (modificación)
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: manual         # era: auto
        prefetch: 1                      # un mensaje a la vez por consumer (backpressure)
```

Con Manual ACK:
- El mensaje permanece en `unacknowledged` hasta que el consumer llama `channel.basicAck()`
- Si el consumer falla o el proceso muere, RabbitMQ re-entrega el mensaje al próximo consumer disponible
- `prefetch: 1` evita que un consumer acumule mensajes sin procesar cuando hay varios consumers

#### 3.3.3 Dead Letter Exchange (DLX)

Se declara un exchange de tipo `direct` llamado `arquisoft.dlx`. Cada cola de negocio se
configura con argumentos `x-dead-letter-exchange` y `x-message-ttl` opcionales.

Política:
- Tras `x-delivery-limit: 3` reintentos fallidos (NACK sin requeue), el mensaje va a la DLQ correspondiente
- DLQ naming: `{contexto}.{routing-key}.dlq` (ej: `fichas.fichas.ficha.creada.dlq`)
- La DLQ es durable — los mensajes muertos quedan disponibles para inspección manual o reprocesamiento

```
Flujo de un mensaje fallido:
  [Producer] → arquisoft.events (TopicExchange)
               ↓ routing key: fichas.ficha.creada
  [Queue: fichas.ficha.creada] → Consumer falla 3 veces
               ↓ basicNack(requeue=false)
  [arquisoft.dlx (DirectExchange)]
               ↓ routing key: fichas.ficha.creada.dead
  [DLQ: fichas.ficha.creada.dlq] → operador inspecciona / reprocesa manualmente
```

### 3.4 Propagación de traceId (resuelve `pendiente-amqp-traceid.md`)

#### En el publicador (`RabbitMQEventPublisher`)

```java
rabbitTemplate.convertAndSend(exchange, routingKey, event, msg -> {
    String traceId = MDC.get(MdcKeys.TRACE_ID);
    String userId  = MDC.get(MdcKeys.USER_ID);
    msg.getMessageProperties()
       .setHeader("X-Trace-Id", traceId != null ? traceId 
                                : UUID.randomUUID().toString().replace("-", ""));
    msg.getMessageProperties()
       .setHeader("X-User-Id",  userId != null ? userId : "SYSTEM");
    return msg;
});
```

#### En el consumidor (`AbstractEventConsumer`)

Clase base que cada bounded context extiende:

```java
public abstract class AbstractEventConsumer {

    /**
     * Ejecuta el procesamiento del mensaje garantizando:
     * 1. MDC poblado con traceId y userId del mensaje original
     * 2. ACK/NACK manual del canal
     * 3. Limpieza de MDC en finally (compatible con Virtual Threads)
     */
    protected void withCorrelation(Message message, Channel channel,
                                   long deliveryTag, EventHandler handler) {
        String traceId = header(message, "X-Trace-Id");
        String userId  = header(message, "X-User-Id");

        Map<String, String> prevMdc = MDC.getCopyOfContextMap();
        MDC.put(MdcKeys.TRACE_ID, traceId != null ? traceId 
                                 : UUID.randomUUID().toString().replace("-", ""));
        MDC.put(MdcKeys.USER_ID,  userId  != null ? userId : "EVENT");
        try {
            handler.handle();
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            log.error("Error procesando evento — requeue=false para DLQ: {}", ex.getMessage(), ex);
            channel.basicNack(deliveryTag, false, false); // → DLX/DLQ
        } finally {
            if (prevMdc != null) MDC.setContextMap(prevMdc);
            else                 MDC.clear();
        }
    }

    @FunctionalInterface
    public interface EventHandler {
        void handle() throws Exception;
    }
}
```

#### Resultado en Grafana (trazabilidad completa)

```
{traceId="abc123"} → muestra la traza completa del flujo:
  AuditFilter       AUDIT POST /api/fichas — 202        traceId=abc123  userId=user-1
  FichaUseCase      Ficha creada, publicando evento      traceId=abc123  userId=user-1
  FichaConsumer     Procesando FichaCreada               traceId=abc123  userId=user-1
  ProyectoUseCase   Proyecto vinculado a ficha           traceId=abc123  userId=user-1
```

### 3.5 Compatibilidad con Virtual Threads

Spring Boot 4.x con `spring.threads.virtual.enabled: true` ya ejecuta `@RabbitListener`
en Virtual Threads vía `VirtualThreadTaskExecutor`. El diseño es compatible porque:

| Aspecto | Por qué no hay problema |
|---|---|
| MDC (`ThreadLocal`) | Cada Virtual Thread tiene su propio stack — `ThreadLocal` es aislado entre VTs, el patrón try/finally funciona igual que con OS threads |
| `ConcurrentHashMap` en `RabbitMQConfig` | No hay `synchronized` — RabbitMQ beans son thread-safe por diseño de Spring |
| `channel.basicAck/Nack` | Operación de I/O no-bloqueante para VTs — más eficiente que con OS threads |
| Publisher Confirms callbacks | Se ejecutan en el thread del confirmador — MDC de ese thread es independiente del publicador |

**Precaución:** los Publisher Confirm callbacks corren en el hilo del `ConfirmListener` de la
conexión. Si se necesita acceder al MDC del request original en el callback, se debe capturar
el contexto MDC **antes** de llamar `convertAndSend` y pasarlo al lambda del confirm.

### 3.6 Cómo cada bounded context declara colas y consumers

El diseño sigue el principio de que **`shared/amqp` provee las abstracciones, cada contexto
declara sus propias colas** en su capa de infraestructura. Esto evita que `shared/amqp` conozca
los contextos de negocio (inversión de dependencias correcta).

#### Declaración de cola y binding en `{contexto}/infrastructure/config/`

```java
// fichas/infrastructure/config/FichasQueueConfig.java
@Configuration
public class FichasQueueConfig {

    public static final String FICHA_CREADA_QUEUE = "fichas.ficha.creada";

    @Bean
    public Queue fichaCreadaQueue() {
        return QueueBuilder
            .durable(FICHA_CREADA_QUEUE)
            .withArgument("x-dead-letter-exchange", RabbitMQConfig.DLX_NAME)
            .withArgument("x-dead-letter-routing-key", FICHA_CREADA_QUEUE + ".dead")
            .build();
    }

    @Bean
    public Binding fichaCreadaBinding(Queue fichaCreadaQueue, TopicExchange arquisoftEventsExchange) {
        return BindingBuilder
            .bind(fichaCreadaQueue)
            .to(arquisoftEventsExchange)
            .with(FICHA_CREADA_QUEUE);   // routing key = topic del evento
    }
}
```

#### Consumer en `{contexto}/infrastructure/amqp/`

```java
// proyectos/infrastructure/amqp/FichaCreadaConsumer.java
@Slf4j
@Component
@RequiredArgsConstructor
public class FichaCreadaConsumer extends AbstractEventConsumer {

    private final VincularFichaAProyectoUseCase useCase;

    @RabbitListener(queues = FichasQueueConfig.FICHA_CREADA_QUEUE)
    public void onFichaCreada(Message message, Channel channel) throws IOException {
        withCorrelation(message, channel, message.getMessageProperties().getDeliveryTag(), () -> {
            FichaCreadaEvent event = deserializar(message, FichaCreadaEvent.class);
            useCase.ejecutar(event.getAggregateId());
        });
    }
}
```

### 3.7 Dependencias del módulo (`shared/amqp/build.gradle`)

No se agregan dependencias externas nuevas. Spring AMQP ya incluye todo lo necesario:
- `RabbitTemplate` con soporte de Publisher Confirms
- `MessagePostProcessor` para headers
- `Channel` para manual ACK

```gradle
// Sin cambio — Spring AMQP ya provee todo lo necesario:
implementation "org.springframework.boot:spring-boot-starter-amqp"
```

Solo se añade la dependencia al módulo `shared:logger` para usar `MdcKeys`:

```gradle
implementation project(':shared:logger')   // nuevo — para MdcKeys.TRACE_ID / USER_ID
```

---

## 4. Vista de componentes

```
┌─────────────────────────────────────────────────────────────────────────┐
│  shared/amqp                                                            │
│                                                                         │
│  EventPublisher (interfaz)          AbstractEventConsumer (clase base)  │
│       ↑                                       ↑                         │
│  RabbitMQEventPublisher             {contexto}Consumer                  │
│  - añade X-Trace-Id header          - @RabbitListener(queues=...)       │
│  - añade X-User-Id header           - llama withCorrelation(...)        │
│  - publisher confirms               - MDC poblado automáticamente       │
│                                     - Manual ACK / NACK → DLQ          │
│  RabbitMQConfig                                                         │
│  - TopicExchange: arquisoft.events  ← productor publica aquí           │
│  - DirectExchange: arquisoft.dlx    ← mensajes fallidos van aquí       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│  {contexto}/infrastructure                                              │
│                                                                         │
│  {Contexto}QueueConfig                                                  │
│  - Queue: {contexto}.{entidad}.{accion}  (durable, x-dead-letter-*)    │
│  - Binding → arquisoft.events / routing-key = queue-name               │
│                                                                         │
│  {Evento}Consumer extends AbstractEventConsumer                         │
│  - @RabbitListener(queues = "{contexto}.{entidad}.{accion}")           │
│  - delega a UseCase de application                                      │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Cambios en `application.yml`

```yaml
spring:
  rabbitmq:
    # Fiabilidad del publicador
    publisher-confirm-type: CORRELATED   # añadir
    publisher-returns: true              # añadir
    listener:
      simple:
        acknowledge-mode: manual         # cambiar de: auto
        prefetch: 1                      # añadir — backpressure por consumer
        # concurrency y max-concurrency permanecen iguales (5 / 10)
```

---

## 6. Archivos a crear / modificar

| Archivo | Acción |
|---|---|
| `shared/amqp/src/main/java/com/arquisoft/shared/amqp/RabbitMQConfig.java` | Modificar: añadir `DLX_NAME`, declarar `DirectExchange` DLX, activar publisher confirms en `RabbitTemplate` |
| `shared/amqp/src/main/java/com/arquisoft/shared/amqp/RabbitMQEventPublisher.java` | Modificar: `MessagePostProcessor` para headers `X-Trace-Id` / `X-User-Id`, callback de confirm |
| `shared/amqp/src/main/java/com/arquisoft/shared/amqp/consumer/AbstractEventConsumer.java` | **Nuevo**: base class con `withCorrelation()` + MDC + manual ACK |
| `shared/amqp/build.gradle` | Modificar: añadir `implementation project(':shared:logger')` |
| `src/main/resources/application.yml` | Modificar: `publisher-confirm-type`, `publisher-returns`, `acknowledge-mode: manual`, `prefetch: 1` |

**Nota:** Las colas, bindings y consumers concretos son responsabilidad de cada bounded context
en su capa de infraestructura — `shared/amqp` solo provee las abstracciones base.

---

## 7. Lo que NO se implementa (fuera de scope)

- **Outbox Pattern:** se considera un patrón adicional de consistencia eventual que puede
  implementarse sobre este módulo si se identifican casos donde la DB y RabbitMQ pueden
  quedar inconsistentes en una misma transacción. No es parte de este diseño inicial.
- **Retry con backoff exponencial:** Spring AMQP `RetryTemplate` es compatible y puede
  añadirse como mejora incremental sobre `AbstractEventConsumer`.
- **Headers W3C `traceparent`:** el diseño usa headers internos `X-Trace-Id` / `X-User-Id`
  tal como documenta `pendiente-amqp-traceid.md`. La migración a OTEL `traceparent` es
  una evolución futura si se configura un exporter Brave/OTel.
