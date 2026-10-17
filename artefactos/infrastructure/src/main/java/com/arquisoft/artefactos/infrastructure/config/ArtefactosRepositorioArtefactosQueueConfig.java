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
public class ArtefactosRepositorioArtefactosQueueConfig {

    public static final String VERSION_REPOSITORIO_ARTEFACTO_SUBIDA_QUEUE =
            ArtefactosQueues.PREFIJO + EventTopics.RepositorioArtefactos.VERSION_REPOSITORIO_ARTEFACTO_SUBIDA;

    @Bean
    public Declarables artefactosVersionRepositorioArtefactoSubidaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                VERSION_REPOSITORIO_ARTEFACTO_SUBIDA_QUEUE,
                EventTopics.RepositorioArtefactos.VERSION_REPOSITORIO_ARTEFACTO_SUBIDA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
