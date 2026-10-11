package com.arquisoft.usuarios.domain.bibliotecario.rules.impl;

import com.arquisoft.usuarios.domain.bibliotecario.exception.BibliotecarioUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.bibliotecario.model.DisponibilidadBibliotecarioUsuario;
import com.arquisoft.usuarios.domain.bibliotecario.rules.BibliotecarioUsuarioUnicoRule;

public class BibliotecarioUsuarioUnicoRuleImpl implements BibliotecarioUsuarioUnicoRule {

    @Override
    public void validar(DisponibilidadBibliotecarioUsuario disponibilidad) {
        var bibliotecario = disponibilidad.bibliotecario();
        if (!bibliotecario.esVacio() && !bibliotecario.estaEliminado()) {
            throw new BibliotecarioUsuarioDuplicadoException(disponibilidad.usuario());
        }
    }
}
