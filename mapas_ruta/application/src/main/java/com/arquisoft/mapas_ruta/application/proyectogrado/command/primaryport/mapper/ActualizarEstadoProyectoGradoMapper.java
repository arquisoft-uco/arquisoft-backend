package com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;

public final class ActualizarEstadoProyectoGradoMapper {

    private ActualizarEstadoProyectoGradoMapper() {}

    public static ActualizacionEstadoProyectoGradoDomain toDomain(ActualizarEstadoProyectoGradoCommand command) {
        return ActualizacionEstadoProyectoGradoDomain.crear(
                command.proyectoGrado(), command.estadoNuevo(), command.ocurridoEn());
    }
}
