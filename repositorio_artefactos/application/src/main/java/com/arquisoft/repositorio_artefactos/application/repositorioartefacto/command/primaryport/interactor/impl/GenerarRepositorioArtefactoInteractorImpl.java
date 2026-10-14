package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.GenerarRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.mapper.GenerarRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.GenerarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.GenerarRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GenerarRepositorioArtefactoInteractorImpl implements GenerarRepositorioArtefactoInteractor {

    private final GenerarRepositorioArtefactoUseCase generarRepositorioArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "repositorioArtefactosTransactionManager")
    public UUID ejecutar(GenerarRepositorioArtefactoCommand command) {
        return generarRepositorioArtefactoUseCase.ejecutar(GenerarRepositorioArtefactoMapper.toDomain(command));
    }
}
