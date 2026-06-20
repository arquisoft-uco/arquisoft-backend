package com.arquisoft.shared.amqp;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.events.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Implementación de {@link EventPublisher} basada en Spring Modulith.
 *
 * <p>Delega a {@link ApplicationEventPublisher#publishEvent}, que Spring Modulith intercepta
 * para aplicar el Outbox Pattern:
 * <ol>
 *   <li>Dentro de la transacción activa: persiste el evento en la tabla {@code event_publication}
 *       (misma transacción que el {@code save} del aggregate — atomicidad garantizada).</li>
 *   <li>Tras el commit: publica el evento al exchange RabbitMQ configurado en
 *       {@link ModulithAmqpExternalizationConfig} usando {@code event.getEventTopic()} como
 *       routing key.</li>
 *   <li>Si la publicación falla, el evento queda en {@code event_publication} con
 *       {@code completion_date = NULL} y Spring Modulith lo reintenta automáticamente.</li>
 * </ol>
 *
 * <p>Esta clase tiene prioridad sobre {@link RabbitMQEventPublisher} (que lleva
 * {@code @ConditionalOnMissingBean}) mientras Spring Modulith esté en el classpath.
 *
 * <p><b>Alcance de atomicidad:</b> la atomicidad es garantizada cuando el use case
 * ejecuta dentro de una transacción cuyo {@code DataSource} tiene la tabla
 * {@code event_publication}. {@code ContextAwareEventPublicationRepository} detecta
 * automáticamente qué DataSource tiene la transacción activa.
 */
@Component
@RequiredArgsConstructor
public class SpringModulithEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
