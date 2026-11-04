package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.VersionRepositorioArtefactoSubidaResult;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SubirVersionRepositorioArtefactoResponseDTO;

public final class SubirVersionRepositorioArtefactoResponseMapper {

    private SubirVersionRepositorioArtefactoResponseMapper() {}

    public static SubirVersionRepositorioArtefactoResponseDTO toResponse(
            VersionRepositorioArtefactoSubidaResult result) {
        return new SubirVersionRepositorioArtefactoResponseDTO(result.id(), result.numeroVersion());
    }
}
