package com.arquisoft.usuarios.domain.asesor.rules.impl;

import com.arquisoft.usuarios.domain.asesor.exception.AsesorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.asesor.model.DisponibilidadAsesorUsuario;
import com.arquisoft.usuarios.domain.asesor.rules.AsesorUsuarioUnicoRule;

public class AsesorUsuarioUnicoRuleImpl implements AsesorUsuarioUnicoRule {

    @Override
    public void validar(DisponibilidadAsesorUsuario disponibilidad) {
        var asesor = disponibilidad.asesor();
        if (!asesor.esVacio() && !asesor.estaEliminado()) {
            throw new AsesorUsuarioDuplicadoException(disponibilidad.usuario());
        }
    }
}
