package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.RegistrarRevisionAsesorCommand;

import java.util.UUID;

public final class RegistrarRevisionAsesorRequestMapper {

    private RegistrarRevisionAsesorRequestMapper() {}

    public static RegistrarRevisionAsesorCommand toCommand(UUID versionArtefacto, UUID asesor) {
        return RegistrarRevisionAsesorCommand.crear(versionArtefacto, asesor);
    }
}
