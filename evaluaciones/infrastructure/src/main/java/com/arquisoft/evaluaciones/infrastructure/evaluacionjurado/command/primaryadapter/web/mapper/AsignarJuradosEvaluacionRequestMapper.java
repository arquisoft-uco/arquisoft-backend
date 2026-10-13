package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.AsignarJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web.dto.AsignarJuradosEvaluacionRequestDTO;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.UUID;

public final class AsignarJuradosEvaluacionRequestMapper {

    private AsignarJuradosEvaluacionRequestMapper() {}

    public static AsignarJuradosEvaluacionCommand toCommand(
            AsignarJuradosEvaluacionRequestDTO dto, UUID evaluacion) {
        return AsignarJuradosEvaluacionCommand.crear(
                evaluacion.toString(), UtilColeccion.aplicarPorDefecto(dto.jurados()));
    }
}
