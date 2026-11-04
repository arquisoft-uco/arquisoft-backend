package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.mapper;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaCambioAsesorCommand;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web.dto.ModificarEstadoRespuestaCambioAsesorRequestDTO;

import java.util.UUID;

public final class ModificarEstadoRespuestaCambioAsesorRequestMapper {

    private ModificarEstadoRespuestaCambioAsesorRequestMapper() {}

    public static ModificarEstadoRespuestaCambioAsesorCommand toCommand(
            ModificarEstadoRespuestaCambioAsesorRequestDTO dto,
            String solicitudId, UUID coordinadorUsuario) {
        return ModificarEstadoRespuestaCambioAsesorCommand.crear(
                solicitudId, dto.nuevoEstado(), coordinadorUsuario);
    }
}
