package com.arquisoft.fichas.infrastructure.observacionevaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionRepresentanteQuery;

import java.util.UUID;

public final class ConsultarObservacionesEvaluacionRepresentanteRequestMapper {

    private ConsultarObservacionesEvaluacionRepresentanteRequestMapper() {}

    public static ConsultarObservacionesEvaluacionRepresentanteQuery toQuery(
            UUID evaluacionFichaPerfilId, UUID representanteComite) {
        return ConsultarObservacionesEvaluacionRepresentanteQuery.crear(evaluacionFichaPerfilId, representanteComite);
    }
}
