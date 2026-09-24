package com.arquisoft.fichas.application.estudiante.command.primaryport.mapper;

import com.arquisoft.fichas.application.estudiante.command.primaryport.model.ActualizarEstudianteCommand;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;

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
