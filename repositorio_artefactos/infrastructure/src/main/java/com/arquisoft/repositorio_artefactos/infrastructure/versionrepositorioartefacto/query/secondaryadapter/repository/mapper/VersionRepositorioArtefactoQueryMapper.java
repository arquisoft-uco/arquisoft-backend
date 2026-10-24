package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.VersionRepositorioArtefactoJpaQueryEntity;

public final class VersionRepositorioArtefactoQueryMapper {

    private VersionRepositorioArtefactoQueryMapper() {}

    public static VersionRepositorioArtefactoReadModel toReadModel(VersionRepositorioArtefactoJpaQueryEntity entity) {
        return new VersionRepositorioArtefactoReadModel(
                entity.getId(),
                entity.getNombreDocumento(),
                entity.getNumeroVersion(),
                entity.getFechaCreacion(),
                entity.getTipoContenido(),
                entity.getTamanoBytes(),
                entity.getNombreArchivo(),
                entity.getHashSha256());
    }
}
