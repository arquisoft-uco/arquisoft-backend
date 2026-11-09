package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarCoordinadoresProyectoGradoEstudianteQuery;

import java.util.UUID;

public final class ConsultarCoordinadoresProyectoGradoEstudianteRequestMapper {

    private ConsultarCoordinadoresProyectoGradoEstudianteRequestMapper() {}

    public static ConsultarCoordinadoresProyectoGradoEstudianteQuery toQuery(UUID estudiante) {
        return ConsultarCoordinadoresProyectoGradoEstudianteQuery.crear(estudiante);
    }
}
