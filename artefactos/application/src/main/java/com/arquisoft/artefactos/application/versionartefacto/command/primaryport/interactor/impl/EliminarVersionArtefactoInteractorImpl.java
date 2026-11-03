package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.EliminarVersionArtefactoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.mapper.EliminarVersionArtefactoMapper;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.EliminarVersionArtefactoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.EliminarVersionArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarVersionArtefactoInteractorImpl implements EliminarVersionArtefactoInteractor {

    private final EliminarVersionArtefactoUseCase eliminarVersionArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(EliminarVersionArtefactoCommand command) {
        eliminarVersionArtefactoUseCase.ejecutar(EliminarVersionArtefactoMapper.toDomain(command));
    }
}
