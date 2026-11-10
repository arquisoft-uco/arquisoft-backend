package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.PurgarVersionesProyectoGradoCommand;
import com.arquisoft.artefactos.domain.versionartefacto.PurgaVersionesProyectoGradoDomain;

public final class PurgarVersionesProyectoGradoMapper {

    private PurgarVersionesProyectoGradoMapper() {}

    public static PurgaVersionesProyectoGradoDomain toDomain(PurgarVersionesProyectoGradoCommand command) {
        return PurgaVersionesProyectoGradoDomain.crear(command.proyectoGrado());
    }
}
