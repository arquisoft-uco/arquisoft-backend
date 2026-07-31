package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.ModificarItemFichaPerfilInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarItemFichaPerfilInteractor implements ModificarItemFichaPerfilInputPort {

    private final ModificarItemFichaPerfilUseCase modificarItemFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ModificarItemFichaPerfilCommand command) {
        modificarItemFichaPerfilUseCase.ejecutar(command);
    }
}
