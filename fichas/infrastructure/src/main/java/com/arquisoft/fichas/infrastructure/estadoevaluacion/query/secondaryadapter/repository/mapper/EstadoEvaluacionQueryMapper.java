package com.arquisoft.fichas.infrastructure.estadoevaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.query.secondaryadapter.repository.EstadoEvaluacionJpaQueryEntity;

public final class EstadoEvaluacionQueryMapper {

    private EstadoEvaluacionQueryMapper() {}

    public static EstadoEvaluacionReadModel toReadModel(EstadoEvaluacionJpaQueryEntity entity) {
        return new EstadoEvaluacionReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
