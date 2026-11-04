package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.AsignarJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.AsignacionJuradosEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EvaluacionJuradoDomain;

public final class AsignarJuradosEvaluacionMapper {

    private AsignarJuradosEvaluacionMapper() {}

    public static AsignacionJuradosEvaluacionDomain toDomain(AsignarJuradosEvaluacionCommand command) {
        var juradosEvaluacion = command.jurados().stream()
                .map(jurado -> EvaluacionJuradoDomain.crear(command.evaluacion(), jurado))
                .toList();
        return AsignacionJuradosEvaluacionDomain.crear(command.evaluacion(), juradosEvaluacion);
    }
}
