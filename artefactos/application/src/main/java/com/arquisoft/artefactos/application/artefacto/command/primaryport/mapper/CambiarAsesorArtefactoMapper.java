package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarAsesorArtefactoCommand;
import com.arquisoft.artefactos.domain.artefacto.CambioAsesorArtefactoDomain;

public final class CambiarAsesorArtefactoMapper {

    private CambiarAsesorArtefactoMapper() {}

    public static CambioAsesorArtefactoDomain toDomain(CambiarAsesorArtefactoCommand command) {
        return CambioAsesorArtefactoDomain.crear(command.artefacto(), command.asesor());
    }
}
