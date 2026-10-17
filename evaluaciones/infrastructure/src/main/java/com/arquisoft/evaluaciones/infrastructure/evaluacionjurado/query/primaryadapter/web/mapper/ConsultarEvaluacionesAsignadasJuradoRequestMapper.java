package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesAsignadasJuradoRequestMapper {

    private ConsultarEvaluacionesAsignadasJuradoRequestMapper() {}

    public static ConsultarEvaluacionesAsignadasJuradoQuery toQuery(UUID jurado) {
        return ConsultarEvaluacionesAsignadasJuradoQuery.crear(jurado);
    }
}
