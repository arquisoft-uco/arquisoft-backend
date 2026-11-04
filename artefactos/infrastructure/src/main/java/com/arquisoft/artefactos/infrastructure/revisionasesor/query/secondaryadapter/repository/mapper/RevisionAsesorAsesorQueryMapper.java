package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.RevisionAsesorAsesorJpaQueryEntity;

public final class RevisionAsesorAsesorQueryMapper {

    private RevisionAsesorAsesorQueryMapper() {}

    public static RevisionAsesorAsesorReadModel toReadModel(RevisionAsesorAsesorJpaQueryEntity entity) {
        return new RevisionAsesorAsesorReadModel(
                entity.getId(),
                entity.getVersionArtefactoId(),
                entity.getArtefactoId(),
                entity.getVersion(),
                entity.getEstadoRevisionAsesorId(),
                entity.getEstadoRevisionAsesorNombre(),
                entity.getFechaCreacion());
    }
}
