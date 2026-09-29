package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.EstadoUsuarioSinCambioException;
import com.arquisoft.usuarios.domain.usuario.model.TransicionEstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.rules.EstadoUsuarioCambiaRule;

public class EstadoUsuarioCambiaRuleImpl implements EstadoUsuarioCambiaRule {

    @Override
    public void validar(TransicionEstadoUsuario transicion) {
        if (transicion.actual() == transicion.destino()) {
            throw new EstadoUsuarioSinCambioException(transicion.usuario(), transicion.destino().getNombre());
        }
    }
}
