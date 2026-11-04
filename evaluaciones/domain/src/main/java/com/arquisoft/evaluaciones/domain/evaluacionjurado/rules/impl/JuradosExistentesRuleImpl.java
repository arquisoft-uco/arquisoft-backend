package com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.ExistenciaJurados;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.JuradosExistentesRule;

import java.util.HashSet;

public class JuradosExistentesRuleImpl implements JuradosExistentesRule {

    @Override
    public void validar(ExistenciaJurados existencia) {
        var faltantes = new HashSet<>(existencia.solicitados());
        faltantes.removeAll(existencia.existentes());
        if (!faltantes.isEmpty()) {
            throw new JuradosNoEncontradosException(faltantes);
        }
    }
}
