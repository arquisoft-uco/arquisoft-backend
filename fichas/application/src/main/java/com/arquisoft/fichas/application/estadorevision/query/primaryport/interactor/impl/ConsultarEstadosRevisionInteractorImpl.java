package com.arquisoft.fichas.application.estadorevision.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadorevision.query.primaryport.interactor.ConsultarEstadosRevisionInteractor;
import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;
import com.arquisoft.fichas.application.estadorevision.query.usecase.ConsultarEstadosRevisionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosRevisionInteractorImpl implements ConsultarEstadosRevisionInteractor {

    private final ConsultarEstadosRevisionUseCase consultarEstadosRevisionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EstadoRevisionReadModel> ejecutar() {
        return consultarEstadosRevisionUseCase.ejecutar();
    }
}
