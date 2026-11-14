package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.CambiarCriterioEvaluacionCualitativaJuradoCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.CambioCriterioEvaluacionCualitativaJuradoDomain;

public final class CambiarCriterioEvaluacionCualitativaJuradoMapper {

    private CambiarCriterioEvaluacionCualitativaJuradoMapper() {}

    public static CambioCriterioEvaluacionCualitativaJuradoDomain toDomain(
            CambiarCriterioEvaluacionCualitativaJuradoCommand command) {
        return CambioCriterioEvaluacionCualitativaJuradoDomain.crear(
                command.evaluacionCualitativaJurado(), command.jurado(), command.criterio());
    }
}
