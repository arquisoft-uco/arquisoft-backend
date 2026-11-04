package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.CambiarEstadoRevisionAsesorCommand;
import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;

public final class CambiarEstadoRevisionAsesorMapper {

    private CambiarEstadoRevisionAsesorMapper() {}

    public static CambioEstadoRevisionAsesorDomain toDomain(CambiarEstadoRevisionAsesorCommand command) {
        return CambioEstadoRevisionAsesorDomain.crear(
                command.revisionAsesor(), command.estado(), command.estudiante());
    }
}
