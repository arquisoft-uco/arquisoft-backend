package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.interactor.ConsultarEvaluacionesCualitativasAsesorEstudianteInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.mapper.ConsultarEvaluacionesCualitativasAsesorEstudianteMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.usecase.ConsultarEvaluacionesCualitativasAsesorEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCualitativasAsesorEstudianteInteractorImpl
        implements ConsultarEvaluacionesCualitativasAsesorEstudianteInteractor {

    private final ConsultarEvaluacionesCualitativasAsesorEstudianteUseCase
            consultarEvaluacionesCualitativasAsesorEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EvaluacionCualitativaAsesorEstudianteReadModel> ejecutar(ConsultarEvaluacionesCualitativasAsesorEstudianteQuery entrada) {
        var criteria = ConsultarEvaluacionesCualitativasAsesorEstudianteMapper.toCriteria(entrada);
        return consultarEvaluacionesCualitativasAsesorEstudianteUseCase.ejecutar(criteria);
    }
}
