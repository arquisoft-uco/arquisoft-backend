package com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;
import com.arquisoft.artefactos.infrastructure.observacion.command.secondaryadapter.entity.ObservacionJpaEntity;

public final class ObservacionJpaMapper {

    private ObservacionJpaMapper() {}

    public static ObservacionJpaEntity toJpaEntity(ObservacionEntity entity) {
        return ObservacionJpaEntity.builder()
                .id(entity.id())
                .revisionAsesor(entity.revisionAsesor())
                .descripcion(entity.descripcion())
                .estado(entity.estado())
                .build();
    }

    public static ObservacionEntity toEntity(ObservacionJpaEntity jpaEntity) {
        return new ObservacionEntity(
                jpaEntity.getId(),
                jpaEntity.getRevisionAsesor(),
                jpaEntity.getDescripcion(),
                jpaEntity.getEstado());
    }
}
