package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudRegistroModificacionUsuariosRequestDTO;

import java.util.UUID;

public final class EnviarSolicitudRegistroModificacionUsuariosRequestMapper {

    private EnviarSolicitudRegistroModificacionUsuariosRequestMapper() {}

    public static EnviarSolicitudRegistroModificacionUsuariosCommand toCommand(
            EnviarSolicitudRegistroModificacionUsuariosRequestDTO dto, UUID remitenteUsuario) {
        return EnviarSolicitudRegistroModificacionUsuariosCommand.crear(
                remitenteUsuario, dto.destinatario(), dto.mensajeSolicitud());
    }
}
