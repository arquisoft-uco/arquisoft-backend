package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.entity.ObservacionItemJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.entity.ObservacionItemJuradoJpaEntity;

public final class ObservacionItemJuradoJpaMapper {

    private ObservacionItemJuradoJpaMapper() {}

    public static ObservacionItemJuradoEntity toEntity(ObservacionItemJuradoJpaEntity jpaEntity) {
        return new ObservacionItemJuradoEntity(
                jpaEntity.getId(), jpaEntity.getEvaluacionCuantitativaJuradoId(), jpaEntity.getDescripcion());
    }

    public static ObservacionItemJuradoJpaEntity toJpaEntity(ObservacionItemJuradoEntity entity) {
        return ObservacionItemJuradoJpaEntity.builder()
                .id(entity.id())
                .evaluacionCuantitativaJuradoId(entity.evaluacionCuantitativaJurado())
                .descripcion(entity.descripcion())
                .build();
    }
}
