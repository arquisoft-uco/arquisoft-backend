package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificacionesEvaluacionesQueueConfig {

    public static final String EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS;

    @Bean
    public Declarables notificacionesEvaluacionesCualitativasJuradoRegistradasDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS_QUEUE,
                EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
