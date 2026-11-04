package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.AsignarEstudiantesProyectoGradoCommand;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web.dto.AsignarEstudiantesProyectoGradoRequestDTO;

import java.util.UUID;

public final class AsignarEstudiantesProyectoGradoRequestMapper {

    private AsignarEstudiantesProyectoGradoRequestMapper() {}

    public static AsignarEstudiantesProyectoGradoCommand toCommand(
            AsignarEstudiantesProyectoGradoRequestDTO dto, UUID proyectoGrado, UUID coordinador) {
        return AsignarEstudiantesProyectoGradoCommand.crear(proyectoGrado, coordinador, dto.estudiantes());
    }
}
