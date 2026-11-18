package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.CambiarCriterioEvaluacionCualitativaJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.dto.CambiarCriterioEvaluacionCualitativaJuradoRequestDTO;

import java.util.UUID;

public final class CambiarCriterioEvaluacionCualitativaJuradoRequestMapper {

    private CambiarCriterioEvaluacionCualitativaJuradoRequestMapper() {}

    public static CambiarCriterioEvaluacionCualitativaJuradoCommand toCommand(
            CambiarCriterioEvaluacionCualitativaJuradoRequestDTO dto, UUID id, UUID jurado) {
        return CambiarCriterioEvaluacionCualitativaJuradoCommand.crear(id, dto.criterio(), jurado);
    }
}
