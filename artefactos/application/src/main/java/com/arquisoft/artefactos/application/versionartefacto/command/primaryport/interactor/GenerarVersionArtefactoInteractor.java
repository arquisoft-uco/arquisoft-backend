package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.GenerarVersionArtefactoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.VersionArtefactoGeneradaResult;
import com.arquisoft.shared.interactor.Interactor;

public interface GenerarVersionArtefactoInteractor
        extends Interactor<GenerarVersionArtefactoCommand, VersionArtefactoGeneradaResult> {
}
