package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesCualitativasAsesorRequestMapper {

    private ConsultarEvaluacionesCualitativasAsesorRequestMapper() {}

    public static ConsultarEvaluacionesCualitativasAsesorQuery toQuery(UUID evaluacionAsesor, UUID asesor) {
        return ConsultarEvaluacionesCualitativasAsesorQuery.crear(evaluacionAsesor, asesor);
    }
}
