package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactoCommand;
import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper.AsignarEstudiantesArtefactoMapper;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;

public final class AsignarArtefactoMapper {

    private AsignarArtefactoMapper() {}

    public static AsignacionArtefactoDomain toDomain(AsignarArtefactoCommand command) {
        var artefacto = ArtefactoDomain.crear(
                command.proyectoGrado(), command.asesor(), command.versionRepositorioArtefacto());
        var estudiantes = AsignarEstudiantesArtefactoMapper.toDomain(
                artefacto.getId(), command.asesor(), command.estudiantes());
        return AsignacionArtefactoDomain.crear(artefacto, estudiantes);
    }
}
