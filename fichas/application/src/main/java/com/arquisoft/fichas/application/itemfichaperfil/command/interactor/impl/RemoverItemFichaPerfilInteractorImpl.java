package com.arquisoft.fichas.application.itemfichaperfil.command.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.interactor.RemoverItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.command.mapper.RemoverItemFichaPerfilMapper;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.usecase.RemoverItemFichaPerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverItemFichaPerfilInteractorImpl implements RemoverItemFichaPerfilInteractor {

    private final RemoverItemFichaPerfilUseCase removerItemFichaPerfilUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(RemoverItemFichaPerfilCommand command) {
        removerItemFichaPerfilUseCase.ejecutar(RemoverItemFichaPerfilMapper.toDomain(command));
    }
}
