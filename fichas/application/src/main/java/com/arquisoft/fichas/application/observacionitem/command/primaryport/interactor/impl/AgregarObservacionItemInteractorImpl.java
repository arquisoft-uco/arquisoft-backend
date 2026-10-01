package com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.AgregarObservacionItemInteractor;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.mapper.AgregarObservacionItemMapper;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.AgregarObservacionItemCommand;
import com.arquisoft.fichas.application.observacionitem.command.usecase.AgregarObservacionItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarObservacionItemInteractorImpl implements AgregarObservacionItemInteractor {

    private final AgregarObservacionItemUseCase agregarObservacionItemUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarObservacionItemCommand command) {
        return agregarObservacionItemUseCase.ejecutar(AgregarObservacionItemMapper.toDomain(command));
    }
}
