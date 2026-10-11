package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ExistenciaAsesorProyectoGrado;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.AsesorProyectoGradoExisteRule;

public class AsesorProyectoGradoExisteRuleImpl implements AsesorProyectoGradoExisteRule {

    @Override
    public void validar(ExistenciaAsesorProyectoGrado existencia) {
        if (!existencia.existe()) {
            throw new AsesorProyectoGradoNoEncontradoException(
                    existencia.asesorProyectoGrado(), existencia.proyectoGrado());
        }
    }
}
