package com.arquisoft.fichas.infrastructure.estadorevision.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;
import com.arquisoft.fichas.infrastructure.estadorevision.query.secondaryadapter.repository.EstadoRevisionJpaQueryEntity;

public final class EstadoRevisionQueryMapper {

    private EstadoRevisionQueryMapper() {}

    public static EstadoRevisionReadModel toReadModel(EstadoRevisionJpaQueryEntity entity) {
        return new EstadoRevisionReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
