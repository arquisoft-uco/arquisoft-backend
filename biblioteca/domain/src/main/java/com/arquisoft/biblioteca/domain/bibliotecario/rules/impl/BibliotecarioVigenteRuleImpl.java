package com.arquisoft.biblioteca.domain.bibliotecario.rules.impl;

import com.arquisoft.biblioteca.domain.bibliotecario.exception.BibliotecarioNoVigenteException;
import com.arquisoft.biblioteca.domain.bibliotecario.model.VigenciaBibliotecario;
import com.arquisoft.biblioteca.domain.bibliotecario.rules.BibliotecarioVigenteRule;

public class BibliotecarioVigenteRuleImpl implements BibliotecarioVigenteRule {

    @Override
    public void validar(VigenciaBibliotecario vigencia) {
        if (!vigencia.existe() || vigencia.eliminado()) {
            throw new BibliotecarioNoVigenteException(vigencia.bibliotecario());
        }
    }
}
