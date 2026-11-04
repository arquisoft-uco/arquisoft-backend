package com.arquisoft.evaluaciones.domain.jurado.rules.impl;

import com.arquisoft.evaluaciones.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.evaluaciones.domain.jurado.model.ExistenciaJurado;
import com.arquisoft.evaluaciones.domain.jurado.rules.JuradoExistenteRule;

public class JuradoExistenteRuleImpl implements JuradoExistenteRule {

    @Override
    public void validar(ExistenciaJurado existencia) {
        if (!existencia.existe()) {
            throw new JuradoNoEncontradoException(existencia.jurado());
        }
    }
}
