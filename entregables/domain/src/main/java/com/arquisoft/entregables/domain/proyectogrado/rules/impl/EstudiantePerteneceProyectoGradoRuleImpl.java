package com.arquisoft.entregables.domain.proyectogrado.rules.impl;

import com.arquisoft.entregables.domain.proyectogrado.exception.EstudianteNoPerteneceProyectoGradoException;
import com.arquisoft.entregables.domain.proyectogrado.model.PertenenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.EstudiantePerteneceProyectoGradoRule;

public class EstudiantePerteneceProyectoGradoRuleImpl implements EstudiantePerteneceProyectoGradoRule {

    @Override
    public void validar(PertenenciaProyectoGrado pertenencia) {
        if (!pertenencia.pertenece()) {
            throw new EstudianteNoPerteneceProyectoGradoException(
                    pertenencia.proyectoGrado(), pertenencia.estudiante());
        }
    }
}
