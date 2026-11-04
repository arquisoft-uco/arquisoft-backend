package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.GenerarRepositorioArtefactoCommand;
import com.arquisoft.shared.interactor.Interactor;

import java.util.UUID;

public interface GenerarRepositorioArtefactoInteractor
        extends Interactor<GenerarRepositorioArtefactoCommand, UUID> {
}
