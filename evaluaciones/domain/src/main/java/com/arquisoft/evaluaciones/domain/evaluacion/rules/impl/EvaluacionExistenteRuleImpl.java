package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionExistenteRule;

public class EvaluacionExistenteRuleImpl implements EvaluacionExistenteRule {

    @Override
    public void validar(ExistenciaEvaluacion existencia) {
        if (!existencia.existe()) {
            throw new EvaluacionNoEncontradaException(existencia.evaluacion());
        }
    }
}
