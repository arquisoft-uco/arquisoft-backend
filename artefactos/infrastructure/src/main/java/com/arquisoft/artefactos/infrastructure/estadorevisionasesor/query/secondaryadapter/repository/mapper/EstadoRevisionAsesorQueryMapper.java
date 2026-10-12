package com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.secondaryadapter.repository.EstadoRevisionAsesorJpaQueryEntity;

public final class EstadoRevisionAsesorQueryMapper {

    private EstadoRevisionAsesorQueryMapper() {}

    public static EstadoRevisionAsesorReadModel toReadModel(EstadoRevisionAsesorJpaQueryEntity entity) {
        return new EstadoRevisionAsesorReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
