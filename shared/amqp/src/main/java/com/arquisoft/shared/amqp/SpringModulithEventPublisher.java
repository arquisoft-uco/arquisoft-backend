package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.MensajeriaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class SpringModulithEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent evento) {
        applicationEventPublisher.publishEvent(evento);

        log.debug(Mensajes.obtener(MensajeriaKey.LOG_EVENTO_AL_OUTBOX),
                evento.getTipoEvento(), evento.getTemaEvento());
    }
}
