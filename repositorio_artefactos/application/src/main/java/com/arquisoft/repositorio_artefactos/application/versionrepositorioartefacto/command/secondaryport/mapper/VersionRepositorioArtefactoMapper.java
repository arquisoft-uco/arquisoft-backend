package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.UltimaVersionDocumento;

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

    public static UltimaVersionDocumento toUltimaVersion(VersionRepositorioArtefactoEntity entity) {
        return new UltimaVersionDocumento(entity.nombreDocumento(), entity.numeroVersion());
    }
}
