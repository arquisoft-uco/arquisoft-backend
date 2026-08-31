package com.arquisoft.shared.amqp.consumer;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.MensajeriaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.tracing.infrastructure.traza.propagacion.TrazaHeaders;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.dao.DataAccessException;
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

        var solicitud = SolicitudTraza.paraEvento(header(message, TrazaHeaders.AMQP_TRACE_ID),
                message.getMessageProperties().getConsumerQueue(),
                header(message, TrazaHeaders.AMQP_TRANSACTION_ID));

        try (var alcance = gestorTraza.abrir(solicitud)) {
            gestorTraza.registrarUsuario(header(message, TrazaHeaders.AMQP_USER_ID));
            log.debug(Mensajes.obtener(MensajeriaKey.LOG_EVENTO_RECIBIDO),
                    message.getMessageProperties().getConsumerQueue(), deliveryTag);
            try {
                handler.handle();
            } catch (Exception ex) {
                rechazar(message, channel, deliveryTag, ex);
                return;
            }

            log.debug(Mensajes.obtener(MensajeriaKey.LOG_EVENTO_PROCESADO),
                    message.getMessageProperties().getConsumerQueue(), deliveryTag);
        }
        // Solo se alcanza si handler.handle() no lanzó excepción.
        channel.basicAck(deliveryTag, false);
    }

    // Un payload que no deserializa o que viola una regla de negocio fallara igual en el
    // siguiente intento: reencolarlo solo bloquea la cola. Una caida de base de datos o del
    // proveedor externo, en cambio, suele haberse ido para cuando el mensaje vuelve. De ahi que
    // solo el fallo transitorio se reencole, y una sola vez — isRedelivered() acota el ciclo sin
    // necesidad de llevar un contador propio en las cabeceras.
    private void rechazar(Message message, Channel channel, long deliveryTag, Exception ex)
            throws IOException {

        boolean transitorio = esTransitorio(ex);

        if (transitorio && !Boolean.TRUE.equals(message.getMessageProperties().isRedelivered())) {
            log.warn(Mensajes.obtener(MensajeriaKey.LOG_EVENTO_REENCOLADO),
                    deliveryTag, ex.getMessage(), ex);
            channel.basicNack(deliveryTag, false, true);
            return;
        }

        var clave = transitorio
                ? MensajeriaKey.LOG_EVENTO_A_DLQ_TRAS_REINTENTO
                : MensajeriaKey.LOG_EVENTO_A_DLQ;
        log.error(Mensajes.obtener(clave), deliveryTag, ex.getMessage(), ex);
        channel.basicNack(deliveryTag, false, false);
    }

    private static boolean esTransitorio(Throwable ex) {
        for (var causa = ex; causa != null && causa != causa.getCause();
                causa = causa.getCause()) {
            if (causa instanceof InfrastructureException || causa instanceof DataAccessException) {
                return true;
            }
        }
        return false;
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
