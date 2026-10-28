package com.arquisoft.artefactos.application.versionartefacto.command.usecase;

import com.arquisoft.artefactos.application.versionartefacto.command.result.VersionArtefactoGeneradaResult;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface GenerarVersionArtefactoUseCase
        extends UseCase<GeneracionVersionArtefactoDomain, VersionArtefactoGeneradaResult> {
}
