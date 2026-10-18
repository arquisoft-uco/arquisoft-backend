package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.VersionRepositorioArtefactoSubidaResult;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public final class VersionRepositorioArtefactoSubidaResultMapper {

    private VersionRepositorioArtefactoSubidaResultMapper() {}

    public static VersionRepositorioArtefactoSubidaResult toResult(VersionRepositorioArtefactoDomain version) {
        return new VersionRepositorioArtefactoSubidaResult(version.getId(), version.getNumeroVersion());
    }
}
