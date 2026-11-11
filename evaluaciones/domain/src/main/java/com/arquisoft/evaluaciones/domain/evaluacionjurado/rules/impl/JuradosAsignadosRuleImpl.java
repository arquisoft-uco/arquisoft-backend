package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosNoAsignadosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosAsignadosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.JuradosAsignadosRule;

import java.util.HashSet;

public class JuradosAsignadosRuleImpl implements JuradosAsignadosRule {

    @Override
    public void validar(JuradosAsignadosEvaluacion entrada) {
        var noAsignados = new HashSet<>(entrada.solicitados());
        noAsignados.removeAll(entrada.asignados());
        if (!noAsignados.isEmpty()) {
            throw new JuradosNoAsignadosException(noAsignados);
        }
    }
}
