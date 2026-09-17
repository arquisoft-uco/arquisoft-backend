package com.arquisoft.evaluaciones.application.estadoevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.primaryport.interactor.ConsultarEstadosEvaluacionEvaluacionesInteractor;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.application.estadoevaluacion.query.usecase.ConsultarEstadosEvaluacionEvaluacionesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosEvaluacionEvaluacionesInteractorImpl implements ConsultarEstadosEvaluacionEvaluacionesInteractor {

    private final ConsultarEstadosEvaluacionEvaluacionesUseCase consultarEstadosEvaluacionEvaluacionesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EstadoEvaluacionReadModel> ejecutar() {
        return consultarEstadosEvaluacionEvaluacionesUseCase.ejecutar();
    }
}
