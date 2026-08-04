package com.arquisoft.shared.events;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
    private final List<DomainEvent> unPublishedEvents = new ArrayList<>();

    public void publishEvent(DomainEvent event) {
        unPublishedEvents.add(event);
    }

    public List<DomainEvent> drainUnPublishedEvents() {
        List<DomainEvent> drained = new ArrayList<>(unPublishedEvents);
        unPublishedEvents.clear();
        return drained;
    }

    protected List<DomainEvent> getUnPublishedEvents() {
        return new ArrayList<>(unPublishedEvents);
    }
}
