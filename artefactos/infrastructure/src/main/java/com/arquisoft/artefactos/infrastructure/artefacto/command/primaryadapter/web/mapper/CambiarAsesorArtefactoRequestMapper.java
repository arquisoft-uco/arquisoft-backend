package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarAsesorArtefactoCommand;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.CambiarAsesorArtefactoRequestDTO;

import java.util.UUID;

public final class CambiarAsesorArtefactoRequestMapper {

    private CambiarAsesorArtefactoRequestMapper() {}

    public static CambiarAsesorArtefactoCommand toCommand(UUID artefacto, CambiarAsesorArtefactoRequestDTO dto) {
        return CambiarAsesorArtefactoCommand.crear(artefacto, dto.asesor());
    }
}
