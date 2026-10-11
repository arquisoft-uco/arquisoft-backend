package com.arquisoft.proyectos.application.estudianteproyectogrado.command.validator;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.proyectos.domain.proyectogrado.ProyectoGradoDomain;

public interface DestituirEstudianteProyectoGradoValidator {

    void validar(DestitucionEstudianteProyectoGradoDomain entrada, ProyectoGradoDomain proyecto, boolean vinculado);
}
