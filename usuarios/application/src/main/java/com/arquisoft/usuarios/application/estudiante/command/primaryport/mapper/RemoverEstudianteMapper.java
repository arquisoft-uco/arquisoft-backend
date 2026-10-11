package com.arquisoft.usuarios.application.estudiante.command.primaryport.mapper;

import com.arquisoft.usuarios.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;

public final class RemoverEstudianteMapper {

    private RemoverEstudianteMapper() {}

    public static EstudianteDomain toDomain(RemoverEstudianteCommand command) {
        return EstudianteDomain.crear(command.usuario());
    }
}
