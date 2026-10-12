package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarEstadosProyectoGradoEstudianteQuery;

import java.util.UUID;

public final class ConsultarEstadosProyectoGradoEstudianteRequestMapper {

    private ConsultarEstadosProyectoGradoEstudianteRequestMapper() {}

    public static ConsultarEstadosProyectoGradoEstudianteQuery toQuery(UUID proyectoGradoId, UUID estudiante) {
        return ConsultarEstadosProyectoGradoEstudianteQuery.crear(proyectoGradoId, estudiante);
    }
}
