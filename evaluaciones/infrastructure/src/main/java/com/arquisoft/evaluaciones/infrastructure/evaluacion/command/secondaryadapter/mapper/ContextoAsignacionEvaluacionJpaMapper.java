package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ContextoAsignacionEvaluacionEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.projection.ContextoAsignacionEvaluacionProjection;

public final class ContextoAsignacionEvaluacionJpaMapper {

    private ContextoAsignacionEvaluacionJpaMapper() {}

    public static ContextoAsignacionEvaluacionEntity toEntity(ContextoAsignacionEvaluacionProjection proyeccion) {
        return new ContextoAsignacionEvaluacionEntity(proyeccion.getEvaluacion(), proyeccion.getEstado());
    }
}
