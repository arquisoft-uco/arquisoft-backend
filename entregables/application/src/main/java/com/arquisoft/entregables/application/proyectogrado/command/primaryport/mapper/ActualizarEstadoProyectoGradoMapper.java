package com.arquisoft.entregables.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.entregables.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;

public final class ActualizarEstadoProyectoGradoMapper {

    private ActualizarEstadoProyectoGradoMapper() {}

    public static ActualizacionEstadoProyectoGradoDomain toDomain(ActualizarEstadoProyectoGradoCommand command) {
        return ActualizacionEstadoProyectoGradoDomain.crear(
                command.proyectoGrado(), command.estadoNuevo(), command.ocurridoEn());
    }
}
