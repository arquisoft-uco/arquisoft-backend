package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository.VersionArtefactoEstudianteJpaQueryEntity;

public final class VersionArtefactoEstudianteQueryMapper {

    private VersionArtefactoEstudianteQueryMapper() {}

    public static VersionArtefactoReadModel toReadModel(VersionArtefactoEstudianteJpaQueryEntity entity) {
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
