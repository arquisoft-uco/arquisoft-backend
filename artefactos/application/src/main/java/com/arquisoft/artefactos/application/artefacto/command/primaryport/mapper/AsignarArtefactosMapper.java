package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactosCommand;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactosDomain;

public final class AsignarArtefactosMapper {

    private AsignarArtefactosMapper() {}

    public static AsignacionArtefactosDomain toDomain(AsignarArtefactosCommand command) {
        var artefactos = ArtefactoDomain.crear(
                command.proyectoGrado(), command.asesor(), command.versionesRepositorioArtefacto());
        return AsignacionArtefactosDomain.crear(artefactos);
    }
}
