package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudAmpliacionPlazoRequestDTO;

public final class EnviarSolicitudAmpliacionPlazoRequestMapper {

    private EnviarSolicitudAmpliacionPlazoRequestMapper() {}

    public static EnviarSolicitudAmpliacionPlazoCommand toCommand(
            EnviarSolicitudAmpliacionPlazoRequestDTO dto, String remitenteUsuarioId) {
        return EnviarSolicitudAmpliacionPlazoCommand.crear(
                remitenteUsuarioId, dto.destinatario(), dto.mensajeSolicitud());
    }
}
