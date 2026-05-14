package com.arquisoft.shared.amqp.consumer;

import com.arquisoft.shared.logger.MdcKeys;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Clase base para todos los consumers AMQP del sistema.
 *
 * <p>Encapsula tres responsabilidades transversales que todo consumer debe cumplir:
 * <ol>
 *   <li><b>Propagación de traza:</b> lee los headers {@code X-Trace-Id} y {@code X-User-Id}
 *       del mensaje y los pone en el MDC antes de delegar al handler. Esto garantiza que
 *       todos los logs del procesamiento asíncrono compartan el mismo {@code traceId} del
 *       request HTTP original, haciendo la traza completa visible en Grafana.</li>
 *   <li><b>Manual ACK/NACK:</b> solo hace {@code basicAck} si el handler termina sin excepción.
 *       Si lanza, hace {@code basicNack(requeue=false)} para enviar el mensaje al
 *       Dead Letter Exchange ({@value com.arquisoft.shared.amqp.RabbitMQConfig#DLX_NAME})
 *       en lugar de re-encolar indefinidamente.</li>
 *   <li><b>Limpieza de MDC:</b> restaura el contexto MDC previo en el bloque {@code finally},
 *       compatible con Virtual Threads (cada VT tiene su propio {@code ThreadLocal}).</li>
 * </ol>
 *
 * <h3>Uso</h3>
 * <pre>{@code
 * @Slf4j
 * @Component
 * @RequiredArgsConstructor
 * public class FichaCreadaConsumer extends AbstractEventConsumer {
 *
 *     private final VincularFichaUseCase useCase;
 *
 *     @RabbitListener(queues = FichasQueueConfig.FICHA_CREADA_QUEUE)
 *     public void onFichaCreada(Message message, Channel channel) throws IOException {
 *         withCorrelation(message, channel, () -> {
 *             FichaCreadaEvent event = deserialize(message, FichaCreadaEvent.class);
 *             useCase.ejecutar(event.getAggregateId());
 *         });
 *     }
 * }
 * }</pre>
 */
@Slf4j
public abstract class AbstractEventConsumer {

    /**
     * Ejecuta el handler del evento garantizando propagación de traza, manual ACK/NACK
     * y limpieza de MDC.
     *
     * @param message     mensaje AMQP recibido del listener
     * @param channel     canal RabbitMQ para emitir ACK o NACK
     * @param handler     lógica de procesamiento del evento (puede lanzar cualquier excepción)
     * @throws IOException si falla la comunicación con el canal AMQP al hacer ACK/NACK
     */
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
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            log.error("Error procesando evento — enviando a DLQ: deliveryTag={} error={}",
                    deliveryTag, ex.getMessage(), ex);
            // requeue=false → el mensaje va al Dead Letter Exchange en lugar de volver a la cola.
            // Evita bucles infinitos de re-entrega ante errores de negocio no recuperables.
            channel.basicNack(deliveryTag, false, false);
        } finally {
            if (prevMdc != null) {
                MDC.setContextMap(prevMdc);
            } else {
                MDC.clear();
            }
        }
    }

    /**
     * Lee un header de correlación del mensaje. Devuelve {@code null} si el header no existe.
     */
    protected String header(Message message, String headerName) {
        Object value = message.getMessageProperties().getHeader(headerName);
        return value != null ? value.toString() : null;
    }

    /**
     * Contrato del handler de evento. Puede lanzar cualquier excepción —
     * {@link #withCorrelation} se encarga de convertirla en NACK.
     */
    @FunctionalInterface
    public interface EventHandler {
        void handle() throws Exception;
    }
}
