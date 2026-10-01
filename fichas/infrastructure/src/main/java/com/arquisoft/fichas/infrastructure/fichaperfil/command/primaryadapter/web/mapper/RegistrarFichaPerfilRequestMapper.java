package com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web.dto.RegistrarFichaPerfilRequestDTO;

public final class RegistrarFichaPerfilRequestMapper {

    private RegistrarFichaPerfilRequestMapper() {}

    public static RegistrarFichaPerfilCommand toCommand(RegistrarFichaPerfilRequestDTO dto) {
        return RegistrarFichaPerfilCommand.crear(
                dto.tituloProyecto(), dto.asesorFicha(), dto.estudiantes());
    }
}
