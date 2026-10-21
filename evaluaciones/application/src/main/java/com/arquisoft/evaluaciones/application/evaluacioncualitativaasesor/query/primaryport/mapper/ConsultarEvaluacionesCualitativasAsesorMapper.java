package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.criteria.EvaluacionCualitativaAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorQuery;

public final class ConsultarEvaluacionesCualitativasAsesorMapper {

    private ConsultarEvaluacionesCualitativasAsesorMapper() {}

    public static EvaluacionCualitativaAsesorCriteria toCriteria(ConsultarEvaluacionesCualitativasAsesorQuery query) {
        return new EvaluacionCualitativaAsesorCriteria(query.evaluacionAsesor(), query.asesor());
    }
}
