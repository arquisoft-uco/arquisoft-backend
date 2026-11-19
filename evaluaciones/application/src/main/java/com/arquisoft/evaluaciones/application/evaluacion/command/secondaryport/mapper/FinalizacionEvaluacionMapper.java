package com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.FinalizacionEvaluacionEntity;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.FinalizacionEvaluacionDomain;

public final class FinalizacionEvaluacionMapper {

    private FinalizacionEvaluacionMapper() {}

    public static FinalizacionEvaluacionEntity toEntity(FinalizacionEvaluacionDomain domain) {
        return new FinalizacionEvaluacionEntity(
                domain.getEvaluacion(),
                EstadoEvaluacion.FINALIZADA.getId(),
                domain.getNotaFinal(),
                domain.getResultado().getId());
    }
}
