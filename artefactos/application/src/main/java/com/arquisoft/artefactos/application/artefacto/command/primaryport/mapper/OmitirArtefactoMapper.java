package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.OmitirArtefactoCommand;
import com.arquisoft.artefactos.domain.artefacto.OmisionArtefactoDomain;

public final class OmitirArtefactoMapper {

    private OmitirArtefactoMapper() {}

    public static OmisionArtefactoDomain toDomain(OmitirArtefactoCommand command) {
        return OmisionArtefactoDomain.crear(command.artefacto(), command.asesor());
    }
}
