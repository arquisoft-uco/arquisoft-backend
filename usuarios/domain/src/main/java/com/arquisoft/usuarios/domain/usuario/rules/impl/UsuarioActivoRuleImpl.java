package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioInactivoException;
import com.arquisoft.usuarios.domain.usuario.model.EstadoActividadUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioActivoRule;

public class UsuarioActivoRuleImpl implements UsuarioActivoRule {

    @Override
    public void validar(EstadoActividadUsuario estadoActividad) {
        if (!estadoActividad.usuarioDomain().estaActivo()) {
            throw new UsuarioInactivoException(estadoActividad.usuario());
        }
    }
}
