package com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.interactor.GenerarEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.mapper.GenerarEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model.GenerarEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.GenerarEvaluacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GenerarEvaluacionInteractorImpl implements GenerarEvaluacionInteractor {

    private final GenerarEvaluacionUseCase generarEvaluacionUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public GeneracionEvaluacionResult ejecutar(GenerarEvaluacionCommand command) {
        return generarEvaluacionUseCase.ejecutar(GenerarEvaluacionMapper.toDomain(command));
    }
}
