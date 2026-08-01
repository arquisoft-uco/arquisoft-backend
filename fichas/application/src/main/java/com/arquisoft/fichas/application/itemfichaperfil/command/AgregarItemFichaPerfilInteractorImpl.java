package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.AgregarItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.AgregarItemFichaPerfilUseCase;
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
        return agregarItemFichaPerfilUseCase.ejecutar(command);
    }
}
