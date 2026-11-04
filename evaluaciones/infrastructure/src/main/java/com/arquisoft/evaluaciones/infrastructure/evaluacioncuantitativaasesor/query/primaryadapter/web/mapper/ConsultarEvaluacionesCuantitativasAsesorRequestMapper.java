package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesCuantitativasAsesorRequestMapper {

    private ConsultarEvaluacionesCuantitativasAsesorRequestMapper() {}

    public static ConsultarEvaluacionesCuantitativasAsesorQuery toQuery(UUID evaluacionAsesor, UUID asesor) {
        return ConsultarEvaluacionesCuantitativasAsesorQuery.crear(evaluacionAsesor, asesor);
    }
}
