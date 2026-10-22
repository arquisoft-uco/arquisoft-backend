package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoRealizadasQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesCualitativasJuradoRealizadasRequestMapper {

    private ConsultarEvaluacionesCualitativasJuradoRealizadasRequestMapper() {}

    public static ConsultarEvaluacionesCualitativasJuradoRealizadasQuery toQuery(UUID evaluacionJuradoId, UUID jurado) {
        return ConsultarEvaluacionesCualitativasJuradoRealizadasQuery.crear(evaluacionJuradoId, jurado);
    }
}
