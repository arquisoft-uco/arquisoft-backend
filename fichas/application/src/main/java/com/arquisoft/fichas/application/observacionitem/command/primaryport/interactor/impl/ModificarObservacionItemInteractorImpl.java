package com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.ModificarObservacionItemInteractor;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper.ModificarObservacionItemMapper;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ModificarObservacionItemCommand;
import com.arquisoft.fichas.application.observacionitem.command.usecase.ModificarObservacionItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarObservacionItemInteractorImpl implements ModificarObservacionItemInteractor {

    private final ModificarObservacionItemUseCase modificarObservacionItemUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ModificarObservacionItemCommand command) {
        modificarObservacionItemUseCase.ejecutar(ModificarObservacionItemMapper.toDomain(command));
    }
}
