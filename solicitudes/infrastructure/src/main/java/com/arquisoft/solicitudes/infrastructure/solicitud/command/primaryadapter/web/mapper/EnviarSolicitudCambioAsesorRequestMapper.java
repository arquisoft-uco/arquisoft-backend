package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudCambioAsesorRequestDTO;

public final class EnviarSolicitudCambioAsesorRequestMapper {

    private EnviarSolicitudCambioAsesorRequestMapper() {}

    public static EnviarSolicitudCambioAsesorCommand toCommand(
            EnviarSolicitudCambioAsesorRequestDTO dto, String remitenteUsuarioId) {
        return EnviarSolicitudCambioAsesorCommand.crear(
                remitenteUsuarioId, dto.destinatario(), dto.mensajeSolicitud());
    }
}
