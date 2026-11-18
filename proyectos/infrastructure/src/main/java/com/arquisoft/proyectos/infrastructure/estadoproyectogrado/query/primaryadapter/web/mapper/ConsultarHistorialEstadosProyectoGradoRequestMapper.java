package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarHistorialEstadosProyectoGradoQuery;

import java.util.UUID;

public final class ConsultarHistorialEstadosProyectoGradoRequestMapper {

    private ConsultarHistorialEstadosProyectoGradoRequestMapper() {}

    public static ConsultarHistorialEstadosProyectoGradoQuery toQuery(UUID proyectoGradoId, UUID usuario) {
        return ConsultarHistorialEstadosProyectoGradoQuery.crear(proyectoGradoId, usuario);
    }
}
