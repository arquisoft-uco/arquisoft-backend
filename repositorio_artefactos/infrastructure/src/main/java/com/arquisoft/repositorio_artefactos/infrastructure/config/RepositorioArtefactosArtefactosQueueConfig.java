package com.arquisoft.repositorio_artefactos.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositorioArtefactosArtefactosQueueConfig {

    public static final String VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_QUEUE =
            RepositorioArtefactosQueues.PREFIJO
                    + EventTopics.Artefactos.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA;

    @Bean
    public Declarables repositorioArtefactosVersionRepositorioArtefactoEliminacionAutorizadaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_QUEUE,
                EventTopics.Artefactos.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
