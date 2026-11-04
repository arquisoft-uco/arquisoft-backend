package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoEstudianteObservacionCommand;
import com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.dto.CambiarEstadoEstudianteObservacionRequestDTO;

import java.util.UUID;

public final class CambiarEstadoEstudianteObservacionRequestMapper {

    private CambiarEstadoEstudianteObservacionRequestMapper() {}

    public static CambiarEstadoEstudianteObservacionCommand toCommand(
            CambiarEstadoEstudianteObservacionRequestDTO dto, UUID observacion, UUID estudiante) {
        return CambiarEstadoEstudianteObservacionCommand.crear(observacion, dto.estado(), estudiante);
    }
}
