package com.arquisoft.fichas.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class FichasQueues {

    private FichasQueues() {}

    public static final String PREFIJO = "fichas" + RabbitMQConfig.SEPARADOR_COLA;
}
