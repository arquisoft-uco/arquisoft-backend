package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionCualitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionCualitativaJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionCualitativaJuradoExistenteRule;

public class EvaluacionCualitativaJuradoExistenteRuleImpl implements EvaluacionCualitativaJuradoExistenteRule {

    @Override
    public void validar(ExistenciaEvaluacionCualitativaJurado existencia) {
        if (!existencia.existe()) {
            throw new EvaluacionCualitativaJuradoNoEncontradaException(existencia.evaluacionCualitativaJurado());
        }
    }
}
