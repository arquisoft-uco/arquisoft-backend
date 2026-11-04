package com.arquisoft.artefactos.application.versionartefacto.command.usecase;

import com.arquisoft.artefactos.application.versionartefacto.command.result.CargaVersionArtefactoResult;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface SolicitarCargaVersionArtefactoUseCase
        extends UseCase<SolicitudCargaVersionArtefactoDomain, CargaVersionArtefactoResult> {
}
