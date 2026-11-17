package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.OmitirEvaluacionesCuantitativasAsesorCommand;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.OmisionEvaluacionesCuantitativasAsesorDomain;

public final class OmitirEvaluacionesCuantitativasAsesorMapper {

    private OmitirEvaluacionesCuantitativasAsesorMapper() {}

    public static OmisionEvaluacionesCuantitativasAsesorDomain toDomain(
            OmitirEvaluacionesCuantitativasAsesorCommand command) {
        return OmisionEvaluacionesCuantitativasAsesorDomain.crear(
                command.evaluacionAsesor(), command.asesor(), command.items());
    }
}
