package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringModulithEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent evento) {
        applicationEventPublisher.publishEvent(evento);
    }
}
