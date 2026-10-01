package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioIdentificadorDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadIdentificadorUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioIdentificadorUnicoRule;

public class UsuarioIdentificadorUnicoRuleImpl implements UsuarioIdentificadorUnicoRule {

    @Override
    public void validar(DisponibilidadIdentificadorUsuario disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new UsuarioIdentificadorDuplicadoException(disponibilidad.identificador());
        }
    }
}
