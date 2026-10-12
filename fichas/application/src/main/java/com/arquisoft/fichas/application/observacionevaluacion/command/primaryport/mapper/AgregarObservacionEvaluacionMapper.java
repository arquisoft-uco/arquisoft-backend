package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.AgregarObservacionEvaluacionCommand;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.ObservacionEvaluacionDomain;

public final class AgregarObservacionEvaluacionMapper {

    private AgregarObservacionEvaluacionMapper() {}

    public static AgregacionObservacionEvaluacionDomain toDomain(AgregarObservacionEvaluacionCommand command) {
        var observacionEvaluacion = ObservacionEvaluacionDomain.crear(
                command.evaluacionFichaPerfil(), command.observacion());
        return AgregacionObservacionEvaluacionDomain.crear(observacionEvaluacion, command.representanteComite());
    }
}
