package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioContactoDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadContactoUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioContactoUnicoRule;

public class UsuarioContactoUnicoRuleImpl implements UsuarioContactoUnicoRule {

    @Override
    public void validar(DisponibilidadContactoUsuario disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new UsuarioContactoDuplicadoException(disponibilidad.contacto());
        }
    }
}
