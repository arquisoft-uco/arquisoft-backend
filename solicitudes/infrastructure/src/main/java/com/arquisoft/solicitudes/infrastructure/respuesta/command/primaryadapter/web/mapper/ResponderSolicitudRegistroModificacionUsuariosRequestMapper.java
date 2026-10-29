package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudRegistroModificacionUsuariosRequestDTO;

import java.util.UUID;

public final class ResponderSolicitudRegistroModificacionUsuariosRequestMapper {

    private ResponderSolicitudRegistroModificacionUsuariosRequestMapper() {}

    public static ResponderSolicitudRegistroModificacionUsuariosCommand toCommand(
            ResponderSolicitudRegistroModificacionUsuariosRequestDTO dto,
            UUID solicitudId, UUID administradorUsuario) {
        return ResponderSolicitudRegistroModificacionUsuariosCommand.crear(
                solicitudId, dto.contenido(), administradorUsuario);
    }
}
