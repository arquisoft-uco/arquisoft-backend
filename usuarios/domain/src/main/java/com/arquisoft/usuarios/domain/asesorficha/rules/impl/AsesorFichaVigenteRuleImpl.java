package com.arquisoft.usuarios.domain.asesorficha.rules.impl;

import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.usuarios.domain.asesorficha.model.ExistenciaAsesorFicha;
import com.arquisoft.usuarios.domain.asesorficha.rules.AsesorFichaVigenteRule;

public class AsesorFichaVigenteRuleImpl implements AsesorFichaVigenteRule {

    @Override
    public void validar(ExistenciaAsesorFicha existencia) {
        var asesorFicha = existencia.asesorFicha();
        if (asesorFicha.esVacio() || asesorFicha.estaEliminado()) {
            throw new AsesorFichaNoEncontradoException(existencia.usuario());
        }
    }
}
