package com.arquisoft.usuarios.domain.bibliotecario.rules.impl;

import com.arquisoft.usuarios.domain.bibliotecario.exception.BibliotecarioNoEncontradoException;
import com.arquisoft.usuarios.domain.bibliotecario.model.ExistenciaBibliotecario;
import com.arquisoft.usuarios.domain.bibliotecario.rules.BibliotecarioVigenteRule;

public class BibliotecarioVigenteRuleImpl implements BibliotecarioVigenteRule {

    @Override
    public void validar(ExistenciaBibliotecario existencia) {
        var bibliotecario = existencia.bibliotecario();
        if (bibliotecario.esVacio() || bibliotecario.estaEliminado()) {
            throw new BibliotecarioNoEncontradoException(existencia.usuario());
        }
    }
}
