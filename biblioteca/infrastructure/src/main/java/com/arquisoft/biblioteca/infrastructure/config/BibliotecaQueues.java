package com.arquisoft.biblioteca.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class BibliotecaQueues {

    private BibliotecaQueues() {}

    public static final String PREFIJO = "biblioteca" + RabbitMQConfig.SEPARADOR_COLA;
}
