package com.arquisoft.solicitudes.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class SolicitudesQueues {

    private SolicitudesQueues() {}

    public static final String PREFIJO = "solicitudes" + RabbitMQConfig.SEPARADOR_COLA;
}
