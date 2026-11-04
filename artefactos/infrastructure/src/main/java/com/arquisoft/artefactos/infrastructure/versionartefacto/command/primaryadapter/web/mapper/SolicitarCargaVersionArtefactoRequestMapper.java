package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.SolicitarCargaVersionArtefactoCommand;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.dto.SolicitarCargaVersionArtefactoRequestDTO;

import java.util.UUID;

public final class SolicitarCargaVersionArtefactoRequestMapper {

    private SolicitarCargaVersionArtefactoRequestMapper() {}

    public static SolicitarCargaVersionArtefactoCommand toCommand(
            SolicitarCargaVersionArtefactoRequestDTO dto, UUID artefactoId, UUID solicitante) {
        return SolicitarCargaVersionArtefactoCommand.crear(
                artefactoId, solicitante, dto.nombreArchivo(), dto.tipoContenido(), dto.tamanoBytes());
    }
}
