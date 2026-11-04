package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ResponderSolicitudAmpliacionPlazoRequestDTO;

import java.util.UUID;

public final class ResponderSolicitudAmpliacionPlazoRequestMapper {

    private ResponderSolicitudAmpliacionPlazoRequestMapper() {}

    public static ResponderSolicitudAmpliacionPlazoCommand toCommand(
            ResponderSolicitudAmpliacionPlazoRequestDTO dto,
            String solicitudId, UUID coordinadorUsuario) {
        return ResponderSolicitudAmpliacionPlazoCommand.crear(
                solicitudId, dto.contenido(), coordinadorUsuario);
    }
}
