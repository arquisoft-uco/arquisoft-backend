package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;

/**
 * Publicador de eventos de dominio a RabbitMQ.
 * Cada evento conoce su routing key vía getRoutingKey().
 */
public interface EventPublisher {

    /**
     * Publica un evento de dominio al exchange arquisoft.events
     * usando la routing key que devuelve event.getRoutingKey().
     */
    void publish(DomainEvent event);
}
