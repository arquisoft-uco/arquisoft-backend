package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.OmitirEvaluacionesCuantitativasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.mapper.OmitirEvaluacionesCuantitativasAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.OmitirEvaluacionesCuantitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.OmitirEvaluacionesCuantitativasAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OmitirEvaluacionesCuantitativasAsesorInteractorImpl
        implements OmitirEvaluacionesCuantitativasAsesorInteractor {

    private final OmitirEvaluacionesCuantitativasAsesorUseCase omitirEvaluacionesCuantitativasAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(OmitirEvaluacionesCuantitativasAsesorCommand command) {
        omitirEvaluacionesCuantitativasAsesorUseCase.ejecutar(
                OmitirEvaluacionesCuantitativasAsesorMapper.toDomain(command));
    }
}
