package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionAsesorQuery;

import java.util.UUID;

public final class ConsultarObservacionesEvaluacionAsesorRequestMapper {

    private ConsultarObservacionesEvaluacionAsesorRequestMapper() {}

    public static ConsultarObservacionesEvaluacionAsesorQuery toQuery(
            UUID evaluacionFichaPerfilId, UUID asesorFicha) {
        return ConsultarObservacionesEvaluacionAsesorQuery.crear(evaluacionFichaPerfilId, asesorFicha);
    }
}
