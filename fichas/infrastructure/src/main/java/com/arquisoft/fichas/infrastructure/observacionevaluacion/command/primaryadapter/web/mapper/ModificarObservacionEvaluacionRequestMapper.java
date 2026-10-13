package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.ModificarObservacionEvaluacionCommand;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web.dto.ModificarObservacionEvaluacionRequestDTO;

import java.util.UUID;

public final class ModificarObservacionEvaluacionRequestMapper {

    private ModificarObservacionEvaluacionRequestMapper() {}

    public static ModificarObservacionEvaluacionCommand toCommand(
            ModificarObservacionEvaluacionRequestDTO dto, UUID observacionEvaluacionId, UUID representanteComiteId) {
        return ModificarObservacionEvaluacionCommand.crear(
                observacionEvaluacionId, dto.observacion(), representanteComiteId);
    }
}
