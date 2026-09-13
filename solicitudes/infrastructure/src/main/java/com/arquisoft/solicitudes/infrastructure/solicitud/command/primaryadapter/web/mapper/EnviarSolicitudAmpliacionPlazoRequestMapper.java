package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web.dto.EnviarSolicitudAmpliacionPlazoRequestDTO;

import java.util.UUID;

public final class EnviarSolicitudAmpliacionPlazoRequestMapper {

    private EnviarSolicitudAmpliacionPlazoRequestMapper() {}

    public static EnviarSolicitudAmpliacionPlazoCommand toCommand(
            EnviarSolicitudAmpliacionPlazoRequestDTO dto, UUID remitenteUsuario) {
        return EnviarSolicitudAmpliacionPlazoCommand.crear(
                remitenteUsuario, dto.destinatario(), dto.mensajeSolicitud());
    }
}
