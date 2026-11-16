package com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.entregable.command.primaryport.mapper.AgregarEntregableMapper;
import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model.GenerarEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.evaluacion.EvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.GeneracionEvaluacionDomain;

public final class GenerarEvaluacionMapper {

    private GenerarEvaluacionMapper() {}

    public static GeneracionEvaluacionDomain toDomain(GenerarEvaluacionCommand command) {
        var entregable = AgregarEntregableMapper.toDomain(command.entregable());
        var evaluacion = EvaluacionDomain.crear(entregable.getId());
        return GeneracionEvaluacionDomain.crear(entregable, evaluacion);
    }
}
