package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.ConsultarEvaluacionAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.mapper.ConsultarEvaluacionAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.ConsultarEvaluacionAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionAsesorInteractorImpl implements ConsultarEvaluacionAsesorInteractor {

    private final ConsultarEvaluacionAsesorUseCase consultarEvaluacionAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public EvaluacionAsesorReadModel ejecutar(ConsultarEvaluacionAsesorQuery entrada) {
        var criteria = ConsultarEvaluacionAsesorMapper.toCriteria(entrada);
        return consultarEvaluacionAsesorUseCase.ejecutar(criteria);
    }
}
