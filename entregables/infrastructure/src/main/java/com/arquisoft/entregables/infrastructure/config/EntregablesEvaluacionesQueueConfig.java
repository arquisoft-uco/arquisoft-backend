package com.arquisoft.entregables.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntregablesEvaluacionesQueueConfig {

    public static final String EVALUACION_FINALIZADA_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Evaluaciones.EVALUACION_FINALIZADA;

    @Bean
    public Declarables entregablesEvaluacionFinalizadaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                EVALUACION_FINALIZADA_QUEUE,
                EventTopics.Evaluaciones.EVALUACION_FINALIZADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
