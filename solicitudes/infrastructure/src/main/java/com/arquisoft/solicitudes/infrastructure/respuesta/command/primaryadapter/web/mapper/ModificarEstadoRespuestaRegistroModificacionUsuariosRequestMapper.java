package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaRegistroModificacionUsuariosRequestDTO;

import java.util.UUID;

public final class ModificarEstadoRespuestaRegistroModificacionUsuariosRequestMapper {

    private ModificarEstadoRespuestaRegistroModificacionUsuariosRequestMapper() {}

    public static ModificarEstadoRespuestaRegistroModificacionUsuariosCommand toCommand(
            ModificarEstadoRespuestaRegistroModificacionUsuariosRequestDTO dto,
            UUID solicitudId, UUID administradorUsuario) {
        return ModificarEstadoRespuestaRegistroModificacionUsuariosCommand.crear(
                solicitudId, dto.nuevoEstado(), administradorUsuario);
    }
}
