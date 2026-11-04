package com.arquisoft.artefactos.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class ArtefactosQueues {

    private ArtefactosQueues() {}

    public static final String PREFIJO = "artefactos" + RabbitMQConfig.SEPARADOR_COLA;
}
