package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;

import java.util.UUID;

public final class DestituirEstudianteProyectoGradoRequestMapper {

    private DestituirEstudianteProyectoGradoRequestMapper() {}

    public static DestituirEstudianteProyectoGradoCommand toCommand(UUID proyectoGrado, UUID estudiante,
                                                                    UUID coordinador) {
        return DestituirEstudianteProyectoGradoCommand.crear(proyectoGrado, estudiante, coordinador);
    }
}
