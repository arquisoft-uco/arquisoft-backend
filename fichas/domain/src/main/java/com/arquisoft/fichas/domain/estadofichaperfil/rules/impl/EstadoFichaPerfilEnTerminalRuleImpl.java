package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.shared.util.UtilText;

public class EstadoFichaPerfilEnTerminalRuleImpl implements EstadoFichaPerfilEnTerminalRule {

    @Override
    public void validar(EstadoActualFicha estado) {
        EstadoFicha estadoActual = convertir(estado.estadoActual());

        if (estadoActual == EstadoFicha.VACIO) {
            throw new EstadoFichaPerfilNoEncontradoException(estado.fichaPerfil());
        }

        if (estadoActual.esTerminal()) {
            throw new EstadoFichaPerfilTerminalException(estadoActual);
        }
    }

    private EstadoFicha convertir(String estadoActual) {
        if (UtilText.isEmptyOrNull(estadoActual)) {
            return EstadoFicha.VACIO;
        }
        try {
            return EstadoFicha.valueOf(estadoActual);
        } catch (IllegalArgumentException ex) {
            return EstadoFicha.VACIO;
        }
    }
}
