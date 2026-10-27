package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEncontradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.ExistenciaRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorExisteRule;

public class RevisionAsesorExisteRuleImpl implements RevisionAsesorExisteRule {

    @Override
    public void validar(ExistenciaRevisionAsesor entrada) {
        if (!entrada.existe()) {
            throw new RevisionAsesorNoEncontradaException();
        }
    }
}
