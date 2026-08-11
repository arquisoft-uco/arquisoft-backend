package com.arquisoft.fichas.application.itemfichaperfil.command.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.domain.itemfichaperfil.AgregacionItemFichaPerfilDomain;

public final class AgregarItemFichaPerfilMapper {

    private AgregarItemFichaPerfilMapper() {}

    public static AgregacionItemFichaPerfilDomain toDomain(AgregarItemFichaPerfilCommand command) {
        return AgregacionItemFichaPerfilDomain.crear(
                command.fichaPerfil(),
                command.tipoItem(),
                command.contenido(),
                command.estudiante());
    }
}
