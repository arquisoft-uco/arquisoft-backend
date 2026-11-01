package com.arquisoft.artefactos.application.observacion.command.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoAsesorObservacionCommand;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoAsesorObservacionDomain;

public final class CambiarEstadoAsesorObservacionMapper {

    private CambiarEstadoAsesorObservacionMapper() {}

    public static CambioEstadoAsesorObservacionDomain toDomain(CambiarEstadoAsesorObservacionCommand command) {
        return CambioEstadoAsesorObservacionDomain.crear(command.observacion(), command.estado(), command.asesor());
    }
}
