package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.RevisionAsesorAsesorJpaQueryEntity;

public final class RevisionAsesorAsesorQueryMapper {

    private RevisionAsesorAsesorQueryMapper() {}

    public static RevisionAsesorReadModel toReadModel(RevisionAsesorAsesorJpaQueryEntity entity) {
        return new RevisionAsesorReadModel(
                entity.getId(),
                entity.getVersionArtefactoId(),
                entity.getArtefactoId(),
                entity.getVersion(),
                entity.getEstadoRevisionAsesorId(),
                entity.getEstadoRevisionAsesorNombre());
    }
}
