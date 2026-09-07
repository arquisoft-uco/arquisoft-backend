package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCualitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.mapper.ConsultarEvaluacionesCualitativasJuradoEstudianteMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.ConsultarEvaluacionesCualitativasJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCualitativasJuradoInteractorImpl
        implements ConsultarEvaluacionesCualitativasJuradoInteractor {

    private final ConsultarEvaluacionesCualitativasJuradoUseCase consultarEvaluacionesCualitativasJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EvaluacionCualitativaJuradoReadModel> ejecutar(
            ConsultarEvaluacionesCualitativasJuradoEstudianteQuery entrada) {
        var criteria = ConsultarEvaluacionesCualitativasJuradoEstudianteMapper.toCriteria(entrada);
        return consultarEvaluacionesCualitativasJuradoUseCase.ejecutar(criteria);
    }
}
