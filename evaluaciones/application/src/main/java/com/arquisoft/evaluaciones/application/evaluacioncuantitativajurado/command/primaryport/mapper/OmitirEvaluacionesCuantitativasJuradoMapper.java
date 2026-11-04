package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.OmitirEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.OmisionEvaluacionesCuantitativasJuradoDomain;

public final class OmitirEvaluacionesCuantitativasJuradoMapper {

    private OmitirEvaluacionesCuantitativasJuradoMapper() {}

    public static OmisionEvaluacionesCuantitativasJuradoDomain toDomain(
            OmitirEvaluacionesCuantitativasJuradoCommand command) {
        return OmisionEvaluacionesCuantitativasJuradoDomain.crear(command.evaluacionJurado(), command.evaluaciones());
    }
}
