# Pendiente — Propagación de traceId en eventos AMQP

**Fecha:** 2026-05-12  
**Estado:** Pendiente — el módulo AMQP está siendo trabajado por otro compañero  
**Relacionado con:** `shared/amqp`, `seguridad/infrastructure/filter/AuditFilter.java`, `shared/logger/MdcKeys.java`

---

## Contexto

El `traceId` se genera en `AuditFilter` al inicio de cada request HTTP y se almacena en MDC.  
Todos los logs del request (use cases, servicios) lo heredan automáticamente vía `ThreadLocal`.

**Problema:** cuando un use case publica un evento RabbitMQ, el consumidor corre en un hilo distinto
y su MDC está vacío — los logs del procesamiento asíncrono no tienen `traceId`, rompiendo la trazabilidad.

---

## Solución a implementar

### En el productor (adaptador AMQP que publica eventos)

```java
// Al construir el Message o el MessagePostProcessor, añadir headers de correlación:
MessageProperties props = new MessageProperties();
props.setHeader("X-Trace-Id", MDC.get(MdcKeys.TRACE_ID));
props.setHeader("X-User-Id",  MDC.get(MdcKeys.USER_ID));
Message message = messageConverter.toMessage(payload, props);
rabbitTemplate.send(exchange, routingKey, message);
```

O con `MessagePostProcessor`:
```java
rabbitTemplate.convertAndSend(exchange, routingKey, payload, msg -> {
    msg.getMessageProperties().setHeader("X-Trace-Id", MDC.get(MdcKeys.TRACE_ID));
    msg.getMessageProperties().setHeader("X-User-Id",  MDC.get(MdcKeys.USER_ID));
    return msg;
});
```

### En el consumidor (listener AMQP que recibe eventos)

```java
@RabbitListener(queues = "nombre-de-la-cola")
public void onMessage(Message message) {
    String traceId = (String) message.getMessageProperties().getHeader("X-Trace-Id");
    String userId  = (String) message.getMessageProperties().getHeader("X-User-Id");

    MDC.put(MdcKeys.TRACE_ID, traceId != null ? traceId : UUID.randomUUID().toString().replace("-", ""));
    MDC.put(MdcKeys.USER_ID,  userId  != null ? userId  : "EVENT");
    try {
        // lógica del consumidor — todos los logs tendrán traceId del request original
        useCase.ejecutar(...);
    } finally {
        MDC.remove(MdcKeys.TRACE_ID);
        MDC.remove(MdcKeys.USER_ID);
    }
}
```

---

## Resultado esperado en Grafana

Búsqueda por `{traceId="abc123"}` mostrará la traza completa:

```
1. AuditFilter    → AUDIT POST /api/proyectos — 202          traceId=abc123  userId=user-1
2. UseCase        → Proyecto creado, publicando evento        traceId=abc123  userId=user-1
3. EventConsumer  → Procesando evento ProyectoCreado          traceId=abc123  userId=user-1
4. UseCase        → Notificación enviada a coordinador        traceId=abc123  userId=user-1
```

---

## Archivos a modificar

| Archivo | Cambio |
|---|---|
| Adaptador AMQP productor (`shared/amqp` o `*/infrastructure/amqp/`) | Añadir headers `X-Trace-Id` y `X-User-Id` al publicar |
| Listener AMQP consumidor (`*/infrastructure/amqp/`) | Leer headers y poblar MDC en try/finally |
| `MdcKeys.java` | Ya tiene `TRACE_ID` y `USER_ID` — no requiere cambios |

---

## Notas adicionales

- Los headers `X-Trace-Id` / `X-User-Id` son convención interna — no es OTEL.  
  Si en el futuro se configura un exporter Brave/OTel, el header estándar es `traceparent` (W3C).
- El patrón try/finally en el listener es idéntico al de `AuditFilter` — garantiza limpieza de MDC
  incluso si el procesamiento lanza excepción.
- Virtual Threads: cada `@RabbitListener` corre en su propio virtual thread con MDC aislado — 
  el patrón funciona sin cambios adicionales.
