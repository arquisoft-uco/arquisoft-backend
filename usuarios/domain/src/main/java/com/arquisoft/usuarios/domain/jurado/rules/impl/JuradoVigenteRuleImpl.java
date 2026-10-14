package com.arquisoft.usuarios.domain.jurado.rules.impl;

import com.arquisoft.usuarios.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.usuarios.domain.jurado.model.ExistenciaJurado;
import com.arquisoft.usuarios.domain.jurado.rules.JuradoVigenteRule;

public class JuradoVigenteRuleImpl implements JuradoVigenteRule {

    @Override
    public void validar(ExistenciaJurado existencia) {
        var jurado = existencia.jurado();
        if (jurado.esVacio() || jurado.estaEliminado()) {
            throw new JuradoNoEncontradoException(existencia.usuario());
        }
    }
}
