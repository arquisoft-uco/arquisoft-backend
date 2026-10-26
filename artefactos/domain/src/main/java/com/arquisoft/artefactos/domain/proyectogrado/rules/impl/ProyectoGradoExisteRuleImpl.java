package com.arquisoft.artefactos.domain.proyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoExisteRule;

public class ProyectoGradoExisteRuleImpl implements ProyectoGradoExisteRule {

    @Override
    public void validar(ExistenciaProyectoGrado existencia) {
        if (!existencia.existe()) {
            throw new ProyectoGradoNoEncontradoException(existencia.proyectoGrado());
        }
    }
}
