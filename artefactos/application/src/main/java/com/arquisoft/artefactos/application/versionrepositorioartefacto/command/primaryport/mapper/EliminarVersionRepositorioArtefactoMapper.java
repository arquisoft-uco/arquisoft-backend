package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;

public final class EliminarVersionRepositorioArtefactoMapper {

    private EliminarVersionRepositorioArtefactoMapper() {}

    public static EliminacionVersionRepositorioArtefactoDomain toDomain(
            EliminarVersionRepositorioArtefactoCommand command) {
        return EliminacionVersionRepositorioArtefactoDomain.crear(command.version());
    }
}
