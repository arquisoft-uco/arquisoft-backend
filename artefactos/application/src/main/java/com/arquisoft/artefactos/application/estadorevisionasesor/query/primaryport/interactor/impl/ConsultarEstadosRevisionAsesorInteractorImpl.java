package com.arquisoft.artefactos.application.estadorevisionasesor.query.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.estadorevisionasesor.query.primaryport.interactor.ConsultarEstadosRevisionAsesorInteractor;
import com.arquisoft.artefactos.application.estadorevisionasesor.query.readmodel.EstadoRevisionAsesorReadModel;
import com.arquisoft.artefactos.application.estadorevisionasesor.query.usecase.ConsultarEstadosRevisionAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosRevisionAsesorInteractorImpl implements ConsultarEstadosRevisionAsesorInteractor {

    private final ConsultarEstadosRevisionAsesorUseCase consultarEstadosRevisionAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public List<EstadoRevisionAsesorReadModel> ejecutar() {
        return consultarEstadosRevisionAsesorUseCase.ejecutar();
    }
}
