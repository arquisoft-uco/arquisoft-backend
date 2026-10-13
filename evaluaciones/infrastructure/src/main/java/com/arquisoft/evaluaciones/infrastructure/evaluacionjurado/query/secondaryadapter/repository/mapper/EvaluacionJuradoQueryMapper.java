package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository.EvaluacionJuradoJpaQueryEntity;

public final class EvaluacionJuradoQueryMapper {

    private EvaluacionJuradoQueryMapper() {}

    public static EvaluacionJuradoReadModel toReadModel(EvaluacionJuradoJpaQueryEntity entity) {
        return new EvaluacionJuradoReadModel(
                entity.getId(),
                new EvaluacionJuradoReadModel.Jurado(
                        entity.getJuradoId(), entity.getJuradoNombre(), entity.getJuradoEmail()));
    }
}
