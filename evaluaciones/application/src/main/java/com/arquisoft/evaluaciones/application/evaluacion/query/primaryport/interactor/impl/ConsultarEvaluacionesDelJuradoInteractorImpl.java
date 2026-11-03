package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.ConsultarEvaluacionesDelJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper.ConsultarEvaluacionesDelJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesDelJuradoQuery;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.application.evaluacion.query.usecase.ConsultarEvaluacionesDelJuradoUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesDelJuradoInteractorImpl implements ConsultarEvaluacionesDelJuradoInteractor {

    private final ConsultarEvaluacionesDelJuradoUseCase consultarEvaluacionesDelJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public PaginatedResult<EvaluacionReadModel> ejecutar(ConsultarEvaluacionesDelJuradoQuery entrada) {
        var criteria = ConsultarEvaluacionesDelJuradoMapper.toCriteria(entrada);
        return consultarEvaluacionesDelJuradoUseCase.ejecutar(criteria);
    }
}
