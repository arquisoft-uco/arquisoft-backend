package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ExistenciaVinculoEstudianteProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudianteVinculadoProyectoGradoRule;

public class EstudianteVinculadoProyectoGradoRuleImpl implements EstudianteVinculadoProyectoGradoRule {

    @Override
    public void validar(ExistenciaVinculoEstudianteProyectoGrado existencia) {
        if (!existencia.vinculado()) {
            throw new EstudianteNoVinculadoException(existencia.estudiante(), existencia.proyectoGrado());
        }
    }
}
