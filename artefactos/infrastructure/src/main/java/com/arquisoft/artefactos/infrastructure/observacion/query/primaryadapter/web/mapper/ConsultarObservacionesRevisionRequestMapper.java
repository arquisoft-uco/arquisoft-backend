package com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionQuery;

import java.util.UUID;

public final class ConsultarObservacionesRevisionRequestMapper {

    private ConsultarObservacionesRevisionRequestMapper() {}

    public static ConsultarObservacionesRevisionQuery toQuery(UUID revisionAsesorId, UUID asesor) {
        return ConsultarObservacionesRevisionQuery.crear(revisionAsesorId, asesor);
    }
}
