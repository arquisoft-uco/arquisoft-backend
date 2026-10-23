package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.interactor.RegistrarEvaluacionesCualitativasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.mapper.RegistrarEvaluacionesCualitativasAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.RegistrarEvaluacionesCualitativasAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionesCualitativasAsesorInteractorImpl
        implements RegistrarEvaluacionesCualitativasAsesorInteractor {

    private final RegistrarEvaluacionesCualitativasAsesorUseCase registrarEvaluacionesCualitativasAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RegistrarEvaluacionesCualitativasAsesorCommand command) {
        registrarEvaluacionesCualitativasAsesorUseCase.ejecutar(
                RegistrarEvaluacionesCualitativasAsesorMapper.toDomain(command));
    }
}
