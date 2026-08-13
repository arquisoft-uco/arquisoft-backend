package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionTerminalException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.UltimoEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionEnTerminalRule;

public class EstadoEvaluacionEnTerminalRuleImpl implements EstadoEvaluacionEnTerminalRule {

    @Override
    public void validar(UltimoEstadoEvaluacion ultimo) {
        EstadoEvaluacion ultimoEstado = ultimo.ultimoEstado();

        if (ultimoEstado == EstadoEvaluacion.VACIO) {
            return;
        }

        if (ultimoEstado.esTerminal()) {
            throw new EstadoEvaluacionTerminalException();
        }
    }
}
