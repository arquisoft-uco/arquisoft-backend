package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.secondaryadapter.entity.VersionRepositorioArtefactoJpaEntity;

public final class VersionRepositorioArtefactoJpaMapper {

    private VersionRepositorioArtefactoJpaMapper() {}

    public static VersionRepositorioArtefactoJpaEntity toJpaEntity(VersionRepositorioArtefactoEntity entity) {
        return VersionRepositorioArtefactoJpaEntity.builder()
                .id(entity.id())
                .nombreDocumento(entity.nombreDocumento())
                .numeroVersion(entity.numeroVersion())
                .fechaCreacion(entity.fechaCreacion())
                .repositorioArtefacto(entity.repositorioArtefacto())
                .uriArchivo(entity.uriArchivo())
                .tipoContenido(entity.tipoContenido())
                .tamanoBytes(entity.tamanoBytes())
                .nombreArchivo(entity.nombreArchivo())
                .hashSha256(entity.hashSha256())
                .build();
    }

    public static VersionRepositorioArtefactoEntity toEntity(VersionRepositorioArtefactoJpaEntity jpaEntity) {
        return new VersionRepositorioArtefactoEntity(
                jpaEntity.getId(),
                jpaEntity.getNombreDocumento(),
                jpaEntity.getNumeroVersion(),
                jpaEntity.getFechaCreacion(),
                jpaEntity.getRepositorioArtefacto(),
                jpaEntity.getUriArchivo(),
                jpaEntity.getTipoContenido(),
                jpaEntity.getTamanoBytes(),
                jpaEntity.getNombreArchivo(),
                jpaEntity.getHashSha256());
    }
}
