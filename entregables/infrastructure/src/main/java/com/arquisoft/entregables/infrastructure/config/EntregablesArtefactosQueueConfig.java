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
public class EntregablesArtefactosQueueConfig {

    public static final String PAQUETE_ENTREGABLE_GENERADO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Artefactos.PAQUETE_ENTREGABLE_GENERADO;

    @Bean
    public Declarables entregablesPaqueteEntregableGeneradoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                PAQUETE_ENTREGABLE_GENERADO_QUEUE,
                EventTopics.Artefactos.PAQUETE_ENTREGABLE_GENERADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
