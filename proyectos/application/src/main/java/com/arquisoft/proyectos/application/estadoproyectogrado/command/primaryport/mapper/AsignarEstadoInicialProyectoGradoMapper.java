package com.arquisoft.proyectos.application.estadoproyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGradoDomain;

import java.util.UUID;

public final class AsignarEstadoInicialProyectoGradoMapper {

    private AsignarEstadoInicialProyectoGradoMapper() {}

    public static EstadoProyectoGradoDomain toDomain(UUID proyectoGrado) {
        return EstadoProyectoGradoDomain.crear(proyectoGrado);
    }
}
