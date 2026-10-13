package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilRepetidoException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.TransicionEstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilNoRepetidoRule;

public class EstadoFichaPerfilNoRepetidoRuleImpl implements EstadoFichaPerfilNoRepetidoRule {

    @Override
    public void validar(TransicionEstadoFicha transicion) {
        if (transicion.estadoActual() == transicion.estadoNuevo()) {
            throw new EstadoFichaPerfilRepetidoException(transicion.estadoNuevo());
        }
    }
}
