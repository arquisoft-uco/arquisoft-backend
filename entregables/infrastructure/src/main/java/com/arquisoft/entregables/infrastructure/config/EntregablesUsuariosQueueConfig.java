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
public class EntregablesUsuariosQueueConfig {

    public static final String ASESOR_AGREGADO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Usuarios.ASESOR_AGREGADO;

    @Bean
    public Declarables entregablesAsesorAgregadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_AGREGADO_QUEUE,
                EventTopics.Usuarios.ASESOR_AGREGADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    public static final String ASESOR_REMOVIDO_QUEUE =
            EntregablesQueues.PREFIJO + EventTopics.Usuarios.ASESOR_REMOVIDO;

    @Bean
    public Declarables entregablesAsesorRemovidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ASESOR_REMOVIDO_QUEUE,
                EventTopics.Usuarios.ASESOR_REMOVIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
