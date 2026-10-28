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
public class NotificacionesArtefactosQueueConfig {

    public static final String VERSION_ARTEFACTO_GENERADA_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Artefactos.VERSION_ARTEFACTO_GENERADA;

    @Bean
    public Declarables notificacionesVersionArtefactoGeneradaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                VERSION_ARTEFACTO_GENERADA_QUEUE,
                EventTopics.Artefactos.VERSION_ARTEFACTO_GENERADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
