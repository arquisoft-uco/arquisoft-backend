package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SubirVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;

public final class SubirVersionRepositorioArtefactoMapper {

    private SubirVersionRepositorioArtefactoMapper() {}

    public static SubidaVersionRepositorioArtefactoDomain toDomain(SubirVersionRepositorioArtefactoCommand command) {
        return SubidaVersionRepositorioArtefactoDomain.crear(
                command.carga(),
                command.repositorio(),
                command.solicitante(),
                command.nombreDocumento(),
                command.nuevoDocumento());
    }
}
