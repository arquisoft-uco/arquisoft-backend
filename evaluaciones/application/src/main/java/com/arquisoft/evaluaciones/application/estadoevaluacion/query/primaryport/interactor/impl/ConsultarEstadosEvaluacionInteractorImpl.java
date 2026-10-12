package com.arquisoft.evaluaciones.application.estadoevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.primaryport.interactor.ConsultarEstadosEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.usecase.ConsultarEstadosEvaluacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosEvaluacionInteractorImpl implements ConsultarEstadosEvaluacionInteractor {

    private final ConsultarEstadosEvaluacionUseCase consultarEstadosEvaluacionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EstadoEvaluacionReadModel> ejecutar() {
        return consultarEstadosEvaluacionUseCase.ejecutar();
    }
}
