package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.CerrarRevisionAsesorCommand;
import com.arquisoft.artefactos.domain.revisionasesor.CierreRevisionAsesorDomain;

public final class CerrarRevisionAsesorMapper {

    private CerrarRevisionAsesorMapper() {}

    public static CierreRevisionAsesorDomain toDomain(CerrarRevisionAsesorCommand command) {
        return CierreRevisionAsesorDomain.crear(command.revisionAsesor(), command.asesor());
    }
}
