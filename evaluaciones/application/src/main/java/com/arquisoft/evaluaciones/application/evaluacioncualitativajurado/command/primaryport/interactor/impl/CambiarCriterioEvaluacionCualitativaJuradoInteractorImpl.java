package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.interactor.CambiarCriterioEvaluacionCualitativaJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.mapper.CambiarCriterioEvaluacionCualitativaJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.CambiarCriterioEvaluacionCualitativaJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.CambiarCriterioEvaluacionCualitativaJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarCriterioEvaluacionCualitativaJuradoInteractorImpl
        implements CambiarCriterioEvaluacionCualitativaJuradoInteractor {

    private final CambiarCriterioEvaluacionCualitativaJuradoUseCase cambiarCriterioEvaluacionCualitativaJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(CambiarCriterioEvaluacionCualitativaJuradoCommand command) {
        cambiarCriterioEvaluacionCualitativaJuradoUseCase.ejecutar(
                CambiarCriterioEvaluacionCualitativaJuradoMapper.toDomain(command));
    }
}
