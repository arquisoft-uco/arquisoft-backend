package com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.command.secondaryadapter.entity.EstadoFichaJpaEntity;

public final class EstadoFichaJpaMapper {

    private EstadoFichaJpaMapper() {}

    public static EstadoFichaEntity toEntity(EstadoFichaJpaEntity jpaEntity) {
        return new EstadoFichaEntity(
                jpaEntity.getId(),
                jpaEntity.getNombre(),
                jpaEntity.getDescripcion());
    }

    public static EstadoFichaJpaEntity toJpaEntity(EstadoFichaEntity entity) {
        return EstadoFichaJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .descripcion(entity.descripcion())
                .build();
    }

    public static EstadoFichaJpaEntity toReferencia(String id) {
        return EstadoFichaJpaEntity.builder().id(id).build();
    }
}
