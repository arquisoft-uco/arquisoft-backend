package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.TransicionEstadoFichaNoPermitidaException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.TransicionEstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.TransicionEstadoFichaPermitidaRule;

public class TransicionEstadoFichaPermitidaRuleImpl implements TransicionEstadoFichaPermitidaRule {

    @Override
    public void validar(TransicionEstadoFicha transicion) {
        if (!transicion.estadoActual().permiteTransicionPorAsesorA(transicion.estadoNuevo())) {
            throw new TransicionEstadoFichaNoPermitidaException(transicion.estadoActual(), transicion.estadoNuevo());
        }
    }
}
