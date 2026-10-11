package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.ConsultarObservacionesEvaluacionEstudianteInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper.ConsultarObservacionesEvaluacionEstudianteMapper;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionEstudianteQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesEvaluacionEstudianteInteractorImpl
        implements ConsultarObservacionesEvaluacionEstudianteInteractor {

    private final ConsultarObservacionesEvaluacionEstudianteUseCase consultarObservacionesEvaluacionEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<ObservacionEvaluacionReadModel> ejecutar(ConsultarObservacionesEvaluacionEstudianteQuery entrada) {
        var criteria = ConsultarObservacionesEvaluacionEstudianteMapper.toCriteria(entrada);
        return consultarObservacionesEvaluacionEstudianteUseCase.ejecutar(criteria);
    }
}
