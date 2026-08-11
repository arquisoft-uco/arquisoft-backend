package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.asesorficha.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;

import java.util.UUID;

public class AsesorFichaExisteRuleImpl implements AsesorFichaExisteRule {

    private final AsesorFichaOutputPort asesorFichaOutputPort;

    public AsesorFichaExisteRuleImpl(AsesorFichaOutputPort asesorFichaOutputPort) {
        this.asesorFichaOutputPort = asesorFichaOutputPort;
    }

    @Override
    public void validar(UUID asesorFicha) {
        if (!asesorFichaOutputPort.existePorId(asesorFicha)) {
            throw new AsesorFichaNoEncontradoException(asesorFicha);
        }
    }
}
