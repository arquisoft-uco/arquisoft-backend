package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.CambiarVersionRepositorioArtefactoRequestDTO;

import java.util.UUID;

public final class CambiarVersionRepositorioArtefactoRequestMapper {

    private CambiarVersionRepositorioArtefactoRequestMapper() {}

    public static CambiarVersionRepositorioArtefactoCommand toCommand(
            UUID artefacto, UUID asesor, CambiarVersionRepositorioArtefactoRequestDTO dto) {
        return CambiarVersionRepositorioArtefactoCommand.crear(artefacto, asesor, dto.versionRepositorioArtefacto());
    }
}
