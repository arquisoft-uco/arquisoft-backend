package com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EvaluacionJuradoDomain;

public final class EvaluacionJuradoMapper {

    private EvaluacionJuradoMapper() {}

    public static EvaluacionJuradoEntity toEntity(EvaluacionJuradoDomain domain) {
        return new EvaluacionJuradoEntity(
                domain.getId(), domain.getEvaluacion(), domain.getJurado(), domain.getEstado().getId());
    }
}
