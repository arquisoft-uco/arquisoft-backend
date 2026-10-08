package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.ConsultarObservacionesEvaluacionAsesorInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper.ConsultarObservacionesEvaluacionAsesorMapper;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionAsesorQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesEvaluacionAsesorInteractorImpl
        implements ConsultarObservacionesEvaluacionAsesorInteractor {

    private final ConsultarObservacionesEvaluacionAsesorUseCase consultarObservacionesEvaluacionAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<ObservacionEvaluacionReadModel> ejecutar(ConsultarObservacionesEvaluacionAsesorQuery entrada) {
        var criteria = ConsultarObservacionesEvaluacionAsesorMapper.toCriteria(entrada);
        return consultarObservacionesEvaluacionAsesorUseCase.ejecutar(criteria);
    }
}
