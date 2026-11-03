package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.criteria.EvaluacionCuantitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorQuery;

public final class ConsultarEvaluacionesCuantitativasAsesorMapper {

    private ConsultarEvaluacionesCuantitativasAsesorMapper() {}

    public static EvaluacionCuantitativaAsesorCriteria toCriteria(ConsultarEvaluacionesCuantitativasAsesorQuery query) {
        return new EvaluacionCuantitativaAsesorCriteria(query.evaluacionAsesor(), query.asesor());
    }
}
