package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.OmitirEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.OmitirEvaluacionesCuantitativasJuradoRequestDTO;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.UUID;

public final class OmitirEvaluacionesCuantitativasJuradoRequestMapper {

    private OmitirEvaluacionesCuantitativasJuradoRequestMapper() {}

    public static OmitirEvaluacionesCuantitativasJuradoCommand toCommand(
            OmitirEvaluacionesCuantitativasJuradoRequestDTO dto, UUID evaluacionJurado) {
        return OmitirEvaluacionesCuantitativasJuradoCommand.crear(
                evaluacionJurado.toString(),
                UtilColeccion.aplicarPorDefecto(dto.evaluaciones()));
    }
}
