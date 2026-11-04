package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.CambiarNombreRepositorioVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.CambioNombreRepositorioDomain;

public final class CambiarNombreRepositorioVersionRepositorioArtefactoMapper {

    private CambiarNombreRepositorioVersionRepositorioArtefactoMapper() {}

    public static CambioNombreRepositorioDomain toDomain(
            CambiarNombreRepositorioVersionRepositorioArtefactoCommand command) {
        return CambioNombreRepositorioDomain.crear(command.repositorioArtefacto(), command.nombreRepositorio());
    }
}
