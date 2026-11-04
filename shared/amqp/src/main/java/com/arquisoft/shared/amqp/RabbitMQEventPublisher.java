package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.app.MensajeriaKey;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(EventPublisher.class)
public class RabbitMQEventPublisher implements EventPublisher {

    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 500L;

    private final RabbitTemplate rabbitTemplate;
    private final AppLogger logger;

    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate, AppLogger logger) {
        this.rabbitTemplate = rabbitTemplate;
        this.logger = logger;
    }

    @Override
    public void publish(DomainEvent evento) {
        String claveRuta = evento.getTemaEvento();
        var corr = new CorrelationData(evento.getIdEvento());

        AmqpException lastException = null;
        long backoffMs = INITIAL_BACKOFF_MS;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                // Headers X-Trace-Id y X-User-Id son inyectados por el traceHeadersPostProcessor
                // registrado en RabbitMQConfig#rabbitTemplate — no se duplican aquí.
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    claveRuta,
                    evento,
                    corr
                );
                logger.info(MensajeriaKey.LOG_EVENTO_PUBLICADO,
                        evento.getTipoEvento(), claveRuta, evento.getIdEvento());
                return;
            } catch (AmqpException ex) {
                // Error transitorio de conectividad: vale la pena reintentar.
                lastException = ex;
                if (attempt < MAX_ATTEMPTS) {
                    logger.warn(MensajeriaKey.LOG_PUBLICACION_REINTENTO,
                            attempt, MAX_ATTEMPTS, backoffMs, evento.getTipoEvento(), evento.getIdEvento());
                    sleepUninterruptibly(backoffMs);
                    backoffMs *= 2;
                }
            } catch (RuntimeException ex) {
                // Error no transitorio (ej. fallo de serialización): no se reintenta.
                // Se loguea aquí para preservar el contexto del evento antes de propagar.
                logger.error(MensajeriaKey.LOG_PUBLICACION_NO_RECUPERABLE, ex,
                        evento.getTipoEvento(), claveRuta, evento.getIdEvento());
                throw ex;
            }
        }

        logger.error(MensajeriaKey.LOG_PUBLICACION_AGOTADA, lastException,
                MAX_ATTEMPTS, evento.getTipoEvento(), claveRuta, evento.getIdEvento());
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
