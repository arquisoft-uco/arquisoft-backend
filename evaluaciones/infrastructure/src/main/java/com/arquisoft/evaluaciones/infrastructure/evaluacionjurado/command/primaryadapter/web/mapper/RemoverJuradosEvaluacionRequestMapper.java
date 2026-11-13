package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.RemoverJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.dto.RemoverJuradosEvaluacionRequestDTO;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.UUID;

public final class RemoverJuradosEvaluacionRequestMapper {

    private RemoverJuradosEvaluacionRequestMapper() {}

    public static RemoverJuradosEvaluacionCommand toCommand(
            RemoverJuradosEvaluacionRequestDTO dto, UUID evaluacion) {
        return RemoverJuradosEvaluacionCommand.crear(
                evaluacion, UtilColeccion.aplicarPorDefecto(dto.jurados()));
    }
}
