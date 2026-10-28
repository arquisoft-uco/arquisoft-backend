package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaAmpliacionPlazoRequestDTO;

import java.util.UUID;

public final class ModificarEstadoRespuestaAmpliacionPlazoRequestMapper {

    private ModificarEstadoRespuestaAmpliacionPlazoRequestMapper() {}

    public static ModificarEstadoRespuestaAmpliacionPlazoCommand toCommand(
            ModificarEstadoRespuestaAmpliacionPlazoRequestDTO dto,
            String solicitudId, UUID coordinadorUsuario) {
        return ModificarEstadoRespuestaAmpliacionPlazoCommand.crear(
                solicitudId, dto.nuevoEstado(), coordinadorUsuario);
    }
}
