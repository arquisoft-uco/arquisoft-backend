package com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.MarcarRevisionItemComoVisualizadaInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper.MarcarRevisionItemComoVisualizadaMapper;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.MarcarRevisionItemComoVisualizadaCommand;
import com.arquisoft.fichas.application.revisionitem.command.usecase.MarcarRevisionItemComoVisualizadaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MarcarRevisionItemComoVisualizadaInteractorImpl implements MarcarRevisionItemComoVisualizadaInteractor {

    private final MarcarRevisionItemComoVisualizadaUseCase marcarRevisionItemComoVisualizadaUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(MarcarRevisionItemComoVisualizadaCommand command) {
        marcarRevisionItemComoVisualizadaUseCase.ejecutar(MarcarRevisionItemComoVisualizadaMapper.toDomain(command));
    }
}
