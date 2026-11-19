package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.AtrasarProyectoGradoCommand;
import com.arquisoft.proyectos.domain.proyectogrado.AtrasoProyectoGradoDomain;

public final class AtrasarProyectoGradoMapper {

    private AtrasarProyectoGradoMapper() {}

    public static AtrasoProyectoGradoDomain toDomain(AtrasarProyectoGradoCommand command) {
        return AtrasoProyectoGradoDomain.crear(command.proyectoGrado());
    }
}
