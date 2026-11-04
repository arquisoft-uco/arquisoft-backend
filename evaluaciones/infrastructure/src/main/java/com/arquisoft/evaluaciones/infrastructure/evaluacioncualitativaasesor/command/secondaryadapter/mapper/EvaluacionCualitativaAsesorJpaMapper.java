package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.entity.EvaluacionCualitativaAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.secondaryadapter.entity.EvaluacionCualitativaAsesorJpaEntity;

public final class EvaluacionCualitativaAsesorJpaMapper {

    private EvaluacionCualitativaAsesorJpaMapper() {}

    public static EvaluacionCualitativaAsesorJpaEntity toJpaEntity(EvaluacionCualitativaAsesorEntity entity) {
        return EvaluacionCualitativaAsesorJpaEntity.builder()
                .id(entity.id())
                .evaluacionAsesor(entity.evaluacionAsesor())
                .item(entity.item())
                .criterio(entity.criterio())
                .build();
    }
}
