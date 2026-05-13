package com.arquisoft.shared.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento base para todos los eventos de dominio del sistema.
 * Todos los contextos publican eventos que heredan de esta clase.
 */
public abstract class DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final LocalDateTime occurredAt;
    private final String eventType;

    protected DomainEvent(String aggregateId) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.occurredAt = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }

    public String getEventId() {
        return eventId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public String getEventType() {
        return eventType;
    }

    /**
     * Topic conceptual al que pertenece este evento.
     * Formato esperado: '{contexto}.{entidad}.{accion}' (ej. 'fichas.ficha.creada').
     *
     * Este valor es agnóstico del broker — la implementación de EventPublisher
     * decide cómo interpretarlo (en RabbitMQ se usa como routing key del
     * exchange arquisoft.events; en otros brokers podría ser un topic de Kafka
     * o un canal genérico).
     */
    public abstract String getEventTopic();
}
