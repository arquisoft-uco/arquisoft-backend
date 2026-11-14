package com.arquisoft.entregables.application.artefacto.command.primaryport.mapper;

import com.arquisoft.entregables.application.artefacto.command.primaryport.model.PurgarArtefactosProyectoGradoCommand;
import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;

public final class PurgarArtefactosProyectoGradoMapper {

    private PurgarArtefactosProyectoGradoMapper() {}

    public static PurgaArtefactosProyectoGradoDomain toDomain(PurgarArtefactosProyectoGradoCommand command) {
        return PurgaArtefactosProyectoGradoDomain.crear(command.proyectoGrado());
    }
}
