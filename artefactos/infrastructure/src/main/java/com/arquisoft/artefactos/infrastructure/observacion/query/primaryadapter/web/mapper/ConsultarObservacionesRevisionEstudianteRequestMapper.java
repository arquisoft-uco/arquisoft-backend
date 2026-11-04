package com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionEstudianteQuery;

import java.util.UUID;

public final class ConsultarObservacionesRevisionEstudianteRequestMapper {

    private ConsultarObservacionesRevisionEstudianteRequestMapper() {}

    public static ConsultarObservacionesRevisionEstudianteQuery toQuery(UUID revisionAsesorId, UUID estudiante) {
        return ConsultarObservacionesRevisionEstudianteQuery.crear(revisionAsesorId, estudiante);
    }
}
