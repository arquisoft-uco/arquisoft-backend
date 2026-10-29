package com.arquisoft.artefactos.application.revisionasesor.command.primaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.primaryport.model.RegistrarRevisionAsesorCommand;
import com.arquisoft.artefactos.domain.revisionasesor.RegistroRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;

public final class RegistrarRevisionAsesorMapper {

    private RegistrarRevisionAsesorMapper() {}

    public static RegistroRevisionAsesorDomain toDomain(RegistrarRevisionAsesorCommand command) {
        var revision = RevisionAsesorDomain.crear(command.versionArtefacto());
        return RegistroRevisionAsesorDomain.crear(revision, command.asesor());
    }
}
