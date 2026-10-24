package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.ModificarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;

public final class ModificarRepositorioArtefactoMapper {

    private ModificarRepositorioArtefactoMapper() {}

    public static ModificacionRepositorioArtefactoDomain toDomain(ModificarRepositorioArtefactoCommand command) {
        return ModificacionRepositorioArtefactoDomain.crear(
                command.repositorio(),
                command.nombreRepositorio(),
                command.descripcion(),
                command.administrador());
    }
}
