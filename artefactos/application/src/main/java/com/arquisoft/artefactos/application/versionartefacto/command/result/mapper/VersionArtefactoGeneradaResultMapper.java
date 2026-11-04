package com.arquisoft.artefactos.application.versionartefacto.command.result.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.result.VersionArtefactoGeneradaResult;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;

public final class VersionArtefactoGeneradaResultMapper {

    private VersionArtefactoGeneradaResultMapper() {}

    public static VersionArtefactoGeneradaResult toResult(VersionArtefactoDomain version) {
        return new VersionArtefactoGeneradaResult(version.getId(), version.getNumeroVersion());
    }
}
