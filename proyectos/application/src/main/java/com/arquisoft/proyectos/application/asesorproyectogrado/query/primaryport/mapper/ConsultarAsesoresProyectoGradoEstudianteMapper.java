package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoEstudianteQuery;

public final class ConsultarAsesoresProyectoGradoEstudianteMapper {

    private ConsultarAsesoresProyectoGradoEstudianteMapper() {}

    public static AsesorProyectoGradoEstudianteCriteria toCriteria(
            ConsultarAsesoresProyectoGradoEstudianteQuery query) {
        return new AsesorProyectoGradoEstudianteCriteria(query.proyectoGrado(), query.estudiante());
    }
}
