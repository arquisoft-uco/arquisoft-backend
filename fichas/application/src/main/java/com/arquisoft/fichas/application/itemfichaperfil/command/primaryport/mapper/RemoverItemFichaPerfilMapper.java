package com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.domain.itemfichaperfil.RemocionItemFichaPerfilDomain;

public final class RemoverItemFichaPerfilMapper {

    private RemoverItemFichaPerfilMapper() {}

    public static RemocionItemFichaPerfilDomain toDomain(RemoverItemFichaPerfilCommand command) {
        return RemocionItemFichaPerfilDomain.crear(command.item(), command.estudiante());
    }
}
