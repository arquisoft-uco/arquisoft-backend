package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.AgregarObservacionEvaluacionInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.mapper.AgregarObservacionEvaluacionMapper;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.AgregarObservacionEvaluacionCommand;
import com.arquisoft.fichas.application.observacionevaluacion.command.usecase.AgregarObservacionEvaluacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarObservacionEvaluacionInteractorImpl implements AgregarObservacionEvaluacionInteractor {

    private final AgregarObservacionEvaluacionUseCase agregarObservacionEvaluacionUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarObservacionEvaluacionCommand command) {
        return agregarObservacionEvaluacionUseCase.ejecutar(AgregarObservacionEvaluacionMapper.toDomain(command));
    }
}
