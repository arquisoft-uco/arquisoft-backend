package com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.ReabrirObservacionItemInteractor;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper.ReabrirObservacionItemMapper;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ReabrirObservacionItemCommand;
import com.arquisoft.fichas.application.observacionitem.command.usecase.ReabrirObservacionItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReabrirObservacionItemInteractorImpl implements ReabrirObservacionItemInteractor {

    private final ReabrirObservacionItemUseCase reabrirObservacionItemUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ReabrirObservacionItemCommand command) {
        reabrirObservacionItemUseCase.ejecutar(ReabrirObservacionItemMapper.toDomain(command));
    }
}
