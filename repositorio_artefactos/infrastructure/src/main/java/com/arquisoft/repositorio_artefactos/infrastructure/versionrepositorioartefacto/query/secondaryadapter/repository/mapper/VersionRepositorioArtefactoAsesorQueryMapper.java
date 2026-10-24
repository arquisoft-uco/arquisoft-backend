package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.RepositorioDeVersionReadModel;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoAsesorReadModel;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository.VersionRepositorioArtefactoAsesorJpaQueryEntity;

public final class VersionRepositorioArtefactoAsesorQueryMapper {

    private VersionRepositorioArtefactoAsesorQueryMapper() {}

    public static VersionRepositorioArtefactoAsesorReadModel toReadModel(
            VersionRepositorioArtefactoAsesorJpaQueryEntity entity) {
        return new VersionRepositorioArtefactoAsesorReadModel(
                entity.getId(),
                entity.getNombreDocumento(),
                Math.toIntExact(entity.getNumeroVersion()),
                entity.getFechaCreacion(),
                entity.getTipoContenido(),
                entity.getTamanoBytes(),
                entity.getNombreArchivo(),
                entity.getHashSha256(),
                new RepositorioDeVersionReadModel(
                        entity.getRepositorioId(),
                        entity.getNombreRepositorio(),
                        entity.getDescripcionRepositorio()));
    }
}
