package com.arquisoft.shared.publisher;

import com.arquisoft.shared.events.DomainEvent;

public interface EventPublisher {

    void publish(DomainEvent event);
}
