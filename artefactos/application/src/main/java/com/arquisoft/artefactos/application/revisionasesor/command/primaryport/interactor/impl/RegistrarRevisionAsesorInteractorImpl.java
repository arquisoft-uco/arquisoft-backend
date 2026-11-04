package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.RegistrarRevisionAsesorInteractor;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper.RegistrarRevisionAsesorMapper;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.RegistrarRevisionAsesorCommand;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.RegistrarRevisionAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarRevisionAsesorInteractorImpl implements RegistrarRevisionAsesorInteractor {

    private final RegistrarRevisionAsesorUseCase registrarRevisionAsesorUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public UUID ejecutar(RegistrarRevisionAsesorCommand command) {
        return registrarRevisionAsesorUseCase.ejecutar(RegistrarRevisionAsesorMapper.toDomain(command));
    }
}
