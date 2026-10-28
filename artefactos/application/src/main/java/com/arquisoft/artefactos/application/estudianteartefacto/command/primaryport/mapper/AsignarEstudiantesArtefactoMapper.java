package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.AsignarEstudiantesArtefactoCommand;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;

import java.util.List;
import java.util.UUID;

public final class AsignarEstudiantesArtefactoMapper {

    private AsignarEstudiantesArtefactoMapper() {}

    public static AgregacionEstudiantesArtefactoDomain toDomain(AsignarEstudiantesArtefactoCommand command) {
        return toDomain(command.artefacto(), command.asesor(), command.estudiantes());
    }

    public static AgregacionEstudiantesArtefactoDomain toDomain(UUID artefacto, UUID asesor, List<UUID> estudiantes) {
        var relaciones = EstudianteArtefactoDomain.crear(artefacto, estudiantes);
        return AgregacionEstudiantesArtefactoDomain.crear(asesor, relaciones);
    }
}
