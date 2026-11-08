package com.arquisoft.mapas_ruta.infrastructure.config;

import com.arquisoft.shared.amqp.RabbitMQConfig;

public final class MapasRutaQueues {

    private MapasRutaQueues() {}

    public static final String PREFIJO = "mapas_ruta" + RabbitMQConfig.SEPARADOR_COLA;
}
