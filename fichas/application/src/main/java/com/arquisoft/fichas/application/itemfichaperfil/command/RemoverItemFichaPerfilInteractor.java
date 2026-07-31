package com.arquisoft.fichas.application.itemfichaperfil.command;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.RemoverItemFichaPerfilInputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilInteractor implements RemoverItemFichaPerfilInputPort {

    private final RemoverItemFichaPerfilUseCase removerItemFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(RemoverItemFichaPerfilCommand command) {
        removerItemFichaPerfilUseCase.ejecutar(command);
    }
}
