package com.arquisoft.artefactos.domain.asesor.rules.impl;

import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.artefactos.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorVigenteRule;

public class AsesorVigenteRuleImpl implements AsesorVigenteRule {

    @Override
    public void validar(VigenciaAsesor vigencia) {
        var encontrado = vigencia.encontrado();
        if (encontrado.esVacio() || encontrado.estaEliminado()) {
            throw new AsesorNoVigenteException(vigencia.asesor());
        }
    }
}
