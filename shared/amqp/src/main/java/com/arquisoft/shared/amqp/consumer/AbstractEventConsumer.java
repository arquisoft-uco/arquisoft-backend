package com.arquisoft.shared.amqp.consumer;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.MensajeriaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.tracing.infrastructure.traza.propagacion.TrazaHeaders;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
public abstract class AbstractEventConsumer {

    private final ObjectMapper objectMapper;
    private final GestorTraza gestorTraza;

    protected AbstractEventConsumer(ObjectMapper objectMapper, GestorTraza gestorTraza) {
        this.objectMapper = objectMapper;
        this.gestorTraza = gestorTraza;
    }

    protected <T> T deserialize(Message message, Class<T> type) {
        return objectMapper.readValue(message.getBody(), type);
    }

    protected void withCorrelation(Message message, Channel channel, EventHandler handler)
            throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        var solicitud = SolicitudTraza.paraEvento(header(message, TrazaHeaders.AMQP_TRACE_ID));

        try (var alcance = gestorTraza.abrir(solicitud)) {
            gestorTraza.registrarUsuario(header(message, TrazaHeaders.AMQP_USER_ID));
            try {
                handler.handle();
            } catch (Exception ex) {
                log.error(Mensajes.obtener(MensajeriaKey.LOG_EVENTO_A_DLQ),
                        deliveryTag, ex.getMessage(), ex);
                channel.basicNack(deliveryTag, false, false);
                return;
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
