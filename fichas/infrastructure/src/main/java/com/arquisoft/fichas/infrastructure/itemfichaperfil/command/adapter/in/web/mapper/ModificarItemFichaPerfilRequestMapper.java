package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto.ModificarItemFichaPerfilRequestDTO;

import java.util.UUID;

public final class ModificarItemFichaPerfilRequestMapper {

    private ModificarItemFichaPerfilRequestMapper() {}

    public static ModificarItemFichaPerfilCommand toCommand(
            ModificarItemFichaPerfilRequestDTO dto, UUID item, UUID estudiante) {
        return ModificarItemFichaPerfilCommand.crear(item, dto.contenido(), estudiante);
    }
}
