package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarCompanerosProyectoGradoQuery;

import java.util.UUID;

public final class ConsultarCompanerosProyectoGradoRequestMapper {

    private ConsultarCompanerosProyectoGradoRequestMapper() {}

    public static ConsultarCompanerosProyectoGradoQuery toQuery(UUID proyectoGradoId, UUID estudiante) {
        return ConsultarCompanerosProyectoGradoQuery.crear(proyectoGradoId, estudiante);
    }
}
