package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.command.result.ArtefactosAsignadosResult;
import com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.dto.AsignarArtefactosResponseDTO;

public final class AsignarArtefactosResponseMapper {

    private AsignarArtefactosResponseMapper() {}

    public static AsignarArtefactosResponseDTO toResponse(ArtefactosAsignadosResult result) {
        return new AsignarArtefactosResponseDTO(result.artefactos());
    }
}
