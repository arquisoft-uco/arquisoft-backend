package com.arquisoft.shared.events;

/**
 * Puerto de salida para la publicación de eventos de dominio.
 *
 * <p>Definido en {@code shared:domain} (Java puro, sin dependencias de framework)
 * para que cualquier capa de aplicación pueda inyectarlo sin acoplarse a la
 * infraestructura de mensajería. La implementación concreta ({@code RabbitMQEventPublisher}
 * en {@code shared:amqp}) es la única que conoce RabbitMQ.
 */
public interface EventPublisher {

    /**
     * Publica un evento de dominio al exchange central.
     * La routing key usada es {@link DomainEvent#getEventTopic()}.
     */
    void publish(DomainEvent event);
}
