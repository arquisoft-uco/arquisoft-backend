package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository.RevisionItemJpaQueryEntity;

public final class RevisionItemQueryMapper {

    private RevisionItemQueryMapper() {}

    public static RevisionItemReadModel toReadModel(RevisionItemJpaQueryEntity entity) {
        return new RevisionItemReadModel(
                entity.getId(),
                entity.getItemId(),
                entity.getEstadoRevisionId(),
                entity.getEstadoRevisionNombre(),
                entity.getFechaCreacion());
    }
}
