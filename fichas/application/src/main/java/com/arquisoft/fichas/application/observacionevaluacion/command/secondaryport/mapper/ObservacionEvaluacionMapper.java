package com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.entity.ObservacionEvaluacionEntity;
import com.arquisoft.fichas.domain.observacionevaluacion.ObservacionEvaluacionDomain;

public final class ObservacionEvaluacionMapper {

    private ObservacionEvaluacionMapper() {}

    public static ObservacionEvaluacionEntity toEntity(ObservacionEvaluacionDomain observacionEvaluacion) {
        return new ObservacionEvaluacionEntity(
                observacionEvaluacion.getId(),
                observacionEvaluacion.getEvaluacionFichaPerfil(),
                observacionEvaluacion.getObservacion());
    }
}
