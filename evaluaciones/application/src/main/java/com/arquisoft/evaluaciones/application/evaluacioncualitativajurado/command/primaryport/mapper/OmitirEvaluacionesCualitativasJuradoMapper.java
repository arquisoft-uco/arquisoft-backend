package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.OmitirEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.OmisionEvaluacionesCualitativasJuradoDomain;

public final class OmitirEvaluacionesCualitativasJuradoMapper {

    private OmitirEvaluacionesCualitativasJuradoMapper() {}

    public static OmisionEvaluacionesCualitativasJuradoDomain toDomain(
            OmitirEvaluacionesCualitativasJuradoCommand command) {
        return OmisionEvaluacionesCualitativasJuradoDomain.crear(command.evaluacionJurado(), command.evaluaciones());
    }
}
