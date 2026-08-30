package com.arquisoft.fichas.infrastructure.config;

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
public class FichasUsuariosQueueConfig {

    public static final String USUARIO_CREADO_ROUTING_KEY = EventTopics.Usuarios.USUARIO_CREADO;

    public static final String USUARIO_CREADO_QUEUE =
            FichasQueues.PREFIJO + EventTopics.Usuarios.USUARIO_CREADO;

    @Bean
    public Queue fichasUsuarioCreadoQueue() {
        return QueueBuilder
                .durable(USUARIO_CREADO_QUEUE)
                .withArgument(RabbitMQConfig.ARG_DEAD_LETTER_EXCHANGE, RabbitMQConfig.DLX_NAME)
                .withArgument(RabbitMQConfig.ARG_DEAD_LETTER_ROUTING_KEY,
                        ColaDeadLetter.nombre(USUARIO_CREADO_QUEUE))
                .build();
    }

    @Bean
    public Queue fichasUsuarioCreadoDeadQueue() {
        return ColaDeadLetter.declarar(USUARIO_CREADO_QUEUE);
    }

    @Bean
    public Binding fichasUsuarioCreadoDeadBinding(
            Queue fichasUsuarioCreadoDeadQueue,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaDeadLetter.enlazar(fichasUsuarioCreadoDeadQueue, arquisoftDeadLetterExchange);
    }

    @Bean
    public Binding fichasUsuarioCreadoBinding(
            Queue fichasUsuarioCreadoQueue,
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange) {
        return BindingBuilder
                .bind(fichasUsuarioCreadoQueue)
                .to(arquisoftEventsExchange)
                .with(USUARIO_CREADO_ROUTING_KEY);
    }
}
