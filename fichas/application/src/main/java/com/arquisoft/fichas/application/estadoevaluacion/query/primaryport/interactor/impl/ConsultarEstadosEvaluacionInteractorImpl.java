package com.arquisoft.fichas.application.estadoevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadoevaluacion.query.primaryport.interactor.ConsultarEstadosEvaluacionInteractor;
import com.arquisoft.fichas.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.fichas.application.estadoevaluacion.query.usecase.ConsultarEstadosEvaluacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosEvaluacionInteractorImpl implements ConsultarEstadosEvaluacionInteractor {

    private final ConsultarEstadosEvaluacionUseCase consultarEstadosEvaluacionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EstadoEvaluacionReadModel> ejecutar() {
        return consultarEstadosEvaluacionUseCase.ejecutar();
    }
}
