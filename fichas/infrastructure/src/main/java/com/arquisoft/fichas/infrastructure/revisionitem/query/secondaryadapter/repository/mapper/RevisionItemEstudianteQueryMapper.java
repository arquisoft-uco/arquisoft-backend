package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository.RevisionItemEstudianteJpaQueryEntity;

public final class RevisionItemEstudianteQueryMapper {

    private RevisionItemEstudianteQueryMapper() {}

    public static RevisionItemReadModel toReadModel(RevisionItemEstudianteJpaQueryEntity entity) {
        return new RevisionItemReadModel(
                entity.getId(),
                entity.getItemId(),
                entity.getEstadoRevisionId(),
                entity.getEstadoRevisionNombre(),
                entity.getFechaCreacion());
    }
}
