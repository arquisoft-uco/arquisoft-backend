package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.AvanceEvaluacionEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection.AvanceEvaluacionProjection;

public final class AvanceEvaluacionJpaMapper {

    private AvanceEvaluacionJpaMapper() {}

    public static AvanceEvaluacionEntity toEntity(AvanceEvaluacionProjection projection) {
        return new AvanceEvaluacionEntity(
                projection.getEvaluacion(),
                projection.getEstado(),
                projection.getEntregable(),
                projection.getProyecto(),
                projection.getVersion(),
                projection.getJuradosVigentes(),
                projection.getJuradosEvaluados(),
                projection.getAsesorCompleto());
    }
}
