package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.CoordinadorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarCoordinadoresProyectoGradoEstudianteQuery;

public final class ConsultarCoordinadoresProyectoGradoEstudianteMapper {

    private ConsultarCoordinadoresProyectoGradoEstudianteMapper() {}

    public static CoordinadorProyectoGradoEstudianteCriteria toCriteria(
            ConsultarCoordinadoresProyectoGradoEstudianteQuery query) {
        return new CoordinadorProyectoGradoEstudianteCriteria(query.estudiante());
    }
}
