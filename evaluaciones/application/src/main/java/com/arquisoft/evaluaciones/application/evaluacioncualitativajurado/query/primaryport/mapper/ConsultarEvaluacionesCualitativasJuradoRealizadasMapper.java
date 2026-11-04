package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoRealizadasQuery;

public final class ConsultarEvaluacionesCualitativasJuradoRealizadasMapper {

    private ConsultarEvaluacionesCualitativasJuradoRealizadasMapper() {}

    public static EvaluacionCualitativaJuradoRealizadaCriteria toCriteria(ConsultarEvaluacionesCualitativasJuradoRealizadasQuery query) {
        return new EvaluacionCualitativaJuradoRealizadaCriteria(query.evaluacionJurado(), query.jurado());
    }
}
