package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoFichaPerfilCommand;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.dto.AgregarEstadoFichaPerfilRequestDTO;

import java.util.UUID;

public final class AgregarEstadoFichaPerfilRequestMapper {

    private AgregarEstadoFichaPerfilRequestMapper() {}

    public static AgregarEstadoFichaPerfilCommand toCommand(
            UUID fichaPerfil, AgregarEstadoFichaPerfilRequestDTO dto, UUID asesorFicha) {
        return AgregarEstadoFichaPerfilCommand.crear(fichaPerfil, dto.estadoFicha(), asesorFicha);
    }
}
