package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery;

public final class ConsultarEvaluacionesCuantitativasAsesorEstudianteMapper {

    private ConsultarEvaluacionesCuantitativasAsesorEstudianteMapper() {}

    public static EvaluacionCuantitativaAsesorEstudianteCriteria toCriteria(
            ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery query) {
        return new EvaluacionCuantitativaAsesorEstudianteCriteria(query.evaluacionAsesor(), query.estudiante());
    }
}
