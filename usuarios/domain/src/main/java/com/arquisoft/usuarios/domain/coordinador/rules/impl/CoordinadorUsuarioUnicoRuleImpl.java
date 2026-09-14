package com.arquisoft.usuarios.domain.coordinador.rules.impl;

import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.coordinador.model.DisponibilidadCoordinadorUsuario;
import com.arquisoft.usuarios.domain.coordinador.rules.CoordinadorUsuarioUnicoRule;

public class CoordinadorUsuarioUnicoRuleImpl implements CoordinadorUsuarioUnicoRule {

    @Override
    public void validar(DisponibilidadCoordinadorUsuario disponibilidad) {
        if (disponibilidad.yaEsCoordinador()) {
            throw new CoordinadorUsuarioDuplicadoException(disponibilidad.usuario());
        }
    }
}
