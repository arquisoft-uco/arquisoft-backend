package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosConEvaluacionesException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.EvaluacionesRegistradasJurados;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.JuradosSinEvaluacionesRule;

public class JuradosSinEvaluacionesRuleImpl implements JuradosSinEvaluacionesRule {

    @Override
    public void validar(EvaluacionesRegistradasJurados entrada) {
        if (!entrada.conEvaluaciones().isEmpty()) {
            throw new JuradosConEvaluacionesException(entrada.conEvaluaciones());
        }
    }
}
