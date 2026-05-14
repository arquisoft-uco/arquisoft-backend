package com.arquisoft.shared.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para Aggregate Roots que emiten eventos de dominio.
 *
 * <p>Acumula los eventos no publicados en memoria. El use case los drena
 * tras persistir el aggregate llamando a {@link #drainUnPublishedEvents()},
 * que retorna y limpia la lista en una sola operación atómica — evitando
 * pérdida silenciosa de eventos por olvidar el paso de limpieza.
 *
 * <p>Una entidad es un Aggregate Root cuando, además de extender esta clase,
 * define su identidad, sus invariantes y su comportamiento de negocio.
 */
public abstract class AggregateRoot {
    private final List<DomainEvent> unPublishedEvents = new ArrayList<>();

    protected void publishEvent(DomainEvent event) {
        unPublishedEvents.add(event);
    }

    /**
     * Retorna todos los eventos no publicados y limpia la lista en una sola
     * operación. Usar este método en los use cases para drenar y publicar
     * eventos tras persistir el aggregate.
     *
     * @return lista de eventos acumulados desde la última llamada a este método
     */
    public List<DomainEvent> drainUnPublishedEvents() {
        List<DomainEvent> drained = new ArrayList<>(unPublishedEvents);
        unPublishedEvents.clear();
        return drained;
    }

    /**
     * Solo para uso en tests de dominio que necesiten inspeccionar eventos
     * sin limpiarlos. En producción usar {@link #drainUnPublishedEvents()}.
     */
    protected List<DomainEvent> getUnPublishedEvents() {
        return new ArrayList<>(unPublishedEvents);
    }
}
