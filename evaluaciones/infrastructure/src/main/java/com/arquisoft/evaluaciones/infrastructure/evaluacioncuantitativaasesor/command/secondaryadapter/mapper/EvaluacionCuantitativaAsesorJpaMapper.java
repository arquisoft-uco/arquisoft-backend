package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.entity.EvaluacionCuantitativaAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.secondaryadapter.entity.EvaluacionCuantitativaAsesorJpaEntity;

public final class EvaluacionCuantitativaAsesorJpaMapper {

    private EvaluacionCuantitativaAsesorJpaMapper() {}

    public static EvaluacionCuantitativaAsesorJpaEntity toJpaEntity(EvaluacionCuantitativaAsesorEntity entity) {
        return EvaluacionCuantitativaAsesorJpaEntity.builder()
                .id(entity.id())
                .evaluacionAsesor(entity.evaluacionAsesor())
                .item(entity.item())
                .puntaje(entity.puntaje())
                .build();
    }
}
