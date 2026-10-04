package com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.RemoverEstudianteInteractor;
import com.arquisoft.fichas.application.estudiante.command.primaryport.mapper.RemoverEstudianteMapper;
import com.arquisoft.fichas.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.fichas.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.fichas.application.estudiante.command.usecase.RemoverEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteInteractorImpl implements RemoverEstudianteInteractor {

    private final RemoverEstudianteUseCase removerEstudianteUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public RemocionEstudianteResult ejecutar(RemoverEstudianteCommand command) {
        return removerEstudianteUseCase.ejecutar(RemoverEstudianteMapper.toDomain(command));
    }
}
