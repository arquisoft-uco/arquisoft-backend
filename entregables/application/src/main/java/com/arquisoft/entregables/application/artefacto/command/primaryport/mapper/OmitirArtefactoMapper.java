package com.arquisoft.entregables.application.artefacto.command.primaryport.mapper;

import com.arquisoft.entregables.application.artefacto.command.primaryport.model.OmitirArtefactoCommand;
import com.arquisoft.entregables.domain.artefacto.OmisionArtefactoDomain;

public final class OmitirArtefactoMapper {

    private OmitirArtefactoMapper() {}

    public static OmisionArtefactoDomain toDomain(OmitirArtefactoCommand command) {
        return OmisionArtefactoDomain.crear(command.artefacto(), command.ocurridoEn());
    }
}
