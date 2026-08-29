package com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.ModificarRevisionItemInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper.ModificarRevisionItemMapper;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.ModificarRevisionItemCommand;
import com.arquisoft.fichas.application.revisionitem.command.usecase.ModificarRevisionItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarRevisionItemInteractorImpl implements ModificarRevisionItemInteractor {

    private final ModificarRevisionItemUseCase modificarRevisionItemUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ModificarRevisionItemCommand command) {
        modificarRevisionItemUseCase.ejecutar(ModificarRevisionItemMapper.toDomain(command));
    }
}
