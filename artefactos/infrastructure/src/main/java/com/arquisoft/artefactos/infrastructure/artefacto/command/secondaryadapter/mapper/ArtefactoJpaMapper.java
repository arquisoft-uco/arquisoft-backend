package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;

public final class ArtefactoJpaMapper {

    private ArtefactoJpaMapper() {}

    public static ArtefactoJpaEntity toJpaEntity(ArtefactoEntity entity) {
        return ArtefactoJpaEntity.builder()
                .id(entity.id())
                .proyectoGrado(entity.proyectoGrado())
                .versionRepositorioArtefacto(entity.versionRepositorioArtefacto())
                .asesor(entity.asesor())
                .build();
    }

    public static ArtefactoEntity toEntity(ArtefactoJpaEntity jpaEntity) {
        return new ArtefactoEntity(
                jpaEntity.getId(),
                jpaEntity.getProyectoGrado(),
                jpaEntity.getVersionRepositorioArtefacto(),
                jpaEntity.getAsesor());
    }
}
