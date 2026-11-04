package com.arquisoft.usuarios.domain.jurado.rules.impl;

import com.arquisoft.usuarios.domain.jurado.exception.JuradoUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.jurado.model.DisponibilidadJuradoUsuario;
import com.arquisoft.usuarios.domain.jurado.rules.JuradoUsuarioUnicoRule;

public class JuradoUsuarioUnicoRuleImpl implements JuradoUsuarioUnicoRule {

    @Override
    public void validar(DisponibilidadJuradoUsuario disponibilidad) {
        var jurado = disponibilidad.jurado();
        if (!jurado.esVacio() && !jurado.estaEliminado()) {
            throw new JuradoUsuarioDuplicadoException(disponibilidad.usuario(), disponibilidad.nombre());
        }
    }
}
