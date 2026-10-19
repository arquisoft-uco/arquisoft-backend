package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.ConsultarEvaluacionesAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper.ConsultarEvaluacionesAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.usecase.ConsultarEvaluacionesAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesAsesorInteractorImpl implements ConsultarEvaluacionesAsesorInteractor {

    private final ConsultarEvaluacionesAsesorUseCase consultarEvaluacionesAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public PaginatedResult<EvaluacionReadModel> ejecutar(ConsultarEvaluacionesAsesorQuery entrada) {
        var criteria = ConsultarEvaluacionesAsesorMapper.toCriteria(entrada);
        return consultarEvaluacionesAsesorUseCase.ejecutar(criteria);
    }
}
