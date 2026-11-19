package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.model.AsignarAsesorEvaluacionCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.primaryadapter.web.dto.AsignarAsesorEvaluacionRequestDTO;

import java.util.UUID;

public final class AsignarAsesorEvaluacionRequestMapper {

    private AsignarAsesorEvaluacionRequestMapper() {}

    public static AsignarAsesorEvaluacionCommand toCommand(AsignarAsesorEvaluacionRequestDTO dto, UUID evaluacion) {
        return AsignarAsesorEvaluacionCommand.crear(evaluacion, dto.asesor());
    }
}
