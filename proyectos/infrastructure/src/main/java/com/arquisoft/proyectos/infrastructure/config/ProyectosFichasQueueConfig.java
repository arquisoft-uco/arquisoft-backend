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
public class ProyectosFichasQueueConfig {

    public static final String FICHA_PERFIL_APROBADA_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.Fichas.FICHA_PERFIL_APROBADA;

    @Bean
    public Declarables proyectosFichaPerfilAprobadaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                FICHA_PERFIL_APROBADA_QUEUE,
                EventTopics.Fichas.FICHA_PERFIL_APROBADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
