package com.arquisoft.proyectos.application.estadoproyectogrado.query.validator;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.EstadoProyectoGradoEstudianteCriteria;

public interface ConsultarEstadosProyectoGradoEstudianteValidator {

    void validar(EstadoProyectoGradoEstudianteCriteria criteria, boolean proyectoExiste, boolean vinculado);
}
