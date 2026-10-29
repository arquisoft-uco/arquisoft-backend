package com.arquisoft.entregables.domain.proyectogrado.rules.impl;

import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.entregables.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoExisteRule;

public class ProyectoGradoExisteRuleImpl implements ProyectoGradoExisteRule {

    @Override
    public void validar(ExistenciaProyectoGrado existencia) {
        if (!existencia.existe()) {
            throw new ProyectoGradoNoEncontradoException(existencia.proyectoGrado());
        }
    }
}
