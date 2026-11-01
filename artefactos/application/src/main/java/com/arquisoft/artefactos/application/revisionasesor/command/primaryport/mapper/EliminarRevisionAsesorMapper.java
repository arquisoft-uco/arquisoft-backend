package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.EliminarRevisionAsesorCommand;
import com.arquisoft.artefactos.domain.revisionasesor.EliminacionRevisionAsesorDomain;

public final class EliminarRevisionAsesorMapper {

    private EliminarRevisionAsesorMapper() {}

    public static EliminacionRevisionAsesorDomain toDomain(EliminarRevisionAsesorCommand command) {
        return EliminacionRevisionAsesorDomain.crear(command.revisionAsesor(), command.asesor());
    }
}
