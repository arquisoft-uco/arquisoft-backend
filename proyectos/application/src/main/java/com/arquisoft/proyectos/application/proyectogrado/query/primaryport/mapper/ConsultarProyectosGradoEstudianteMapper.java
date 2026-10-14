package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoEstudianteQuery;

public final class ConsultarProyectosGradoEstudianteMapper {

    private ConsultarProyectosGradoEstudianteMapper() {}

    public static ProyectoGradoEstudianteCriteria toCriteria(ConsultarProyectosGradoEstudianteQuery query) {
        return new ProyectoGradoEstudianteCriteria(query.estudiante());
    }
}
