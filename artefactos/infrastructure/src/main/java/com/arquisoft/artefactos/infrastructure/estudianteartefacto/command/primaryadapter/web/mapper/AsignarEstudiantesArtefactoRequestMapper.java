package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.AsignarEstudiantesArtefactoCommand;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web.dto.AsignarEstudiantesArtefactoRequestDTO;

import java.util.UUID;

public final class AsignarEstudiantesArtefactoRequestMapper {

    private AsignarEstudiantesArtefactoRequestMapper() {}

    public static AsignarEstudiantesArtefactoCommand toCommand(
            UUID artefacto, AsignarEstudiantesArtefactoRequestDTO dto, UUID asesor) {
        return AsignarEstudiantesArtefactoCommand.crear(artefacto, asesor, dto.estudiantes());
    }
}
