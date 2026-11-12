package com.arquisoft.entregables.application.artefacto.command.primaryport.mapper;

import com.arquisoft.entregables.application.artefacto.command.primaryport.model.ReactivarArtefactoCommand;
import com.arquisoft.entregables.domain.artefacto.ReactivacionArtefactoDomain;

public final class ReactivarArtefactoMapper {

    private ReactivarArtefactoMapper() {}

    public static ReactivacionArtefactoDomain toDomain(ReactivarArtefactoCommand command) {
        return ReactivacionArtefactoDomain.crear(command.artefacto(), command.ocurridoEn());
    }
}
