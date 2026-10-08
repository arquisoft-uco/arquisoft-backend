package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.interactor.ConsultarObservacionesEvaluacionRepresentanteInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper.ConsultarObservacionesEvaluacionRepresentanteMapper;
import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionRepresentanteQuery;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionRepresentanteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesEvaluacionRepresentanteInteractorImpl
        implements ConsultarObservacionesEvaluacionRepresentanteInteractor {

    private final ConsultarObservacionesEvaluacionRepresentanteUseCase consultarObservacionesEvaluacionRepresentanteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<ObservacionEvaluacionReadModel> ejecutar(ConsultarObservacionesEvaluacionRepresentanteQuery entrada) {
        var criteria = ConsultarObservacionesEvaluacionRepresentanteMapper.toCriteria(entrada);
        return consultarObservacionesEvaluacionRepresentanteUseCase.ejecutar(criteria);
    }
}
