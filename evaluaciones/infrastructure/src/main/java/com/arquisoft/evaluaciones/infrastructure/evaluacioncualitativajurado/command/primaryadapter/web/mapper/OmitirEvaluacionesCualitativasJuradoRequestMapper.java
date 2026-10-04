package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.OmitirEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.dto.OmitirEvaluacionesCualitativasJuradoRequestDTO;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.UUID;

public final class OmitirEvaluacionesCualitativasJuradoRequestMapper {

    private OmitirEvaluacionesCualitativasJuradoRequestMapper() {}

    public static OmitirEvaluacionesCualitativasJuradoCommand toCommand(
            OmitirEvaluacionesCualitativasJuradoRequestDTO dto, UUID evaluacionJurado) {
        return OmitirEvaluacionesCualitativasJuradoCommand.crear(
                evaluacionJurado.toString(),
                UtilColeccion.aplicarPorDefecto(dto.evaluaciones()));
    }
}
