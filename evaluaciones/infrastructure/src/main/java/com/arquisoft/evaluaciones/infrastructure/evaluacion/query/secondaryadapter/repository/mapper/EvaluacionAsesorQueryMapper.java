package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.EvaluacionAsesorJpaQueryEntity;

public final class EvaluacionAsesorQueryMapper {

    private EvaluacionAsesorQueryMapper() {}

    public static EvaluacionReadModel toReadModel(EvaluacionAsesorJpaQueryEntity entity) {
        return new EvaluacionReadModel(
                entity.getId(),
                new EvaluacionReadModel.Entregable(
                        entity.getEntregableId(), entity.getEntregableProyecto(), entity.getEntregableVersion()),
                new EvaluacionReadModel.Estado(entity.getEstadoId(), entity.getEstadoNombre()));
    }
}
