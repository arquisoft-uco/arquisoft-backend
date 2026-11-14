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

    public static final String REVISION_ASESOR_CERRADA_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Artefactos.REVISION_ASESOR_CERRADA;

    public static final String ARTEFACTO_OMITIDO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Artefactos.ARTEFACTO_OMITIDO;

    public static final String ARTEFACTO_REACTIVADO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Artefactos.ARTEFACTO_REACTIVADO;

    @Bean
    public Declarables entregablesRevisionAsesorCerradaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                REVISION_ASESOR_CERRADA_QUEUE,
                EventTopics.Artefactos.REVISION_ASESOR_CERRADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables entregablesArtefactoOmitidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ARTEFACTO_OMITIDO_QUEUE,
                EventTopics.Artefactos.ARTEFACTO_OMITIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables entregablesArtefactoReactivadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ARTEFACTO_REACTIVADO_QUEUE,
                EventTopics.Artefactos.ARTEFACTO_REACTIVADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
