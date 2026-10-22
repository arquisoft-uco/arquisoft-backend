package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.ConsultarEvaluacionesAsignadasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.mapper.ConsultarEvaluacionesAsignadasJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.usecase.ConsultarEvaluacionesAsignadasJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesAsignadasJuradoInteractorImpl
        implements ConsultarEvaluacionesAsignadasJuradoInteractor {

    private final ConsultarEvaluacionesAsignadasJuradoUseCase consultarEvaluacionesAsignadasJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EvaluacionAsignadaJuradoReadModel> ejecutar(ConsultarEvaluacionesAsignadasJuradoQuery entrada) {
        var criteria = ConsultarEvaluacionesAsignadasJuradoMapper.toCriteria(entrada);
        return consultarEvaluacionesAsignadasJuradoUseCase.ejecutar(criteria);
    }
}
