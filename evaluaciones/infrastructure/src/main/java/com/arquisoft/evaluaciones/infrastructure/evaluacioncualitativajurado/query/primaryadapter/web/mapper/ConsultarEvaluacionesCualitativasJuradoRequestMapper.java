package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoEstudianteQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesCualitativasJuradoRequestMapper {

    private ConsultarEvaluacionesCualitativasJuradoRequestMapper() {}

    public static ConsultarEvaluacionesCualitativasJuradoEstudianteQuery toQuery(
            UUID evaluacionJuradoId, UUID estudianteId) {
        return ConsultarEvaluacionesCualitativasJuradoEstudianteQuery.crear(evaluacionJuradoId, estudianteId);
    }
}
