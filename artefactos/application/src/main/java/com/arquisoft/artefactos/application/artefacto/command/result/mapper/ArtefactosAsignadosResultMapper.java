package com.arquisoft.artefactos.application.artefacto.command.result.mapper;

import com.arquisoft.artefactos.application.artefacto.command.result.ArtefactosAsignadosResult;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactosDomain;

public final class ArtefactosAsignadosResultMapper {

    private ArtefactosAsignadosResultMapper() {}

    public static ArtefactosAsignadosResult toResult(AsignacionArtefactosDomain asignacion) {
        return new ArtefactosAsignadosResult(
                asignacion.getArtefactos().stream().map(ArtefactoDomain::getId).toList());
    }
}
