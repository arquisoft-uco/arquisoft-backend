package com.arquisoft.evaluaciones.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class EvaluacionesQueues {

    private EvaluacionesQueues() {}

    public static final String PREFIJO = "evaluaciones" + RabbitMQConfig.SEPARADOR_COLA;
}
