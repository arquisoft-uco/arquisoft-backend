package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.ExistenciaEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.EvaluacionCuantitativaJuradoExisteRule;

public class EvaluacionCuantitativaJuradoExisteRuleImpl implements EvaluacionCuantitativaJuradoExisteRule {

    @Override
    public void validar(ExistenciaEvaluacionCuantitativaJurado existencia) {
        if (!existencia.existe()) {
            throw new EvaluacionCuantitativaJuradoNoEncontradaException(existencia.evaluacionCuantitativaJurado());
        }
    }
}
