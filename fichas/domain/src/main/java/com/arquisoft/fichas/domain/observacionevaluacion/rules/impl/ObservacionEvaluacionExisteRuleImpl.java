package com.arquisoft.fichas.domain.observacionevaluacion.rules.impl;

import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionNoEncontradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.model.ExistenciaObservacionEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.ObservacionEvaluacionExisteRule;

public class ObservacionEvaluacionExisteRuleImpl implements ObservacionEvaluacionExisteRule {

    @Override
    public void validar(ExistenciaObservacionEvaluacion existencia) {
        if (!existencia.existe()) {
            throw new ObservacionEvaluacionNoEncontradaException(existencia.observacionEvaluacion());
        }
    }
}
