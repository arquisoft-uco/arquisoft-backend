package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.EstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.EvaluacionJuradoFinalizadaRule;

public class EvaluacionJuradoFinalizadaRuleImpl implements EvaluacionJuradoFinalizadaRule {

    @Override
    public void validar(EstadoEvaluacionJurado estado) {
        if (estado.finalizada()) {
            throw new EvaluacionJuradoFinalizadaException(estado.evaluacionCuantitativaJurado());
        }
    }
}
