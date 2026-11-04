package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.CambiarEstadoRevisionAsesorInteractor;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper.CambiarEstadoRevisionAsesorMapper;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.CambiarEstadoRevisionAsesorCommand;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.CambiarEstadoRevisionAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarEstadoRevisionAsesorInteractorImpl implements CambiarEstadoRevisionAsesorInteractor {

    private final CambiarEstadoRevisionAsesorUseCase cambiarEstadoRevisionAsesorUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(CambiarEstadoRevisionAsesorCommand command) {
        cambiarEstadoRevisionAsesorUseCase.ejecutar(CambiarEstadoRevisionAsesorMapper.toDomain(command));
    }
}
