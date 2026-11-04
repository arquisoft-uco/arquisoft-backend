package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaNovedadAsesorRequestDTO;

import java.util.UUID;

public final class ModificarEstadoRespuestaNovedadAsesorRequestMapper {

    private ModificarEstadoRespuestaNovedadAsesorRequestMapper() {}

    public static ModificarEstadoRespuestaNovedadAsesorCommand toCommand(
            ModificarEstadoRespuestaNovedadAsesorRequestDTO dto,
            String solicitudId, UUID asesorUsuario) {
        return ModificarEstadoRespuestaNovedadAsesorCommand.crear(
                solicitudId, dto.nuevoEstado(), asesorUsuario);
    }
}
