package com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;
import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity.ArtefactoEntregableJpaEntity;

public final class ArtefactoEntregableJpaMapper {

    private ArtefactoEntregableJpaMapper() {}

    public static ArtefactoEntregableJpaEntity toJpaEntity(ArtefactoEntregableEntity entity) {
        return ArtefactoEntregableJpaEntity.builder()
                .id(entity.id())
                .artefacto(entity.artefacto())
                .entregableId(entity.entregableProyectoGrado())
                .uriArchivo(entity.uriArchivo())
                .versionArtefacto(entity.versionArtefacto())
                .numeroVersion(entity.numeroVersion())
                .nombreDocumento(entity.nombreDocumento())
                .versionRepositorioArtefacto(entity.versionRepositorioArtefacto())
                .repositorio(entity.repositorio())
                .nombreRepositorio(entity.nombreRepositorio())
                .build();
    }
}
