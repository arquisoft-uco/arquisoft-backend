package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.domain.artefacto.CambioVersionRepositorioArtefactoDomain;

public final class CambiarVersionRepositorioArtefactoMapper {

    private CambiarVersionRepositorioArtefactoMapper() {}

    public static CambioVersionRepositorioArtefactoDomain toDomain(CambiarVersionRepositorioArtefactoCommand command) {
        return CambioVersionRepositorioArtefactoDomain.crear(
                command.artefacto(), command.asesor(), command.versionRepositorioArtefacto());
    }
}
