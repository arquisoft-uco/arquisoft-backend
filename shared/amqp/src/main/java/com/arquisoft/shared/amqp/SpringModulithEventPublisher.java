package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.app.MensajeriaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class SpringModulithEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final AppLogger logger;

    @Override
    public void publish(DomainEvent evento) {
        applicationEventPublisher.publishEvent(evento);

        logger.debug(MensajeriaKey.LOG_EVENTO_AL_OUTBOX,
                evento.getTipoEvento(), evento.getTemaEvento());
    }
}
