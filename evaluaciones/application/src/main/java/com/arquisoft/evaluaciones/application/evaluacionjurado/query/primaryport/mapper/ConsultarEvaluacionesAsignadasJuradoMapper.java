package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;

public final class ConsultarEvaluacionesAsignadasJuradoMapper {

    private ConsultarEvaluacionesAsignadasJuradoMapper() {}

    public static EvaluacionesAsignadasJuradoCriteria toCriteria(ConsultarEvaluacionesAsignadasJuradoQuery query) {
        return new EvaluacionesAsignadasJuradoCriteria(query.jurado());
    }
}
