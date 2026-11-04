package com.arquisoft.entregables.domain.asesor.rules.impl;

import com.arquisoft.entregables.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.entregables.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.entregables.domain.asesor.rules.AsesorVigenteRule;

public class AsesorVigenteRuleImpl implements AsesorVigenteRule {

    @Override
    public void validar(VigenciaAsesor vigencia) {
        if (!vigencia.vigente()) {
            throw new AsesorNoVigenteException(vigencia.asesor());
        }
    }
}
