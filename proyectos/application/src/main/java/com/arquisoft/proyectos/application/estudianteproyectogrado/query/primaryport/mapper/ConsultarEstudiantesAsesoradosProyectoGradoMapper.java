package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteAsesoradoProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesAsesoradosProyectoGradoQuery;

public final class ConsultarEstudiantesAsesoradosProyectoGradoMapper {

    private ConsultarEstudiantesAsesoradosProyectoGradoMapper() {}

    public static EstudianteAsesoradoProyectoGradoCriteria toCriteria(
            ConsultarEstudiantesAsesoradosProyectoGradoQuery query) {
        return new EstudianteAsesoradoProyectoGradoCriteria(query.proyectoGrado(), query.asesor());
    }
}
