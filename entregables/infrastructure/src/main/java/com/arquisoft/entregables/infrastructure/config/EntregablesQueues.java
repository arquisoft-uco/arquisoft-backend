package com.arquisoft.entregables.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class EntregablesQueues {

    private EntregablesQueues() {}

    public static final String PREFIJO = "entregables" + RabbitMQConfig.SEPARADOR_COLA;
}
