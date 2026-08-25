package com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estadoevaluacion.command.secondaryport.entity.EstadoEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;

public final class EstadoEvaluacionJpaMapper {

    private EstadoEvaluacionJpaMapper() {}

    public static EstadoEvaluacionEntity toEntity(EstadoEvaluacionJpaEntity jpaEntity) {
        return new EstadoEvaluacionEntity(
                jpaEntity.getId(),
                jpaEntity.getNombre(),
                jpaEntity.getDescripcion());
    }

    public static EstadoEvaluacionJpaEntity toJpaEntity(EstadoEvaluacionEntity entity) {
        return EstadoEvaluacionJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .descripcion(entity.descripcion())
                .build();
    }

    public static EstadoEvaluacionJpaEntity toReferencia(String id) {
        return EstadoEvaluacionJpaEntity.builder().id(id).build();
    }
}
