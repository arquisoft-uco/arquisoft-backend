package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.interactor.CambiarPuntajeEvaluacionCuantitativaAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.mapper.CambiarPuntajeEvaluacionCuantitativaAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.CambiarPuntajeEvaluacionCuantitativaAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarPuntajeEvaluacionCuantitativaAsesorInteractorImpl
        implements CambiarPuntajeEvaluacionCuantitativaAsesorInteractor {

    private final CambiarPuntajeEvaluacionCuantitativaAsesorUseCase cambiarPuntajeEvaluacionCuantitativaAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(CambiarPuntajeEvaluacionCuantitativaAsesorCommand command) {
        cambiarPuntajeEvaluacionCuantitativaAsesorUseCase.ejecutar(
                CambiarPuntajeEvaluacionCuantitativaAsesorMapper.toDomain(command));
    }
}
