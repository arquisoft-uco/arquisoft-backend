package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery;

import java.util.UUID;

public final class ConsultarEvaluacionesCuantitativasJuradoRealizadasRequestMapper {

    private ConsultarEvaluacionesCuantitativasJuradoRealizadasRequestMapper() {}

    public static ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery toQuery(UUID evaluacionJuradoId, UUID jurado) {
        return ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery.crear(evaluacionJuradoId, jurado);
    }
}
