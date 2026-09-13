package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery;

public final class ConsultarEvaluacionesCuantitativasJuradoEstudianteMapper {

    private ConsultarEvaluacionesCuantitativasJuradoEstudianteMapper() {}

    public static EvaluacionCuantitativaJuradoCriteria toCriteria(
            ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery query) {
        return new EvaluacionCuantitativaJuradoCriteria(query.evaluacionJurado(), query.estudiante());
    }
}
