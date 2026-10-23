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
public class EvaluacionesEvaluacionesQueueConfig {

    public static final String EVALUACIONES_CUALITATIVAS_ASESOR_REGISTRADAS_QUEUE =
            EvaluacionesQueues.PREFIJO + EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_ASESOR_REGISTRADAS;

    @Bean
    public Declarables evaluacionesEvaluacionesCualitativasAsesorRegistradasDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                EVALUACIONES_CUALITATIVAS_ASESOR_REGISTRADAS_QUEUE,
                EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_ASESOR_REGISTRADAS,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
