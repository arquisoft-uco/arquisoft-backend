package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaAsesorCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.dto.CambiarPuntajeEvaluacionCuantitativaAsesorRequestDTO;

import java.util.UUID;

public final class CambiarPuntajeEvaluacionCuantitativaAsesorRequestMapper {

    private CambiarPuntajeEvaluacionCuantitativaAsesorRequestMapper() {}

    public static CambiarPuntajeEvaluacionCuantitativaAsesorCommand toCommand(
            CambiarPuntajeEvaluacionCuantitativaAsesorRequestDTO dto, UUID evaluacionCuantitativaAsesor, UUID asesor) {
        return CambiarPuntajeEvaluacionCuantitativaAsesorCommand.crear(
                evaluacionCuantitativaAsesor, dto.puntaje(), asesor);
    }
}
