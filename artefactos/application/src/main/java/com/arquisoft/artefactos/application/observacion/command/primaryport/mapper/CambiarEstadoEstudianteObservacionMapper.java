package com.arquisoft.artefactos.application.observacion.command.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoEstudianteObservacionCommand;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoEstudianteObservacionDomain;

public final class CambiarEstadoEstudianteObservacionMapper {

    private CambiarEstadoEstudianteObservacionMapper() {}

    public static CambioEstadoEstudianteObservacionDomain toDomain(CambiarEstadoEstudianteObservacionCommand command) {
        return CambioEstadoEstudianteObservacionDomain.crear(
                command.observacion(), command.estado(), command.estudiante());
    }
}
