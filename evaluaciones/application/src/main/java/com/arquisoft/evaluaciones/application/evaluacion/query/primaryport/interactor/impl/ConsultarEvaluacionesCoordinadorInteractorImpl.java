package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.ConsultarEvaluacionesCoordinadorInteractor;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper.ConsultarEvaluacionesCoordinadorMapper;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.usecase.ConsultarEvaluacionesCoordinadorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCoordinadorInteractorImpl implements ConsultarEvaluacionesCoordinadorInteractor {

    private final ConsultarEvaluacionesCoordinadorUseCase consultarEvaluacionesCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public PaginatedResult<EvaluacionReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarEvaluacionesCoordinadorMapper.toCriteria(entrada);
        return consultarEvaluacionesCoordinadorUseCase.ejecutar(criteria);
    }
}
