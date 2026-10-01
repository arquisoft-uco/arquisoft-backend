package com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.interactor.ModificarItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.mapper.ModificarItemFichaPerfilMapper;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.ModificarItemFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilInteractorImpl implements ModificarItemFichaPerfilInteractor {

    private final ModificarItemFichaPerfilUseCase modificarItemFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ModificarItemFichaPerfilCommand command) {
        modificarItemFichaPerfilUseCase.ejecutar(ModificarItemFichaPerfilMapper.toDomain(command));
    }
}
