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
public class EvaluacionesProyectosQueueConfig {

    public static final String ESTUDIANTE_PROYECTO_ASIGNADO_QUEUE =
            EvaluacionesQueues.PREFIJO + EventTopics.Proyectos.ESTUDIANTE_PROYECTO_ASIGNADO;

    public static final String ESTUDIANTE_PROYECTO_DESTITUIDO_QUEUE =
            EvaluacionesQueues.PREFIJO + EventTopics.Proyectos.ESTUDIANTE_PROYECTO_DESTITUIDO;

    @Bean
    public Declarables evaluacionesEstudianteProyectoAsignadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTE_PROYECTO_ASIGNADO_QUEUE,
                EventTopics.Proyectos.ESTUDIANTE_PROYECTO_ASIGNADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables evaluacionesEstudianteProyectoDestituidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTE_PROYECTO_DESTITUIDO_QUEUE,
                EventTopics.Proyectos.ESTUDIANTE_PROYECTO_DESTITUIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
