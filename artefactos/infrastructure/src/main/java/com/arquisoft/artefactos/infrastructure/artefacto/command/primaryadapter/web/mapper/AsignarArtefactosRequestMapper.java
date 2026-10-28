package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactosCommand;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.AsignarArtefactosRequestDTO;

import java.util.UUID;

public final class AsignarArtefactosRequestMapper {

    private AsignarArtefactosRequestMapper() {}

    public static AsignarArtefactosCommand toCommand(AsignarArtefactosRequestDTO dto, UUID asesor) {
        return AsignarArtefactosCommand.crear(dto.proyectoGrado(), asesor, dto.versionesRepositorioArtefacto());
    }
}
