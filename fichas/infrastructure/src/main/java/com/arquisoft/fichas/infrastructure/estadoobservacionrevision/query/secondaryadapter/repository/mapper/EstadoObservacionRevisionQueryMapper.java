package com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.secondaryadapter.repository.EstadoObservacionRevisionJpaQueryEntity;

public final class EstadoObservacionRevisionQueryMapper {

    private EstadoObservacionRevisionQueryMapper() {}

    public static EstadoObservacionRevisionReadModel toReadModel(EstadoObservacionRevisionJpaQueryEntity entity) {
        return new EstadoObservacionRevisionReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
