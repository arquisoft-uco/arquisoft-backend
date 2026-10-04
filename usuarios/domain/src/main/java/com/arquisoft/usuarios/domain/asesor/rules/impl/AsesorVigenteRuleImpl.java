package com.arquisoft.usuarios.domain.asesor.rules.impl;

import com.arquisoft.usuarios.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.usuarios.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.usuarios.domain.asesor.rules.AsesorVigenteRule;

public class AsesorVigenteRuleImpl implements AsesorVigenteRule {

    @Override
    public void validar(ExistenciaAsesor existencia) {
        var asesor = existencia.asesor();
        if (asesor.esVacio() || asesor.estaEliminado()) {
            throw new AsesorNoEncontradoException(existencia.usuario());
        }
    }
}
