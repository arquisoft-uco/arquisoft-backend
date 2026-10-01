package com.arquisoft.proyectos.application.estudiante.command.primaryport.mapper;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;

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
