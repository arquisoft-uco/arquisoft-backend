package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioExisteRule;

public class UsuarioExisteRuleImpl implements UsuarioExisteRule {

    @Override
    public void validar(ExistenciaUsuario existencia) {
        if (existencia.encontrado().esVacio()) {
            throw new UsuarioNoEncontradoException(existencia.usuario());
        }
    }
}
