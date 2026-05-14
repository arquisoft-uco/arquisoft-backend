package com.arquisoft.shared.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para entidades de dominio que emiten eventos.
 *
 * Acumula los eventos no publicados en memoria. El use case los drena
 * tras persistir mediante getUnPublishedEvents() y los publica vía
 * {@link EventPublisher}, luego llama a clearUnPublishedEvents().
 *
 * Nota: esta clase NO define identidad, invariantes ni comportamiento de un
 * Aggregate Root completo — solo encapsula la gestión de eventos. Una entidad
 * es un Aggregate Root cuando, además de extender esta clase, define su
 * identidad, sus invariantes y su comportamiento de negocio.
 */
public abstract class EventEmittingEntity {
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
