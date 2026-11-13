package com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.EvaluacionCuantitativaAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.ExistenciaEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.EvaluacionCuantitativaAsesorExisteRule;

public class EvaluacionCuantitativaAsesorExisteRuleImpl implements EvaluacionCuantitativaAsesorExisteRule {

    @Override
    public void validar(ExistenciaEvaluacionCuantitativaAsesor existencia) {
        if (!existencia.existe()) {
            throw new EvaluacionCuantitativaAsesorNoEncontradaException(existencia.evaluacionCuantitativaAsesor());
        }
    }
}
