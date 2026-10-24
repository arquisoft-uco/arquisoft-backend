package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactosCommand;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactosDomain;

public final class AsignarArtefactosMapper {

    private AsignarArtefactosMapper() {}

    public static AsignacionArtefactosDomain toDomain(AsignarArtefactosCommand command) {
        var artefactos = command.versionesRepositorioArtefacto().stream()
                .map(version -> ArtefactoDomain.crear(command.proyectoGrado(), version, command.asesor()))
                .toList();
        return AsignacionArtefactosDomain.crear(command.proyectoGrado(), command.asesor(), artefactos);
    }
}
