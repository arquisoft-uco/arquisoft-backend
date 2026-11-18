package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.mapper.ConsultarEvaluacionesCuantitativasAsesorEstudianteMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.usecase.ConsultarEvaluacionesCuantitativasAsesorEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractorImpl
        implements ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractor {

    private final ConsultarEvaluacionesCuantitativasAsesorEstudianteUseCase
            consultarEvaluacionesCuantitativasAsesorEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EvaluacionCuantitativaAsesorReadModel> ejecutar(
            ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery entrada) {
        var criteria = ConsultarEvaluacionesCuantitativasAsesorEstudianteMapper.toCriteria(entrada);
        return consultarEvaluacionesCuantitativasAsesorEstudianteUseCase.ejecutar(criteria);
    }
}
