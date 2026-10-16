package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoEstudianteQuery;

import java.util.UUID;

public final class ConsultarProyectosGradoEstudianteRequestMapper {

    private ConsultarProyectosGradoEstudianteRequestMapper() {}

    public static ConsultarProyectosGradoEstudianteQuery toQuery(UUID estudiante) {
        return ConsultarProyectosGradoEstudianteQuery.crear(estudiante);
    }
}
