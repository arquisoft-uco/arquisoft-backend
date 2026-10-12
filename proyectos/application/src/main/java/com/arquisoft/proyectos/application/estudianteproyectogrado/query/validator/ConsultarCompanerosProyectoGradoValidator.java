package com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.CompaneroProyectoGradoCriteria;

public interface ConsultarCompanerosProyectoGradoValidator {

    void validar(CompaneroProyectoGradoCriteria criteria, boolean proyectoExiste, boolean vinculado);
}
