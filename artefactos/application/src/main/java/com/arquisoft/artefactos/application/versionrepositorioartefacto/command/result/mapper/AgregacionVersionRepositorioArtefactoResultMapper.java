package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.mapper;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.AgregacionVersionRepositorioArtefactoResult;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;

public final class AgregacionVersionRepositorioArtefactoResultMapper {

    private AgregacionVersionRepositorioArtefactoResultMapper() {}

    public static AgregacionVersionRepositorioArtefactoResult.Agregada toResultAgregada(
            VersionRepositorioArtefactoDomain version) {
        return new AgregacionVersionRepositorioArtefactoResult.Agregada(version.getId());
    }

    public static AgregacionVersionRepositorioArtefactoResult.Duplicada toResultDuplicada(
            VersionRepositorioArtefactoDomain version) {
        return new AgregacionVersionRepositorioArtefactoResult.Duplicada(version.getId());
    }
}
