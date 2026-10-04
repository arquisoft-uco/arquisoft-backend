package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudNovedadCoordinadorRequestDTO;

import java.util.UUID;

public final class ResponderSolicitudNovedadCoordinadorRequestMapper {

    private ResponderSolicitudNovedadCoordinadorRequestMapper() {}

    public static ResponderSolicitudNovedadCoordinadorCommand toCommand(
            ResponderSolicitudNovedadCoordinadorRequestDTO dto,
            String solicitudId, UUID coordinadorUsuario) {
        return ResponderSolicitudNovedadCoordinadorCommand.crear(
                solicitudId, dto.contenido(), coordinadorUsuario);
    }
}
