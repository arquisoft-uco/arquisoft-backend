package com.arquisoft.fichas.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FichasUsuariosQueueConfig {

    // El nombre de la cola tiene que seguir siendo una expresion constante: @RabbitListener lo lee
    // como valor de anotacion (JLS 9.7.1), asi que no puede salir de una llamada a metodo.
    public static final String USUARIO_CREADO_QUEUE =
            FichasQueues.PREFIJO + EventTopics.Usuarios.USUARIO_CREADO;

    @Bean
    public Declarables fichasUsuarioCreadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                USUARIO_CREADO_QUEUE,
                EventTopics.Usuarios.USUARIO_CREADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
