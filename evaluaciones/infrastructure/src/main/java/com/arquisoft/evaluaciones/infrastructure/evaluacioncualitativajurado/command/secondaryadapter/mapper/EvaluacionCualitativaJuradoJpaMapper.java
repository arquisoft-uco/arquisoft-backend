package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.entity.EvaluacionCualitativaJuradoJpaEntity;

public final class EvaluacionCualitativaJuradoJpaMapper {

    private EvaluacionCualitativaJuradoJpaMapper() {}

    public static EvaluacionCualitativaJuradoJpaEntity toJpaEntity(EvaluacionCualitativaJuradoEntity entity) {
        return EvaluacionCualitativaJuradoJpaEntity.builder()
                .id(entity.id())
                .evaluacionJurado(entity.evaluacionJurado())
                .item(entity.item())
                .criterio(entity.criterio())
                .build();
    }

    public static EvaluacionCualitativaJuradoEntity toEntity(EvaluacionCualitativaJuradoJpaEntity jpaEntity) {
        return new EvaluacionCualitativaJuradoEntity(
                jpaEntity.getId(), jpaEntity.getEvaluacionJurado(), jpaEntity.getItem(), jpaEntity.getCriterio());
    }
}
