package com.arquisoft.proyectos.application.asesorproyectogrado.query.validator;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoEstudianteCriteria;

public interface ConsultarAsesoresProyectoGradoEstudianteValidator {

    void validar(AsesorProyectoGradoEstudianteCriteria criteria, boolean proyectoExiste, boolean vinculado);
}
