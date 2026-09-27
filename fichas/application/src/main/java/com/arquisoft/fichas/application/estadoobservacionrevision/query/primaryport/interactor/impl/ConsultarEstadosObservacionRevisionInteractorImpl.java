package com.arquisoft.fichas.application.estadoobservacionrevision.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.primaryport.interactor.ConsultarEstadosObservacionRevisionInteractor;
import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;
import com.arquisoft.fichas.application.estadoobservacionrevision.query.usecase.ConsultarEstadosObservacionRevisionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosObservacionRevisionInteractorImpl implements ConsultarEstadosObservacionRevisionInteractor {

    private final ConsultarEstadosObservacionRevisionUseCase consultarEstadosObservacionRevisionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EstadoObservacionRevisionReadModel> ejecutar() {
        return consultarEstadosObservacionRevisionUseCase.ejecutar();
    }
}
