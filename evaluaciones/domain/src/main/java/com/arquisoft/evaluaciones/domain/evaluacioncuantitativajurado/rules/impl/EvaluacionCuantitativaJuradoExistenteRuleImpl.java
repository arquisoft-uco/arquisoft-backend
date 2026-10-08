package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionCuantitativaJuradoExistenteRule;

public class EvaluacionCuantitativaJuradoExistenteRuleImpl implements EvaluacionCuantitativaJuradoExistenteRule {

    @Override
    public void validar(ExistenciaEvaluacionCuantitativaJurado existencia) {
        if (!existencia.existe()) {
            throw new EvaluacionCuantitativaJuradoNoEncontradaException(existencia.evaluacionCuantitativaJurado());
        }
    }
}
