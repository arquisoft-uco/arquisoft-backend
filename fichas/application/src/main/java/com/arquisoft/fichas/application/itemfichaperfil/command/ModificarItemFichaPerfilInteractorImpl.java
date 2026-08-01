package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.ModificarItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.ModificarItemFichaPerfilUseCase;
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
        modificarItemFichaPerfilUseCase.ejecutar(command);
    }
}
