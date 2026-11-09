package com.arquisoft.proyectos.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProyectosEntregablesQueueConfig {

    public static final String ENTREGABLE_GENERADO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO;
    public static final String ENTREGABLE_CERRADO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CERRADO;
    public static final String ENTREGABLE_CANCELADO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CANCELADO;
    public static final String ENTREGABLE_RECHAZADO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_RECHAZADO;

    @Bean
    public Declarables proyectosEntregableProyectoGradoGeneradoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_GENERADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables proyectosEntregableProyectoGradoCerradoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_CERRADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CERRADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables proyectosEntregableProyectoGradoCanceladoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_CANCELADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CANCELADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables proyectosEntregableProyectoGradoRechazadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_RECHAZADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_RECHAZADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
