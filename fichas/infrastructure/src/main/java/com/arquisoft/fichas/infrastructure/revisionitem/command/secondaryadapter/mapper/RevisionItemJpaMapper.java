package com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.mapper.EstadoRevisionJpaMapper;
import com.arquisoft.fichas.infrastructure.revisionitem.command.secondaryadapter.entity.RevisionItemJpaEntity;

public final class RevisionItemJpaMapper {

    private RevisionItemJpaMapper() {}

    public static RevisionItemEntity toEntity(RevisionItemJpaEntity jpaEntity) {
        return new RevisionItemEntity(
                jpaEntity.getId(),
                jpaEntity.getItemId(),
                jpaEntity.getEstadoRevision().getId(),
                jpaEntity.getFechaCreacion());
    }

    public static RevisionItemJpaEntity toJpaEntity(RevisionItemEntity entity) {
        return RevisionItemJpaEntity.builder()
                .id(entity.id())
                .itemId(entity.item())
                .estadoRevision(EstadoRevisionJpaMapper.toReferencia(entity.estadoRevision()))
                .fechaCreacion(entity.fechaCreacion())
                .build();
    }
}
