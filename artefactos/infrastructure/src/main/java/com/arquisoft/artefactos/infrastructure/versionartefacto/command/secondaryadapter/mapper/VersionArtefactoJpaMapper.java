package com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.secondaryadapter.entity.VersionArtefactoJpaEntity;

public final class VersionArtefactoJpaMapper {

    private VersionArtefactoJpaMapper() {}

    public static VersionArtefactoJpaEntity toJpaEntity(VersionArtefactoEntity entity) {
        return VersionArtefactoJpaEntity.builder()
                .id(entity.id())
                .artefacto(entity.artefacto())
                .numeroVersion(entity.numeroVersion())
                .fechaCreacion(entity.fechaCreacion())
                .uriArchivo(entity.uriArchivo())
                .build();
    }
}
