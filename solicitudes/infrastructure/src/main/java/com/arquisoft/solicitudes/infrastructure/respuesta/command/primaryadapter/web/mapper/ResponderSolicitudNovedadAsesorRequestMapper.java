package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudNovedadAsesorRequestDTO;

import java.util.UUID;

public final class ResponderSolicitudNovedadAsesorRequestMapper {

    private ResponderSolicitudNovedadAsesorRequestMapper() {}

    public static ResponderSolicitudNovedadAsesorCommand toCommand(
            ResponderSolicitudNovedadAsesorRequestDTO dto,
            String solicitudId, UUID asesorUsuario) {
        return ResponderSolicitudNovedadAsesorCommand.crear(
                solicitudId, dto.contenido(), asesorUsuario);
    }
}
