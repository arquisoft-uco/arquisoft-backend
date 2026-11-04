package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EnviarRevisionAsesorCommand;

import java.util.UUID;

public final class EnviarRevisionAsesorRequestMapper {

    private EnviarRevisionAsesorRequestMapper() {}

    public static EnviarRevisionAsesorCommand toCommand(UUID revisionAsesor, UUID asesor) {
        return EnviarRevisionAsesorCommand.crear(revisionAsesor, asesor);
    }
}
