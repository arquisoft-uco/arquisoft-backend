package com.arquisoft.biblioteca.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BibliotecaEntregablesQueueConfig {

    public static final String ENTREGABLE_APROBADO_QUEUE =
            BibliotecaQueues.PREFIJO + EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_APROBADO;

    @Bean
    public Declarables bibliotecaEntregableProyectoGradoAprobadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ENTREGABLE_APROBADO_QUEUE,
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_APROBADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
