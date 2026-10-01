package com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.AgregarRevisionItemInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper.AgregarRevisionItemMapper;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.AgregarRevisionItemCommand;
import com.arquisoft.fichas.application.revisionitem.command.usecase.AgregarRevisionItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarRevisionItemInteractorImpl implements AgregarRevisionItemInteractor {

    private final AgregarRevisionItemUseCase agregarRevisionItemUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarRevisionItemCommand command) {
        return agregarRevisionItemUseCase.ejecutar(AgregarRevisionItemMapper.toDomain(command));
    }
}
