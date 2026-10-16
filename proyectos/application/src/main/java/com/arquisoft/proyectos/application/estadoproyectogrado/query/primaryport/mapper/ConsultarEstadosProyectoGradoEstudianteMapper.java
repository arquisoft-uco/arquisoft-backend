package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.EstadoProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarEstadosProyectoGradoEstudianteQuery;

public final class ConsultarEstadosProyectoGradoEstudianteMapper {

    private ConsultarEstadosProyectoGradoEstudianteMapper() {}

    public static EstadoProyectoGradoEstudianteCriteria toCriteria(ConsultarEstadosProyectoGradoEstudianteQuery query) {
        return new EstadoProyectoGradoEstudianteCriteria(query.proyectoGrado(), query.estudiante());
    }
}
