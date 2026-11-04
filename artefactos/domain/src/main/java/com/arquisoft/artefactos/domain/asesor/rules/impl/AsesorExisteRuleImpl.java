package com.arquisoft.artefactos.domain.asesor.rules.impl;

import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorExisteRule;

public class AsesorExisteRuleImpl implements AsesorExisteRule {

    @Override
    public void validar(ExistenciaAsesor existencia) {
        if (existencia.encontrado().esVacio()) {
            throw new AsesorNoEncontradoException(existencia.asesor());
        }
    }
}
