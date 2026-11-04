package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;

import java.util.UUID;

public final class EliminarVersionRepositorioArtefactoRequestMapper {

    private EliminarVersionRepositorioArtefactoRequestMapper() {}

    public static EliminarVersionRepositorioArtefactoCommand toCommand(UUID version) {
        return EliminarVersionRepositorioArtefactoCommand.crear(version);
    }
}
