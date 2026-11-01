package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.interactor.EliminarRevisionAsesorInteractor;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper.EliminarRevisionAsesorMapper;
import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EliminarRevisionAsesorCommand;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.EliminarRevisionAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarRevisionAsesorInteractorImpl implements EliminarRevisionAsesorInteractor {

    private final EliminarRevisionAsesorUseCase eliminarRevisionAsesorUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(EliminarRevisionAsesorCommand command) {
        eliminarRevisionAsesorUseCase.ejecutar(EliminarRevisionAsesorMapper.toDomain(command));
    }
}
