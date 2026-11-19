package com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.interactor.AsignarAsesorEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.mapper.AsignarAsesorEvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.model.AsignarAsesorEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.usecase.AsignarAsesorEvaluacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AsignarAsesorEvaluacionInteractorImpl implements AsignarAsesorEvaluacionInteractor {

    private final AsignarAsesorEvaluacionUseCase asignarAsesorEvaluacionUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(AsignarAsesorEvaluacionCommand command) {
        asignarAsesorEvaluacionUseCase.ejecutar(AsignarAsesorEvaluacionMapper.toDomain(command));
    }
}
