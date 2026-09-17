package com.arquisoft.proyectos.application.estudiante.command.primaryport.mapper;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;

public final class RemoverEstudianteMapper {

    private RemoverEstudianteMapper() {}

    public static EstudianteDomain toDomain(RemoverEstudianteCommand command) {
        return EstudianteDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
