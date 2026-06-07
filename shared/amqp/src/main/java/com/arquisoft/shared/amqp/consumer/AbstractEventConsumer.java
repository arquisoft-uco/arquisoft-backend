package com.arquisoft.shared.amqp.consumer;

import com.arquisoft.shared.logger.MdcKeys;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
public abstract class AbstractEventConsumer {

    private final ObjectMapper objectMapper;

    protected AbstractEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected <T> T deserialize(Message message, Class<T> type) {
        return objectMapper.readValue(message.getBody(), type);
    }

    protected void withCorrelation(Message message, Channel channel, EventHandler handler)
            throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        String traceId = header(message, "X-Trace-Id");
        String userId  = header(message, "X-User-Id");

        Map<String, String> prevMdc = MDC.getCopyOfContextMap();
        MDC.put(MdcKeys.TRACE_ID, traceId != null ? traceId
                                 : UUID.randomUUID().toString().replace("-", ""));
        MDC.put(MdcKeys.USER_ID,  userId  != null ? userId : "EVENT");

        try {
            handler.handle();
        } catch (Exception ex) {
            log.error("Error procesando evento — enviando a DLQ: deliveryTag={} error={}",
                    deliveryTag, ex.getMessage(), ex);
            // requeue=false → el mensaje va al Dead Letter Exchange en lugar de volver a la cola.
            // Evita bucles infinitos de re-entrega ante errores de negocio no recuperables.
            channel.basicNack(deliveryTag, false, false);
            return;
        } finally {
            if (prevMdc != null) {
                MDC.setContextMap(prevMdc);
            } else {
                MDC.clear();
            }
        }
        // Solo se alcanza si handler.handle() no lanzó excepción.
        channel.basicAck(deliveryTag, false);
    }

    protected String header(Message message, String headerName) {
        Object value = message.getMessageProperties().getHeader(headerName);
        return value != null ? value.toString() : null;
    }

    @FunctionalInterface
    public interface EventHandler {
        void handle() throws Exception;
    }
}
