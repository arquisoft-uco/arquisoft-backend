package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorEstudianteQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesCualitativasAsesorEstudianteRequestMapper {

    private ConsultarEvaluacionesCualitativasAsesorEstudianteRequestMapper() {}

    public static ConsultarEvaluacionesCualitativasAsesorEstudianteQuery toQuery(UUID evaluacionAsesor, UUID estudiante) {
        return ConsultarEvaluacionesCualitativasAsesorEstudianteQuery.crear(evaluacionAsesor, estudiante);
    }
}
