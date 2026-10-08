package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.CambiarPuntajeEvaluacionCuantitativaJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.CambiarPuntajeEvaluacionCuantitativaJuradoRequestDTO;

import java.util.UUID;

public final class CambiarPuntajeEvaluacionCuantitativaJuradoRequestMapper {

    private CambiarPuntajeEvaluacionCuantitativaJuradoRequestMapper() {}

    public static CambiarPuntajeEvaluacionCuantitativaJuradoCommand toCommand(
            CambiarPuntajeEvaluacionCuantitativaJuradoRequestDTO dto, UUID id, String juradoSubject) {
        return CambiarPuntajeEvaluacionCuantitativaJuradoCommand.crear(id, dto.puntaje(), juradoSubject);
    }
}
