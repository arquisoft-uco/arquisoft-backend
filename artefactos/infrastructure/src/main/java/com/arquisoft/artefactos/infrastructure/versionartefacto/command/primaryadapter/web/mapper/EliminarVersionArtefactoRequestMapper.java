package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.EliminarVersionArtefactoCommand;

import java.util.UUID;

public final class EliminarVersionArtefactoRequestMapper {

    private EliminarVersionArtefactoRequestMapper() {}

    public static EliminarVersionArtefactoCommand toCommand(UUID versionArtefacto, UUID solicitante) {
        return EliminarVersionArtefactoCommand.crear(versionArtefacto, solicitante);
    }
}
