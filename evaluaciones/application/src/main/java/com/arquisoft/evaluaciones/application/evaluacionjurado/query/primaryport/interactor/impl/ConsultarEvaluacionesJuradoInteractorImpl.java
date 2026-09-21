package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.ConsultarEvaluacionesJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.mapper.ConsultarEvaluacionesJuradoEstudianteMapper;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.ConsultarEvaluacionesJuradoUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesJuradoInteractorImpl implements ConsultarEvaluacionesJuradoInteractor {

    private final ConsultarEvaluacionesJuradoUseCase consultarEvaluacionesJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public PaginatedResult<EvaluacionJuradoReadModel> ejecutar(ConsultarEvaluacionesJuradoEstudianteQuery entrada) {
        var criteria = ConsultarEvaluacionesJuradoEstudianteMapper.toCriteria(entrada);
        return consultarEvaluacionesJuradoUseCase.ejecutar(criteria);
    }
}
