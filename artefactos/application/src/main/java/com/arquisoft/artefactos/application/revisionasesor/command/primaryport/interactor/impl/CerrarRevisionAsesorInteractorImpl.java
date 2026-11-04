package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.CerrarRevisionAsesorInteractor;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper.CerrarRevisionAsesorMapper;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.CerrarRevisionAsesorCommand;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.CerrarRevisionAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CerrarRevisionAsesorInteractorImpl implements CerrarRevisionAsesorInteractor {

    private final CerrarRevisionAsesorUseCase cerrarRevisionAsesorUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(CerrarRevisionAsesorCommand command) {
        cerrarRevisionAsesorUseCase.ejecutar(CerrarRevisionAsesorMapper.toDomain(command));
    }
}
