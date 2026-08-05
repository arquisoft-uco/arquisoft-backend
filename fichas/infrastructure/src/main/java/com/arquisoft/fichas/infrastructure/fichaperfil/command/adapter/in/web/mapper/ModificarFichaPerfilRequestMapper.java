package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto.ModificarFichaPerfilRequestDTO;

import java.util.UUID;

public final class ModificarFichaPerfilRequestMapper {

    private ModificarFichaPerfilRequestMapper() {}

    public static ModificarFichaPerfilCommand toCommand(
            ModificarFichaPerfilRequestDTO dto, UUID fichaPerfil, UUID estudiante) {
        return ModificarFichaPerfilCommand.crear(fichaPerfil, estudiante, dto.tituloProyecto());
    }
}
