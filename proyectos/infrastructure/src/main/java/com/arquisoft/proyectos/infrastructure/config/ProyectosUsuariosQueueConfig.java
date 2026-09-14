package com.arquisoft.proyectos.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProyectosUsuariosQueueConfig {

    public static final String COORDINADOR_AGREGADO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Usuarios.COORDINADOR_AGREGADO;

    @Bean
    public Declarables proyectosCoordinadorAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                COORDINADOR_AGREGADO_QUEUE,
                EventTopics.Usuarios.COORDINADOR_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
