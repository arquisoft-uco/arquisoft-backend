package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.EnviarRevisionAsesorInteractor;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper.EnviarRevisionAsesorMapper;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EnviarRevisionAsesorCommand;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.EnviarRevisionAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EnviarRevisionAsesorInteractorImpl implements EnviarRevisionAsesorInteractor {

    private final EnviarRevisionAsesorUseCase enviarRevisionAsesorUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(EnviarRevisionAsesorCommand command) {
        enviarRevisionAsesorUseCase.ejecutar(EnviarRevisionAsesorMapper.toDomain(command));
    }
}
