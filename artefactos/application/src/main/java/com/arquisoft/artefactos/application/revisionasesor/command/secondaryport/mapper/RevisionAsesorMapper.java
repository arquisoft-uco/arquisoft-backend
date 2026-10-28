package com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;

public final class RevisionAsesorMapper {

    private RevisionAsesorMapper() {}

    public static RevisionAsesorDomain toDomain(RevisionAsesorEntity entity) {
        return RevisionAsesorDomain.reconstruir(
                entity.id(),
                entity.versionArtefacto(),
                EstadoRevisionAsesor.desde(entity.estado()));
    }
}
