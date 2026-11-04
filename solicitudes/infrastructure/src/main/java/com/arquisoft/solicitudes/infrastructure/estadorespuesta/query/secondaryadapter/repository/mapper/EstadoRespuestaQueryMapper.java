package com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.secondaryadapter.repository.mapper;

import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;
import com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.secondaryadapter.repository.EstadoRespuestaJpaQueryEntity;

public final class EstadoRespuestaQueryMapper {

    private EstadoRespuestaQueryMapper() {}

    public static EstadoRespuestaReadModel toReadModel(EstadoRespuestaJpaQueryEntity entity) {
        return new EstadoRespuestaReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
