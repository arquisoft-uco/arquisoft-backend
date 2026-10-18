package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public final class VersionRepositorioArtefactoMapper {

    private VersionRepositorioArtefactoMapper() {}

    public static VersionRepositorioArtefactoEntity toEntity(VersionRepositorioArtefactoDomain version) {
        return new VersionRepositorioArtefactoEntity(
                version.getId(),
                version.getNombreDocumento(),
                version.getNumeroVersion(),
                version.getFechaCreacion(),
                version.getRepositorioArtefacto(),
                version.getUriArchivo(),
                version.getTipoContenido(),
                version.getTamanoBytes(),
                version.getNombreArchivo(),
                version.getHashSha256());
    }
}
