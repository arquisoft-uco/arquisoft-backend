package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.RegistrarEvaluacionesCuantitativasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.mapper.RegistrarEvaluacionesCuantitativasAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.RegistrarEvaluacionesCuantitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.RegistrarEvaluacionesCuantitativasAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrarEvaluacionesCuantitativasAsesorInteractorImpl
        implements RegistrarEvaluacionesCuantitativasAsesorInteractor {

    private final RegistrarEvaluacionesCuantitativasAsesorUseCase registrarEvaluacionesCuantitativasAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RegistrarEvaluacionesCuantitativasAsesorCommand command) {
        registrarEvaluacionesCuantitativasAsesorUseCase.ejecutar(
                RegistrarEvaluacionesCuantitativasAsesorMapper.toDomain(command));
    }
}
