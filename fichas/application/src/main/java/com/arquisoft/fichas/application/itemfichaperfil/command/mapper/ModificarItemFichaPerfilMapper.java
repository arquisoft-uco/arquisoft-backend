package com.arquisoft.fichas.application.itemfichaperfil.command.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.domain.itemfichaperfil.ModificacionItemFichaPerfilDomain;

public final class ModificarItemFichaPerfilMapper {

    private ModificarItemFichaPerfilMapper() {}

    public static ModificacionItemFichaPerfilDomain toDomain(ModificarItemFichaPerfilCommand command) {
        return ModificacionItemFichaPerfilDomain.crear(
                command.item(),
                command.contenido(),
                command.estudiante());
    }
}
