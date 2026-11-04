package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.GenerarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;

public final class GenerarRepositorioArtefactoMapper {

    private GenerarRepositorioArtefactoMapper() {}

    public static RepositorioArtefactoDomain toDomain(GenerarRepositorioArtefactoCommand command) {
        return RepositorioArtefactoDomain.crear(
                command.nombreRepositorio(),
                command.descripcion(),
                command.administrador());
    }
}
