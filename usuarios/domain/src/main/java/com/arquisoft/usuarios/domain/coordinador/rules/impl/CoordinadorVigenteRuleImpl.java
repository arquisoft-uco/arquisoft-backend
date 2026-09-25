package com.arquisoft.usuarios.domain.coordinador.rules.impl;

import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.usuarios.domain.coordinador.model.ExistenciaCoordinador;
import com.arquisoft.usuarios.domain.coordinador.rules.CoordinadorVigenteRule;

public class CoordinadorVigenteRuleImpl implements CoordinadorVigenteRule {

    @Override
    public void validar(ExistenciaCoordinador existencia) {
        var coordinador = existencia.coordinador();
        if (coordinador.esVacio() || coordinador.estaEliminado()) {
            throw new CoordinadorNoEncontradoException(existencia.usuario());
        }
    }
}
