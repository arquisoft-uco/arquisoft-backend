package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;

public final class EliminarVersionRepositorioArtefactoMapper {

    private EliminarVersionRepositorioArtefactoMapper() {}

    public static EliminacionVersionRepositorioArtefactoDomain toDomain(
            EliminarVersionRepositorioArtefactoCommand command) {
        return EliminacionVersionRepositorioArtefactoDomain.crear(command.version());
    }
}
