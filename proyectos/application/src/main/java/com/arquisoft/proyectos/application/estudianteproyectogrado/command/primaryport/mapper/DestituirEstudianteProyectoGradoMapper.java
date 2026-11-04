package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;

public final class DestituirEstudianteProyectoGradoMapper {

    private DestituirEstudianteProyectoGradoMapper() {}

    public static DestitucionEstudianteProyectoGradoDomain toDomain(DestituirEstudianteProyectoGradoCommand command) {
        return DestitucionEstudianteProyectoGradoDomain.crear(
                command.proyectoGrado(), command.estudiante(), command.coordinador());
    }
}
