package com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.impl;

import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.UsuarioExisteRule;

public final class UsuarioExisteRuleImpl implements UsuarioExisteRule {

    @Override
    public void validar(ExistenciaUsuario entrada) {
        if (!entrada.existe()) {
            throw new UsuarioNoEncontradoException(entrada.usuario());
        }
    }
}
