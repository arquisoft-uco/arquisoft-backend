package com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estadorevision.command.secondaryport.entity.EstadoRevisionEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;

public final class EstadoRevisionJpaMapper {

    private EstadoRevisionJpaMapper() {}

    public static EstadoRevisionEntity toEntity(EstadoRevisionJpaEntity jpaEntity) {
        return new EstadoRevisionEntity(
                jpaEntity.getId(),
                jpaEntity.getNombre(),
                jpaEntity.getDescripcion());
    }

    public static EstadoRevisionJpaEntity toJpaEntity(EstadoRevisionEntity entity) {
        return EstadoRevisionJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .descripcion(entity.descripcion())
                .build();
    }

    public static EstadoRevisionJpaEntity toReferencia(String id) {
        return EstadoRevisionJpaEntity.builder().id(id).build();
    }
}
