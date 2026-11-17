package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.EvaluacionEntity;
import com.arquisoft.evaluaciones.domain.evaluacion.EvaluacionDomain;

public final class EvaluacionMapper {

    private EvaluacionMapper() {}

    public static EvaluacionEntity toEntity(EvaluacionDomain evaluacion) {
        return new EvaluacionEntity(evaluacion.getId(), evaluacion.getEntregable(), evaluacion.getEstado().getId());
    }
}
