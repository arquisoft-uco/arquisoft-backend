package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.AgregarItemFichaPerfilCommand;
import com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web.dto.AgregarItemFichaPerfilRequestDTO;

import java.util.UUID;

public final class AgregarItemFichaPerfilRequestMapper {

    private AgregarItemFichaPerfilRequestMapper() {}

    public static AgregarItemFichaPerfilCommand toCommand(
            AgregarItemFichaPerfilRequestDTO dto, UUID fichaPerfil, UUID estudiante) {
        return AgregarItemFichaPerfilCommand.crear(
                fichaPerfil, dto.tipoItem(), dto.contenido(), estudiante);
    }
}
