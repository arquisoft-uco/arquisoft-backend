package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaNovedadCoordinadorRequestDTO;

import java.util.UUID;

public final class ModificarEstadoRespuestaNovedadCoordinadorRequestMapper {

    private ModificarEstadoRespuestaNovedadCoordinadorRequestMapper() {}

    public static ModificarEstadoRespuestaNovedadCoordinadorCommand toCommand(
            ModificarEstadoRespuestaNovedadCoordinadorRequestDTO dto,
            String solicitudId, UUID coordinadorUsuario) {
        return ModificarEstadoRespuestaNovedadCoordinadorCommand.crear(
                solicitudId, dto.nuevoEstado(), coordinadorUsuario);
    }
}
