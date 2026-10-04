package com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.AgregarObservacionEvaluacionCommand;
import com.arquisoft.fichas.infrastructure.observacionevaluacion.command.primaryadapter.web.dto.AgregarObservacionEvaluacionRequestDTO;

import java.util.UUID;

public final class AgregarObservacionEvaluacionRequestMapper {

    private AgregarObservacionEvaluacionRequestMapper() {}

    public static AgregarObservacionEvaluacionCommand toCommand(
            AgregarObservacionEvaluacionRequestDTO dto, UUID evaluacionFichaPerfilId, UUID representanteComiteId) {
        return AgregarObservacionEvaluacionCommand.crear(
                evaluacionFichaPerfilId, dto.observacion(), representanteComiteId);
    }
}
