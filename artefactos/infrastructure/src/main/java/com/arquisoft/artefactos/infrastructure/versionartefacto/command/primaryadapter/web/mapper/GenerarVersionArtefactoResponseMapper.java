package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.result.VersionArtefactoGeneradaResult;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.dto.GenerarVersionArtefactoResponseDTO;

public final class GenerarVersionArtefactoResponseMapper {

    private GenerarVersionArtefactoResponseMapper() {}

    public static GenerarVersionArtefactoResponseDTO toResponse(VersionArtefactoGeneradaResult result) {
        return new GenerarVersionArtefactoResponseDTO(result.id(), result.numeroVersion());
    }
}
