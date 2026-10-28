package com.arquisoft.artefactos.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArtefactosProyectosQueueConfig {

    public static final String PROYECTO_GRADO_REGISTRADO_QUEUE =
            ArtefactosQueues.PREFIJO + EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO;

    @Bean
    public Declarables artefactosProyectoGradoRegistradoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                PROYECTO_GRADO_REGISTRADO_QUEUE,
                EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ASESOR_PROYECTO_GRADO_ASIGNADO_QUEUE =
            ArtefactosQueues.PREFIJO + EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_ASIGNADO;

    @Bean
    public Declarables artefactosAsesorProyectoGradoAsignadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_PROYECTO_GRADO_ASIGNADO_QUEUE,
                EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_ASIGNADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ASESOR_PROYECTO_GRADO_FINALIZADO_QUEUE =
            ArtefactosQueues.PREFIJO + EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_FINALIZADO;

    @Bean
    public Declarables artefactosAsesorProyectoGradoFinalizadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_PROYECTO_GRADO_FINALIZADO_QUEUE,
                EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_FINALIZADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
