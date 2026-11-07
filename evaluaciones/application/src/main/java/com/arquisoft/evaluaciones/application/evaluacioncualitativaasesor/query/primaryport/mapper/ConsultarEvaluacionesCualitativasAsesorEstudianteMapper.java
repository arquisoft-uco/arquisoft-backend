package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorEstudianteQuery;

public final class ConsultarEvaluacionesCualitativasAsesorEstudianteMapper {

    private ConsultarEvaluacionesCualitativasAsesorEstudianteMapper() {}

    public static EvaluacionCualitativaAsesorEstudianteCriteria toCriteria(ConsultarEvaluacionesCualitativasAsesorEstudianteQuery query) {
        return new EvaluacionCualitativaAsesorEstudianteCriteria(query.evaluacionAsesor(), query.estudiante());
    }
}
