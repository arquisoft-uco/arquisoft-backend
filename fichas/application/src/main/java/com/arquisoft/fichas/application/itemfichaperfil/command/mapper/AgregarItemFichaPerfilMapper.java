package com.arquisoft.fichas.application.itemfichaperfil.command.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.AgregarItemFichaPerfilDomain;

public final class AgregarItemFichaPerfilMapper {

    private AgregarItemFichaPerfilMapper() {}

    public static AgregarItemFichaPerfilDomain toDomain(AgregarItemFichaPerfilCommand command) {
        return AgregarItemFichaPerfilDomain.crear(
                command.fichaPerfil(),
                command.tipoItem(),
                command.contenido(),
                command.estudiante());
    }
}
