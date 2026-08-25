package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Colas que notificaciones consume del contexto fichas.
 *
 * <p>Convencion de nombres: la cola es {@code {contextoConsumidor}.{routingKey}} y la routing key
 * es el {@code eventTopic} del evento del productor.
 */
@Configuration
public class NotificacionesFichasQueueConfig {

    public static final String ASESOR_CAMBIADO_QUEUE =
            "notificaciones.fichas.ficha_perfil.asesor_cambiado";

    public static final String ASESOR_CAMBIADO_ROUTING_KEY =
            "fichas.ficha_perfil.asesor_cambiado";

    @Bean
    public Queue notificacionesAsesorCambiadoQueue() {
        return QueueBuilder
                .durable(ASESOR_CAMBIADO_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConfig.DLX_NAME)
                .withArgument("x-dead-letter-routing-key", ASESOR_CAMBIADO_QUEUE + ".dead")
                .build();
    }

    @Bean
    public Binding notificacionesAsesorCambiadoBinding(
            Queue notificacionesAsesorCambiadoQueue,
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange) {
        return BindingBuilder
                .bind(notificacionesAsesorCambiadoQueue)
                .to(arquisoftEventsExchange)
                .with(ASESOR_CAMBIADO_ROUTING_KEY);
    }
}
