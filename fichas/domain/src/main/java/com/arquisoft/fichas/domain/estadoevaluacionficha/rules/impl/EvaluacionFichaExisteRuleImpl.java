package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;

import java.util.UUID;

public class EvaluacionFichaExisteRuleImpl implements EvaluacionFichaExisteRule {

    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    public EvaluacionFichaExisteRuleImpl(EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort) {
        this.evaluacionFichaPerfilOutputPort = evaluacionFichaPerfilOutputPort;
    }

    @Override
    public void validar(UUID evaluacionFichaPerfil) {
        if (!evaluacionFichaPerfilOutputPort.existePorId(evaluacionFichaPerfil)) {
            throw new EvaluacionFichaPerfilNoEncontradaException(evaluacionFichaPerfil);
        }
    }
}
