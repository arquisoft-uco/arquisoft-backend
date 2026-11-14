package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.RemoverJuradosEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.mapper.RemoverJuradosEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.RemoverJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.RemoverJuradosEvaluacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverJuradosEvaluacionInteractorImpl implements RemoverJuradosEvaluacionInteractor {

    private final RemoverJuradosEvaluacionUseCase removerJuradosEvaluacionUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RemoverJuradosEvaluacionCommand command) {
        removerJuradosEvaluacionUseCase.ejecutar(RemoverJuradosEvaluacionMapper.toDomain(command));
    }
}
