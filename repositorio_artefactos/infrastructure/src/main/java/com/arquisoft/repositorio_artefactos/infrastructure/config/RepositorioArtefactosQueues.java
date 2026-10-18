package com.arquisoft.repositorio_artefactos.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class RepositorioArtefactosQueues {

    private RepositorioArtefactosQueues() {}

    public static final String PREFIJO = "repositorio_artefactos" + RabbitMQConfig.SEPARADOR_COLA;
}
