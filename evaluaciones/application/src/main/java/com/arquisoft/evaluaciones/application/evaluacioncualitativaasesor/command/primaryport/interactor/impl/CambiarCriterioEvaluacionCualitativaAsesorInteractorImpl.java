package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.CambiarCriterioEvaluacionCualitativaAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.mapper.CambiarCriterioEvaluacionCualitativaAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.CambiarCriterioEvaluacionCualitativaAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.CambiarCriterioEvaluacionCualitativaAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarCriterioEvaluacionCualitativaAsesorInteractorImpl
        implements CambiarCriterioEvaluacionCualitativaAsesorInteractor {

    private final CambiarCriterioEvaluacionCualitativaAsesorUseCase cambiarCriterioEvaluacionCualitativaAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(CambiarCriterioEvaluacionCualitativaAsesorCommand command) {
        cambiarCriterioEvaluacionCualitativaAsesorUseCase.ejecutar(
                CambiarCriterioEvaluacionCualitativaAsesorMapper.toDomain(command));
    }
}
