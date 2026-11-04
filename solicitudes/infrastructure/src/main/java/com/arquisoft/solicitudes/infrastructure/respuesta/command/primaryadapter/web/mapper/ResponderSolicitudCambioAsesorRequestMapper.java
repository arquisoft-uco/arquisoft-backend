package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudCambioAsesorRequestDTO;

import java.util.UUID;

public final class ResponderSolicitudCambioAsesorRequestMapper {

    private ResponderSolicitudCambioAsesorRequestMapper() {}

    public static ResponderSolicitudCambioAsesorCommand toCommand(
            ResponderSolicitudCambioAsesorRequestDTO dto,
            String solicitudId, UUID coordinadorUsuario) {
        return ResponderSolicitudCambioAsesorCommand.crear(
                solicitudId, dto.contenido(), coordinadorUsuario);
    }
}
