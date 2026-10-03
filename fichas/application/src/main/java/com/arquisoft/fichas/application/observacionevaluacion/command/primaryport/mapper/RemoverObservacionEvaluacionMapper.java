package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.RemoverObservacionEvaluacionCommand;
import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;

public final class RemoverObservacionEvaluacionMapper {

    private RemoverObservacionEvaluacionMapper() {}

    public static RemocionObservacionEvaluacionDomain toDomain(RemoverObservacionEvaluacionCommand command) {
        return RemocionObservacionEvaluacionDomain.crear(command.observacionEvaluacion(), command.representanteComite());
    }
}
