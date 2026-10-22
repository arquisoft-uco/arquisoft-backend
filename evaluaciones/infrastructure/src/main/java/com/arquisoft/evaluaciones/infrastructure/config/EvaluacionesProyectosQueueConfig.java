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

    public static final String ESTUDIANTES_ASIGNADOS_QUEUE =
            EvaluacionesQueues.PREFIJO + EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS;

    @Bean
    public Declarables evaluacionesEstudiantesProyectoGradoAsignadosDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTUDIANTES_ASIGNADOS_QUEUE,
                EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
