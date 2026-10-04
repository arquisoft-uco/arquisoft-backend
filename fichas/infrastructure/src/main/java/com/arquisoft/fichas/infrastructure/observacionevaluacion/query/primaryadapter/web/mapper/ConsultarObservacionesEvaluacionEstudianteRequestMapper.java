package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionEstudianteQuery;

import java.util.UUID;

public final class ConsultarObservacionesEvaluacionEstudianteRequestMapper {

    private ConsultarObservacionesEvaluacionEstudianteRequestMapper() {}

    public static ConsultarObservacionesEvaluacionEstudianteQuery toQuery(
            UUID evaluacionFichaPerfilId, UUID estudiante) {
        return ConsultarObservacionesEvaluacionEstudianteQuery.crear(evaluacionFichaPerfilId, estudiante);
    }
}
