package com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.query.secondaryadapter.repository.EstadoFichaJpaQueryEntity;

public final class EstadoFichaQueryMapper {

    private EstadoFichaQueryMapper() {}

    public static EstadoFichaReadModel toReadModel(EstadoFichaJpaQueryEntity entity) {
        return new EstadoFichaReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
