package com.arquisoft.notificaciones.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class NotificacionesQueues {

    private NotificacionesQueues() {}

    public static final String PREFIJO = "notificaciones" + RabbitMQConfig.SEPARADOR_COLA;
}
