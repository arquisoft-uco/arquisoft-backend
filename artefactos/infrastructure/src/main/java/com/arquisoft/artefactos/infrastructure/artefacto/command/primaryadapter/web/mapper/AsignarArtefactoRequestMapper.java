package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactoCommand;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.AsignarArtefactoRequestDTO;

import java.util.UUID;

public final class AsignarArtefactoRequestMapper {

    private AsignarArtefactoRequestMapper() {}

    public static AsignarArtefactoCommand toCommand(AsignarArtefactoRequestDTO dto, UUID asesor) {
        return AsignarArtefactoCommand.crear(
                dto.proyectoGrado(), asesor, dto.versionRepositorioArtefacto(), dto.estudiantes());
    }
}
