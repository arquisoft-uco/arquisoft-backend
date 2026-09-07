package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoEstudianteQuery;

public final class ConsultarEvaluacionesCualitativasJuradoEstudianteMapper {

    private ConsultarEvaluacionesCualitativasJuradoEstudianteMapper() {}

    public static EvaluacionCualitativaJuradoCriteria toCriteria(
            ConsultarEvaluacionesCualitativasJuradoEstudianteQuery query) {
        return new EvaluacionCualitativaJuradoCriteria(query.evaluacionJurado(), query.estudiante());
    }
}
