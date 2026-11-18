package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.EvaluacionCuantitativaAsesorInexistenteException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaEvaluacionCuantitativaAsesorPorId;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionCuantitativaAsesorExistenteRule;

public class EvaluacionCuantitativaAsesorExistenteRuleImpl implements EvaluacionCuantitativaAsesorExistenteRule {

    @Override
    public void validar(ExistenciaEvaluacionCuantitativaAsesorPorId existencia) {
        if (!existencia.existe()) {
            throw new EvaluacionCuantitativaAsesorInexistenteException(existencia.evaluacionCuantitativaAsesor());
        }
    }
}
