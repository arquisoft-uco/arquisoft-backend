package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEliminadoException;
import com.arquisoft.usuarios.domain.usuario.model.VigenciaUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioNoEliminadoRule;

public class UsuarioNoEliminadoRuleImpl implements UsuarioNoEliminadoRule {

    @Override
    public void validar(VigenciaUsuario vigencia) {
        if (vigencia.encontrado().estaEliminado()) {
            throw new UsuarioEliminadoException(vigencia.usuario());
        }
    }
}
