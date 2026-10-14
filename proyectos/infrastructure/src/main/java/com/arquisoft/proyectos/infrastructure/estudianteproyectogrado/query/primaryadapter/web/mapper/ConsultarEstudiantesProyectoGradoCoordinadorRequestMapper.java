package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesProyectoGradoCoordinadorQuery;

import java.util.UUID;

public final class ConsultarEstudiantesProyectoGradoCoordinadorRequestMapper {

    private ConsultarEstudiantesProyectoGradoCoordinadorRequestMapper() {}

    public static ConsultarEstudiantesProyectoGradoCoordinadorQuery toQuery(UUID proyectoGradoId, UUID coordinador) {
        return ConsultarEstudiantesProyectoGradoCoordinadorQuery.crear(proyectoGradoId, coordinador);
    }
}
