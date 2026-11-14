package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.CambiarCriterioEvaluacionCualitativaAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.CambioCriterioEvaluacionCualitativaAsesorDomain;

public final class CambiarCriterioEvaluacionCualitativaAsesorMapper {

    private CambiarCriterioEvaluacionCualitativaAsesorMapper() {}

    public static CambioCriterioEvaluacionCualitativaAsesorDomain toDomain(
            CambiarCriterioEvaluacionCualitativaAsesorCommand command) {
        return CambioCriterioEvaluacionCualitativaAsesorDomain.crear(
                command.evaluacionCualitativaAsesor(), command.asesor(), command.criterio());
    }
}
