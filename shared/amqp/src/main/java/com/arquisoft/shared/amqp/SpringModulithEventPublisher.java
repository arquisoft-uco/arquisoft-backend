package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

// @Primary y no solo el @ConditionalOnMissingBean del respaldo: esa condicion, sobre un
// @Component escaneado, se evalua en un orden que Spring Boot solo garantiza dentro de una
// autoconfiguracion. Si alguna vez no cortara, ganar el publisher directo significaria saltarse
// el outbox en silencio — sin fila en event_publication y sin atomicidad.
@Component
@Primary
@RequiredArgsConstructor
public class SpringModulithEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent evento) {
        applicationEventPublisher.publishEvent(evento);
    }
}
