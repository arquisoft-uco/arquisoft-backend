package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery;

public final class ConsultarEvaluacionesCuantitativasJuradoRealizadasMapper {

    private ConsultarEvaluacionesCuantitativasJuradoRealizadasMapper() {}

    public static EvaluacionCuantitativaJuradoRealizadaCriteria toCriteria(ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery query) {
        return new EvaluacionCuantitativaJuradoRealizadaCriteria(query.evaluacionJurado(), query.jurado());
    }
}
