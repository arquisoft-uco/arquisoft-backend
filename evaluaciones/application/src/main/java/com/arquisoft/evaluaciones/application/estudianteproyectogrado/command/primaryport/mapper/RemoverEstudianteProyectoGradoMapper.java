package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

public final class RemoverEstudianteProyectoGradoMapper {

    private RemoverEstudianteProyectoGradoMapper() {}

    public static EstudianteProyectoGradoDomain toDomain(RemoverEstudianteProyectoGradoCommand command) {
        return EstudianteProyectoGradoDomain.crear(
                command.estudiante(), command.proyectoGrado(), command.ocurridoEn());
    }
}
