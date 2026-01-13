package com.arquisoft.shared.domain;

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
}
