package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.EvaluacionEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.entity.EvaluacionJpaEntity;

public final class EvaluacionJpaMapper {

    private EvaluacionJpaMapper() {}

    public static EvaluacionJpaEntity toJpaEntity(EvaluacionEntity entity) {
        return EvaluacionJpaEntity.builder()
                .id(entity.id())
                .entregable(entity.entregable())
                .estadoEvaluacion(entity.estado())
                .build();
    }
}
