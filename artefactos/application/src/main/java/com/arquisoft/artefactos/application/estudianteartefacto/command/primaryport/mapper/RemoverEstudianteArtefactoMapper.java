package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.RemoverEstudianteArtefactoCommand;
import com.arquisoft.artefactos.domain.estudianteartefacto.RemocionEstudianteArtefactoDomain;

public final class RemoverEstudianteArtefactoMapper {

    private RemoverEstudianteArtefactoMapper() {}

    public static RemocionEstudianteArtefactoDomain toDomain(RemoverEstudianteArtefactoCommand command) {
        return RemocionEstudianteArtefactoDomain.crear(command.artefacto(), command.asesor(), command.estudiante());
    }
}
