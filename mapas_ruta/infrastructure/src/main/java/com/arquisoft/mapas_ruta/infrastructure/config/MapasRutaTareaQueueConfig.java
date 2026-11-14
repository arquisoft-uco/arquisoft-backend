package com.arquisoft.mapas_ruta.infrastructure.config;

import com.arquisoft.shared.amqp.ColaEvento;
import com.arquisoft.shared.message.constant.EventTopics;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapasRutaTareaQueueConfig {

    public static final String TAREA_FINALIZADA_QUEUE =
            MapasRutaQueues.PREFIJO + EventTopics.MapasRuta.TAREA_FINALIZADA;

    @Bean
    public Declarables mapasRutaTareaFinalizadaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                TAREA_FINALIZADA_QUEUE,
                EventTopics.MapasRuta.TAREA_FINALIZADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
