package com.arquisoft.artefactos.domain.asesor.rules.impl;

import com.arquisoft.artefactos.domain.asesor.exception.AsesorDeRevisionNoEncontradoException;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesorDeRevision;
import com.arquisoft.artefactos.domain.asesor.rules.AsesorDeRevisionExisteRule;

public class AsesorDeRevisionExisteRuleImpl implements AsesorDeRevisionExisteRule {

    @Override
    public void validar(ExistenciaAsesorDeRevision entrada) {
        if (!entrada.existe()) {
            throw new AsesorDeRevisionNoEncontradoException(entrada.revisionAsesor());
        }
    }
}
