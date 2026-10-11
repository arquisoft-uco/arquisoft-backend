package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoAprobacionFichaPerfilCommand;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web.dto.AgregarEstadoAprobacionFichaPerfilRequestDTO;

import java.util.UUID;

public final class AgregarEstadoAprobacionFichaPerfilRequestMapper {

    private AgregarEstadoAprobacionFichaPerfilRequestMapper() {}

    public static AgregarEstadoAprobacionFichaPerfilCommand toCommand(
            String fichaPerfil, AgregarEstadoAprobacionFichaPerfilRequestDTO dto, UUID coordinador) {
        return AgregarEstadoAprobacionFichaPerfilCommand.crear(fichaPerfil, dto.acepta(), coordinador);
    }
}
