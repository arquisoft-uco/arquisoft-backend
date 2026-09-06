package com.arquisoft.fichas.application.revisionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.domain.revisionitem.RevisionItemDomain;

public final class RevisionItemMapper {

    private RevisionItemMapper() {}

    public static RevisionItemEntity toEntity(RevisionItemDomain revisionItem) {
        return new RevisionItemEntity(
                revisionItem.getId(),
                revisionItem.getItem(),
                revisionItem.getEstadoRevision().getId(),
                revisionItem.getFechaCreacion());
    }
}
