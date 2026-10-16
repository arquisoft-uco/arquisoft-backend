package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.secondaryadapter.entity.RepositorioArtefactoJpaEntity;

public final class RepositorioArtefactoJpaMapper {

    private RepositorioArtefactoJpaMapper() {}

    public static RepositorioArtefactoEntity toEntity(RepositorioArtefactoJpaEntity jpaEntity) {
        return new RepositorioArtefactoEntity(
                jpaEntity.getId(),
                jpaEntity.getNombreRepositorio(),
                jpaEntity.getDescripcion(),
                jpaEntity.getAdministrador());
    }

    public static RepositorioArtefactoJpaEntity toJpaEntity(RepositorioArtefactoEntity entity) {
        return RepositorioArtefactoJpaEntity.builder()
                .id(entity.id())
                .nombreRepositorio(entity.nombreRepositorio())
                .descripcion(entity.descripcion())
                .administrador(entity.administrador())
                .build();
    }
}
