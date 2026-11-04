package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.repositorio_artefactos.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository.RepositorioArtefactoJpaQueryEntity;

public final class RepositorioArtefactoQueryMapper {

    private RepositorioArtefactoQueryMapper() {}

    public static RepositorioArtefactoReadModel toReadModel(RepositorioArtefactoJpaQueryEntity entity) {
        return new RepositorioArtefactoReadModel(
                entity.getId(),
                entity.getNombreRepositorio(),
                entity.getDescripcion(),
                new AdministradorReadModel(
                        entity.getAdministradorId(),
                        entity.getAdministradorIdentificador(),
                        entity.getAdministradorNombre(),
                        entity.getAdministradorEmail()));
    }
}
