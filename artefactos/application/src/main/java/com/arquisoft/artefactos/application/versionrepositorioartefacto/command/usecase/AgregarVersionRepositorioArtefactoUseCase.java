package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.AgregacionVersionRepositorioArtefactoResult;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarVersionRepositorioArtefactoUseCase
        extends UseCase<VersionRepositorioArtefactoDomain, AgregacionVersionRepositorioArtefactoResult> {
}
