package com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.CerrarObservacionItemInteractor;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper.CerrarObservacionItemMapper;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.CerrarObservacionItemCommand;
import com.arquisoft.fichas.application.observacionitem.command.usecase.CerrarObservacionItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CerrarObservacionItemInteractorImpl implements CerrarObservacionItemInteractor {

    private final CerrarObservacionItemUseCase cerrarObservacionItemUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(CerrarObservacionItemCommand command) {
        cerrarObservacionItemUseCase.ejecutar(CerrarObservacionItemMapper.toDomain(command));
    }
}
