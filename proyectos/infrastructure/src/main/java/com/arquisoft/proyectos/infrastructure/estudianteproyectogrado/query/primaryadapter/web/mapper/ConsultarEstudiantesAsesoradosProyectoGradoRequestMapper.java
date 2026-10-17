package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesAsesoradosProyectoGradoQuery;

import java.util.UUID;

public final class ConsultarEstudiantesAsesoradosProyectoGradoRequestMapper {

    private ConsultarEstudiantesAsesoradosProyectoGradoRequestMapper() {}

    public static ConsultarEstudiantesAsesoradosProyectoGradoQuery toQuery(UUID proyectoGradoId, UUID asesor) {
        return ConsultarEstudiantesAsesoradosProyectoGradoQuery.crear(proyectoGradoId, asesor);
    }
}
