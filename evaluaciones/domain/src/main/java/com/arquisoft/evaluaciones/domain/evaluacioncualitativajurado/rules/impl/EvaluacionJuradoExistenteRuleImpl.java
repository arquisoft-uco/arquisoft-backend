package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoExistenteRule;

public class EvaluacionJuradoExistenteRuleImpl implements EvaluacionJuradoExistenteRule {

    @Override
    public void validar(ExistenciaEvaluacionJurado existencia) {
        if (!existencia.existe()) {
            throw new EvaluacionJuradoNoEncontradaException(existencia.evaluacionJurado());
        }
    }
}
