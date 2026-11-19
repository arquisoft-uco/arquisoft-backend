package com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.entregable.command.primaryport.mapper.AgregarEntregableMapper;
import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model.GenerarEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.evaluacion.EvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.GeneracionEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.EvaluacionAsesorDomain;

public final class GenerarEvaluacionMapper {

    private GenerarEvaluacionMapper() {}

    public static GeneracionEvaluacionDomain toDomain(GenerarEvaluacionCommand command) {
        var entregable = AgregarEntregableMapper.toDomain(command.entregable());
        var evaluacion = EvaluacionDomain.crear(entregable.getId());
        var evaluacionAsesor = EvaluacionAsesorDomain.crear(evaluacion.getId(), command.asesor());
        return GeneracionEvaluacionDomain.crear(entregable, evaluacion, evaluacionAsesor);
    }
}
