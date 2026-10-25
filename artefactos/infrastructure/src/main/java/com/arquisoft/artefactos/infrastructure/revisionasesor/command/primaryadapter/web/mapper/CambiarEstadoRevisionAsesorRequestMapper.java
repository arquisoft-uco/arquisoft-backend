package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.CambiarEstadoRevisionAsesorCommand;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.dto.CambiarEstadoRevisionAsesorRequestDTO;

import java.util.UUID;

public final class CambiarEstadoRevisionAsesorRequestMapper {

    private CambiarEstadoRevisionAsesorRequestMapper() {}

    public static CambiarEstadoRevisionAsesorCommand toCommand(
            CambiarEstadoRevisionAsesorRequestDTO dto, UUID revisionAsesor, UUID estudiante) {
        return CambiarEstadoRevisionAsesorCommand.crear(revisionAsesor, dto.estado(), estudiante);
    }
}
