package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorRealizadaQuery;

public final class ConsultarEvaluacionAsesorRealizadaMapper {

    private ConsultarEvaluacionAsesorRealizadaMapper() {}

    public static EvaluacionAsesorRealizadaCriteria toCriteria(ConsultarEvaluacionAsesorRealizadaQuery query) {
        return new EvaluacionAsesorRealizadaCriteria(query.evaluacionAsesor(), query.asesor());
    }
}
