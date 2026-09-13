package com.arquisoft.proyectos.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class ProyectosQueues {

    private ProyectosQueues() {}

    public static final String PREFIJO = "proyectos" + RabbitMQConfig.SEPARADOR_COLA;
}
