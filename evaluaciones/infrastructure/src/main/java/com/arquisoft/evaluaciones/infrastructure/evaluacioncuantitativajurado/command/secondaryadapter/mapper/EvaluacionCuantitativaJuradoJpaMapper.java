package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.entity.EvaluacionCuantitativaJuradoJpaEntity;

public final class EvaluacionCuantitativaJuradoJpaMapper {

    private EvaluacionCuantitativaJuradoJpaMapper() {}

    public static EvaluacionCuantitativaJuradoEntity toEntity(EvaluacionCuantitativaJuradoJpaEntity jpaEntity) {
        return new EvaluacionCuantitativaJuradoEntity(
                jpaEntity.getId(),
                jpaEntity.getEvaluacionJuradoId(),
                jpaEntity.getItemId(),
                jpaEntity.getPuntaje());
    }

    public static EvaluacionCuantitativaJuradoJpaEntity toJpaEntity(EvaluacionCuantitativaJuradoEntity entity) {
        return EvaluacionCuantitativaJuradoJpaEntity.builder()
                .id(entity.id())
                .evaluacionJuradoId(entity.evaluacionJurado())
                .itemId(entity.item())
                .puntaje(entity.puntaje())
                .build();
    }
}
