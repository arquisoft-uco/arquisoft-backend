package com.arquisoft.proyectos.domain.estadoproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.EstadoProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.estadoproyectogrado.model.ExistenciaEstadoProyectoGrado;
import com.arquisoft.proyectos.domain.estadoproyectogrado.rules.EstadoProyectoGradoExisteRule;

public class EstadoProyectoGradoExisteRuleImpl implements EstadoProyectoGradoExisteRule {

    @Override
    public void validar(ExistenciaEstadoProyectoGrado existencia) {
        if (!existencia.existe()) {
            throw new EstadoProyectoGradoNoEncontradoException(existencia.proyectoGrado());
        }
    }
}
