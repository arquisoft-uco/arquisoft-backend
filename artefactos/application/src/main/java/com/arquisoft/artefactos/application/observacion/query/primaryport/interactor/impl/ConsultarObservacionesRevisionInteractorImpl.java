package com.arquisoft.artefactos.application.observacion.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.query.primaryport.interactor.ConsultarObservacionesRevisionInteractor;
import com.arquisoft.artefactos.application.observacion.query.primaryport.mapper.ConsultarObservacionesRevisionMapper;
import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionQuery;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.application.observacion.query.usecase.ConsultarObservacionesRevisionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesRevisionInteractorImpl implements ConsultarObservacionesRevisionInteractor {

    private final ConsultarObservacionesRevisionUseCase consultarObservacionesRevisionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public List<ObservacionReadModel> ejecutar(ConsultarObservacionesRevisionQuery entrada) {
        var criteria = ConsultarObservacionesRevisionMapper.toCriteria(entrada);
        return consultarObservacionesRevisionUseCase.ejecutar(criteria);
    }
}
