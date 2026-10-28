package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.entity.RevisionAsesorJpaEntity;

public final class RevisionAsesorJpaMapper {

    private RevisionAsesorJpaMapper() {}

    public static RevisionAsesorEntity toEntity(RevisionAsesorJpaEntity jpaEntity) {
        return new RevisionAsesorEntity(
                jpaEntity.getId(),
                jpaEntity.getVersionArtefacto(),
                jpaEntity.getEstado());
    }
}
