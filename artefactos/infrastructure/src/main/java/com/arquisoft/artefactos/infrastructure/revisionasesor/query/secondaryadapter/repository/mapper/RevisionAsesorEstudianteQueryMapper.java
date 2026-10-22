package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.RevisionAsesorEstudianteJpaQueryEntity;

public final class RevisionAsesorEstudianteQueryMapper {

    private RevisionAsesorEstudianteQueryMapper() {}

    public static RevisionAsesorReadModel toReadModel(RevisionAsesorEstudianteJpaQueryEntity entity) {
        return new RevisionAsesorReadModel(
                entity.getId(),
                entity.getVersionArtefactoId(),
                entity.getArtefactoId(),
                entity.getVersion(),
                entity.getEstadoRevisionAsesorId(),
                entity.getEstadoRevisionAsesorNombre());
    }
}
