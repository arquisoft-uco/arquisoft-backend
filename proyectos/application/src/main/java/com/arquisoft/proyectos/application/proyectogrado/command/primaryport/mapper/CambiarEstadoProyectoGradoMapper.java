package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.CambiarEstadoProyectoGradoCommand;
import com.arquisoft.proyectos.domain.proyectogrado.CambioEstadoProyectoGradoDomain;

public final class CambiarEstadoProyectoGradoMapper {

    private CambiarEstadoProyectoGradoMapper() {}

    public static CambioEstadoProyectoGradoDomain toDomain(CambiarEstadoProyectoGradoCommand command) {
        return CambioEstadoProyectoGradoDomain.crear(
                command.proyectoGrado(), command.asesor(), command.estadoProyecto());
    }
}
