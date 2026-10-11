package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionCoordinadorQuery;

import java.util.UUID;

public final class ConsultarObservacionesEvaluacionCoordinadorRequestMapper {

    private ConsultarObservacionesEvaluacionCoordinadorRequestMapper() {}

    public static ConsultarObservacionesEvaluacionCoordinadorQuery toQuery(UUID fichaPerfilId) {
        return ConsultarObservacionesEvaluacionCoordinadorQuery.crear(fichaPerfilId);
    }
}
