package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasJuradoRealizadasInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.mapper.ConsultarEvaluacionesCuantitativasJuradoRealizadasMapper;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.usecase.ConsultarEvaluacionesCuantitativasJuradoRealizadasUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCuantitativasJuradoRealizadasInteractorImpl
        implements ConsultarEvaluacionesCuantitativasJuradoRealizadasInteractor {

    private final ConsultarEvaluacionesCuantitativasJuradoRealizadasUseCase useCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EvaluacionCuantitativaJuradoReadModel> ejecutar(ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery entrada) {
        var criteria = ConsultarEvaluacionesCuantitativasJuradoRealizadasMapper.toCriteria(entrada);
        return useCase.ejecutar(criteria);
    }
}
