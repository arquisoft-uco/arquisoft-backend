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
public class NotificacionesSolicitudesQueueConfig {

    // El nombre de la cola tiene que seguir siendo una expresion constante: @RabbitListener lo lee
    // como valor de anotacion (JLS 9.7.1), asi que no puede salir de una llamada a metodo.
    public static final String NOVEDAD_COORDINADOR_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ENVIADA;

    @Bean
    public Declarables notificacionesSolicitudNovedadCoordinadorDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                NOVEDAD_COORDINADOR_QUEUE,
                EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ENVIADA,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
