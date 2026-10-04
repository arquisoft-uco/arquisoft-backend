package com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estadoobservacionrevision.command.secondaryport.entity.EstadoObservacionRevisionEntity;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.entity.EstadoObservacionRevisionJpaEntity;

public final class EstadoObservacionRevisionJpaMapper {

    private EstadoObservacionRevisionJpaMapper() {}

    public static EstadoObservacionRevisionEntity toEntity(EstadoObservacionRevisionJpaEntity jpaEntity) {
        return new EstadoObservacionRevisionEntity(
                jpaEntity.getId(),
                jpaEntity.getNombre(),
                jpaEntity.getDescripcion());
    }

    public static EstadoObservacionRevisionJpaEntity toJpaEntity(EstadoObservacionRevisionEntity entity) {
        return EstadoObservacionRevisionJpaEntity.builder()
                .id(entity.id())
                .nombre(entity.nombre())
                .descripcion(entity.descripcion())
                .build();
    }

    public static EstadoObservacionRevisionJpaEntity toReferencia(String id) {
        return EstadoObservacionRevisionJpaEntity.builder().id(id).build();
    }
}
