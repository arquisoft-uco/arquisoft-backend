package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.OmitirEvaluacionesCualitativasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.mapper.OmitirEvaluacionesCualitativasAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.OmitirEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.OmitirEvaluacionesCualitativasAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OmitirEvaluacionesCualitativasAsesorInteractorImpl
        implements OmitirEvaluacionesCualitativasAsesorInteractor {

    private final OmitirEvaluacionesCualitativasAsesorUseCase omitirEvaluacionesCualitativasAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(OmitirEvaluacionesCualitativasAsesorCommand command) {
        omitirEvaluacionesCualitativasAsesorUseCase.ejecutar(
                OmitirEvaluacionesCualitativasAsesorMapper.toDomain(command));
    }
}
