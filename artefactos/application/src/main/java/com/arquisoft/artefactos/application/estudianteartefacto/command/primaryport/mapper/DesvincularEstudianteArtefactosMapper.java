package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.domain.estudianteartefacto.DesvinculacionEstudianteArtefactosDomain;

import java.util.UUID;

public final class DesvincularEstudianteArtefactosMapper {

    private DesvincularEstudianteArtefactosMapper() {}

    public static DesvinculacionEstudianteArtefactosDomain toDomain(UUID proyectoGrado, UUID estudiante) {
        return DesvinculacionEstudianteArtefactosDomain.crear(proyectoGrado, estudiante);
    }
}
