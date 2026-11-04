package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository.EvaluacionAsesorDeEvaluacionJpaQueryEntity;

public final class EvaluacionAsesorDeEvaluacionQueryMapper {

    private EvaluacionAsesorDeEvaluacionQueryMapper() {}

    public static EvaluacionAsesorReadModel toReadModel(EvaluacionAsesorDeEvaluacionJpaQueryEntity entity) {
        return new EvaluacionAsesorReadModel(
                entity.getId(),
                new EvaluacionAsesorReadModel.Asesor(
                        entity.getAsesorId(), entity.getAsesorNombre(), entity.getAsesorEmail()));
    }
}
