package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.shared.amqp.ColaDeadLetter;
import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificacionesFichasQueueConfig {

    public static final String ASESOR_CAMBIADO_ROUTING_KEY =
            EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;

    public static final String ASESOR_CAMBIADO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;

    @Bean
    public Queue notificacionesAsesorCambiadoQueue() {
        return QueueBuilder
                .durable(ASESOR_CAMBIADO_QUEUE)
                .withArgument(RabbitMQConfig.ARG_DEAD_LETTER_EXCHANGE, RabbitMQConfig.DLX_NAME)
                .withArgument(RabbitMQConfig.ARG_DEAD_LETTER_ROUTING_KEY,
                        ColaDeadLetter.nombre(ASESOR_CAMBIADO_QUEUE))
                .build();
    }

    @Bean
    public Queue notificacionesAsesorCambiadoDeadQueue() {
        return ColaDeadLetter.declarar(ASESOR_CAMBIADO_QUEUE);
    }

    @Bean
    public Binding notificacionesAsesorCambiadoDeadBinding(
            Queue notificacionesAsesorCambiadoDeadQueue,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaDeadLetter.enlazar(
                notificacionesAsesorCambiadoDeadQueue, arquisoftDeadLetterExchange);
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
