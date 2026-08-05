package com.arquisoft.fichas.application.itemfichaperfil.command.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.RemoverItemFichaPerfilDomain;

public final class RemoverItemFichaPerfilMapper {

    private RemoverItemFichaPerfilMapper() {}

    public static RemoverItemFichaPerfilDomain toDomain(RemoverItemFichaPerfilCommand command) {
        return RemoverItemFichaPerfilDomain.crear(command.item(), command.estudiante());
    }
}
