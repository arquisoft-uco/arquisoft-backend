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
public class MapasRutaProyectosQueueConfig {

    public static final String PROYECTO_GRADO_REGISTRADO_QUEUE =
            MapasRutaQueues.PREFIJO + EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO;

    public static final String PROYECTO_GRADO_ESTADO_CAMBIADO_QUEUE =
            MapasRutaQueues.PREFIJO + EventTopics.Proyectos.PROYECTO_GRADO_ESTADO_CAMBIADO;

    @Bean
    public Declarables mapasRutaProyectoGradoRegistradoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                PROYECTO_GRADO_REGISTRADO_QUEUE,
                EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }

    @Bean
    public Declarables mapasRutaProyectoGradoEstadoCambiadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                PROYECTO_GRADO_ESTADO_CAMBIADO_QUEUE,
                EventTopics.Proyectos.PROYECTO_GRADO_ESTADO_CAMBIADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
