package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository.EvaluacionAsesorRealizadaJpaQueryEntity;

public final class EvaluacionAsesorRealizadaQueryMapper {

    private EvaluacionAsesorRealizadaQueryMapper() {}

    public static EvaluacionAsesorRealizadaReadModel toReadModel(EvaluacionAsesorRealizadaJpaQueryEntity entity) {
        return new EvaluacionAsesorRealizadaReadModel(
                entity.getId(),
                new EvaluacionAsesorRealizadaReadModel.Evaluacion(
                        entity.getEvaluacionId(),
                        new EvaluacionAsesorRealizadaReadModel.Entregable(
                                entity.getEntregableId(),
                                entity.getEntregableProyecto(),
                                entity.getEntregableVersion()),
                        new EvaluacionAsesorRealizadaReadModel.Estado(
                                entity.getEstadoId(), entity.getEstadoNombre())),
                new EvaluacionAsesorRealizadaReadModel.Asesor(
                        entity.getAsesorId(), entity.getAsesorNombre(), entity.getAsesorEmail()));
    }
}
