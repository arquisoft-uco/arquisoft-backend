package com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.RemoverRevisionItemInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper.RemoverRevisionItemMapper;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.RemoverRevisionItemCommand;
import com.arquisoft.fichas.application.revisionitem.command.usecase.RemoverRevisionItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverRevisionItemInteractorImpl implements RemoverRevisionItemInteractor {

    private final RemoverRevisionItemUseCase removerRevisionItemUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(RemoverRevisionItemCommand command) {
        removerRevisionItemUseCase.ejecutar(RemoverRevisionItemMapper.toDomain(command));
    }
}
