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
public class ProyectosMapasRutaQueueConfig {

    public static final String MAPA_RUTA_PLAZO_VENCIDO_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.MapasRuta.MAPA_RUTA_PLAZO_VENCIDO;

    public static final String MAPA_RUTA_FECHA_FIN_CAMBIADA_QUEUE =
            ProyectosQueues.PREFIJO + EventTopics.MapasRuta.MAPA_RUTA_FECHA_FIN_CAMBIADA;

    @Bean
    public Declarables proyectosMapaRutaPlazoVencidoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                MAPA_RUTA_PLAZO_VENCIDO_QUEUE,
                EventTopics.MapasRuta.MAPA_RUTA_PLAZO_VENCIDO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables proyectosMapaRutaFechaFinCambiadaDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                MAPA_RUTA_FECHA_FIN_CAMBIADA_QUEUE,
                EventTopics.MapasRuta.MAPA_RUTA_FECHA_FIN_CAMBIADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
