package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ContextoAsignacionEvaluacionEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.ContextoAsignacionEvaluacionDomain;

public final class ContextoAsignacionEvaluacionMapper {

    private ContextoAsignacionEvaluacionMapper() {}

    public static ContextoAsignacionEvaluacionDomain toDomain(ContextoAsignacionEvaluacionEntity entity) {
        return ContextoAsignacionEvaluacionDomain.reconstruir(
                entity.evaluacion(), EstadoEvaluacion.desde(entity.estado()));
    }
}
