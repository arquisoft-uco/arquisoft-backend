package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorRealizadaQuery;

import java.util.UUID;

public final class ConsultarEvaluacionAsesorRealizadaRequestMapper {

    private ConsultarEvaluacionAsesorRealizadaRequestMapper() {}

    public static ConsultarEvaluacionAsesorRealizadaQuery toQuery(UUID evaluacionAsesorId, UUID asesor) {
        return ConsultarEvaluacionAsesorRealizadaQuery.crear(evaluacionAsesorId, asesor);
    }
}
