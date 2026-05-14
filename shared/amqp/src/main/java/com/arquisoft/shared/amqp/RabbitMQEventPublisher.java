package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.MdcKeys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementación de {@link EventPublisher} que publica eventos de dominio
 * al exchange {@value RabbitMQConfig#EXCHANGE_NAME} usando el topic conceptual
 * que cada evento declara en {@link DomainEvent#getEventTopic()} como routing key.
 *
 * <p><b>Fiabilidad:</b> reintentos automáticos con backoff exponencial ante caídas
 * momentáneas de RabbitMQ (hasta 3 intentos: 500 ms → 1 s → 2 s).
 * Si el broker sigue caído después de los reintentos, la excepción se propaga
 * al use case para que el request HTTP falle con 500.
 *
 * <p><b>Trazabilidad:</b> cada mensaje incluye los headers {@code X-Trace-Id} y
 * {@code X-User-Id} tomados del MDC del hilo publicador. Si el MDC está vacío
 * (evento disparado fuera de un request HTTP), se genera un traceId aleatorio y
 * userId queda como {@code "SYSTEM"}.
 */
@Slf4j
@Component
public class RabbitMQEventPublisher implements EventPublisher {

    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 500L;

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(DomainEvent event) {
        String routingKey = event.getEventTopic();
        CorrelationData corr = new CorrelationData(event.getEventId());

        // Capturar MDC antes del bucle — en Virtual Threads el hilo no cambia,
        // pero se captura aquí para que el MessagePostProcessor (lambda) lo cierre.
        String traceId = MDC.get(MdcKeys.TRACE_ID);
        String userId  = MDC.get(MdcKeys.USER_ID);

        AmqpException lastException = null;
        long backoffMs = INITIAL_BACKOFF_MS;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    routingKey,
                    event,
                    msg -> {
                        msg.getMessageProperties().setHeader("X-Trace-Id",
                            traceId != null ? traceId : UUID.randomUUID().toString().replace("-", ""));
                        msg.getMessageProperties().setHeader("X-User-Id",
                            userId != null ? userId : "SYSTEM");
                        return msg;
                    },
                    corr
                );
                log.info("Evento publicado: type={} routingKey={} eventId={}",
                        event.getEventType(), routingKey, event.getEventId());
                return;
            } catch (AmqpException ex) {
                lastException = ex;
                if (attempt < MAX_ATTEMPTS) {
                    log.warn("Error al publicar evento (intento {}/{}), reintentando en {} ms: type={} eventId={}",
                            attempt, MAX_ATTEMPTS, backoffMs, event.getEventType(), event.getEventId());
                    sleepUninterruptibly(backoffMs);
                    backoffMs *= 2;
                }
            }
        }

        log.error("Error al publicar evento tras {} intentos: type={} routingKey={} eventId={}",
                MAX_ATTEMPTS, event.getEventType(), routingKey, event.getEventId(), lastException);
        throw lastException;
    }

    private void sleepUninterruptibly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
