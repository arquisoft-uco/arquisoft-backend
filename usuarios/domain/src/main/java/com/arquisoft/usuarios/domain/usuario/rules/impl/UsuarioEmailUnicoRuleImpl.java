package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadEmailUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;

public class UsuarioEmailUnicoRuleImpl implements UsuarioEmailUnicoRule {

    @Override
    public void validar(DisponibilidadEmailUsuario disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new UsuarioEmailDuplicadoException(disponibilidad.email());
        }
    }
}
