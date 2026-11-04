package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public final class VersionRepositorioArtefactoMapper {

    private VersionRepositorioArtefactoMapper() {}

    public static VersionRepositorioArtefactoDomain toDomain(VersionRepositorioArtefactoEntity entity) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                entity.id(),
                entity.nombreDocumento(),
                entity.versionDocumento(),
                entity.repositorioArtefacto(),
                entity.nombreRepositorio(),
                new VersionRepositorioArtefactoDomain.DatosArchivo(
                        entity.nombreArchivo(), entity.tipoContenido(), entity.tamanoBytes()));
    }

    public static VersionRepositorioArtefactoEntity toEntity(VersionRepositorioArtefactoDomain version) {
        return new VersionRepositorioArtefactoEntity(
                version.getId(),
                version.getNombreDocumento(),
                version.getVersionDocumento(),
                version.getRepositorioArtefacto(),
                version.getNombreRepositorio(),
                version.getNombreArchivo(),
                version.getTipoContenido(),
                version.getTamanoBytes());
    }
}
