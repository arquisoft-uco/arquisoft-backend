package com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.RemoverJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.RemocionJuradosEvaluacionDomain;

public final class RemoverJuradosEvaluacionMapper {

    private RemoverJuradosEvaluacionMapper() {}

    public static RemocionJuradosEvaluacionDomain toDomain(RemoverJuradosEvaluacionCommand command) {
        return RemocionJuradosEvaluacionDomain.crear(command.evaluacion(), command.jurados());
    }
}
