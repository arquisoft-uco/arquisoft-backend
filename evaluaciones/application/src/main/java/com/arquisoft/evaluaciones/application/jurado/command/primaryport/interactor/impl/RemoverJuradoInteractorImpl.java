package com.arquisoft.evaluaciones.application.jurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.jurado.command.primaryport.interactor.RemoverJuradoInteractor;
import com.arquisoft.evaluaciones.application.jurado.command.primaryport.mapper.RemoverJuradoMapper;
import com.arquisoft.evaluaciones.application.jurado.command.primaryport.model.RemoverJuradoCommand;
import com.arquisoft.evaluaciones.application.jurado.command.result.RemocionJuradoResult;
import com.arquisoft.evaluaciones.application.jurado.command.usecase.RemoverJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverJuradoInteractorImpl implements RemoverJuradoInteractor {

    private final RemoverJuradoUseCase removerJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public RemocionJuradoResult ejecutar(RemoverJuradoCommand command) {
        return removerJuradoUseCase.ejecutar(RemoverJuradoMapper.toDomain(command));
    }
}
