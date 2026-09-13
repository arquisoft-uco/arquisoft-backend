package com.arquisoft.usuarios.domain.asesorficha.rules.impl;

import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.asesorficha.model.DisponibilidadAsesorFichaUsuario;
import com.arquisoft.usuarios.domain.asesorficha.rules.AsesorFichaUsuarioUnicoRule;

public class AsesorFichaUsuarioUnicoRuleImpl implements AsesorFichaUsuarioUnicoRule {

    @Override
    public void validar(DisponibilidadAsesorFichaUsuario disponibilidad) {
        if (disponibilidad.yaEsAsesorFicha()) {
            throw new AsesorFichaUsuarioDuplicadoException(disponibilidad.usuario());
        }
    }
}
