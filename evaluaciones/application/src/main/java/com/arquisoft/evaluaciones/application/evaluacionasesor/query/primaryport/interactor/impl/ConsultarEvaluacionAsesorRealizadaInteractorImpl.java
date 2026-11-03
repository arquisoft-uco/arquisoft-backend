package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.interactor.ConsultarEvaluacionAsesorRealizadaInteractor;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.mapper.ConsultarEvaluacionAsesorRealizadaMapper;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorRealizadaQuery;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.ConsultarEvaluacionAsesorRealizadaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionAsesorRealizadaInteractorImpl implements ConsultarEvaluacionAsesorRealizadaInteractor {

    private final ConsultarEvaluacionAsesorRealizadaUseCase consultarEvaluacionAsesorRealizadaUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public EvaluacionAsesorRealizadaReadModel ejecutar(ConsultarEvaluacionAsesorRealizadaQuery entrada) {
        var criteria = ConsultarEvaluacionAsesorRealizadaMapper.toCriteria(entrada);
        return consultarEvaluacionAsesorRealizadaUseCase.ejecutar(criteria);
    }
}
