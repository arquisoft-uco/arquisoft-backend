package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesCuantitativasAsesorEstudianteRequestMapper {

    private ConsultarEvaluacionesCuantitativasAsesorEstudianteRequestMapper() {}

    public static ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery toQuery(
            UUID evaluacionAsesor, UUID estudiante) {
        return ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery.crear(evaluacionAsesor, estudiante);
    }
}
