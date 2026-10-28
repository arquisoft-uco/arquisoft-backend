package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.AsignarEstudiantesArtefactoCommand;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;

public final class AsignarEstudiantesArtefactoMapper {

    private AsignarEstudiantesArtefactoMapper() {}

    public static AgregacionEstudiantesArtefactoDomain toDomain(AsignarEstudiantesArtefactoCommand command) {
        var relaciones = EstudianteArtefactoDomain.crear(command.artefacto(), command.estudiantes());
        return AgregacionEstudiantesArtefactoDomain.crear(command.asesor(), relaciones);
    }
}
