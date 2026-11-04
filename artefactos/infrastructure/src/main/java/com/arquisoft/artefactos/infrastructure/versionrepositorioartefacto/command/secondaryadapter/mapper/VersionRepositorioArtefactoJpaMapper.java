package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;

public final class VersionRepositorioArtefactoJpaMapper {

    private VersionRepositorioArtefactoJpaMapper() {}

    public static VersionRepositorioArtefactoEntity toEntity(VersionRepositorioArtefactoJpaEntity jpaEntity) {
        return new VersionRepositorioArtefactoEntity(
                jpaEntity.getId(),
                jpaEntity.getNombreDocumento(),
                jpaEntity.getVersionDocumento(),
                jpaEntity.getRepositorioArtefacto(),
                jpaEntity.getNombreRepositorio(),
                jpaEntity.getNombreArchivo(),
                jpaEntity.getTipoContenido(),
                jpaEntity.getTamanoBytes());
    }

    public static VersionRepositorioArtefactoJpaEntity toJpaEntity(VersionRepositorioArtefactoEntity entity) {
        return VersionRepositorioArtefactoJpaEntity.builder()
                .id(entity.id())
                .nombreDocumento(entity.nombreDocumento())
                .versionDocumento(entity.versionDocumento())
                .repositorioArtefacto(entity.repositorioArtefacto())
                .nombreRepositorio(entity.nombreRepositorio())
                .nombreArchivo(entity.nombreArchivo())
                .tipoContenido(entity.tipoContenido())
                .tamanoBytes(entity.tamanoBytes())
                .build();
    }
}
