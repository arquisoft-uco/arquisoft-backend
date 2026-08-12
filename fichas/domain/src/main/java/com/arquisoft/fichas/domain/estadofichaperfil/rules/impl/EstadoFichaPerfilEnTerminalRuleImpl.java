package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;

public class EstadoFichaPerfilEnTerminalRuleImpl implements EstadoFichaPerfilEnTerminalRule {

    @Override
    public void validar(EstadoActualFicha estado) {
        var estadoActual = estado.estadoActual()
                .orElseThrow(() -> new EstadoFichaPerfilNoEncontradoException(estado.fichaPerfil()));

        if (estadoActual.esTerminal()) {
            throw new EstadoFichaPerfilTerminalException(estadoActual);
        }
    }
}
