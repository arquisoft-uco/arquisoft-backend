package com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;
import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity.ArtefactoEntregableJpaEntity;
import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity.ArtefactoJpaEntity;

public final class ArtefactoEntregableJpaMapper {

    private ArtefactoEntregableJpaMapper() {}

    public static ArtefactoJpaEntity toArtefactoJpaEntity(ArtefactoEntregableEntity entity) {
        return ArtefactoJpaEntity.builder()
                .id(entity.versionArtefacto())
                .uriArchivo(entity.uriArchivo())
                .artefacto(entity.artefacto())
                .versionRepositorioArtefacto(entity.versionRepositorioArtefacto())
                .nombreDocumento(entity.nombreDocumento())
                .repositorio(entity.repositorio())
                .nombreRepositorio(entity.nombreRepositorio())
                .build();
    }

    public static ArtefactoEntregableJpaEntity toJpaEntity(ArtefactoEntregableEntity entity) {
        return ArtefactoEntregableJpaEntity.builder()
                .id(entity.id())
                .artefactoId(entity.versionArtefacto())
                .entregableId(entity.entregableProyectoGrado())
                .build();
    }
}
