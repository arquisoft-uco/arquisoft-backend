package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;

public class UsuarioEmailUnicoRuleImpl implements UsuarioEmailUnicoRule {

    private final UsuarioOutputPort usuarioOutputPort;

    public UsuarioEmailUnicoRuleImpl(UsuarioOutputPort usuarioOutputPort) {
        this.usuarioOutputPort = usuarioOutputPort;
    }

    @Override
    public void validar(String email) {
        if (usuarioOutputPort.existePorEmail(email)) {
            throw new UsuarioEmailDuplicadoException(email);
        }
    }
}
