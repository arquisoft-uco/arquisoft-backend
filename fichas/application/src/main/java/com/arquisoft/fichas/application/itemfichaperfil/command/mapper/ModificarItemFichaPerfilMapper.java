package com.arquisoft.fichas.application.itemfichaperfil.command.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ModificarItemFichaPerfilDomain;

public final class ModificarItemFichaPerfilMapper {

    private ModificarItemFichaPerfilMapper() {}

    public static ModificarItemFichaPerfilDomain toDomain(ModificarItemFichaPerfilCommand command) {
        return ModificarItemFichaPerfilDomain.crear(
                command.item(),
                command.contenido(),
                command.estudiante());
    }
}
