package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorQuery;

import java.util.UUID;

public final class ConsultarEvaluacionAsesorRequestMapper {

    private ConsultarEvaluacionAsesorRequestMapper() {}

    public static ConsultarEvaluacionAsesorQuery toQuery(UUID evaluacionId) {
        return ConsultarEvaluacionAsesorQuery.crear(evaluacionId);
    }
}
