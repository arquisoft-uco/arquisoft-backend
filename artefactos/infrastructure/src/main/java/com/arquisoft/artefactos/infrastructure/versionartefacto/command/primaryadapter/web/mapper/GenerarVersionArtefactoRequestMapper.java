package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.GenerarVersionArtefactoCommand;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.dto.GenerarVersionArtefactoRequestDTO;

import java.util.UUID;

public final class GenerarVersionArtefactoRequestMapper {

    private GenerarVersionArtefactoRequestMapper() {}

    public static GenerarVersionArtefactoCommand toCommand(
            GenerarVersionArtefactoRequestDTO dto, UUID artefactoId, UUID solicitante) {
        return GenerarVersionArtefactoCommand.crear(dto.carga(), artefactoId, solicitante);
    }
}
