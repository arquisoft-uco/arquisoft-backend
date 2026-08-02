package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.rules.FichaPerfilExisteRule;

import java.util.UUID;

public class FichaPerfilExisteRuleImpl implements FichaPerfilExisteRule {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    public FichaPerfilExisteRuleImpl(FichaPerfilOutputPort fichaPerfilOutputPort) {
        this.fichaPerfilOutputPort = fichaPerfilOutputPort;
    }

    @Override
    public void validar(UUID fichaPerfil) {
        if (!fichaPerfilOutputPort.existePorId(fichaPerfil)) {
            throw new FichaPerfilNoEncontradaException(fichaPerfil);
        }
    }
}
