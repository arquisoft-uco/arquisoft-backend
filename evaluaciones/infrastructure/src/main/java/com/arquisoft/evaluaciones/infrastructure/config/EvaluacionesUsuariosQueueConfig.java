package com.arquisoft.evaluaciones.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvaluacionesUsuariosQueueConfig {

    public static final String JURADO_AGREGADO_QUEUE =
            EvaluacionesQueues.PREFIJO + EventTopics.Usuarios.JURADO_AGREGADO;

    @Bean
    public Declarables evaluacionesJuradoAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                JURADO_AGREGADO_QUEUE,
                EventTopics.Usuarios.JURADO_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
