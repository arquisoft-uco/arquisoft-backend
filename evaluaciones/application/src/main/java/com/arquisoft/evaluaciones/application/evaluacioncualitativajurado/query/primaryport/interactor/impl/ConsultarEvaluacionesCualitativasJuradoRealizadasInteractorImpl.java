package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCualitativasJuradoRealizadasInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.mapper.ConsultarEvaluacionesCualitativasJuradoRealizadasMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoRealizadasQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.usecase.ConsultarEvaluacionesCualitativasJuradoRealizadasUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesCualitativasJuradoRealizadasInteractorImpl
        implements ConsultarEvaluacionesCualitativasJuradoRealizadasInteractor {

    private final ConsultarEvaluacionesCualitativasJuradoRealizadasUseCase useCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<EvaluacionCualitativaJuradoReadModel> ejecutar(ConsultarEvaluacionesCualitativasJuradoRealizadasQuery entrada) {
        var criteria = ConsultarEvaluacionesCualitativasJuradoRealizadasMapper.toCriteria(entrada);
        return useCase.ejecutar(criteria);
    }
}
