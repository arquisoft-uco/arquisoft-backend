package com.arquisoft.artefactos.application.observacion.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.query.primaryport.interactor.ConsultarObservacionesRevisionEstudianteInteractor;
import com.arquisoft.artefactos.application.observacion.query.primaryport.mapper.ConsultarObservacionesRevisionEstudianteMapper;
import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionEstudianteQuery;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.application.observacion.query.usecase.ConsultarObservacionesRevisionEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesRevisionEstudianteInteractorImpl
        implements ConsultarObservacionesRevisionEstudianteInteractor {

    private final ConsultarObservacionesRevisionEstudianteUseCase consultarObservacionesRevisionEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public List<ObservacionReadModel> ejecutar(ConsultarObservacionesRevisionEstudianteQuery entrada) {
        var criteria = ConsultarObservacionesRevisionEstudianteMapper.toCriteria(entrada);
        return consultarObservacionesRevisionEstudianteUseCase.ejecutar(criteria);
    }
}
