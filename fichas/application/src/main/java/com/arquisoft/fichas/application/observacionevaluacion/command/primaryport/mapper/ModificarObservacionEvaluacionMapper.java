package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.ModificarObservacionEvaluacionCommand;
import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;

public final class ModificarObservacionEvaluacionMapper {

    private ModificarObservacionEvaluacionMapper() {}

    public static ModificacionObservacionEvaluacionDomain toDomain(ModificarObservacionEvaluacionCommand command) {
        return ModificacionObservacionEvaluacionDomain.crear(
                command.observacionEvaluacion(), command.observacion(), command.representanteComite());
    }
}
