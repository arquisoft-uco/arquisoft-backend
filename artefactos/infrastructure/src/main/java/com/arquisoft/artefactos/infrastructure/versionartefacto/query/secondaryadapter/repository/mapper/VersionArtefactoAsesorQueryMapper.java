package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository.VersionArtefactoAsesorJpaQueryEntity;

public final class VersionArtefactoAsesorQueryMapper {

    private VersionArtefactoAsesorQueryMapper() {}

    public static VersionArtefactoReadModel toReadModel(VersionArtefactoAsesorJpaQueryEntity entity) {
        return new VersionArtefactoReadModel(
                entity.getId(),
                entity.getArtefactoId(),
                entity.getVersion(),
                entity.getFechaCreacion(),
                entity.getUriArchivo(),
                entity.getNombreProyecto(),
                entity.getNombreDocumento(),
                entity.getVersionDocumento(),
                entity.getNombreRepositorio(),
                entity.getNombreAsesor(),
                entity.getRevisionAsesorId(),
                entity.getEstadoRevisionId(),
                entity.getNombreEstadoRevision());
    }
}
