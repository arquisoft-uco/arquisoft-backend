package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.entity.EvaluacionJuradoJpaEntity;

public final class EvaluacionJuradoJpaMapper {

    private EvaluacionJuradoJpaMapper() {}

    public static EvaluacionJuradoJpaEntity toJpaEntity(EvaluacionJuradoEntity entity) {
        return EvaluacionJuradoJpaEntity.builder()
                .id(entity.id())
                .evaluacion(entity.evaluacion())
                .jurado(entity.jurado())
                .build();
    }
}
