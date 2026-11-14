package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.CambiarCriterioEvaluacionCualitativaAsesorCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.dto.CambiarCriterioEvaluacionCualitativaAsesorRequestDTO;

import java.util.UUID;

public final class CambiarCriterioEvaluacionCualitativaAsesorRequestMapper {

    private CambiarCriterioEvaluacionCualitativaAsesorRequestMapper() {}

    public static CambiarCriterioEvaluacionCualitativaAsesorCommand toCommand(
            CambiarCriterioEvaluacionCualitativaAsesorRequestDTO dto, UUID evaluacionCualitativaAsesor, UUID asesor) {
        return CambiarCriterioEvaluacionCualitativaAsesorCommand.crear(
                evaluacionCualitativaAsesor, dto.criterio(), asesor);
    }
}
