package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EliminarRevisionAsesorCommand;

import java.util.UUID;

public final class EliminarRevisionAsesorRequestMapper {

    private EliminarRevisionAsesorRequestMapper() {}

    public static EliminarRevisionAsesorCommand toCommand(UUID revisionAsesor, UUID asesor) {
        return EliminarRevisionAsesorCommand.crear(revisionAsesor, asesor);
    }
}
