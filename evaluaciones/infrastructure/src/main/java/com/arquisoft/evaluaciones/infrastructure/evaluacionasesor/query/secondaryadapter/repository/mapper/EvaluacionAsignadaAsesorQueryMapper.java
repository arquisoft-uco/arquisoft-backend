package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository.EvaluacionAsignadaAsesorJpaQueryEntity;

public final class EvaluacionAsignadaAsesorQueryMapper {

    private EvaluacionAsignadaAsesorQueryMapper() {}

    public static EvaluacionAsignadaAsesorReadModel toReadModel(EvaluacionAsignadaAsesorJpaQueryEntity entity) {
        return new EvaluacionAsignadaAsesorReadModel(
                entity.getId(),
                new EvaluacionAsignadaAsesorReadModel.Evaluacion(
                        entity.getEvaluacionId(),
                        new EvaluacionAsignadaAsesorReadModel.Estado(
                                entity.getEstadoId(), entity.getEstadoNombre())),
                new EvaluacionAsignadaAsesorReadModel.Entregable(
                        entity.getEntregableId(),
                        entity.getEntregableProyecto(),
                        entity.getEntregableVersion()),
                entity.isTieneEvaluacionesCualitativas(),
                entity.isTieneEvaluacionesCuantitativas());
    }
}
