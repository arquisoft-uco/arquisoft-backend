package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorDeEvaluacionCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorQuery;

public final class ConsultarEvaluacionAsesorMapper {

    private ConsultarEvaluacionAsesorMapper() {}

    public static EvaluacionAsesorDeEvaluacionCriteria toCriteria(ConsultarEvaluacionAsesorQuery query) {
        return new EvaluacionAsesorDeEvaluacionCriteria(query.evaluacion());
    }
}
