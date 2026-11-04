package com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

public final class RemoverEstudianteProyectoGradoMapper {

    private RemoverEstudianteProyectoGradoMapper() {}

    public static EstudianteProyectoGradoDomain toDomain(RemoverEstudianteProyectoGradoCommand command) {
        return EstudianteProyectoGradoDomain.crear(
                command.estudiante(), command.proyectoGrado(), command.ocurridoEn());
    }
}
