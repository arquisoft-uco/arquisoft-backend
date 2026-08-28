package com.arquisoft.solicitudes.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SolicitudesUsuariosQueueConfig {

    public static final String USUARIO_CREADO_QUEUE = "solicitudes.usuarios.usuario.creado";

    public static final String USUARIO_CREADO_ROUTING_KEY = "usuarios.usuario.creado";

    @Bean
    public Queue solicitudesUsuarioCreadoQueue() {
        return QueueBuilder
                .durable(USUARIO_CREADO_QUEUE)
                .withArgument("x-dead-letter-exchange", RabbitMQConfig.DLX_NAME)
                .withArgument("x-dead-letter-routing-key", USUARIO_CREADO_QUEUE + ".dead")
                .build();
    }

    @Bean
    public Binding solicitudesUsuarioCreadoBinding(
            Queue solicitudesUsuarioCreadoQueue,
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange) {
        return BindingBuilder
                .bind(solicitudesUsuarioCreadoQueue)
                .to(arquisoftEventsExchange)
                .with(USUARIO_CREADO_ROUTING_KEY);
    }
}
