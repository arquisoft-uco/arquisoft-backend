package com.arquisoft.artefactos.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estudiante.command.primaryport.interactor.RemoverEstudianteInteractor;
import com.arquisoft.artefactos.application.estudiante.command.primaryport.mapper.RemoverEstudianteMapper;
import com.arquisoft.artefactos.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.artefactos.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.artefactos.application.estudiante.command.usecase.RemoverEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteInteractorImpl implements RemoverEstudianteInteractor {

    private final RemoverEstudianteUseCase removerEstudianteUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public RemocionEstudianteResult ejecutar(RemoverEstudianteCommand command) {
        return removerEstudianteUseCase.ejecutar(RemoverEstudianteMapper.toDomain(command));
    }
}
