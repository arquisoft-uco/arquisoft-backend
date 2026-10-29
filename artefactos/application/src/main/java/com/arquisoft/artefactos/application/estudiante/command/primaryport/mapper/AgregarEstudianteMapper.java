package com.arquisoft.artefactos.application.estudiante.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;

public final class AgregarEstudianteMapper {

    private AgregarEstudianteMapper() {}

    public static EstudianteDomain toDomain(AgregarEstudianteCommand command) {
        return EstudianteDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
