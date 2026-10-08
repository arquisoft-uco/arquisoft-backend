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
public class NotificacionesUsuariosQueueConfig {

    public static final String ESTADO_CAMBIADO_QUEUE =
            NotificacionesQueues.PREFIJO + EventTopics.Usuarios.USUARIO_ESTADO_CAMBIADO;

    @Bean
    public Declarables notificacionesUsuarioEstadoCambiadoDeclarables(
            @Qualifier("arquisoftEventsExchange") TopicExchange arquisoftEventsExchange,
            @Qualifier("arquisoftDeadLetterExchange") DirectExchange arquisoftDeadLetterExchange) {
        return ColaEvento.declarar(
                ESTADO_CAMBIADO_QUEUE,
                EventTopics.Usuarios.USUARIO_ESTADO_CAMBIADO,
                arquisoftEventsExchange,
                arquisoftDeadLetterExchange);
    }
}
