package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.OmitirEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.OmisionEvaluacionesCualitativasAsesorDomain;

public final class OmitirEvaluacionesCualitativasAsesorMapper {

    private OmitirEvaluacionesCualitativasAsesorMapper() {}

    public static OmisionEvaluacionesCualitativasAsesorDomain toDomain(
            OmitirEvaluacionesCualitativasAsesorCommand command) {
        return OmisionEvaluacionesCualitativasAsesorDomain.crear(
                command.evaluacionAsesor(), command.asesor(), command.items());
    }
}
