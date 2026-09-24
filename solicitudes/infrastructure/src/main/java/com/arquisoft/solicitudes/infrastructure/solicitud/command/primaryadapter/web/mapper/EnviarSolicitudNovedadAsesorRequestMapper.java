package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudNovedadAsesorRequestDTO;

import java.util.UUID;

public final class EnviarSolicitudNovedadAsesorRequestMapper {

    private EnviarSolicitudNovedadAsesorRequestMapper() {}

    public static EnviarSolicitudNovedadAsesorCommand toCommand(
            EnviarSolicitudNovedadAsesorRequestDTO dto, UUID remitenteUsuario) {
        return EnviarSolicitudNovedadAsesorCommand.crear(
                remitenteUsuario, dto.destinatario(), dto.mensajeSolicitud());
    }
}
