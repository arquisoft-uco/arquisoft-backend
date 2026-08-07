package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.events.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

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
    public void publish(DomainEvent evento) {
        String claveRuta = evento.getTemaEvento();
        CorrelationData corr = new CorrelationData(evento.getIdEvento());

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
                log.info("Evento publicado: tipo={} claveRuta={} idEvento={}",
                        evento.getTipoEvento(), claveRuta, evento.getIdEvento());
                return;
            } catch (AmqpException ex) {
                // Error transitorio de conectividad: vale la pena reintentar.
                lastException = ex;
                if (attempt < MAX_ATTEMPTS) {
                    log.warn("Error al publicar evento (intento {}/{}), reintentando en {} ms: tipo={} idEvento={}",
                            attempt, MAX_ATTEMPTS, backoffMs, evento.getTipoEvento(), evento.getIdEvento());
                    sleepUninterruptibly(backoffMs);
                    backoffMs *= 2;
                }
            } catch (RuntimeException ex) {
                // Error no transitorio (ej. fallo de serialización): no se reintenta.
                // Se loguea aquí para preservar el contexto del evento antes de propagar.
                log.error("Error no recuperable al publicar evento (sin reintentos): tipo={} claveRuta={} idEvento={}",
                        evento.getTipoEvento(), claveRuta, evento.getIdEvento(), ex);
                throw ex;
            }
        }

        log.error("Error al publicar evento tras {} intentos: tipo={} claveRuta={} idEvento={}",
                MAX_ATTEMPTS, evento.getTipoEvento(), claveRuta, evento.getIdEvento(), lastException);
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
