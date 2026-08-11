package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.estadofichaperfil.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;

import java.util.UUID;

public class EstadoFichaPerfilEnTerminalRuleImpl implements EstadoFichaPerfilEnTerminalRule {

    private final EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    public EstadoFichaPerfilEnTerminalRuleImpl(EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort) {
        this.estadoFichaPerfilOutputPort = estadoFichaPerfilOutputPort;
    }

    @Override
    public void validar(UUID entrada) {
        var estadoActual = estadoFichaPerfilOutputPort.obtenerEstadoActual(entrada)
                .orElseThrow(() -> new EstadoFichaPerfilNoEncontradoException(entrada));

        if (estadoActual.esTerminal()) {
            throw new EstadoFichaPerfilTerminalException(estadoActual);
        }
    }
}
