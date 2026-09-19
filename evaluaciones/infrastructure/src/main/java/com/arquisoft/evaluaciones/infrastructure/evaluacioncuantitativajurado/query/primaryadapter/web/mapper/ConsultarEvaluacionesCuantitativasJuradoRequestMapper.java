package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesCuantitativasJuradoRequestMapper {

    private ConsultarEvaluacionesCuantitativasJuradoRequestMapper() {}

    public static ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery toQuery(UUID evaluacionJuradoId) {
        return ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery.crear(evaluacionJuradoId);
    }
}
