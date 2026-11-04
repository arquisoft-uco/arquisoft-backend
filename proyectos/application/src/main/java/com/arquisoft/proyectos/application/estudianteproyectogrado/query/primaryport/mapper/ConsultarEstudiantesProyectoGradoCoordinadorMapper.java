package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesProyectoGradoCoordinadorQuery;

public final class ConsultarEstudiantesProyectoGradoCoordinadorMapper {

    private ConsultarEstudiantesProyectoGradoCoordinadorMapper() {}

    public static EstudianteProyectoGradoCoordinadorCriteria toCriteria(
            ConsultarEstudiantesProyectoGradoCoordinadorQuery query) {
        return new EstudianteProyectoGradoCoordinadorCriteria(query.proyectoGrado(), query.coordinador());
    }
}
