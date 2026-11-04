package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.GenerarVersionArtefactoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.mapper.GenerarVersionArtefactoMapper;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.GenerarVersionArtefactoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.VersionArtefactoGeneradaResult;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.GenerarVersionArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GenerarVersionArtefactoInteractorImpl implements GenerarVersionArtefactoInteractor {

    private final GenerarVersionArtefactoUseCase generarVersionUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public VersionArtefactoGeneradaResult ejecutar(GenerarVersionArtefactoCommand command) {
        return generarVersionUseCase.ejecutar(GenerarVersionArtefactoMapper.toDomain(command));
    }
}
