package com.arquisoft.shared.events;

public interface EventPublisher {

    void publish(DomainEvent event);
}
