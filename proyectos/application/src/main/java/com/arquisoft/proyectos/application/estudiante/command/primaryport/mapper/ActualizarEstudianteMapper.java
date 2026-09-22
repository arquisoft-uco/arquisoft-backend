package com.arquisoft.proyectos.application.estudiante.command.primaryport.mapper;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.ActualizarEstudianteCommand;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;

public final class ActualizarEstudianteMapper {

    private ActualizarEstudianteMapper() {}

    public static EstudianteDomain toDomain(ActualizarEstudianteCommand command) {
        return EstudianteDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
