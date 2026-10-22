package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.ConsultarEvaluacionesEstudianteInteractor;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper.ConsultarEvaluacionesEstudianteMapper;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.usecase.ConsultarEvaluacionesEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesEstudianteInteractorImpl implements ConsultarEvaluacionesEstudianteInteractor {

    private final ConsultarEvaluacionesEstudianteUseCase consultarEvaluacionesEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public PaginatedResult<EvaluacionReadModel> ejecutar(ConsultarEvaluacionesEstudianteQuery entrada) {
        var criteria = ConsultarEvaluacionesEstudianteMapper.toCriteria(entrada);
        return consultarEvaluacionesEstudianteUseCase.ejecutar(criteria);
    }
}
