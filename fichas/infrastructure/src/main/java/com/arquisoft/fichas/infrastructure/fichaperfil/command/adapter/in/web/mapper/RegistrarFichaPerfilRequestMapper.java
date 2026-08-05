package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web.dto.RegistrarFichaPerfilRequestDTO;

public final class RegistrarFichaPerfilRequestMapper {

    private RegistrarFichaPerfilRequestMapper() {}

    public static RegistrarFichaPerfilCommand toCommand(RegistrarFichaPerfilRequestDTO dto) {
        return RegistrarFichaPerfilCommand.crear(
                dto.tituloProyecto(), dto.asesorFicha(), dto.estudiantes());
    }
}
