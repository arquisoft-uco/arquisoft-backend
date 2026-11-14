package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.ConsultarEvaluacionesAsignadasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.mapper.ConsultarEvaluacionesAsignadasAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionesAsignadasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.ConsultarEvaluacionesAsignadasAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesAsignadasAsesorInteractorImpl
        implements ConsultarEvaluacionesAsignadasAsesorInteractor {

    private final ConsultarEvaluacionesAsignadasAsesorUseCase consultarEvaluacionesAsignadasAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public PaginatedResult<EvaluacionAsignadaAsesorReadModel> ejecutar(
            ConsultarEvaluacionesAsignadasAsesorQuery entrada) {
        var criteria = ConsultarEvaluacionesAsignadasAsesorMapper.toCriteria(entrada);
        return consultarEvaluacionesAsignadasAsesorUseCase.ejecutar(criteria);
    }
}
