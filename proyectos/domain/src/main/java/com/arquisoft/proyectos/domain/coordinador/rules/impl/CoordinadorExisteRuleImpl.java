package com.arquisoft.proyectos.domain.coordinador.rules.impl;

import com.arquisoft.proyectos.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.proyectos.domain.coordinador.model.ExistenciaCoordinador;
import com.arquisoft.proyectos.domain.coordinador.rules.CoordinadorExisteRule;

public class CoordinadorExisteRuleImpl implements CoordinadorExisteRule {

    @Override
    public void validar(ExistenciaCoordinador existencia) {
        if (!existencia.existe()) {
            throw new CoordinadorNoEncontradoException(existencia.coordinador());
        }
    }
}
