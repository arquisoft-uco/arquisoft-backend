package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosEvaluadosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosEvaluadosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.JuradosPendientesPorEvaluarRule;

public class JuradosPendientesPorEvaluarRuleImpl implements JuradosPendientesPorEvaluarRule {

    @Override
    public void validar(JuradosEvaluadosEvaluacion entrada) {
        if (!entrada.evaluados().isEmpty()) {
            throw new JuradosEvaluadosException(entrada.evaluados());
        }
    }
}
