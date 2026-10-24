package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SubirVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SubirVersionRepositorioArtefactoRequestDTO;

import java.util.UUID;

public final class SubirVersionRepositorioArtefactoRequestMapper {

    private SubirVersionRepositorioArtefactoRequestMapper() {}

    public static SubirVersionRepositorioArtefactoCommand toCommand(
            SubirVersionRepositorioArtefactoRequestDTO dto, UUID repositorioId, UUID solicitante) {
        return SubirVersionRepositorioArtefactoCommand.crear(
                dto.carga(), repositorioId, solicitante, dto.nombreDocumento(), dto.nuevoDocumento());
    }
}
