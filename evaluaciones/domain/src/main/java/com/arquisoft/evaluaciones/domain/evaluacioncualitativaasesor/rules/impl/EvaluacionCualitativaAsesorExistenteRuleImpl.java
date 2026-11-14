package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionCualitativaAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionCualitativaAsesorExistenteRule;

public class EvaluacionCualitativaAsesorExistenteRuleImpl implements EvaluacionCualitativaAsesorExistenteRule {

    @Override
    public void validar(ExistenciaEvaluacionCualitativaAsesor existencia) {
        if (!existencia.existeContexto() || !existencia.existeRegistro()) {
            throw new EvaluacionCualitativaAsesorNoEncontradaException(existencia.evaluacionCualitativaAsesor());
        }
    }
}
