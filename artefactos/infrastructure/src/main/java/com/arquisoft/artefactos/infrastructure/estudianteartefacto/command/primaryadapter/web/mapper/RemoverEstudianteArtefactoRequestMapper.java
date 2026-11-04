package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.RemoverEstudianteArtefactoCommand;

import java.util.UUID;

public final class RemoverEstudianteArtefactoRequestMapper {

    private RemoverEstudianteArtefactoRequestMapper() {}

    public static RemoverEstudianteArtefactoCommand toCommand(UUID artefacto, UUID estudiante, UUID asesor) {
        return RemoverEstudianteArtefactoCommand.crear(artefacto, asesor, estudiante);
    }
}
