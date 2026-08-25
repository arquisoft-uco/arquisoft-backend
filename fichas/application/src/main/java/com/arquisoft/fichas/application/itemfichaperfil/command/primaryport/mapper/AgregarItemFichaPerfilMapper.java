package com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.domain.itemfichaperfil.AgregacionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;

public final class AgregarItemFichaPerfilMapper {

    private AgregarItemFichaPerfilMapper() {}

    public static AgregacionItemFichaPerfilDomain toDomain(AgregarItemFichaPerfilCommand command) {
        var item = ItemFichaPerfilDomain.crear(
                command.fichaPerfil(),
                command.tipoItem(),
                command.contenido());

        return AgregacionItemFichaPerfilDomain.crear(item, command.estudiante());
    }
}
