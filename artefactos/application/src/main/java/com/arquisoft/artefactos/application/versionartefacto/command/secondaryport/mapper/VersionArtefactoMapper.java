package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;

public final class VersionArtefactoMapper {

    private VersionArtefactoMapper() {}

    public static VersionArtefactoEntity toEntity(VersionArtefactoDomain version) {
        return new VersionArtefactoEntity(
                version.getId(),
                version.getArtefacto(),
                version.getNumeroVersion(),
                version.getFechaCreacion(),
                version.getUriArchivo());
    }
}
