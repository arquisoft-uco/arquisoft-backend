package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoExisteRule;

public class ProyectoGradoExisteRuleImpl implements ProyectoGradoExisteRule {

    @Override
    public void validar(ExistenciaProyectoGrado existencia) {
        if (!existencia.existe()) {
            throw new ProyectoGradoNoEncontradoException(existencia.proyectoGrado());
        }
    }
}
