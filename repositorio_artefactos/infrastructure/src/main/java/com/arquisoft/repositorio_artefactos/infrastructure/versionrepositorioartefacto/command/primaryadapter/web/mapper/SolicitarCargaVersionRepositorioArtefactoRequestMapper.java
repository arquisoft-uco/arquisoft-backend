package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SolicitarCargaVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SolicitarCargaVersionRepositorioArtefactoRequestDTO;

import java.util.UUID;

public final class SolicitarCargaVersionRepositorioArtefactoRequestMapper {

    private SolicitarCargaVersionRepositorioArtefactoRequestMapper() {}

    public static SolicitarCargaVersionRepositorioArtefactoCommand toCommand(
            SolicitarCargaVersionRepositorioArtefactoRequestDTO dto, UUID repositorioId, UUID solicitante) {
        return SolicitarCargaVersionRepositorioArtefactoCommand.crear(
                repositorioId, solicitante, dto.nombreArchivo(), dto.tipoContenido(), dto.tamanoBytes());
    }
}
