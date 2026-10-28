package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository.EvaluacionAsignadaJuradoJpaQueryEntity;

public final class EvaluacionAsignadaJuradoQueryMapper {

    private EvaluacionAsignadaJuradoQueryMapper() {}

    public static EvaluacionAsignadaJuradoReadModel toReadModel(EvaluacionAsignadaJuradoJpaQueryEntity entity) {
        return new EvaluacionAsignadaJuradoReadModel(
                entity.getId(),
                new EvaluacionAsignadaJuradoReadModel.Estado(
                        entity.getEstadoJuradoId(), entity.getEstadoJuradoNombre()),
                new EvaluacionAsignadaJuradoReadModel.Evaluacion(
                        entity.getEvaluacionId(),
                        new EvaluacionAsignadaJuradoReadModel.Estado(
                                entity.getEstadoId(), entity.getEstadoNombre())),
                new EvaluacionAsignadaJuradoReadModel.Entregable(
                        entity.getEntregableId(),
                        entity.getEntregableProyecto(),
                        entity.getEntregableVersion()));
    }
}
