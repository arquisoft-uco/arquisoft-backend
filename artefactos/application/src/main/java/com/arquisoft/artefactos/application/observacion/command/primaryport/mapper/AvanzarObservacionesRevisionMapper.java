package com.arquisoft.artefactos.application.observacion.command.primaryport.mapper;

import com.arquisoft.artefactos.domain.observacion.AvanceObservacionesRevisionDomain;

import java.util.UUID;

public final class AvanzarObservacionesRevisionMapper {

    private AvanzarObservacionesRevisionMapper() {}

    public static AvanceObservacionesRevisionDomain toDomain(UUID revisionAsesor) {
        return AvanceObservacionesRevisionDomain.crear(revisionAsesor);
    }
}
