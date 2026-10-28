package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.GenerarVersionArtefactoCommand;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;

public final class GenerarVersionArtefactoMapper {

    private GenerarVersionArtefactoMapper() {}

    public static GeneracionVersionArtefactoDomain toDomain(GenerarVersionArtefactoCommand command) {
        return GeneracionVersionArtefactoDomain.crear(command.carga(), command.artefacto(), command.solicitante());
    }
}
