package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.mapper.ConsultarEvaluacionesCuantitativasAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase.ConsultarEvaluacionesCuantitativasAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCuantitativasAsesorInteractorImpl
        implements ConsultarEvaluacionesCuantitativasAsesorInteractor {

    private final ConsultarEvaluacionesCuantitativasAsesorUseCase consultarEvaluacionesCuantitativasAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EvaluacionCuantitativaAsesorReadModel> ejecutar(ConsultarEvaluacionesCuantitativasAsesorQuery entrada) {
        var criteria = ConsultarEvaluacionesCuantitativasAsesorMapper.toCriteria(entrada);
        return consultarEvaluacionesCuantitativasAsesorUseCase.ejecutar(criteria);
    }
}
