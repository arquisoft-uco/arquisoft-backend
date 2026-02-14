package com.arquisoft.shared.amqp;

/**
 * Publicador de eventos de dominio a través de RabbitMQ.
 * Los contextos usan esto para publicar eventos de dominio.
 */
public interface EventPublisher {
    /**
     * Publica un evento de dominio.
     */
    void publish(Object event);

    /**
     * Publica un evento de dominio con una ruta específica.
     */
    void publish(String routingKey, Object event);

    /**
     * Publica múltiples eventos.
     */
    void publishBatch(java.util.List<?> events);
}
