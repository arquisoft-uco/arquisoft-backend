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

    public static final String EVALUACION_FINALIZADA_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Evaluaciones.EVALUACION_FINALIZADA;

    public static final String EVALUACION_GENERADA_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Evaluaciones.EVALUACION_GENERADA;

    @Bean
    public Declarables notificacionesEvaluacionFinalizadaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                EVALUACION_FINALIZADA_QUEUE,
                EventTopics.Evaluaciones.EVALUACION_FINALIZADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesEvaluacionGeneradaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                EVALUACION_GENERADA_QUEUE,
                EventTopics.Evaluaciones.EVALUACION_GENERADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
