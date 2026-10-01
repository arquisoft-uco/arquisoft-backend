package com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.interactor.AgregarItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.mapper.AgregarItemFichaPerfilMapper;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.AgregarItemFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarItemFichaPerfilInteractorImpl implements AgregarItemFichaPerfilInteractor {

    private final AgregarItemFichaPerfilUseCase agregarItemFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarItemFichaPerfilCommand command) {
        return agregarItemFichaPerfilUseCase.ejecutar(AgregarItemFichaPerfilMapper.toDomain(command));
    }
}
