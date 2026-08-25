package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;

public class EstadoFichaPerfilEnTerminalRuleImpl implements EstadoFichaPerfilEnTerminalRule {

    @Override
    public void validar(EstadoActualFicha estado) {
        EstadoFicha estadoActual = estado.estadoActual();

        if (estadoActual == EstadoFicha.VACIO) {
            return;
        }

        if (estadoActual.esTerminal()) {
            throw new EstadoFichaPerfilTerminalException(estadoActual);
        }
    }
}
