package com.arquisoft.proyectos.domain.asesor.rules.impl;

import com.arquisoft.proyectos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.proyectos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.proyectos.domain.asesor.rules.AsesorExisteRule;

public class AsesorExisteRuleImpl implements AsesorExisteRule {

    @Override
    public void validar(ExistenciaAsesor existencia) {
        if (!existencia.existe()) {
            throw new AsesorNoEncontradoException(existencia.asesor());
        }
    }
}
