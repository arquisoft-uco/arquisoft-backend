package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoEstudianteQuery;

import java.util.UUID;

public final class ConsultarAsesoresProyectoGradoEstudianteRequestMapper {

    private ConsultarAsesoresProyectoGradoEstudianteRequestMapper() {}

    public static ConsultarAsesoresProyectoGradoEstudianteQuery toQuery(UUID proyectoGradoId, UUID estudiante) {
        return ConsultarAsesoresProyectoGradoEstudianteQuery.crear(proyectoGradoId, estudiante);
    }
}
