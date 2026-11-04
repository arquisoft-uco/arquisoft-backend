package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;

public final class RepositorioArtefactoMapper {

    private RepositorioArtefactoMapper() {}

    public static RepositorioArtefactoEntity toEntity(RepositorioArtefactoDomain repositorio) {
        return new RepositorioArtefactoEntity(
                repositorio.getId(),
                repositorio.getNombreRepositorio(),
                repositorio.getDescripcion(),
                repositorio.getAdministrador());
    }

    public static RepositorioArtefactoDomain toDomain(RepositorioArtefactoEntity entity) {
        return RepositorioArtefactoDomain.reconstruir(
                entity.id(),
                entity.nombreRepositorio(),
                entity.descripcion(),
                entity.administrador());
    }
}
