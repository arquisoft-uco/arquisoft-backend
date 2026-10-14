package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.ConsultarObservacionesEvaluacionCoordinadorInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper.ConsultarObservacionesEvaluacionCoordinadorMapper;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionCoordinadorQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesEvaluacionCoordinadorInteractorImpl
        implements ConsultarObservacionesEvaluacionCoordinadorInteractor {

    private final ConsultarObservacionesEvaluacionCoordinadorUseCase consultarObservacionesEvaluacionCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<ObservacionEvaluacionReadModel> ejecutar(ConsultarObservacionesEvaluacionCoordinadorQuery entrada) {
        var criteria = ConsultarObservacionesEvaluacionCoordinadorMapper.toCriteria(entrada);
        return consultarObservacionesEvaluacionCoordinadorUseCase.ejecutar(criteria);
    }
}
