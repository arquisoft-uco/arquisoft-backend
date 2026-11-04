package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosYaAsignadosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.DisponibilidadJuradosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.JuradosSinAsignarRule;

import java.util.HashSet;

public class JuradosSinAsignarRuleImpl implements JuradosSinAsignarRule {

    @Override
    public void validar(DisponibilidadJuradosEvaluacion disponibilidad) {
        var duplicados = new HashSet<>(disponibilidad.solicitados());
        duplicados.retainAll(disponibilidad.yaAsignados());
        if (!duplicados.isEmpty()) {
            throw new JuradosYaAsignadosException(duplicados);
        }
    }
}
