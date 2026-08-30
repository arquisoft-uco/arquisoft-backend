package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudNovedadAsesorRequestDTO;

public final class EnviarSolicitudNovedadAsesorRequestMapper {

    private EnviarSolicitudNovedadAsesorRequestMapper() {}

    public static EnviarSolicitudNovedadAsesorCommand toCommand(
            EnviarSolicitudNovedadAsesorRequestDTO dto, String remitenteUsuarioId) {
        return EnviarSolicitudNovedadAsesorCommand.crear(
                remitenteUsuarioId, dto.destinatario(), dto.mensajeSolicitud());
    }
}
