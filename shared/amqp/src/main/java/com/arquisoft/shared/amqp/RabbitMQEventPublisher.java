package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.events.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Fallback de {@link EventPublisher} activo únicamente cuando {@link SpringModulithEventPublisher}
 * no está en el contexto (Spring Modulith ausente o deshabilitado).
 *
 * <p><b>Política de reintentos:</b> backoff exponencial ante errores de conectividad
 * ({@link AmqpException}) — hasta 3 intentos: 500 ms → 1 s → 2 s. {@code RuntimeException}
 * no se reintenta (error de serialización u otro fallo de programación).
 *
 * <p><b>Trazabilidad:</b> los headers {@code X-Trace-Id} y {@code X-User-Id} son inyectados
 * por el {@code traceHeadersPostProcessor} registrado en el {@link RabbitTemplate}
 * ({@link RabbitMQConfig}).
 */
@Slf4j
@Component
@ConditionalOnMissingBean(EventPublisher.class)
public class RabbitMQEventPublisher implements EventPublisher { // EventPublisher de shared:domain

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

        AmqpException lastException = null;
        long backoffMs = INITIAL_BACKOFF_MS;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                // Headers X-Trace-Id y X-User-Id son inyectados por el traceHeadersPostProcessor
                // registrado en RabbitMQConfig#rabbitTemplate — no se duplican aquí.
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    routingKey,
                    event,
                    corr
                );
                log.info("Evento publicado: type={} routingKey={} eventId={}",
                        event.getEventType(), routingKey, event.getEventId());
                return;
            } catch (AmqpException ex) {
                // Error transitorio de conectividad: vale la pena reintentar.
                lastException = ex;
                if (attempt < MAX_ATTEMPTS) {
                    log.warn("Error al publicar evento (intento {}/{}), reintentando en {} ms: type={} eventId={}",
                            attempt, MAX_ATTEMPTS, backoffMs, event.getEventType(), event.getEventId());
                    sleepUninterruptibly(backoffMs);
                    backoffMs *= 2;
                }
            } catch (RuntimeException ex) {
                // Error no transitorio (ej. fallo de serialización): no se reintenta.
                // Se loguea aquí para preservar el contexto del evento antes de propagar.
                log.error("Error no recuperable al publicar evento (sin reintentos): type={} routingKey={} eventId={}",
                        event.getEventType(), routingKey, event.getEventId(), ex);
                throw ex;
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
