package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudNovedadCoordinadorRequestDTO;

import java.util.UUID;

public final class EnviarSolicitudNovedadCoordinadorRequestMapper {

    private EnviarSolicitudNovedadCoordinadorRequestMapper() {}

    public static EnviarSolicitudNovedadCoordinadorCommand toCommand(
            EnviarSolicitudNovedadCoordinadorRequestDTO dto, UUID remitenteUsuario) {
        return EnviarSolicitudNovedadCoordinadorCommand.crear(
                remitenteUsuario, dto.destinatario(), dto.mensajeSolicitud());
    }
}
