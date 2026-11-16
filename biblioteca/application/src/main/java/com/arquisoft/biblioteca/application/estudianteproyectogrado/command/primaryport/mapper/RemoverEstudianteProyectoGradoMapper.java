package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

public final class RemoverEstudianteProyectoGradoMapper {

    private RemoverEstudianteProyectoGradoMapper() {}

    public static EstudianteProyectoGradoDomain toDomain(RemoverEstudianteProyectoGradoCommand command) {
        return EstudianteProyectoGradoDomain.crear(
                command.estudiante(), command.proyectoGrado(), command.ocurridoEn());
    }
}
