package com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.RemoverObservacionItemInteractor;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper.RemoverObservacionItemMapper;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.RemoverObservacionItemCommand;
import com.arquisoft.fichas.application.observacionitem.command.usecase.RemoverObservacionItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverObservacionItemInteractorImpl implements RemoverObservacionItemInteractor {

    private final RemoverObservacionItemUseCase removerObservacionItemUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(RemoverObservacionItemCommand command) {
        removerObservacionItemUseCase.ejecutar(RemoverObservacionItemMapper.toDomain(command));
    }
}
