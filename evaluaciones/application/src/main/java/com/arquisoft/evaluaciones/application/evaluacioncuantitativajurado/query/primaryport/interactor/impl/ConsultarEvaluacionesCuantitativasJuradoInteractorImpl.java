package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.mapper.ConsultarEvaluacionesCuantitativasJuradoEstudianteMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.ConsultarEvaluacionesCuantitativasJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCuantitativasJuradoInteractorImpl
        implements ConsultarEvaluacionesCuantitativasJuradoInteractor {

    private final ConsultarEvaluacionesCuantitativasJuradoUseCase consultarEvaluacionesCuantitativasJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EvaluacionCuantitativaJuradoReadModel> ejecutar(
            ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery entrada) {
        var criteria = ConsultarEvaluacionesCuantitativasJuradoEstudianteMapper.toCriteria(entrada);
        return consultarEvaluacionesCuantitativasJuradoUseCase.ejecutar(criteria);
    }
}
