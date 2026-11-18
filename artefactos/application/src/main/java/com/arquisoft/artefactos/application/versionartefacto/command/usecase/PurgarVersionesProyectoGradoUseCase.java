package com.arquisoft.artefactos.application.versionartefacto.command.usecase;

import com.arquisoft.artefactos.application.versionartefacto.command.result.PurgaVersionesProyectoGradoResult;
import com.arquisoft.artefactos.domain.versionartefacto.PurgaVersionesProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface PurgarVersionesProyectoGradoUseCase
        extends UseCase<PurgaVersionesProyectoGradoDomain, PurgaVersionesProyectoGradoResult> {
}
