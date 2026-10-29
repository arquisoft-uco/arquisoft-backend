package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.AsignarArtefactoInteractor;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper.AsignarArtefactoMapper;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactoCommand;
import com.arquisoft.artefactos.application.artefacto.command.usecase.AsignarArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarArtefactoInteractorImpl implements AsignarArtefactoInteractor {

    private final AsignarArtefactoUseCase asignarArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public UUID ejecutar(AsignarArtefactoCommand command) {
        return asignarArtefactoUseCase.ejecutar(AsignarArtefactoMapper.toDomain(command));
    }
}
