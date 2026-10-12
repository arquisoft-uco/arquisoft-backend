package com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.EstudianteAsesoradoProyectoGradoCriteria;

public interface ConsultarEstudiantesAsesoradosProyectoGradoValidator {

    void validar(EstudianteAsesoradoProyectoGradoCriteria criteria, boolean proyectoExiste, boolean asesorAsignado);
}
