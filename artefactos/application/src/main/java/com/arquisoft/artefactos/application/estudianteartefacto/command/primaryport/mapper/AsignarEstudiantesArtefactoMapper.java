package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.AsignarEstudiantesArtefactoCommand;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;
import java.util.UUID;

public final class AsignarEstudiantesArtefactoMapper {

    private AsignarEstudiantesArtefactoMapper() {}

    public static AgregacionEstudiantesArtefactoDomain toDomain(AsignarEstudiantesArtefactoCommand command) {
        return toDomain(command.asesor(), List.of(command.artefacto()), command.estudiantes());
    }

    public static AgregacionEstudiantesArtefactoDomain toDomain(
            UUID asesor, List<UUID> artefactos, List<UUID> estudiantes) {
        if (UtilColeccion.esVaciaONula(estudiantes)) {
            return AgregacionEstudiantesArtefactoDomain.VACIO;
        }
        var relaciones = EstudianteArtefactoDomain.crear(artefactos, estudiantes);
        return AgregacionEstudiantesArtefactoDomain.crear(asesor, relaciones);
    }
}
