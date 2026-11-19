package com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.primaryport.model.AsignarAsesorEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.EvaluacionAsesorDomain;

public final class AsignarAsesorEvaluacionMapper {

    private AsignarAsesorEvaluacionMapper() {}

    public static EvaluacionAsesorDomain toDomain(AsignarAsesorEvaluacionCommand command) {
        return EvaluacionAsesorDomain.crear(command.evaluacion(), command.asesor());
    }
}
