package com.arquisoft.shared.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para Agregados (Aggregate Root).
 * Gestiona eventos de dominio no publicados.
 */
public abstract class AggregateRoot {
    private final List<DomainEvent> unPublishedEvents = new ArrayList<>();

    protected void publishEvent(DomainEvent event) {
        unPublishedEvents.add(event);
    }

    public List<DomainEvent> getUnPublishedEvents() {
        return new ArrayList<>(unPublishedEvents);
    }

    public void clearUnPublishedEvents() {
        unPublishedEvents.clear();
    }
}
