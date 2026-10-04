package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.RemoverObservacionEvaluacionInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.mapper.RemoverObservacionEvaluacionMapper;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.RemoverObservacionEvaluacionCommand;
import com.arquisoft.fichas.application.observacionevaluacion.command.usecase.RemoverObservacionEvaluacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverObservacionEvaluacionInteractorImpl implements RemoverObservacionEvaluacionInteractor {

    private final RemoverObservacionEvaluacionUseCase removerObservacionEvaluacionUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(RemoverObservacionEvaluacionCommand command) {
        removerObservacionEvaluacionUseCase.ejecutar(RemoverObservacionEvaluacionMapper.toDomain(command));
    }
}
