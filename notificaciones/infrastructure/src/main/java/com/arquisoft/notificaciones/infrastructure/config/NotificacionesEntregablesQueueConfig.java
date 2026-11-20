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
public class NotificacionesEntregablesQueueConfig {

    public static final String ENTREGABLE_GENERADO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO;
    public static final String ENTREGABLE_DISPONIBLE_EVALUACION_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_DISPONIBLE_EVALUACION;
    public static final String ENTREGABLE_CANCELADO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CANCELADO;
    public static final String ENTREGABLE_CERRADO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CERRADO;

    @Bean
    public Declarables notificacionesEntregableProyectoGradoGeneradoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_GENERADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesEntregableProyectoGradoDisponibleEvaluacionDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_DISPONIBLE_EVALUACION_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_DISPONIBLE_EVALUACION,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesEntregableProyectoGradoCanceladoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_CANCELADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CANCELADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables notificacionesEntregableProyectoGradoCerradoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_CERRADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CERRADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
