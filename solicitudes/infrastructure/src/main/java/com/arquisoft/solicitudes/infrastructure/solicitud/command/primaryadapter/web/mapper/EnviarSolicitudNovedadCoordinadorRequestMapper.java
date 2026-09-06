package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudNovedadCoordinadorRequestDTO;

public final class EnviarSolicitudNovedadCoordinadorRequestMapper {

    private EnviarSolicitudNovedadCoordinadorRequestMapper() {}

    public static EnviarSolicitudNovedadCoordinadorCommand toCommand(
            EnviarSolicitudNovedadCoordinadorRequestDTO dto, String remitenteUsuarioId) {
        return EnviarSolicitudNovedadCoordinadorCommand.crear(
                remitenteUsuarioId, dto.destinatario(), dto.mensajeSolicitud());
    }
}
