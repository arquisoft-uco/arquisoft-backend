package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoAsesorObservacionCommand;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.dto.CambiarEstadoAsesorObservacionRequestDTO;

import java.util.UUID;

public final class CambiarEstadoAsesorObservacionRequestMapper {

    private CambiarEstadoAsesorObservacionRequestMapper() {}

    public static CambiarEstadoAsesorObservacionCommand toCommand(
            CambiarEstadoAsesorObservacionRequestDTO dto, UUID observacion, UUID asesor) {
        return CambiarEstadoAsesorObservacionCommand.crear(observacion, dto.estado(), asesor);
    }
}
