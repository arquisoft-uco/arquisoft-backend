package com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.mapper.EstadoObservacionRevisionJpaMapper;
import com.arquisoft.fichas.infrastructure.observacionitem.command.secondaryadapter.entity.ObservacionItemJpaEntity;

public final class ObservacionItemJpaMapper {

    private ObservacionItemJpaMapper() {}

    public static ObservacionItemEntity toEntity(ObservacionItemJpaEntity jpaEntity) {
        return new ObservacionItemEntity(
                jpaEntity.getId(),
                jpaEntity.getRevisionItemId(),
                jpaEntity.getObservacion(),
                jpaEntity.getEstadoObservacionRevision().getId());
    }

    public static ObservacionItemJpaEntity toJpaEntity(ObservacionItemEntity entity) {
        return ObservacionItemJpaEntity.builder()
                .id(entity.id())
                .revisionItemId(entity.revisionItem())
                .observacion(entity.observacion())
                .estadoObservacionRevision(
                        EstadoObservacionRevisionJpaMapper.toReferencia(entity.estadoObservacionRevision()))
                .build();
    }
}
