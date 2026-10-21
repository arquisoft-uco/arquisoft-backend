package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.interactor.ConsultarEvaluacionesCualitativasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.mapper.ConsultarEvaluacionesCualitativasAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.ConsultarEvaluacionesCualitativasAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCualitativasAsesorInteractorImpl
        implements ConsultarEvaluacionesCualitativasAsesorInteractor {

    private final ConsultarEvaluacionesCualitativasAsesorUseCase consultarEvaluacionesCualitativasAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EvaluacionCualitativaAsesorReadModel> ejecutar(ConsultarEvaluacionesCualitativasAsesorQuery entrada) {
        var criteria = ConsultarEvaluacionesCualitativasAsesorMapper.toCriteria(entrada);
        return consultarEvaluacionesCualitativasAsesorUseCase.ejecutar(criteria);
    }
}
