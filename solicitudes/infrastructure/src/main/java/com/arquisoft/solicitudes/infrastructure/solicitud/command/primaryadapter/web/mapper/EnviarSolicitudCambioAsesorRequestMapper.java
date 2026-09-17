package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudCambioAsesorRequestDTO;

import java.util.UUID;

public final class EnviarSolicitudCambioAsesorRequestMapper {

    private EnviarSolicitudCambioAsesorRequestMapper() {}

    public static EnviarSolicitudCambioAsesorCommand toCommand(
            EnviarSolicitudCambioAsesorRequestDTO dto, UUID remitenteUsuario) {
        return EnviarSolicitudCambioAsesorCommand.crear(
                remitenteUsuario, dto.destinatario(), dto.mensajeSolicitud());
    }
}
