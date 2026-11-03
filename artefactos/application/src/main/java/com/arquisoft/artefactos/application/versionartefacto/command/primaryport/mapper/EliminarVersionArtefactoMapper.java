package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.EliminarVersionArtefactoCommand;
import com.arquisoft.artefactos.domain.versionartefacto.EliminacionVersionArtefactoDomain;

public final class EliminarVersionArtefactoMapper {

    private EliminarVersionArtefactoMapper() {}

    public static EliminacionVersionArtefactoDomain toDomain(EliminarVersionArtefactoCommand command) {
        return EliminacionVersionArtefactoDomain.crear(command.versionArtefacto(), command.solicitante());
    }
}
