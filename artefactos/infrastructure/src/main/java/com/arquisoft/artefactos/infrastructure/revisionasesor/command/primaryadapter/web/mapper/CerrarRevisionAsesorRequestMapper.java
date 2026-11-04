package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.CerrarRevisionAsesorCommand;

import java.util.UUID;

public final class CerrarRevisionAsesorRequestMapper {

    private CerrarRevisionAsesorRequestMapper() {}

    public static CerrarRevisionAsesorCommand toCommand(UUID revisionAsesor, UUID asesor) {
        return CerrarRevisionAsesorCommand.crear(revisionAsesor, asesor);
    }
}
