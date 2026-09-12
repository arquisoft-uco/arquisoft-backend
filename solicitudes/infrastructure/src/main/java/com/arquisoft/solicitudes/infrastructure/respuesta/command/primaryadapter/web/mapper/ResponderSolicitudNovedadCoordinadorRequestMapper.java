package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudNovedadCoordinadorRequestDTO;

public final class ResponderSolicitudNovedadCoordinadorRequestMapper {

    private ResponderSolicitudNovedadCoordinadorRequestMapper() {}

    public static ResponderSolicitudNovedadCoordinadorCommand toCommand(
            ResponderSolicitudNovedadCoordinadorRequestDTO dto,
            String solicitudId, String coordinadorUsuarioId) {
        return ResponderSolicitudNovedadCoordinadorCommand.crear(
                solicitudId, dto.contenido(), coordinadorUsuarioId);
    }
}
