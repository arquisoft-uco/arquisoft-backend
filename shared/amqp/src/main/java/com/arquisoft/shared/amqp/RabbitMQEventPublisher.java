package com.arquisoft.shared.amqp;

import com.arquisoft.shared.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Implementación de {@link EventPublisher} que publica eventos de dominio
 * al exchange {@value RabbitMQConfig#EXCHANGE_NAME} usando el topic conceptual
 * que cada evento declara en {@link DomainEvent#getEventTopic()} como routing key.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(DomainEvent event) {
        String routingKey = event.getEventTopic();
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    routingKey,
                    event
            );
            log.info("Evento publicado: type={} routingKey={} eventId={}",
                    event.getEventType(), routingKey, event.getEventId());
        } catch (AmqpException ex) {
            log.error("Error al publicar evento: type={} routingKey={} eventId={}",
                    event.getEventType(), routingKey, event.getEventId(), ex);
            throw ex;
        }
    }
}
