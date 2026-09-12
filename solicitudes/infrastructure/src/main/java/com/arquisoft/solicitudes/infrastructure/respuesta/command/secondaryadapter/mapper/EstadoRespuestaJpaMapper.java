package com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.mapper;

import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.EstadoRespuestaJpaEntity;

public final class EstadoRespuestaJpaMapper {

    private EstadoRespuestaJpaMapper() {}

    public static EstadoRespuestaJpaEntity toReferencia(String id) {
        return EstadoRespuestaJpaEntity.builder().id(id).build();
    }
}
