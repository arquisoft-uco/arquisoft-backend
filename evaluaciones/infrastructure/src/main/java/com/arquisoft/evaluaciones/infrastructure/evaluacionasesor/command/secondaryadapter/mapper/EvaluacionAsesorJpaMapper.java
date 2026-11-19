package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.entity.EvaluacionAsesorJpaEntity;

public final class EvaluacionAsesorJpaMapper {

    private EvaluacionAsesorJpaMapper() {}

    public static EvaluacionAsesorJpaEntity toJpaEntity(EvaluacionAsesorEntity entity) {
        return EvaluacionAsesorJpaEntity.builder()
                .id(entity.id())
                .evaluacion(entity.evaluacion())
                .asesor(entity.asesor())
                .build();
    }

    public static EvaluacionAsesorEntity toEntity(EvaluacionAsesorJpaEntity jpa) {
        return new EvaluacionAsesorEntity(jpa.getId(), jpa.getEvaluacion(), jpa.getAsesor());
    }
}
