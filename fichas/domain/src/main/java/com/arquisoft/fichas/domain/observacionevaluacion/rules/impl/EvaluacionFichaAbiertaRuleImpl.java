package com.arquisoft.fichas.domain.observacionevaluacion.rules.impl;

import com.arquisoft.fichas.domain.observacionevaluacion.exception.EvaluacionFichaCerradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.model.EstadoEvaluacionObservada;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.EvaluacionFichaAbiertaRule;

public class EvaluacionFichaAbiertaRuleImpl implements EvaluacionFichaAbiertaRule {

    @Override
    public void validar(EstadoEvaluacionObservada estado) {
        if (estado.ultimoEstado().esTerminal()) {
            throw new EvaluacionFichaCerradaException(
                    estado.evaluacionFichaPerfil(), estado.ultimoEstado().getId());
        }
    }
}
