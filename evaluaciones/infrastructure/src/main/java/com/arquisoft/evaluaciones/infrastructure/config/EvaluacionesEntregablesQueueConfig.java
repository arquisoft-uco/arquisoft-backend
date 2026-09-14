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
public class EvaluacionesEntregablesQueueConfig {

    public static final String ENTREGABLE_GENERADO_QUEUE =
            EvaluacionesQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO;

    @Bean
    public Declarables evaluacionesEntregableGeneradoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_GENERADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
